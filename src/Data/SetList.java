package Data;

import java.util.ArrayList;

public class SetList {

    private ArrayList<Song> songs;
    private String name;

    public SetList(String name) {
        this.name = name;
        songs = new ArrayList<>(10);
    }

    public SetList() {
        this("UnnamedSet");
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
