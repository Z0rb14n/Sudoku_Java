package util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageIOHelper;
import sudokujava.SolverMode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SudokuScreenIO {
    private static final ITesseract instance = new Tesseract();
    public static int imageOffset = 2;
    private final RobotWrapper bot;

    public boolean printReadCharacter = false;
    public int delayBetweenClicksMs = 100;
    public int delayShowBoxMs = 200;

    public SudokuScreenIO() {
        try {
            bot = new RobotWrapper();
        } catch (java.awt.AWTException e) {
            throw new OCRException("Could not initialize robot", e);
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
                bot.delay(delayBetweenClicksMs);
                bot.simplerType(Byte.toString(tiles[i][j]));
            }
        }
    }

    public byte[][] readTiles(SolverMode mode) {
        byte[][] tiles = new byte[9][9];
        int x = mode.getTopLeftX();
        int y = mode.getTopLeftY();
        bot.mouseMove(x, y);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(x + mode.imageWidth, y);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(x + mode.imageWidth, y + mode.imageHeight);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(x, y + mode.imageHeight);
        int width = Math.floorDiv(mode.imageWidth, 9);
        int height = Math.floorDiv(mode.imageHeight, 9);
        int actualImageWidth = mode.imageWidth - (2 * imageOffset);
        int actualImageHeight = mode.imageHeight - (2 * imageOffset);
        BufferedImage puzzle = bot.screenShot(x + imageOffset, y + imageOffset, actualImageWidth, actualImageHeight);
        ByteBuffer imgData;
        int pixelSize = puzzle.getColorModel().getPixelSize();
        try {
            imgData = ImageIOHelper.getImageByteBuffer(puzzle);
        } catch (IOException ex) {
            throw new OCRException("Failed to create image buffer", ex);
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                try {
                    Rectangle rect = new Rectangle(width * j, height * i, width - (2 * imageOffset), height - (2 * imageOffset));
                    String result = instance.doOCR(actualImageWidth, actualImageHeight, imgData, rect, pixelSize);
                    result = result.replaceAll("\\s+|\\n+|\\|+", "");
                    if (printReadCharacter) System.out.println(result);
                    if (!result.isEmpty()) {
                        try {
                            tiles[i][j] = Byte.parseByte(result);
                        } catch (NumberFormatException e) {
                            throw new OCRException("Invalid characters in OCR: " + result);
                        }
                    }
                } catch (TesseractException e) {
                    throw new OCRException(e);
                }
            }
        }
        return tiles;
    }

}
