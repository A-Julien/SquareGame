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

    /*public String device;
    public int type; // 1 = MOVE; 2 = you Have A neihbgor ; 3 = Give Instruction to a client
    public String replyQueue;
    public Cell pos;
    public String cmd;
    public String [] mots;

    public Task(String command, String replyQueue){
        this.cmd = command;
        this.replyQueue = replyQueue;
        mots = command.trim().split("\\s+");

        String responsse;

        switch (mots[0]){
            case "MOVE":

                type =1;
                pos = new Cell(Integer.parseInt(mots[1]), Integer.parseInt(mots[2]));
                break;
            case "NEW_NEIGHBOR":
                type = 2;
                break;
            case "GIVE_INSTRUCTION" :
                type = 3;
                break;


            case "PRINT":
                type = 5;
                break;

            case "PING":
                type = 6;
                break;
            case "PONG":
                type = 7;
                break;
            default:
                type = -1;
        }
    }






    @Override
    public String toString() {
        return "From : " + replyQueue +" CMD = " + cmd ;
    }

    public String print() {
        String msg = "Nouveau message de " + replyQueue + " :";
        for(int i = 1; i < mots.length ; i++){
            msg +=  " " +mots[i];
        }
        return msg;
    }*/






}
