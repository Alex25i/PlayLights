package Data;

import java.time.LocalDate;
import java.util.ArrayList;

public class Gig {


    private ArrayList<SetList> sets;
    private String location;
    private LocalDate date;

    public Gig(String location, LocalDate date) {
        this.location = location;
        this.date = date;
        sets = new ArrayList<>(3);
    }

    public Gig(LocalDate date) {
        this("unnamedLocation", date);
    }

    public ArrayList<SetList> getSets() {
        return sets;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
