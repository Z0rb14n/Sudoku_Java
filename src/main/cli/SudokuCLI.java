package cli;

import sudokujava.InputType;
import sudokujava.SolverMode;
import sudokujava.SolverSpeed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class SudokuCLI {
    /**
     * When file mode is enabled, data is retrieved from a file
     * Supported arguments:
     * - FILE_OUTPUT_PARAM
     * - IMAGE_BOUNDS_PARAM (in conjunction with AUTO_TYPE_PARAM, else ignored)
     * - AUTO_TYPE_PARAM (in conjunction with IMAGE_BOUNDS_PARAM)
     * - DEBUG_PRINT_PARAM
     * - SPEED PARAM
     * - IMAGE_OUTPUT_DELAY_PARAM (in conjunction with IMAGE_BOUNDS_PARAM and AUTO_TYPE_PARAM)
     * Unsupported arguments:
     * - FROM_IMAGE_PARAM
     * - DATA_INPUT_PARAM
     * - IMAGE_INPUT_DELAY_PARAM
     */
    private static final String FILE_INPUT_PARAM = "-f";
    /**
     * Specifies a file to print output data to
     */
    private static final String FILE_OUTPUT_PARAM = "-o";
    /**
     * Image coordinates:
     * top left X, top left Y, top left width, top left height
     * <p>
     * if FROM_IMAGE_PARAM selected, image is taken from this area
     * <p>
     * if AUTO_TYPE_PARAM selected, image will be entered into this area
     */
    private static final String IMAGE_BOUNDS_PARAM = "-i";
    private static final String FROM_IMAGE_PARAM = "-fi";
    private static final String DATA_INPUT_PARAM = "-d";
    /**
     * If present, auto type is enabled
     */
    private static final String AUTO_TYPE_PARAM = "-a";
    /**
     * If not present, debug info is not printed
     */
    private static final String DEBUG_PRINT_PARAM = "-p";
    private static final String SPEED_PARAM = "-s";
    private static final String IMAGE_INPUT_DELAY_PARAM = "-wi";
    private static final String IMAGE_OUTPUT_DELAY_PARAM = "-wo";
    private static HashMap<String, Integer> usedArguments = new HashMap<>();

    static {
        usedArguments.put(FILE_INPUT_PARAM, 1);
        usedArguments.put(SPEED_PARAM, 1);
        usedArguments.put(FROM_IMAGE_PARAM, 0);
        usedArguments.put(IMAGE_BOUNDS_PARAM, 4);
        usedArguments.put(IMAGE_INPUT_DELAY_PARAM, 1);
        usedArguments.put(IMAGE_OUTPUT_DELAY_PARAM, 1);
        usedArguments.put(DEBUG_PRINT_PARAM, 0);
        usedArguments.put(FILE_OUTPUT_PARAM, 1);
        usedArguments.put(AUTO_TYPE_PARAM, 0);
        usedArguments.put(DATA_INPUT_PARAM, 81);
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

    // Assumes param doesn't have duplicate input types
    private static InputType getInputType(HashMap<String, String[]> param) {
        if (param.containsKey(FILE_INPUT_PARAM)) return InputType.FILE;
        if (param.containsKey(DATA_INPUT_PARAM)) return InputType.DATA;
        if (param.containsKey(FROM_IMAGE_PARAM)) return InputType.FROM_IMAGE;
        throw new IllegalArgumentException("No input parameters (e.g. -'f'ile, -'i'mage, -'d'ata)");
    }

    private static void checkConflictingInputTypes(HashMap<String, String[]> param) {
        InputType type = getInputType(param);
        if (type == InputType.FILE) {
            if (param.containsKey(FROM_IMAGE_PARAM) || param.containsKey(DATA_INPUT_PARAM)) {
                throw new IllegalArgumentException("Multiple input parameters detected.");
            }
            if (param.containsKey(IMAGE_INPUT_DELAY_PARAM)) {
                System.out.println("Image input delay specified for file input. Skipping.");
            }
        } else if (type == InputType.FROM_IMAGE) {
            if (param.containsKey(DATA_INPUT_PARAM)) {
                throw new IllegalArgumentException("Multiple input parameters detected.");
            }
            if (!param.containsKey(IMAGE_BOUNDS_PARAM)) {
                throw new IllegalArgumentException("From image input type selected without image bounds");
            }

        } else { // DATA
            if (param.containsKey(IMAGE_INPUT_DELAY_PARAM)) {
                System.out.println("Image input delay specified for file input. Skipping.");
            }
        }
        if (type != InputType.FROM_IMAGE && !param.containsKey(AUTO_TYPE_PARAM) && param.containsKey(IMAGE_BOUNDS_PARAM))
            System.out.println("Image bounds provided when autotyping is disabled and input mode is not from image. Skipping.");
        if (!param.containsKey(AUTO_TYPE_PARAM) && param.containsKey(IMAGE_OUTPUT_DELAY_PARAM))
            System.out.println("Image output delay specified when autotype is disabled. Skipping.");
        if (param.containsKey(AUTO_TYPE_PARAM) && !param.containsKey(IMAGE_BOUNDS_PARAM))
            throw new IllegalArgumentException("Autotype enabled but image bounds not specified");
    }

    public static SolverMode generateSettings(String[] args) throws IllegalArgumentException {
        HashMap<String, String[]> param = generateParameters(args);
        for (String key : param.keySet()) {
            if (usedArguments.containsKey(key) && usedArguments.get(key) != param.get(key).length) {
                throw new IllegalArgumentException("Argument " + key + " expects " + usedArguments.get(key) + " arguments but got " + param.get(key).length);
            }
            if (!usedArguments.containsKey(key)) {
                System.out.println("Unused argument: " + key);
            }
        }
        checkConflictingInputTypes(param);

        InputType type = getInputType(param);
        SolverMode mode;
        int x = Integer.MIN_VALUE;
        int y = Integer.MIN_VALUE;
        int width = Integer.MIN_VALUE;
        int height = Integer.MIN_VALUE;
        if (param.containsKey(IMAGE_BOUNDS_PARAM)) {
            x = Integer.parseInt(param.get(IMAGE_BOUNDS_PARAM)[0]);
            y = Integer.parseInt(param.get(IMAGE_BOUNDS_PARAM)[1]);
            width = Integer.parseInt(param.get(IMAGE_BOUNDS_PARAM)[2]);
            height = Integer.parseInt(param.get(IMAGE_BOUNDS_PARAM)[3]);
        }
        if (type == InputType.FILE) {
            mode = SolverMode.FromFile(param.get(FILE_INPUT_PARAM)[0]);
        } else if (type == InputType.FROM_IMAGE) {
            mode = SolverMode.FromImage(x, y, width, height);
            if (param.containsKey(IMAGE_INPUT_DELAY_PARAM)) {
                mode.setImageCaptureDelay(Integer.parseInt(param.get(IMAGE_INPUT_DELAY_PARAM)[0]));
            }
        } else {
            String[] data = param.get(DATA_INPUT_PARAM);
            if (data.length != 81)
                throw new IllegalArgumentException("Data parameter is not of length 81: " + Arrays.toString(param.get(DATA_INPUT_PARAM)));
            byte[] intData = new byte[81];
            for (int i = 0; i < 81; i++) {
                intData[i] = Byte.parseByte(data[i]);
                if (intData[i] < 0 && intData[i] > 9)
                    throw new IllegalArgumentException("Data parameter out of range [0-9]: " + Arrays.toString(param.get(DATA_INPUT_PARAM)));
            }
            byte[][] formattedData = new byte[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    formattedData[i][j] = intData[i * 9 + j];
                }
            }
            mode = SolverMode.FromData(formattedData);
        }
        if (param.containsKey(IMAGE_BOUNDS_PARAM)) {
            mode.setTopLeftX(x);
            mode.setTopLeftY(y);
            mode.setImageWidth(width);
            mode.setImageHeight(height);
        }

        if (param.containsKey(FILE_OUTPUT_PARAM))
            mode.setOutputFile(param.get(FILE_OUTPUT_PARAM)[0]);

        if (param.containsKey(SPEED_PARAM)) {
            if (param.get(SPEED_PARAM)[0].length() != 1) {
                throw new IllegalArgumentException("SolverSpeed parameter length needs to be 1: " + param.get(SPEED_PARAM)[0]);
            }
            mode.setSolverSpeed(SolverSpeed.getSpeed(param.get(SPEED_PARAM)[0].charAt(0)));
            if (mode.getSolverSpeed() == null) {
                mode.setSolverSpeed(SolverMode.defaultSpeedParam);
                throw new IllegalArgumentException("SolverSpeed parameter has invalid character: " + param.get(SPEED_PARAM)[0]);
            }
        }

        mode.setFlag(SolverMode.AUTO_TYPE, param.containsKey(AUTO_TYPE_PARAM));
        mode.setFlag(SolverMode.PRINT_ONLY_ANSWERS, param.containsKey(DEBUG_PRINT_PARAM));
        if (param.containsKey(IMAGE_OUTPUT_DELAY_PARAM))
            mode.setImageTypeDelay(Integer.parseInt(param.get(IMAGE_OUTPUT_DELAY_PARAM)[0]));

        return mode;
    }

    public static void main(String[] args) {
        if (initialHelpCases(args)) return;
        try {
            SolverMode settings = generateSettings(args);

            throw new UnsupportedOperationException("Not implemented yet.");
        } catch (IllegalArgumentException ex) {
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
}
