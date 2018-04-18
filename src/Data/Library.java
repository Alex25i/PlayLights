package Data;

import GUI.SongCenterController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Library {

    private List<Song> songList;
    private List<Gig> gigList;

    public Library() {
        songList = new ArrayList<>();
        gigList = new ArrayList<>();
        createSongs();
    }

    private void createSongs() {
        songList.add(HowYouRemindMe.createSong());
    }

    private Song createHowYouRemindMe() {

    }

    /**
     * creates an empty gig which can be filled with data afterwards
     *
     * @param location the name location of the gig or null if you don't want to specify
     * @param date     the date of the gig or null if you don't want to specify
     * @return the created gig
     */
    public Gig createGig(String location, LocalDate date) {
        Gig gig = new Gig();
        if (location != null) {
            gig.setLocation(location);
        }
        if (date != null) {
            gig.setDate(date);
        }

        gigList.add(gig);
        if (SongCenterController.getInstance() != null) {
            SongCenterController.getInstance().getGigs().add(gig);
        }
        return gig;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Gig> getGigList() {
        return gigList;
    }
}
