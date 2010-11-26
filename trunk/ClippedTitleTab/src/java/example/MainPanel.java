package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

public class MainPanel extends JPanel {
    private final Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
    private final JTabbedPane tabbedPane = new JTabbedPane() {
        public String getToolTipTextAt(int index) {
            return getTitleAt(index);
        }
        public void insertTab(String title, Icon icon, Component component, String tip, int index) {
            super.insertTab(title, icon, component, title, index);
        }
    };
    public MainPanel() {
        super(new BorderLayout());
        if(tabbedPane.getUI() instanceof com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI) {
            tabbedPane.setUI(new com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI() {
                @Override protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                    Insets insets = tabPane.getInsets();
                    Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
                    int width = tabPane.getWidth() - tabAreaInsets.left - tabAreaInsets.right - insets.left - insets.right;
                    switch(tabPlacement) {
                      case LEFT: case RIGHT:
                        return (int)(width/4);
                      case BOTTOM: case TOP: default:
                        return (int)(width/tabPane.getTabCount());
                    }
                }
                @Override protected void paintText(Graphics g, int tabPlacement,
                                         Font font, FontMetrics metrics, int tabIndex,
                                         String title, Rectangle textRect,
                                         boolean isSelected) {
                    Rectangle tabRect = rects[tabIndex];
                    Rectangle rect = new Rectangle(textRect.x+tabInsets.left, textRect.y, tabRect.width-tabInsets.left-tabInsets.right, textRect.height);
                    String clippedText = SwingUtilities.layoutCompoundLabel(metrics, title, null,
                                                                            SwingUtilities.CENTER, SwingUtilities.CENTER,
                                                                            SwingUtilities.CENTER, SwingUtilities.TRAILING,
                                                                            rect, new Rectangle(), rect, 0);
                    if(title.equals(clippedText)) {
                        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
                    }else{
                        rect = new Rectangle(textRect.x+tabInsets.left, textRect.y, tabRect.width-tabInsets.left-tabInsets.right, textRect.height);
                        super.paintText(g, tabPlacement, font, metrics, tabIndex, clippedText, rect, isSelected);
                    }
                }
            });
        }else{
            tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
                @Override protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
                    Insets insets = tabPane.getInsets();
                    Insets tabAreaInsets = getTabAreaInsets(tabPlacement);
                    int width = tabPane.getWidth() - tabAreaInsets.left - tabAreaInsets.right - insets.left - insets.right;
                    switch(tabPlacement) {
                      case LEFT: case RIGHT:
                        return (int)(width/4);
                      case BOTTOM: case TOP: default:
                        return (int)(width/tabPane.getTabCount());
                    }
                }
                @Override protected void paintText(Graphics g, int tabPlacement,
                                         Font font, FontMetrics metrics, int tabIndex,
                                         String title, Rectangle textRect,
                                         boolean isSelected) {
                    int fw = (int) font.getSize();
                    Rectangle tabRect = rects[tabIndex];
                    Rectangle rect = new Rectangle(textRect.x+fw/2, textRect.y, tabRect.width-fw, textRect.height);
                    String clippedText = SwingUtilities.layoutCompoundLabel(metrics, title, null,
                                                                            SwingUtilities.CENTER, SwingUtilities.CENTER,
                                                                            SwingUtilities.CENTER, SwingUtilities.TRAILING,
                                                                            rect, new Rectangle(), rect, 0);
                    if(title.equals(clippedText)) {
                        super.paintText(g, tabPlacement, font, metrics, tabIndex, title, textRect, isSelected);
                    }else{
                        rect = new Rectangle(textRect.x+fw/2, textRect.y, tabRect.width-fw, textRect.height);
                        super.paintText(g, tabPlacement, font, metrics, tabIndex, clippedText, rect, isSelected);
                    }
                }
            });
        }
        final java.util.List<? extends JTabbedPane> list = java.util.Arrays.asList(
            makeTestTab(new JTabbedPane()),
            makeTestTab(tabbedPane));
        JPanel p = new JPanel(new GridLayout(2,1));
        for(JTabbedPane t:list) p.add(t);
        add(new JCheckBox(new AbstractAction("LEFT") {
            @Override public void actionPerformed(ActionEvent e) {
                JCheckBox c = (JCheckBox)e.getSource();
                for(JTabbedPane t:list) {
                    t.setTabPlacement(c.isSelected()?JTabbedPane.LEFT:JTabbedPane.TOP);
                }
            }
        }), BorderLayout.NORTH);
        add(p);
        setPreferredSize(new Dimension(320, 240));
    }
    private JTabbedPane makeTestTab(JTabbedPane jtp) {
//         jtp.setTabPlacement(JTabbedPane.RIGHT);
//         jtp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        jtp.addTab("1111111111111111111111111111", new JLabel("aaaaaaaaaaa"));
        jtp.addTab("2", new JLabel("bbbbbbbbb"));
        jtp.addTab("33333333333333333333333333333333333333333333", new JLabel("cccccccccc"));
        jtp.addTab("444444444444", new JLabel("dddddddddddddddd"));
        return jtp;
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
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
