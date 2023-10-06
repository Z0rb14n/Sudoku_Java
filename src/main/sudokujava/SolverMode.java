package sudokujava;

import sudokujava.algorithm.General;
import util.SudokuScreenIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SolverMode {
    public static final String HIDE_NOT_FOUND_CANDIDATE_MSG = "HideNonFoundCandidates";
    public static final String SHOW_CANDIDATE_REMOVAL_MSG = "ShowCandidateRemoval";
    public static final String HIDE_NO_ALGORITHM_FOUND_MSG = "HideNoAlgorithmFound";
    public static final String PRINT_ONLY_ANSWERS = "PrintOnlyAnswers";
    public static final String AUTO_TYPE = "Autotype";
    public static final HashMap<String, Integer> FLAG_LOOKUP = new HashMap<>();
    public static final int HIDE_NOT_FOUND_CANDIDATE_MSG_INDEX = 0;
    public static final int SHOW_CANDIDATE_REMOVAL_MSG_INDEX = 1;
    public static final int HIDE_NO_ALGORITHM_FOUND_MSG_INDEX = 2;
    public static final int PRINT_ONLY_ANSWERS_INDEX = 3;
    public static final int AUTO_TYPE_INDEX = 4;
    public static SolverSpeed defaultSpeedParam = SolverSpeed.REALLY_SLOW;

    static {
        FLAG_LOOKUP.put(HIDE_NO_ALGORITHM_FOUND_MSG, HIDE_NO_ALGORITHM_FOUND_MSG_INDEX);
        FLAG_LOOKUP.put(SHOW_CANDIDATE_REMOVAL_MSG, SHOW_CANDIDATE_REMOVAL_MSG_INDEX);
        FLAG_LOOKUP.put(HIDE_NOT_FOUND_CANDIDATE_MSG, HIDE_NOT_FOUND_CANDIDATE_MSG_INDEX);
        FLAG_LOOKUP.put(PRINT_ONLY_ANSWERS, PRINT_ONLY_ANSWERS_INDEX);
        FLAG_LOOKUP.put(AUTO_TYPE, AUTO_TYPE_INDEX);
    }

    private SudokuScreenIO ssio;
    private InputType type;
    private byte[][] tiles;
    private boolean[] flags = new boolean[]{true, true, false, false, false};
    private int topLeftX = Integer.MIN_VALUE;
    private int topLeftY = Integer.MIN_VALUE;
    private int imageWidth;
    private int imageHeight;
    private int imageCaptureDelay = 0;

    private int imageTypeDelay = 0;
    private SolverSpeed solverSpeed = defaultSpeedParam;
    private String inputFile = null;
    /**
     * If false, only answers be printed
     */
    private String outputFile = null;

    private SolverMode() {
        ssio = new SudokuScreenIO();
    }

    public InputType getType() {
        return type;
    }

    public byte[][] getTiles() {
        return tiles;
    }

    public boolean[] getFlags() {
        return flags;
    }

    public boolean getFlag(String flag) {
        return flags[FLAG_LOOKUP.get(flag)];
    }

    public boolean setFlag(String flag, boolean value) {
        if (!FLAG_LOOKUP.containsKey(flag)) return false;
        flags[FLAG_LOOKUP.get(flag)] = value;
        return true;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public void setTopLeftX(int topLeftX) {
        this.topLeftX = topLeftX;
    }

    public void setTopLeftY(int topLeftY) {
        this.topLeftY = topLeftY;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getImageCaptureDelay() {
        return imageCaptureDelay;
    }

    public void setImageCaptureDelay(int imageCaptureDelay) {
        this.imageCaptureDelay = imageCaptureDelay;
    }

    public SolverSpeed getSolverSpeed() {
        return solverSpeed;
    }

    public void setSolverSpeed(SolverSpeed solverSpeed) {
        this.solverSpeed = solverSpeed;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public int getImageTypeDelay() {
        return imageTypeDelay;
    }

    public void setImageTypeDelay(int imageTypeDelay) {
        this.imageTypeDelay = imageTypeDelay;
    }

    public static SolverMode FromFile(String inputFile) {
        SolverMode mode = new SolverMode();
        mode.type = InputType.FILE;
        mode.inputFile = inputFile;
        return mode;
    }

    public static SolverMode FromImage(int topLeftX, int topLeftY, int imageWidth, int imageHeight) {
        SolverMode mode = new SolverMode();
        mode.type = InputType.FROM_IMAGE;
        mode.topLeftX = topLeftX;
        mode.topLeftY = topLeftY;
        mode.imageWidth = imageWidth;
        mode.imageHeight = imageHeight;
        return mode;
    }

    public static SolverMode FromData(byte[][] data) {
        SolverMode mode = new SolverMode();
        mode.type = InputType.DATA;
        mode.tiles = data;
        return mode;
    }

    public void getTileDataFromInput() {
        switch (type) {
            case DATA:
                return;
            case FILE:
                try (Scanner input = new Scanner(new File(inputFile))) {
                    tiles = new byte[9][9];
                    for (int i = 0; i < 9; i++) {
                        String line = input.nextLine().trim();
                        // TODO SPECIAL EXCEPTION
                        if (line.length() != 9)
                            throw new RuntimeException("File " + inputFile + " does not contain exactly 9 characters at line " + i + ": " + line);
                        for (int j = 0; j < line.length(); j++) {
                            if (!Character.isDigit(line.charAt(j)))
                                throw new RuntimeException("File " + inputFile + " has invalid characters at line " + i + ": " + line);
                            else tiles[i][j] = (byte) Character.digit(line.charAt(j), 10);
                        }
                    }
                } catch (FileNotFoundException | NoSuchElementException ex) {
                    throw new RuntimeException("File " + inputFile + " is invalid", ex);
                }
            case FROM_IMAGE:
                tiles = ssio.readTiles(this);
            default:
                throw new RuntimeException("InputType not defined");
        }
    }

    public void onFinish(byte[][] finishedTiles) {
        if (outputFile != null) {
            File output = new File(outputFile);
            try {
                if (output.createNewFile()) {
                    System.out.println("File created: " + outputFile);
                } else {
                    System.out.println("File " + outputFile + " already exists.");
                }
                FileWriter writer = new FileWriter(outputFile);
                for (byte[] tile : finishedTiles) {
                    for (byte b : tile) {
                        writer.write("" + b);
                    }
                    writer.write("\n");
                }
                writer.close();
            } catch (IOException e) {
                System.out.println("Could not create or write to file " + outputFile);
            }
        }
        if (flags[AUTO_TYPE_INDEX]) {
            ssio.typeValues(this, finishedTiles);
        }
        General.printTiles(finishedTiles);
    }

}
