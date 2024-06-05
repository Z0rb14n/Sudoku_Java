package ui;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ImageMenu extends JPanel {
    private static final String[] VALUES = new String[]{"Top Left X", "Top Left Y", "Width", "Height", "Delay"};
    private static final int[] VALUE_DEFAULTS = new int[]{100, 100, 500, 500, 3000};
    private final LabelAndField[] fields = new LabelAndField[VALUES.length];
    private final AutoTypeBox box = new AutoTypeBox();
    private final JButton previewButton = new JButton("Preview");
    private PreviewFrame previewFrame;

    ImageMenu() {
        super();
        setLayout(new GridLayout(3, 3));
        for (int i = 0; i < VALUES.length; i++) {
            fields[i] = new LabelAndField(VALUES[i]);
            fields[i].field.setText(Integer.toString(VALUE_DEFAULTS[i]));
            add(fields[i]);
        }
        add(box);
        add(previewButton);
        previewButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] values = getFieldValues();
                previewFrame = new PreviewFrame(values[0], values[1], values[2], values[3]);
                previewButton.setEnabled(false);
                previewFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        previewButton.setEnabled(true);
                        previewFrame = null;
                    }
                });
            }
        });
        update(UIMode.getDefault());
    }

    void update(UIMode mode) {
        for (LabelAndField field : fields) {
            field.setFieldEnabled(mode == UIMode.IMAGE);
        }
        box.setBoxEnabled(mode != UIMode.MANUAL);
        previewButton.setEnabled(mode == UIMode.IMAGE);
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

    private static class AutoTypeBox extends JPanel {
        private final JCheckBox box = new JCheckBox();

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

    private static class LabelAndField extends JPanel {
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

    private static class Field extends JTextField {
        Field() {
            super(6);
            setText("0");
            ((PlainDocument) getDocument()).setDocumentFilter(new IntegerFilter(4, true));
        }

        int getValue() {
            if (getText().isEmpty()) return 0;
            return Integer.parseInt(getText());
        }
    }
}
