package Data;

import Midi.MixTrackController;

import java.util.ArrayList;

public class Song {

    public String name;
    public String interpret;
    public int tempo;
    public ArrayList<UserEvent> userEvents;

    public Song(String name, String interpret, int tempo) {
        this.name = name;
        this.interpret = interpret;
        this.tempo = tempo;
        this.userEvents = new ArrayList<>();
    }

    public void addUserEvent(String name, BeatStamp eventTime, MixTrackController.PAD triggerPad, Runnable eventAction) {
        userEvents.add(new UserEvent(name, eventTime, triggerPad, eventAction));
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

    private static class UserEvent {
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

        protected BeatStamp getEventTime() {
            return eventTime;
        }

        protected MixTrackController.PAD getTriggerPad() {
            return triggerPad;
        }

        protected Runnable getTriggeredCode() {
            return eventAction;
        }


    }
}
