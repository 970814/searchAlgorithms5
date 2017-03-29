package travel.window;

import travel.Traveler;
import travel.point.PointMaker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

/**
 * Created by L on 2016/10/4.
 */
public class MainWindow extends JFrame {
    int Width = 1500;
    int Height = 900;
    PointMaker maker = new PointMaker(new PointMaker.Bounds(100, 100, Width - 100, Height - 100)) {
        {

        }
    };
    Traveler traveler = new Traveler();
    Point point = new Point(0, 0);
    JPopupMenu popupMenu = new JPopupMenu();
    String[] str = {"show number", "show location", "show MaxSize"};
    String[] str2 = {"normal", "add", "remove", "select","moveTo"};
    JCheckBoxMenuItem[] items = new JCheckBoxMenuItem[str.length];
    JRadioButtonMenuItem[] radioItems = new JRadioButtonMenuItem[str2.length];
    Double dist = null;
    Double dist0 = null;
    public Double distExact = null;
    public Double result = null;

    boolean addMode = false;
    boolean removeMode = false;
    boolean normalMode = true;
    boolean selectMode = false;
    boolean movedMode = false;
    Rectangle rectangle = null;
    Point start = null;
    boolean showAllSolution = false;
    boolean showBetterSolution = false;
    public boolean runFlag = false;
    Traveler.Stack stack = new Traveler.Stack();
    StringBuilder path ;
    StringBuilder path2 ;
    Stack<Point> movePoints = new Stack<>();
//    private boolean checkMod(int modifiers, int mask) {
//        return ((modifiers & mask) == mask);
//    }

