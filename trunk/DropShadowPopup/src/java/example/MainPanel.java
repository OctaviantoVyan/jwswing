package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.border.*;

class MainPanel extends JPanel {
    private final JCheckBox check = new JCheckBox("Paint Shadow", true);
    private final JLabel label = new JLabel();
    private JPopupMenu popup0;
    private DropShadowPopupMenu popup1;
    public MainPanel() {
        super(new BorderLayout());
        label.setIcon(new ImageIcon(getClass().getResource("test.png")));
        label.setComponentPopupMenu(popup1);
        check.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                JCheckBox c = (JCheckBox)e.getSource();
                label.setComponentPopupMenu(c.isSelected()?popup1:popup0);
            }
        });
        add(check, BorderLayout.NORTH);
        add(label);
        setPreferredSize(new Dimension(320, 240));
    }
    @Override public void updateUI() {
        super.updateUI();
        popup0 = new JPopupMenu();
        popup1 = new DropShadowPopupMenu();
        initPopupMenu(popup0);
        initPopupMenu(popup1);
    }
    private static void initPopupMenu(JPopupMenu p) {
        for(JComponent c:Arrays.<JComponent>asList(new JMenuItem("Open(dummy)"),
                                                   new JMenuItem("Save(dummy)"),
                                                   new JMenuItem("Close(dummy)"),
                                                   new JSeparator(),
                                                   new JMenuItem(new AbstractAction("Exit") {
                                                       @Override public void actionPerformed(ActionEvent e) {
                                                           JMenuItem m = (JMenuItem)e.getSource();
                                                           JPopupMenu popup = (JPopupMenu)m.getParent();
                                                           JComponent invoker = (JComponent)popup.getInvoker();
                                                           Window f = SwingUtilities.getWindowAncestor(invoker);
                                                           if(f!=null) { f.dispose(); }
                                                       }
                                                   }))) {
            c.setOpaque(true);
            p.add(c);
        }
        // Bug ID: 6595814 Nimbus LAF: Renderers, MenuSeparators, colors rollup bug
        // http://bugs.sun.com/view_bug.do?bug_id=6595814
        //p.addSeparator();
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
        }catch(ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
//*
class DropShadowPopupMenu extends JPopupMenu {
    private static final int off = 4;
    private BufferedImage shadow = null;
    private Border inner = null;
    @Override public boolean isOpaque() {
        return false;
    }
    @Override public void updateUI() {
        //clear shadow border
        inner = null;
        setBorder(null);
        super.updateUI();
    }
    @Override public void paintComponent(Graphics g) {
        //super.paintComponent(g); //???: Windows LnF
        Graphics2D g2 = (Graphics2D)g.create();
        g2.drawImage(shadow, 0, 0, this);
        g2.setPaint(getBackground()); //??? 1.7.0_03
        g2.fillRect(0,0,getWidth()-off,getHeight()-off);
        g2.dispose();
    }
    @Override public void show(Component c, int x, int y) {
        if(inner==null) { inner = getBorder(); }
        setBorder(makeShadowBorder(c, new Point(x, y)));

        Dimension d = getPreferredSize();
        int w = d.width, h = d.height;
        if(shadow==null || shadow.getWidth()!=w || shadow.getHeight()!=h) {
            shadow = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = shadow.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setPaint(Color.BLACK);
            for(int i=0;i<off;i++) {
                g2.fillRoundRect(off, off, w-off-off+i, h-off-off+i, 4, 4);
            }
            g2.dispose();
        }
        super.show(c, x, y);
    }
    private Border makeShadowBorder(Component c, Point p) {
        Rectangle r = SwingUtilities.getWindowAncestor(c).getBounds();
        Dimension d = this.getPreferredSize();
        SwingUtilities.convertPointToScreen(p, c);
        //System.out.println(r+" : "+p);
        //pointed out by sawshun
        Border outer;
        if(r.contains(p.x, p.y, d.width+off, d.height+off)) {
            outer = BorderFactory.createEmptyBorder(0,0,off,off);
        }else{
            outer = new ShadowBorder(off,off,this,p);
        }
        return BorderFactory.createCompoundBorder(outer, inner);
    }
}

class ShadowBorder extends AbstractBorder {
    private final int xoff, yoff;
    private final Insets insets;
    private BufferedImage screen = null;
    private BufferedImage shadow = null;

    public ShadowBorder(int x, int y, JComponent c, Point p) {
        super();
        this.xoff = x;
        this.yoff = y;
        this.insets = new Insets(0,0,xoff,yoff);
        try{
            Robot robot = new Robot();
            Dimension d = c.getPreferredSize();
            screen = robot.createScreenCapture(new Rectangle(p.x, p.y, d.width+xoff, d.height+yoff));
        }catch(AWTException ex) {
            ex.printStackTrace();
        }
    }
    @Override public Insets getBorderInsets(Component c) {
        return insets;
    }
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        if(screen==null) { return; }
        if(shadow==null || shadow.getWidth()!=w || shadow.getHeight()!=h) {
            shadow = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = shadow.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setPaint(Color.BLACK);
            for(int i=0;i<xoff;i++) {
                g2.fillRoundRect(xoff, xoff, w-xoff-xoff+i, h-xoff-xoff+i, 4, 4);
            }
            g2.dispose();
        }
        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(screen, 0, 0, c);
        g2d.drawImage(shadow, 0, 0, c);
        g2d.setPaint(c.getBackground()); //??? 1.7.0_03
        g2d.fillRect(x,y,w-xoff,h-yoff);
    }
}
/*/
//JDK 1.7.0: JPopupMenu#setBackground(new Color(0,true));
class DropShadowPopupMenu extends JPopupMenu {
    private static final int off = 4;
    private BufferedImage shadow = null;
    private Border border = null;
    @Override public boolean isOpaque() {
        return false;
    }
    @Override public void updateUI() {
        setBorder(null);
        super.updateUI();
        border = null;
    }
    @Override public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.drawImage(shadow, 0, 0, this);
        g2.setPaint(getBackground()); //??? 1.7.0_03
        g2.fillRect(0,0,getWidth()-off,getHeight()-off);
        g2.dispose();
    }
    @Override public void show(Component c, int x, int y) {
        if(border==null) {
            Border inner = getBorder();
            Border outer = BorderFactory.createEmptyBorder(0, 0, off, off);
            border = BorderFactory.createCompoundBorder(outer, inner);
        }
        setBorder(border);
        Dimension d = getPreferredSize();
        int w = d.width, h = d.height;
        if(shadow==null || shadow.getWidth()!=w || shadow.getHeight()!=h) {
            shadow = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = shadow.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setPaint(Color.BLACK);
            for(int i=0;i<off;i++) {
                g2.fillRoundRect(off, off, w-off-off+i, h-off-off+i, 4, 4);
            }
            g2.dispose();
        }
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                Window pop = SwingUtilities.getWindowAncestor(DropShadowPopupMenu.this);
                if(pop instanceof JWindow) {
                    pop.setBackground(new Color(0,true)); //JDK 1.7.0
                }
            }
        });
        super.show(c, x, y);
    }
}
//*/
