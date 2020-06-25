package sudokujava;

import java.awt.*;

class SolverMode {
    boolean isImage;
    byte[][] tileArray;
    boolean writeToFile = false;
    boolean hideNotFoundCandidateMsg = true;
    boolean showCandidateRemovalMsg = true;
    boolean hideNoBlankWereFound = false;
    Point topLeft;
    int imageWidth;
    int imageHeight;
    int imageCaptureDelay;
    Speed speed;

    SolverMode(byte[][] tileArray,
               boolean writeToFile,
               boolean hideCandidateNotFoundMsg,
               boolean showCandidateRemovalMsg,
               boolean hideNoBlankWereFound,
               Speed speed) {
        if (tileArray.length != 9) {
            throw new IllegalArgumentException();
        }
        for (byte[] bytes : tileArray) {
            if (bytes.length != 9) throw new IllegalArgumentException();
        }
        this.tileArray = tileArray;
        this.writeToFile = writeToFile;
        this.hideNotFoundCandidateMsg = hideCandidateNotFoundMsg;
        this.showCandidateRemovalMsg = showCandidateRemovalMsg;
        this.hideNoBlankWereFound = hideNoBlankWereFound;
        this.speed = speed;
        if (this.speed == null) throw new IllegalArgumentException();
        isImage = false;
    }

    SolverMode(int topLeftX, int topLeftY, int imageWidth, int imageHeight, int imageDelay) {
        if (topLeftX < 0 || topLeftY < 0 || imageWidth <= 0 || imageHeight <= 0) throw new IllegalArgumentException();
        isImage = true;
        this.topLeft = new Point(topLeftX,topLeftY);
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.imageCaptureDelay = imageDelay;
        this.speed = SudokuJava.DEFAULT;
        System.out.println("Image top left coord: " + topLeft.getX() + "," + topLeft.getY() + " width: " + imageWidth + ", height: " + imageHeight + ", delay: " + imageCaptureDelay);
    }
    
    public enum Speed {
        FAST('0', 0),
        MEDIUM('1', 1),
        SLOW('2', 2),
        VERY_SLOW('3', 3),
        REALLY_SLOW('4', 4),
        RECURSE('R', Integer.MAX_VALUE);

        Speed(char rep, int speed) {
            this.rep = rep;
            this.speed = speed;
        }

        private char rep;
        private int speed;

        public char characterRepresentation() {
            return rep;
        }
        public static Speed getSpeed(char input) {
            for (Speed sp : Speed.values()) {
                if (sp.rep == input) return sp;
            }
            return null;
        }
        
        public boolean isGreaterThan(Speed speed) {
            return Integer.compare(this.speed, speed.speed) == 1;
        }
    }
}
