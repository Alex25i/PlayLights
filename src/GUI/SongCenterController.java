package GUI;

import Data.Gig;
import Data.SetList;
import Data.Song;
import Logic.PlayLights;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

public class SongCenterController {
    private SongCenterController songCenterController;

    @FXML
    private AnchorPane tableContainer;

    private TableView<Gig> gigTable;
    private TableView<Song> songTable;

    @FXML
    public void initialize() {
        setupTableGigs();
        tableContainer.getChildren().add(gigTable);
    }

    private void setupTableGigs() {
        ObservableList<Gig> gigs = FXCollections.observableArrayList();
        gigs.addAll(PlayLights.getPlayLights().getLibrary().getGigList());

        gigTable = new TableView<>();
        gigTable.setItems(gigs);

        TableColumn<Gig, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        gigTable.getColumns().add(dateColumn);

        TableColumn<Gig, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        gigTable.getColumns().add(locationColumn);

        gigTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        gigTable.setEditable(false);

        if (!gigs.isEmpty()) {
            gigTable.getSelectionModel().select(0, dateColumn);
        }
    }

    private void setupTableSongs(Gig gig) {
        ObservableList<Song> songs = FXCollections.observableArrayList();
        for (SetList set : gig.getSets()) {
            songs.addAll(set.getSongs());
        }

        songTable = new TableView<>();
        songTable.setItems(songs);

        TableColumn<Song, String> positionColumn = new TableColumn<>("Position");
        songTable.getColumns().add(positionColumn);

        TableColumn<Song, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        songTable.getColumns().add(nameColumn);

        songTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        songTable.setEditable(false);

        if (!songs.isEmpty()) {
            songTable.getSelectionModel().select(0, positionColumn);
        }
    }

    public SongCenterController getSongCenterController() {
        return songCenterController;
    }
}
