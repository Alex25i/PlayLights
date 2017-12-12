package Logic;

public class LiveTimeCode {

    private Long reverenceTime;
    private BeatStamp reverencePosition;

    private int beatsPerBar;
    private int tempo = -1; // timeCodeTempo in bpm

    public void start() {
        if (tempo < 0) {
            throw new IllegalStateException("The tempo is not set or incorrect ");
        }
        reverenceTime = System.currentTimeMillis();
    }

    public void stop() {
        reverenceTime = null;
    }

    public boolean isStarted() {
        return reverenceTime != null;
    }

    public void syncNow(BeatStamp beatStamp) {
        reverenceTime = System.currentTimeMillis();
        reverencePosition = beatStamp;
    }

    public void runOnTime(Runnable runThis, BeatStamp runAtBeat) {
        long sleepTime = calculateTimeToReach(runAtBeat);
        if (sleepTime < 0) {
            // run time is in past
            return;
        }
        if (!isStarted()) {
            new IllegalStateException("Can't trigger a code on a not running beat code").printStackTrace();
            System.err.println("Requested run time:\nBar " + runAtBeat.barNr + " Beat: " + runAtBeat.beatNr);
        }

        Runnable waitRunnable = () -> {
            try {
                Thread.sleep(sleepTime);
                runThis.run();
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (calculateTimeToReach(runAtBeat) > 0) {
                    runOnTime(runThis, runAtBeat);
                }
            }
        };
        Thread a = new Thread(waitRunnable);
        a.start();
    }

    private long calculateTimeToReach(BeatStamp beatStamp) {
        // beats between the beat/time Sync and given beat stamp
        int deltaBeats = (beatStamp.barNr * beatsPerBar + beatStamp.beatNr) - (reverencePosition.barNr * beatsPerBar + reverencePosition.beatNr);
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

    public class BeatStamp {
        private int barNr;
        private int beatNr;

        public BeatStamp(int barNr, int beatNr) {
            this.barNr = barNr;
            this.beatNr = beatNr;
        }
    }
}
