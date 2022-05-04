package cli;

import sudokujava.SolverMode;

import java.util.ArrayList;
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
        usedArguments.put("-a", 0); // whether autotype is enabled
        // TODO MORE ARGUMENTS
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

    public static SolverMode generateSettings(String[] args) {
        HashMap<String, String[]> param = generateParameters(args);
        for (String key : param.keySet()) {
            if (usedArguments.containsKey(key) && usedArguments.get(key) != param.get(key).length) {
                throw new ArgumentException("Argument " + key + " expects " + usedArguments.get(key) + " arguments but got " + param.get(key).length);
            }
            if (!usedArguments.containsKey(key)) {
                System.out.println("Unused argument: " + key);
            }
        }
        throw new UnsupportedOperationException("Not implemented yet.");
    }

    public static void main(String[] args) {
        if (initialHelpCases(args)) return;
        try {
            SolverMode settings = generateSettings(args);

            throw new UnsupportedOperationException("Not implemented yet.");
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
