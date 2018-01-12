package Data;

import Logic.PlayLights;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private List<Song> songList;
    private List<Gig> gigList;

    public Library() {
        songList = new ArrayList<>();
        gigList = new ArrayList<>();
        createSongs();
    }

    private void createSongs() {
        songList.add(createHowYouRemindMe());
    }

    private Song createHowYouRemindMe() {
        Song hyrm = new Song("How you remind me", "Nickelback", 172, 4,
                new BeatStamp(22, 1), MixTrackController.PAD.PAD_0X1);

        Song.PadAction padAction;
        Song.UserEvent userEvent;

        // Pad Action
        MixTrackController.PAD triggerPad = MixTrackController.PAD.PAD_0X1;
        padAction = hyrm.addPadAction(triggerPad, 0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer midiOrganizer = PlayLights.getPlayLights().getMidiOrganizer();
                midiOrganizer.sendMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE, 2, 1,
                        MidiOrganizer.VELOCITY_FULL, midiOrganizer.getMpcDeviceConnector());
            }
        });

        // User Events
        BeatStamp beatStamp = new BeatStamp(13, 1);
        userEvent = hyrm.addUserEvent("Intro", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X0;
        padAction = hyrm.addPadAction(triggerPad, 0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer midiOrganizer = PlayLights.getPlayLights().getMidiOrganizer();
                midiOrganizer.sendMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE, 2, 2,
                        MidiOrganizer.VELOCITY_FULL, midiOrganizer.getMpcDeviceConnector());
            }
        });

        // User Events
        beatStamp = new BeatStamp(19, 1);
        userEvent = hyrm.addUserEvent("Vers", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_1X1;
        padAction = hyrm.addPadAction(triggerPad, 0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer midiOrganizer = PlayLights.getPlayLights().getMidiOrganizer();
                midiOrganizer.sendMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE, 2, 2,
                        MidiOrganizer.VELOCITY_FULL, midiOrganizer.getMpcDeviceConnector());
            }
        });

        // User Events
        beatStamp = new BeatStamp(21, 1);
        userEvent = hyrm.addUserEvent("Chorus", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);

        return hyrm;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Gig> getGigList() {
        return gigList;
    }
}
