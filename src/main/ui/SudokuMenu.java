package ui;

import sudokujava.SolverMode.Speed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class SudokuMenu extends JPanel {
    private SourceBox sb = new SourceBox();
    private ActualMenu menu = new ActualMenu();

    SudokuMenu() {
        Dimension size = new Dimension(600, 200);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLayout(new BorderLayout());
        add(sb, BorderLayout.PAGE_START);
        add(menu, BorderLayout.CENTER);
    }

    private void update() {
        menu.update();
    }

    private class ActualMenu extends JPanel {
        private GeneralMenu mm = new GeneralMenu();
        private ImageMenu im = new ImageMenu();
        private FileMenu fm = new FileMenu();

        ActualMenu() {
            super();
            setLayout(new GridLayout(3, 1));
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

        Speed getSpeed() {
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

    Speed getSpeed() {
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
