package Logic;

import Data.BeatStamp;
import Midi.MidiOrganizer;

import java.util.List;

public class LiveTimeCode {

    private Long reverenceTime;
    private BeatStamp reverencePosition;

    private int beatsPerBar;
    private int tempo = -1; // timeCodeTempo in bpm

    private List<Thread> sleepingThreads;

    public LiveTimeCode(int beatsPerBar, int tempo, List<Thread> sleepingThreads) {
        this.reverenceTime = reverenceTime;
        this.reverencePosition = reverencePosition;
        this.beatsPerBar = beatsPerBar;
        this.tempo = tempo;
        this.sleepingThreads = sleepingThreads;
    }


    public void start(BeatStamp beatStamp) {
        if (tempo < 0) {
            throw new IllegalStateException("The tempo is not set or incorrect ");
        }
        syncNow(beatStamp);
        //TODO: Timed-run the Song.userEvents
    }

    public void stop() {
        reverenceTime = null;
    }

    public boolean isStarted() {
        return reverenceTime != null;
    }

    /**
     * @return the position of the @{@link LiveTimeCode as a @{@link BeatStamp}
     */
    public BeatStamp calcCurrentBeat() {
        //TODO: Implement
        return null;
    }

    public void syncNow(BeatStamp beatStamp) {
        reverenceTime = System.currentTimeMillis();
        reverencePosition = beatStamp;
    }

    /**
     * run given @{@link Runnable} at the time of given @{@link BeatStamp}
     *
     * @param runThis
     * @param runAtBeat
     */
    //TODO: Move this method to other class, maybe Player
    //TODO:
    public void runOnTime(Runnable runThis, BeatStamp runAtBeat) {
        long sleepTime = calculateTimeToReach(runAtBeat);
        if (sleepTime < 0) {
            // run time is in past
            return;
        }
        if (!isStarted()) {
            new IllegalStateException("Can't trigger a code on a not running beat code").printStackTrace();
            System.err.println("Requested run time:\nBar " + runAtBeat.getBarNr() + " Beat: " + runAtBeat.getBeatNr());
        }

        Runnable waitRunnable = () -> {
            try {
                Thread.sleep(sleepTime);


                boolean removed = sleepingThreads.remove(Thread.currentThread());
                if (!removed && MidiOrganizer.verbose) {
                    new Exception("Sleeping thread could not got removed from @sleepingThreads\n" +
                            "Check your implementation why this is the case! (Sleeping until @BeatStamp: Bar="
                            + runAtBeat.getBarNr() + " Beat=" + runAtBeat.getBeatNr() + ")").printStackTrace();
                }

                // @runThis could run in the same thread as the waiting was before, so the runnable an be executed in this thread
                runThis.run();
            } catch (InterruptedException e) {
                // thread gets interrupted if the TimeCode got reSynced.
                // So just call the this method recursively after the sync is done.
                e.printStackTrace();
                if (calculateTimeToReach(runAtBeat) > 0) {
                    runOnTime(runThis, runAtBeat);
                }
            }
        };

        Thread waitThread = new Thread(waitRunnable);
        sleepingThreads.add(waitThread);
        waitThread.start();
    }

    /**
     * calculates how long it takes to reach given @{@link BeatStamp}
     *
     * @param beatStamp point in the song the duration to reach is calculated
     * @return the time it takes to reach the @{@link BeatStamp} in Milliseconds
     */
    private long calculateTimeToReach(BeatStamp beatStamp) {
        if (!isStarted()) {
            if (MidiOrganizer.verbose) {
                new IllegalStateException("You can't calculate how long it does take to reach a certain @BeatStamp when the timeCode is not running yet.").printStackTrace();
            }
            return -1;
        }
        // beats between the beat/time Sync and given beat stamp
        int deltaBeats = (beatStamp.getBarNr() * beatsPerBar + beatStamp.getBeatNr()) - (reverencePosition.getBarNr() * beatsPerBar + reverencePosition.getBeatNr());
        if (deltaBeats < 0) {
            // deltaBeats is negative
            return -1;
        }

        final int millsPerBeat = (int) (1000 / ((double) tempo / 60));
        int deltaTime = deltaBeats * millsPerBeat;

        // subtract the the passed time since the beat/time was synced
        deltaTime -= (System.currentTimeMillis() - reverenceTime);

        return deltaTime;
    }

    public Long getReverenceTime() {
        return reverenceTime;
    }

    public void setReverenceTime(Long reverenceTime) {
        this.reverenceTime = reverenceTime;
    }

    public BeatStamp getReverencePosition() {
        return reverencePosition;
    }

    public void setReverencePosition(BeatStamp reverencePosition) {
        this.reverencePosition = reverencePosition;
    }

    public int getBeatsPerBar() {
        return beatsPerBar;
    }

    public void setBeatsPerBar(int beatsPerBar) {
        this.beatsPerBar = beatsPerBar;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        if (tempo <= 0 || tempo > 300) {
            new IllegalArgumentException("Check the tempo. Its unlikely " + tempo).printStackTrace();
        }

        this.tempo = tempo;
    }

}
