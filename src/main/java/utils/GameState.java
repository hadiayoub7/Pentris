package utils;

import gui.Game;
import pentominoes.Piece;

public class GameState {
    public int score;
    public Board board;
    public Piece piece;
    public Piece nextPiece;
    private int lastRow;

    public GameState() {
    }

    public GameState(Game game) {
        board = game.getBoard();
        score = game.getScore();
        piece = game.getPiece();
        nextPiece = game.getNextPiece();
        lastRow = 0;
    }

    public void reload(Game game) {
        board = game.getBoard();
        score = game.getScore();
        piece = game.getPiece();
        nextPiece = game.getNextPiece();
    }

    public boolean isGameOver() {

        lastRow = board.firstNonEmptyRow();

        if (lastRow < 5 && lastRow > 0)
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

    public int checkLinesCleared() {
        return board.checkLinesCleared().getFirst();
    }

    public GameState copy() {
        GameState copy = new GameState();
        copy.score = this.score;
        copy.board = this.board;
        copy.piece = this.piece;
        copy.nextPiece = this.nextPiece;

        return copy;
    }
}
