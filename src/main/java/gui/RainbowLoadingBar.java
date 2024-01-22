package gui;

import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.Music;

import java.util.Objects;


public class RainbowLoadingBar extends Application {

    private HelloApplication helloApp;
    private Stage helloStage;
    private static final Duration ANIMATION_TIME = Duration.seconds(5);
    
    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/tetris.png")));

        helloApp = new HelloApplication();
        helloStage = new Stage(StageStyle.UNDECORATED);
        helloStage.getIcons().add(icon);

        helloApp.start(helloStage);
        helloStage.hide();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: transparent;");

        Rectangle borderRect = createRainbowRectangle();

        root.getChildren().add(borderRect);

        Scene scene = new Scene(root, 1000, 1000, Color.TRANSPARENT);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheets/style.css")).toExternalForm());

        primaryStage.initStyle(StageStyle.TRANSPARENT);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Rainbow Loading Bar");
        primaryStage.getIcons().add(icon);
        primaryStage.show();

        playBorderDrawingAnimation(borderRect, primaryStage);

        playColorShiftingAnimation(borderRect);
    }


    private Rectangle createRainbowRectangle() {
        Rectangle borderRect = new Rectangle(50, 50, 350, 400);
        borderRect.setFill(null);
        borderRect.setStrokeWidth(5);
        borderRect.setStrokeLineCap(StrokeLineCap.BUTT);
        borderRect.setEffect(new DropShadow(20, Color.web("#ffffff", 0.7)));
        return borderRect;
    }

    private void playBorderDrawingAnimation(Rectangle borderRect, Stage primaryStage) {
        double perimeter = borderRect.getWidth() * 2 + borderRect.getHeight() * 2;
        borderRect.getStrokeDashArray().setAll(perimeter);
        borderRect.setStrokeDashOffset(-perimeter);

        Timeline drawingTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(borderRect.strokeDashOffsetProperty(), -perimeter)),
                new KeyFrame(ANIMATION_TIME.subtract(Duration.seconds(0.5)),
                        new KeyValue(borderRect.strokeDashOffsetProperty(), 0, Interpolator.LINEAR))
        );
        drawingTimeline.setCycleCount(1);

        drawingTimeline.setOnFinished(event -> Platform.runLater(() -> {
            Bounds boundsInScreen = borderRect.localToScreen(borderRect.getBoundsInLocal());

            helloStage.setWidth(boundsInScreen.getWidth());
            helloStage.setHeight(boundsInScreen.getHeight());
            helloStage.setX(boundsInScreen.getMinX());
            helloStage.setY(boundsInScreen.getMinY());

            helloStage.show();

            primaryStage.close();
        }));

        drawingTimeline.play();
    }

    private void playColorShiftingAnimation(Rectangle borderRect) {
        AnimationTimer colorTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double time = (now / 1000000000.0) % ANIMATION_TIME.toSeconds();
                double hue = time * 360 / ANIMATION_TIME.toSeconds();
                borderRect.setStroke(Color.hsb(hue, 1.0, 1.0));
            }
        };
        colorTimer.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
