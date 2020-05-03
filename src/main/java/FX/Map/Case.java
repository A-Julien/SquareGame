package FX.Map;

import Manager.Map.ZoneFx;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.*;

public class Case extends Rectangle {
    private Point point;
    private ZoneFx zoneFx;

    Case(int x, int y, double height, double width) {
        point = new Point(x, y);

        setX(x * width);
        setY(y * height);
        setWidth(width);
        setHeight(height);
        setFill(Color.WHITE);
        setStrokeType(StrokeType.CENTERED);
        setStroke(Color.BLACK);

    }

    
    public Point getPoint() {
        return point;
    }

    public void colorSwap(Color c) {
        setFill(c);
    }

    public void setZone(ZoneFx zone) {
        this.zoneFx = zone;
        colorSwap(zoneFx.getZoneColor());
    }

    public void removeZone() {
        zoneFx = null;
        colorSwap(Color.WHITE);
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