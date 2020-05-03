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

/**
 * Define the grid
 */
public class Grid extends Group {
    public CellFX[][] cellFXES;
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
                grille.getChildren().add(cellFXES[i][j]);
            }
        }
    }

    private EventHandler<MouseEvent> handlerCLICK = event -> colorerZone(
            ((CellFX) event.getTarget()).getPoint() ,((CellFX) event.getTarget()).getPoint()
    );

    private EventHandler<MouseEvent> handlerPRESS = event -> {
        debutSelection = new Point((int)event.getX(), (int) event.getY());
        caseDebut = ((CellFX) event.getTarget()).getPoint();
    };

    private EventHandler<MouseEvent> handlerDetect = event -> {
        ((CellFX) event.getTarget()).startFullDrag();
    };


    private EventHandler<MouseEvent> handlerDRAGING = event -> {
        Point finSelection = new Point((int) event.getX(), (int) event.getY());
       dessinerZoneSelection(debutSelection, finSelection);
    };

    private EventHandler<MouseEvent> handlerEND = event -> {
        Point caseFin = ((CellFX) event.getTarget()).getPoint();
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
                cellFXES[i][j].setZone(currentZone);
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

    public CellFX getCell(int x, int y){
       return cellFXES[x][y];
    }

    private void handlerSelection(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {

                cellFXES[i][j].setOnMouseClicked(handlerCLICK);
                cellFXES[i][j].setOnDragDetected(handlerDetect);
                cellFXES[i][j].setOnMouseDragOver(handlerDRAGING);
                cellFXES[i][j].setOnMouseDragReleased(handlerEND);
                cellFXES[i][j].setOnMousePressed(handlerPRESS);
            }
        }
    }

    private void createCell(){
        this.cellFXES = new CellFX[x][y];
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cellFXES[i][j] = new CellFX(i, j, this.hauteurCase, this.largeurCase);
            }
        }
        if(this.manager){
            handlerSelection();
        }

    }

    public void rmHandlerSelection(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                cellFXES[i][j].setOnMouseClicked(handlerShowInfoCell);
                cellFXES[i][j].setOnDragDetected(null);
                cellFXES[i][j].setOnMouseDragOver(null);
                cellFXES[i][j].setOnMouseDragReleased(null);
                cellFXES[i][j].setOnMousePressed(null); }
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
