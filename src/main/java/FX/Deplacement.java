package FX;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Deplacement extends BorderPane {
    Button gauche;
    Button droite;
    Button haut;
    Button bas;
    HBox hbox;
    Deplacement(){
        super();
        gauche = new javafx.scene.control.Button("←");
        droite = new javafx.scene.control.Button("→");
        haut = new javafx.scene.control.Button("↑");
        bas = new javafx.scene.control.Button("↓ ");
        hbox = new HBox();
        hbox.getChildren().addAll(gauche,droite,haut,bas);
        setCenter(hbox);
    }

    public Button getGauche() {
        return gauche;
    }

    public Button getDroite() {
        return droite;
    }

    public Button getHaut() {
        return haut;
    }

    public Button getBas() {
        return bas;
    }
}
