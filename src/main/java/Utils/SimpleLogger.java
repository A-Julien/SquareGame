package Utils;

public class SimpleLogger {
    private String tag = "";

    public SimpleLogger(String tag) {
        this.addTag(tag);
    }

    public void log(String message){
        System.out.println( this.tag + " " + message);
    }

    public void logNoNlWithTag(String message){
        System.out.print( this.tag + " " + message);
    }
    public void logNoNl(String message){
        System.out.print(message);
    }

    public void addTag(String tag){
        this.tag = this.tag +"[" + tag + "]";
    }
}
