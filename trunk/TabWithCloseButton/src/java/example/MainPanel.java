package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    private final MyJTabbedPane tab = new MyJTabbedPane();
    private final JPopupMenu pop = new JPopupMenu();
    public MainPanel() {
        super(new BorderLayout());
        pop.add(new NewTabAction("Add", null));
        pop.addSeparator();
        pop.add(new CloseAllAction("Close All", null));
        tab.setComponentPopupMenu(pop);
        tab.addTab("JLabel", new JLabel("JDK 6"));
        tab.addTab("JTree",  new JScrollPane(new JTree()));
        add(tab);
        setPreferredSize(new Dimension(320, 240));
    }
    static private int count = 0;
    class NewTabAction extends AbstractAction{
        public NewTabAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            tab.addTab("Title"+count, new JLabel("Tab"+count));
            tab.setSelectedIndex(tab.getTabCount()-1);
            count++;
        }
    }
    class CloseAllAction extends AbstractAction{
        public CloseAllAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            tab.removeAll();
        }
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

class MyJTabbedPane extends JTabbedPane {
    private static final Icon icon = new CloseTabIcon();
    public MyJTabbedPane() {
        super();
    }
    @Override public void addTab(String title, final Component content) {
        JPanel tab = new JPanel(new BorderLayout());
        tab.setOpaque(false);
        JLabel label = new JLabel(title);
        label.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
        JButton button = new JButton(icon);
        //button.setBorderPainted(false);
        //button.setFocusPainted(false);
        //button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                removeTabAt(indexOfComponent(content));
            }
        });
        tab.add(label,  BorderLayout.WEST);
        tab.add(button, BorderLayout.EAST);
        tab.setBorder(BorderFactory.createEmptyBorder(2,1,1,1));
        super.addTab(title, content);
        setTabComponentAt(getTabCount()-1, tab);
    }
    private static class CloseTabIcon implements Icon {
        private int width;
        private int height;
        public CloseTabIcon() {
            width  = 16;
            height = 16;
        }
        @Override public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x, y);
            g.setColor(Color.BLACK);
            g.drawLine(4,  4, 11, 11);
            g.drawLine(4,  5, 10, 11);
            g.drawLine(5,  4, 11, 10);
            g.drawLine(11, 4,  4, 11);
            g.drawLine(11, 5,  5, 11);
            g.drawLine(10, 4,  4, 10);
            g.translate(-x, -y);
        }
        @Override public int getIconWidth() {
            return width;
        }
        @Override public int getIconHeight() {
            return height;
        }
//         public Rectangle getBounds() {
//             return new Rectangle(0, 0, width, height);
//         }
    }
}
