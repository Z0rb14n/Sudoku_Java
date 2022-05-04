package sudokujava;

public class SolverMode {
    private boolean isImage;
    byte[][] tileArray;
    private boolean writeToFile = false;
    private boolean hideNotFoundCandidateMsg = true;
    private boolean showCandidateRemovalMsg = true;
    private boolean hideNoBlankWereFound = false;
    private int topLeftX = Integer.MIN_VALUE;
    private int topLeftY = Integer.MIN_VALUE;
    public int imageWidth;
    public int imageHeight;
    int imageCaptureDelay;
    Speed speed;
    public String inputFile = null;
    public boolean debugPrinted = false;
    public boolean autoType = false;
    public String outputFile = null;

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

    SolverMode(byte[][] tileArray, Speed speed) {
        this(tileArray, false, true, true, false, speed);
    }

    SolverMode(byte[][] tileArray, boolean writeToFile, boolean hideCandidateNotFoundMsg, boolean showCandidateRemovalMsg, boolean hideNoBlankWereFound) {
        this(tileArray, writeToFile, hideCandidateNotFoundMsg, showCandidateRemovalMsg, hideNoBlankWereFound, Speed.getDefault());
    }

    SolverMode(int topLeftX, int topLeftY, int imgWidth, int imgHeight, int imgDelay, Speed speed) {
        if (topLeftX < 0 || topLeftY < 0 || imgWidth <= 0 || imgHeight <= 0) throw new IllegalArgumentException();
        isImage = true;
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.imageWidth = imgWidth;
        this.imageHeight = imgHeight;
        this.imageCaptureDelay = imgDelay;
        this.speed = speed;
        System.out.println("Image top left coord: " + topLeftX + "," + topLeftY + " width: " + imageWidth + ", height: " + imageHeight + ", delay: " + imageCaptureDelay);

    }

    SolverMode(int topLeftX, int topLeftY, int imageWidth, int imageHeight, int imageDelay) {
        this(topLeftX, topLeftY, imageWidth, imageHeight, imageDelay, Speed.getDefault());
    }

    public enum Speed {
        FAST('0', 0),
        MEDIUM('1', 1),
        SLOW('2', 2),
        VERY_SLOW('3', 3),
        REALLY_SLOW('4', 4),
        RECURSE('R', Integer.MAX_VALUE);

        public static Speed getDefault() {
            return SLOW;
        }

        public static String[] getLabels() {
            String[] labels = new String[values().length];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = values()[i].toString();
            }
            return labels;
        }

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

    boolean doWriteToFile() {
        return writeToFile;
    }

    boolean isImage() {
        return isImage;
    }

    public boolean showCandidateNotFoundMessage() {
        return !hideNotFoundCandidateMsg;
    }

    public boolean showCandidateRemovalMessage() {
        return showCandidateRemovalMsg;
    }

    public boolean showAlgorithmUnusedMessage() {
        return hideNoBlankWereFound;
    }

    public int getTopLeftX() {
        return topLeftX;
    }

    public int getTopLeftY() {
        return topLeftY;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isImage).append("\n");
        if (tileArray == null) sb.append("IMAGE");
        else {
            for (byte[] bytes : tileArray) {
                for (byte b : bytes) {
                    sb.append(b);
                }
                sb.append("\n");
            }
        }
        sb.append(writeToFile).append(hideNotFoundCandidateMsg).append(showCandidateRemovalMsg).append(hideNoBlankWereFound);
        if (topLeftX != Integer.MIN_VALUE) sb.append(topLeftX).append(topLeftY);
        if (isImage) sb.append(imageWidth).append(imageHeight).append(imageCaptureDelay);
        sb.append(speed.characterRepresentation());
        return sb.toString();
    }
}
