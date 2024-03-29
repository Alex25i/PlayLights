package Data;

import Logic.PlayLights;
import Midi.MixTrackController;

import javax.sound.midi.ShortMessage;
import java.util.*;

public class Song {
    // static
    public enum ModelComplexity {
        simple, complex
    }

    // dynamic
    private String name;
    private String interpret;
    private int tempo;
    private int beatsPerBar;
    private ModelComplexity complexity;

    private BeatStamp lastBeat;
    private ShortMessage startUpMessage;
    private List<UserEvent> userEvents;
    private Map<MixTrackController.PAD, PadAction[]> padActions;

    /**
     * Pad at which the player will automatically trigger the beginning of the song (if it is loaded)
     * Can be null, if this feature is not desired for the song (manual start required)
     */
    private MixTrackController.PAD autoStartPad;

    public Song(String name, String interpret, int tempo, int beatsPerBar, ModelComplexity complexity, BeatStamp lastBeat, ShortMessage startUpMessage,
                MixTrackController.PAD autoStartPad) {
        this.name = name;
        this.interpret = interpret;
        this.tempo = tempo;
        this.userEvents = new ArrayList<>();
        this.beatsPerBar = beatsPerBar;
        this.complexity = complexity;
        this.startUpMessage = startUpMessage;
        padActions = new HashMap<>(16);
        this.lastBeat = lastBeat;
        this.autoStartPad = autoStartPad;

        //validate parameters
        if (PlayLights.verbose && name.isEmpty() || interpret.isEmpty() || tempo < 20 || tempo > 300
                || beatsPerBar == 0 || lastBeat == null) {
            new IllegalArgumentException("Song initialised incorrectly! Here are the arguments: \n"
                    + "name = [" + name + "], interpret = [" + interpret + "], tempo = [" + tempo
                    + "], beatsPerBar = [" + beatsPerBar + "], lastBeat = ["
                    + lastBeat + "], autoStartPad = [" + autoStartPad + "]").printStackTrace();
        }
    }

    /**
     * calculates the time difference between two @{@link BeatStamp}s in beats
     * <p>
     * BE AWARE: the result is negative, if the first beat is after the second one
     *
     * @param firstBeat  first @{@link BeatStamp} for distance calculation
     * @param secondBeat second @{@link BeatStamp} for distance calculation
     * @return the amount of beats between the two given @{@link BeatStamp}s
     */
    public int calcBeatDistance(BeatStamp firstBeat, BeatStamp secondBeat) {
        if (firstBeat == null || secondBeat == null) {
            if (PlayLights.verbose) {
                new NullPointerException("Beat distance calculation on null object reverence").printStackTrace();
            }
            return 0;
        }
        return (secondBeat.getBarNr() * beatsPerBar + secondBeat.getBeatNr())
                - (firstBeat.getBarNr() * beatsPerBar + firstBeat.getBeatNr());
    }

    /**
     * calculates the next upcoming {@link UserEvent} base on the given {@link BeatStamp}
     *
     * @param revBeat a {@link BeatStamp } as reverence point from which on the next {@link UserEvent} will be calculated
     * @return the calculated {@link UserEvent}
     */
    public UserEvent calcNextUserEvent(BeatStamp revBeat) {
        UserEvent nearestEvent = null;
        int nearestDistance = Integer.MAX_VALUE;
        for (UserEvent userEvent : userEvents) {
            int currentDistance = calcBeatDistance(revBeat, userEvent.getEventTime());
            if (currentDistance > 0 && currentDistance < nearestDistance) {
                nearestEvent = userEvent;
                nearestDistance = currentDistance;
            }
        }
        if (nearestEvent == null) {
            if (PlayLights.verbose) {
                new Exception("No nearest event could be found. This is okay if this is the last user event").printStackTrace();
            }
        }
        return nearestEvent;
    }

    /**
     * @return sum of the beats of the song in total
     */
    public int calcTotalBeatCount() {
        return calcBeatDistance(BeatStamp.FIRST_BEAT, getLastBeat());
    }

    public UserEvent addUserEvent(String label, BeatStamp eventTime, Runnable eventAction) {

        // check for double userEvent creation (at least in this padAction)
        for (UserEvent existingEvent : userEvents) {
            if (existingEvent.getEventTime().equals(eventTime)) {
                if (PlayLights.verbose) {
                    new IllegalArgumentException("It already exists a user event with this beaStamp").printStackTrace();
                }
                return null;
            }
        }
        UserEvent userEvent = new UserEvent(label, eventTime, eventAction);
        userEvents.add(userEvent);
        return userEvent;
    }

    public PadAction addPadAction(MixTrackController.PAD pad, int bank, Runnable action) {
        if (PlayLights.verbose && padActions.get(pad) != null && padActions.get(pad)[bank] != null) {
            new IllegalStateException("There is already an action registered for the pad "
                    + pad.toString() + "at the bank " + bank + ".").printStackTrace();
        }
        PadAction[] padActionsList = padActions.get(pad);

        //if no actions are assigned to this pad yet, create a new list to hold assignments
        if (padActionsList == null) {
            if (MixTrackController.padIsBankRoot(pad)) {
                padActionsList = new PadAction[1];
            } else {
                padActionsList = new PadAction[6];
            }
        }
        if (padActionsList.length < bank + 1 && PlayLights.verbose) {
            new IllegalArgumentException("bank " + bank + " is not available for pad " + pad.toString()).printStackTrace();
            // don't return so the error gets thrown here. There will be one thrown anyway.
        }
        PadAction padAction = new PadAction(pad, bank, action);
        padActionsList[bank] = padAction;
        padActions.put(pad, padActionsList);
        return padAction;
    }

