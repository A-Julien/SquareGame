package Task;

import java.io.Serializable;

public enum TaskCommand implements Serializable {
    MOVE("MOVE"),
    PRINT("PRINT"),
    MOVE_GRANTED("MOVE_GRANTED"),
    MOVE_NOT_GRANTED("MOVE_NOT_GRANTED"),
    FORWARD_MESSAGE("FORWARD_MESSAGE");

    private final String cmd;

    TaskCommand(String cmd) {
        this.cmd = cmd;
    }
    private String cmd() { return cmd; }

}
