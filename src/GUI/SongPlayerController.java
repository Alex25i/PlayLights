package GUI;

import Data.BeatStamp;
import Logic.SongPlayer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class SongPlayerController {

    private static SongPlayerController songPlayerController;

    private SongPlayer player;
    private double zoomFactor = 1;

    private AnimationTimer songGridAnimation;

    @FXML
    Pane rootPane;

    private Group songGrid;
    private Line positionIndicator;


    @FXML
    public void initialize() {
        songPlayerController = this;
        songGrid = new Group();
    }

    public void prepare(SongPlayer songPlayer, Runnable songEnd) {
        player = songPlayer;

        rootPane.setStyle("-fx-background-color: #000;");
        drawSongGrid();
        drawStaticOverlay();

        prepareAnimation(songEnd);
    }

    private void prepareAnimation(Runnable songEnd) {
        songGridAnimation = new AnimationTimer() {
            final int songEndPos = (int) (-player.getCurrentSong().calcTotalBeatCount() * zoomFactor * 20 - 30);

            @Override
            public void handle(long now) {
                final long currentTime = System.currentTimeMillis();

                // time passed in milliseconds since the last time code sync was triggered
                final long timeSinceLastSync = currentTime - player.getTimeCode().getReverenceTime();
                // validate result
                if (timeSinceLastSync <= 0) {
                    new IllegalStateException("TimeCode Sync is in the future. " +
                            "Check your implementation why this seems to be the case!").printStackTrace();
                }

                // time the song plays with the current tempo until the synced BeatStamp at the timeCode is reached.
                final long timeToSyncedBeat = player.getCurrentSong().calcBeatDistance(
                        new BeatStamp(1, 1), player.getTimeCode().getReverencePosition());

                // time passed in milliseconds since the current song started, if the speed were always ath the current rate
                final long theoreticalTimeSinceBeginning = timeToSyncedBeat + timeSinceLastSync;

                final double millsPerBeat = (int) (1000 / ((double) player.getTimeCode().getTempo() / 60));
                int animationPosition = (int) (-theoreticalTimeSinceBeginning / (double) millsPerBeat * zoomFactor * 20);

                if (animationPosition != songGrid.getLayoutX()) {
                    songGrid.setLayoutX(animationPosition);
                }
                // run the given code(songEnd) shortly after the song is finished

                if (songGrid.getLayoutX() <= songEndPos) {
                    //song ended
                    songEnd.run();
                }
            }
        };

    }

    public void startAnimation() {
        songGridAnimation.start();
    }

    public void stopAnimation() {
        songGridAnimation.stop();
    }


    private void drawStaticOverlay() {
        int midLinePositionX = (int) (rootPane.getWidth() / 2);
        positionIndicator = new Line(midLinePositionX, 0, midLinePositionX, 200);
        positionIndicator.setStroke(Color.RED);
        positionIndicator.setStrokeWidth(2);
        rootPane.getChildren().add(positionIndicator);
    }

    private void drawSongGrid() {
        final int xStartOffset = (int) rootPane.getWidth() / 2;

        for (int beat = 0; beat < player.getCurrentSong().calcTotalBeatCount(); beat++) {
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
        }
        rootPane.getChildren().add(songGrid);
    }

    public static SongPlayerController getSongPlayerController() {
        return songPlayerController;
    }
}
