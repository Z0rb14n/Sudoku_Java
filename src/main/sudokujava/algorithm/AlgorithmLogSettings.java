package sudokujava.algorithm;

public class AlgorithmLogSettings {
    private static final AlgorithmLogSettings logSettings = new AlgorithmLogSettings();

    public static AlgorithmLogSettings getInstance() {
        return logSettings;
    }

    private AlgorithmLogSettings() {
    }

    private boolean printCandidateNotFound = false;
    private boolean printCandidateRemoval = false;
    private boolean printAlgorithmUnused = false;

    public boolean shouldShowCandidateNotFound() {
        return printCandidateNotFound;
    }

    public void setPrintCandidateNotFound(boolean value) {
        this.printCandidateNotFound = value;
    }

    public boolean shouldPrintCandidateRemoval() {
        return printCandidateRemoval;
    }

    public void setPrintCandidateRemoval(boolean printCandidateRemoval) {
        this.printCandidateRemoval = printCandidateRemoval;
    }

    public boolean shouldPrintAlgorithmUnused() {
        return printAlgorithmUnused;
    }

    public void setPrintAlgorithmUnused(boolean printAlgorithmUnused) {
        this.printAlgorithmUnused = printAlgorithmUnused;
    }
}
