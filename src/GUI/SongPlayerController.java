package GUI;

import Data.BeatStamp;
import Data.Song;
import Logic.SongPlayer;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;

import java.util.List;

public class SongPlayerController {

    private static SongPlayerController songPlayerController;

    private SongPlayer player;
    private double zoomFactor = 1;

    private AnimationTimer songGridAnimation;
    private boolean animationRunning;

    @FXML
    private Pane rootPane;

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
        drawUserEventMarker();
        drawStaticOverlay();

        prepareAnimation(songEnd);
    }

    private void prepareAnimation(Runnable songEnd) {
        songGridAnimation = new AnimationTimer() {
            final int songEndPos = (int) (-player.getCurrentSong().calcTotalBeatCount() * zoomFactor * 20 - 30);

            @Override
            public void handle(long now) {
                final long currentTime = System.currentTimeMillis();

                if (player.getTimeCode() == null || !player.getTimeCode().isRunning()) {
                    if (player.getTimeCode().atFirstBeat()) {
                        new IllegalStateException("Can't play the song animation." +
                                "Start a corresponding timeCode instance first!").printStackTrace();
                    } else {
                        // only snap the animation to last beat if the song has not finished yet
                        if (!player.getTimeCode().hasFinished()) {
                            //snap the animation to nearest beat
                            BeatStamp currentBeat = player.getTimeCode().calcCurrentBeatPos();
                            final int currentBeatSum = player.getCurrentSong().calcBeatDistance(BeatStamp.FIRST_BEAT, currentBeat);
                            final int currentPxPos = (int) -(currentBeatSum * zoomFactor * 20);
                            if (currentPxPos != songGrid.getLayoutX()) {
                                songGrid.setLayoutX(currentPxPos);
                            }
                        }
                    }
                    stopAnimation();
                    return;
                }

                // time passed in milliseconds since the last time code sync was triggered
                final long timeSinceLastSync = currentTime - player.getTimeCode().getReverenceTime();
                // validate result
                if (timeSinceLastSync < 0 || timeSinceLastSync > 3600000) {
                    new IllegalStateException("timeSinceLastSync is not in a valid range: timeSinceLastSync = " + timeSinceLastSync
                            + "Check your implementation why this seems to be the case!").printStackTrace();
                    stopAnimation();
                    return;
                }
                final double millsPerBeat = (int) (1000 / ((double) player.getTimeCode().getTempo() / 60));

                // time the song plays with the current tempo until the synced BeatStamp at the timeCode is reached.
                final long timeToSyncedBeat = (long) (player.getCurrentSong().calcBeatDistance(
                        BeatStamp.FIRST_BEAT, player.getTimeCode().getReverencePosition()) * millsPerBeat);

                // time passed in milliseconds since the current song started, if the speed were always ath the current rate
                final long theoreticalTimeSinceBeginning = timeToSyncedBeat + timeSinceLastSync;


                int animationPosition = (int) (-theoreticalTimeSinceBeginning / millsPerBeat * zoomFactor * 20);

                if (animationPosition != songGrid.getLayoutX()) {
                    songGrid.setLayoutX(animationPosition);

                }
                //System.out.println("animationPosition = " + animationPosition);
                // run the given code(songEnd) shortly after the song is finished
                if (songGrid.getLayoutX() <= songEndPos) {
                    //song ended
                    songEnd.run();
                }
            }
        };
        animationRunning = false;
    }

    public void startAnimation() {
        if (animationRunning) {
            return;
        }
        songGridAnimation.start();
        animationRunning = true;
    }

    public void stopAnimation() {
        if (!animationRunning) {
            return;
        }
        songGridAnimation.stop();
        animationRunning = false;
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
            int xCoordinates = (int) (beat * zoomFactor * 20);
            if (beat % player.getCurrentSong().getBeatsPerBar() == 0) {

                // white line, first beat of the bar
                gridLine.setStroke(new Color(.5, .5, .5, 1));
                gridLine.setStrokeWidth(2);
                gridLine.setStartY(0);
                gridLine.setStartX(xCoordinates + xStartOffset);
                gridLine.setEndX(xCoordinates + xStartOffset);

                //Bar nr at each beginning of the bar
                int barNr = beat / player.getCurrentSong().getBeatsPerBar() + 1;
                Text barNrLb = new Text(String.valueOf(barNr));
                barNrLb.setStroke(new Color(.5, .5, .5, 1));
                barNrLb.setX(beat * zoomFactor * 20 + 5 + xStartOffset);
                barNrLb.setY(15);
                songGrid.getChildren().add(barNrLb);
            } else {
                // gray line, beat within a bar
                gridLine.setStroke(Color.WHITE);
                gridLine.setStrokeWidth(0.3);
                gridLine.setStartY(25);
                gridLine.setStartX(xCoordinates + xStartOffset + .5);
                gridLine.setEndX(xCoordinates + xStartOffset + .5);
            }
            gridLine.setEndY(200);
            songGrid.getChildren().add(gridLine);
        }
        rootPane.getChildren().add(songGrid);
    }

    private void drawUserEventMarker() {
        final int xStartOffset = (int) rootPane.getWidth() / 2;
        final List<Song.UserEvent> userEvents = player.getCurrentSong().getUserEvents();
        for (Song.UserEvent userEvent : userEvents) {
            final BeatStamp beatPos = userEvent.getEventTime();
            final int pxPos = (int) (player.getCurrentSong().calcBeatDistance(BeatStamp.FIRST_BEAT, beatPos)
                    * zoomFactor * 20) + xStartOffset;

            //line
            Line markerLine = new Line(pxPos, 40, pxPos, 190);
            markerLine.setStrokeWidth(2);
            markerLine.setStroke(Color.FORESTGREEN);

            songGrid.getChildren().add(markerLine);

            //Triangle at the bottom of the marker
            Polygon markerStand = new Polygon();
            markerStand.getPoints().addAll(pxPos - 12.0, 200.5,
                    pxPos + 12.0, 200.5,
                    (double) pxPos, 185.0);
            markerStand.setStroke(Color.FORESTGREEN);
            markerStand.setFill(Color.FORESTGREEN);
            //markerStand.setStrokeType(StrokeType.OUTSIDE);
            markerLine.setStrokeWidth(2);
            songGrid.getChildren().add(markerStand);

            //Sign of th marker
            Rectangle markerSign = new Rectangle(pxPos - 25.5, 24.5, 26, 25);
            markerSign.setFill(Color.FORESTGREEN);
            markerSign.setStroke(Color.FORESTGREEN);
            songGrid.getChildren().add(markerSign);

            Text markerLetter;
            if (userEvent.getName() != null && userEvent.getName().length() > 0) {
                markerLetter = new Text(userEvent.getName().substring(0, 1));
            } else {
                markerLetter = new Text("?");
            }
            markerLetter.setX(pxPos - 21);
            markerLetter.setY(45);

            //markerLetter.setScaleX(1.5);
            //markerLetter.setScaleY(1.5);

            markerLetter.setStyle("-fx-font: 24 arial;");
            markerLetter.setStroke(Color.WHITE);
            markerLetter.setFill(Color.WHITE);
            markerLetter.setStrokeType(StrokeType.INSIDE);
            songGrid.getChildren().add(markerLetter);
        }
    }

    /**
     * resets the animation position to the first beat of the song
     */
    public void resetAnimationPosition() {
        stopAnimation();
        songGrid.setLayoutX(0);
    }

    public void setBlackBackgroundColor() {

    }

    public static SongPlayerController getSongPlayerController() {
        return songPlayerController;
    }

    public boolean animationIsRunning() {
        return animationRunning;
    }
}
