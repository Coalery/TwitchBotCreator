package coalery.twitchbotcreator;

import android.util.Log;

import org.jibble.pircbot.PircBot;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.util.ArrayList;
import java.util.HashMap;

import coalery.twitchbotcreator.api.BlacklistApi;
import coalery.twitchbotcreator.api.RandomApi;
import coalery.twitchbotcreator.api.RandomItem;

public class TwitchBot extends PircBot {
    public static org.mozilla.javascript.Context rhino;
    public static Scriptable scope;

    private String channel;
    private String code;

    private BlacklistApi blacklistApi;

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

        Log.i("TESTTESTTEST0", "1");

        HashMap<String, String> map = new HashMap<>();
        for(String kv : split[0].split(";")) {
            String[] kv_split = kv.split("=");
            if(kv_split.length == 1) {
                map.put(kv_split[0], null);
            } else {
                map.put(kv_split[0], kv_split[1]);
            }
        }

        Log.i("TESTTESTTEST0", "2");

        ArrayList<String> badges = new ArrayList<>();
        String rawBadges = map.get("badges");
        if(rawBadges != null) {
            String[] badge_split = rawBadges.split(",");
            for(String badge : badge_split)
                badges.add(badge.split("/")[0]);
        }

        Log.i("TESTTESTTEST0", "3");

        StringBuilder sb = new StringBuilder();
        for(int i=4; i<split.length; i++) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(split[i]);
        }

        Log.i("TESTTESTTEST0", "4");

        Scriptable badge_array = rhino.newArray(scope, badges.toArray());
        String sender_id = split[1].split("!")[0].substring(1);
        String sender_nickname = map.get("display-name");
        String message = sb.toString();

        Log.i("TESTTESTTEST0", "5");

        if(message.charAt(0) == ':')
            message = message.substring(1);

        Log.i("TESTTESTTEST2", "1");
//        Log.i("TESTTESTTEST1", blacklistApi.toString());
        Log.i("TESTTESTTEST2", "2");

        if(blacklistApi != null && blacklistApi.contains(sender_id)) return;

        Log.i("TESTTESTTEST1", "2");

        Object obj = callScriptMethod("onMessageReceived", new Object[] {channel, badge_array, sender_id, sender_nickname, message});

        Log.i("TESTTESTTEST1", "3");

        if(obj == null) return;

        Log.i("TESTTESTTEST1", "4");

        if(obj instanceof Undefined) return;

        Log.i("TESTTESTTEST1", "5");

        sendMessage(channel, obj.toString());
    }

    private void initializeScript() {
        rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);

        scope = rhino.initStandardObjects();
        rhino.setLanguageVersion(Context.VERSION_1_8);

        Object wrappedOut = Context.javaToJS(System.out, scope);
        ScriptableObject.putProperty(scope, "out", wrappedOut);

        try {
            ScriptableObject.defineClass(scope, BlacklistApi.class);
            ScriptableObject.defineClass(scope, RandomApi.class);

            Scriptable blacklist = rhino.newObject(scope, "BlacklistApi");
            scope.put("blacklist", scope, blacklist);
            blacklistApi = (BlacklistApi) blacklist;

//            Scriptable random = rhino.newObject(scope, "RandomApi");
        } catch(Exception e) {e.printStackTrace();}

        rhino.evaluateString(scope, code, "JavaScript", 1, null);
        callScriptMethod("onStart", new Object[] {});

        org.mozilla.javascript.Context.exit();
    }

    private Object callScriptMethod(String name, Object[] args) {
        try {
            Log.i("TESTTESTTEST", "aejfopawe");
            Function func = (Function)scope.get(name, scope);
            Log.i("TESTTESTTEST", func.toString());
            return func.call(rhino, scope, scope, args);
        } catch(ClassCastException cast) {
            cast.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
            Log.d("TwitchBotCreator", "Failed to call function - " + name);
        }
        return null;
    }

}
