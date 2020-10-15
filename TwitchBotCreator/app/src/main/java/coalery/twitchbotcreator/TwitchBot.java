package coalery.twitchbotcreator;

import android.util.Log;

import org.jibble.pircbot.PircBot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.HashMap;

public class TwitchBot extends PircBot {
    private org.mozilla.javascript.Context rhino;
    private Scriptable scope;
    private String channel;
    private String code;

    TwitchBot(String channel, String username, String code) {
        this.channel = channel;
        this.code = code;
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
        String rawBadges = map.get("badges");
        if(rawBadges != null) {
            String[] badge_split = rawBadges.split(",");
            for(String badge : badge_split)
                badges.add(badge.split("/")[0]);
        }

        StringBuilder sb = new StringBuilder();
        for(int i=4; i<split.length; i++) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(split[i]);
        }

        Scriptable badge_array = rhino.newArray(scope, badges.toArray());
        String sender_id = split[1].split("!")[0].substring(1);
        String sender_nickname = map.get("display-name");
        String message = sb.toString();

        if(message.charAt(0) == ':')
            message = message.substring(1);

        Object obj = callScriptMethod("onMessageReceived", new Object[] {channel, badge_array, sender_id, sender_nickname, message});
        if(obj == null) return;
        if(obj instanceof Undefined) return;
        sendMessage(channel, obj.toString());
    }

    private void initializeScript() {
        rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);

        try {
            scope = rhino.initStandardObjects();
            rhino.setLanguageVersion(Context.VERSION_1_8);
            rhino.evaluateString(scope, code, "JavaScript", 1, null);
            callScriptMethod("onStart", new Object[] {});
        } catch(Exception e) {
            log(e.toString());
        } finally {
            org.mozilla.javascript.Context.exit();
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
