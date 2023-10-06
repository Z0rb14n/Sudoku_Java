package ui;

import sudokujava.SolverMode.Speed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private class Button extends JButton implements ActionListener {
        Button() {
            super("Run");
            addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            App.getInstance().run();
        }
    }
}
