package pentominoes;

import javafx.scene.paint.Color;

/**
 * This Enumeration represents the 12 pentominoe pieces with theirs characteristics.
 */
public enum Pentominoes {

    X('X', 0, 1, Color.THISTLE, new int[][]{{0, 1, 0}, {1, 1, 1}, {0, 1, 0}}),
    I('I', 1, 2, Color.LIGHTSKYBLUE, new int[][]{{1, 1, 1, 1, 1}}),
    Z('Z', 2, 2, Color.LIGHTSALMON, new int[][]{{0, 1, 1}, {0, 1, 0}, {1, 1, 0}}),
    T('T', 3, 4, Color.MEDIUMORCHID, new int[][]{{1, 1, 1}, {0, 1, 0}, {0, 1, 0}}),
    U('U', 4, 4, Color.AQUAMARINE, new int[][]{{1, 1}, {1, 0}, {1, 1}}),
    V('V', 5, 4, Color.SANDYBROWN, new int[][]{{1, 1, 1}, {1, 0, 0}, {1, 0, 0}}),
    W('W', 6, 4, Color.KHAKI, new int[][]{{0, 0, 1}, {0, 1, 1}, {1, 1, 0}}),
    Y('Y', 7, 4, Color.PALEGREEN, new int[][]{{1, 0}, {1, 1}, {1, 0}, {1, 0}}),
    L('L', 8, 4, Color.CHARTREUSE, new int[][]{{1, 0}, {1, 0}, {1, 0}, {1, 1}}),
    P('P', 9, 4, Color.CORAL, new int[][]{{0, 1}, {1, 1}, {1, 1}}),
    N('N', 10, 4, Color.GOLD, new int[][]{{1, 1, 0, 0}, {0, 1, 1, 1}}),
    F('F', 11, 4, Color.HOTPINK, new int[][]{{0, 1, 1}, {1, 1, 0}, {0, 1, 0}});

    private final int[][] representation;
    private final int rotations;
    private final Color color;
    private final char name;
    private final int id;

    Pentominoes(char name, int id, int rotations, Color color, int[][] representation) {
        this.name = name;
        this.rotations = rotations;
        this.color = color;
        this.representation = representation;
        this.id = id;
    }

    /**
     * @return Color of the piece.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return Number of rotations the piece has.
     */
    public int getRotations() {
        return rotations;
    }

    /**
     * @return Name of the piece.
     */
    public char getName() {
        return name;
    }

    /**
     * @return Matrix representation of the piece.
     */
    public int[][] getRepresentation() {
        return representation;
    }

    public int getId() {
        return id;
    }
}
