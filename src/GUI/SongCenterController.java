package GUI;

import Data.Gig;
import Logic.PlayLights;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class SongCenterController {
    private SongCenterController songCenterController;

    @FXML
    private AnchorPane tableContainer;

    @FXML
    private TableView<Gig> gigTable;

    @FXML
    public void initialize() {
        setupTableGigs();
    }

    private void setupTableGigs() {
        ObservableList<Gig> gigs = FXCollections.observableArrayList();
        gigs.addAll(PlayLights.getPlayLights().getLibrary().getGigList());
        gigTable.setItems(gigs);
        gigTable.setItems(gigs);

        TableColumn<Gig, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        gigTable.getColumns().add(dateColumn);

        TableColumn<Gig, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        gigTable.getColumns().add(locationColumn);
    }

    public SongCenterController getSongCenterController() {
        return songCenterController;
    }
}
