package Data;

public class BeatStamp {
    private int barNr;
    private int beatNr;

    public BeatStamp(int barNr, int beatNr) {
        this.barNr = barNr;
        this.beatNr = beatNr;
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
