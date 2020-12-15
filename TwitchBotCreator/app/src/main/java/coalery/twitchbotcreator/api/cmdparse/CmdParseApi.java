package coalery.twitchbotcreator.api.cmdparse;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.ArrayList;
import java.util.Arrays;

import coalery.twitchbotcreator.TwitchBot;

public class CmdParseApi extends ScriptableObject {

    private String command;
    private ArrayList<String> arguments;

    public CmdParseApi() {}

    @JSConstructor
    public void jsConstructor(String totalCmd) {
        String[] split = totalCmd.split(" ");

        command = split[0];

        arguments = new ArrayList<>();
        arguments.addAll(Arrays.asList(split).subList(1, split.length));
    }

    @JSFunction
    public String getArgumentAt(int index) { return arguments.get(index); }

    @JSGetter
    public String command() { return command; }

    @JSGetter
    public Scriptable arguments() { return TwitchBot.rhino.newArray(TwitchBot.scope, arguments.toArray()); }

    @JSGetter
    public int argCount() { return arguments.size(); }

    @Override
    public String getClassName() { return "CmdParseApi"; }

}
