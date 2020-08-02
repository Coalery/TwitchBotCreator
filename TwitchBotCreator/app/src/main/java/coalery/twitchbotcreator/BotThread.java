package coalery.twitchbotcreator;

import android.util.Log;

public class BotThread extends Thread {

    @Override
    public void run() {
        final String ADDRESS = "irc.twitch.tv.";
        final int PORT = 6667;

        final String BOT_USERNAME = "twitch_bot_creator";
        final String OAUTH_TOKEN = "oauth:j4psapvud2om1n06xt51bb2d09dkm0";
        final String CHANNEL_NAME = "#doralife12";

        try {
            TwitchBot bot = new TwitchBot(CHANNEL_NAME, BOT_USERNAME);
            bot.setVerbose(false);
            bot.connect(ADDRESS, PORT, OAUTH_TOKEN);
            bot.joinChannel(CHANNEL_NAME);
        } catch(Exception e0) {
            Log.e("Error!", e0.toString());
        }
    }

}