    public UserEvent getClosestEventOfPadAction(PadAction padAction, BeatStamp time) {
        if (time == null) {
            if (PlayLights.verbose) {
                new NullPointerException("Can't calculate the closest Event to a null BeatStamp reverence").printStackTrace();
            }
        }

        if (padAction == null) {
            if (PlayLights.verbose) {
                new NullPointerException("Can't calculate the closest Event to a null padAction reverence").printStackTrace();
            }
        }

        if (padAction.getUserEvents().isEmpty()) {
            if (PlayLights.verbose) {
                new NullPointerException("No user events registered for given padAction: "
                        + "Pad=" + padAction.getTriggerPad() + " Bank=" + padAction.getBank()).printStackTrace();
            }
        }

        UserEvent closestEventOfPad = null;
        int closesDistance = Integer.MAX_VALUE;
        for (UserEvent userEvent : padActions.get(padAction.getTriggerPad())[padAction.getBank()].getUserEvents()) {
            if (Math.abs(calcBeatDistance(time, userEvent.eventTime)) < closesDistance) {
                closesDistance = Math.abs(calcBeatDistance(time, userEvent.eventTime));
                closestEventOfPad = userEvent;
            }
        }
        return closestEventOfPad;
    }

    public List<Song.PadAction> getPadActionsFromBank(int bank) {
        List<Song.PadAction> padActionList = new ArrayList<>();
        Collection<PadAction[]> padActions = this.padActions.values();
        for (PadAction[] padActionArray : padActions) {
            if (padActionArray[0] != null) {
                if (MixTrackController.padIsBankRoot(padActionArray[0].getTriggerPad())) {
                    // triggered pad is root and not empty
                    padActionList.add(padActionArray[0]);
                } else if (padActionArray[bank] != null) {
                    // triggered pad is not root and not empty
                    padActionList.add(padActionArray[bank]);
                }
            } else if (padActionArray[bank] != null) {
                // triggered pad is not root and not empty
                padActionList.add(padActionArray[bank]);
            }
        }
        return padActionList;
    }

    /**
     * a user event is one time event in a song where the user has to press a @{@link Midi.MixTrackController.PAD} in
     * order to trigger a @{@link PadAction}
     */
    public class UserEvent implements Comparable {
        private String label;
        private BeatStamp eventTime;
        private MixTrackController.PAD triggerPad;
        private Runnable eventAction;

        private UserEvent(String label, BeatStamp eventTime, Runnable eventAction) {
            this.label = label;
            this.eventTime = eventTime;
            this.eventAction = eventAction;
        }

        /**
         * Compares this object with the specified object for order.  Returns a
         * negative integer, zero, or a positive integer as this object is less
         * than, equal to, or greater than the specified object.
         *
         * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
         * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
         * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
         * <tt>y.compareTo(x)</tt> throws an exception.)
         *
         * <p>The implementor must also ensure that the relation is transitive:
         * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
         * <tt>x.compareTo(z)&gt;0</tt>.
         *
         * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
         * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
         * all <tt>z</tt>.
         *
         * <p>It is strongly recommended, but <i>not</i> strictly required that
         * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
         * class that implements the <tt>Comparable</tt> interface and violates
         * this condition should clearly indicate this fact.  The recommended
         * language is "Note: this class has a natural ordering that is
         * inconsistent with equals."
         *
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

        public String getLabel() {
            return label;
        }

        public BeatStamp getEventTime() {
            return eventTime;
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
        private int bank;

        private PadAction(MixTrackController.PAD triggerPad, int bank, Runnable action) {
            this.triggerPad = triggerPad;
            this.bank = bank;
            this.action = action;
            userEvents = new ArrayList<>();
        }

        private boolean isActionOfEvent(UserEvent userEvent) {
            return userEvents.contains(userEvent);
        }

        public MixTrackController.PAD getTriggerPad() {
            return triggerPad;
        }

        public List<UserEvent> getUserEvents() {
            return userEvents;
        }

        public void addUserEvent(UserEvent userEvent) {
            if (!userEvents.contains(userEvent)) {
                userEvents.add(userEvent);
            }
        }

        public int getBank() {
            return bank;
        }

        public Runnable getAction() {
            return action;
        }
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

    public ModelComplexity getComplexity() {
        return complexity;
    }

    public BeatStamp getLastBeat() {
        return lastBeat;
    }

    public ShortMessage getStartUpMessage() {
        return startUpMessage;
    }

    public List<UserEvent> getUserEvents() {
        return userEvents;
    }

    public Map<MixTrackController.PAD, PadAction[]> getPadActions() {
        return padActions;
    }

    public MixTrackController.PAD getAutoStartPad() {
        return autoStartPad;
    }

}
