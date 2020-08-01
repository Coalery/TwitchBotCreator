package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final String ADDRESS = "irc.twitch.tv.";
    private final int PORT = 6667;

    private final String BOT_USERNAME = "twitch_bot_creator";
    private final String CHANNEL_NAME = "#doralife12";
    private final String OAUTH_TOKEN = "oauth:j4psapvud2om1n06xt51bb2d09dkm0";

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private Context rhino;
    private Scriptable scope;

    private View[] screens;

    private EditText code;
    private TextView log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screens = new View[3];
        screens[0] = findViewById(R.id.home_screen);
        screens[1] = findViewById(R.id.code_screen);
        screens[2] = findViewById(R.id.log_screen);

        code = findViewById(R.id.code_text);
        log = findViewById(R.id.log_text);

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
            Thread worker = new Thread() {
                public void run() {
                    try {
                        socket = new Socket();
                    }
                }
            };
        });

        setScreen(0);
    }

    public void setScreen(int index) {
        for(View screen : screens)
            screen.setVisibility(View.INVISIBLE);
        screens[index].setVisibility(View.VISIBLE);
    }

    public void addLog(String content) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String timeText = String.format(Locale.KOREA, "[%02d:%02d:%02d]", hour, minute, second);

        log.append(timeText + " " + content + "\n");
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
            addLog(e.toString());
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
            addLog("Failed to call function - " + name);
        }
    }
}
