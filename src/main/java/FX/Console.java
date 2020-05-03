package FX;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.text.DateFormat;
import java.util.Date;

public class Console extends Scene  {
    private VBox vBox;
    private DateFormat shortDateFormat;
    ScrollPane scrollPane;
    private final String whiteConsole = "-fx-background: rgb(255,255,255);\n -fx-background-color:transparent";
    private final String blackConsole = "-fx-background: rgb(0,0,0);\n -fx-background-color:transparent";
    Color currentTextColor;
    public Console() {
        super(new ScrollPane(), 400, 400);
        this.scrollPane = (ScrollPane) this.getRoot();
        scrollPane.prefWidth(650);
        //scrollPane.prefHeight(400);
        scrollPane.minHeight(200);
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
        this.setBlack();
         this.vBox = new VBox();
         scrollPane.setContent(vBox);
        newLog("Terminal");
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    private void addLogs(String s) {
        String log = shortDateFormat.format(new Date());
        log += " : " + s;
        Text t = new Text(log);
        t.setFill(currentTextColor);
        vBox.getChildren().add(t);
        scrollPane.vvalueProperty().bind(vBox.heightProperty());
    }

    public void newLog(String s){
        Platform.runLater(() -> addLogs(s));
    }

    public void setWhite(){
        scrollPane.setStyle(whiteConsole);
        currentTextColor = Color.BLACK;
    }

    public void setBlack(){
        scrollPane.setStyle(blackConsole);
        currentTextColor = Color.BEIGE;
    }
}

