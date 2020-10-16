package coalery.twitchbotcreator;

public class BotInitializeThread extends Thread {

    private String channel;
    private String oauth;
    private String code;
    private IBotInitializeCallback callback;

    public BotInitializeThread(String channel, String oauth, String code, IBotInitializeCallback callback) {
        if(!channel.startsWith("#"))
            channel = "#" + channel;

        this.channel = channel;
        this.oauth = oauth;
        this.code = code;
        this.callback = callback;
    }

    @Override
    public void run() {
        final String ADDRESS = "irc.twitch.tv.";
        final int PORT = 6667;

        final String BOT_USERNAME = "twitch_bot_creator";


        try {
            TwitchBot bot = new TwitchBot(channel, BOT_USERNAME, code);
            bot.setVerbose(false);
            bot.connect(ADDRESS, PORT, oauth);
            bot.joinChannel(channel);

            this.callback.onCompleted(bot);
        } catch(Exception e0) {
            e0.printStackTrace();
        }
    }

}
