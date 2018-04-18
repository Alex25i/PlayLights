package Data;

import GUI.SongCenterController;
import Logic.PlayLights;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

import java.time.LocalDate;
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
                MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
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

                PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, 4, 1,
                        MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 2, MidiOrganizer.VELOCITY_FULL));
                PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(4, 8, 0.5,
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
                MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
                PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, Integer.MAX_VALUE,
                        2, MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 4,
                                MidiOrganizer.VELOCITY_FULL));
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


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X0;
        padAction = hyrm.addPadAction(triggerPad, 2, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement
                // Example
                MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
                PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, 2,
                        2, MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 5,
                                MidiOrganizer.VELOCITY_FULL));
                PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(2, 1,
                        2, MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE_ON, 2, 6,
                                MidiOrganizer.VELOCITY_FULL));
            }
        });

        // User Events
        beatStamp = new BeatStamp(29, 1);
        userEvent = hyrm.addUserEvent("Chorus", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);
        beatStamp = new BeatStamp(30, 1);
        userEvent = hyrm.addUserEvent("Chorus", beatStamp, triggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });
        padAction.addUserEvent(userEvent);


        return hyrm;
    }

    /**
     * creates an empty gig which can be filled with data afterwards
     *
     * @param location the name location of the gig or null if you don't want to specify
     * @param date     the date of the gig or null if you don't want to specify
     * @return the created gig
     */
    public Gig createGig(String location, LocalDate date) {
        Gig gig = new Gig();
        if (location != null) {
            gig.setLocation(location);
        }
        if (date != null) {
            gig.setDate(date);
        }

        gigList.add(gig);
        if (SongCenterController.getInstance() != null) {
            SongCenterController.getInstance().getGigs().add(gig);
        }
        return gig;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Gig> getGigList() {
        return gigList;
    }
}
