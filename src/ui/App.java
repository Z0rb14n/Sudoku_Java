/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author adminasaurus
 */
public class App extends JFrame {
    private final AppPanel ap;
    public App() {
        super("l m a o ");
        ap = new AppPanel();
        add(ap);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        setVisible(true);
        pack();
    }
    public static void main(String[] args) {
        App app = new App();
    }
}
