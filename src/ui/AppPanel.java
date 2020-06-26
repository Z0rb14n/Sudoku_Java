/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javax.swing.*;
import java.awt.*;

class AppPanel extends JPanel {
    SudokuRenderer sr;
    SudokuMenu sm;

    AppPanel() {
        super();
        setPreferredSize(new Dimension(480, 640));
        setBackground(Color.WHITE);
        setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
        sm  =new SudokuMenu();
        sr = new SudokuRenderer();
        add(sm);
        add(sr);
    }
}
