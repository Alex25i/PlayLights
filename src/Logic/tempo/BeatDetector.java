package Logic.tempo;

import java.util.LinkedList;

public abstract class BeatDetector {

    public enum MODE {SECOND_BEAT, BEAT}

    private LiveTimeCode liveTimeCode;
    private boolean syncBeat;

    private LinkedList<Long> beats;
    private int tempo;

    public BeatDetector(LiveTimeCode liveTimeCode) {
        this.liveTimeCode = liveTimeCode;

        syncBeat = false;
        beats = new LinkedList<>();
    }

    public void beat() {
        if (validateBeat()) {
            beats.add(System.currentTimeMillis());
        }

        // only sync, if sync mode is on and the last sync time is older than the now syncing time
        if (syncBeat && beats.get(beats.size() - 1) > liveTimeCode.getReverenceTime()) {
            liveTimeCode.syncAt(beats.getLast(), liveTimeCode.calcNearestBeatPos(beats.getLast()));
        }
    }

    abstract boolean validateBeat();
}
