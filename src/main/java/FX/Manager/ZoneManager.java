package FX.Manager;

import FX.Case;
import FX.Grid;
import Manager.Map.Cell;
import Manager.Map.ZoneFx;
import Manager.Manager;
import Manager.Map.Zone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import Exception.ServerNotSetException;
import Exception.MapNotSetException;

public class ZoneManager extends BorderPane {
    private TableView<ZoneFx> tableView;
    private ArrayList<ZoneFx> zones;
    private EventHandler<ActionEvent> eventColorPicker;
    private ZoneFx currentZone;
    private Grid grid;
    private ZoneFx initialZone;
    private Button launch;

    private Manager manager;

    public ZoneManager(Grid grid, Manager manager) {
        super();

        this.manager = manager;
        this.grid = grid;
        tableView = new TableView<ZoneFx>();
        tableView.setEditable(true);

        tableView.setOnMousePressed(event -> {
            System.out.println("Selection de la table view");

            currentZone = zones.get(tableView.getFocusModel().getFocusedIndex());
            grid.setCurrentZone(currentZone);
            System.out.println(currentZone);
        });

        TableColumn<ZoneFx, String> zoneName = new TableColumn<ZoneFx, String>("Zone");
        zoneName.setCellValueFactory(new PropertyValueFactory<>("nomZone"));

        zoneName.setCellFactory(TextFieldTableCell.<ZoneFx>forTableColumn());
        zoneName.setOnEditCommit((TableColumn.CellEditEvent<ZoneFx, String> event) -> {
            TablePosition<ZoneFx, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            System.out.println("Modification en : " + newName);
            tableView.getItems().get(event.getTablePosition().getRow()).setNomZone(newName);
            refreshCellOnGrid(null);
        });

        TableColumn<ZoneFx, ColorPicker> cCol = new TableColumn<ZoneFx, ColorPicker> ("Couleur");
        cCol.setCellValueFactory(new PropertyValueFactory<>("colorPicker"));
        cCol.setMaxWidth(80);

        TableColumn<ZoneFx, String> zoneIP = new TableColumn<>("Adresse IP");
        zoneIP.setCellValueFactory(new PropertyValueFactory<>("ip"));

        zoneIP.setCellFactory(TextFieldTableCell.<ZoneFx>forTableColumn());
        zoneIP.setOnEditCommit((TableColumn.CellEditEvent<ZoneFx, String> event) -> {
            //TablePosition<Zone, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            System.out.println("Modification en : " + newName);
            tableView.getItems().get(event.getTablePosition().getRow()).setIp(newName);
        });






        tableView.getColumns().addAll(zoneName,cCol, zoneIP);

        eventColorPicker = e -> {
            System.out.println("Nouvelle couleur : " + ((ColorPicker) e.getTarget()).getValue());
            refreshCellOnGrid(null);
        };
        initialZone = new ZoneFx("Init", Color.WHITE);
        initialZone.setEventColorPicker(eventColorPicker);
        zones = new ArrayList<ZoneFx>();
        currentZone = new ZoneFx("ZoneA", Color.BLUE);
        currentZone.setEventColorPicker(eventColorPicker);
        zones.add(currentZone);
        currentZone = new ZoneFx("ZoneB", Color.YELLOWGREEN);
        currentZone.setEventColorPicker(eventColorPicker);
        zones.add(currentZone);

        refreshTable();
        setCenter(tableView);

        Button ajouter = new Button("+");
        Button supprimer = new Button("-");
        Button print = new Button("Imprimer zone");
        launch = new Button("Initialisation");
        ToolBar toolBar = new ToolBar(ajouter, supprimer, print, launch);

        EventHandler<ActionEvent> eventAjouter = e -> {
            currentZone = new ZoneFx("Nouvelle Zone", Color.color(Math.random(), Math.random(), Math.random()));
            currentZone.setEventColorPicker(eventColorPicker);
            zones.add(currentZone);

            refreshTable();

        };

        EventHandler<ActionEvent> eventSupprimer = e -> {
            Zone aDelete = zones.get(tableView.getFocusModel().getFocusedIndex());
            zones.remove(aDelete);
            try {
                currentZone = zones.get(0);
            } catch (IndexOutOfBoundsException i) {
                currentZone = initialZone;
            }
            grid.setCurrentZone(currentZone);
            refreshTable();
            refreshCellOnGrid(aDelete);

        };

        EventHandler<ActionEvent> eventImprimer = e -> {
            for (int i = 0; i < grid.getX(); i++) {
                for (int j = 0; j < grid.getX(); j++) {
                    System.out.println(" Case : " + grid.getCell(i, j));
                }
            }

        };

        ajouter.setOnAction(eventAjouter);
        supprimer.setOnAction(eventSupprimer);
        print.setOnAction(eventImprimer);

        setTop(toolBar);
        for(int i = 0; i < grid.getX(); i ++){
            for(int j = 0; j < grid.getX(); j ++){
                Case c = grid.getCell(i,j);
                c.setZ(initialZone);
            }
        }
    }


