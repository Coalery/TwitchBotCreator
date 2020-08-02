package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private BotThread botThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button bot_onoff = findViewById(R.id.bot_onoff);

        bot_onoff.setOnClickListener((view) -> {
            botThread = new BotThread();
            botThread.start();
        });
    }
}
