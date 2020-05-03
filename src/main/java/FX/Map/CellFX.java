package FX.Map;

import Manager.Map.ZoneFx;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.*;

/**
 * Definition of cell for FX
 */
public class CellFX extends Rectangle {
    private Point point;
    private ZoneFx zoneFx;

    CellFX(int x, int y, double height, double width) {
        point = new Point(x, y);

        setX(x * width);
        setY(y * height);
        setWidth(width);
        setHeight(height);
        setFill(Color.WHITE);
        setStrokeType(StrokeType.CENTERED);
        setStroke(Color.BLACK);
    }

    
    Point getPoint() {
        return point;
    }

    public void colorSwap(Color c) {
        setFill(c);
    }

    public void setZone(ZoneFx zone) {
        this.zoneFx = zone;
        colorSwap(zoneFx.getZoneColor());
    }

    public ZoneFx getZoneFx() {
        return zoneFx;
    }

    public void setZoneFx(ZoneFx zoneFx) {
        this.zoneFx = zoneFx;
        colorSwap(zoneFx.getZoneColor());
    }

    @Override
    public String toString() {
        return "Position : (" + point.getX() + " ; " + point.getY() + " ).\n" + zoneFx;
    }
}
