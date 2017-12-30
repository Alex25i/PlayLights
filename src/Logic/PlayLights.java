package Logic;

import Data.Library;
import GUI.UiManager;

public class PlayLights {
    private static PlayLights playLights;
    private UiManager uiManager;
    private Library library;

    public PlayLights(UiManager uiManager) {
        playLights = this;
        library = new Library();
        this.uiManager = uiManager;
    }

    public static PlayLights getPlayLights() {
        return playLights;
    }

    public UiManager getUiManager() {
        return uiManager;
    }

    public Library getLibrary() {
        return library;
    }
}
