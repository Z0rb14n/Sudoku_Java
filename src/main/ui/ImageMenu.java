package ui;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

class ImageMenu extends JPanel {
    // TODO POTENTAL IMAGE DELAY OPTION
    private static final String[] VALUES = new String[]{"Top Left X", "Top Left Y", "Width", "Height"};
    private LabelAndField[] fields = new LabelAndField[VALUES.length];
    private AutoTypeBox box = new AutoTypeBox();

    ImageMenu() {
        super();
        setLayout(new GridLayout(2, 4));
        for (int i = 0; i < VALUES.length; i++) {
            fields[i] = new LabelAndField(VALUES[i]);
            add(fields[i]);
        }
        add(box);
        update(UIMode.getDefault());
    }

    void update(UIMode mode) {
        for (LabelAndField field : fields) {
            field.setFieldEnabled(mode == UIMode.IMAGE);
        }
        box.setBoxEnabled(mode != UIMode.MANUAL);
    }

    int[] getFieldValues() {
        int[] values = new int[fields.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = fields[i].getFieldValue();
        }
        return values;
    }

    boolean getAutotypeEnabled() {
        return box.isBoxSelected();
    }

    private class AutoTypeBox extends JPanel {
        private JCheckBox box = new JCheckBox();

        AutoTypeBox() {
            super();
            add(new JLabel("Autotype"));
            box.setSelected(true);
            add(box);
        }

        boolean isBoxSelected() {
            return box.isSelected();
        }

        void setBoxEnabled(boolean flag) {
            box.setEnabled(flag);
        }
    }

    private class LabelAndField extends JPanel {
        Field field = new Field();

        LabelAndField(String label) {
            super();
            add(new JLabel(label));
            add(field);
        }

        void setFieldEnabled(boolean flag) {
            field.setEnabled(flag);
        }

        int getFieldValue() {
            return field.getValue();
        }
    }

    private class Field extends JTextField {
        Field() {
            super(6);
            setText("0");
            ((PlainDocument) getDocument()).setDocumentFilter(new IntegerFilter(4, false));
        }

        int getValue() {
            return Integer.parseInt(getText());
        }
    }
}
