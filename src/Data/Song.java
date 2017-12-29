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

    private BeatStamp lastBeat;
    private List<UserEvent> userEvents;
    private Map<MixTrackController.PAD, PadAction> padActions;
    // pad at which the player will automatically trigger the beginning of the song (if it is loaded)
    // Can be null, if this feature is not desired for the song (manual start required)
    private MixTrackController.PAD autoStartPad;

    public Song(String name, String interpret, int tempo, int beatsPerBar, BeatStamp lastBeat,
                MixTrackController.PAD autoStartPad) {
        this.name = name;
        this.interpret = interpret;
        this.tempo = tempo;
        this.userEvents = new ArrayList<>();
        this.beatsPerBar = beatsPerBar;
        padActions = new HashMap<>(16);
        this.lastBeat = lastBeat;
        this.autoStartPad = autoStartPad;
    }

    public void addUserEvent(String name, BeatStamp eventTime, MixTrackController.PAD triggerPad, Runnable eventAction) {
        userEvents.add(new UserEvent(name, eventTime, triggerPad, eventAction));
    }

    public void addPadAction(MixTrackController.PAD pad, Runnable action) {
        if (MidiOrganizer.verbose && padActions.containsKey(pad)) {
            new IllegalStateException("There is already an action registered for the pad "
                    + pad.toString()).printStackTrace();
        }
        padActions.put(pad, new PadAction(pad, action));
    }

    public UserEvent getClosestEventOfPad(MixTrackController.PAD pad, BeatStamp time) {
        if (time == null) {
            if (MidiOrganizer.verbose) {
                new NullPointerException("Can't calculate the closest Event to a null BeatStamp reverence").printStackTrace();
            }
            return null;
        }
        if (!padActions.containsKey(pad)) {
            if (MidiOrganizer.verbose) {
                new NullPointerException("No user events registered for given event").printStackTrace();
            }
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
    public int getBeatDistance(BeatStamp beatStamp1, BeatStamp beatStamp2) {
        if (beatStamp1 == null || beatStamp2 == null) {
            if (MidiOrganizer.verbose) {
                new NullPointerException("Beat distance calculation on null object reverence").printStackTrace();
            }
            return -1;
        }
        return Math.abs((beatStamp1.getBarNr() * beatsPerBar + beatStamp1.getBeatNr())
                - (beatStamp2.getBarNr() * beatsPerBar + beatStamp2.getBeatNr()));
    }

    public String getName() {
        return name;
    }

    public String getInterpret() {
        return interpret;
    }

    public int getTempo() {
        return tempo;
    }

    public int getBeatsPerBar() {
        return beatsPerBar;
    }

    public BeatStamp getLastBeat() {
        return lastBeat;
    }

    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    public Map<MixTrackController.PAD, PadAction> getPadActions() {
        return padActions;
    }

    public MixTrackController.PAD getAutoStartPad() {
        return autoStartPad;
    }

    /**
     * a user event is one time event in a song where the user as to press a @{@link Midi.MixTrackController.PAD} in
     * order to trigger a @{@link PadAction}
     */
    public class UserEvent implements Comparable {
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

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         * <p>
         * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
         * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
         * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
         * <tt>y.compareTo(x)</tt> throws an exception.)
         * <p>
         * <p>The implementor must also ensure that the relation is transitive:
         * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
         * <tt>x.compareTo(z)&gt;0</tt>.
         * <p>
         * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
         * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
         * all <tt>z</tt>.
         * <p>
         * <p>It is strongly recommended, but <i>not</i> strictly required that
         * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
         * class that implements the <tt>Comparable</tt> interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         * <p>
         * <p>In the foregoing description, the notation
         * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
         * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
         * <tt>0</tt>, or <tt>1</tt> according to whether the value of
         * <i>expression</i> is negative, zero or positive.
         *
         * @param o the object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         * is less than, equal to, or greater than the specified object.
         * @throws NullPointerException if the specified object is null
         * @throws ClassCastException   if the specified object's type prevents it
         *                              from being compared to this object.
         */
        @Override
        public int compareTo(Object o) {
            UserEvent givenUserEvent = ((UserEvent) o);
            int beatsTotalThisObject = eventTime.getBarNr() * beatsPerBar + eventTime.getBeatNr();
            int beatsTotalGivenObject = givenUserEvent.getEventTime().getBarNr() * beatsPerBar
                    + givenUserEvent.getEventTime().getBeatNr();

            return beatsTotalThisObject - beatsTotalGivenObject;
        }

        public String getName() {
            return name;
        }

        public BeatStamp getEventTime() {
            return eventTime;
        }

        public MixTrackController.PAD getTriggerPad() {
            return triggerPad;
        }

        public Runnable getEventAction() {
            return eventAction;
        }
    }

    /**
     * a pad action is the action which occurs when the user pressed a @{@link Midi.MixTrackController.PAD}
     * it defined the action which should be triggered by pressing the pad
     */
    public class PadAction {
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

        public MixTrackController.PAD getTriggerPad() {
            return triggerPad;
        }

        public ArrayList<UserEvent> getUserEvents() {
            return userEvents;
        }

        public Runnable getAction() {
            return action;
        }
    }


}
