package travel;

import travel.point.PointMaker;
import travel.window.MainWindow;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by L on 2016/10/4.
 */
public class Traveler {
    ArrayList<Point> pointSet; //点集
    public double[][] d = null;   //d[i][j] 储存从point(i)开始左到右直到point(0),然后右到左直到point(j),此段路线的近似最短路径
    public int[] s = null;
    public int L = -1;
    public double[][] ds = null;

    public Traveler() {
    }

    public void clear() {
        pointSet = null;
        d = null;
        s = null;
        ds = null;
        distsNP.clear();
    }

    public Traveler(ArrayList<Point> pointSet) {
        this.pointSet = pointSet;
        L = pointSet.size() - 2;
    }

    public void travelOf(ArrayList<Point> pointSet) {
        this.pointSet = pointSet;
        L = pointSet.size() - 2;
        travelOf();
    }


    public void travelOf() { //计算最短路径
        int n = pointSet.size();
        ds = new double[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                ds[i][j] = distanceOf(i, j);
            }
        }
        d = new double[n][n];
        s = new int[n];
        s[0] = 0;
        d[0][1] = ds[0][1];
        for (int j = 2; j < n; j++) {
            d[0][j] = d[0][j - 1] + ds[j - 1][j];
        }
        for (int i = 1; i < n - 1; i++) {
            double q = Integer.MAX_VALUE;
            for (int k = 0; k < i; k++) {
                double t = d[k][i] + ds[k][i+1];
                if (q > t) {
                    q = t;
                    s[i] = k;
                }
            }
            d[i][i + 1] = q;
            for (int j = i + 2; j < n; j++) {
                d[i][j] = d[i][j - 1] +  ds[j - 1][j];
            }
        }
        d[n - 1][n - 1] = d[n - 2][n - 1] + ds[n - 2][n - 1];

    }

    @Deprecated
    public void travel() { //计算最短路径
        int n = pointSet.size();
        d = new double[n][n];
        s = new int[n];
        s[0] = 0;
        d[0][1] = distanceOf(0, 1);
        for (int j = 2; j < n; j++) {
            d[0][j] = d[0][j - 1] + distanceOf(j - 1, j);
        }
        for (int i = 1; i < n - 1; i++) {
            double q =Integer.MAX_VALUE;
            for (int k = 0; k < i; k++) {
                double t = d[k][i] + distanceOf(k, i + 1);
                if (q > t) {
                    q = t;
                    s[i] = k;
                }
            }
            d[i][i + 1] = q;
            for (int j = i + 2; j < n; j++) {
                d[i][j] = d[i][j - 1] + distanceOf(j - 1, j);
            }
        }
        d[n - 1][n - 1] = d[n - 2][n - 1] + distanceOf(n - 2, n - 1);
    }

