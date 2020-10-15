package coalery.twitchbotcreator;

public class BotInitializeThread extends Thread {

    private String code;
    private IBotInitializeCallback callback;

    public BotInitializeThread(String code, IBotInitializeCallback callback) {
        this.code = code;
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
            TwitchBot bot = new TwitchBot(CHANNEL_NAME, BOT_USERNAME, code);
            bot.setVerbose(false);
            bot.connect(ADDRESS, PORT, OAUTH_TOKEN);
            bot.joinChannel(CHANNEL_NAME);

            this.callback.onCompleted(bot);
        } catch(Exception e0) {
            e0.printStackTrace();
        }
    }

}
