package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {
    private final String ADDRESS = "irc.twitch.tv.";
    private final int PORT = 6667;

    private final String BOT_USERNAME = "twitch_bot_creator";
    private final String CHANNEL_NAME = "#doralife12";
    private final String OAUTH_TOKEN = "oauth:j4psapvud2om1n06xt51bb2d09dkm0";

    private Context rhino;
    private Scriptable scope;

    private View[] screens;

    private EditText code;

    private TwitchBot twitchBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screens = new View[3];
        screens[0] = findViewById(R.id.home_screen);
        screens[1] = findViewById(R.id.code_screen);
        screens[2] = findViewById(R.id.log_screen);

        code = findViewById(R.id.code_text);
        TextView log = findViewById(R.id.log_text);

        Logger.registerLogLocation(log);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            if(item.getItemId() == R.id.bottom_bar_home)
                setScreen(0);
            else if(item.getItemId() == R.id.bottom_bar_code)
                setScreen(1);
            else if(item.getItemId() == R.id.bottom_bar_log)
                setScreen(2);

            return true;
        });

        Button bot_onoff = findViewById(R.id.bot_onoff);

        bot_onoff.setOnClickListener((view) -> {
            try {
                twitchBot = new TwitchBot(CHANNEL_NAME, BOT_USERNAME);
                twitchBot.setVerbose(true);
                twitchBot.connect(ADDRESS, PORT, OAUTH_TOKEN);
                twitchBot.joinChannel(CHANNEL_NAME);
                twitchBot.sendMessage(CHANNEL_NAME, "Hello!");
            } catch(Exception e0) {
                Logger.log(e0.getMessage());
            }
        });

        setScreen(0);
    }

    public void setScreen(int index) {
        for(View screen : screens)
            screen.setVisibility(View.INVISIBLE);
        screens[index].setVisibility(View.VISIBLE);
    }

    private void runScript() {
        rhino = Context.enter();
        rhino.setOptimizationLevel(-1);

        try {
            scope = rhino.initStandardObjects();
            rhino.evaluateString(scope, code.getText().toString(), "JavaScript", 1, null);
            callScriptMethod("onStart", new Object[] {});
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            Logger.log(e.toString());
        } finally {
            Context.exit();
        }
    }

    private void callScriptMethod(String name, Object[] args) {
        try {
            Function func = (Function)scope.get(name, scope);
            func.call(rhino, scope, scope, args);
        } catch(ClassCastException ignored) {
        } catch(Exception e) {
            Logger.log("Failed to call function - " + name);
        }
    }
}
