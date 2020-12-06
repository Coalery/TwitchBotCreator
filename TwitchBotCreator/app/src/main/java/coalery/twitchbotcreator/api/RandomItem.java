package coalery.twitchbotcreator.api;

import androidx.annotation.NonNull;

import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSGetter;

import java.util.Locale;

public class RandomItem extends ScriptableObject {
    private final String message;
    private final int value;

    public RandomItem(String message, int value) {
        this.message = message;
        this.value = value;
    }

    public String getMessage() { return message; }
    public int getValue() { return value; }

    @JSGetter
    public String message() { return message; }

    @JSGetter
    public int value() { return value; }

    @Override
    public String getClassName() { return "RandomItem"; }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.KOREA, "RandomItem{\"message\":%s,\"value\":%d}", message, value);
    }
}