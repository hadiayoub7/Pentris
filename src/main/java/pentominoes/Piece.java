package pentominoes;

import gui.Game;
import javafx.scene.paint.Color;
import utils.Board;
import utils.Pair;
import utils.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * This class has the functionalities for the pieces defined in Pentominoe Enum.
 */
public class Piece {
    private List<Point> points;
    private Color color;
    private int rotations;
    private char name;
    private int[][] representation;
    private int id;

    public Piece() {}

    /**
     * Configure the pentominoe piece into the current object.
     * @param pentominoe The pentominoe piece that needs to be configured in this piece object.
     */
    private void config(Pentominoes pentominoe) {
        name = pentominoe.getName();
        color = pentominoe.getColor();
        rotations = pentominoe.getRotations();
        representation = pentominoe.getRepresentation();
        points = Point.array2points(representation);
        id = pentominoe.getId();

        fixStartPoint();
    }

    public Piece copy() {
        Piece copy = new Piece();
        copy.setPoints(this.getPoints());
        copy.setColor(this.getColor());
        copy.setRepresentation(this.getRepresentation());
        copy.setName(this.getName());
        copy.setRotations(this.getRotations());
        copy.setId(this.getId());

        return copy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Point> getPoints() {
        return new ArrayList<>(points);
    }

    public void setPoints(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getRotations() {
        return rotations;
    }

    public void setRotations(int rotations) {
        this.rotations = rotations;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    public int[][] getRepresentation() {
        return representation;
    }

    public void setRepresentation(int[][] representation) {
        this.representation = representation;
    }

    public void rotateRight(int rotations) {
        for (int i = 0; i < rotations; i++)
            rotateRight();
    }

    public void rotateLeft(int rotations) {
        for (int i = 0; i < rotations; i++)
            rotateLeft();
    }

    public void translate(Point point) {
        List<Point> newPoints = new ArrayList<>();

        for (Point p : points)
            newPoints.add(new Point(point.getX() + p.getX(), point.getY() + p.getY()));

        setPoints(newPoints);
    }

    public boolean isTranslateValid(int[][] b, Point p) {
        boolean q = true;

        for (Point point : points) {
            int x = point.getX() + p.getX();
            int y = point.getY() + p.getY();
            q &= (x >= 0 && x < b[0].length && y < b.length && b[y][x] == -1);
        }

        return q;
    }

    public void drop(Board board) {
        Piece dropped = drop(board, false).getSecond();
        setPoints(dropped.getPoints());
    }

    public Pair<Board, Piece> drop(Board board, boolean toggle) {
        Piece original = this;

        Pair<Board, Piece> dropped = new Pair<>();
        Point downTranslate = new Point(0, 1);

        while (isTranslateValid(board.getB(), downTranslate)) {
            translate(downTranslate);
        }

        dropped.setFirst(board);
        dropped.setSecond(this);

        if (toggle) {
            Board.toggleCells(board, this);
        } else {
            setPoints(original.getPoints());
        }

        return dropped;
    }

    public void rotateLeft() {
        // We'll rotate the piece to right around the middle point.
        Point mid = points.get(2);

        int minX = Game.WIDTH, maxX = 0;
        int minY = Game.HEIGHT, maxY = 0;

        List<Point> rotated = new ArrayList<>();
        for (Point point : points) {
            int x = mid.getX() - (point.getY() - mid.getY());
            int y = mid.getY() + (point.getX() - mid.getX());

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            rotated.add(new Point(x, y));
        }

        setPoints(rotated);

        // the rotated piece may get out of boundaries, i.e., to the 2nd, 3rd or 4th quadrant
        //      so, translate it back to the first quadrant within the range 5x15.
        fixCoordinates(minX, minY, maxX, maxY);
    }

    public void rotateRight() {
        // We'll rotate the piece to right around the middle point.
        Point mid = points.get(2);

        int minX = Game.WIDTH, maxX = 0;
        int minY = Game.HEIGHT, maxY = 0;

        List<Point> rotated = new ArrayList<>();
        for (Point point : points) {
            int x = mid.getX() + (point.getY() - mid.getY());
            int y = mid.getY() - (point.getX() - mid.getX());

            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);

            rotated.add(new Point(x, y));
        }

        setPoints(rotated);

        fixCoordinates(minX, minY, maxX, maxY);
    }

    private void fixCoordinates(int minX, int minY, int maxX, int maxY) {
        if (minX < 0) { // fix left
            for (Point point : points) {
                int x = point.getX() + Math.abs(minX);
                point.setX(x);
            }
        } else if (maxX >= Game.WIDTH) { // fix right
            int diffX = maxX - (Game.WIDTH - 1);
            for (Point point : points) {
                int x = point.getX() - diffX;
                point.setX(x);
            }
        }

        if (minY < 0) { // fix above
            for (Point point : points) {
                int y = point.getY() + Math.abs(minY);
                point.setY(y);
            }
        } else if (maxY >= Game.HEIGHT) { // fix bottom
            int diffY = maxY - (Game.HEIGHT - 1);
            for (Point point : points) {
                int y = point.getY() - diffY;
                point.setY(y);
            }
        }
    }

    public void fixStartPoint() {
        bringToTopLeft(); // bring piece top left corner.
        
        int shiftVal = Math.max(0, 4 - Point.getMaxPointY(points));

        List<Point> newPoints = new ArrayList<>();
        for (Point point : points) {
            int col = point.getX();
            int row = point.getY() + shiftVal;
            newPoints.add(new Point(col, row));
        }

        setPoints(newPoints);
    }
    
    private void bringToTopLeft() {
        int minY = Point.getMinPointY(points);
        int minX = Point.getMinPointX(points);
        
        List<Point> newPoints = new ArrayList<>();
        for (Point point : points) {
            int col = point.getX();
            int row = point.getY();
            newPoints.add(new Point(col - minX, row - minY));
        }
        
        setPoints(newPoints);
    }

    public void getRandomPiece() {
        Pentominoes pentominoe = Pentominoes.values()[(int) (Math.random() * 12)];
        config(pentominoe);
    }

    public int[][] togglePiece(int[][] b) {
        for (Point point : points) {
            int x = point.getX();
            int y = point.getY();
            if (x < b[0].length && y < b.length)
                b[y][x] = id;
        }

        return b;
    }
    
    public void findBestOrder(Board board) {
        List<Piece> pieces = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            Piece piece = new Piece();
            piece.config(Pentominoes.values()[i]);
            pieces.add(piece);
        }
        
        // not completed yet...
    }

    public static Piece[] bestOrder() {
        Piece[] bestOrderPieces = new Piece[12];

        Piece piece = new Piece();

        piece.config(Pentominoes.V);
        bestOrderPieces[0] = piece;

        piece = new Piece();
        piece.config(Pentominoes.Z);
        bestOrderPieces[1] = piece;

        piece = new Piece();
        piece.config(Pentominoes.I);
        bestOrderPieces[2] = piece;

        piece = new Piece();
        piece.config(Pentominoes.T);
        bestOrderPieces[3] = piece;

        piece = new Piece();
        piece.config(Pentominoes.W);
        bestOrderPieces[4] = piece;

        piece = new Piece();
        piece.config(Pentominoes.L);
        bestOrderPieces[5] = piece;

        piece = new Piece();
        piece.config(Pentominoes.F);
        bestOrderPieces[6] = piece;

        piece = new Piece();
        piece.config(Pentominoes.Y);
        bestOrderPieces[7] = piece;

        piece = new Piece();
        piece.config(Pentominoes.X);
        bestOrderPieces[8] = piece;

        piece = new Piece();
        piece.config(Pentominoes.N);
        bestOrderPieces[9] = piece;

        piece = new Piece();
        piece.config(Pentominoes.P);
        bestOrderPieces[10] = piece;

        piece = new Piece();
        piece.config(Pentominoes.U);
        bestOrderPieces[11] = piece;

        return bestOrderPieces;
    }

    @Override
    public String toString() {
        StringBuilder rep = new StringBuilder();
        rep.append(String.format("This Pentominoe Piece is: %s with %d rotations.\n", name, rotations));

        for (Point point : points) {
            rep.append(point).append(" ");
        }
        rep.append('\n');

        rep.append("*******************************************");

        return rep.toString();
    }
}
