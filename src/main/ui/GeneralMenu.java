package ui;

import sudokujava.SolverSpeed;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class GeneralMenu extends JPanel {
    private SpeedChooser sc = new SpeedChooser();

    GeneralMenu() {
        super();
        add(new JLabel("Select SolverSpeed:"));
        add(sc);
        add(new Button());
    }

    void update(UIMode mode) {
        sc.setEnabled(mode != UIMode.FILE);
    }

    SolverSpeed getSpeed() {
        String str = (String) sc.getSelectedItem();
        return SolverSpeed.valueOf(str);
    }

    private class SpeedChooser extends JComboBox<String> {
        SpeedChooser() {
            super(SolverSpeed.getLabels());
            setSelectedItem(SolverSpeed.getDefault().toString());
            setEditable(false);
        }
    }

    private class Button extends JButton implements MouseListener {
        Button() {
            super("Run");
            addMouseListener(this);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            App.getInstance().run();
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }
    }
}
