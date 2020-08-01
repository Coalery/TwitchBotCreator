package coalery.twitchbotcreator;

import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;

class Logger {

    private static TextView logLocation;

    static void registerLogLocation(@NonNull TextView logLocation) {
        Logger.logLocation = logLocation;
    }

    static void log(String content) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        String timeText = String.format(Locale.KOREA, "[%02d:%02d:%02d]", hour, minute, second);

        logLocation.append(timeText + " " + content + "\n");
    }

}
