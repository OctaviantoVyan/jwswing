package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    public MainPanel() {
        super(new BorderLayout());
        Vector list = makeIconList();
        TestModel model = new TestModel(list);
        IconTable table = new IconTable(model, list);
        JPanel p = new JPanel(new GridBagLayout());
        p.add(table, new GridBagConstraints());
        p.setBackground(Color.WHITE);
        add(p);
        setPreferredSize(new Dimension(320, 240));
    }
    private Vector makeIconList() {
        Vector<MyIcon> list = new Vector<MyIcon>();
        list.addElement(new MyIcon("wi0009"));
        list.addElement(new MyIcon("wi0054"));
        list.addElement(new MyIcon("wi0062"));
        list.addElement(new MyIcon("wi0063"));
        list.addElement(new MyIcon("wi0064"));
        list.addElement(new MyIcon("wi0096"));
        list.addElement(new MyIcon("wi0111"));
        list.addElement(new MyIcon("wi0122"));
        list.addElement(new MyIcon("wi0124"));
        return list;
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
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class TestModel extends DefaultTableModel {
    public TestModel(Vector list) {
        super();
        addRow(new Object[] {list.elementAt(0), list.elementAt(1), list.elementAt(2) });
        addRow(new Object[] {list.elementAt(3), list.elementAt(4), list.elementAt(5) });
        addRow(new Object[] {list.elementAt(6), list.elementAt(7), list.elementAt(8) });
    }
    @Override public boolean isCellEditable(int row, int column) {
        return false;
    }
    @Override public int getColumnCount() {
        return 3;
    }
    @Override public String getColumnName(int col) {
        return "";
    }
}
class TestRenderer extends DefaultTableCellRenderer {
    public TestRenderer() {
        super();
        setHorizontalAlignment(JLabel.CENTER);
        //setOpaque(true);
        //setBorder(BorderFactory.createEmptyBorder());
    }
    @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setIcon(((MyIcon)value).large);
        return this;
    }
}
class MyIcon {
    public final ImageIcon large;
    public final ImageIcon small;
    public MyIcon(String str) {
        large = new ImageIcon(getClass().getResource(str+"-48.png"));
        small = new ImageIcon(getClass().getResource(str+"-24.png"));
    }
}
class IconTable extends JTable {
    private final MyGlassPane panel = new MyGlassPane();
    private final EditorFromList editor;
    private Rectangle rect;

    public IconTable(TableModel model, Vector list) {
        super(model);
        setDefaultRenderer(Object.class, new TestRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        initCellSize(50);
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent me) {
                startEditing();
            }
        });
        editor = new EditorFromList(list);
        editor.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
                    cancelEditing();
                }
            }
        });
        editor.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent me) {
                changeValue(me.getPoint());
            }
        });
        panel.add(editor);
        panel.setVisible(false);
    }
    private void initCellSize(int size) {
        setRowHeight(size);
        JTableHeader tableHeader = getTableHeader();
        tableHeader.setResizingAllowed(false);
        tableHeader.setReorderingAllowed(false);
        TableColumnModel m = getColumnModel();
        for(int i=0;i<m.getColumnCount();i++) {
            TableColumn col = m.getColumn(i);
            col.setMinWidth(size);
            col.setMaxWidth(size);
        }
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    class MyGlassPane extends JPanel{
        public MyGlassPane() {
            super((LayoutManager)null);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent me) {
                    if(rect==null || rect.contains(me.getPoint())) return;
                    changeValue(me.getPoint());
                }
            });
            setFocusTraversalPolicy(new DefaultFocusTraversalPolicy() {
                @Override public boolean accept(Component c) {return c==editor;}
            });
            //editor.requestFocusInWindow();
        }
        @Override public void setVisible(boolean flag) {
            super.setVisible(flag);
            setFocusTraversalPolicyProvider(flag);
            setFocusCycleRoot(flag);
        }
        private static final int xoff = 4;
        @Override public void paintComponent(Graphics g) {
            g.setColor(new Color(255,255,255,100));
            g.fillRect(0,0,getWidth(), getHeight());
            BufferedImage bufimg = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bufimg.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g2.setPaint(Color.BLACK);
            for(int i=0;i<xoff;i++) {
                g2.fillRoundRect(rect.x-i, rect.y+xoff,
                                 rect.width+i+i, rect.height-xoff+i, 5, 5);
            }
            g.drawImage(bufimg, 0, 0, null);
        }
    }
    private void initEditor() {
        Dimension dim = editor.getPreferredSize();
        rect = getCellRect(getSelectedRow(), getSelectedColumn(), true);
        int iv = (dim.width-rect.width)/2;
        Point p = SwingUtilities.convertPoint(this, rect.getLocation(), panel);
        rect.setRect(p.x-iv, p.y-iv, dim.width, dim.height);
        editor.setBounds(rect);
        editor.setSelectedValue(getValueAt(getSelectedRow(), getSelectedColumn()), true);
    }
    public void startEditing() {
        JFrame f = (JFrame)getTopLevelAncestor();
        f.setGlassPane(panel);
        initEditor();
        panel.setVisible(true);
        editor.requestFocusInWindow();
    }
    private void cancelEditing() {
        panel.setVisible(false);
    }
    private void changeValue(Point p) {
        Object o = editor.getModel().getElementAt(editor.locationToIndex(p));
        if(o != null) {
            setValueAt(o, getSelectedRow(), getSelectedColumn());
        }
        panel.setVisible(false);
    }
    class EditorFromList extends JList {
        private static final int ins = 2;
        public EditorFromList(Vector list) {
            super(list);
            ImageIcon icon = ((MyIcon)list.elementAt(0)).small;
            int iw = ins+icon.getIconWidth();
            int ih = ins+icon.getIconHeight();
            setLayoutOrientation(JList.HORIZONTAL_WRAP);
            setVisibleRowCount(0);
            setFixedCellWidth(iw);
            setFixedCellHeight(ih);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setPreferredSize(new Dimension(iw*3+ins, ih*3+ins));
            setCellRenderer(new ListCellRenderer() {
                private final JLabel label = new JLabel();
                private final Color selctedColor = new Color(200, 200, 255);
                @Override public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    label.setOpaque(true);
                    label.setHorizontalAlignment(JLabel.CENTER);
                    if(index == rollOverRowIndex) {
                        label.setBackground(getSelectionBackground());
                    }else if(isSelected) {
                        label.setBackground(selctedColor);
                    }else{
                        label.setBackground(getBackground());
                    }
                    label.setIcon(((MyIcon)value).small);
                    return label;
                }
            });
            RollOverListener lst = new RollOverListener();
            addMouseMotionListener(lst);
            addMouseListener(lst);
        }
        private int rollOverRowIndex = -1;
        private class RollOverListener extends MouseInputAdapter {
            @Override public void mouseExited(MouseEvent e) {
                rollOverRowIndex = -1;
                repaint();
            }
            @Override public void mouseMoved(MouseEvent e) {
                int row = locationToIndex(e.getPoint());
                if( row != rollOverRowIndex ) {
                    rollOverRowIndex = row;
                    repaint();
                }
            }
        }
    }
}
