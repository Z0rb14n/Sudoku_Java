package ui;

public enum UIMode {
    FILE("File"), IMAGE("Image"), MANUAL("Manual");
    private String label;

    UIMode(String label) {
        this.label = label;
    }

    public static UIMode getDefault() {
        return MANUAL;
    }

    public static UIMode getMode(String str) {
        for (UIMode mode : values()) {
            if (mode.label.equals(str)) {
                return mode;
            }
        }
        throw new IllegalArgumentException();
    }

    public static String[] labels() {
        String[] strings = new String[values().length];
        for (int i = 0; i < strings.length; i++) {
            strings[i] = values()[i].label;
        }
        return strings;
    }

    @Override
    public String toString() {
        return label;
    }
}
