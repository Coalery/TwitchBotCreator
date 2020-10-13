package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private TwitchBot twitchBot; // 봇 객체
    private boolean isBotOn; // 봇이 켜져있는가.

    private Button botSwitch; // 봇 스위치 버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        isBotOn = false;

        // #region 봇 스위치 버튼 설정
        botSwitch = findViewById(R.id.bot_switch);
        botSwitch.setText("켜기");

        botSwitch.setOnClickListener((view) -> {
            if(isBotOn) { // 봇이 켜져있으면, 봇을 끈다.
                botSwitch.setEnabled(false); // 처리 중에는 버튼을 비활성화한다.
                if(twitchBot != null) { // 봇을 끈다.
                    twitchBot.disconnect();
                    twitchBot.dispose();
                }
                isBotOn = false;
                botSwitch.setText("켜기");
                botSwitch.setEnabled(true); // 처리를 완료하였으므로 버튼을 다시 활성화시킨다.
            } else { // 봇이 꺼져있으면, 봇을 킨다.
                botSwitch.setEnabled(false); // 처리 중에는 버튼을 비활성화한다.
                BotInitializeThread botInitThread = new BotInitializeThread(getApplicationContext(), new BotInitializeCallback());
                botInitThread.start(); // 초기화 쓰레드를 시작한다.
            }
        });
        // #endregion

        // #region 저장 버튼 설정
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setText("저장");
        // #endregion
    }

    private class BotInitializeCallback implements IBotInitializeCallback { // 봇 초기화가 완료되면 호출되는 콜백
        @Override
        public void onCompleted(TwitchBot bot) {
            MainActivity.this.twitchBot = bot;
            runOnUiThread(() -> { // 처리를 완료하였으므로 버튼을 다시 활성화시킨다.
                isBotOn = true;
                botSwitch.setText("끄기");
                botSwitch.setEnabled(true);
            });
        }
    }

}
