package utils;

public class Score implements Comparable<Score> {
    private String username;
    private int score;

    public Score(String username, int score) {
        this.username = username;
        this.score = score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return username + " " + score;
    }

}
