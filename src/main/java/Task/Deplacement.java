package Task;

import java.io.Serializable;

public enum Deplacement implements Serializable {
    HAUT("UP"),
    BAS("DOWN"),
    GAUCHE("LEFT"),
    DROITE("RIGHT");

    private final String cmd;

    Deplacement(String cmd) {
        this.cmd = cmd;
    }


}