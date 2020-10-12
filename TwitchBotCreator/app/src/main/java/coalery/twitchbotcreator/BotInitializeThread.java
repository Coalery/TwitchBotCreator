package coalery.twitchbotcreator;

import android.content.Context;
import android.util.Log;

public class BotInitializeThread extends Thread {

    private Context context;
    private IBotInitializeCallback callback;

    public BotInitializeThread(Context context, IBotInitializeCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    @Override
    public void run() {
        final String ADDRESS = "irc.twitch.tv.";
        final int PORT = 6667;

        final String BOT_USERNAME = "twitch_bot_creator";
        final String OAUTH_TOKEN = "oauth:hapg6bmd76460g3el2pejhaw56dido";
        final String CHANNEL_NAME = "#doralife12";

        try {
            TwitchBot bot = new TwitchBot(context, CHANNEL_NAME, BOT_USERNAME);
            bot.setVerbose(false);
            bot.connect(ADDRESS, PORT, OAUTH_TOKEN);
            bot.joinChannel(CHANNEL_NAME);

            this.callback.onCompleted(bot);
        } catch(Exception e0) {
            Log.e("Error!", e0.toString());
        }
    }

}
