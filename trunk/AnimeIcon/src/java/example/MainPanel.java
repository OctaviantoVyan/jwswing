package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.*;
//import org.jdesktop.swingworker.SwingWorker;
import javax.swing.SwingWorker;

public class MainPanel extends JPanel {
    private final JTextArea area     = new JTextArea();
    private final JProgressBar bar   = new JProgressBar();
    private final JPanel statusPanel = new JPanel(new BorderLayout());
    private final JButton runButton  = new JButton(new RunAction());
    private final JButton canButton  = new JButton(new CancelAction());
    private final AnimatedLabel anil = new AnimatedLabel();
    private SwingWorker<String, String> worker;

    public MainPanel() {
        super(new BorderLayout());
        area.setEditable(false);
        Box box = Box.createHorizontalBox();
        box.add(anil);
        box.add(Box.createHorizontalGlue());
        box.add(runButton);
        box.add(canButton);
        add(box, BorderLayout.NORTH);
        add(statusPanel, BorderLayout.SOUTH);
        add(new JScrollPane(area));
        setPreferredSize(new Dimension(320, 200));
    }

    class RunAction extends AbstractAction {
        public RunAction() {
            super("run");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            final JProgressBar bar = new JProgressBar(0, 100);
            runButton.setEnabled(false);
            canButton.setEnabled(true);
            anil.startAnimation();
            statusPanel.removeAll();
            statusPanel.add(bar);
            statusPanel.revalidate();
            bar.setIndeterminate(true);
            worker = new SwingWorker<String, String>() {
                @Override public String doInBackground() {
                    //System.out.println("doInBackground() is EDT?: " + EventQueue.isDispatchThread());
                    try{
                        Thread.sleep(1000);
                    }catch(InterruptedException ie) {
                        if(isCancelled()) {
                            worker.cancel(true);
                        }
                        return "Interrupted";
                    }
                    int current = 0;
                    int lengthOfTask = 120; //list.size();
                    publish("Length Of Task: " + lengthOfTask);
                    publish("------------------------------");
                    while(current<lengthOfTask && !isCancelled()) {
                        try{
                            Thread.sleep(50);
                        }catch(InterruptedException ie) {
                            return "Interrupted";
                        }
                        setProgress(100 * current / lengthOfTask);
                        current++;
                    }
                    return "Done";
                }
                @Override protected void process(List<String> chunks) {
                    System.out.println("process() is EDT?: " + EventQueue.isDispatchThread());
                    for(String message : chunks) {
                        appendLine(message);
                    }
                }
                @Override public void done() {
                    //System.out.println("done() is EDT?: " + EventQueue.isDispatchThread());
                    anil.stopAnimation();
                    runButton.setEnabled(true);
                    canButton.setEnabled(false);
                    statusPanel.remove(bar);
                    statusPanel.revalidate();
                    String text = null;
                    if(isCancelled()) {
                        text = "Cancelled";
                    }else{
                        try{
                            text = get();
                        }catch(Exception ex) {
                            ex.printStackTrace();
                            text = "Exception";
                        }
                    }
                    appendLine(text);
                }
            };
            worker.addPropertyChangeListener(new ProgressListener(bar));
            worker.execute();
        }
    }
    class CancelAction extends AbstractAction {
        public CancelAction() {
            super("cancel");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            if(worker!=null && !worker.isDone()) {
                worker.cancel(true);
            }
            worker = null;
        }
    }
    private boolean isCancelled() {
        return (worker!=null)?worker.isCancelled():true;
    }
    private void appendLine(String str) {
        area.append(str+"\n");
        area.setCaretPosition(area.getDocument().getLength());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        //frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        //frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class ProgressListener implements PropertyChangeListener {
    private final JProgressBar progressBar;
    ProgressListener(JProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setValue(0);
    }
    @Override public void propertyChange(PropertyChangeEvent e) {
        String strPropertyName = e.getPropertyName();
        if("progress".equals(strPropertyName)) {
            progressBar.setIndeterminate(false);
            int progress = (Integer)e.getNewValue();
            progressBar.setValue(progress);
        }
    }
}

class AnimatedLabel extends JLabel implements ActionListener {
    private final Timer animator;
    //private final AnimeIcon icon = new AnimeIcon();
    //private final AnimeIcon2 icon = new AnimeIcon2();
    //private final AnimeIcon3 icon = new AnimeIcon3();
    private final AnimeIcon4 icon = new AnimeIcon4();
    public AnimatedLabel() {
        super();
        animator = new Timer(100, this);
        setIcon(icon);
        addHierarchyListener(new HierarchyListener() {
            @Override public void hierarchyChanged(HierarchyEvent e) {
                if((e.getChangeFlags() & HierarchyEvent.DISPLAYABILITY_CHANGED)!=0 && !isDisplayable()) {
                    stopAnimation();
                }
            }
        });
    }
    @Override public void actionPerformed(ActionEvent e) {
        icon.next();
        repaint();
    }
    public void startAnimation() {
        icon.setRunning(true);
        animator.start();
    }
    public void stopAnimation() {
        icon.setRunning(false);
        animator.stop();
    }
}

class AnimeIcon implements Icon {
    private static final Color cColor = new Color(0.5f,0.5f,0.5f);
    private static final double r  = 2.0d;
    private static final double sx = 1.0d;
    private static final double sy = 1.0d;
    private static final Dimension dim = new Dimension((int)(r*8+sx*2), (int)(r*8+sy*2));
    private final List<Shape> list = new ArrayList<Shape>(Arrays.asList(
        new Ellipse2D.Double(sx+3*r, sy+0*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+5*r, sy+1*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+6*r, sy+3*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+5*r, sy+5*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+3*r, sy+6*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+1*r, sy+5*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+0*r, sy+3*r, 2*r, 2*r),
        new Ellipse2D.Double(sx+1*r, sy+1*r, 2*r, 2*r)));

