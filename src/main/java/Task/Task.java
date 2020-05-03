package Task;

import java.io.Serializable;

public class Task implements Serializable {
    public TaskCommand cmdType;
    public Object cmd;
    public String replyQueu;

    public Task(TaskCommand cmdType, Object cmd, String replyQueu) {
        this.cmdType = cmdType;
        this.cmd = cmd;
        this.replyQueu = replyQueu;
    }

    @Override
    public String toString() {
        return cmdType.toString() + " : " + cmd + " from " + replyQueu;
    }

}
