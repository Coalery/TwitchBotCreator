package coalery.twitchbotcreator.api;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSStaticFunction;

import java.util.ArrayList;
import java.util.Random;

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
        itemList.add(new RandomItem(message, val));
    }
}
