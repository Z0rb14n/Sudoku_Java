package cli;

import sudokujava.SolverSpeed;
import sudokujava.SudokuJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class SudokuCLI {
    private static final HashMap<String, Integer> usedArguments = new HashMap<>();
    private static final String INPUT_FILE_TAG = "-f";
    private static final String SPEED_TAG = "-s";
    private static final String IMG_COORD_TAG = "-i";
    private static final String DELAY_TAG = "-d";
    private static final String DEBUG_TAG = "--debug";
    private static final String OUTPUT_FILE_TAG = "-o";
    private static final String AUTOTYPE_TAG = "-a";

    static {
        usedArguments.put(INPUT_FILE_TAG, 1); // input file
        usedArguments.put(SPEED_TAG, 1); // speed
        usedArguments.put(IMG_COORD_TAG, 4); // image coordinates (top left X, top left Y, top left width, top left height)
        usedArguments.put(DELAY_TAG, 1); // delay
        usedArguments.put(DEBUG_TAG, 0); // whether debug stuff is printed??
        usedArguments.put(OUTPUT_FILE_TAG, 1); // output file
        usedArguments.put(AUTOTYPE_TAG, 0); // whether autotype is enabled
    }

    private static boolean initialHelpCases(String[] args) {
        if (args.length == 0) { // needs args
            printHelp();
            System.exit(1);
            return true;
        }
        if (args.length == 1) { // help
            printHelp();
            if (args[0].compareToIgnoreCase("help") == 0) {
                return true;
            }
            System.exit(1);
            return true;
        }
        return false;
    }

    private static HashMap<String, String[]> generateParameters(String[] args) {
        LinkedList<ArrayList<String>> parameters = new LinkedList<>();
        for (String str : args) {
            if (str.startsWith("-")) {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(str);
                parameters.add(temp);
            } else {
                parameters.getLast().add(str);
            }
        }
        HashMap<String, String[]> param = new HashMap<>();
        for (ArrayList<String> list : parameters) {
            if (list.size() > 1)
                param.put(list.get(0), list.subList(1, list.size()).toArray(new String[0]));
            else param.put(list.get(0), new String[0]);
        }
        return param;
    }

    private static SudokuJava generateImageSettings(HashMap<String, String[]> param, SolverSpeed speed) {
        boolean autoType = param.containsKey(AUTOTYPE_TAG);
        String[] img = param.get(IMG_COORD_TAG);
        try {
            int topLeftX = Integer.parseInt(img[0]);
            int topLeftY = Integer.parseInt(img[1]);
            int width = Integer.parseInt(img[2]);
            int height = Integer.parseInt(img[3]);
            int delay = 3000;
            if (param.containsKey(DELAY_TAG)) {
                delay = Integer.parseInt(param.get(DELAY_TAG)[0]);
                if (delay < 0) {
                    System.out.println("Delay < 0; received " + delay + "; clamping.");
                    delay = 0;
                }
            }
            return new SudokuJava(topLeftX, topLeftY, width, height, delay, autoType, speed);
        } catch (NumberFormatException ex) {
            throw new ArgumentException("Invalid number in arguments: ", ex);
        }
    }

    private static SudokuJava generateFileSettings(HashMap<String, String[]> param, SolverSpeed speed) {
        if (param.containsKey(AUTOTYPE_TAG)) {
            System.out.println("Unused argument: " + AUTOTYPE_TAG + "; autotype not available for file");
        }
        if (param.containsKey(DELAY_TAG)) {
            System.out.println("Unused argument " + DELAY_TAG + "; delay not applicable to file");
        }
        String file = param.get(INPUT_FILE_TAG)[0];
        return new SudokuJava(file, speed);
    }

    public static SudokuJava generateSettings(String[] args) {
        HashMap<String, String[]> param = generateParameters(args);
        for (String key : param.keySet()) {
            if (param.containsKey(key) && usedArguments.get(key) != param.get(key).length) {
                throw new ArgumentException("Argument " + key + " expects " + usedArguments.get(key) + " arguments but got " + param.get(key).length);
            }
            if (!param.containsKey(key)) {
                System.out.println("Unused argument: " + key);
            }
        }
        if (!param.containsKey(INPUT_FILE_TAG) && !param.containsKey(IMG_COORD_TAG)) {
            throw new ArgumentException("Must specify one of a file or image.");
        }
        if (param.containsKey(INPUT_FILE_TAG) && param.containsKey(IMG_COORD_TAG)) {
            throw new ArgumentException("Specified both image and file.");
        }
        SolverSpeed speed = SolverSpeed.REALLY_SLOW;
        if (param.containsKey(SPEED_TAG)) {
            SolverSpeed parsed = SolverSpeed.parseSpeed(param.get(SPEED_TAG)[0]);
            if (parsed != null) speed = parsed;
        }
        SudokuJava sudokuJava;
        if (param.containsKey(INPUT_FILE_TAG)) sudokuJava = generateFileSettings(param, speed);
        else sudokuJava = generateImageSettings(param, speed);
        sudokuJava.setCheckValidity(param.containsKey(DEBUG_TAG));
        if (param.containsKey(OUTPUT_FILE_TAG)) sudokuJava.setOutputFile(param.get(OUTPUT_FILE_TAG)[0]);
        return sudokuJava;
    }

    public static void main(String[] args) {
        if (initialHelpCases(args)) return;
        try {
            SudokuJava sudokuJava = generateSettings(args);
            sudokuJava.run();
        } catch (ArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("  -  sudoku -f <file>");
        System.out.println("  -  sudoku -i <top left X, Y, width, height> [-d delay] [-a]");
        System.out.println("Optional:");
        System.out.println("  -o <outputfile> sets output file");
        System.out.println("  --debug does verification checks");
        System.out.println("  -s <speed> sets the speed; defaults to REALLY_SLOW if not set");
    }

    private static class ArgumentException extends RuntimeException {
        public ArgumentException(String str) {
            super(str);
        }

        public ArgumentException(String msg, Throwable parent) {
            super(msg, parent);
        }
    }
}
