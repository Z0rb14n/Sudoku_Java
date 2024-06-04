/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import sudokujava.SolverSpeed;

import javax.swing.*;
import java.awt.*;

class AppPanel extends JPanel {
    private final SudokuRenderer sr;
    private final SudokuMenu sm;

    AppPanel() {
        super();
        setPreferredSize(new Dimension(600, 640));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());
        sm = new SudokuMenu();
        sr = new SudokuRenderer();
        add(sm, BorderLayout.PAGE_START);
        add(sr, BorderLayout.CENTER);
    }

    void setTiles(byte[][] tiles) {
        sr.writeSolution(tiles);
    }

    int[] getFields() {
        return sm.getFields();
    }

    byte[][] getTiles() {
        return sr.getTiles();
    }

    boolean getAutoType() {
        return sm.getAutoType();
    }

    String getFile() {
        return sm.getFile();
    }

    SolverSpeed getSelectedSpeed() {
        return sm.getSpeed();
    }

    UIMode getUIMode() {
        return sm.getUIMode();
    }
}
