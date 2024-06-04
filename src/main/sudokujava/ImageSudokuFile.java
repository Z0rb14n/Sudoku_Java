package sudokujava;

import util.SudokuScreenIO;

public class ImageSudokuFile implements SudokuFile {
    private final int topLeftX;
    private final int topLeftY;
    private final int imageWidth;
    private final int imageHeight;
    private final int imageDelay;

    public ImageSudokuFile(int topLeftX, int topLeftY, int imageWidth, int imageHeight, int imageDelay) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageDelay = imageDelay;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    @Override
    public byte[][] getTiles() {
        try {
            Thread.sleep(imageDelay);
        } catch (InterruptedException ex) {
            System.err.println("Interrupted.");
        }
        return new SudokuScreenIO().readTiles(topLeftX, topLeftY, imageWidth, imageHeight);
    }

    @Override
    public String toString() {
        return "IMAGE," + topLeftX + "," + topLeftY + "," + imageWidth + "," + imageHeight + "," + imageDelay + ",";
    }
}
