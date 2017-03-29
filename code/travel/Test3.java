package travel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by L on 2016/10/5.
 */
public class Test3 {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new JFrame() {
            Point point = new Point(0,0);
            Point point2 = new Point(0,0);
            JFrame frame = this;
            {
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        point = e.getPoint();
                        point2 = e.getLocationOnScreen();


                    }
                });
                setSize(1000, 500);
                setContentPane(new JPanel(){
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D d = (Graphics2D) g;
                        d.drawString("x: " + point.x + ", y: " + point.y, 0, 40);
                        d.drawString("x: " + point2.x + ", y: " + point2.y, 0, 80);
                        frame.repaint();
                    }
                });
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setLocationRelativeTo(null);
            }

        }.setVisible(true));
    }
}
