package ui;

import sudokujava.OCRException;
import sudokujava.SolverMode;
import sudokujava.SolverSpeed;
import sudokujava.SudokuJava;

import javax.swing.*;

public class App extends JFrame {
    private static App singleton;
    private AppPanel ap = new AppPanel();

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
        SolverSpeed solverSpeed = ap.getSelectedSpeed();
        String file = ap.getFile();
        if (mode == UIMode.FILE) {
            if (file.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Invalid File input.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                try {
                    SudokuJava sj = new SudokuJava(SolverMode.FromFile(file));
                    sj.run();
                    ap.setTiles(sj.getTiles());
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid File contents.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (OCRException ex) {
                    JOptionPane.showMessageDialog(this, "Bad OCR reading/initialization.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (mode == UIMode.MANUAL) {
            try {
                SolverMode solverMode = SolverMode.FromData(tiles);
                solverMode.setSolverSpeed(solverSpeed);
                SudokuJava sj = new SudokuJava(solverMode);
                sj.run();
                ap.setTiles(sj.getTiles());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (mode == UIMode.IMAGE) {
            try {
                SolverMode solverMode = SolverMode.FromImage(fields[0], fields[1], fields[2], fields[3]);
                solverMode.setFlag(SolverMode.AUTO_TYPE, autoType);
                solverMode.setSolverSpeed(solverSpeed);
                SudokuJava sj = new SudokuJava(solverMode);
                sj.run();
                ap.setTiles(sj.getTiles());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid arguments.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (OCRException ex) {
                JOptionPane.showMessageDialog(this, "Bad OCR reading/initialization.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Uncaught Exception " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
