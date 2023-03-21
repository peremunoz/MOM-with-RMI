package RMIObjects;

import java.io.Serializable;

public record Message(String message, int type) implements Serializable {
}