/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 *
 * @author adminasaurus
 */
public class SudokuMenu extends JPanel {
    public static final String[] VALUES = new String[]{"File", "Image", "Manual"};
    public static final String DEFAULT = "Manual";
    private JPanel actualMenu = new JPanel();
    public SudokuMenu() {
        Dimension size = new Dimension(480,100);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setLayout(new BorderLayout());
        add(new SourceBox(), BorderLayout.PAGE_START);
        actualMenu.setLayout(new CardLayout());
        actualMenu.add(new ManualMenu(), VALUES[2]);
        actualMenu.add(new ImageMenu(), VALUES[1]);
        actualMenu.add(new FileMenu(), VALUES[0]);
        add(actualMenu, BorderLayout.CENTER);
    }

    private class SourceBox extends JComboBox<String> implements ItemListener {
        SourceBox() {
            super(VALUES);
            setSelectedItem(DEFAULT);
            setEditable(false);
            addItemListener(this);
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            CardLayout cl = (CardLayout) actualMenu.getLayout();
            cl.show(actualMenu, (String) e.getItem());
        }
    }
}
