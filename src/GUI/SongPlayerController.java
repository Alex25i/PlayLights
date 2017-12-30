package GUI;

import Data.BeatStamp;
import Data.Song;
import Logic.LiveTimeCode;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SongPlayerController {

    private static Dimension2D WINDOW_SIZE_DEFAULT;

    private static SongPlayerController songPlayerController;

    private LiveTimeCode songTimeCode;
    private Song song;
    private int somingFactor = 1;

    private TranslateTransition songAnimation;

    @FXML
    private Pane rootPane;
    @FXML
    private Canvas songCanvas;
    @FXML
    private Canvas staticOverlay;


    static {
        WINDOW_SIZE_DEFAULT = new Dimension2D(800, 600);
    }

    @FXML
    public void initialize() {
        songPlayerController = this;
        songAnimation = new TranslateTransition();
    }

    public void prepare(Song song, LiveTimeCode liveTimeCode) {
        this.song = song;
        this.songTimeCode = liveTimeCode;
        prepareCanvas();


        drawSongCanvasGrid();
        drawStaticCanvas();
    }

    public void startCanvasAnimation() {
        //songAnimation.setFromX(songCanvas.getWidth() / 2);
        //songAnimation.setByX(songCanvas.getWidth());
        //songAnimation.setDuration(Duration.seconds(calcTotalBeatCoutFromSong() / 120 * 60)); //TODO: calc correct playback speed


        songAnimation.setNode(songCanvas);
        songAnimation.setInterpolator(Interpolator.LINEAR);

        songAnimation.setFromX(0);
        songAnimation.setFromY(0);
        songAnimation.setByX(-2000);
        songAnimation.setByY(0);
        songAnimation.setDuration(Duration.seconds(40));
        songAnimation.setCycleCount(Animation.INDEFINITE);
//        songAnimation.setAutoReverse(false);
        songAnimation.play();
    }

    public void startCanvasAnimation(BeatStamp songPosition) {

    }

    public void stopCanvasAnimation() {

    }


    private void drawTestGraphics() {
        GraphicsContext gc = songCanvas.getGraphicsContext2D();

        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);
        gc.setFill(Color.GRAY);
        gc.strokeLine(1.5 * 200 + 10, 0, 1.5 * 200 + 10, 200);
        gc.setLineWidth(1.5);
        for (int i = 0; i <= 6; i++) {
            gc.strokeLine(i * 200 + 10, 0, i * 200 + 10, 200);
        }

        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.seconds(5));
        tt.setNode(songCanvas);
        tt.setInterpolator(Interpolator.LINEAR);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setFromX(0);
        tt.setFromY(0);
        tt.setByX(-600);
        tt.setByY(0);
        tt.play();
    }

    /**
     * prepares the song canvas layout and draws it
     */
    private void prepareCanvas() {
        int canvasSize = calcTotalBeatCoutFromSong() * somingFactor * 20;
        canvasSize = 2000; //TODO: Only for testing Delete afterwards
        songCanvas.setWidth(canvasSize);
        songCanvas.setLayoutY(0);
        songCanvas.setLayoutX(rootPane.getWidth() / 2);

        // TODO: paste static song animation init here
        rootPane.setStyle("-fx-background-color: #444;");
    }

    private void drawStaticCanvas() {
        GraphicsContext gc = staticOverlay.getGraphicsContext2D();

        gc.setLineWidth(3);
        gc.setStroke(Color.RED);

        int midLinePositionX = (int) (staticOverlay.getWidth() / 2);
        gc.strokeLine(midLinePositionX, 0, midLinePositionX, staticOverlay.getHeight());
    }

    private void drawSongCanvasGrid() {
        GraphicsContext gc = songCanvas.getGraphicsContext2D();
        gc.setLineWidth(1);


        for (int beat = 0; beat < calcTotalBeatCoutFromSong(); beat++) {
            if (beat % song.getBeatsPerBar() == 0) {
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(1.5);
            } else {
                gc.setStroke(Color.LIGHTGRAY);
                gc.setLineWidth(0.2);
            }
            gc.strokeLine(beat * somingFactor * 20, 0, beat * somingFactor * 20, songCanvas.getHeight());
        }
        gc.setLineWidth(1.5);
    }

    private int calcTotalBeatCoutFromSong() {
        return (song.getLastBeat().getBarNr() - 1) * song.getBeatsPerBar() + song.getLastBeat().getBeatNr() - 1;
    }


    public static SongPlayerController getSongPlayerController() {
        return songPlayerController;
    }
}
