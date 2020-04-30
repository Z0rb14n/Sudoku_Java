/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 *
 * @author adminasaurus
 */
public class AppPanel extends JPanel {
    SudokuRenderer sr;
    SudokuMenu sm;
    public AppPanel() {
        super();
        setPreferredSize(new Dimension(480, 640));
        setBackground(Color.WHITE);
        BoxLayout bl = new BoxLayout(this,BoxLayout.PAGE_AXIS);
        setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        sm  =new SudokuMenu();
        sr = new SudokuRenderer();
        add(sm);
        add(sr);
    }
}
