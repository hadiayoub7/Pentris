package ai;

import gui.Game;
import javafx.application.Platform;
import pentominoes.Piece;
import utils.Point;

import java.util.Arrays;

public class Player implements Runnable{

    private final GBot gbot;
    private final Game game;
    private volatile int success = 1;
    private boolean isRunning = false;

    public Player(Game game, GBot gbot) {
        this.game = game;
        this.gbot = gbot;
    }

    public void play() {
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        isRunning = true;
        thread.start();
    }

    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }
    public void reset() {
        success = 1;
        isRunning = false;
    }

    @Override
    public void run() {

        Point RIGHT = new Point(1, 0);
        Point LEFT = new Point(-1, 0);

        System.out.println("BOT RUNS HERE ************************");
        while (isRunning && success != -1) {
            int[] bestMove = gbot.getBestMove();

            Piece piece = game.getPiece();

            System.out.println(Arrays.toString(bestMove) + " - " + piece.getName());

            // rotate right.
            piece.rotateRight(bestMove[0]);

            // adjust the piece to the left.
            for (int i = 0; i < Game.WIDTH; i++) {
                if (piece.isTranslateValid(game.getBoard().getB(), LEFT))
                    piece.translate(LEFT);
            }

            // move the piece to the right.
            for (int i = 0; i < bestMove[1]; i++) {
                if (piece.isTranslateValid(game.getBoard().getB(), RIGHT))
                    piece.translate(RIGHT);
            }

            // update the piece in game.
            game.setPiece(piece);

            success = 1; // allow the piece to be translated down.
            while (isRunning && success == 1) {
                Platform.runLater(() -> {
                    success = game.translatePiece(new Point(0, 1));
                    if (success == 0) {
                        game.bringNextPieceUp();
                    }
                });

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (success == -1)
            javafx.application.Platform.runLater(game::reset);
    }
}
