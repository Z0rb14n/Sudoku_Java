package sudokujava;

public enum SolverSpeed {
    FAST(0),
    MEDIUM(1),
    SLOW(2),
    VERY_SLOW(3),
    REALLY_SLOW(4),
    RECURSE(Integer.MAX_VALUE);

    public static SolverSpeed getDefault() {
        return SLOW;
    }

    public static String[] getLabels() {
        String[] labels = new String[values().length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = values()[i].toString();
        }
        return labels;
    }

    SolverSpeed(int speed) {
        this.speed = speed;
    }
    private final int speed;

    public static SolverSpeed parseSpeed(String input) {
        if ("fast".compareToIgnoreCase(input) == 0 || "f".compareToIgnoreCase(input) == 0) return FAST;
        if ("medium".compareToIgnoreCase(input) == 0 || "m".compareToIgnoreCase(input) == 0) return MEDIUM;
        if ("slow".compareToIgnoreCase(input) == 0 || "s".compareToIgnoreCase(input) == 0) return SLOW;
        if ("very slow".compareToIgnoreCase(input) == 0
                || "veryslow".compareToIgnoreCase(input) == 0
                || "v".compareToIgnoreCase(input) == 0
                || "vs".compareToIgnoreCase(input) == 0) {
            return VERY_SLOW;
        }
        if ("really slow".compareToIgnoreCase(input) == 0
                || "reallyslow".compareToIgnoreCase(input) == 0
                || "rs".compareToIgnoreCase(input) == 0) return REALLY_SLOW;
        if ("recurse".compareToIgnoreCase(input) == 0
                || "r".compareToIgnoreCase(input) == 0) return RECURSE;
        return null;
    }

    public boolean isGreaterThan(SolverSpeed speed) {
        return Integer.compare(this.speed, speed.speed) == 1;
    }
}
