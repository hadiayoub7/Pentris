package gui;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelloController {

    private Text tetrisText;
    private Button closeButton;
    private double offset = 0.0;


    public void setTetrisText(Text tetrisText) {
        this.tetrisText = tetrisText;
        initializeTetrisText();
    }


    public void setCloseButton(Button closeButton) {
        this.closeButton = closeButton;
        this.closeButton.setOnAction(event -> handleCloseButtonAction());
    }

    private void initializeTetrisText() {
        // Load custom font (assuming the font file is in the resources/fonts directory)
        try {
            Font customFont = Font.loadFont(getClass().getResourceAsStream("/fonts/CyberwayRiders-lg97d.ttf"), 60);
            tetrisText.setFont(customFont);
        } catch (Exception e) {
            tetrisText.setFont(new Font("System", 60)); // Fallback font if custom font fails to load
        }

        // Animation logic
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                offset += 0.0050;
                if (offset > 1) {
                    offset -= 1;
                }

                LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true,
                        CycleMethod.REPEAT,
                        new Stop(offset % 1.0, Color.web("#711c91")), // Purple
                        new Stop((offset + 0.2) % 1.0, Color.web("#ea00d9")), // Pink
                        new Stop((offset + 0.4) % 1.0, Color.web("#0abdc6")), // Cyan
                        new Stop((offset + 0.6) % 1.0, Color.web("#133e7c")), // Dark blue
                        new Stop((offset + 0.8) % 1.0, Color.web("#091833")), // Very dark blue
                        new Stop((offset + 1.0) % 1.0, Color.web("#711c91"))); // Purple again for seamless transition

                tetrisText.setFill(gradient);

                DropShadow ds = new DropShadow();
                ds.setOffsetY(0.0f);
                ds.setOffsetX(0.0f);
                ds.setColor(Color.web("#0abdc6"));
                ds.setRadius(20);

                tetrisText.setEffect(ds);
            }
        };
        timer.start();
    }

    public void handleCloseButtonAction() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
        System.exit(0);
    }
}
