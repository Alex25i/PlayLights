package Logic;

import Data.BeatStamp;
import Logic.tempo.LiveTimeCode;
import Midi.MidiOrganizer;
import Midi.MixTrackController;

import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TriggerJobs {
    private List<Timer> jobs;
    private LiveTimeCode timeCode;
    private MixTrackController.PAD lastPressedPad;

    public TriggerJobs(LiveTimeCode timeCode) {
        this.timeCode = timeCode;
        jobs = new ArrayList<>();
    }

    /**
     * creates and executes a trigger job which periodically sends a midi message th the mpc adapter.
     * the start will be delayed by startDelay beats of the current song
     *
     * @param startDelay  delay in beats after which the job will be started, must not be negative, can also be a fraction of a beat
     * @param iterations  the count the midi message will be triggered, must be 1 or more
     * @param interval    the interval between two iterations in beats, can also be a fraction of a beat
     * @param midiMessage the midi message that will be triggered
     */
    public void schedulePeriodicJop(final double startDelay, final int iterations, final double interval, ShortMessage midiMessage) {
        if (startDelay < 0 || iterations < 1 || interval < 0) {
            if (PlayLights.verbose) {
                new IllegalArgumentException("start delay must be greater than 0 " +
                        "and iterations must be greater than 1" +
                        "and interval must be greater than 0").printStackTrace();
            }
            return;
        }
        Timer timer = new Timer("Trigger job" + jobs.size() + 1);
        TimerTask timerTask = new TimerTask() {
            int iteration = 1;

            @Override
            public void run() {
                if (iteration <= iterations) {
                    MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
                    mo.sendMpcMidiMessage(midiMessage);
                } else {
                    timer.cancel();
                    jobs.remove(timer);
                }
                iteration++;
            }
        };
        final double millsPerBeat = (int) (1000 / ((double) timeCode.getTempo() / 60));
        long timeStartDelay = (long) (startDelay * millsPerBeat);
        long timeInterval = (long) (millsPerBeat * interval);
        timer.schedule(timerTask, timeStartDelay, timeInterval);
        jobs.add(timer);
    }

    /**
     * schedule a Periodic job which is played until it is interrupted explicitly (no iteration counter)
     *
     * @param startBeat   beat nr form a bar to start the job
     * @param interval    the interval between two iterations in beats, can also be a fraction of a beat
     * @param midiMessage the midi message that will be triggered
     */
    //TODO: Only needed if there is a tap sync feature
    public void scheduleInfinitePeriodicJop(final int startBeat, final double interval, ShortMessage midiMessage) {
        BeatStamp beatStamp = timeCode.calcCurrentBeatPos(); // beat pos (last beat played)

        // check if

    }

    public void stopAllTimer() {
        for (Timer job : jobs) {
            job.cancel();
        }
        jobs.clear();
    }
}
