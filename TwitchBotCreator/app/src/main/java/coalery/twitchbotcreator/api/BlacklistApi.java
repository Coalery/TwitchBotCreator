package coalery.twitchbotcreator.api;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.HashSet;

import coalery.twitchbotcreator.TwitchBot;

public class BlacklistApi extends ScriptableObject {

    public HashSet<String> blackList;

    public BlacklistApi() { }

    @JSConstructor
    public void jsConstructorMethod() {
        blackList = new HashSet<>();
    }

    @Override
    public String getClassName() {
        return "BlacklistApi";
    }

    @JSFunction
    public void add(String userId) { blackList.add(userId); }

    @JSFunction
    public void remove(String userId) {
        blackList.remove(userId);
    }

    @JSFunction
    public boolean contains(String userId) { return blackList.contains(userId); }

    @JSGetter
    public int length() {
        return blackList.size();
    }

    @JSFunction
    public String getAllbyString() {
        StringBuilder sb = new StringBuilder();
        for(String userId : blackList) {
            sb.append(userId);
            sb.append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1);
    }

    @JSFunction
    public Scriptable getAllbyList() {
        return TwitchBot.rhino.newArray(TwitchBot.scope, blackList.toArray());
    }

}
