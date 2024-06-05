package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

public class PreviewFrame extends JFrame {
    public PreviewFrame(int topX, int topY, int width, int height) {
        super("Preview");
        setUndecorated(true);
        setSize(width, height);
        setLocation(topX, topY);
        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        addKeyListener(new PreviewFrameCloser());
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setFocusable(true);
        setVisible(true);
        setAlwaysOnTop(true);
        toFront();
        requestFocus();
    }

    private void close() {
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSED));
        dispose();
    }

    private class PreviewFrameCloser extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getExtendedKeyCode() == KeyEvent.VK_ESCAPE) {
                close();
            }
        }
    }
}
