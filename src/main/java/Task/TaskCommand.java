package Task;

import java.io.Serializable;

public enum TaskCommand implements Serializable {
    INIT("INIT"),
    MOVE("MOVE"),
    PRINT("PRINT"),
    MOVE_GRANTED("MOVE_GRANTED"),
    MOVE_NOT_GRANTED("MOVE_NOT_GRANTED"),
    FORWARD_MESSAGE("FORWARD_MESSAGE"),
    FORWARD_APPROVED("FORWARD_APPROVED"),
    MOVE_GRANTED_FORWARDED("MOVE_GRANTED_FORWARDED"),
    CLIENT_NOT_FOUNDED("CLIENT_NOT_FOUNDED"),
    NEIHGBOR("NEIHGBOR"),
    GET_COLOR("COLOR"),
    PING("PING"),
    PONG("PONG");

    private final String cmd;

    TaskCommand(String cmd) {
        this.cmd = cmd;
    }
    private String cmd() { return cmd; }

}
