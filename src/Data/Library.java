package Data;

import Midi.MidiController;
import Midi.MixTrackController;

import java.util.ArrayList;

public class Library {

    private static ArrayList<Song> songList;
    private static ArrayList<Gig> gigList;

    private Song createHowYouRemindMe() {
        Song hyrm = new Song("How you remind me", "Nickelback", 172);

        BeatStamp beatStamp = new BeatStamp(7, 1);
        MixTrackController.PAD triiggerPad = MixTrackController.PAD.PAD_0X0;
        hyrm.addUserEvent("Intro", beatStamp, triiggerPad, new Runnable() {
            @Override
            public void run() {
                MidiController.getMidiController().getMixTrackController().blinkPad(triiggerPad, MixTrackController.BLINK_DURATION.BARS2, hyrm.tempo);
            }
        });

        hyrm.addUserEvent("Prechorus", new BeatStamp(10, 1), MixTrackController.PAD.PAD_0X0, new Runnable() {
            @Override
            public void run() {

            }
        });

        return hyrm;
    }

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
