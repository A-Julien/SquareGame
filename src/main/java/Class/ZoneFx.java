package Class;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class ZoneFx extends Zone{
    public ColorPicker colorPicker;
    EventHandler<ActionEvent> eventColorPicker;


    public ZoneFx(String nom, Color color){
        super(nom, color);
        this.colorPicker = new ColorPicker();
        positionGrille = new ArrayList<>();
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public void setColorPicker(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }

    public Color getColor(){
        return colorPicker.getValue();
    }

    public void setEventColorPicker(EventHandler<ActionEvent> eventColorPicker) {
        colorPicker.setOnAction(eventColorPicker);
    }
}
