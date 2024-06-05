package ui;

import sudokujava.SolverSpeed;
import sudokujava.SudokuJava;
import util.OCRException;

import javax.swing.*;

public class App extends JFrame {
    private static App singleton;
    private final AppPanel ap = new AppPanel();

    public static App getInstance() {
        if (singleton == null) {
            singleton = new App();
        }
        return singleton;
    }

    private App() {
        super("l m a o ");
        add(ap);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        pack();
        setVisible(true);
    }

    void run() {
        int[] fields = ap.getFields();
        byte[][] tiles = ap.getTiles();
        UIMode mode = ap.getUIMode();
        boolean autoType = ap.getAutoType();
        SolverSpeed speed = ap.getSelectedSpeed();
        String file = ap.getFile();
        if (mode == UIMode.FILE) {
            if (file.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid File input.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    SudokuJava sj = new SudokuJava(file, speed);
                    sj.run();
                    ap.setTiles(sj.getTiles());
                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Invalid File contents.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (OCRException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Bad OCR reading/initialization: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (mode == UIMode.MANUAL) {
            try {
                SudokuJava sj = new SudokuJava(tiles, speed);
                sj.run();
                ap.setTiles(sj.getTiles());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (mode == UIMode.IMAGE) {
            try {
                SudokuJava sj = new SudokuJava(fields[0], fields[1], fields[2], fields[3], fields[4], autoType, speed);
                sj.run();
                ap.setTiles(sj.getTiles());
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Invalid arguments.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (OCRException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Bad OCR reading/initialization: \n" + ex, "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
