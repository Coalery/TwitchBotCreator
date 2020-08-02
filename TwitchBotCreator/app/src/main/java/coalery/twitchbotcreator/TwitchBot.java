package coalery.twitchbotcreator;

import android.util.Log;

import org.jibble.pircbot.PircBot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.HashMap;

public class TwitchBot extends PircBot {
    private Context rhino;
    private Scriptable scope;
    private String channel;

    TwitchBot(String channel, String username) {
        this.channel = channel;
        setName(username);
        setLogin(username);
    }

    @Override
    protected void onConnect() {
        super.onConnect();

        sendRawLine("CAP REQ :twitch.tv/membership");
        sendRawLine("CAP REQ :twitch.tv/commands");
        sendRawLine("CAP REQ :twitch.tv/tags");

        initializeScript();
    }

    @Override
    protected void handleLine(String line) {
        Log.d("Received", line);
        String[] split = line.split(" ");

        if(!split[2].equals("PRIVMSG")) return;
        HashMap<String, String> map = new HashMap<>();
        for(String kv : split[0].split(";")) {
            String[] kv_split = kv.split("=");
            if(kv_split.length == 1) {
                map.put(kv_split[0], null);
            } else {
                map.put(kv_split[0], kv_split[1]);
            }
        }

        ArrayList<String> badges = new ArrayList<>();
        if(map.get("badges") != null) {
            String[] badge_split = map.get("badges").split(",");
            for(String badge : badge_split)
                badges.add(badge.split("/")[0]);
        }

        Object[] badge_array = badges.toArray();
        String sender_id = split[1].split("!")[0].substring(1);
        String sender_nickname = map.get("display-name");
        String message = split[4].substring(1);

        Object obj = callScriptMethod("onMessageReceived", new Object[] {channel, badge_array, sender_id, sender_nickname, message});
        if(obj instanceof Undefined) return;
        sendMessage(channel, (String) obj);
    }

    private void initializeScript() {
        String testCode =
                "function onStart() {" +
                "" +
                "}" +
                "" +
                "function onMessageReceived(channel, badges, sender_id, sender_nickname, message) {" +
                "    return message;" +
                "}";

        rhino = Context.enter();
        rhino.setOptimizationLevel(-1);

        try {
            scope = rhino.initStandardObjects();
            rhino.setLanguageVersion(Context.VERSION_1_8);
            rhino.evaluateString(scope, testCode, "JavaScript", 1, null);
            callScriptMethod("onStart", new Object[] {});
        } catch(Exception e) {
            log(e.toString());
        } finally {
            Context.exit();
        }
    }

    private Object callScriptMethod(String name, Object[] args) {
        try {
            Function func = (Function)scope.get(name, scope);
            return func.call(rhino, scope, scope, args);
        } catch(ClassCastException ignored) {
        } catch(Exception e) {
            Log.d("TwitchBotCreator", "Failed to call function - " + name);
        }
        return null;
    }

}
