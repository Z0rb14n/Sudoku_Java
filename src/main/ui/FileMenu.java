package ui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

class FileMenu extends JPanel implements MouseListener {
    private JLabel location = new JLabel("");
    private JButton button = new JButton("Select file");
    private static final FileFilter FILTER = new FileFilter() {
        @Override
        public boolean accept(File f) {
            return f.isDirectory() || f.getPath().endsWith(".txt");
        }

        @Override
        public String getDescription() {
            return "Special Text Files (.txt)";
        }
    };

    FileMenu() {
        super();
        add(location);
        button.addMouseListener(this);
        add(button);
        update(UIMode.getDefault());
    }

    String getFileLocation() {
        return location.getText();
    }

    void update(UIMode mode) {
        button.setEnabled(mode == UIMode.FILE);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JFileChooser jfc = new JFileChooser();
        jfc.setFileFilter(FILTER);
        int returnVal = jfc.showDialog(this, "Select");
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jfc.getSelectedFile();
            location.setText(file.getPath());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
