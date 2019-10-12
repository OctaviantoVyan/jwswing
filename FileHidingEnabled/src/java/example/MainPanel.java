// -*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
// @homepage@

package example;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;
import javax.swing.*;

public final class MainPanel extends JPanel {
  private MainPanel() {
    super(new BorderLayout());

    Object showHiddenProperty = Toolkit.getDefaultToolkit().getDesktopProperty("awt.file.showHiddenFiles");
    System.out.println("awt.file.showHiddenFiles: " + showHiddenProperty);

    JFileChooser chooser = new JFileChooser();
    Optional.ofNullable(searchPopupMenu(chooser)).ifPresent(pop -> {
      pop.addSeparator();
      JCheckBoxMenuItem mi = new JCheckBoxMenuItem("isFileHidingEnabled");
      mi.addActionListener(e -> chooser.setFileHidingEnabled(((JCheckBoxMenuItem) e.getSource()).isSelected()));
      mi.setSelected(chooser.isFileHidingEnabled());
      pop.add(mi);
    });

    JTextArea log = new JTextArea();
    JButton button = new JButton("showOpenDialog");
    button.addActionListener(e -> {
      int retValue = chooser.showOpenDialog(getRootPane());
      if (retValue == JFileChooser.APPROVE_OPTION) {
        log.setText(chooser.getSelectedFile().getAbsolutePath());
      }
    });

    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createTitledBorder("JFileChooser"));
    p.add(button);
    add(p, BorderLayout.NORTH);
    add(new JScrollPane(log));
    setPreferredSize(new Dimension(320, 240));
  }

  private static JPopupMenu searchPopupMenu(Container parent) {
    for (Component c: parent.getComponents()) {
      if (c instanceof JComponent && Objects.nonNull(((JComponent) c).getComponentPopupMenu())) {
        return ((JComponent) c).getComponentPopupMenu();
      } else {
        JPopupMenu pop = searchPopupMenu((Container) c);
        if (Objects.nonNull(pop)) {
          return pop;
        }
      }
    }
    return null;
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
