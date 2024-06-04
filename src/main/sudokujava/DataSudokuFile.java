package sudokujava;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class DataSudokuFile implements SudokuFile {
    private final File file;

    public DataSudokuFile(File file) {
        this.file = file;
    }

    @Override
    public byte[][] getTiles() {
        try (Scanner input = new Scanner(file)) {
            byte[][] tiles = new byte[9][9];
            for (int i = 0; i < 9; i++) {
                if (!input.hasNext()) return null;
                String line = input.nextLine().trim();
                if (line.length() != 9) return null;
                for (int j = 0; j < line.length(); j++) {
                    if (!Character.isDigit(line.charAt(j))) return null;
                    else tiles[i][j] = (byte) Character.digit(line.charAt(j), 10);
                }
            }
            return tiles;
        } catch (FileNotFoundException | NoSuchElementException ex) {
            return null;
        }
    }
}
