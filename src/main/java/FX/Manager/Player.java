package FX.Manager;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class Player {
    private static int compteur_id = 0;

    int x = 0;
    int y = 0;
    String name = "Nouveau Joueur";
    int id;
    public ColorPicker colorPicker;
    public Player(){
        this.id = compteur_id;
        compteur_id++;
        colorPicker = new ColorPicker(Color.color(Math.random(), Math.random(), Math.random()));

    }

    public Player(int x, int y){
        this.x = x;
        this.y = y;
        this.id = compteur_id;
        compteur_id++;
        colorPicker = new ColorPicker(Color.color(Math.random(), Math.random(), Math.random()));
    }

     public Player(String name, int x, int y){
        this.name = name;
        this.x = x;
        this.y = y;
        this.id = compteur_id;
        compteur_id++;
         colorPicker = new ColorPicker(Color.color(Math.random(), Math.random(), Math.random()));
    }

    public Player(String name){
        this.name = name;
        colorPicker = new ColorPicker(Color.color(Math.random(), Math.random(), Math.random()));
        this.y = y;
        this.id = compteur_id;
        compteur_id++;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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
    @Override
    public String toString() {
        return ("Id : " + id + " Pos : ("+this.x+";"+this.y+") couleur : "+ getColor() );
    }
}