//    public int count = 0;//通过推导和大量实践发现距离函数将会调用大约n^2次，主要是重复计算了n^2/2 次。
    //但这并不是重点，重点是LinkedList访问效率实在太低
    //本程序对于数据的访问非常频繁，所以采用数组链表

    public double distanceOf(int i, int j) {
//        count++;
        return distanceOf(pointSet.get(i), pointSet.get(j));
    }

    public static double distanceOf(Point a, Point b) { //计算两点之间的距离
        return Point.distance(a.x, a.y, b.x, b.y);
    }

    public double quickTravelNPOf(ArrayList<Point> pointSet,Stack stack) throws InterruptedException {
        this.pointSet = pointSet;
        int n = pointSet.size();
        ds = new double[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                ds[i][j] = distanceOf(i, j);
            }
        }
        distsNP.clear();
        ArrayList<Point> ps = (ArrayList<Point>) pointSet.clone();
        Point H = ps.remove(0);
        stack.push(H);
        Stack result = quickTravelNPOf(pointSet, ds, ps, stack, H, H);

        return result.result;
    }
    public static class Stack extends java.util.Stack<Point> {
        public double result = 0.0;

        public Stack() {
        }
        public Stack(double v) {
            result = v;
        }
    }

    private Stack quickTravelNPOf(ArrayList<Point> mainSet, double[][] ds,
                                   ArrayList<Point> ps, Stack stack, Point T, Point H) {

        if (ps.size() == 0) {
            return new Stack(distanceOf(mainSet, ds, T, H));
        }
//        if (!frame.runFlag) {
//            throw new InterruptedException("stop()");
//        }
        double result = Integer.MAX_VALUE;
        Integer index = null;
        Stack ss = null;
        for (int i = 0; i < ps.size(); i++) {
            Point p = ps.remove(i);
            double d = distanceOf(mainSet, ds, T, p);
            Stack stk  = quickTravelNPOf(mainSet, ds, ps, new Stack(), p, H);
            if (stk.result + d < result) {
                if (index != null) {
                    stack.set(index, p);
                } else {
                    index = stack.size();
                    stack.push(p);
                }
                result = stk.result + d;
                ss = stk;
            }
            ps.add(i, p);
        }
        stack.addAll(ss);
        stack.result = result;
        return stack;
    }

    public void travelNPOf(MainWindow frame, ArrayList<Point> pointSet, ArrayList<PointMaker.PairPoint> lines) throws InterruptedException {
        this.pointSet = pointSet;
        int n = pointSet.size();
        ds = new double[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
                ds[i][j] = distanceOf(i, j);
            }
        }
        distsNP.clear();
        ArrayList<Point> ps = (ArrayList<Point>) pointSet.clone();
        Point H = ps.remove(0);
        travelNPOf(pointSet, ds, frame, ps, lines, H, H);
    }//可以考虑将lines改为一个栈


    public LinkedList<Double> distsNP  = new LinkedList<>();
    private void travelNPOf(ArrayList<Point> mainSet, double[][] ds, MainWindow frame,
                            ArrayList<Point> ps, ArrayList<PointMaker.PairPoint> lines, Point T, Point H) throws InterruptedException {
//        double travelD = 0.0;
        if (ps.size() == 0) {
            PointMaker.PairPoint pairPoint = new PointMaker.PairPoint(T, H);
            lines.add(pairPoint);
            double d = distanceOf(mainSet, ds, T, H);
            frame.distExact += d;
            distsNP.add(frame.distExact);
            frame.repaint();
            Thread.sleep(delay * 2);
            lines.remove(lines.size() - 1);
            frame.distExact -= d;
            frame.repaint();
            Thread.sleep(delay);
        }
        if (!frame.runFlag) {
            throw new InterruptedException("stop()");
        }

        for (int i = 0; i < ps.size(); i++) {
            Point p = ps.remove(i);
            PointMaker.PairPoint pairPoint = new PointMaker.PairPoint(T, p);
            lines.add(pairPoint);//connect
            double d = distanceOf(mainSet, ds, T, p);
            frame.distExact += d;
            frame.repaint();
            Thread.sleep(delay);
            travelNPOf(mainSet,ds, frame, ps, lines, p, H);
            ps.add(i, p);
            lines.remove(lines.size() - 1);//disconnect
            frame.distExact -= d;
            frame.repaint();
            Thread.sleep(delay);
        }
    }

    public long delay = 100;

    private Double distanceOf(ArrayList<Point> mainSet, double[][] ds, Point T, Point H) {
        int a = mainSet.indexOf(T);
        int b = mainSet.indexOf(H);
        int i;
        int j;
        if (a > b) {
            i = b;
            j = a;
        } else {
            i = a;
            j = b;
        }
        return ds[i][j];
    }

    public void paintSolution(Graphics2D d, int baseY) {
        int tmp = baseY;
        int startX = 0;
        int i = 0;
        for (Double aDouble : distsNP) {
            d.drawString("Solution "+(i+1)+" = "+aDouble+" px = "+MainWindow.convert(aDouble),startX,tmp+=15);
            if (i % 60 == 59) {
                tmp = baseY;
                startX += 120;
            }
            i++;
        }
    }

    public void paintSolution(Graphics2D d, int baseY, double v) {
        int tmp = baseY;
        int startX = 0;
        int i = 0;
        for (Double aDouble : distsNP) {
            if (aDouble > v) {
                continue;
            }
            d.drawString("*Solution "+(i+1)+" = "+aDouble+" px = "+MainWindow.convert(aDouble),startX,tmp+=15);
            if (i % 60 == 59) {
                tmp = baseY;
                startX += 120;
            }
            i++;
        }
    }
}
