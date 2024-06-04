package sudokujava;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

class SudokuFileIO {
    static SudokuFile parse(File file) {
        try (Scanner input = new Scanner(file)) {
            input.useDelimiter(",");
            if (!input.next().equals("IMAGE")) {
                return new DataSudokuFile(file);
            }
            try {
                return new ImageSudokuFile(input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt(), input.nextInt());
            } catch (NoSuchElementException e) {
                return null;
            }
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    static void writeTiles(String path, byte[][] tiles) {
        File output = new File(path);
        try {
            if (output.createNewFile()) {
                System.out.println("File created: " + path);
            } else {
                System.out.println("File " + path + " already exists; overwriting");
            }
            FileWriter writer = new FileWriter(path);
            for (int i = 0; i < tiles.length; i++) {
                if (i != 0) writer.write('\n');
                for (byte b : tiles[i]) {
                    writer.write(Byte.toString(b));
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not create or write to file " + path);
        }
    }
}