    public void refreshTable(){
        ObservableList<ZoneFx> list = FXCollections.observableArrayList();
        for(int i = 0 ; i < zones.size(); i++){
            list.add(zones.get(i));
        }
        tableView.setItems(list);
    }

    public ZoneFx getCurrentZone() {
        return currentZone;
    }

    public void refreshCellOnGrid(Zone delete){
        System.out.println("Je dois metre a jour toutes les cellules");
        if(delete != null){
            System.out.println("La zone " + delete + " a était supprimé");
        }
        for(int i = 0; i < grid.getX(); i ++){
            for(int j = 0; j < grid.getX(); j ++){
                Case c = grid.getCell(i,j);
                if(c.getZ() == delete){
                    System.out.println("Il faut supprimer la zone pour " + c.getPoint());
                    c.setZ(initialZone);
                   // c.removeZone();
                } else {
                    for(ZoneFx z : zones){
                        if(c.getZ().getId() == z.getId() ){
                            c.setZ(z);
                        }
                    }
                }
            }
        }
    }

    public Button getLaunchButton(){
        return launch;
    }

    /**
     * Button start trigger
     * build the map and launch manager
     */
    public void eventLaunch(){
        System.out.print("Build map");
        setTop(null);
        //toolBar = new ToolBar(print);
        //setTop(toolBar);
        grid.rmHandlerSelection();
        //zones.add(initialZone);


        ArrayList<Zone> finalZone = new ArrayList();
        boolean find = false;
        Zone z = null;
        for(int i = 0; i < grid.getX(); i++){
            for(int j = 0; j < grid.getY(); j++){

                int index = -1;
                find = false;

                for (Zone zone: finalZone) {
                    if (zone.getId() == grid.cases[i][j].getZ().getId()) { //TODO HUM... PB.. getid()
                        find  = true;
                        index++;
                        break;
                    }
                    index++;
                }

                if(!find){
                    z = new Zone(grid.cases[i][j].getZ());
                    z.addCell(new Cell(i, j));
                    z.setColor(
                            (grid.cases[i][j].getZ()).getZoneColor().getRed(),
                            (grid.cases[i][j].getZ()).getZoneColor().getGreen(),
                            (grid.cases[i][j].getZ()).getZoneColor().getBlue()
                    );

                    finalZone.add(z);
                } else {
                    finalZone.get(index).addCell(new Cell(i, j));
                }
            }
            System.out.print(".");
        }

        System.out.println("");

        this.manager.setMap(finalZone);

        for(Zone zone : finalZone){
            System.out.println(zone.toString());
        }
        refreshTable();
        try {
            this.manager.run();
        } catch (ServerNotSetException | TimeoutException | MapNotSetException | IOException e) {
            System.out.println("Manager error while starting : " + e.toString());
        }
    }
}
