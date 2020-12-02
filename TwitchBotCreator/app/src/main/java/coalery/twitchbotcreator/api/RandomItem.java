package coalery.twitchbotcreator.api;

public class RandomItem {
    private final String message;
    private final int value;

    public RandomItem(String message, int value) {
        this.message = message;
        this.value = value;
    }

    public String getMessage() { return message; }
    public int getValue() { return value; }
}