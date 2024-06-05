package util;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.ImageIOHelper;
import sudokujava.ImageSudokuFile;

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
        instance.setDatapath("./tessdata");
        instance.setTessVariable("tessedit_char_whitelist", "0123456789");
    }

    public void typeValues(ImageSudokuFile mode, byte[][] tiles) {
        final int width = Math.floorDiv(mode.getImageWidth(), 9);
        final int halfWidth = Math.floorDiv(width, 2);
        final int height = Math.floorDiv(mode.getImageHeight(), 9);
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

    public byte[][] readTiles(int topLeftX,
                              int topLeftY,
                              int imageWidth,
                              int imageHeight) {
        byte[][] tiles = new byte[9][9];
        bot.mouseMove(topLeftX, topLeftY);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(topLeftX + imageWidth, topLeftY);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(topLeftX + imageWidth, topLeftY + imageHeight);
        bot.delay(delayShowBoxMs);
        bot.mouseMove(topLeftX, topLeftY + imageHeight);
        int width = Math.floorDiv(imageWidth, 9);
        int height = Math.floorDiv(imageHeight, 9);
        int actualImageWidth = imageWidth - (2 * imageOffset);
        int actualImageHeight = imageHeight - (2 * imageOffset);
        BufferedImage puzzle = bot.screenShot(topLeftX + imageOffset, topLeftY + imageOffset, actualImageWidth, actualImageHeight);
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
                    result = result.replaceAll("\\s+|\\n+", "");
                    if (printReadCharacter) System.out.println(result);
                    if (!result.isEmpty()) {
                        try {
                            tiles[i][j] = Byte.parseByte(result);
                            if (tiles[i][j] < 1 || tiles[i][j] > 9) throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            throw new OCRException("Invalid characters in OCR: " + result + " at (" + i + "," + j + ")");
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
