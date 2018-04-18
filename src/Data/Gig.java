package Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Gig {


    private List<SetList> sets;
    private String location;
    private LocalDate date;

    public Gig() {
        sets = new ArrayList<>();
        location = "Unknown";
    }

    public SetList createSet() {
        SetList setList = new SetList();
        sets.add(setList);
        return setList;
    }

    public List<SetList> getSets() {
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
