package gui;

import ai.GBot;
import ai.Player;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import pentominoes.Piece;
import utils.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


/**
 * This class represents the Tetris game board with its functionality.
 */
public class Game extends Application {

    private Music music;
    public static final int WIDTH = 5;
    public static final int HEIGHT = 20;
    private final int squareSize = 40;
    private boolean gameOver;
    private final Rectangle[][] boardCells;
    private Board board;
    private Scene mainScene;
    private GridPane nextMoveGrid;
    private GridPane holdPieceGrid;
    private List<Point> ghostPiecePoints;
    private Timeline timeline;
    private int score;
    private int currentRow;
    private Label currentScore;
    private boolean BLOCK_INPUT;
    private Button botBtn;
    private Button playBestOrderBtn;
    private Player player;
    private final int MOVE_INTERVAL = 400; // millis.
    private final int LOCK_DELAY = 500; // mills
    private final Color EMPTY_COLOR = Color.valueOf("#2c2a2a");
    private GBot gBot;
    private Piece piece;
    private Piece nextPiece;
    private Piece holdPiece;
    private Piece[] bestOrder;
    private int currPiece;
    private final HighScores highScores;
    private boolean holdIsUsed;
    private boolean ONCE;
    private String username;
    TableView<Score> scoreTable;
    private Label highestScoreValue;
    private Dialog<String> dialog;

    /**
     * Constructs the board of the game with cells.
     */
    public Game(Music music) {

        boardCells = new Rectangle[HEIGHT][WIDTH];
        dialog = new Dialog<>();
        piece = new Piece();
        this.music = music;
        nextPiece = new Piece();
        bestOrder = null;
        currPiece = 0;
        board = new Board();
        ghostPiecePoints = new ArrayList<>();
        highScores = new HighScores();
        highestScoreValue = new Label();
        score = 0;
        currentRow = HEIGHT - 1;
        ONCE = true;
        BLOCK_INPUT = false;
        gameOver = false;
        holdIsUsed = false;
        

        gBot = new GBot(this);
        player = new Player(this, gBot);

        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                Rectangle cell = new Rectangle(squareSize, squareSize);
                cell.setFill(EMPTY_COLOR);
                boardCells[row][col] = cell;
            }
        }

        initializeBoard();
