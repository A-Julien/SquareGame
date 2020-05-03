package Utils.Logger;

import FX.Console;

public class SimpleLogger {
    private String tag = "";
    private Console console;

    public SimpleLogger(String tag, Console console) {
        this.console = console;
        this.addTag(tag);
    }

    public void log(String message){
        if (this.console == null) {
            System.out.println( this.tag + " " + message);
            return;
        }
        this.console.newLog(this.tag + " " + message);
    }

    public void addTag(String tag){
        this.tag = this.tag +"[" + tag + "]";
    }

    public void replaceTag(String tag){
        this.tag = tag;
    }
    public void setConsole(FX.Console console){
        this.console = console;
    }
}
