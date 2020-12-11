package coalery.twitchbotcreator.api;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.ArrayList;
import java.util.Random;

import coalery.twitchbotcreator.TwitchBot;

public class RandomApi extends ScriptableObject {
    private ArrayList<RandomItem> itemList;

    public RandomApi() {}

    @Override
    public String getClassName() {
        return "RandomApi";
    }

    @JSStaticFunction
    public static int randomRange(int start, int end) {
        Random random = new Random();
        return start + random.nextInt(end - start + 1);
    }

    @JSStaticFunction
    public static double randomDouble() {
        Random random = new Random();
        return random.nextDouble();
    }

    @JSConstructor
    public void jsConstructorMethod() {
        itemList = new ArrayList<>();
    }

    @JSFunction
    public void addItem(String message, int val) {
        if(val <= 0) {
            return;
        }
        itemList.add(new RandomItem(message, val));
    }

    @JSFunction
    public void removeItem(String message, int val) { itemList.remove(new RandomItem(message, val)); }

    @JSFunction
    public void removeItemByIndex(int index) { itemList.remove(index); }

    @JSFunction
    public RandomItem get(int index) {
        return itemList.get(index);
    }

    @JSFunction
    public String random() {
        int sum = 0;
        for(int i=0; i<itemList.size(); i++) {
            sum += itemList.get(i).getValue();
        }

        Random random = new Random();
        int randomValue = random.nextInt(sum) + 1;

        String result = "";
        for(int i=0; i<itemList.size(); i++) {
            randomValue -= itemList.get(i).getValue();
            if(randomValue <= 0) {
                result = itemList.get(i).getMessage();
                break;
            }
        }
        return result;
    }

    @JSGetter
    public int length() {
        return itemList.size();
    }

    @JSFunction
    public String getAllbyString() {
        StringBuilder sb = new StringBuilder();
        for(RandomItem item : itemList) {
            sb.append(item.toString());
            sb.append(",");
        }
        String result = sb.toString();
        return result.substring(0, result.length() - 1); // 반점 빼기
    }

    @JSFunction
    public Scriptable getAllbyList() {
        return TwitchBot.rhino.newArray(TwitchBot.scope, itemList.toArray());
    }
}
