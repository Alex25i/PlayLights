package Data;

import Logic.PlayLights;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

public class HowYouRemindMe {

    public static Song createSong() {
        Song hyrm = new Song("How you remind me", "Nickelback", 172, 4,
                new BeatStamp(151, 4), MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON,
                2, 0, MidiOrganizer.VELOCITY_FULL), MixTrackController.PAD.PAD_0X1);

        MixTrackController.PAD triggerPad;
        Song.UserEvent userEvent;
        Song.PadAction padAction;
        BeatStamp beatStamp;

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X1;
        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 1, MidiOrganizer.VELOCITY_FULL);
        });
        // User Events
        beatStamp = new BeatStamp(13, 1);

        userEvent = hyrm.addUserEvent("A", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(45, 1);
        userEvent = hyrm.addUserEvent("A", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(113, 1);
        userEvent = hyrm.addUserEvent("A", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);
        //----------------------------------------


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_1X1;

        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example

            PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, 4, 1,
                    MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 2, MidiOrganizer.VELOCITY_FULL));
            PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(4, 8, 0.5,
                    MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 3, MidiOrganizer.VELOCITY_FULL));
        });

        // User Event
        beatStamp = new BeatStamp(19, 1);
        userEvent = hyrm.addUserEvent("B", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Event
        beatStamp = new BeatStamp(59, 1);
        userEvent = hyrm.addUserEvent("B", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(119, 1);
        userEvent = hyrm.addUserEvent("B", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);
        //----------------------------------------


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_2X1;
        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example
            PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(0, Integer.MAX_VALUE,
                    2, MidiOrganizer.createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 4,
                            MidiOrganizer.VELOCITY_FULL));
        });

        // User Events
        beatStamp = new BeatStamp(21, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(37, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(61, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(77, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(121, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(131, 1);
        userEvent = hyrm.addUserEvent("C", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);
        //----------------------------------------


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X0;
        padAction = hyrm.addPadAction(triggerPad, 2, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 5, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(29, 1);
        userEvent = hyrm.addUserEvent("C1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(30, 1);
        userEvent = hyrm.addUserEvent("C1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(69, 1);
        userEvent = hyrm.addUserEvent("C1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(70, 1);
        userEvent = hyrm.addUserEvent("C1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);
        //----------------------------------------


        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_1X0;
        padAction = hyrm.addPadAction(triggerPad, 2, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 6, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(29, 3);
        userEvent = hyrm.addUserEvent("C2", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);


        beatStamp = new BeatStamp(30, 3);
        userEvent = hyrm.addUserEvent("C2", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        beatStamp = new BeatStamp(69, 3);
        userEvent = hyrm.addUserEvent("C2", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);


        beatStamp = new BeatStamp(70, 3);
        userEvent = hyrm.addUserEvent("C2", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);
        //----------------------------------------

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_2X0;
        padAction = hyrm.addPadAction(triggerPad, 2, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 7, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(31, 1);
        userEvent = hyrm.addUserEvent("C3", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(71, 1);
        userEvent = hyrm.addUserEvent("C3", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);


        //----------------------------------------

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_4X1;
        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 8, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(93, 1);
        userEvent = hyrm.addUserEvent("D", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        //----------------------------------------

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_5X1;
        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 9, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(127, 1);
        userEvent = hyrm.addUserEvent("E", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(129, 3);
        userEvent = hyrm.addUserEvent("E", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(130, 3);
        userEvent = hyrm.addUserEvent("E", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        //----------------------------------------

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_0X0;
        padAction = hyrm.addPadAction(triggerPad, 4, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 10, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(129, 1);
        userEvent = hyrm.addUserEvent("E1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        // User Events
        beatStamp = new BeatStamp(130, 1);
        userEvent = hyrm.addUserEvent("E1", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);

        //----------------------------------------

        // Pad Action
        triggerPad = MixTrackController.PAD.PAD_6X1;
        padAction = hyrm.addPadAction(triggerPad, 0, () -> {
            //TODO: Implement
            // Example
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(MidiOrganizer.MESSAGE_TYPE_NOTE_ON, 2, 11, MidiOrganizer.VELOCITY_FULL);
        });

        // User Events
        beatStamp = new BeatStamp(151, 1);
        userEvent = hyrm.addUserEvent("F", beatStamp, () -> {
            //TODO: Implement -> Blink pad
        });
        padAction.addUserEvent(userEvent);


        return hyrm;
    }
}
