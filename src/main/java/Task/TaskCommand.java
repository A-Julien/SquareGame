package Task;

import java.io.Serializable;

public enum TaskCommand implements Serializable {
    MOVE("MOVE"),
    PRINT("PRINT");

    private final String cmd;

    TaskCommand(String cmd) {
        this.cmd = cmd;
    }
    private String cmd() { return cmd; }

}
