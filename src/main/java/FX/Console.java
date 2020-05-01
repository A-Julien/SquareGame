package FX;

import Manager.Manager;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.text.DateFormat;
import java.util.Date;

public class Console extends Scene {
        VBox vBox;
        DateFormat shortDateFormat;
        ScrollPane scrollPane;
        public Console(){
            super(new ScrollPane(),  400,  400);
            this.scrollPane = (ScrollPane) this.getRoot();
            shortDateFormat = DateFormat.getDateTimeInstance( DateFormat.SHORT,
                    DateFormat.SHORT);

            this.vBox = new VBox();
            scrollPane.setContent(vBox);
            this.addLogs("Test logs");
            // primaryStage.setFullScreen(true);

        }

    public void addLogs(String s){
           String log = shortDateFormat.format(new Date());

            log += " : " + s;
            Text t = new Text(log);
            vBox.getChildren().add(t);
        }



    }

