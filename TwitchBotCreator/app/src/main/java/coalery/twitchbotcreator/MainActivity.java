package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Context rhino;
    private Scriptable scope;

    private View[] screens;

    private EditText code;

    private BotThread botThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        screens = new View[2];
        screens[0] = findViewById(R.id.home_screen);
        screens[1] = findViewById(R.id.code_screen);

        code = findViewById(R.id.code_text);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setScreen(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button bot_onoff = findViewById(R.id.bot_onoff);

        bot_onoff.setOnClickListener((view) -> {
            botThread = new BotThread();
            botThread.start();
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
            log(e.toString());
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
            log("Failed to call function - " + name);
        }
    }

    void log(String content) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String timeText = String.format(Locale.KOREA, "[%02d:%02d:%02d]", hour, minute, second);

        Toast.makeText(getApplicationContext(), timeText + " " + content + "\n", Toast.LENGTH_SHORT).show();
    }
}
