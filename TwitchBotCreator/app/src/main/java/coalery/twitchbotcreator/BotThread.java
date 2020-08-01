package coalery.twitchbotcreator;

import android.util.Log;

public class BotThread extends Thread {
    private TwitchBot twitchBot;

    @Override
    public void run() {
        final String ADDRESS = "irc.twitch.tv.";
        final int PORT = 6667;

        final String BOT_USERNAME = "twitch_bot_creator";
        final String CHANNEL_NAME = "#doralife12";
        final String OAUTH_TOKEN = "oauth:j4psapvud2om1n06xt51bb2d09dkm0";

        try {
            twitchBot = new TwitchBot(CHANNEL_NAME, BOT_USERNAME);
            twitchBot.setVerbose(true);
            twitchBot.connect(ADDRESS, PORT, OAUTH_TOKEN);
            twitchBot.joinChannel(CHANNEL_NAME);
            twitchBot.sendMessage(CHANNEL_NAME, "Hello!");
        } catch(Exception e0) {
            Log.e("Error!", e0.toString());
        }
    }

}
