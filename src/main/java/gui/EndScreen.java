//package gui;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.AnchorPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.text.Text;
//import javafx.stage.Stage;
//import javafx.stage.StageStyle;
//
//import java.io.IOException;
//
//
//public class EndScreen extends Application {
//
//    @Override
//    public void start(Stage stage) throws IOException {
//        final int BUTTONWIDTH = 60;
//
//        AnchorPane root = new AnchorPane();
//        root.setStyle("-fx-background-color: #1E1E1E;");
//
//        VBox vBox = new VBox();
//        vBox.setAlignment(Pos.CENTER);
//        vBox.setSpacing(20);
//        vBox.setPadding(new Insets(20));
//        AnchorPane.setTopAnchor(vBox, 0.0);
//        AnchorPane.setRightAnchor(vBox, 0.0);
//        AnchorPane.setBottomAnchor(vBox, 0.0);
//        AnchorPane.setLeftAnchor(vBox, 0.0);
//
//        Label welcomeText = new Label();
//
//        Button letBotPlayButton = new Button("Bot");
//        letBotPlayButton.setStyle("-fx-background-color: #af4c4c; -fx-text-fill: white;");
//        letBotPlayButton.setMaxWidth(BUTTONWIDTH);
//
//        Button startBtn = new Button("Retry");
//        startBtn.setMinWidth(BUTTONWIDTH);
//        startBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
//        startBtn.setOnAction(event -> {
//            stage.close();
//            Application gameApp = new Game();
//            Stage gameStage = new Stage();
//            try {
//                gameApp.start(gameStage);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//
//        Text tetrisText = new Text("GAME OVER");
//
//        HelloController controller = new HelloController();
//        controller.setTetrisText(tetrisText);
//
//        Button closeButton = new Button("×");
//        closeButton.getStyleClass().add("close-btn");;
//        controller.setCloseButton(closeButton);
//
//        vBox.getChildren().addAll(tetrisText, welcomeText, startBtn,letBotPlayButton);
//
//
//        root.getChildren().add(vBox);
//        root.getChildren().add(closeButton);
//        AnchorPane.setTopAnchor(closeButton, 10.0);
//        AnchorPane.setRightAnchor(closeButton, 10.0);
//
//        Scene scene = new Scene(root, 320, 240);
//        stage.setTitle("Pentris");
//        stage.setScene(scene);
//        stage.initStyle(StageStyle.UNDECORATED); // Make the stage undecorated
//        stage.show();
//
//    }
//
//
//
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}
