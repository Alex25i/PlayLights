package Data;

import java.util.ArrayList;

public class Library {

    private static ArrayList<Song> songList;
    private static ArrayList<Gig> gigList;

    public static ArrayList<Song> getSongList() {
        return songList;
    }

    public static void setSongList(ArrayList<Song> songList) {
        Library.songList = songList;
    }

    public static ArrayList<Gig> getGigList() {
        return gigList;
    }

    public static void setGigList(ArrayList<Gig> gigList) {
        Library.gigList = gigList;
    }
}
