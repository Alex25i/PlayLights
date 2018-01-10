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
                new BeatStamp(22, 1), MixTrackController.PAD.PAD_0X0);

        Song.PadAction padAction;
        Song.UserEvent userEvent;

        // Pad Actions
        padAction = hyrm.addPadAction(MixTrackController.PAD.PAD_0X0, new Runnable() {
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
        BeatStamp beatStamp = new BeatStamp(14, 1);
        MixTrackController.PAD triiggerPad = MixTrackController.PAD.PAD_0X0;
        userEvent = hyrm.addUserEvent("Intro", beatStamp, triiggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);

        userEvent = hyrm.addUserEvent("Prechorus", new BeatStamp(20, 1), MixTrackController.PAD.PAD_0X0, new Runnable() {
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
