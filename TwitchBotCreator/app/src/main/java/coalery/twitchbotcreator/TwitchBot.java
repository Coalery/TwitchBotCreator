package coalery.twitchbotcreator;

import org.jibble.pircbot.PircBot;

public class TwitchBot extends PircBot {
    private String channel;

    TwitchBot(String channel, String nick) {
        this.channel = channel;
        setName(nick);
        setLogin(nick);
    }

    @Override
    protected  void handleLine(String line) {

    }

}
