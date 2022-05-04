package cli;

import sudokujava.SolverMode;

import java.util.Objects;

public final class CLISettings {
    public static final int DEFAULT_INT_VALUE = 0xFFFFFFFF;
    public String inputFile = null;
    public SolverMode.Speed speed = null;
    public int topLeftX = DEFAULT_INT_VALUE;
    public int topLeftY = DEFAULT_INT_VALUE;
    public int imgWidth = DEFAULT_INT_VALUE;
    public int imgHeight = DEFAULT_INT_VALUE;
    public int imgDelay = DEFAULT_INT_VALUE;
    public boolean debugPrinted = false;
    public String outputFile = null;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null && !(o instanceof CLISettings)) return false;
        CLISettings that = (CLISettings) o;
        return topLeftX == that.topLeftX &&
                topLeftY == that.topLeftY &&
                imgWidth == that.imgWidth &&
                imgHeight == that.imgHeight &&
                imgDelay == that.imgDelay &&
                debugPrinted == that.debugPrinted &&
                Objects.equals(inputFile, that.inputFile) &&
                speed == that.speed &&
                Objects.equals(outputFile, that.outputFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputFile, speed, topLeftX, topLeftY, imgWidth, imgHeight, imgDelay, debugPrinted, outputFile);
    }
}
