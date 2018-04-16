package Logic.tempo;

import java.util.ArrayList;
import java.util.Comparator;

public class TempoRecognition {

    private static final double ALLOWED_DEVIATION_FACTOR = 0.4;
    private ArrayList<Long> tapTimings;
    private int expectedTempo;

    public TempoRecognition(int expectedTempo) {
        tapTimings = new ArrayList<>();
        this.expectedTempo = expectedTempo;
    }

    public void addTap(long tapTime) {
        tapTimings.add(tapTime);
    }

    private ArrayList<Long> calculateDeltaBetweenTaps() {
        ArrayList<Long> deltaTaps = new ArrayList<>();
        if (tapTimings.size() < 4) {
            new IllegalArgumentException("A minimum of 4 taps is required to recognise a tempo").printStackTrace();
        }
        for (int i = 1; i < tapTimings.size(); i++) {
            long time = tapTimings.get(i) - tapTimings.get(i - 1);
            if (time > 0) {
                deltaTaps.add(time);
            }
        }

        return deltaTaps;
    }

    /**
     * validate the calculated timings between the taps.
     * This ensures that there don't is
     *
     * @param deltaTaps
     * @return
     */
    private ArrayList<Long> validateDeltas(ArrayList<Long> deltaTaps) {
        ArrayList<Long> deltaTapsValidated = new ArrayList<>();
        final int expectedMillsPerBeat = (int) (1000 / ((double) expectedTempo / 60));
        for (Long deltaTap : deltaTaps) {
            double actualDeviationFactor = Math.abs(expectedMillsPerBeat - deltaTap) / (double) expectedMillsPerBeat;
            if (actualDeviationFactor <= ALLOWED_DEVIATION_FACTOR) {
                deltaTapsValidated.add(deltaTap);
            }
        }
        return deltaTapsValidated;
    }

    /**
     * calculate tge median tempo of the given tempo list
     *
     * @param deltaTapsValidated the tempo list from which the median should be determined
     * @return the calculated median, -1 if there is not enough data
     */
    private int calcMedianTempo(ArrayList<Long> deltaTapsValidated) {
        if (deltaTapsValidated.size() < 1) {
            new IllegalStateException("There is less then 1 validated taped delta tempo").printStackTrace();
            return -1;
        }
        deltaTapsValidated.sort(Comparator.naturalOrder());
        double median;
        if (deltaTapsValidated.size() % 2 == 0) {
            median = ((double) deltaTapsValidated.get(deltaTapsValidated.size() / 2) + (double) deltaTapsValidated.get(deltaTapsValidated.size() / 2 - 1)) / 2;
        } else {
            median = (double) deltaTapsValidated.get(deltaTapsValidated.size() / 2);
        }
        return (int) median;
    }
}
