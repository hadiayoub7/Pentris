package utils;

import gui.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents the coordinate (x, y) of each square of each piece.
 */
public class Point implements Comparable<Point>, Cloneable {
    private int x;
    private int y;


    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Point clone() throws CloneNotSupportedException {
        return (Point) super.clone();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }


    /**
     * @param b Matrix to be converted into points
     * @return List of points.
     */
    public static List<Point> array2points(int[][] b) {
        ArrayList<Point> points = new ArrayList<>();

        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b[i].length; j++) {
                if (b[i][j] > 0)
                    points.add(new Point(j, i)); // add point at this coordinates
            }
        }

        return points;
    }


    public static int getMaxPointX(List<Point> points) {
        int xMax = 0;
        for (Point point : points) {
            int x = point.getX();
            xMax = Math.max(xMax, x);
        }

        return xMax;
    }

    public static int getMaxPointY(List<Point> points) {
        int yMax = 0;
        for (Point point : points) {
            int y = point.getY();
            yMax = Math.max(yMax, y);
        }

        return yMax;
    }

    public static int getMinPointX(List<Point> points) {
        int xMin = Game.WIDTH + 1;
        for (Point point : points) {
            int x = point.getX();
            xMin = Math.min(xMin, x);
        }

        return xMin;
    }

    public static int getMinPointY(List<Point> points) {
        int yMin = Game.HEIGHT + 1;
        for (Point point : points) {
            int y = point.getY();
            yMin = Math.min(yMin, y);
        }

        return yMin;
    }

    public static Point getMinPoints(List<Point> points) {
        Point p = new Point(Game.WIDTH, Game.HEIGHT);
        for (Point point : points) {
            int minX = Math.min(p.getX(), point.getX());
            int minY = Math.min(p.getY(), point.getY());

            p = new Point(minX, minY);
        }

        return p;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public int compareTo(Point point) {
        if (this.y == point.y)
            return this.x - point.x;
        return this.y - point.y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
