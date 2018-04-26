package Logic;

import Data.BeatStamp;
import Data.Gig;
import Data.Song;
import GUI.SongPlayerController;
import Logic.tempo.LiveTimeCode;
import Logic.tempo.TempoRecognition;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

import java.util.ArrayList;

public class SongPlayer {
    private Song currentSong;
    private Gig currentGig; // currently not used
    private int songPos; // position of song in the gig. Independent of sets.
    private LiveTimeCode timeCode;
    private TempoRecognition tempoRecognition; // currently not used
    private TriggerJobs triggerJobs;


    public SongPlayer(Song currentSong, Gig currentGig, int songPos) {
        this.currentSong = currentSong;
        timeCode = new LiveTimeCode(currentSong);
        triggerJobs = new TriggerJobs(timeCode);

        this.currentGig = currentGig;
        this.songPos = songPos;

        MidiOrganizer midiOrganizer = PlayLights.getInstance().getMidiOrganizer();
        midiOrganizer.sendMidiMessage(currentSong.getStartUpMessage(), midiOrganizer.getMpcDeviceConnector());


        PlayLights.getInstance().getMidiOrganizer().getMixTrackController().prepareSong(currentSong);

        // TODO: This not the final implementation
        if (SongPlayerController.getInstance() != null) {

            SongPlayerController.getInstance().prepare(this, createSongEndRunnable());
        } else {
            new IllegalStateException("Can't prepare SongPlayerController: It is not instanced yet. " +
                    "Check your implementation! Is the the controller class set in the fxml file?").printStackTrace();
        }
        PlayLights.getInstance().getMidiOrganizer().getMixTrackController().updateActiveBank(MixTrackController.PAD.PAD_0X1, currentSong);
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

        if (!currentSong.getPadActions().containsKey(pad)) {
            if (PlayLights.verbose) {
                System.out.println("Pad" + pad + " has no associated action in this song: " + currentSong.getName());
            }
            return;
        }
        Song.PadAction padAction;
        if (MixTrackController.padIsBankRoot(pad)) {
            padAction = currentSong.getPadActions().get(pad)[0];
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().updateActiveBank(pad, currentSong);
        } else {
            padAction = currentSong.getPadActions().get(pad)[PlayLights.getInstance().getMidiOrganizer().getMixTrackController().getActiveBank()];
        }

        if (padAction == null) {
            if (PlayLights.verbose) {
                System.out.println("Pad" + pad + "has no associated action in the active bank: "
                        + PlayLights.getInstance().getMidiOrganizer().getMixTrackController().getActiveBank());
            }
            return;
        }

        if (pad == currentSong.getAutoStartPad() && !timeCode.isRunning() && timeCode.atFirstBeat()) {
            // pad triggers autoStart of the song

            // create new list with same elements as the user events from the PadAction
            ArrayList<Song.UserEvent> padUserEvents = new ArrayList<>(padAction.getUserEvents());
            padUserEvents.sort(null);
            if (!padUserEvents.isEmpty()) {
                // set the event time of the first event of the starting pad action as the starting time of the time code
                timeCode.start(padUserEvents.get(0).getEventTime());
                SongPlayerController.getInstance().startAnimation();
                // if LED is blinking, stop that first

                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS);
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.PLAY_B_LED_ADDRESS, true);
                padAction.getAction().run();
                return; // Always return here?
            } else if (PlayLights.verbose) {
                new IllegalStateException("There are no events int the event list of the starting pad").printStackTrace();
            }
        }


        //TODO: Maybe implement some security checks before syncing the timeCode
        if (!timeCode.isRunning()) {
            return;
        }
        triggerJobs.stopAllTimer();
        padAction.getAction().run();


        if (timeCode.isRunning() && currentSong.calcBeatDistance(currentSong.getLastBeat(), timeCode.calcCurrentBeatPos()) >= 0) {
            // pad was pressed while the animation already finished (but the time code kept going)
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.CUE_B_LED_ADDRESS);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.CUE_B_LED_ADDRESS, false);
        }

        timeCode.syncNow(currentSong.getClosestEventOfPadAction(padAction, timeCode.calcCurrentBeatPos()).getEventTime());
        SongPlayerController.getInstance().setNextUserEvent(currentSong.calcNextUserEvent(timeCode.calcCurrentBeatPos()));

        if (!SongPlayerController.getInstance().animationIsRunning()) {
            SongPlayerController.getInstance().startAnimation();
        }
    }

    public void playPausePressed() {
        if (currentSong.calcBeatDistance(currentSong.getLastBeat(), timeCode.calcCurrentBeatPos()) >= 0) {
            if (timeCode.isRunning()) {
                //the song is running after the last beat of the song
                timeCode.stop();
                triggerJobs.stopAllTimer();
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().startBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS, 1000);
            } else {
                //the song is stopped after the last beat of the song
                timeCode.start(timeCode.calcCurrentBeatPos());
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS);
                PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.PLAY_B_LED_ADDRESS, true);
            }
            return;
        }

        if (timeCode.isRunning()) {
            timeCode.stop();
            triggerJobs.stopAllTimer();
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().startBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS, 1000);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.CUE_B_LED_ADDRESS, true);
        } else {
            if (timeCode.atFirstBeat()) {
                timeCode.start(BeatStamp.FIRST_BEAT);
            } else {
                timeCode.start(timeCode.calcCurrentBeatPos());
            }
            SongPlayerController.getInstance().startAnimation();
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.PLAY_B_LED_ADDRESS, true);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.CUE_B_LED_ADDRESS, false);
        }
    }

    public void resetPressed() {
        if ((!timeCode.isRunning() && !timeCode.atFirstBeat()) || (timeCode.isRunning()
                && currentSong.calcBeatDistance(currentSong.getLastBeat(), timeCode.calcCurrentBeatPos()) >= 0)) {

            //song is stopped not at the beginning of the song or the song is running after the last beat of the song
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMidiMessage(currentSong.getStartUpMessage(), mo.getMpcDeviceConnector());

            timeCode.reset();
            triggerJobs.stopAllTimer();
            SongPlayerController.getInstance().resetAnimationPosition();
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().stopBlinkLed(MixTrackController.CUE_B_LED_ADDRESS);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().setLedIllumination(MixTrackController.CUE_B_LED_ADDRESS, false);
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().startBlinkLed(MixTrackController.PLAY_B_LED_ADDRESS, 1500);
        }
    }

    public Runnable createSongEndRunnable() {
        return () -> {
            SongPlayerController.getInstance().stopAnimation();
            PlayLights.getInstance().getMidiOrganizer().getMixTrackController().startBlinkLed(MixTrackController.CUE_B_LED_ADDRESS, 1500);
            //TODO: Inform User that song has ended
        };
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public Gig getCurrentGig() {
        return currentGig;
    }

    public int getSongPos() {
        return songPos;
    }

    public LiveTimeCode getTimeCode() {
        return timeCode;
    }

    public TempoRecognition getTempoRecognition() {
        return tempoRecognition;
    }

    public TriggerJobs getTriggerJobs() {
        return triggerJobs;
    }
}
