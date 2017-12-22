package Data;

import Midi.MidiOrganizer;
import Midi.MixTrackController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Song {

    private String name;
    private String interpret;
    private int tempo;
    private int beatsPerBar;
    private List<UserEvent> userEvents;
    private Map<MixTrackController.PAD, PadAction> padActions;

    public Song(String name, String interpret, int tempo, int beatsPerBar) {
        this.name = name;
        this.interpret = interpret;
        this.tempo = tempo;
        this.userEvents = new ArrayList<>();
        this.beatsPerBar = beatsPerBar;
        padActions = new HashMap<>(16);
    }

    public void addUserEvent(String name, BeatStamp eventTime, MixTrackController.PAD triggerPad, Runnable eventAction) {
        userEvents.add(new UserEvent(name, eventTime, triggerPad, eventAction));
    }

    public void addPadAction(MixTrackController.PAD pad, Runnable action) {
        if (MidiOrganizer.verbose && padActions.containsKey(pad)) {
            new IllegalStateException("There is already an action registered for the pad " + pad.toString()).printStackTrace();
        }
        padActions.put(pad, new PadAction(pad, action));
    }

    private UserEvent getClosestEventOfPad(MixTrackController.PAD pad, BeatStamp time) {
        if (time == null) {
            new NullPointerException("Can't calculate the closest Event to a null BeatStamp reverence").printStackTrace();
            return null;
        }
        if (!padActions.containsKey(pad)) {
            new NullPointerException("No user events registered for given event").printStackTrace();
            return null;
        }

        UserEvent closestEventOfPad = null;
        int closesDistance = Integer.MAX_VALUE;
        for (UserEvent userEvent : padActions.get(pad).userEvents) {
            if (getBeatDistance(time, userEvent.eventTime) < closesDistance) {
                closesDistance = getBeatDistance(time, userEvent.eventTime);
                closestEventOfPad = userEvent;
            }
        }
        return closestEventOfPad;
    }

    /**
     * calculates the time difference between two @{@link BeatStamp}s in beats
     *
     * @param beatStamp1 first @{@link BeatStamp} for delta calculation
     * @param beatStamp2 second @{@link BeatStamp} for delta calculation
     * @return the amount of beats between the two given @{@link BeatStamp}s
     */
    private int getBeatDistance(BeatStamp beatStamp1, BeatStamp beatStamp2) {
        if (beatStamp1 == null || beatStamp2 == null) {
            new NullPointerException("Beat distance calculation on null object reverence").printStackTrace();
            return -1;
        }
        return Math.abs((beatStamp1.getBarNr() * beatsPerBar + beatStamp1.getBeatNr())
                - (beatStamp2.getBarNr() * beatsPerBar + beatStamp2.getBeatNr()));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    /**
     * a user event is one time event in a song where the user as to press a @{@link Midi.MixTrackController.PAD} in
     * order to trigger a @{@link PadAction}
     */
    private class UserEvent {
        private String name;
        private BeatStamp eventTime;
        private MixTrackController.PAD triggerPad;
        private Runnable eventAction;

        private UserEvent(String name, BeatStamp eventTime, MixTrackController.PAD triggerPad, Runnable eventAction) {
            this.name = name;
            this.eventTime = eventTime;
            this.triggerPad = triggerPad;
            this.eventAction = eventAction;
        }
    }

    /**
     * a pad action is the action which occurs when the user pressed a @{@link Midi.MixTrackController.PAD}
     * it defined the action which should be triggered by pressing the pad
     */
    private class PadAction {
        private MixTrackController.PAD triggerPad;
        private ArrayList<UserEvent> userEvents;
        private Runnable action;

        private PadAction(MixTrackController.PAD triggerPad, Runnable action) {
            this.triggerPad = triggerPad;
            this.action = action;
            userEvents = new ArrayList<>();
        }

        private boolean isActionOfEvent(UserEvent userEvent) {
            return userEvents.contains(userEvent);
        }
    }


}
