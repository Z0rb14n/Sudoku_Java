package ui;

import javax.swing.*;

public class App extends JFrame {
    private static App singleton;

    public static App getInstance() {
        if (singleton == null) {
            singleton = new App();
        }
        return singleton;
    }
    private App() {
        super("l m a o ");
        AppPanel ap = new AppPanel();
        add(ap);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(false);
        pack();
        setVisible(true);
    }

    void run() {
        // TODO METHOD BODY
    }
    public static void main(String[] args) {
        App.getInstance();
    }
}
