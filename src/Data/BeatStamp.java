package Data;

import Logic.PlayLights;

/**
 * represents a position in a {@link Song}
 * the first bar of a {@link Song} is 1, the first Beat of a bar is 1 as well.
 */
public class BeatStamp {
    /**
     * represents the first beat of a {@link Song}
     */
    public static final BeatStamp FIRST_BEAT = new BeatStamp(1, 1);

    private int barNr;
    private int beatNr;

    public BeatStamp(int barNr, int beatNr) {
        if ((barNr < 1 || beatNr < 1) && PlayLights.verbose) {
            new IllegalArgumentException("Shouldn't create a BeatStamp with the barNr or beatNr smaller than 1: " +
                    "\"barNr = [\" + barNr + \"], beatNr = [\" + beatNr + \"]\"").printStackTrace();
        }
        this.barNr = barNr;
        this.beatNr = beatNr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeatStamp beatStamp = (BeatStamp) o;
        return barNr == beatStamp.barNr && beatNr == beatStamp.beatNr;
    }

    public int getBarNr() {
        return barNr;
    }

    public void setBarNr(int barNr) {
        this.barNr = barNr;
    }

    public int getBeatNr() {
        return beatNr;
    }

    public void setBeatNr(int beatNr) {
        this.beatNr = beatNr;
    }
}
