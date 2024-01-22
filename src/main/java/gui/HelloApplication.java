package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import utils.Music;

import java.util.Objects;

public class HelloApplication extends Application {
    public String getUsername() {
        return username;
    }

    private String username;
    private Music music;
    
    private void initMusic() {
        music = new Music();
    }
    @Override
    public void start(Stage stage) {

        AnchorPane root = new AnchorPane();
        
        initMusic();
        
        Thread t = new Thread(music::welcome);
        t.setDaemon(true);
        t.start();

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(20));
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);

        Label welcomeText = new Label();

        TextField userNameTextField = new TextField();
        userNameTextField.setPromptText("Username");
        userNameTextField.setMaxWidth(200);
        userNameTextField.getStyleClass().add("username-field");

        Button startBtn = new Button("Start");
        startBtn.getStyleClass().add("start-btn");
        startBtn.setOnAction(event -> {
            stage.close();
            Game gameApp = new Game(music);
            Stage gameStage = new Stage();
            gameApp.setUsername(userNameTextField.getText().trim().toLowerCase());
            try {
                gameApp.start(gameStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Text tetrisText = new Text("Pentris");

        HelloController controller = new HelloController();
        controller.setTetrisText(tetrisText);

        Button closeButton = new Button("×");
        closeButton.getStyleClass().add("close-btn");
        controller.setCloseButton(closeButton);

        vBox.getChildren().addAll(tetrisText, welcomeText, startBtn, userNameTextField);


        root.getChildren().add(vBox);
        root.getChildren().add(closeButton);
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);

        Scene scene = new Scene(root, 320, 240);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheets/style.css")).toExternalForm());
        stage.setTitle("Pentris");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED); // Make the stage undecorated
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