//        createDialog();
    }
    
    private void createDialog(String title) {
        dialog.setHeaderText(title);
        dialog.getDialogPane().getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/stylesheets/style.css")).toExternalForm());
        ((Stage) (dialog.getDialogPane().getScene().getWindow())).getIcons().add(
                new Image(Objects.requireNonNull(getClass().getResource("/images/tetris.png")).toExternalForm()));
        dialog.getDialogPane().getStyleClass().add("dialog");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    /**
     * This basically will initiate the board.
     */
    private void initializeBoard() {

        // split the window into two main panes
        SplitPane pane = new SplitPane();
        pane.setOrientation(Orientation.HORIZONTAL);

        Rectangle clip = new Rectangle();
        clip.setWidth(WIDTH * squareSize);
        clip.setHeight((HEIGHT - 5) * 65);

        GridPane grid = new GridPane(HEIGHT, WIDTH);
        grid.setPrefSize(200, 618);
        grid.setMinSize(200, 618);
        grid.setMaxSize(200, 618);
        grid.getStyleClass().add("game-grid");
        grid.setVgap(1);
        grid.setHgap(1);
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                grid.add(boardCells[row][col], col, row);
            }
        }
        
        // hide the first five rows which are used only to simulate that the pieces are spawning down.
        grid.setClip(clip);
        grid.setTranslateY(-(5 * 41));
        
        pane.getItems().add(initializeLeftPanel()); // create the left panel.
        pane.getItems().add(grid); // the game grid.
        pane.getItems().add(initializeRightPanel()); // the right panel.

        mainScene = new Scene(pane);
        mainScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheets/style.css")).toExternalForm());
        mainScene.setOnKeyPressed(this::handleUserKeyboardInput);
        mainScene.setOnKeyReleased(Event::consume);

        initiateFirstPiece(); // create the first piece to be placed on the board.
    }

    /**
     * Initiate the right panel of the board.
     *
     * @return <i>VPox</i> - the right panel container.
     */
    private VBox initializeRightPanel() {
        VBox rightPane = new VBox();
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.setPadding(new Insets(20, 0, 0, 0)); // leave a space from the top.

        // create the highest score.
        Label highestScore = new Label("Highest Score");
        highestScore.getStyleClass().add("highest-score");
        VBox.setMargin(highestScore, new Insets(0, 0, 10, 0));
        rightPane.getChildren().add(highestScore);

        highestScoreValue = new Label();
        highestScoreValue.getStyleClass().add("highest-score-value");
        VBox.setMargin(highestScoreValue, new Insets(0, 0, 80, 0));
        rightPane.getChildren().add(highestScoreValue);

        // create the current score.
        Label currentScoreLabel = new Label("Your Score");
        currentScoreLabel.getStyleClass().add("current-score");
        VBox.setMargin(currentScoreLabel, new Insets(0, 0, 10, 0));
        rightPane.getChildren().add(currentScoreLabel);

        currentScore = new Label("0");
        currentScore.getStyleClass().add("current-score-value");
        VBox.setMargin(currentScore, new Insets(0, 0, 100, 0));
        rightPane.getChildren().add(currentScore);

        // create the next move.
        Label next = new Label("Next moves");
        next.getStyleClass().add("next-move");
        VBox.setMargin(next, new Insets(0, 0, 10, 0));
        rightPane.getChildren().add(next);

        // create the grid that shall represent the small next piece on the right panel.
        nextMoveGrid = updateNextPieceGrid();
        VBox.setMargin(nextMoveGrid, new Insets(0, 0, 50, 0));
        rightPane.getChildren().add(nextMoveGrid);

        // create the button that runs the bot.
        botBtn = new Button();
        botBtn.setText("Run Bot");
        botBtn.getStyleClass().add("run-bot");
        botBtn.setAlignment(Pos.CENTER);
        botBtn.setFocusTraversable(false);
        botBtn.setMinSize(70, 30);
        botBtn.setTextAlignment(TextAlignment.CENTER);
        botBtn.setOnAction(ActionEvent -> toggleRunMode());
        VBox.setMargin(botBtn, new Insets(0, 0, 20, 0));
        rightPane.getChildren().add(botBtn);

        playBestOrderBtn = new Button();
        playBestOrderBtn.setText("Play Best Order");
        playBestOrderBtn.getStyleClass().add("run-bot");
        playBestOrderBtn.setAlignment(Pos.CENTER);
        playBestOrderBtn.setFocusTraversable(false);
        playBestOrderBtn.setMinSize(100, 30);
        playBestOrderBtn.setTextAlignment(TextAlignment.CENTER);
        playBestOrderBtn.setOnAction(ActionEvent -> playBestOrder());
        rightPane.getChildren().add(playBestOrderBtn);

        return rightPane;
    }
    
    /**
     * Initiate the left panel of the board.
     * @return VBox - the left panel container.
     */
    private VBox initializeLeftPanel() {
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.TOP_CENTER);
        leftPane.setPadding(new Insets(20, 0, 0, 0));
        leftPane.setMinWidth(200);
        
        // create the hold grid
        Label holdLabel = new Label();
        holdLabel.setText("Hold");
        holdLabel.getStyleClass().add("hold-label");
        VBox.setMargin(holdLabel, new Insets(0, 0, 10, 0));
        leftPane.getChildren().add(holdLabel);
        
        holdPieceGrid = updateHoldPieceGrid();
        VBox.setMargin(holdPieceGrid, new Insets(0, 0, 50, 0));
        leftPane.getChildren().add(holdPieceGrid);
        
        // create the highscore table
        scoreTable = new TableView<>(); // create table.
        scoreTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_LAST_COLUMN);
        scoreTable.setEditable(false);
        scoreTable.setFocusTraversable(false);
        scoreTable.setPrefSize(200, 250);
        VBox.setMargin(scoreTable, new Insets(0, 20, 0, 20));

        TableColumn<Score, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<Score, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreTable.getColumns().add(usernameColumn);
        scoreTable.getColumns().add(scoreColumn);
        
        updateHighscore();
        
        leftPane.getChildren().add(scoreTable);
        
