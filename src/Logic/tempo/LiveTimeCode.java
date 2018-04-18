package Logic.tempo;

import Data.BeatStamp;
import Data.Song;
import Logic.PlayLights;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

public class LiveTimeCode {


    private long reverenceTime = 0;
    private BeatStamp reverencePosition = BeatStamp.FIRST_BEAT;
    private int tempo = -1; // timeCodeTempo in bpm

    private Song currentSong;


    private List<Thread> sleepingThreads;

    public LiveTimeCode(Song currentSong) {
        this.currentSong = currentSong;
        setTempo(currentSong.getTempo());
        this.sleepingThreads = new ArrayList<>();
    }


    /**
     * start the {@link LiveTimeCode} instace at the given {@link BeatStamp}
     *
     * @param beatStamp the position of the {@link Song} the {@link LiveTimeCode} should start at.
     *                  Pass "{@code new BeatStamp(1,1)}" to start at the beginning of th {@link Song}.
     */
    public void start(BeatStamp beatStamp) {
        if (isRunning()) {
            new IllegalStateException("The timecode is already running").printStackTrace();
            return;
        }
        if (tempo < 0 && PlayLights.verbose) {
            throw new IllegalStateException("The tempo is not set or incorrect ");
        }
        syncNow(beatStamp);
        Platform.runLater(() -> PlayLights.getInstance().getPrimaryStage().setAlwaysOnTop(true));

        //TODO: Timed-run the Song.userEvents
    }

    public void stop() {
        reverencePosition = calcCurrentBeatPos();
        reverenceTime = 0;
        for (Thread sleepingThread : sleepingThreads) {
            sleepingThread.interrupt();
        }
        sleepingThreads.clear();
        Platform.runLater(() -> PlayLights.getInstance().getPrimaryStage().setAlwaysOnTop(false));
    }

    public boolean isRunning() {
        return reverenceTime != 0;
    }

    /**
     * @return true if this {@link LiveTimeCode} instance is waiting at the first Beat
     */
    public boolean atFirstBeat() {
        return !isRunning() && calcCurrentBeatPos().equals(BeatStamp.FIRST_BEAT);
    }

    /**
     * determines weather the {@link LiveTimeCode} has finished playing
     *
     * @return true, if the {@link LiveTimeCode} has reached the last beat and has stopped
     */
    public boolean hasFinished() {
        return !isRunning() && currentSong.calcBeatDistance(currentSong.getLastBeat(), calcCurrentBeatPos()) >= 0;
    }

    /**
     * @return the position of the current {@link LiveTimeCode} as a {@link BeatStamp}
     */
    public BeatStamp calcCurrentBeatPos() {
        // should also return a valid result if the timeCode is not running
        if (!isRunning()) {
            return reverencePosition;
        }
        // time passed in milliseconds since the last time code sync was triggered
        final long timeSinceLastSync = System.currentTimeMillis() - reverenceTime;
        // validate result
        if (timeSinceLastSync <= 0) {
            new IllegalStateException("TimeCode Sync is in the future. " +
                    "Check your implementation why this seems to be the case!").printStackTrace();
        }
        final double beatsPerMill = tempo / 60.0 / 1000.0;
        int beatsSinceLastSync = (int) (timeSinceLastSync * beatsPerMill);
        int currentPosBar = reverencePosition.getBarNr() + beatsSinceLastSync / currentSong.getBeatsPerBar();
        int currentPosBeat = reverencePosition.getBeatNr() + beatsSinceLastSync % currentSong.getBeatsPerBar();
        return new BeatStamp(currentPosBar, currentPosBeat);
    }

    public BeatStamp calcNearestBeatPos(long time) {
        //TODO: Implement Method
        return null;
    }

    /**
     * syncs the {@link LiveTimeCode} with the current system time and the given {@link BeatStamp}
     *
     * @param beatStamp song position the {@link LiveTimeCode} gets synced with99
     */
    public void syncNow(BeatStamp beatStamp) {
        reverenceTime = System.currentTimeMillis();
        reverencePosition = beatStamp;
    }

    /**
     * syncs the {@link LiveTimeCode} with the current system time and the given {@link BeatStamp}
     *
     * @param beatStamp song position the {@link LiveTimeCode} gets synced with99
     */
    public void syncAt(long time, BeatStamp beatStamp) {
        reverenceTime = time;
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
        if (!isRunning() && PlayLights.verbose) {
            new IllegalStateException("Can't trigger a code on a not running beat code").printStackTrace();
            System.err.println("Requested run time:\nBar " + runAtBeat.getBarNr() + " Beat: " + runAtBeat.getBeatNr());
        }

        Runnable waitRunnable = () -> {
            try {
                Thread.sleep(sleepTime);


                boolean removed = sleepingThreads.remove(Thread.currentThread());
                if (!removed && PlayLights.verbose) {
                    new Exception("Sleeping thread could not be removed from @sleepingThreads\n" +
                            "Check your implementation why this is the case! (Sleeping until @BeatStamp: Bar="
                            + runAtBeat.getBarNr() + " Beat=" + runAtBeat.getBeatNr() + ")").printStackTrace();
                }

                // runThis could run in the same thread as the waiting was before,
                // therefore the runnable can be executed in this thread
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
     * calculates how long it takes to reach given {@link BeatStamp}
     *
     * @param beatStamp point in the song the duration to reach is calculated
     * @return the time it takes to reach the {@link BeatStamp} in Milliseconds
     */
    private long calculateTimeToReach(BeatStamp beatStamp) {
        if (!isRunning()) {
            if (PlayLights.verbose) {
                new IllegalStateException("You can't calculate how long it does take to reach a certain @BeatStamp when the timeCode is not running yet.").printStackTrace();
            }
            return -1;
        }
        // beats between the beat/time Sync and given beat stamp
        int deltaBeats = (beatStamp.getBarNr() * currentSong.getBeatsPerBar() + beatStamp.getBeatNr())
                - (reverencePosition.getBarNr() * currentSong.getBeatsPerBar() + reverencePosition.getBeatNr());
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

    /**
     * @return the newest time at which the {@link LiveTimeCode} was synced with the the band
     */
    public long getReverenceTime() {
        return reverenceTime;
    }

    public BeatStamp getReverencePosition() {
        return reverencePosition;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        if (tempo <= 0 || tempo > 300) {
            new IllegalArgumentException("Check the tempo. Its unlikely correct" + tempo).printStackTrace();
        }

        this.tempo = tempo;
    }

    /**
     * resets the time code to the beginning of the song
     */
    public void reset() {
        if (isRunning()) stop();
        reverencePosition = BeatStamp.FIRST_BEAT;
    }


}
