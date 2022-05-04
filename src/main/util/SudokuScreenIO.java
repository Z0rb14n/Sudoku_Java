package util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import sudokujava.OCRException;
import sudokujava.SolverMode;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SudokuScreenIO {
    private static final ITesseract instance = new Tesseract();
    public static int imageOffset = 2;
    private RobotWrapper bot;

    public SudokuScreenIO() {
        try {
            bot = new RobotWrapper();
        } catch (java.awt.AWTException e) {
            System.err.println("Could not initialize robot.");
            e.printStackTrace();
            throw new OCRException();
        }
        instance.setDatapath("tessdata");
    }

    public void typeValues(SolverMode mode, byte[][] tiles) {
        final int width = Math.floorDiv(mode.imageWidth, 9);
        final int halfWidth = Math.floorDiv(width, 2);
        final int height = Math.floorDiv(mode.imageHeight, 9);
        final int halfHeight = Math.floorDiv(height, 2);
        int x = mode.getTopLeftX() + halfWidth;
        int y = mode.getTopLeftY() + halfHeight;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                bot.mouseMove(x + (j * width), y + (i * height));
                bot.button1();
                bot.delay(100);
                bot.simplerType("" + tiles[i][j]);
            }
        }
    }

    public byte[][] readTiles(SolverMode mode) {
        BufferedImage[][] lol = new BufferedImage[9][9];
        byte[][] tiles = new byte[9][9];
        int x = mode.getTopLeftX();
        int y = mode.getTopLeftY();
        bot.mouseMove(x, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y);
        bot.delay(200);
        bot.mouseMove(x + mode.imageWidth, y + mode.imageHeight);
        bot.delay(200);
        bot.mouseMove(x, y + mode.imageHeight);
        int width = Math.floorDiv(mode.imageWidth, 9);
        int height = Math.floorDiv(mode.imageHeight, 9);
        BufferedImage bigBoi = bot.screenShot(x + imageOffset, y + imageOffset, mode.imageWidth - (2 * imageOffset), mode.imageHeight - (2 * imageOffset));
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                lol[i][j] = bigBoi.getSubimage((width * j), (height * i), width - (2 * imageOffset), height - (2 * imageOffset));
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    String result = instance.doOCR(lol[i][j]).trim();
                    result = result.replaceAll("([\\s]+|[\\n]+|[|]+)", "");
                    if (result.isEmpty()) {
                        System.out.print("Blank char.");
                        tiles[i][j] = 0;
                        continue;
                    }
                    try {
                        tiles[i][j] = Byte.parseByte(result);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid characters in OCR: " + result + ", length: " + result.length());
                        Toolkit.getDefaultToolkit().beep();
                        throw new OCRException();
                    }
                    System.out.println(result);
                } catch (TesseractException e) {
                    e.printStackTrace();
                    throw new OCRException();
                }
            }
        }
        return tiles;
    }

}
