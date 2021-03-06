// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.net.URL;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());
    // toolBar1.putClientProperty("JToolBar.isRollover", Boolean.FALSE);
    // toolBar2.putClientProperty("JToolBar.isRollover", Boolean.FALSE);

    URL url1 = getClass().getResource("/toolBarButtonGraphics/general/Copy24.gif");
    URL url2 = getClass().getResource("/toolBarButtonGraphics/general/Cut24.gif");
    URL url3 = getClass().getResource("/toolBarButtonGraphics/general/Help24.gif");

    // When jlfgr-1_0.jar does not exist in the classpath
    if (Objects.isNull(url1)) {
      url1 = getClass().getResource("Copy24.gif");
      url2 = getClass().getResource("Cut24.gif");
      url3 = getClass().getResource("Help24.gif");
    }

    JToolBar toolBar1 = new JToolBar("ToolBarButton");
    toolBar1.add(new JButton(new ImageIcon(url1)));
    toolBar1.add(new JButton(new ImageIcon(url2)));
    toolBar1.add(Box.createGlue());
    toolBar1.add(new JButton(new ImageIcon(url3)));

    JToolBar toolBar2 = new JToolBar("JButton");
    toolBar2.add(createToolBarButton(url1));
    toolBar2.add(createToolBarButton(url2));
    toolBar2.add(Box.createGlue());
    toolBar2.add(createToolBarButton(url3));

    add(toolBar1, BorderLayout.NORTH);
    add(new JScrollPane(new JTextArea()));
    add(toolBar2, BorderLayout.SOUTH);
    setPreferredSize(new Dimension(320, 240));
  }

  private static JButton createToolBarButton(URL url) {
    JButton b = new JButton(new ImageIcon(url));
    b.setRequestFocusEnabled(false);
    return b;
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

// class ToolBarButton extends JButton {
//   protected ToolBarButton(ImageIcon icon) {
//     super(icon);
//     setContentAreaFilled(false);
//     setFocusPainted(false);
//     addMouseListener(new MouseAdapter() {
//       @Override public void mouseEntered(MouseEvent e) {
//         setContentAreaFilled(true);
//       }
//
//       @Override public void mouseExited(MouseEvent e) {
//         setContentAreaFilled(false);
//       }
//     });
//   }
// }
