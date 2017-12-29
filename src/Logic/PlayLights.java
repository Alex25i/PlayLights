package Logic;

import Data.Library;
import GUI.UiManager;

public class PlayLights {
    public static PlayLights playLights;
    private UiManager uiManager;
    private Library library;

    public PlayLights(UiManager uiManager) {
        library = new Library();
        this.uiManager = uiManager;
    }
}
