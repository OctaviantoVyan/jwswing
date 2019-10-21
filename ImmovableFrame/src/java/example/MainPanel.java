// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionListener;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameUI;

// How to Use Internal Frames (The Java™ Tutorials > Creating a GUI With JFC/Swing > Using Swing Components)
// https://docs.oracle.com/javase/tutorial/uiswing/components/internalframe.html
// Lock JInternalPane
// https://community.oracle.com/thread/1392111
public final class MainPanel extends JPanel {
  private static final int OFFSET = 30;
  private static AtomicInteger openFrameCount = new AtomicInteger();
  private final JDesktopPane desktop = new JDesktopPane();

  private MainPanel() {
    super(new BorderLayout());

    // title, resizable, closable, maximizable, iconifiable
    JInternalFrame immovableFrame = new JInternalFrame("immovable", false, false, true, true);
    Component north = ((BasicInternalFrameUI) immovableFrame.getUI()).getNorthPane();
    MouseMotionListener[] actions = north.getListeners(MouseMotionListener.class);
    for (MouseMotionListener l: actions) {
      north.removeMouseMotionListener(l);
    }
    // immovableFrame.setLocation(0, 0);
    immovableFrame.setSize(160, 0);
    desktop.add(immovableFrame);
    immovableFrame.setVisible(true);

    desktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
    desktop.addComponentListener(new ComponentAdapter() {
      @Override public void componentResized(ComponentEvent e) {
        immovableFrame.setSize(immovableFrame.getSize().width, e.getComponent().getSize().height);
      }
    });

    add(desktop);
    add(createMenuBar(), BorderLayout.NORTH);
    setPreferredSize(new Dimension(320, 240));
  }

  protected JMenuBar createMenuBar() {
    JMenu menu = new JMenu("Window");
    menu.setMnemonic(KeyEvent.VK_W);

    JMenuItem menuItem = menu.add("New");
    menuItem.setMnemonic(KeyEvent.VK_N);
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK));
    menuItem.setActionCommand("new");
    menuItem.addActionListener(e -> {
      JInternalFrame frame = createInternalFrame();
      desktop.add(frame);
      frame.setVisible(true);
      // desktop.getDesktopManager().activateFrame(frame);
    });

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(menu);
    return menuBar;
  }

  private static JInternalFrame createInternalFrame() {
    String title = String.format("Document #%s", openFrameCount.getAndIncrement());
    JInternalFrame f = new JInternalFrame(title, true, true, true, true);
    f.setSize(160, 100);
    f.setLocation(OFFSET * openFrameCount.intValue(), OFFSET * openFrameCount.intValue());
    return f;
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
