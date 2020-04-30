/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author adminasaurus
 */
public class SudokuRenderer extends JPanel {
    SudokuSquare[] squares = new SudokuSquare[81];
    public SudokuRenderer() {
        setBoxSize(new Dimension(360,360));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(9,9));
        for (int i = 0; i < 81; i++) {
            squares[i] = new SudokuSquare();
            add(squares[i]);
        }
    }
    
    public void writeSolution(byte[][] b) {
        if (b.length != 9 && b[0].length != 9) throw new IllegalArgumentException();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i*9 + j].setText("" + b[i][j]);
                squares[i*9 + j].setEnabled(false);
            }
        }
    }
    
    public void reset() {
        for (SudokuSquare ss : squares) {
            ss.setText("");
            ss.setEnabled(true);
        }
    }
    
    private void setBoxSize(Dimension d) {
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }
    
    public void disableSquares() {
        for (SudokuSquare ss : squares) {
            ss.setEnabled(false);
        }
    }
    
    public void enableSquares() {
        for (SudokuSquare ss : squares) {
            ss.setEnabled(true);
        }
    }
    
    
    public class SudokuSquare extends JTextField {
        public SudokuSquare() {
            super();
            SudokuSquare ss = this; 
            setHorizontalAlignment(JTextField.CENTER);
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (ss.getText().length() == 1 || !Character.isDigit(e.getKeyChar())) {
                        e.consume();
                    }
                }
            });
        }
    }
}
