import java.io.Serializable;

public class Message implements Serializable {
    private final String message;
    private final int type;
    public Message(String message, int type) {
        this.message = message;
        this.type = type;
    }
    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }
}