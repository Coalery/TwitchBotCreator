package coalery.twitchbotcreator;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.jibble.pircbot.PircBot;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TwitchBot extends PircBot {
    private static final String BOT_FILE_PATH = "/twitchbot";
    private static final String BOT_FILE_NAME = "twitchbot.js";

    private Context context;
    private org.mozilla.javascript.Context rhino;
    private Scriptable scope;
    private String channel;

    TwitchBot(Context context, String channel, String username) {
        this.context = context;
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

        StringBuilder sb = new StringBuilder();
        for(int i=4; i<split.length; i++) {
            if(sb.length() > 0) sb.append(" ");
            sb.append(split[i]);
        }

        Object[] badge_array = badges.toArray();
        String sender_id = split[1].split("!")[0].substring(1);
        String sender_nickname = map.get("display-name");
        String message = sb.toString();

        if(message.charAt(0) == ':')
            message = message.substring(1);

        Object obj = callScriptMethod("onMessageReceived", new Object[] {channel, badge_array, sender_id, sender_nickname, message});
        if(obj instanceof Undefined) return;
        sendMessage(channel, (String) obj);
    }

    private void initializeScript() {
        rhino = org.mozilla.javascript.Context.enter();
        rhino.setOptimizationLevel(-1);

        String code = loadFile();


        try {
            scope = rhino.initStandardObjects();
            rhino.setLanguageVersion(org.mozilla.javascript.Context.VERSION_1_8);
            rhino.evaluateString(scope, code, "JavaScript", 1, null);
            callScriptMethod("onStart", new Object[] {});
        } catch(Exception e) {
            log(e.toString());
        } finally {
            org.mozilla.javascript.Context.exit();
        }
    }

    private void writeDefaultFileIfNotExist(String scriptFilePath) throws IOException {
        File scriptFileDir = new File(scriptFilePath);
        if(!scriptFileDir.exists()) { scriptFileDir.mkdir(); } // 디렉토리가 없다면 만든다.

        File scriptFile = new File(scriptFilePath, BOT_FILE_NAME);
        if(!scriptFile.exists()) { // 파일이 없으면 만든다.
            String testCode =
            "function onStart() {}" +
            "" +
            "function onMessageReceived(channel, badges, sender_id, sender_nickname, message) {" +
            "    return message;" +
            "}";

            String[] codeLines = testCode.split("\n");

            BufferedWriter bw = new BufferedWriter(new FileWriter(scriptFile, false));
            for(String codeLine : codeLines) {
                bw.write(codeLine);
                bw.newLine();
            }
            bw.close();
        }
    }

    private String loadFile() { // 파일을 읽어서 파일 내용을 반환한다.
        String result = "";
        try {
            String scriptFilePath = null;
            if(Build.VERSION.SDK_INT < 29) scriptFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + BOT_FILE_PATH;
            else scriptFilePath = context.getExternalFilesDir(BOT_FILE_PATH).getAbsolutePath();

            writeDefaultFileIfNotExist(scriptFilePath);

            BufferedReader br = new BufferedReader(new FileReader(new File(context.getFilesDir().getAbsolutePath() + BOT_FILE_PATH, BOT_FILE_NAME)));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
            result = sb.toString();
        } catch(IOException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return result;
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
