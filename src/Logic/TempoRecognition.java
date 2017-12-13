package Logic;

import java.util.ArrayList;

public class TempoRecognition {

    private static final double allowedDeviationFactor = 0.4;
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
        if (tapTimings.size() < 3) {
            new IllegalArgumentException("A minimum of 3 taps is required to recognise a tempo").printStackTrace();
        }
        for (int i = 1; i < tapTimings.size(); i++) {
            long time = tapTimings.get(i) - tapTimings.get(i - 1);
            if (time > 0) {
                deltaTaps.add(time);
            }
        }

        return deltaTaps;
    }

    private ArrayList<Long> validateDeltas(ArrayList<Long> deltaTaps) {
        ArrayList<Long> deltaTapsValidated = new ArrayList<>();
        final int expectedMillsPerBeat = (int) (1000 / ((double) expectedTempo / 60));
        for (Long deltaTap : deltaTaps) {
            double actualDeviationFactor = Math.abs(expectedMillsPerBeat - deltaTap) / (double) expectedMillsPerBeat;
            if (actualDeviationFactor <= allowedDeviationFactor) {
                deltaTapsValidated.add(deltaTap);
            }
        }
        return deltaTapsValidated;
    }


}
