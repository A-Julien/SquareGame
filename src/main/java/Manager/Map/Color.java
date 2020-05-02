package Manager.Map;

import java.io.Serializable;

public class Color implements Serializable {
    public double red;
    public double green;
    public double blue;

    public Color(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
}