//        VBox.setMargin(holdPieceGrid, new Insets(0, 0, 50, 0));
        
        return leftPane;
    }

    /**
     * Update the highscore list.
     */
    private void updateHighscore() {
        ObservableList<Score> scoresList = FXCollections.observableArrayList(highScores.getAllScores());
        highestScoreValue.setText(String.valueOf(scoresList.getFirst().getScore()));
        scoreTable.setItems(scoresList);
    }

    /**
     * Update the grid that holds the next piece.
     *
     * @return <i>GridPane</i> - the next piece container.
     */
    private GridPane updateNextPieceGrid() {
        GridPane nextMove = new GridPane(5, 5);
        nextMove.setPrefSize(110, 110);
        nextMove.getStyleClass().add("piece-grid");
        nextMove.setAlignment(Pos.CENTER);
        nextMove.setVgap(1);
        nextMove.setHgap(1);

        return nextMove;
    }

    /**
     * Update the grid that holds the piece that is being held.
     * @return GridPane - the piece on hold container.
     */
    private GridPane updateHoldPieceGrid() {
        GridPane holdPiece = new GridPane(5, 5);
        holdPiece.setPrefSize(110, 110);
        holdPiece.getStyleClass().add("piece-grid");
        holdPiece.setAlignment(Pos.CENTER);
        holdPiece.setVgap(1);
        holdPiece.setHgap(1);
        
        return holdPiece;
    }

    private void handleUserKeyboardInput(KeyEvent keyEvent) {
        // stop all the keys from working when the game is paused except the `P` to resume it again, or
        // it's over.
        if (BLOCK_INPUT || gameOver || ((player.isRunning() || timeline.getStatus() == Animation.Status.PAUSED)
                && keyEvent.getCode() != KeyCode.P)) {
            return;
        }
        
        switch (keyEvent.getCode()) {
            case LEFT: {
            }
            case A: {
                move(-1);
                break;
            }

            case RIGHT: {
            }
            case D: {
                move(1);
                break;
            }

            case DOWN: {
            }
            case S: {
                shortQuickDrop();
                break;
            }
            
            case C: {
                if (!holdIsUsed) {
                    holdPiece();
                    holdIsUsed = true;
                }
                break;
            }

            case UP: {
            }
            case W: {
                rotate(1);
                break;
            }

            case Z: {
                rotate(-1);
                break;
            }

            case SPACE: {
                drop();
                break;
            }

            case ESCAPE:
            case P: {
                toggleGameState();
                break;
            }
        }
    }

    /**
     * Toggle the run between the bot and the user-play.
     */
    private void toggleRunMode() {

        botBtn.getStyleClass().clear();

        if (player.isRunning() || gameOver) {
            stopBot();
        } else {
            runBot();
        }
    }

    /**
     * Run the bot for the current game state.
     */
    private void runBot() {
        botBtn.setText("Stop Bot");
        botBtn.getStyleClass().clear();
        botBtn.getStyleClass().add("stop-bot");
        clearPrevious(piece.getPoints());
        timeline.stop();
        player.play();
    }

    /**
     * Stop the bot for the current game state.
     */
    private void stopBot() {
        botBtn.setText("Run Bot");
        botBtn.getStyleClass().clear();
        botBtn.getStyleClass().add("run-bot");
        playBestOrderBtn.setDisable(false);

        player.stop();

        if (gameOver) {
            gameOver = false;
            music.tetris();
            resetUI();
            initiateFirstPiece();
            letPiecesRain();
        }

        if (timeline.getStatus() != Animation.Status.RUNNING) {
            timeline.play();
        }
    }

    /**
     * Play the best order defined already in the class <b>Piece</b>.
     */
    private void playBestOrder() {
        reset();
        resetUI();
        
        if (playBestOrderBtn.isDisabled()) { // it was already played, reset.
            player.stop();
            playBestOrderBtn.setDisable(false);
            bestOrder = null;
            currPiece = 0;
        } else { // run the best order.
            playBestOrderBtn.setDisable(true);
            bestOrder = new Piece[12];
            initiateFirstPiece();
            runBot();
        }
    }

    public void reset() {
        if (timeline != null) {
            timeline.stop();
        }

        if (player != null) {
            player.stop();
            gBot = new GBot(this);
            player = new Player(this, gBot);
            if (gameOver) {
                music.gameOver();
            }
            
            player.reset();
        }

        botBtn.setText("Play Again");
        botBtn.getStyleClass().clear();
        botBtn.getStyleClass().add("run-bot");

        ghostPiecePoints.clear();
        board.reset();

        currentRow = HEIGHT - 1;
//        gameOver = false;
        score = 0;

        piece = new Piece();
        piece.getRandomPiece();
        
        currPiece = 0;

        nextPiece = new Piece();
        nextPiece.getRandomPiece();
//        updateScore();
    }

    private void resetUI() {
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                boardCells[row][col].setFill(EMPTY_COLOR);
            }
        }

        updateScore();
    }

    /**
     * Put the current piece on hold and get the next piece on play.
     */
    private void holdPiece() {
        clearPrevious(piece.getPoints());
        
        if (holdPiece == null) { // hold piece is empty.
            holdPiece = piece.copy();
            holdPiece.fixStartPoint();
            piece = nextPiece.copy();
            piece.fixStartPoint();
            nextPiece.getRandomPiece();
        } else {
            Piece tmp = holdPiece.copy();
            tmp.fixStartPoint();
            holdPiece = piece.copy();
            holdPiece.fixStartPoint();
            piece = tmp.copy();
            piece.fixStartPoint();
        }
        
        updateNextPieceGrid();
        updateHoldPieceShape();
    }

    private void updateNextPieceShape() {
        nextMoveGrid.getChildren().clear();

        // We are using points (x,y) from Point class for the piece representation instead of using two loops.
        for (Point point : nextPiece.getPoints()) {
            int x = point.getX();
            int y = point.getY();

            Rectangle cell = new Rectangle(20, 20);
            cell.getStyleClass().add("next-move-grid-cell");
            cell.setFill(nextPiece.getColor());

            nextMoveGrid.add(cell, x, y);
        }
    }
    
    private void updateHoldPieceShape() {
        holdPieceGrid.getChildren().clear();

        for (Point point : holdPiece.getPoints()) {
            int x = point.getX();
            int y = point.getY();

            Rectangle cell = new Rectangle(20, 20);
            cell.getStyleClass().add("next-move-grid-cell");
            cell.setFill(holdPiece.getColor());

            holdPieceGrid.add(cell, x, y);
        }
    }

    /**
     * Initiate the first pentominoe piece.
     */
    private void initiateFirstPiece() {
        if (bestOrder == null) { // normal play.
            piece.getRandomPiece();
            nextPiece.getRandomPiece();
        } else { // best order play. so, get the pieces.
            bestOrder = Piece.bestOrder();
            piece = bestOrder[currPiece];
            currPiece++;

            nextPiece = bestOrder[currPiece];
            currPiece++;
        }
    }
    
    /**
     * This method will make sure the pentominoe piece keeps falling down.
     */
    private void letPiecesRain() {
        updateNextPieceShape();
        colorPieceOnBoard(piece.getPoints(), false);
        
        new Thread(() -> Platform.runLater(() -> music.tetris())).start();
        
        timeline = new Timeline(new KeyFrame(Duration.millis(MOVE_INTERVAL), actionEvent -> {
            int translated = translatePiece(new Point(0, 1));

            if (translated == 0) { // Piece Landed.
                bringNextPieceUp();
            } else if (translated == -1) { // Game Over.
                reset();
                music.gameOver();
            }
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Translate the pentominoe piece by Point (x,y).
     * That means the piece will translate x amount to right/left and y amount down.
     *
     * @param p A point represents the translation value.
     */
    public int translatePiece(Point p) {
        BLOCK_INPUT = true;

        if (piece.isTranslateValid(board.getB(), p)) { // can be translated.
            clearPrevious(piece.getPoints());
            piece.translate(p);
            colorPieceOnBoard(piece.getPoints(), false);
        } else {
            if (gameOver) return -1;
            ghostPiecePoints.clear();
            return 0; // A collision happened.
        }

        BLOCK_INPUT = false;

        return 1; // Can be translated down further.
    }

    /**
     * Check whether the game is over or not.
     * This is done by: 
     * 1. Checking if a piece cannot exceed the first row (because the first row is full).
     * 2. Checking if a piece cannot fully be on the board.
     * 
     * @return True if the game is over, False otherwise.
     */
    public boolean isGameOver() {
        if (currentRow < 5 && currentRow > 0)
            return true;

        for (Point point : piece.getPoints()) {
            int beneathY = point.getY() - 1;
            int x = point.getX();
            if (beneathY > 0 && beneathY < 5 && !board.isCellEmpty(beneathY, x)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Color the pentominoe piece each with its own color.
     *
     * @param points of the pentominoe piece.
     * @param ghost  to determine, whether the piece to be colored is the falling piece or the ghost/shadow one.
     */
    private void colorPieceOnBoard(List<Point> points, boolean ghost) {
        BLOCK_INPUT = true;

        if (!ghost)
            displayGhostPiece();

        Color color = ghost ? piece.getColor().deriveColor(3, 3, 5, 0.1) : piece.getColor();
        // color the pentomino piece
        for (Point point : points) {
            int col = point.getX();
            int row = point.getY();

            if (board.isCellEmpty(row, col))
                boardCells[row][col].setFill(color);
        }

        BLOCK_INPUT = false;
    }

    /**
     * Clear the previous pentominoe piece on the board.
     *
     * @param points List of points represent the pentominoe piece to be cleared.
     */
    private void clearPrevious(List<Point> points) {
        BLOCK_INPUT = true;

        for (Point point : points) {
            if (board.isCellEmpty(point.getY(), point.getX())) {
                boardCells[point.getY()][point.getX()].setFill(EMPTY_COLOR);
            }
        }

        BLOCK_INPUT = false;
    }

    /**
     * Fix the current piece on board and bring the next one.
     */
    public void bringNextPieceUp() {
        BLOCK_INPUT = true;
        holdIsUsed = false; // make sure the hold option is enabled one time for each new piece.

        ghostPiecePoints.clear();
        // color the piece on board after it collides.
        colorPieceOnBoard(piece.getPoints(), false);

        board.setB(piece.togglePiece(board.getB())); // toggle cells.

        currentRow = board.firstNonEmptyRow();

        // check if some rows are full to be removed before placing the new piece on oldBoard.
        Pair<Integer, List<Integer>> removed = board.checkLinesCleared();
        if (removed.getFirst() > 0) {
            music.lineCleared();
            score += removed.getFirst();
            if (score > Integer.parseInt(highestScoreValue.getText()) && ONCE) {
                music.newScore();
                highestScoreValue.setText(String.valueOf(score));
                updateHighscore();
                ONCE = false;
            }
            updateScore();
            removeRowGUI(removed.getSecond());
        }

        if (isGameOver()) { // Game Over.
//            System.out.println("Game is Over! " + currentRow);
//            System.out.println("Score = " + score);
            writeScoreToFile(); // save score in the file.
            updateHighscore(); // update the highscore list
            gameOver = true;
            return;
        }

        if (bestOrder == null) {
            piece = nextPiece.copy(); // change the current piece to the next one.
            nextPiece.getRandomPiece();
        } else {
                bestOrder = Piece.bestOrder();
                piece = nextPiece.copy();
                if (currPiece <= 12) {
                    nextPiece = bestOrder[currPiece % 12];
                    currPiece++;
                } else { // reset
                    playBestOrder();
                }
            }
//        } else {
//            bestOrder = Piece.bestOrder();
//            piece = nextPiece.copy();
//            nextPiece = bestOrder[currPiece % 12];
//            currPiece++;
//        }
        
        updateNextPieceShape();

        BLOCK_INPUT = false;
    }

    /**
     * Display the ghost piece on the b.
     */
    private void displayGhostPiece() {
        clearPrevious(ghostPiecePoints);

        Piece dropped = piece.copy();
        dropped.drop(board);

        ghostPiecePoints = dropped.getPoints();

        // drop the piece all the way downer and color it.
        colorPieceOnBoard(ghostPiecePoints, true);
    }

    public void removeRowGUI(List<Integer> rowsToRemove) {
        for (int row : rowsToRemove) {
            // moves the rows above the current row down
            for (int r = row; r > 0; r--) {
                for (int col = 0; col < WIDTH; col++) {
                    boardCells[r][col].setFill(boardCells[r - 1][col].getFill());
                }
            }

            for (int col = 0; col < WIDTH; col++) {
                boardCells[0][col].setFill(EMPTY_COLOR);
            }
        }
    }

    /**
     * Moves the piece to the right / left.
     *
     * @param x Direction of the movement. Right (= 1) or left (= -1).
     */
    public void move(int x) {
        BLOCK_INPUT = true;

        Point p = new Point((x == 1) ? 1 : -1, 0);
        
        if (piece.isTranslateValid(board.getB(), p)) { // piece can be moved.
            clearPrevious(piece.getPoints());
            piece.translate(p);
            colorPieceOnBoard(piece.getPoints(), false);
        }

        BLOCK_INPUT = false;
    }

    /**
     * Drop the piece all the way down.
     */
    public void drop() {
        BLOCK_INPUT = true;
        
        clearPrevious(piece.getPoints());
        piece.setPoints(ghostPiecePoints);
        colorPieceOnBoard(piece.getPoints(), false);

        ghostPiecePoints.clear();

        // bring the next piece immediately in order not to wait MOVE_INTERVAL to the piece to show up.
        bringNextPieceUp();

        BLOCK_INPUT = false;
    }

    /**
     * Short drop the piece.
     */
    private void shortQuickDrop() {
        translatePiece(new Point(0, 2));
    }

    /**
     * Rotate the piece.
     * @param direction The rotation direction to right(= 1) or left(= -1).
     */
    public void rotate(int direction) {
        BLOCK_INPUT = true;

        Piece beforeRotation = piece.copy();

        // save the piece points in case the rotation is invalid
        if (direction == 1) {
            piece.rotateRight(1);
        } else {
            piece.rotateLeft(1);
        }

        // check if the piece can be rotated at the current position.
        if (piece.isTranslateValid(board.getB(), new Point(0, 0))) {
            clearPrevious(beforeRotation.getPoints());
            colorPieceOnBoard(piece.getPoints(), false); // display piece.
        } else { // restore piece.
            piece = beforeRotation;
        }

        BLOCK_INPUT = false;
    }

    /**
     * Toggles the game state between running and pausing.
     */
    public void toggleGameState() {
        if (player.isRunning()) {
            player.stop();
            music.welcome();
            return;
        }

        if (timeline.getStatus() == Animation.Status.RUNNING) { // game is running, pause it.
            timeline.stop();
            music.pause();
        } else { // game is paused, resume it
            timeline.play();
            music.tetris();
        }
    }

    /**
     * Update the score.
     */
    public void updateScore() {
        currentScore.setText(String.valueOf(score));
    }
    
    private void writeScoreToFile() {
        try (FileWriter writer =
                     new FileWriter("src/main/resources/highscores.txt", true)) {
            
            if (username == null) {
                username = "player-" + Long.toString(System.currentTimeMillis()).substring(7);
            }
            
            String string = String.format("%s %d\n", username, score);
            writer.append(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean gameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public Board getBoard() {
        return board.copy();
    }

    public void setBoard(Board b) {
        this.board = b.copy();
    }

    public Piece getPiece() {
        return piece.copy();
    }

    public Piece getNextPiece() {
        return nextPiece.copy();
    }

    public int getScore() {
        return score;
    }

    public void loadGameState(GameState state) {
        board = state.board.copy();
        piece = state.piece.copy();
        nextPiece = state.nextPiece.copy();
        score = state.score;
    }

    public void setPiece(Piece piece) {
        this.piece = piece.copy();
    }

    public void setNextPiece(Piece nextPiece) {
        this.nextPiece = nextPiece.copy();
    }

    public GBot getgBot() {
        return gBot;
    }

    public void setgBot(GBot gBot) {
        this.gBot = gBot;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public void start(Stage stage) {
        Font.loadFont(getClass().getResourceAsStream("/fonts/BruceForeverRegular-X3jd2.ttf"), 60);
        Font.loadFont(getClass().getResourceAsStream("/fonts/Babymentani-OVBr3.ttf"), 60);
        stage.setScene(mainScene);
        stage.setTitle("Pentris");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/tetris.png"))));
        stage.setMinWidth(400);
        stage.setResizable(false);
        stage.show();
        stage.focusedProperty().addListener((observableValue, aBoolean, t1) -> toggleGameState());

        updateHighscore();
        letPiecesRain();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
