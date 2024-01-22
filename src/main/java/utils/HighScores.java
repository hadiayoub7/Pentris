package utils;

import java.io.*;
import java.util.*;

public class HighScores {

    private final static String FILE_PATH = "src/main/resources/highscores.txt";

    public List<String> getAllScoresAsString() {
        return turnIntoStringList(getAllScores());
    }

    public void addNewScore(String username, int highscore) {
        List<Score> scores = getAllScores();
        scores.add(new Score(username, highscore));
        writeScores(scores);
    }

    public List<Score> getAllScores() {
        List<Score> scores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null && !(line.isEmpty())) {
                String[] parts = line.split(" ");
                scores.add(new Score(parts[0], Integer.parseInt(parts[1])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.sort(scores); 
        return scores;
    }

    private List<String> turnIntoStringList(List<Score> scores) {
        List<String> stringList = new LinkedList<>();
        for (Score score : scores) {
            stringList.add(score.toString());
        }
        return stringList;
    }

    public List<Score> getAllScores(String name) {
        List<Score> scores = getAllScores();
        scores = scores.stream().filter(score -> score.getUsername().toLowerCase().startsWith(name.toLowerCase()))
                .toList();
        return scores;
    }

    private void writeScores(List<Score> scores) {
        Collections.sort(scores);
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Score score : scores) {
                pw.println(score);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
