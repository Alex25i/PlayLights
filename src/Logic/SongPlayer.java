package Logic;

import Data.Song;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

import java.util.ArrayList;

public class SongPlayer {
    private Song currentSong;
    private LiveTimeCode timeCode;
    private TempoRecognition tempoRecognition;


    public SongPlayer(Song currentSong) {
        this.currentSong = currentSong;
        timeCode = new LiveTimeCode(currentSong);
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
            System.out.println("Pad" + pad + "has no associated action in this song: " + currentSong.getName());
            return;
        }
        Song.PadAction padAction = currentSong.getPadActions().get(pad);

        if (pad == currentSong.getAutoStartPad() && !timeCode.isStarted()) {
            // create new list with same elements as the user events from the PadAction
            ArrayList<Song.UserEvent> padUserEvents = new ArrayList<>(padAction.getUserEvents());
            padUserEvents.sort(null);
            if (!padUserEvents.isEmpty()) {
                // set the event time of the first event of the starting pad action as the starting time of the time code
                timeCode.start(padUserEvents.get(0).getEventTime());
            } else if (MidiOrganizer.verbose) {
                new IllegalStateException("There are no events int the event list of the starting pad").printStackTrace();
            }
        }

        timeCode.syncNow(currentSong.getClosestEventOfPad(pad, timeCode.calcCurrentBeat()).getEventTime());
        padAction.getAction().run();
    }
}
