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
                new BeatStamp(50, 1), MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON,
                2, 0, MidiOrganizer.VELOCITY_FULL), MixTrackController.PAD.PAD_0X1);

        MixTrackController.PAD triggerPad;
        Song.UserEvent userEvent;
        Song.PadAction padAction;
        BeatStamp beatStamp;

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X1;
        padAction = hyrm.addPadAction(triggerPad, 0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer mo = PlayLights.getPlayLights().getMidiOrganizer();
                mo.sendMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 1, MidiOrganizer.VELOCITY_FULL,
                        mo.getMpcDeviceConnector());
            }
        });
        // User Events
        beatStamp = new BeatStamp(13, 1);

        userEvent = hyrm.addUserEvent("Intro", beatStamp, triggerPad, new Runnable() {
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

                PlayLights.getPlayLights().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, 4, 1,
                        MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 2, MidiOrganizer.VELOCITY_FULL));
                PlayLights.getPlayLights().getSongPlayer().getTriggerJobs().schedulePeriodicJop(4, 8, 0.5,
                        MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 3, MidiOrganizer.VELOCITY_FULL));
            }
        });

        // User Events
        beatStamp = new BeatStamp(19, 1);
        userEvent = hyrm.addUserEvent("PreChorus", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_2X1;
        padAction = hyrm.addPadAction(triggerPad, 0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer mo = PlayLights.getPlayLights().getMidiOrganizer();
                mo.sendMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 4, MidiOrganizer.VELOCITY_FULL,
                        mo.getMpcDeviceConnector());
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

    public Gig createGig() {
        Gig gig = new Gig();
        gigList.add(gig);
        return gig;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Gig> getGigList() {
        return gigList;
    }
}
