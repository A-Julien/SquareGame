package FX;
import Class.*;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.Point;
import java.util.ArrayList;

public class Grid extends Group {
    Case[][] cases;
    private Group zoneSelection= new Group();
    private Group grille = new Group();
    private Rectangle zone = new Rectangle();
    //int caseHauteur, int caseLargeur, int hauteurPX, int largeurPx;
    private double largeurCase, hauteurCase;
    int x, y;

    private Point caseDebut = new Point(0,0);
    private Point caseFin = new Point(0,0);
    private Point debutSelection = new Point(0,0);
    private Point finSelection = new Point(0,0);
    private ZoneFx currentZone;

    private Circle player;



    Grid(int nbCaseHauteur, int nbCaseLargeur, double hauteurPX, double largeurPx) {
        this.x = nbCaseLargeur;
        this.y = nbCaseHauteur;
        this.largeurCase = largeurPx / nbCaseLargeur;
        //this.largeurCase = this.compute / nbCaseHauteur
        this.hauteurCase = hauteurPX / nbCaseHauteur;
       // this.hauteurCase = maxHeight() / nbCaseHauteur;



        createCell();

        this.getChildren().addAll(zoneSelection,grille);
        afficherCases();
        grille.setOpacity(0.5);
        zoneSelection.getChildren().add(zone);

        player = new Circle();
        player.setCenterX(hauteurCase/2);
        player.setCenterY(largeurCase/2);
        player.setRadius(Math.min(hauteurCase, largeurCase)/8*3);


    }

    public void afficherCases() {
        grille.getChildren().removeAll();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                grille.getChildren().add(cases[i][j]);
            }
        }
    }


    EventHandler<MouseEvent> handlerCLICK = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            //System.out.println("Cliqué sur la case : " + ((Case) event.getTarget()).getPoint());
            colorerZone( ((Case) event.getTarget()).getPoint() ,((Case) event.getTarget()).getPoint());

        }
    };

    EventHandler<MouseEvent> handlerPRESS = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            debutSelection = new Point((int)event.getX(), (int) event.getY());
            caseDebut = ((Case) event.getTarget()).getPoint();
            //System.out.println("Pressed");
        }
    };

    EventHandler<MouseEvent> handlerDetect = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

            System.out.println("Drag la case : " + caseDebut);

            ((Case) event.getTarget()).startFullDrag();
            //debutSelection = new Point((int)event.getX(), (int) event.getY());
            //caseDebut = ((Case) event.getTarget()).getPoint();
        }
    };


    EventHandler<MouseEvent> handlerDRAGING = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
           finSelection = new Point((int)event.getX(), (int) event.getY());
           dessinerZoneSelection(debutSelection, finSelection);
        }
    };

    EventHandler<MouseEvent> handlerEND = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            //System.out.println("Finis");
            caseFin = ((Case) event.getTarget()).getPoint();
            System.out.println(caseFin);
            zone.setStroke(Color.TRANSPARENT);
            zone.setX(0);
            zone.setY(0);
            zone.setWidth(0);
            zone.setHeight(0);
            colorerZone(caseDebut,caseFin);


        }
    };

    EventHandler<MouseEvent> handlerShowInfoCell = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");

            alert.setContentText(((Case) event.getTarget()).toString());
            alert.setHeaderText("Informations :");
            alert.showAndWait();


        }
    };

   public void dessinerZoneSelection(Point p1, Point p2){
     //  System.out.println("Je dessine");
       int minX = Math.min((int)p1.getX(), (int) p2.getX());
       int maxX = Math.max((int)p1.getX(), (int) p2.getX());
       int minY = Math.min((int)p1.getY(), (int) p2.getY());
       int maxY = Math.max((int)p1.getY(), (int) p2.getY());
     //  zoneSelection.getChildren().removeAll();

      // Rectangle zone = new Rectangle();
       zone.setX(minX);
       zone.setY(minY);
       zone.setWidth(maxX-minX);
       zone.setHeight(maxY-minY);
       zone.setFill(Color.TRANSPARENT);
       zone.setStrokeType(StrokeType.CENTERED);
       zone.setStroke(Color.BLACK);
    }

    public void colorerZone(Point p1, Point p2){
        int minX = Math.min((int)p1.getX(), (int) p2.getX());
        int maxX = Math.max((int)p1.getX(), (int) p2.getX());

        int minY = Math.min((int)p1.getY(), (int) p2.getY());
        int maxY = Math.max((int)p1.getY(), (int) p2.getY());


        for(int i = minX; i <= maxX; i++){
            for(int j = minY; j <= maxY; j++){
                //cases[i][j].changerCouleur(currentZone.getColor());
                cases[i][j].setZone(currentZone);
            }
        }
        //System.out.print("Coloriage de la zone ");
    }

    public void setCurrentZone(ZoneFx zone) {
        this.currentZone = zone;
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

    public void setCell(int x, int y, Case c){
      cases[x][y] = c;
    }

    public Case getCell(int x, int y){
       return cases[x][y];
    }

    private void handlerSelection(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {

                cases[i][j].setOnMouseClicked(handlerCLICK);
                cases[i][j].setOnDragDetected(handlerDetect);
                cases[i][j].setOnMouseDragOver(handlerDRAGING);
                cases[i][j].setOnMouseDragReleased(handlerEND);
                cases[i][j].setOnMousePressed(handlerPRESS);

                //  cases[i][j].setOnMouseDragOver(handlerMAJ);
                //  cases[i][j].setOnMouseDragReleased(handlerENDING);



            }
        }
    }

    private void createCell(){
        this.cases = new Case[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cases[i][j] = new Case(i, j, this.hauteurCase, this.largeurCase);

            }
        }
        handlerSelection();
    }

    protected void rmHandlerSelection(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cases[i][j].setOnMouseClicked(handlerShowInfoCell);
                cases[i][j].setOnDragDetected(null);
                cases[i][j].setOnMouseDragOver(null);
                cases[i][j].setOnMouseDragReleased(null);
                cases[i][j].setOnMousePressed(null);

                //  cases[i][j].setOnMouseDragOver(handlerMAJ);
                //  cases[i][j].setOnMouseDragReleased(handlerENDING);



            }
        }
    }

    protected void affCircle(){
       if(!getChildren().contains(player)){
           getChildren().add(player);
       }
    }

    protected void unAffCircle(){
        if(getChildren().contains(player)){
            getChildren().remove(player);
        }
    }

    public void setPosCircle(int x, int y){
       player.setCenterX(x*largeurCase+largeurCase/2);
        player.setCenterY(y*hauteurCase+hauteurCase/2);
   }

   public void setColorCircle(Color c){
       player.setFill(c);
   }

}
