/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokujava;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Scanner;
import sudokujava.SolverMode.Speed;
import util.ParsingException;

/**
 *
 * @author adminasaurus
 */
public class SudokuFileParser {

    public final static String INPUT_FILE = SudokuJava.INPUT_FILE;
    public final static String OUTPUT_FILE = SudokuJava.OUTPUT_FILE;

    public static SolverMode parse() throws ParsingException {
        try {
            return attemptParseAsImageFile();
        } catch (FileNotFoundException e) {
            throw new ParsingException();
        } catch (ParsingException e) {
            try {
                return attemptParseAsTextFile();
            } catch (FileNotFoundException ex) {
                throw new ParsingException();
            }
        }
    }

    private static SolverMode attemptParseAsImageFile() throws ParsingException, FileNotFoundException {
        File file = new File(INPUT_FILE);
        Scanner input = new Scanner(file);
        input.useDelimiter(",");
        try {
            if (!input.next().equals("IMAGE")) {
                throw new ParsingException("Invalid image file.");
            }
            int c = input.nextInt();
            int d = input.nextInt();
            if (c < 0 || d < 0) {
                throw new ParsingException("Invalid image file.");
            }
            int imageWidth = input.nextInt();
            int imageHeight = input.nextInt();
            int imageCaptureDelay = input.nextInt();
            if (imageWidth <= 0 || imageHeight <= 0 || imageCaptureDelay < 0) {
                throw new ParsingException("Invalid image file.");
            }
            return new SolverMode(c, d, imageWidth, imageHeight, imageCaptureDelay);
        } catch (NoSuchElementException e) {
            throw new ParsingException("Invalid image file.");
        } finally {
            input.close();
        }
    }

    private static SolverMode attemptParseAsTextFile() throws ParsingException, FileNotFoundException {
        File file = new File(INPUT_FILE);
        Scanner input = new Scanner(file);
        byte[][] tiles = new byte[9][9];
        Speed speed;
        if (!input.hasNextLine()) {
            throw new ParsingException("Empty file.");
        }
        String headerLine = input.nextLine();
        if (headerLine.length() != 5) {
            throw new ParsingException("Too many characters in header.");
        }
        boolean[] bools = new boolean[4];
        for (int i = 0; i < 4; i++) {
            if (headerLine.charAt(i) == '1') {
                bools[i] = true;
            } else if (headerLine.charAt(i) == '0') {
                bools[i] = false;
            } else {
                throw new ParsingException("Invalid characters in header.");
            }
        }
        speed = Speed.getSpeed(headerLine.charAt(4));
        if (speed == null) {
            throw new ParsingException("Invalid character in header.");
        }
        for (int i = 0; i < 9; i++) {
            if (i != 8 && !input.hasNextLine()) {
                throw new ParsingException("Body is too short.");
            }
            String line = input.nextLine();
            if (line.length() != 9) {
                throw new ParsingException("Invalid length of body.");
            }
            for (int j = 0; j < line.length(); j++) {
                if (!Character.isDigit(line.charAt(j))) {
                    throw new ParsingException("Invalid character in body.");
                } else {
                    tiles[i][j] = (byte) (line.charAt(j) - '0');
                }
            }
        }
        return new SolverMode(tiles,bools[0],bools[1],bools[2],bools[3],speed.characterRepresentation());
    }
}
