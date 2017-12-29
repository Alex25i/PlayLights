package GUI;

import Logic.LiveTimeCode;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class SongPlayerController {

    private static Dimension2D WINDOW_SIZE_DEFAULT;

    private static SongPlayerController songPlayerController;

    private LiveTimeCode songTimeCode;

    @FXML
    private Canvas testCanvas;

    static {
        WINDOW_SIZE_DEFAULT = new Dimension2D(800, 600);
    }

    @FXML
    public void initialize() {
        songPlayerController = this;
        drawTestGraphics();
    }

    private void drawTestGraphics() {
        GraphicsContext gc = testCanvas.getGraphicsContext2D();

        gc.setLineWidth(1);
        gc.setStroke(Color.GRAY);
        gc.setFill(Color.GRAY);
        gc.strokeLine(1.5 * 200 + 10, 0, 1.5 * 200 + 10, 200);
        gc.setLineWidth(1.5);
        for (int i = 0; i <= 6; i++) {
            gc.strokeLine(i * 200 + 10, 0, i * 200 + 10, 200);
        }

        TranslateTransition tt = new TranslateTransition();
        tt.setDuration(Duration.seconds(5));
        tt.setNode(testCanvas);
        tt.setInterpolator(Interpolator.LINEAR);
        tt.setCycleCount(Animation.INDEFINITE);
        tt.setFromX(0);
        tt.setFromY(0);
        tt.setByX(-600);
        tt.setByY(0);
        tt.play();
    }



    public SongPlayerController getSongPlayerController() {
        return songPlayerController;
    }
}
