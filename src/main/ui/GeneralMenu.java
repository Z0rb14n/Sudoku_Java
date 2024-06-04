package ui;

import sudokujava.SolverSpeed;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class GeneralMenu extends JPanel {
    private final SpeedChooser sc = new SpeedChooser();

    GeneralMenu() {
        super();
        add(new JLabel("Select Speed:"));
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
