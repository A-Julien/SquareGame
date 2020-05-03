package FX.Manager;

import FX.Map.CellFX;
import FX.Map.Grid;
import Manager.Map.Cell;
import Manager.Map.ZoneFx;
import Manager.Manager;
import Manager.Map.Zone;
import Utils.Logger.SimpleLogger;
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

class ZoneManager extends BorderPane {
    private TableView<ZoneFx> tableView;
    private ArrayList<ZoneFx> zones;
    private EventHandler<ActionEvent> eventColorPicker;
    private ZoneFx currentZone;
    private Grid grid;
    private ZoneFx initialZone;
    private Button launch;
    private SimpleLogger logger;

    private Manager manager;

    ZoneManager(Grid grid, Manager manager) {
        super();
        this.logger = new SimpleLogger("FX_MAP_BUILDER",null);

        this.manager = manager;
        this.grid = grid;
        tableView = new TableView<>();
        tableView.setEditable(true);

        tableView.setOnMousePressed(event -> {
            currentZone = zones.get(tableView.getFocusModel().getFocusedIndex());
            grid.setCurrentZone(currentZone);
        });

        TableColumn<ZoneFx, String> zoneName = new TableColumn<>("Zone");
        zoneName.setCellValueFactory(new PropertyValueFactory<>("nomZone"));

        zoneName.setCellFactory(TextFieldTableCell.forTableColumn());
        zoneName.setOnEditCommit((TableColumn.CellEditEvent<ZoneFx, String> event) -> {
            TablePosition<ZoneFx, String> pos = event.getTablePosition();
            String newName = event.getNewValue();
            tableView.getItems().get(event.getTablePosition().getRow()).setNomZone(newName);
            refreshCellOnGrid(null);
        });

        TableColumn<ZoneFx, ColorPicker> cCol = new TableColumn<>("Couleur");
        cCol.setCellValueFactory(new PropertyValueFactory<>("colorPicker"));
        cCol.setMaxWidth(80);

        TableColumn<ZoneFx, String> zoneIP = new TableColumn<>("Adresse IP");
        zoneIP.setCellValueFactory(new PropertyValueFactory<>("ip"));

        zoneIP.setCellFactory(TextFieldTableCell.forTableColumn());
        zoneIP.setOnEditCommit((TableColumn.CellEditEvent<ZoneFx, String> event) -> {
            String newName = event.getNewValue();
            tableView.getItems().get(event.getTablePosition().getRow()).setIp(newName);
        });

        tableView.getColumns().addAll(zoneName,cCol, zoneIP);

        eventColorPicker = e -> {
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
        launch = new Button("Initialisation");
        ToolBar toolBar = new ToolBar(ajouter, supprimer, launch);

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

        ajouter.setOnAction(eventAjouter);
        supprimer.setOnAction(eventSupprimer);

        setTop(toolBar);
        for(int i = 0; i < grid.getX(); i ++){
            for(int j = 0; j < grid.getX(); j ++){
                CellFX c = grid.getCell(i,j);
                c.setZoneFx(initialZone);
            }
        }
    }


    private void refreshTable(){
        ObservableList<ZoneFx> list = FXCollections.observableArrayList();
        list.addAll(zones);
        tableView.setItems(list);
    }

    ZoneFx getCurrentZone() {
        return currentZone;
    }

    private void refreshCellOnGrid(Zone delete){
        for(int i = 0; i < grid.getX(); i ++){
            for(int j = 0; j < grid.getX(); j ++){
                CellFX c = grid.getCell(i,j);
                if(c.getZoneFx() == delete){
                    c.setZoneFx(initialZone);
                } else {
                    for(ZoneFx z : zones){
                        if(c.getZoneFx().getId() == z.getId() ){
                            c.setZoneFx(z);
                        }
                    }
                }
            }
        }
    }

    Button getLaunchButton(){
        return launch;
    }


    /**
     * Button start trigger
     * build the map and launch manager
     */
    void eventLaunch(){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Initialisation");
        alert.setHeaderText("Waiting server..");
        alert.showAndWait();

        this.logger.log("Build map");
        setTop(null);

        grid.rmHandlerSelection();


        ArrayList<Zone> finalZone = new ArrayList();
        boolean find = false;
        Zone z = null;
        for(int i = 0; i < grid.getX(); i++){
            for(int j = 0; j < grid.getY(); j++){
                int index = -1;
                find = false;
                for (Zone zone: finalZone) {
                    if (zone.getId() == grid.cellFXES[i][j].getZoneFx().getId()) {
                        find  = true;
                        index++;
                        break;
                    }
                    index++;
                }
                if(!find){
                    z = new Zone(grid.cellFXES[i][j].getZoneFx());
                    z.addCell(new Cell(i, j));
                    z.setColor(
                            (grid.cellFXES[i][j].getZoneFx()).getZoneColor().getRed(),
                            (grid.cellFXES[i][j].getZoneFx()).getZoneColor().getGreen(),
                            (grid.cellFXES[i][j].getZoneFx()).getZoneColor().getBlue()
                    );

                    finalZone.add(z);
                } else {
                    finalZone.get(index).addCell(new Cell(i, j));
                }
            }
        }


        this.manager.setMap(finalZone);
        this.logger.log("Map correctly parse");
        for(Zone zone : finalZone){
            this.logger.log(zone.toString());
        }
        refreshTable();
        try {
            this.manager.run();
        } catch (ServerNotSetException | TimeoutException | MapNotSetException | IOException e) {
            System.out.println("Manager error while starting : " + e.toString());
        }
    }
}
