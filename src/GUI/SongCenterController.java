package GUI;

import Data.Gig;
import Data.SetList;
import Data.Song;
import Logic.PlayLights;
import Midi.MixTrackController;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.TableViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.AnchorPane;

public class SongCenterController {
    public enum VIEW_MODES {GIG, SONG}

    private static SongCenterController songCenterController;

    @FXML
    private AnchorPane tableContainer;

    private VIEW_MODES viewMode;
    private TableView<Gig> gigTable;
    private TableView<TableSong> songTable;
    private ObservableList<Gig> gigs;
    private Gig currentGig;

    @FXML
    public void initialize() {
        songCenterController = this;
        gigs = FXCollections.observableArrayList();
        setupTableGigs();
        setupTableSongs();
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

        dateColumn.setMinWidth(120);
        dateColumn.setMaxWidth(120);

        dateColumn.prefWidthProperty().bind(gigTable.widthProperty().multiply(0.3));
        locationColumn.prefWidthProperty().bind(gigTable.widthProperty().multiply(0.7));

        dateColumn.setReorderable(false);
        locationColumn.setReorderable(false);
        gigTable.getSelectionModel().clearAndSelect(0);

        AnchorPane.setTopAnchor(gigTable, 0.0);
        AnchorPane.setLeftAnchor(gigTable, 0.0);
        AnchorPane.setBottomAnchor(gigTable, 0.0);
        AnchorPane.setRightAnchor(gigTable, 0.0);
    }

