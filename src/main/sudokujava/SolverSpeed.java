package sudokujava;

public enum SolverSpeed {
    FAST('0', 0),
    MEDIUM('1', 1),
    SLOW('2', 2),
    VERY_SLOW('3', 3),
    REALLY_SLOW('4', 4),
    RECURSE('R', Integer.MAX_VALUE);

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

    SolverSpeed(char rep, int speed) {
        this.rep = rep;
        this.speed = speed;
    }

    private final char rep;
    private final int speed;

    public char characterRepresentation() {
        return rep;
    }

    public static SolverSpeed getSpeed(char input) {
        for (SolverSpeed sp : SolverSpeed.values()) {
            if (sp.rep == input) return sp;
        }
        return null;
    }

    public boolean isGreaterThan(SolverSpeed speed) {
        return Integer.compare(this.speed, speed.speed) == 1;
    }
}
