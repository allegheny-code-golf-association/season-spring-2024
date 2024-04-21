package cfh.taxi.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class InfoWindow extends JWindow {

    InfoWindow(String text) {
        text = "<html>" + text.replaceAll("\\n", "<br>");
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(4, 4, 6, 6, Color.LIGHT_GRAY),
                BorderFactory.createLoweredBevelBorder()));
        panel.add(new JLabel(text));
        add(panel);
        pack();
        validate();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                dispose();
            }
        });
    }
    
    void show(int x, int y) {
        setLocation(x, y);
        setVisible(true);
    }
}