    private void setupTableSongs() {
        songTable = new TableView<>();

        TableColumn<TableSong, Integer> setNrColumn = new TableColumn<>("Set Nr.");
        setNrColumn.setCellValueFactory(param -> param.getValue().setNr);
        songTable.getColumns().add(setNrColumn);

        TableColumn<TableSong, Integer> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(param -> param.getValue().position);
        songTable.getColumns().add(positionColumn);

        TableColumn<TableSong, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().song.getName()));
        songTable.getColumns().add(nameColumn);

        TableColumn<TableSong, String> interpretColumn = new TableColumn<>("Interpret");
        interpretColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().song.getInterpret()));
        songTable.getColumns().add(interpretColumn);

        TableColumn<TableSong, Integer> songLengthColumn = new TableColumn<>("Length [bars]");
        songLengthColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().song.getLastBeat().getBarNr()));
        songTable.getColumns().add(songLengthColumn);

        TableColumn<TableSong, Integer> tempoColumn = new TableColumn<>("Tempo");
        tempoColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().song.getTempo()));
        songTable.getColumns().add(tempoColumn);


        songTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        songTable.setEditable(false);
        songTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        songTable.prefWidthProperty().bind(tableContainer.widthProperty());

        setNrColumn.setMinWidth(50);
        setNrColumn.setMaxWidth(50);
        positionColumn.setMinWidth(60);
        positionColumn.setMaxWidth(60);

        interpretColumn.setMinWidth(120);
        interpretColumn.setMaxWidth(120);
        songLengthColumn.setMinWidth(100);
        songLengthColumn.setMaxWidth(100);
        tempoColumn.setMinWidth(50);
        tempoColumn.setMaxWidth(50);


        setNrColumn.setReorderable(false);
        positionColumn.setReorderable(false);
        nameColumn.setReorderable(false);
        interpretColumn.setReorderable(false);
        songLengthColumn.setReorderable(false);
        tempoColumn.setReorderable(false);
        setNrColumn.setReorderable(false);

        AnchorPane.setTopAnchor(songTable, 0.0);
        AnchorPane.setLeftAnchor(songTable, 0.0);
        AnchorPane.setBottomAnchor(songTable, 0.0);
        AnchorPane.setRightAnchor(songTable, 0.0);
    }

    private ObservableList<TableSong> generateSongPosition(Gig gig) {
        ObservableList<TableSong> tableSongs = FXCollections.observableArrayList();
        for (int i = 0; i < gig.getSets().size(); i++) {
            SetList set = gig.getSets().get(i);
            for (int j = 0; j < set.getSongs().size(); j++) {
                Song song = set.getSongs().get(j);
                tableSongs.add(new TableSong(song, new SimpleObjectProperty<>(j + 1), new SimpleObjectProperty<>(i + 1)));
            }
        }
        return tableSongs;
    }

    private class TableSong {
        public Song song;
        public ObservableValue<Integer> position; // the position of the song in the set
        public ObservableValue<Integer> setNr;    // the position of the set in the gig

        /**
         * @param song     the song revered to
         * @param position the position of the song in the set
         * @param setNr    the position of the set in the gig
         */
        public TableSong(Song song, ObservableValue<Integer> position, ObservableValue<Integer> setNr) {
            this.song = song;
            this.position = position;
            this.setNr = setNr;
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

    public void selectionSelectPress() {
        selection(3);
    }

    public void selectionBackPress() {
        selection(4);
    }

    private void selection(int action) {
        if (tableContainer.getChildren().isEmpty()) {
            // there is no table displayed currently
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
                        scrollIfNeeded(gigTable);
                    }

                } else if (action == 2) {
                    // right action
                    if (currentSelectedIndex < gigTable.getItems().size() - 1) {
                        // it is not the last element which is currently selected
                        gigTable.getSelectionModel().clearAndSelect(currentSelectedIndex + 1);
                        scrollIfNeeded(gigTable);
                    }

                } else if (action == 3) {
                    // press action
                    if (gigTable.getSelectionModel().isEmpty()) {
                        // no list item selected
                        break;
                    }
                    currentGig = gigTable.getSelectionModel().getSelectedItem();
                    songTable.setItems(generateSongPosition(currentGig));
                    songTable.getSelectionModel().clearAndSelect(0);
                    showTable(songTable);
                }
                break;
            }
            case SONG: {
                int currentSelectedIndex = songTable.getSelectionModel().getSelectedIndex();
                if (action == 1) {
                    // left action
                    if (currentSelectedIndex > 0) {
                        // it is not the last element which is currently selected
                        songTable.getSelectionModel().clearAndSelect(currentSelectedIndex - 1);
                        scrollIfNeeded(songTable);
                    }

                } else if (action == 2) {
                    // right action
                    if (currentSelectedIndex < songTable.getItems().size() - 1) {
                        // it is not the last element which is currently selected
                        songTable.getSelectionModel().clearAndSelect(currentSelectedIndex + 1);
                        scrollIfNeeded(songTable);
                    }

                } else if (action == 3) {
                    // press action
                    if (songTable.getSelectionModel().isEmpty()) {
                        // no list item selected
                        break;
                    }
                    PlayLights.getInstance().loadSong(songTable.getSelectionModel().getSelectedItem().song,
                            currentGig, songTable.getSelectionModel().getSelectedIndex());
                } else if (action == 4) {
                    showTable(gigTable);
                }
                break;
            }
        }
    }

    private void showTable(TableView table) {
        Platform.runLater(() -> {
            if (tableContainer.getChildren().isEmpty()) {
                tableContainer.getChildren().add(table);
            } else {
                tableContainer.getChildren().set(0, table);
            }
            if (table == gigTable) {
                viewMode = VIEW_MODES.GIG;
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FOLDER_LED_ADDRESS, true);
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FILE_LED_ADDRESS, false);
            } else if (table == songTable) {
                viewMode = VIEW_MODES.SONG;
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FOLDER_LED_ADDRESS, false);
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FILE_LED_ADDRESS, true);
            }
        });
    }

    private void scrollIfNeeded(TableView<?> t) {
        Platform.runLater(() -> {
            int selected = t.getSelectionModel().getSelectedIndex();
            int first = 0;
            int last = 0;
            try {
                TableViewSkin<?> ts = (TableViewSkin<?>) t.getSkin();
                VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(1);

                first = vf.getFirstVisibleCell().getIndex();

                last = vf.getLastVisibleCell().getIndex();
            } catch (Exception ex) {
                new Exception("Error while scroll calculation", ex).printStackTrace();
                // if exception occurs just scroll with naive algorithm (selected is on top of viewpoint)
                t.scrollTo(selected);
                return;
            }
            if (selected > last) {
                // need to scroll down
                if (first + 3 <= selected) {
                    t.scrollTo(first + 3);
                } else {
                    t.scrollTo(selected);
                }
            } else if (selected < first) {
                if (selected - 3 >= 0) {
                    t.scrollTo(selected - 3);
                } else {
                    t.scrollTo(0);
                }
                //need to scroll up
            }
        });

    }

    public static SongCenterController getInstance() {
        return songCenterController;
    }

    public ObservableList<Gig> getGigs() {
        return gigs;
    }
}
