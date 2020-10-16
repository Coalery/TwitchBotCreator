package coalery.twitchbotcreator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        EditText channelText = findViewById(R.id.channelText);
        EditText oauthText = findViewById(R.id.oauthText);

        Button getTokenButton = findViewById(R.id.getTokenButton);
        getTokenButton.setOnClickListener((view) -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitchapps.com/tmi/"));
            startActivity(browserIntent);
        });

        Button settingSaveButton = findViewById(R.id.settingSaveButon);
        settingSaveButton.setOnClickListener((view) -> {
            String channel = channelText.getText().toString();
            String oauth = oauthText.getText().toString();

            if(channel.isEmpty() || oauth.isEmpty()) {
                Toast.makeText(getApplicationContext(), "입력하지 않은 데이터가 있습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            PreferenceManager.setString(getApplicationContext(), "twbotcr_channel", channel);
            PreferenceManager.setString(getApplicationContext(), "twbotcr_oauth", oauth);

            Intent intent = new Intent();
            intent.putExtra("setting", new SettingData(channel, oauth));
            setResult(RESULT_OK, intent);
            finish();
        });

        Intent intent = getIntent();
        if(intent != null) {
            Bundle bundle = intent.getExtras();
            SettingData settingData = bundle.getParcelable("setting");

            channelText.setText(settingData.channel);
            oauthText.setText(settingData.oauth);
        }
    }
}