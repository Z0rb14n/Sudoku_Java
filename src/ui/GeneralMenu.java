package ui;

import sudokujava.SolverMode.Speed;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class GeneralMenu extends JPanel {
    private SpeedChooser sc = new SpeedChooser();

    GeneralMenu() {
        super();
        add(new JLabel("Select Speed:"));
        add(sc);
        add(new Button());
    }

    void update(UIMode mode) {
        sc.setEnabled(mode != UIMode.FILE);
    }

    Speed getSpeed() {
        String str = (String) sc.getSelectedItem();
        return Speed.valueOf(str);
    }

    private class SpeedChooser extends JComboBox<String> {
        SpeedChooser() {
            super(Speed.getLabels());
            setSelectedItem(Speed.getDefault().toString());
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
