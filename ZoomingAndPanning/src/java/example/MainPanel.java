// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private transient ZoomAndPanHandler zoomAndPanHandler;
  private final ImageIcon icon;

  private MainPanel() {
    super(new BorderLayout());
    String path = "example/CRW_3857_JFR.jpg"; // http://sozai-free.com/
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Image img = Optional.ofNullable(cl.getResource(path)).map(u -> {
      try (InputStream s = u.openStream()) {
        return ImageIO.read(s);
      } catch (IOException ex) {
        return makeMissingImage();
      }
    }).orElseGet(MainPanel::makeMissingImage);
    icon = new ImageIcon(img);
    setPreferredSize(new Dimension(320, 240));
  }

  @Override public void updateUI() {
    removeMouseListener(zoomAndPanHandler);
    removeMouseMotionListener(zoomAndPanHandler);
    removeMouseWheelListener(zoomAndPanHandler);
    super.updateUI();
    zoomAndPanHandler = new ZoomAndPanHandler();
    addMouseListener(zoomAndPanHandler);
    addMouseMotionListener(zoomAndPanHandler);
    addMouseWheelListener(zoomAndPanHandler);
  }

  @Override protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setTransform(zoomAndPanHandler.getCoordAndZoomTransform());
    icon.paintIcon(this, g2, 0, 0);
    g2.dispose();
  }

  private static Image makeMissingImage() {
    Icon missingIcon = new MissingIcon();
    int w = missingIcon.getIconWidth();
    int h = missingIcon.getIconHeight();
    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = bi.createGraphics();
    missingIcon.paintIcon(null, g2, 0, 0);
    g2.dispose();
    return bi;
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(MainPanel::createAndShowGui);
  }

  private static void createAndShowGui() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
      ex.printStackTrace();
      Toolkit.getDefaultToolkit().beep();
    }
    JFrame frame = new JFrame("@title@");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new MainPanel());
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}

class ZoomAndPanHandler extends MouseAdapter {
  private static final double ZOOM_FACTOR = 1.2;
  private static final int MIN = -10;
  private static final int MAX = 10;
  private static final int EXTENT = 1;
  private final BoundedRangeModel zoomRange = new DefaultBoundedRangeModel(0, EXTENT, MIN, MAX + EXTENT);
  private final AffineTransform coordAndZoomAtf = new AffineTransform();
  private final Point2D dragStartPoint = new Point();

  @Override public void mousePressed(MouseEvent e) {
    dragStartPoint.setLocation(e.getPoint());
  }

  @Override public void mouseDragged(MouseEvent e) {
    Point2D dragEndPoint = e.getPoint();
    Point2D dragStart = transformPoint(dragStartPoint);
    Point2D dragEnd = transformPoint(dragEndPoint);
    coordAndZoomAtf.translate(dragEnd.getX() - dragStart.getX(), dragEnd.getY() - dragStart.getY());
    dragStartPoint.setLocation(dragEndPoint);
    e.getComponent().repaint();
  }

  @Override public void mouseWheelMoved(MouseWheelEvent e) {
    int dir = e.getWheelRotation();
    int z = zoomRange.getValue();
    zoomRange.setValue(z + EXTENT * (dir > 0 ? -1 : 1));
    if (z == zoomRange.getValue()) {
      return;
    }
    Component c = e.getComponent();
    Rectangle r = c.getBounds();
    Point2D p = new Point2D.Double(r.getCenterX(), r.getCenterY());
    Point2D p1 = transformPoint(p);
    double scale = dir > 0 ? 1 / ZOOM_FACTOR : ZOOM_FACTOR;
    coordAndZoomAtf.scale(scale, scale);
    Point2D p2 = transformPoint(p);
    coordAndZoomAtf.translate(p2.getX() - p1.getX(), p2.getY() - p1.getY());
    c.repaint();
  }

  // https://community.oracle.com/thread/1263955
  // How to implement Zoom & Pan in Java using Graphics2D
  private Point2D transformPoint(Point2D p1) {
    AffineTransform inverse = coordAndZoomAtf;
    boolean hasInverse = coordAndZoomAtf.getDeterminant() != 0d;
    if (hasInverse) {
      try {
        inverse = coordAndZoomAtf.createInverse();
      } catch (NoninvertibleTransformException ex) {
        // should never happen
        assert false;
      }
    }
    Point2D p2 = new Point();
    inverse.transform(p1, p2);
    return p2;
  }

  public AffineTransform getCoordAndZoomTransform() {
    return coordAndZoomAtf;
  }
}

class MissingIcon implements Icon {
  @Override public void paintIcon(Component c, Graphics g, int x, int y) {
    Graphics2D g2 = (Graphics2D) g.create();

    int w = getIconWidth();
    int h = getIconHeight();
    int gap = w / 5;

    g2.setColor(Color.WHITE);
    g2.fillRect(x, y, w, h);

    g2.setColor(Color.RED);
    g2.setStroke(new BasicStroke(w / 8f));
    g2.drawLine(x + gap, y + gap, x + w - gap, y + h - gap);
    g2.drawLine(x + gap, y + h - gap, x + w - gap, y + gap);

    g2.dispose();
  }

  @Override public int getIconWidth() {
    return 1000;
  }

  @Override public int getIconHeight() {
    return 1000;
  }
}
