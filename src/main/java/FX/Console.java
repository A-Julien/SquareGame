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
    public Console() {
        super(new ScrollPane(), 400, 400);
        this.scrollPane = (ScrollPane) this.getRoot();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        setFill(Color.BLACK);
        scrollPane.setStyle("-fx-background: rgb(0,0,0);\n -fx-background-color:transparent");



         this.vBox = new VBox();
         scrollPane.setContent(vBox);
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
         this.addLogs("Test logs");
    }

    private void addLogs(String s) {
        String log = shortDateFormat.format(new Date());
        log += " : " + s;
        Text t = new Text(log);
        t.setFill(Color.BEIGE);
        vBox.getChildren().add(t);
        scrollPane.vvalueProperty().bind(vBox.heightProperty());
    }

    public void newLog(String s){
        Platform.runLater(() -> addLogs(s));
    }
}

