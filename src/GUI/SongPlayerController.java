package GUI;

import Data.BeatStamp;
import Data.Song;
import Logic.SongPlayer;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
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

    @FXML
    public SubScene mainScene;

    @FXML
    private Label songNameLb;

    @FXML
    private Label songNrLb;

    @FXML
    private Label songLengthLb;

    @FXML
    private Label nextUserActionLb;

    private Group songGrid;


    @FXML
    public void initialize() {
        songPlayerController = this;
        songGrid = new Group();
    }

    public void prepare(SongPlayer songPlayer, Runnable songEnd) {
        player = songPlayer;

        drawSongGrid();
        drawUserEventMarker();
        drawStaticOverlay();
        fillSongDescription();

        prepareAnimation(songEnd);
    }

    private void prepareAnimation(Runnable songEnd) {
        songGridAnimation = new AnimationTimer() {
            final int songEndPos = (int) (-player.getCurrentSong().calcTotalBeatCount() * zoomFactor * 20 - 35);

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

        Line positionIndicator = new Line(midLinePositionX, 0, midLinePositionX, 200);
        positionIndicator.setStroke(Color.RED);
        positionIndicator.setStrokeWidth(2);
        rootPane.getChildren().add(positionIndicator);

        Line gridBottom = new Line(0, 200, rootPane.getWidth(), 200);
        gridBottom.setStroke(new Color(.5, .5, .5, 1));
        gridBottom.setStrokeWidth(2);
        rootPane.getChildren().add(gridBottom);

    }

    private void drawSongGrid() {
        final int xStartOffset = (int) rootPane.getWidth() / 2;

        for (int beat = 0; beat <= player.getCurrentSong().calcTotalBeatCount(); beat++) {
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
            Rectangle markerSign = new Rectangle();
            markerSign.setFill(Color.FORESTGREEN);
            markerSign.setStroke(Color.FORESTGREEN);
            markerSign.setX(pxPos - 25.5);
            markerSign.setY(24.5);
            markerSign.setWidth(26);
            if (userEvent.getLabel().length() == 1) {
                // user event is from the bottom pad line (name has only letters)
                markerSign.setHeight(25);
            } else {
                // user event is from the top pad line (name has letter and number eg. A1)
                markerSign.setHeight(50);
            }
            songGrid.getChildren().add(markerSign);

            Text markerLetter;
            if (userEvent.getLabel().length() > 0) {
                markerLetter = new Text(userEvent.getLabel().substring(0, 1));
            } else {
                markerLetter = new Text("?");
            }
            //markerLetter.setScaleX(1.5);
            //markerLetter.setScaleY(1.5);

            markerLetter.setX(pxPos - 21);
            markerLetter.setY(45);

            markerLetter.setStyle("-fx-font: 24 arial;");
            markerLetter.setStroke(Color.WHITE);
            markerLetter.setFill(Color.WHITE);
            markerLetter.setStrokeType(StrokeType.INSIDE);
            songGrid.getChildren().add(markerLetter);

            if (userEvent.getLabel().length() == 2) {
                Text markerLetterNumber = new Text(userEvent.getLabel().substring(1, 2));
                markerLetterNumber.setStyle("-fx-font: 24 arial;");
                markerLetterNumber.setStroke(Color.WHITE);
                markerLetterNumber.setFill(Color.WHITE);
                markerLetterNumber.setStrokeType(StrokeType.INSIDE);
                markerLetterNumber.setX(pxPos - 19);
                markerLetterNumber.setY(70);

                songGrid.getChildren().add(markerLetterNumber);
            }

        }
    }

    private void fillSongDescription() {
        songNameLb.setText(player.getCurrentSong().getInterpret() + " - " + player.getCurrentSong().getName());
        songNrLb.setText(String.valueOf(player.getSongPos() + 1));
        songLengthLb.setText(player.getCurrentSong().getLastBeat().getBarNr() + " bars");
        setNextUserEvent(player.getCurrentSong().calcNextUserEvent(BeatStamp.FIRST_BEAT));
    }

    public void setNextUserEvent(Song.UserEvent nextUserEvent) {
        Platform.runLater(() -> {
            if (nextUserEvent != null) {
                nextUserActionLb.setText(nextUserEvent.getLabel() + " at Bar " + nextUserEvent.getEventTime().getBarNr()
                        + " Beat " + nextUserEvent.getEventTime().getBeatNr());
            } else {
                nextUserActionLb.setText("-none-");
            }
        });
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

    public static SongPlayerController getInstance() {
        return songPlayerController;
    }

    public boolean animationIsRunning() {
        return animationRunning;
    }
}
