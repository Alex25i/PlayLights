package Logic;

import Data.Gig;
import Data.Library;
import Data.SetList;
import Data.Song;
import Midi.MidiOrganizer;
import Midi.MixTrackController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class PlayLights extends Application {
    public static boolean verbose = false;
    private static PlayLights playLights;

    private Stage primaryStage;
    private Library library;
    private SongPlayer songPlayer;
    private MidiOrganizer midiOrganizer;

    public PlayLights() {
        verbose = true;
        playLights = this;
        midiOrganizer = new MidiOrganizer();
        library = new Library();
        midiOrganizer.getMixTrackController().blackoutStartLEDs();
        createTestData();
    }

    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     * <p>
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set. The primary stage will be embedded in
     *                     the browser if the application was launched as an applet.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages and will not be embedded in the browser.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.initStyle(StageStyle.UNDECORATED);
        loadSongCenter();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        //loadSongPlayer(library.getSongList().get(0));
    }

    public void loadSongPlayer(Song song) {
        Platform.runLater(() -> {
            String resourcePath = "../GUI/songPlayer.fxml";
            loadScene(resourcePath);
            songPlayer = new SongPlayer(song);
        });
        getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FOLDER_LED_ADDRESS, false);
        getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.FILE_LED_ADDRESS, false);
    }

    public void loadSongCenter() {
        Platform.runLater(() -> {
            String resourcePath = "../GUI/songCenter.fxml";
            loadScene(resourcePath);
        });
    }

    private void loadScene(String resourcePath) {
        URL location = getClass().getResource(resourcePath);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 576, 324);
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createTestData() {
        Gig gig = library.createGig("Musikschule", LocalDate.of(2018, 3, 11));
        SetList set = gig.createSet();
        set.addSong(library.getSongList().get(0));
        set.addSong(library.getSongList().get(0));
        set.addSong(library.getSongList().get(0));

        gig = library.createGig("Musikschule", LocalDate.of(2018, 3, 11));
        set = gig.createSet();
        set.addSong(library.getSongList().get(0));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static PlayLights getInstance() {
        return playLights;
    }

    public Library getLibrary() {
        return library;
    }

    public SongPlayer getSongPlayer() {
        return songPlayer;
    }

    public MidiOrganizer getMidiOrganizer() {
        return midiOrganizer;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
