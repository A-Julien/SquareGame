package FX;
import Class.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;

public class PlayerManager extends BorderPane implements Mouvement {
    TableView<Player> tableView;
    ArrayList<Player> players;
    ToolBar toolBar;
    EventHandler<ActionEvent> eventAjouter;
    EventHandler<ActionEvent> eventSupprimer;
    EventHandler<ActionEvent> eventImprimer;
    EventHandler<ActionEvent> eventLaunch;
    EventHandler<ActionEvent> eventColorPicker;
    Player currentPlayer;
    Grid grid;
    Deplacement deplacement;


    PlayerManager(Grid grid) {
        super();
        this.grid = grid;
        tableView = new TableView<Player>();
        tableView.setEditable(true);

        players = new ArrayList();
        currentPlayer = new Player("The origin");
        players.add(currentPlayer);



        setCenter(tableView);

        Button ajouter = new Button("+");
        Button supprimer = new Button("-");
        Button print = new Button("Imprimer Joueurs");

        toolBar = new ToolBar(ajouter,supprimer,print);

        eventAjouter = new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                System.out.println("Ajout d'un joueur");
                players.add(new Player());
                refreshTable();

            }
        };

        eventSupprimer= e -> System.out.println("Supression d'un joueur");

        eventImprimer= new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                for(Player p : players){
                    System.out.println(p);
                }

            }
        };




        ajouter.setOnAction(eventAjouter);
        supprimer.setOnAction(eventSupprimer);
        print.setOnAction(eventImprimer);

        setTop(toolBar);



        TableColumn<Player, String> zoneName = new TableColumn<Player, String>("Joueur");
        zoneName.setCellValueFactory(new PropertyValueFactory<>("name"));

        zoneName.setCellFactory(TextFieldTableCell.<Player>forTableColumn());
        zoneName.setOnEditCommit((TableColumn.CellEditEvent<Player, String> event) -> {
            TablePosition<Player, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            System.out.println("Modification en : " + newName);
            tableView.getItems().get(event.getTablePosition().getRow()).setName(newName);
            // refreshCellOnGrid(null);
        });

        TableColumn<Player, Integer> zoneId = new TableColumn<Player, Integer>("id");
        zoneId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Player, Integer> zoneX = new TableColumn<Player, Integer>("X");
        zoneX.setCellValueFactory(new PropertyValueFactory<>("x"));

        TableColumn<Player, Integer> zoneY = new TableColumn<Player, Integer>("Y");
        zoneY.setCellValueFactory(new PropertyValueFactory<>("y"));

        tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("Selection de la table view");

                currentPlayer = players.get(tableView.getFocusModel().getFocusedIndex());
                moovPlayer();

            }
        });

        TableColumn<Player, ColorPicker> cCol = new TableColumn<Player, ColorPicker> ("Couleur");
        cCol.setCellValueFactory(new PropertyValueFactory<>("colorPicker"));



        tableView.getColumns().addAll(zoneName, zoneId, zoneX, zoneY, cCol);
        refreshTable();

        deplacement = new Deplacement();
        setBottom(deplacement);


        deplacement.setOnKeyPressed(clavier);
        tableView.setOnKeyPressed(clavier);



        deplacement.getBas().setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                mouvementBas();
            }
        });

        deplacement.getHaut().setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                mouvementHaut();
            }
        });

        deplacement.getDroite().setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                mouvementDroite();
            }
        });

        deplacement.getGauche().setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                mouvementGauche();
            }
        });
    }


    public void refreshTable(){
        ObservableList<Player> list = FXCollections.observableArrayList();
        for(int i = 0 ; i < players.size(); i++){
            list.add(players.get(i));
        }

        tableView.setItems(list);
        System.out.println("check");

        System.out.println(players.get(0));
        System.out.println(currentPlayer);
        tableView.refresh();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void refreshCellOnGrid(ZoneFx delete){
        System.out.println("Refresh de la grid");
    }

    public void moovPlayer(){
        grid.setPosCircle(currentPlayer.getX(), currentPlayer.getY());
        grid.setColorCircle(currentPlayer.getColor());
    }

    @Override
    public boolean mouvementGauche() {
        if(currentPlayer.getX() <= 0) {
            currentPlayer.setX(0);
            return false;
        }
        currentPlayer.setX(currentPlayer.getX()-1);
        moovPlayer();
        return true;

    }

    @Override
    public boolean mouvementDroite() {
        if(currentPlayer.getX() >= grid.getX()-1) {
            currentPlayer.setX(grid.getX()-1);
            return false;
        }
        currentPlayer.setX(currentPlayer.getX()+1);
        moovPlayer();
        return true;
    }

    @Override
    public boolean mouvementHaut() {
        if(currentPlayer.getY() <= 0) {
            currentPlayer.setY(0);
            return false;
        }
        currentPlayer.setY(currentPlayer.getY()-1);
        moovPlayer();
        return true;
    }

    @Override
    public boolean mouvementBas() {
        if(currentPlayer.getY() >= grid.getY() -1) {
            currentPlayer.setY(grid.getY()-1);
            return false;
        }
        currentPlayer.setY(currentPlayer.getY()+1);
        moovPlayer();
        return true;
    }

    EventHandler<KeyEvent> clavier = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent key) {
            boolean dep = false;
            if(key.getCode()== KeyCode.Q) {
                System.out.println("Déplacement gauche");
                dep = mouvementGauche();
            } else if(key.getCode()== KeyCode.D) {
                System.out.println("Déplacement droite");
                dep = mouvementDroite();
            } else if(key.getCode()== KeyCode.Z) {
                System.out.println("Déplacement Haut");
                dep = mouvementHaut();
            } else if(key.getCode()== KeyCode.S) {
                System.out.println("Déplacement Bas");
                dep = mouvementBas();
            }
            System.out.println("Déplacement : " + dep + " ; " + currentPlayer);
            tableView.refresh();
        }
    };





}
