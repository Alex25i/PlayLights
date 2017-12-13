package Data;

import Midi.MixTrackController;

import java.util.ArrayList;

public class Song {

    protected String name;
    protected String interpret;
    protected int tempo;
    protected ArrayList<UserEvent> userEvent;

    public Song(String name, String interpret, int tempo, ArrayList<UserEvent> userEvents) {
        this.name = name;
        this.interpret = interpret;
        this.tempo = tempo;
        this.userEvent = userEvents;
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

    protected abstract static class UserEvent {
        private String name;
        private BeatStamp eventTime;
        private MixTrackController.PAD triggerPad;
        private Runnable eventAction;

        protected UserEvent(String name, BeatStamp eventTime, MixTrackController.PAD triggerPad) {
            this.name = name;
            this.eventTime = eventTime;
            this.triggerPad = triggerPad;
            this.eventAction = defineEventAction();
        }

        protected abstract Runnable defineEventAction();

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
