package sudokujava.algorithm;

import java.util.ArrayList;

public class CandidateGeneration {
    public static void generate(byte[][] tiles, ArrayList<Byte>[][] candidates) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (tiles[i][j] != 0) {
                    candidates[i][j] = new ArrayList<>(0);
                    continue;
                }
                ArrayList<Byte> temp = new ArrayList<>();
                byte[] row = tiles[i];
                byte[] col = General.findColumn(tiles, j + 1);
                byte[] square = General.findSquare(tiles, General.findSquareNum(i + 1, j + 1));
                for (byte kek = 1; kek < 10; kek++) {
                    if (!General.arrayContains(row, kek) && !General.arrayContains(col, kek) && !General.arrayContains(square, kek)) {
                        temp.add(kek);
                    }
                }
                candidates[i][j] = temp;
            }
        }
    }
}
