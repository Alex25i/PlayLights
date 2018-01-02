package GUI;

import Data.BeatStamp;
import Data.Song;
import Logic.SongPlayer;
import javafx.animation.AnimationTimer;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class SongPlayerController {

    private static SongPlayerController songPlayerController;

    private SongPlayer player;
    private double zoomFactor = 1;

    private TranslateTransition songAnimation;

    @FXML
    Pane rootPane;

    Group songGrid;

    @FXML
    public void initialize() {
        songPlayerController = this;
        songAnimation = new TranslateTransition();
        songGrid = new Group();
    }

    public void prepare(SongPlayer songPlayer) {
        player = songPlayer;

        rootPane.setStyle("-fx-background-color: #000;");
        drawSongGrid();
        drawStaticOverlay();

        songAnimation.setNode(songGrid);
        songAnimation.setInterpolator(Interpolator.LINEAR);
        songAnimation.setFromY(0);
        songAnimation.setByY(0);
        songAnimation.setCycleCount(1);

        animate();
    }

    public void startAnimation() {
        //songAnimation.setFromX(songCanvas.getWidth() / 2);
        //songAnimation.setByX(songCanvas.getWidth());
        int duration = (int) (calcTotalBeatCountFromSong() / (double) player.getTimeCode().getTempo() * 60 * 1000);
        songAnimation.setDuration(Duration.millis(duration)); //TODO: calc correct playback speed
        int xCoordinates = (int) ((calcTotalBeatCountFromSong() - 1) * zoomFactor * 20);
        songAnimation.setFromX(0);
        songAnimation.setByX(-xCoordinates - 50); // a little bit behind last beat
        songAnimation.play();
    }

    public void startAnimation(BeatStamp songPosition) {

    }

    public void stopCanvasAnimation() {

    }


    private void drawStaticOverlay() {
        int midLinePositionX = (int) (rootPane.getWidth() / 2);
        Line line = new Line(midLinePositionX, 0, midLinePositionX, 200);
        line.setStroke(Color.RED);
        line.setStrokeWidth(2);
        rootPane.getChildren().add(line);
    }

    private void drawSongGrid() {
        int xStartOffset = (int) rootPane.getWidth() / 2;

        for (int beat = 0; beat < calcTotalBeatCountFromSong(); beat++) {
            Line gridLine = new Line();
            if (beat % player.getCurrentSong().getBeatsPerBar() == 0) {
                gridLine.setStroke(Color.WHITE);
                gridLine.setStrokeWidth(1.5);
                gridLine.setStartY(0);

                //Bar nr at each beginning of the bar
                int barNr = beat / player.getCurrentSong().getBeatsPerBar() + 1;
                Text barNrLb = new Text(String.valueOf(barNr));
                barNrLb.setStroke(Color.GRAY);
                barNrLb.setX(beat * zoomFactor * 20 + 5 + xStartOffset);
                barNrLb.setY(15);
                songGrid.getChildren().add(barNrLb);
            } else {

                gridLine.setStroke(Color.WHITE);
                gridLine.setStrokeWidth(0.3);
                gridLine.setStartY(25);
            }
            int xCoordinates = (int) (beat * zoomFactor * 20);
            gridLine.setStartX(xCoordinates + xStartOffset);

            gridLine.setEndX(xCoordinates + xStartOffset);
            gridLine.setEndY(200);
            songGrid.getChildren().add(gridLine);

//            Line separationLineHor = new Line(xStartOffset, 25, (calcTotalBeatCountFromSong() - 1) * 20 + xStartOffset, 25);
//            separationLineHor.setStroke(Color.LIGHTGRAY);
//            separationLineHor.setStrokeWidth(0.004);
//            songGrid.getChildren().add(separationLineHor);
        }


        rootPane.getChildren().add(songGrid);
    }

    private void animate() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                System.out.println(now);
                System.out.println(System.currentTimeMillis());
                System.out.println("");
            }
        };
        timer.start();
    }

    private int calcTotalBeatCountFromSong() {
        Song song = player.getCurrentSong();
        return (song.getLastBeat().getBarNr() - 1) * song.getBeatsPerBar() + song.getLastBeat().getBeatNr();
    }


    public static SongPlayerController getSongPlayerController() {
        return songPlayerController;
    }
}
