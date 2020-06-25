package sudokujava;

import sudokujava.SolverMode.Speed;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static sudokujava.SudokuJava.INPUT_FILE;

class SudokuFileParser {
    static SolverMode parse() {
        SolverMode result = attemptParseAsImageFile();
        if (result == null) return attemptParseAsTextFile();
        else return result;
    }

    private static SolverMode attemptParseAsImageFile() {
        File file = new File(INPUT_FILE);
        try (Scanner input = new Scanner(file)) {
            input.useDelimiter(",");
            if (!input.next().equals("IMAGE")) {
                return null;
            }
            try {
                return new SolverMode(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
            } catch (IllegalArgumentException e) {
                return null;
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private static SolverMode attemptParseAsTextFile() {
        File file = new File(INPUT_FILE);
        try (Scanner input = new Scanner(file)) {
            byte[][] tiles = new byte[9][9];
            String headerLine = input.nextLine().trim();
            if (headerLine.length() != 5) return null;
            boolean[] bools = new boolean[4];
            for (int i = 0; i < 4; i++) {
                if (headerLine.charAt(i) == '1') bools[i] = true;
                else if (headerLine.charAt(i) == '0') bools[i] = false;
                else return null;
            }
            Speed speed = Speed.getSpeed(headerLine.charAt(4));
            for (int i = 0; i < 9; i++) {
                String line = input.nextLine().trim();
                if (line.length() != 9) return null;
                for (int j = 0; j < line.length(); j++) {
                    if (!Character.isDigit(line.charAt(j))) return null;
                    else tiles[i][j] = (byte) Character.digit(line.charAt(j), 10);
                }
            }
            return new SolverMode(tiles, bools[0], bools[1], bools[2], bools[3], speed);
        } catch (FileNotFoundException | NoSuchElementException ex) {
            return null;
        }
    }
}
