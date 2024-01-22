package ai;

import gui.Game;
import utils.GameState;
import utils.Point;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class GBot {
    private int populationSize;
    private int tournamentSize;
    private int parentNum;
    private int gamesNum;
    private int currentGame;
    private double[][] genomes;
    private double[][] top10;
    private double mutationRate;
    private double mutationStep;
    private double mutationRate1;
    private double mutationStep1;
    private BufferedWriter writer;
    private File file;
    private List<Integer> scores;
    private final Game game;
    private int[] bestNextMoves;
    private int currentGenome;
    private int generation;

    public GBot(Game game) {
        populationSize = 100;
        tournamentSize = 5;
        parentNum = 10;
        gamesNum = 30;
        currentGame = -1;
        genomes = new double[populationSize][parentNum];
        Play();
        top10 = new double[10][parentNum];
        mutationRate = 0.1;
        mutationStep = 0.01;
        mutationRate1 = 0.05;
        mutationStep1 = 3;
        currentGenome = 0;
        scores = new ArrayList<>();
        this.game = game;
    }

    private void populate() {
        for (int i = 0; i < populationSize; i++) {
            double[] gen = {Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5,
                    Math.random() - 0.5, Math.random() * 0.5, Math.random() - 0.5, Math.random() - 0.5, 0};

            genomes[i] = gen;

            if (i < top10.length)
                top10[i] = gen.clone();
        }
    }

    private void Play() {
        double[] gen1 = {14.928740819414838, 0.22815685282185916, -0.40505060750400884, -1.3945551853055056, -5.415407771851985
                , -1.5420017356444644, 1.0560452657245811, 4740.0}; //best individual
        genomes[0] = gen1;
        currentGenome = 0;
//        tryGame();
    }

    public void train() {
//        System.out.println("train()");

        LocalDate currentDate = LocalDate.now();

        String fileName = String.format("Top-Individual-%s.txt", currentDate);
//        String path = Objects.requireNonNull(getClass().getResource(String.format("/Bot/%s", fileName))).toExternalForm();

        file = new File(fileName);
        populate();

        while (currentGame != 0) {
            System.out.println(generation);
            for (double[] genome : genomes) {
                System.out.println(Arrays.toString(genome));
            }
            System.out.println();

            evaluatePopulation();
            getNextGene();
//            System.out.println();
        }
    }

    private void updateTop10() {
//        System.out.println("updateTop10");

        for (int i = 0; i < top10.length; i++) {
            if (genomes[0][7] > top10[i][7]) {
                top10[top10.length - 1] = genomes[0].clone();
                break;
            }
        }

        Arrays.sort(top10, (a, b) -> Double.compare(b[7], a[7]));

//        for (double[] doubles : top10) System.out.println(Arrays.toString(doubles));
    }

    private void evaluatePopulation() {
//        System.out.println("evaluatePopulation()");
        GameState game = new GameState(this.game);
        for (int i = 0; i < populationSize; i++) {
//            System.out.println("-");
            game.reload(this.game);
            currentGenome = i;
            tryGame();
        }

//        System.out.println();
    }

    private void getNextGene() {
//        System.out.println("getNextGene()");
        generation++;
        Arrays.sort(genomes, (a, b) -> Double.compare(b[7], a[7]));

        updateTop10();

        try {
            writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(Arrays.toString(genomes[0]) + "\n");
            writer.close();
//            System.out.println("Saved to file!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        double[][] tmpGene = new double[populationSize][parentNum];
        int elites = genomes.length / 50;
//        System.arraycopy(genomes, 0, tmpGene, 0, elites);

        for (int i = elites; i < populationSize; i++) {
            int[] parents = tournamentSelection(tournamentSize);
            tmpGene[i] = mate(parents[0], parents[1]);
        }

        genomes = tmpGene;
    }

    private int[] tournamentSelection(int tournamentSize) {
//        System.out.println("tournamentSelection()");
        int[] parents = {0, 0};
        double bestScore = 0;
        int bestPopulation = 0;
        int randomPopulation;

        for (int i = 0; i < tournamentSize; i++) {
            randomPopulation = (int) (Math.random() * tournamentSize);
            if (genomes[randomPopulation][7] > bestScore) {
                bestScore = genomes[randomPopulation][7];
                bestPopulation = randomPopulation;
            }
        }

        parents[0] = bestPopulation;
        bestScore = 0;
        bestPopulation = 0;

        for (int i = 0; i < tournamentSize; i++) {
            randomPopulation = (int) (Math.random() * tournamentSize);
            if (genomes[randomPopulation][7] > bestScore) {
                bestScore = genomes[randomPopulation][7];
                bestPopulation = randomPopulation;
            }
        }

        parents[1] = bestPopulation;

        return parents;
    }

    private double[] mate(int dad, int mom) {
//        System.out.println("mate()");

        int crossover = (int) (Math.random() * 6);
        double[] child = new double[parentNum];

        for (int i = 0; i < crossover; i++) {
            child[i] = genomes[dad][i];
        }

        child[parentNum - 1] = 0;

        for (int i = 0; i < child.length - 1; i++) {
            if (Math.random() < mutationRate) { // get mutation from dad/mom.
                double mut = Math.random() * mutationStep * 2.0;
                child[i] = child[i] + mut - mutationStep;
            }

            if (Math.random() < mutationRate1) { // get mutation from dad/mom.
                double mut = Math.random() * mutationStep1 * 2.0;
                child[i] = child[i] + mut - mutationStep1;
            }

            if (Math.random() < mutationRate) { // get mutation from dad & mom.
                child[i] = (genomes[mom][i] + genomes[dad][i]) / 2;
            }
        }
        return child;
    }

    private void tryGame() {
//        System.out.println("tryGame()");

        GameState game = new GameState(this.game);
        int score = 0;
        currentGame = 0;
        while (currentGame < gamesNum) {
            
            int[] bestMove = getBestMove();
            game.reload(this.game);

            game.piece.rotateRight(bestMove[0]);

            // move it left.
            for (int i = 0; i < Game.WIDTH; i++) {
                if (game.piece.isTranslateValid(game.board.getB(), new Point(-1, 0)))
                    game.piece.translate(new Point(-1, 0));
            }

            // move it right.
            for (int i = 0; i < bestMove[1]; i++) {
                if (game.piece.isTranslateValid(game.board.getB(), new Point(1, 0))) {
                    game.piece.translate(new Point(1, 0));
                }
            }

            game.piece.drop(game.board, true);
            score += game.checkLinesCleared();

            System.out.println(game.board);
            System.out.println("*".repeat(50));
            try {
                Thread.sleep(Duration.ofMillis(1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (game.isGameOver()) {
                scores.add(score);
                score = 0;
                currentGame++;
            }

            if (!scores.isEmpty()) {
                int sum = scores.stream().mapToInt(Integer::intValue).sum();
                double avg = (double) sum / scores.size();
                genomes[currentGenome][7] = avg;
                scores = new ArrayList<>();
            }
        }

        currentGame = 0;
        bestNextMoves = new int[3];
    }


    public int[] getBestMove() {

        GameState original = new GameState(game);

        List<int[]> possibleMoves = new ArrayList<>();
        int[] move = new int[7]; // rotation, translation, rating.
        double[] algorithm = new double[6];

        for (int i = 0; i < original.piece.getRotations(); i++) {
            original.reload(game);
            for (int j = 0; j < Game.WIDTH; j++) {
                original.reload(game);

                original.piece.rotateRight(i); // rotate right i-times.

                boolean moved = true;

                // move left after being rotated.
                for (int k = 0; k < Game.WIDTH; k++) {
                    if (original.piece.isTranslateValid(original.board.getB(), new Point(-1, 0)))
                        original.piece.translate(new Point(-1, 0));
                }

                // move right.
                for (int k = 0; k < j; k++) {
                    if (original.piece.isTranslateValid(original.board.getB(), new Point(1, 0))) {
                        original.piece.translate(new Point(1, 0));
                    } else {
                        moved = false;
                        break;
                    }
                }

                if (!moved)
                    break;

                int rating1 = 0;
                original.piece.drop(original.board, true);
                int clearedRows = original.checkLinesCleared();

                if (original.isGameOver()) {
                    rating1 -= 650;
                    clearedRows = 0;
                }

                algorithm[0] = clearedRows;
                algorithm[1] = Math.pow(original.board.aggregateHeight(), 1.5);
                algorithm[2] = original.board.getHeight();
                algorithm[3] = original.board.relativeHeight();
                algorithm[4] = original.board.countHoles();
                algorithm[5] = original.board.bumpiness();

                rating1 += (int) (algorithm[0] * genomes[0][0]);
                rating1 += (int) (algorithm[1] * genomes[0][1]);
                rating1 += (int) (algorithm[2] * genomes[0][2]);
                rating1 += (int) (algorithm[3] * genomes[0][3]);
                rating1 += (int) (algorithm[4] * genomes[0][4]);
                rating1 += (int) (algorithm[5] * genomes[0][5]);

                if (bestNextMoves != null && i == bestNextMoves[0] && j == bestNextMoves[1]) {
                    rating1 *= (int) genomes[0][6];
                }

                // validate next move
                GameState original2 = original.copy();

                for (int k = 0; k < game.getNextPiece().getRotations(); k++) {
                    for (int l = 0; l < Game.WIDTH; l++) {
                        original.board = original2.board.copy();
                        original.score = original2.score;
                        original.piece = original2.nextPiece.copy();

                        moved = true;

                        original.piece.rotateRight(k); // rotate k-times.

                        // move left
                        for (int m = 0; m < Game.WIDTH; m++) {
                            if (original.piece.isTranslateValid(original.board.getB(), new Point(-1, 0)))
                                original.piece.translate(new Point(-1, 0));
                        }

                        // move right
                        for (int m = 0; m < l; m++) {
                            if (original.piece.isTranslateValid(original.board.getB(), new Point(1, 0)))
                                original.piece.translate(new Point(1, 0));
                            else {
                                moved = false;
                                break;
                            }
                        }

                        if (!moved) {
                            break;
                        }

                        int rating2 = 0;
                        original.piece.drop(original.board, true);
                        clearedRows = original.checkLinesCleared();

                        if (original.isGameOver()) {
                            rating2 -= 650;
                            clearedRows = 0;
                        }
                        
                        algorithm[0] = clearedRows;
                        algorithm[1] = Math.pow(original.board.aggregateHeight(), 1.5);
                        algorithm[2] = original.board.getHeight();
                        algorithm[3] = original.board.relativeHeight();
                        algorithm[4] = original.board.countHoles();
                        algorithm[5] = original.board.bumpiness();
                        
                        rating2 += (int) (algorithm[0] * genomes[0][0]);
                        rating2 += (int) (algorithm[1] * genomes[0][1]);
                        rating2 += (int) (algorithm[2] * genomes[0][2]);
                        rating2 += (int) (algorithm[3] * genomes[0][3]);
                        rating2 += (int) (algorithm[4] * genomes[0][4]);
                        rating2 += (int) (algorithm[5] * genomes[0][5]);


                        move[0] = i;
                        move[1] = j;
                        move[2] = k;
                        move[3] = l;
                        move[4] = rating1;
                        move[5] = rating2;
                        move[6] = rating1 + rating2;
                        
                        possibleMoves.add(move.clone());
                    }
                }
            }
        }

        int mxRating = -10000;
        int mxMove = 0;
        for (int i = 0; i < possibleMoves.size(); i++) {
            if (possibleMoves.get(i)[6] > mxRating) {
                mxRating = possibleMoves.get(i)[6];
                mxMove = i;
            }
        }

        int[] bestMove = new int[3];
        bestMove[0] = possibleMoves.get(mxMove)[0];
        bestMove[1] = possibleMoves.get(mxMove)[1];
        bestMove[2] = possibleMoves.get(mxMove)[6];
        
        bestNextMoves = new int[3];
        bestNextMoves[0] = possibleMoves.get(mxMove)[2];
        bestNextMoves[1] = possibleMoves.get(mxMove)[3];
        bestNextMoves[2] = possibleMoves.get(mxMove)[5];

        return bestMove;
    }
}
