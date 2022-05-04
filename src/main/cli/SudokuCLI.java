package cli;

import sudokujava.SolverMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class SudokuCLI {
    private static HashMap<String, Integer> usedArguments = new HashMap<>();

    static {
        usedArguments.put("-f", 1); // input file
        usedArguments.put("-s", 1); // speed
        usedArguments.put("-i", 4); // image coordinates (top left X, top left Y, top left width, top left height)
        usedArguments.put("-d", 1); // delay
        usedArguments.put("-p", 0); // whether debug stuff is printed??
        usedArguments.put("-o", 1); // output file
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

    public static CLISettings generateSettings(String[] args) {
        HashMap<String, String[]> param = generateParameters(args);
        for (String key : param.keySet()) {
            if (usedArguments.containsKey(key) && usedArguments.get(key) != param.get(key).length) {
                throw new ArgumentException("Argument " + key + " expects " + usedArguments.get(key) + " arguments but got " + param.get(key).length);
            }
            if (!usedArguments.containsKey(key)) {
                System.out.println("Unused argument: " + key);
            }
        }
        CLISettings settings = new CLISettings();
        settings.inputFile = param.containsKey("-f") ? param.get("-f")[0] : null;
        settings.debugPrinted = param.containsKey("-p");
        settings.outputFile = param.containsKey("-o") ? param.get("-o")[0] : null;
        try {
            settings.imgDelay = param.containsKey("-d") ? Integer.parseInt(param.get("-d")[0]) : CLISettings.DEFAULT_INT_VALUE;
        } catch (NumberFormatException ex) {
            throw new ArgumentException("Argument image delay expected integer number: " + param.get("-d")[0]);
        }
        if (param.containsKey("-s")) {
            if (param.get("-s")[0].length() != 1) {
                throw new ArgumentException("Argument speed requires length 1: " + param.get("-s")[0]);
            }
            settings.speed = SolverMode.Speed.getSpeed(param.get("-s")[0].charAt(0));
        }
        if (param.containsKey("-i")) {
            try {
                settings.topLeftX = Integer.parseInt(param.get("-i")[0]);
                settings.topLeftY = Integer.parseInt(param.get("-i")[1]);
                settings.imgWidth = Integer.parseInt(param.get("-i")[2]);
                settings.imgHeight = Integer.parseInt(param.get("-i")[3]);
            } catch (NumberFormatException ex) {
                throw new ArgumentException("Argument image section expected integer number: " + Arrays.toString(param.get("-i")));
            }
        }
        return settings;
    }

    public static void main(String[] args) {
        if (initialHelpCases(args)) return;
        try {
            CLISettings settings = generateSettings(args);
        } catch (ArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(-1);
        } catch (Exception ex) {
            System.err.println(ex);
            System.exit(-1);
        }
    }

    private static void printHelp() {
        System.out.println("Usage:");
        System.out.println("  -  sudoku -f <file> -s <speed>");
        System.out.println("  -  sudoku -i <top left X, Y, width, height> -s <speed> -d delay");
    }

    private static class ArgumentException extends RuntimeException {
        public ArgumentException(String str) {
            super(str);
        }

        public ArgumentException() {
            super();
        }
    }
}
