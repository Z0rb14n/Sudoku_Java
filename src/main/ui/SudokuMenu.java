package ui;

import sudokujava.SolverSpeed;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class SudokuMenu extends JPanel {
    private final SourceBox sb = new SourceBox();
    private final ActualMenu menu = new ActualMenu();

    SudokuMenu() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(sb);
        add(menu);
    }

    private void update() {
        menu.update();
    }

    private class ActualMenu extends JPanel {
        private final GeneralMenu mm = new GeneralMenu();
        private final ImageMenu im = new ImageMenu();
        private final FileMenu fm = new FileMenu();

        ActualMenu() {
            super();
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            add(mm);
            add(im);
            add(fm);
        }

        void update() {
            String string = (String) sb.getSelectedItem();
            UIMode mode = UIMode.getMode(string);
            mm.update(mode);
            im.update(mode);
            fm.update(mode);
        }

        int[] getFields() {
            return im.getFieldValues();
        }

        boolean getAutoType() {
            return im.getAutotypeEnabled();
        }

        SolverSpeed getSpeed() {
            return mm.getSpeed();
        }

        String getFile() {
            return fm.getFileLocation();
        }
    }

    int[] getFields() {
        return menu.getFields();
    }

    boolean getAutoType() {
        return menu.getAutoType();
    }

    SolverSpeed getSpeed() {
        return menu.getSpeed();
    }

    String getFile() {
        return menu.getFile();
    }

    UIMode getUIMode() {
        return UIMode.getMode((String) sb.getSelectedItem());
    }

    private class SourceBox extends JComboBox<String> implements ItemListener {
        SourceBox() {
            super(UIMode.labels());
            setSelectedItem(UIMode.getDefault().toString());
            setEditable(false);
            addItemListener(this);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            SudokuMenu.this.update();
        }
    }
}