    private boolean isRunning = false;
    public void next() {
        if(isRunning) list.add(list.remove(0));
    }
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    @Override public int getIconWidth()  { return dim.width;  }
    @Override public int getIconHeight() { return dim.height; }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint((c!=null)?c.getBackground():Color.WHITE);
        g2.fillRect(x, y, getIconWidth(), getIconHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cColor);
        float alpha = 0.0f;
        g2.translate(x, y);
        for(Shape s: list) {
            alpha = isRunning?alpha+0.1f:0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(s);
        }
        g2.translate(-x, -y);
    }
}

class AnimeIcon2 implements Icon {
    private static final Color cColor = new Color(0.5f,0.8f,0.5f);
    private final List<Shape> list = new ArrayList<Shape>();
    private final Dimension dim;
    private boolean isRunning = false;
    public AnimeIcon2() {
        super();
        int r = 4;
        Shape s = new Ellipse2D.Float(0, 0, 2*r, 2*r);
        for(int i=0;i<8;i++) {
            AffineTransform at = AffineTransform.getRotateInstance(i*2*Math.PI/8);
            at.concatenate(AffineTransform.getTranslateInstance(r, r));
            list.add(at.createTransformedShape(s));
        }
        //int d = (int)(r*2*(1+2*Math.sqrt(2)));
        int d = (int)r*2*(1+3); // 2*Math.sqrt(2) is nearly equal to 3.
        dim = new Dimension(d, d);
    }
    @Override public int getIconWidth() {
        return dim.width;
    }
    @Override public int getIconHeight() {
        return dim.height;
    }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint((c!=null)?c.getBackground():Color.WHITE);
        g2.fillRect(x, y, getIconWidth(), getIconHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cColor);
        float alpha = 0.0f;
        int xx = x + dim.width/2;
        int yy = y + dim.height/2;
        g2.translate(xx, yy);
        for(Shape s: list) {
            alpha = isRunning?alpha+0.1f:0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(s);
        }
    }
    public void next() {
        if(isRunning) {
            list.add(list.remove(0));
        }
    }
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}

class AnimeIcon3 implements Icon {
    private static final Color cColor = new Color(0.9f,0.7f,0.7f);
    private final List<Shape> list = new ArrayList<Shape>();
    private final Dimension dim;
    private boolean isRunning = false;
    int rotate = 45;
    public AnimeIcon3() {
        super();
        int r = 4;
        Shape s = new Ellipse2D.Float(0, 0, 2*r, 2*r);
        for(int i=0;i<8;i++) {
            AffineTransform at = AffineTransform.getRotateInstance(i*2*Math.PI/8);
            at.concatenate(AffineTransform.getTranslateInstance(r, r));
            list.add(at.createTransformedShape(s));
        }
        int d = (int)r*2*(1+3);
        dim = new Dimension(d, d);
    }
    @Override public int getIconWidth() {
        return dim.width;
    }
    @Override public int getIconHeight() {
        return dim.height;
    }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setPaint((c!=null)?c.getBackground():Color.WHITE);
        g2.fillRect(x, y, getIconWidth(), getIconHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cColor);
        float alpha = 0.0f;
        int xx = x + dim.width/2;
        int yy = y + dim.height/2;
        AffineTransform at = AffineTransform.getRotateInstance(Math.toRadians(rotate), xx, yy);
        at.concatenate(AffineTransform.getTranslateInstance(xx, yy));
        for(Shape s: list) {
            alpha = isRunning?alpha+0.1f:0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(at.createTransformedShape(s));
        }
    }
    public void next() {
        if(isRunning) {
            rotate = rotate<360?rotate+45:45;
        }
    }
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}


class AnimeIcon4 implements Icon {
    private static final Color cColor = new Color(0.5f,0.8f,0.5f);
    private final Dimension dim;
    private boolean isRunning = false;
    private final List<Shape> list = new ArrayList<Shape>();
    int r = 4;
    public AnimeIcon4() {
        super();
        int d = (int)r*2*(1+3);
        dim = new Dimension(d, d);

        Ellipse2D.Float cricle = new Ellipse2D.Float(r, r, d-2*r, d-2*r);
        PathIterator i = new FlatteningPathIterator(cricle.getPathIterator(null), r);
        float[] coords = new float[6];
        int idx = 0;
        while(!i.isDone()) {
            i.currentSegment(coords);
            if(idx++ < 8) { // XXX
                list.add(new Ellipse2D.Float(coords[0]-r, coords[1]-r, 2*r, 2*r));
            }
            i.next();
        }
    }
    @Override public int getIconWidth() {
        return dim.width;
    }
    @Override public int getIconHeight() {
        return dim.height;
    }
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint((c!=null)?c.getBackground():Color.WHITE);
        g2.fillRect(x, y, getIconWidth(), getIconHeight());
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(cColor);
        float alpha = 0.1f;
        float p = (1f - alpha)/(float)list.size();
        for(Shape s: list) {
            alpha = isRunning?alpha+p:0.5f;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g2.fill(s);
        }
    }
    public void next() {
        if(isRunning) {
            list.add(list.remove(0));
        }
    }
    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
