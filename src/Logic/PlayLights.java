package Logic;

import Data.Library;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class PlayLights extends Application {
    private static PlayLights playLights;

    private Stage primaryStage;
    private Library library;

    public PlayLights() {
        playLights = this;
        library = new Library();
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

        String resourcePath = "../GUI/SongPlayer.fxml";
        URL location = getClass().getResource(resourcePath);
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Scene scene = new Scene(fxmlLoader.load(), 576, 324);

        primaryStage.setScene(scene);
        // primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        // TODO: Move this to an appropriate location

        SongPlayer songPlayer = new SongPlayer(PlayLights.getPlayLights().getLibrary().getSongList().get(0));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static PlayLights getPlayLights() {
        return playLights;
    }

    public Library getLibrary() {
        return library;
    }

    public static void main(String[] args) {
        new PlayLights();
        launch(args);
    }
}
