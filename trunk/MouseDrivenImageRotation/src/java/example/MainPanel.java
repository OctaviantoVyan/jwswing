package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

public final class MainPanel extends JPanel {
    private final transient DraggableImageMouseListener di;
    public MainPanel() {
        super();
        di = new DraggableImageMouseListener(new ImageIcon(getClass().getResource("test.png")));
        addMouseListener(di);
        addMouseMotionListener(di);
        setPreferredSize(new Dimension(320, 240));
    }
    @Override public void paintComponent(Graphics g) {
        //super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setPaint(new GradientPaint(50, 0, new Color(200,200,200),
                                       getWidth(), getHeight(), new Color(100,100,100), true));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        di.paint(g, this);
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

class DraggableImageMouseListener extends MouseAdapter {
    private static final Color HOVER_COLOR = new Color(100, 255, 200, 100);
    private static final int IR = 40;
    private static final int OR = IR*3;
    private static final BasicStroke BORDER_STROKE = new BasicStroke(4.0f);
    private final RoundRectangle2D.Double border;
    private final Ellipse2D.Double INNER = new Ellipse2D.Double(0,0,IR,IR);
    private final Ellipse2D.Double OUTER = new Ellipse2D.Double(0,0,OR,OR);
    public final Image image;
    public final int width;
    public final int height;
    public final double centerX, centerY;
    public double x = 10.0, y = 50.0, radian = 45.0 * (Math.PI / 180.0);
    public double startX, startY, startA;
    private boolean moverHover, rotatorHover;

    public DraggableImageMouseListener(ImageIcon ii) {
        super();
        image   = ii.getImage();
        width   = ii.getIconWidth();
        height  = ii.getIconHeight();
        centerX = width/2.0;
        centerY = height/2.0;
        INNER.x = x+centerX-IR/2;
        INNER.y = y+centerY-IR/2;
        OUTER.x = x+centerX-OR/2;
        OUTER.y = y+centerY-OR/2;
        border  = new RoundRectangle2D.Double(0, 0, width, height, 10.0, 10.0);
    }
    public void paint(Graphics g, ImageObserver ior) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);
        at.rotate(radian, centerX, centerY);

        g2d.setPaint(Color.WHITE);
        g2d.setStroke(BORDER_STROKE);
        Shape s = new Rectangle2D.Double(border.x-2,border.y-2,border.width+4,border.height+20);
        g2d.fill(at.createTransformedShape(s));
        g2d.draw(at.createTransformedShape(s));

        g2d.drawImage(image, at, ior);
        if(rotatorHover) {
            Area donut = new Area(OUTER);
            donut.subtract(new Area(INNER));
            g2d.setPaint(HOVER_COLOR);
            g2d.fill(donut);
        }else if(moverHover) {
            g2d.setPaint(HOVER_COLOR);
            g2d.fill(INNER);
        }
        g2d.setStroke(BORDER_STROKE);
        g2d.setPaint(Color.WHITE);
        g2d.draw(at.createTransformedShape(border));
        g2d.dispose();
    }
    @Override public void mouseMoved(MouseEvent e) {
        if(OUTER.contains(e.getX(), e.getY()) && !INNER.contains(e.getX(), e.getY())) {
            moverHover = false; rotatorHover = true;
        }else if(INNER.contains(e.getX(), e.getY())) {
            moverHover = true;  rotatorHover = false;
        }else{
            moverHover = rotatorHover =false;
        }
        ((JComponent)e.getSource()).repaint();
    }
    @Override public void mouseReleased(MouseEvent e) {
        rotatorHover = moverHover = false;
        ((JComponent)e.getSource()).repaint();
    }
    @Override public void mousePressed(MouseEvent e) {
        if(OUTER.contains(e.getX(), e.getY()) && !INNER.contains(e.getX(), e.getY())) {
            rotatorHover = true;
            startA = radian - Math.atan2(e.getY()-y-centerY, e.getX()-x-centerX);
            ((JComponent)e.getSource()).repaint();
        }else if(INNER.contains(e.getX(), e.getY())) {
            moverHover = true;
            startX = e.getX();
            startY = e.getY();
            ((JComponent)e.getSource()).repaint();
        }
    }
    @Override public void mouseDragged(MouseEvent e) {
        if(rotatorHover) {
            radian = startA + Math.atan2(e.getY()-y-centerY, e.getX()-x-centerX);
            ((JComponent)e.getSource()).repaint();
        }else if(moverHover) {
            x += e.getX() - startX;
            y += e.getY() - startY;
            INNER.x = x+centerX-IR/2;
            INNER.y = y+centerY-IR/2;
            OUTER.x = x+centerX-OR/2;
            OUTER.y = y+centerY-OR/2;
            startX = e.getX();
            startY = e.getY();
            ((JComponent)e.getSource()).repaint();
        }
    }
}
