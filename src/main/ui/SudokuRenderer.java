package ui;

import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class SudokuRenderer extends JPanel {
    private final SudokuSquare[] squares = new SudokuSquare[81];

    SudokuRenderer() {
        setBoxSize(new Dimension(360, 360));
        setBackground(Color.WHITE);
        setLayout(new GridLayout(9, 9));
        for (int i = 0; i < 81; i++) {
            squares[i] = new SudokuSquare();
            add(squares[i]);
        }
    }

    public byte[][] getTiles() {
        byte[][] tiles = new byte[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (squares[i * 9 + j].getText().isEmpty()) tiles[i][j] = 0;
                else tiles[i][j] = Byte.parseByte(squares[i * 9 + j].getText());
            }
        }
        return tiles;
    }

    public void writeSolution(byte[][] b) {
        if (b.length != 9) throw new IllegalArgumentException();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                squares[i * 9 + j].setText(Byte.toString(b[i][j]));
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


    private class SudokuSquare extends JTextField {
        SudokuSquare() {
            super();
            setHorizontalAlignment(JTextField.CENTER);
            ((PlainDocument) getDocument()).setDocumentFilter(new IntegerFilter(1, true));
        }
    }
}