    public MainWindow() throws HeadlessException {

        for (int i = 0; i < items.length; i++) {
            items[i] = new JCheckBoxMenuItem(str[i]);
            popupMenu.add(items[i]);
        }
        items[0].addActionListener((l) -> {
            maker.paintPoint = !maker.paintPoint;
            MainWindow.this.repaint();
        });
        items[1].addActionListener((l) -> {
            maker.paintLocation = !maker.paintLocation;
            MainWindow.this.repaint();
        });
        items[2].addActionListener((l) -> {
            maker.paintLength = !maker.paintLength;
            MainWindow.this.repaint();
        });
        ButtonGroup group = new ButtonGroup();
        for (int i = 0; i <radioItems.length; i++) {
            radioItems[i] = new JRadioButtonMenuItem(str2[i]);
            popupMenu.add(radioItems[i]);
            group.add(radioItems[i]);
        }
        radioItems[0].setSelected(true);
        radioItems[0].addActionListener((l) ->{
            normalMode = true;
            addMode = false;
            removeMode = false;
            selectMode = false;
            movedMode = false;
            movePoints.clear();
        });
        radioItems[1].addActionListener((l) ->{
            normalMode = false;
            addMode = true;
            removeMode = false;
            selectMode = false;
            movedMode = false;
            movePoints.clear();
        });
        radioItems[2].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            removeMode = true;
            movedMode = false;
            selectMode = false;
            movePoints.clear();
        });
        radioItems[3].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            removeMode = false;
            selectMode = true;
            movedMode = false;
        });
        radioItems[4].addActionListener((l) -> {
            normalMode = false;
            addMode = false;
            removeMode = false;
            selectMode = false;
            movedMode = true;
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
//                System.out.println(key);
                switch (key) {
                    case 32://space
                        showAllSolution = !showAllSolution;
                        break;
                    case 10://enter
                        showBetterSolution = !showBetterSolution;
                        break;
                    case 127://delete
                        movePoints.clear();
                }
                MainWindow.this.repaint();
            }
        });
        setSize(Width, Height);

        setContentPane(mainComponent = new JComponent() {

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if ((removeMode || selectMode || movedMode) && e.getButton() == MouseEvent.BUTTON1) {
                            start = e.getPoint();
                        } else {
                            start = null;
                        }
                    }
                    public void mouseReleased(MouseEvent event) {
                        if (event.isPopupTrigger()) {
                            popupMenu.show(event.getComponent(), event.getX(), event.getY());
                        } else if (normalMode) {
                            return;
                        } else if (addMode) {
                            maker.addNewPoint(event.getPoint());
                            dist = null;
                            MainWindow.this.repaint();
                        } else if (removeMode) {
                            if (runFlag) {
                                rectangle = null;
                                MainWindow.this.repaint();
                                return;
                            }
                            maker.clearX();
                            if (rectangle == null) return;
                            Point location = rectangle.getLocation();
                            Dimension size = rectangle.getSize();

                            for (int i = 0; i < maker.pointSet.size(); i++) {
                                Point p = maker.pointSet.get(i);
                                if (withinBounds(p, location.x, size.width + location.x, location.y, size.height + location.y)) {
                                    maker.pointSet.remove(i);
                                    Object o = maker.buttonList.remove(i);
                                    if (o != null) {
                                        mainComponent.remove((JButton) o);
                                    }
                                    i--;
                                }
                            }
                            rectangle = null;
                        } else if (selectMode) {
                            if (runFlag) {
                                rectangle = null;
                                MainWindow.this.repaint();
                                return;
                            }
//                            maker.clearX();
                            maker.pairs.clear();
                            if (rectangle == null) {
                                return;
                            }
                            Point location = rectangle.getLocation();
                            Dimension size = rectangle.getSize();

                            for (int i = 0; i < maker.pointSet.size(); i++) {
                                Point p = maker.pointSet.get(i);
                                if (withinBounds(p, location.x, size.width + location.x, location.y, size.height + location.y)) {
//                                    Point tmp = maker.pointSet.get(i);
                                    Object o = maker.buttonList.get(i);
                                    if (o != null) {
                                        mainComponent.remove((JButton) o);
                                        maker.buttonList.set(i, null);
                                    }
                                    if (!movePoints.contains(p)) {
                                        movePoints.add(p);
                                    }

                                }
                            }
                            rectangle = null;
                        }

                        MainWindow.this.repaint();
                    }
                });
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        point = e.getPoint();
                        MainWindow.this.repaint();
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        point = e.getPoint();
                        if ((removeMode || selectMode) && start != null) {
                            Point end = e.getPoint();
                            rectangle = new Rectangle(Math.min(start.x, end.x), Math.min(start.y, end.y), Math.abs(end.x - start.x), Math.abs(end.y - start.y));
                            MainWindow.this.repaint();
                        } else if (movedMode && start != null) {
                            Point end = e.getPoint();
                            int x = end.x - start.x;
                            int y = end.y - start.y;
                            start = end;
                            for (int i = 0; i < movePoints.size(); i++) {
                                Point p = movePoints.get(i);
//                                maker.pointSet.remove(maker.pointSet.indexOf(p));
                                p.x += x;
                                p.y += y;
//                                maker.addNewPoint(p);
                            }
                            MainWindow.this.repaint();
                        }
                    }
                });

            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D d = (Graphics2D) g;
                d.setColor(Color.BLUE.darker());
                d.fillRect(0, 0, Width - 50, Height - 80);
                maker.paintPoint(d);
                maker.paintLocation(d);
                maker.paintPath(d);
                maker.paintLines(d);
                d.setColor(Color.YELLOW);
                d.drawString("the number of place: " + maker.pointSet.size(), 0, 10);
                d.drawString("x: " + point.x + ", y: " + point.y, 0, 25);
                if (dist != null) {
                    d.drawString("双调旅行近似解", 0, 40);
                    d.drawString("旅行近似最短路径: " + (dist <= 0 ? "computing..." : dist + " px = " + convert(dist) + " cm"), 0, 55);
                } else if (distExact != null) {
                    d.drawString("NP exact solution", 0, 40);
                    d.drawString((distExact <= 0 ? "" : distExact + " px = " + convert(distExact) + " cm")+", computing...", 0, 55);
                } else if (result != null) {
                    d.drawString("result(NP): " + (result <= 0 ? "computing..." : result + " px = " + convert(result) + " cm") + (path == null ? "" : path.toString()), 0, 55);
                }
                if (dist0 != null) {
                    d.drawString("lastDist: " + dist0 + " px = " + convert(dist0) + " cm" + (path2 == null ? "" : path2.toString()), 0, 70);
                }
                if (showAllSolution) {
                    traveler.paintSolution(d, 85);
                }
                if (showBetterSolution && dist0 != null) {
                    traveler.paintSolution(d, 85, dist0);
                }

                d.setStroke(new BasicStroke(1.0f));
                if (rectangle != null) {
                    if (removeMode) {
                        d.setColor(Color.WHITE);
                    } else {
                        d.setColor(Color.BLACK);
                    }
                    d.draw(rectangle);
                }
                d.setColor(Color.GREEN.brighter());
                for (Point p : movePoints) {
//                    Point p = maker.pointSet.get(integer);
                    d.drawString("*", p.x, p.y);
                }
            }
        });
        setJMenuBar(new JMenuBar() {
            {
                add(new JMenu("Run") {
                    {
                        setMnemonic('R');
                        add(new JMenuItem("Computing(Approx)") {
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt C"));
                                setMnemonic('C');
                                addActionListener((l) -> {
                                    if (maker.pointSet.size() < 2 || runFlag) {
                                        return;
                                    }
                                    {
                                        runFlag = true;
                                        maker.lines.clear();
                                        stack.clear();
                                        maker.pairs.clear();
                                        distExact = null;
//                                        result = null;
//                                        path = null;
                                        new Thread(() -> {
                                            dist = -1.0;
                                            traveler.travelOf(maker.pointSet);
                                            double[][] d = traveler.d;
                                            dist = d[d.length - 1][d.length - 1];
                                            dist0 = dist;
                                            int[] s = traveler.s;
                                            int i = traveler.L;
                                            maker.lines.add(new PointMaker.Pair(traveler.L, traveler.L + 1));
                                            do {
                                                if (!runFlag) {//可能在其他线程中关闭其运行
                                                    maker.lines.clear();
                                                    return;
                                                }
                                                int k = s[i];
                                                try {
                                                    Thread.sleep(5);
                                                } catch (InterruptedException e) {
                                                }
                                                maker.lines.add(new PointMaker.Pair(k, i + 1));
                                                MainWindow.this.repaint();
                                                for (int m = k + 1; m < i; m++) {
                                                    try {
                                                        Thread.sleep(5);
                                                    } catch (InterruptedException e) {
                                                    }
                                                    maker.lines.add(new PointMaker.Pair(m, m + 1));
                                                    MainWindow.this.repaint();
                                                }
                                                i = k;
                                            } while (i > 0);
                                            maker.lines.add(new PointMaker.Pair(0, 1));
                                            path2 = new StringBuilder(", path >>> 0");
                                            if (maker.pointSet.size() > 2) {
                                                StringBuilder b = new StringBuilder();
                                                int k = 0;
                                                int k2 = 0;
                                                PointMaker.Pair x = null;
                                                PointMaker.Pair x2 = null;
                                                for (PointMaker.Pair line : maker.lines) {
                                                    if (line.i == k) {
                                                        if (x == null) {
                                                            x = line;
                                                        } else {
                                                            x2 = line;
                                                            break;
                                                        }
                                                    }
                                                }
                                                k = x.j;
                                                k2 = x2.j;
                                                path2.append(" -> " + k);
                                                b.append(k2);
                                                int count = 2;
                                                D:
                                                do {
                                                    if (count == maker.lines.size()) {
                                                        break D;
                                                    }
                                                    for (PointMaker.Pair line : maker.lines) {
                                                        if (line.i == k) {
                                                            k = line.j;
                                                            path2.append(" -> " + k);
                                                            count++;
                                                        }
                                                        if (line.i == k2) {
                                                            k2 = line.j;
                                                            b.append("," + k2);
                                                            count++;
                                                        }
                                                    }
                                                } while (true && runFlag);
                                                String[] strs = b.toString().split(",");
                                                for (int n = strs.length - 2; n >= 0; n--) {
                                                    path2.append(" -> " + strs[n]);
                                                }
                                            } else {
                                                path2.append(" -> " + 1);
                                            }
                                            path2.append(" -> " + 0);
                                            MainWindow.this.repaint();
                                            runFlag = false;
                                        }).start();
                                    }
                                });
                            }
                        });
                        add(new JMenuItem("Computing(NP)") {
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt N"));
                                setMnemonic('P');
                                addActionListener((l) -> {
                                    if (maker.pointSet.size() < 2 || runFlag) {
                                        return;
                                    }
                                    {
                                        runFlag = true;
                                        maker.clearX();
                                        stack.clear();
                                        dist = null;
                                        result = null;
                                        path = null;
                                        new Thread(() -> {
                                            distExact = -1.0;
                                            try {
                                                traveler.travelNPOf(MainWindow.this, maker.pointSet, maker.pairs);
                                            } catch (InterruptedException e) {
                                            }finally {
                                                runFlag = false;
                                                MainWindow.this.repaint();
                                            }
                                        }).start();
                                    }
                                });
                            }
                        });
                        add(new JMenuItem("Computing(QuickResult)") {
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt Q"));
                                setMnemonic('Q');
                                addActionListener((l) -> {
                                    if (maker.pointSet.size() < 2 || runFlag) {
                                        return;
                                    }
                                    {
                                        runFlag = true;
                                        maker.clearX();
                                        traveler.clear();
                                        dist = null;
                                        distExact = null;
                                        path = null;
                                        new Thread(() -> {
                                            result = -1.0;
                                            stack.clear();
                                            try {
                                                result = traveler.quickTravelNPOf(maker.pointSet, stack);

                                                path = new StringBuilder(", path >>> ");
                                                for (Point point : stack) {
                                                    path.append(maker.pointSet.indexOf(point)).append(" -> ");
                                                }
                                                path.append(maker.pointSet.indexOf(stack.get(0)));
                                                stack.add(stack.get(0));
                                                for (int i = 0; i < stack.size()-1; i++) {
                                                    maker.pairs.add(new PointMaker.PairPoint(stack.get(i), stack.get(i + 1)));
                                                }
                                            } catch (InterruptedException e) {
                                            }finally {
                                                runFlag = false;

                                                MainWindow.this.repaint();
                                            }
                                        }).start();
                                    }
                                });
                            }
                        });
                        add(new JMenuItem("New...") {
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
                                setMnemonic('N');
                                addActionListener((l) -> {
                                    runFlag = false;
                                    traveler.clear();
                                    maker.clear();
                                    stack.clear();
                                    dist = null;
                                    distExact = null;
                                    result = null;
                                    path = null;
                                    dist0 = null;
                                    path2 = null;
                                    new Thread(() -> {
                                        String str = JOptionPane.showInputDialog("please input the number of point: ");
                                        int n;
                                        try {
                                            n = Integer.valueOf(str);//null or parse exception
                                        } catch (NumberFormatException e) {
                                            return;
                                        } catch (NullPointerException e) {
                                            return;
                                        }
                                        maker.make(mainComponent, n);
                                        dist = null;
                                        MainWindow.this.repaint();
                                    }).start();
                                });
                            }
                        });
                        add(new JMenuItem("Switch") {
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt S"));
                                setMnemonic('S');
                                addActionListener((l) ->
                                        new Thread(() -> {
                                            runFlag = false;
                                            traveler.clear();
                                            stack.clear();
                                            maker.clearX();
                                            distExact = null;
                                            dist = null;
                                            dist0 = null;
                                            result = null;
                                            path = null;
                                            path2 = null;
                                            maker.make(mainComponent, maker.pointSet.size());
                                            MainWindow.this.repaint();
                                        }).start()
                                );
                            }
                        });
                        add(new JMenuItem("Reset"){
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt R"));
                                setMnemonic('R');
                                addActionListener((l) ->
                                        new Thread(() -> {
                                            maker.clear();
                                            traveler.clear();
                                            stack.clear();
                                            mainComponent.removeAll();
                                            dist = null;
                                            dist0 = null;
                                            distExact = null;
                                            result = null;
                                            path = null;
                                            path2 = null;
                                            runFlag = false;
                                            movePoints.clear();
                                            MainWindow.this.repaint();
                                        }).start()
                                );
                            }
                        });
                        add(new JMenuItem("ClearPathAndAllThread"){
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt P"));
                                setMnemonic('P');
                                addActionListener((l) ->
                                        new Thread(() -> {
                                            maker.clearX();
                                            traveler.clear();
                                            stack.clear();
                                            dist = null;
                                            result = null;
                                            path = null;
                                            path2 = null;
                                            dist0 = null;
                                            distExact = null;
                                            runFlag = false;
                                            MainWindow.this.repaint();
                                        }).start()
                                );
                            }
                        });
                        add(new JMenuItem("SetDelay"){
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt D"));
                                setMnemonic('D');
                                addActionListener((l) ->
                                        new Thread(() -> {
                                            traveler.delay = Long.parseLong(JOptionPane.showInputDialog("please enter the time interval(ms): "));
                                        }).start());

                            }
                        });
                        add(new JMenuItem("removeAllButton"){
                            {
                                setAccelerator(KeyStroke.getKeyStroke("ctrl alt B"));
                                setMnemonic('B');
                                addActionListener((l) ->{
                                    mainComponent.removeAll();
                                    maker.buttonList.clear();
                                    MainWindow.this.repaint();
                                });
                            }
                        });
                    }
                });
            }
        });
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static double convert(double distExact) {
//        return distExact * (34.5 / 1920);
        return distExact * (50.0 / 1920);
    }

    private boolean withinBounds(Point p, int x1, int x2, int y1, int y2) {
        return (x1 <= p.x && p.x <= x2) && (y1 <= p.y && p.y <= y2);
    }

    final JComponent mainComponent;


    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
