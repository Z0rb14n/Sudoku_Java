/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

/**
 *
 * @author adminasaurus
 */
public class SudokuMenu extends JPanel {
    public SudokuMenu() {
        Dimension size = new Dimension(480,100);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setBackground(Color.RED);
    }
}
