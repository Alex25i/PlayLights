package Logic;

import Data.BeatStamp;
import Data.Song;
import GUI.SongPlayerController;
import Midi.MixTrackController;

import java.util.ArrayList;

public class SongPlayer {
    private Song currentSong;
    private LiveTimeCode timeCode;
    private TempoRecognition tempoRecognition; // currently not used


    public SongPlayer(Song currentSong) {
        this.currentSong = currentSong;
        timeCode = new LiveTimeCode(currentSong);
        PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().prepareSong(currentSong);

        // TODO: This not the final implementation
        if (SongPlayerController.getSongPlayerController() != null) {

            SongPlayerController.getSongPlayerController().prepare(this, new Runnable() {
                @Override
                public void run() {
                    SongPlayerController.getSongPlayerController().stopAnimation();
                    //TODO: Inform User that song has ended
                }
            });
        } else {
            new IllegalStateException("Can't prepare SongPlayerController: It is not instanced yet. " +
                    "Check your implementation! Is the the controller class set in the fxml file?").printStackTrace();
        }
    }

    /**
     * check if the given pad is the start pad of the song. If this is the case and the song is not already running,
     * the song will be started with the first defined user event's beatStamp that is available
     * <p>
     * sync the time code the the closest event action found for this pad
     * <p>
     * run the pad action
     */
    public void padPressed(MixTrackController.PAD pad) {

        if (!currentSong.getPadActions().containsKey(pad) & PlayLights.verbose) {
            System.out.println("Pad" + pad + "has no associated action in this song: " + currentSong.getName());
            return;
        }
        Song.PadAction padAction = currentSong.getPadActions().get(pad);

        if (pad == currentSong.getAutoStartPad() && !timeCode.isRunning() && timeCode.atFirstBeat()) {
            // pad triggers autoStart of the song

            // create new list with same elements as the user events from the PadAction
            ArrayList<Song.UserEvent> padUserEvents = new ArrayList<>(padAction.getUserEvents());
            padUserEvents.sort(null);
            if (!padUserEvents.isEmpty()) {
                // set the event time of the first event of the starting pad action as the starting time of the time code
                timeCode.start(padUserEvents.get(0).getEventTime());
                SongPlayerController.getSongPlayerController().startAnimation();
                // if LED is blinking, stop that first

                PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS);
                PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.PLAY_B_LED_ADDRESS, true);
                padAction.getAction().run();
                return; // Always return here?
            } else if (PlayLights.verbose) {
                new IllegalStateException("There are no events int the event list of the starting pad").printStackTrace();
            }
        }


        //TODO: Maybe implement some security checks before syncing the timeCode
        timeCode.syncNow(currentSong.getClosestEventOfPad(pad, timeCode.calcCurrentBeatPos()).getEventTime());
        padAction.getAction().run();
    }

    public void playPausePressed() {
        if (!timeCode.isRunning()) {
            if (timeCode.atFirstBeat()) {
                timeCode.start(BeatStamp.FIRST_BEAT);
            } else {
                timeCode.start(timeCode.calcCurrentBeatPos());
            }
            SongPlayerController.getSongPlayerController().startAnimation();
            PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS);
            PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.PLAY_B_LED_ADDRESS, true);
        } else {
            timeCode.stop();
            PlayLights.getPlayLights().getMidiOrganizer().getMixTrackController().StartBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS, 1000);
        }
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public LiveTimeCode getTimeCode() {
        return timeCode;
    }

    public TempoRecognition getTempoRecognition() {
        return tempoRecognition;
    }
}
