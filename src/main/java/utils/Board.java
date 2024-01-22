package utils;

import pentominoes.Piece;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the board of the game with its functionalities.
 */
public class Board {
    public final static int WIDTH = 5;
    public final static int HEIGHT = 20;
    private int[][] b;

    /**
     * Constructs the board matrix.
     */
    public Board() {
        b = new int[HEIGHT][WIDTH];
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = 0; col < WIDTH; col++) {
                b[row][col] = -1;
            }
        }
    }

    public int[][] getB() {
        int[][] copy = new int[b.length][b[0].length];
        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, copy[i], 0, b[0].length);
        }
        return copy;
    }

    // Setter for the 2d array 'b'.
    public void setB(int[][] b) {
        this.b = new int[b.length][b[0].length];
        for (int i = 0; i < b.length; i++) {
            System.arraycopy(b[i], 0, this.b[i], 0, b[0].length);
        }
    }

    public int aggregateHeight() {
        int height = 0;
        for (int col = 0; col < WIDTH; col++) {
            for (int row = 0; row < HEIGHT; row++) {
                if (b[row][col] != -1) {
                    height += HEIGHT - row;
                    break;
                }
            }
        }

        return height;
    }

    public int bumpiness() {
        int bumpiness = 0;
        int previousHeight = -1; // Initialize with -1 to indicate no previous column yet

        for (int col = 0; col < WIDTH; col++) {
            int height = 0;

            // Find the height of the current column
            for (int row = 0; row < HEIGHT; row++) {
                if (b[row][col] != -1) {
                    height = HEIGHT - row;
                    break;
                }
            }

            // Calculate bumpiness starting from the second column
            if (previousHeight != -1) {
                bumpiness += Math.abs(height - previousHeight);
            }

            previousHeight = height; // Update previousHeight for the next iteration
        }

        return bumpiness;
    }

    public int getHeight() {
        int[] heights = new int[WIDTH];

        for (int col = 0; col < WIDTH; col++) {
            for (int row = 0; row < HEIGHT; row++) {
                if (b[row][col] != -1) {
                    heights[col] = HEIGHT - row;
                    break;
                }
            }
        }

        return Arrays.stream(heights).sum();
    }

    public int relativeHeight() {
        int[] heights = new int[WIDTH];

        for (int col = 0; col < WIDTH; col++) {
            for (int row = 0; row < HEIGHT; row++) {
                if (b[row][col] != -1) {
                    heights[col] = HEIGHT - row;
                    break;
                }
            }
        }

        int mx = 0, mn = HEIGHT;
        for (int height : heights) {
            mx = Math.max(mx, height);
            mn = Math.min(mn, height);
        }

        return mx - mn;
    }

    public int countHoles() {
        int holes = 0;

        for (int col = 0; col < WIDTH; col++) {
            boolean blockFound = false;
            for (int row = 0; row < HEIGHT; row++) {
                if (b[row][col] != -1) {
                    blockFound = true;
                } else if (blockFound) {
                    holes++;
                }
            }
        }

        return holes;
    }

    public void reset() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                b[i][j] = -1;
            }
        }
    }

    public static void toggleCells(Board board, Piece piece) {
        int[][] b = board.getB();
        boolean canBeToggled = true;

        for (Point point : piece.getPoints()) {
            int x = point.getX();
            int y = point.getY();
            
            if (b[y][x] != -1) {
                canBeToggled = false;
                break;
            }
            
            if (x < WIDTH && y < HEIGHT) {
                b[y][x] = piece.getId();
            }   
        }
        
        if (canBeToggled)
            board.setB(b);
    }

    public Board copy() {
        Board copy = new Board();
        for (int row = 0; row < HEIGHT; row++) {
            System.arraycopy(b[row], 0, copy.b[row], 0, WIDTH);
        }

        return copy;
    }

    public boolean isRowFull(int row) {
        boolean q = true;
        for (int col = 0; col < WIDTH; col++) {
            q &= (b[row][col] != -1);
        }

        return q;
    }
    
    public Pair<Integer, List<Integer>> checkLinesCleared() {
        Pair<Integer, List<Integer>> removed = new Pair<>();
        
        int linesRemoved = 0;

        List<Integer> rowsToRemove = new ArrayList<>();

        for (int row = 0; row < HEIGHT; row++) {
            boolean isFull = isRowFull(row);

            if (isFull) {
                linesRemoved++;
                rowsToRemove.add(row);
            }
        }
        
        removed.setFirst(linesRemoved);
        removed.setSecond(rowsToRemove);

        if (!rowsToRemove.isEmpty()) {

            int[][] newState = getB();

            for (int row : rowsToRemove) {
                // Clear the full row.
                for (int col = 0; col < WIDTH; col++) {
                    newState[row][col] = -1;
                }

                // moves the rows above the current row down
                for (int r = row; r > 0; r--) {
                    for (int col = 0; col < WIDTH; col++) {
                        newState[r][col] = newState[r - 1][col];
                    }
                }

                for (int col = 0; col < WIDTH; col++) {
                    newState[0][col] = -1;
                }
            }

            setB(newState);
        }

        return removed;
    }

    public boolean isGameOver(List<Point> points) {
        // calculate the height of the piece (which is represented by points param)
        int pieceHeight = Point.getMaxPointY(points) - Point.getMinPointY(points) + 1;
        int pieceWidth = Point.getMaxPointX(points) - Point.getMinPointX(points) + 1;
        int mnX = Point.getMinPointX(points);
        int mxX = Point.getMaxPointX(points);

        // count how many rows we have empty in the b
        int emptyRows = 0;
        boolean q = true;
        for (int row = 0; row < HEIGHT; row++) {
            for (int col = mnX; col < mxX; col++) {
                if (b[row][col] != -1) {
                    q = false;
                    break;
                }
            }

            if (q) emptyRows++;
        }

        return pieceHeight > emptyRows;
    }

    public boolean isRowEmpty(int row) {
        for (int x = 0; x < WIDTH; x++) {
            if (b[row][x] != -1) {
                return false;
            }
        }
        return true;
    }

    public int firstEmptyRow() {
        int row = 0;
        for (int i = 0; i < WIDTH; i++) {
            if (isRowEmpty(i)) {
                row = i;
                break;
            }
        }

        return row;
    }

    public int firstNonEmptyRow() {
        int row = 0;
        for (int i = 0; i < HEIGHT; i++) {
            if (!isRowEmpty(i)) {
                row = i;
                break;
            }
        }

        return row;
    }

    public int[] getRow(int row) {
        return b[row].clone();
    }

    public int getCell(int row, int col) {
        return b[row][col];
    }

    public boolean isCellEmpty(int row, int col) {
        return b[row][col] == -1;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        for (int row = 0; row < HEIGHT; row++) {
            res.append(String.format("%02d", row)).append(" - ");
            for (int col = 0; col < WIDTH; col++) {
                res.append(b[row][col]).append(" ");
            }
            res.append('\n');
        }

        return String.valueOf(res);
    }

}
