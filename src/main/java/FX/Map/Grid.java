package FX.Map;
import Manager.Map.ZoneFx;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.awt.Point;

public class Grid extends Group {
    public Case[][] cases;
    private Group grille = new Group();
    private Rectangle zone = new Rectangle();
    private double largeurCase, hauteurCase;
    private int x, y;

    private Point caseDebut = new Point(0,0);
    private Point debutSelection = new Point(0,0);
    private ZoneFx currentZone;

    private Circle player;

    private boolean manager;


    public Grid(int nbCaseHauteur, int nbCaseLargeur, double hauteurPX, double largeurPx, boolean manager) {
        this.x = nbCaseLargeur;
        this.y = nbCaseHauteur;
        this.largeurCase = largeurPx / nbCaseLargeur;
        this.hauteurCase = hauteurPX / nbCaseHauteur;
        this.manager = manager;

        createCell();

        Group zoneSelection = new Group();
        this.getChildren().addAll(zoneSelection,grille);
        afficherCases();
        grille.setOpacity(0.5);

        if(manager) zoneSelection.getChildren().add(zone);

        player = new Circle();
        player.setCenterX(hauteurCase/2);
        player.setCenterY(largeurCase/2);
        player.setRadius(Math.min(hauteurCase, largeurCase)/8*3);
    }



    private void afficherCases() {
        grille.getChildren().removeAll();
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                grille.getChildren().add(cases[i][j]);
            }
        }
    }


    private EventHandler<MouseEvent> handlerCLICK = event -> colorerZone(
            ((Case) event.getTarget()).getPoint() ,((Case) event.getTarget()).getPoint()
    );

    private EventHandler<MouseEvent> handlerPRESS = event -> {
        debutSelection = new Point((int)event.getX(), (int) event.getY());
        caseDebut = ((Case) event.getTarget()).getPoint();
    };

    private EventHandler<MouseEvent> handlerDetect = event -> {
        System.out.println("Drag la case : " + caseDebut);
        ((Case) event.getTarget()).startFullDrag();
    };


    private EventHandler<MouseEvent> handlerDRAGING = event -> {
        Point finSelection = new Point((int) event.getX(), (int) event.getY());
       dessinerZoneSelection(debutSelection, finSelection);
    };

    private EventHandler<MouseEvent> handlerEND = event -> {
        Point caseFin = ((Case) event.getTarget()).getPoint();
        System.out.println(caseFin);
        zone.setStroke(Color.TRANSPARENT);
        zone.setX(0);
        zone.setY(0);
        zone.setWidth(0);
        zone.setHeight(0);
        colorerZone(caseDebut, caseFin);


    };

    private EventHandler<MouseEvent> handlerShowInfoCell = event -> {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");

        alert.setContentText(event.getTarget().toString());
        alert.setHeaderText("Informations :");
        alert.showAndWait();


    };

   private void dessinerZoneSelection(Point p1, Point p2){
       int minX = Math.min((int)p1.getX(), (int) p2.getX());
       int maxX = Math.max((int)p1.getX(), (int) p2.getX());
       int minY = Math.min((int)p1.getY(), (int) p2.getY());
       int maxY = Math.max((int)p1.getY(), (int) p2.getY());

       zone.setX(minX);
       zone.setY(minY);
       zone.setWidth(maxX-minX);
       zone.setHeight(maxY-minY);
       zone.setFill(Color.TRANSPARENT);
       zone.setStrokeType(StrokeType.CENTERED);
       zone.setStroke(Color.BLACK);
    }

    private void colorerZone(Point p1, Point p2){
        int minX = Math.min((int)p1.getX(), (int) p2.getX());
        int maxX = Math.max((int)p1.getX(), (int) p2.getX());
        int minY = Math.min((int)p1.getY(), (int) p2.getY());
        int maxY = Math.max((int)p1.getY(), (int) p2.getY());


        for(int i = minX; i <= maxX; i++){
            for(int j = minY; j <= maxY; j++){
                cases[i][j].setZone(currentZone);
            }
        }
    }

    public void setCurrentZone(ZoneFx zone) {
        this.currentZone = zone;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
        if(this.manager){
            handlerSelection();
        }

    }

    public void rmHandlerSelection(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cases[i][j].setOnMouseClicked(handlerShowInfoCell);
                cases[i][j].setOnDragDetected(null);
                cases[i][j].setOnMouseDragOver(null);
                cases[i][j].setOnMouseDragReleased(null);
                cases[i][j].setOnMousePressed(null); }
        }
    }

    public void affCircle(){
       if(!getChildren().contains(player)) getChildren().add(player);
    }

    public void setPosCircle(int x, int y){
       player.setCenterX(x*largeurCase+largeurCase/2);
        player.setCenterY(y*hauteurCase+hauteurCase/2);
   }
}
