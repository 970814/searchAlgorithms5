package travel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

public class Test5 {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new JFrame() {
            {
                setSize(800, 800);
                add(new MyPanel());
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
            }
        }.setVisible(true));
    }
}
class MyPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(90));
        g2d.drawString("hello, world", 300, -300);
        g2d.setTransform(old);
        g2d.drawString("hello, world", 300, 300);
        //things you draw after here will not be rotated
    }
}