package Task;

import java.io.Serializable;

public enum Direction implements Serializable {
    UP("UP"),
    DOWN("DOWN"),
    LEFT("LEFT"),
    RIGHT("RIGHT");

    private final String cmd;

    Direction(String cmd) {
        this.cmd = cmd;
    }


}