package sudokujava.algorithm;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneralTest {
    @Test
    public void testFindSquareNum() {
        for (int row = 1; row < 10; row++) {
            for (int col = 1; col < 10; col++) {
                int result;
                if (row < 4 && col < 4) {
                    result = 1;
                } else if (row < 4 && col < 7) {
                    result = 2;
                } else if (row < 4) {
                    result = 3;
                } else if (row < 7 && col < 4) {
                    result = 4;
                } else if (row < 7 && col < 7) {
                    result = 5;
                } else if (row < 7) {
                    result = 6;
                } else if (col < 4) {
                    result = 7;
                } else if (col < 7) {
                    result = 8;
                } else {
                    result = 9;
                }
                assertEquals(result, General.findSquareNum(row, col));
            }
        }
    }
}
