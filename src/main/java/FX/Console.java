package FX;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.text.DateFormat;
import java.util.Date;

public class Console extends Scene {
    private VBox vBox;
    private DateFormat shortDateFormat;

    public Console() {
        super(new ScrollPane(), 400, 400);
        ScrollPane scrollPane = (ScrollPane) this.getRoot();
        shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

        this.vBox = new VBox();
        scrollPane.setContent(vBox);
        this.addLogs("Test logs");
    }

    private void addLogs(String s) {
        String log = shortDateFormat.format(new Date());

        log += " : " + s;
        Text t = new Text(log);
        vBox.getChildren().add(t);
    }
}

