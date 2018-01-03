package Data;

import Midi.MixTrackController;

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
        songList.add(createHowYouRemindMe());
    }

    private Song createHowYouRemindMe() {
        Song hyrm = new Song("How you remind me", "Nickelback", 172, 4,
                new BeatStamp(80, 1), MixTrackController.PAD.PAD_0X0);

        // User Events
        BeatStamp beatStamp = new BeatStamp(7, 1);
        MixTrackController.PAD triiggerPad = MixTrackController.PAD.PAD_0X0;
        hyrm.addUserEvent("Intro", beatStamp, triiggerPad, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });

        hyrm.addUserEvent("Prechorus", new BeatStamp(10, 1), MixTrackController.PAD.PAD_0X0, new Runnable() {
            @Override
            public void run() {
                //TODO: Implement -> Blink pad
            }
        });

        // Pad Actions
        hyrm.addPadAction(MixTrackController.PAD.PAD_0X0, new Runnable() {
            @Override
            public void run() {

            }
        });

        return hyrm;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public List<Gig> getGigList() {
        return gigList;
    }
}
