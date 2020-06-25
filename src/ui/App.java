package ui;

import javax.swing.*;

public class App extends JFrame {
    private App() {
        super("l m a o ");
        AppPanel ap = new AppPanel();
        add(ap);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        setVisible(true);
        pack();
    }
    public static void main(String[] args) {
        new App();
    }
}
