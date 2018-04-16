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
    public enum VIEW_MODES {GIG, SET, SONG}

    private static SongCenterController songCenterController;

    @FXML
    private AnchorPane tableContainer;

    private VIEW_MODES viewMode;
    private TableView<Gig> gigTable;
    private TableView<SetList> setsList;
    private TableView<Song> songTable;
    private ObservableList<Gig> gigs;

    @FXML
    public void initialize() {
        songCenterController = this;
        gigs = FXCollections.observableArrayList();
        setupTableGigs();
        showTable(gigTable);
    }

    private void setupTableGigs() {
        gigs.addAll(PlayLights.getInstance().getLibrary().getGigList());

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
        gigTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        gigTable.prefWidthProperty().bind(tableContainer.widthProperty());

        dateColumn.prefWidthProperty().bind(gigTable.widthProperty().multiply(0.3));
        locationColumn.prefWidthProperty().bind(gigTable.widthProperty().multiply(0.7));

        dateColumn.setResizable(false);
        locationColumn.setResizable(false);

        dateColumn.setReorderable(false);
        locationColumn.setReorderable(false);

        if (!gigs.isEmpty()) {
            gigTable.getSelectionModel().select(0, dateColumn);
        }
    }

    private void setupTableSongs(Gig gig) {
        //TODO: Change Implementation to songs of a set, only

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
            songTable.getSelectionModel().select(0);
        }
    }

    /**
     * called when the MixTrack selection wheel was moved to the left
     */
    public void selectionLeft() {
        selection(1);
    }


    /**
     * called when the MixTrack selection wheel was moved to the right
     */
    public void selectionRight() {
        selection(2);
    }

    private void selection(int action) {
        if (tableContainer.getChildren().isEmpty()) {
            // there is no tabele displayed currently
            new IllegalStateException("There is no table displayed at the SongCenter, currently. Check implementation").printStackTrace();
            return;
        }
        switch (viewMode) {
            case GIG: {
                int currentSelectedIndex = gigTable.getSelectionModel().getSelectedIndex();
                if (action == 1) {
                    // left action

                    if (currentSelectedIndex > 0) {
                        // it is not the last element which is currently selected
                        gigTable.getSelectionModel().clearAndSelect(currentSelectedIndex - 1);
                    }
                } else if (action == 2) {
                    // right action
                    if (currentSelectedIndex < gigTable.getItems().size() - 1) {
                        // it is not the last element which is currently selected
                        gigTable.getSelectionModel().clearAndSelect(currentSelectedIndex + 1);
                    }
                }
                break;
            }
        }
    }

    private void showTable(TableView table) {
        if (tableContainer.getChildren().isEmpty()) {
            tableContainer.getChildren().add(table);
        } else {
            tableContainer.getChildren().set(0, table);
        }
        if (table == gigTable) {
            viewMode = VIEW_MODES.GIG;
        } else if (table == setsList) {
            viewMode = VIEW_MODES.SET;
        } else if (table == songTable) {
            viewMode = VIEW_MODES.SONG;
        }
    }

    public static SongCenterController getInstance() {
        return songCenterController;
    }

    public ObservableList<Gig> getGigs() {
        return gigs;
    }
}
