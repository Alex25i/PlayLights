package GUI;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class SongCenterController implements Initializable {

    @FXML
    private Pane tableContainer;

    private static SongCenterController songCenterController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SongCenterController.songCenterController = this;
    }
}
