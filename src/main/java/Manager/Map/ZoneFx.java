package Manager.Map;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;


public class ZoneFx extends Zone {
    public ColorPicker colorPicker;
    EventHandler<ActionEvent> eventColorPicker;

    public ZoneFx(String nom, Color color){
        super(nom);
        this.colorPicker = new ColorPicker();
        colorPicker.setValue(color);
    }

    public ColorPicker getColorPicker() {
        return colorPicker;
    }

    public void setColorPicker(ColorPicker colorPicker) {
        this.colorPicker = colorPicker;
    }

    public Color getZoneColor(){
        return colorPicker.getValue();
    }

    public void setEventColorPicker(EventHandler<ActionEvent> eventColorPicker) {
        colorPicker.setOnAction(eventColorPicker);
    }


}
