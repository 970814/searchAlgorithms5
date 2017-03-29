package travel.point;


import travel.Traveler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by L on 2016/10/4.
 */
public class PointMaker {
    public void addNewPoint(Point point) {
        int size = pointSet.size();
        int i = 0;
        while (i < size && point.x > pointSet.get(i).x) {
            i++;
        }
        pointSet.add(i, point);
        buttonList.add(i, null);
    }
    public static class PairPoint {
        Point i;
        Point j;

        public PairPoint(Point i, Point j) {
            this.i = i;
            this.j = j;
        }
    }
    public static class Pair {
        public int i;
        public int j;

        public Pair(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public String toString() {
            return i + ", " + j;
        }
    }
    public void clearX() {
        lines.clear();
        pairs.clear();
    }
    public void clear() {
        pointSet.clear();
        buttonList.clear();
        clearX();
    }

    public ArrayList<Point> pointSet = new ArrayList<>();
    public ArrayList<JButton> buttonList = new ArrayList<>();

    public ArrayList<Pair> lines = new ArrayList<>();
    public ArrayList<PairPoint> pairs = new ArrayList<>();
    //    int times = 0;
    public boolean paintPoint = false;
    int r = 8;

    public void paintPoint(Graphics2D d) {
        if (!paintPoint) {
            return;
        }
        for (int[] i = new int[1]; i[0] < pointSet.size(); i[0]++) {

            d.setColor(Color.YELLOW.darker());
            Point p = pointSet.get(i[0]);
            d.drawString("" + i[0], p.x, p.y - r / 2);
        }
    }

    public boolean paintLocation = false;
    public boolean paintLength = false;

    public void paintLocation(Graphics2D d) {
        if (!paintLocation) {
            return;
        }
//        int size = pointSet.size();
        for (int[] i = new int[1]; i[0] < pointSet.size(); i[0]++) {
            d.setColor(Color.YELLOW.darker());
            Point p = pointSet.get(i[0]);
            d.drawString("(" + p.x + ", " + p.y + ")", p.x, p.y + r);
        }
    }

    public void paintLines(Graphics2D d) {
        d.setColor(Color.GREEN.brighter());
        for (PairPoint x : pairs) {
            if (x != null) {
                Point p = x.i;
                Point q = x.j;
                d.drawLine(p.x, p.y, q.x, q.y);
            }
        }
    }

    public void paintPath(Graphics2D d) {
        d.setColor(Color.GREEN.brighter());
//        int size = lines.size();
        for (Pair p : lines) {
            if (p != null) {
                paintLine(d, p.i, p.j, paintLength);
            }
        }
    }

    public void paintLine(Graphics2D d, int i, int j, boolean paintLength) {
        Point p = pointSet.get(i);
        Point q = pointSet.get(j);
        d.drawLine(p.x, p.y, q.x, q.y);
        if (paintLength) {
            int mX = p.x + q.x >>> 1;
            int mY = p.y + q.y >>> 1;
            d.translate(mX, mY);
            double theta = Math.atan((p.y - q.y) / (double) (p.x - q.x));
            d.rotate(theta);
            d.drawString(Traveler.distanceOf(p, q) + " px", 0, 0);
            d.rotate(-theta);
            d.translate(-mX, -mY);
        }
    }

    public static class Bounds {
        final int minX;
        final int minY;
        final int maxX;
        final int maxY;

        public Bounds(int minX, int minY, int maxX, int maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }
    }

    static Random random = new Random();
    final Bounds bounds;

    public PointMaker(Bounds bounds) {
        this.bounds = bounds;
    }

    public void make(JComponent component, int n) {
        pointSet = new ArrayList<>(n);
        lines = new ArrayList<>(n);
        buttonList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            int x = random.nextInt(bounds.maxX - bounds.minX) + bounds.minX;
            int y = random.nextInt(bounds.maxY - bounds.minY) + bounds.minY;
            pointSet.add(new Point(x, y));

        }
        sortPointSet();
        component.removeAll();
        for (int[] i = new int[1]; i[0] < n; i[0]++) {
            JButton button;
            component.add(button = new JButton("") {
                public void paintComponent(Graphics g) {
                    g.setColor(Color.ORANGE);
                    g.fillRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
                            25, 25);
                    super.paintComponent(g);
                }

                //                public void paintBorder(Graphics g) {
//                    //画边界区域
//                    g.drawRoundRect(0, 0, getSize().width - 1, getSize().height - 1,
//                            20, 20);
//                }
                {
                    setContentAreaFilled(false);
                    Point p = pointSet.get(i[0]);
                    setToolTipText("<html>" +
                            i[0] + "<br/>" +
                            "x: " + p.x + ", " + "y: " + p.y + "<br/>" +
                            "</html>");
                    setBounds(p.x - r / 2, p.y - r / 2, r, r);
                }
            });
            buttonList.add(button);
        }
    }

    public void sortPointSet() {
        sortPointSet(0, pointSet.size() - 1);
    }

    public void sortPointSet(int low, int high) {
        if (low < high) {
            int q = partition(low, high);
            sortPointSet(low, q - 1);
            sortPointSet(q + 1, high);
        }
    }

    private int partition(int p, int r) {
        Point x = pointSet.get(r);
        int t = p;
        for (int j = p; j < r; j++) {
            if (pointSet.get(j).x < x.x) {
                exchange(t, j);
                t++;
            }
        }
        exchange(t, r);
        return t;
    }

    private void exchange(int i, int j) {
        Point m = pointSet.get(i);
        pointSet.set(i, pointSet.get(j));
        pointSet.set(j, m);
    }
}
