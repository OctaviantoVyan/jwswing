package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private final String[] columnNames = {"String", "Integer", "Boolean"};
    private final Object[][] data = {
      {"aaa", 12, true}, {"bbb", 5, false},
      {"CCC", 92, true}, {"DDD", 0, false}
    };
    private final DefaultTableModel model = new DefaultTableModel(data, columnNames) {
      @Override public Class<?> getColumnClass(int column) {
        return getValueAt(0, column).getClass();
      }
    };
    private final JTable table;
    public MainPanel() {
        super(new BorderLayout());
        UIManager.put("Table.focusCellHighlightBorder", new DotBorder(2,2,2,2));
        table = new JTable(model) {
            @Override public void updateUI() {
                super.updateUI();
                Color sbg = UIManager.getColor("Table.selectionBackground");
                if(sbg!=null) { //Nimbus
                    setSelectionBackground(sbg);
                }
            }
            private final DotBorder dotBorder = new DotBorder(2,2,2,2);
            private final Border emptyBorder  = BorderFactory.createEmptyBorder(2,2,2,2);
            @Override public Component prepareRenderer(TableCellRenderer tcr, int row, int column) {
                Component c = super.prepareRenderer(tcr, row, column);
                if(isRowSelected(row)) {
                    ((JComponent)c).setBorder(dotBorder);
                    dotBorder.setLastCellFlag(column==getColumnCount()-1);
                }else{
                    ((JComponent)c).setBorder(emptyBorder);
                }
                return c;
            }
            @Override public Component prepareEditor(TableCellEditor editor, int row, int column) {
                Component c = super.prepareEditor(editor, row, column);
                if(c instanceof JCheckBox) {
                    JCheckBox b = (JCheckBox)c;
                    //System.out.println(b.getBorder());
                    //b.setBorder(dotBorder);
                    DotBorder border = (DotBorder)b.getBorder();
                    border.setLastCellFlag(column==getColumnCount()-1);
                    b.setBorderPainted(true);
                    b.setBackground(getSelectionBackground());
                }
                return c;
            }
        };

        //TableColumnModel columns = table.getColumnModel();
        //for(int i=0;i<columns.getColumnCount();i++) {
        //    columns.getColumn(i).setCellRenderer(new TestRenderer());
        //}

        table.setRowSelectionAllowed(true);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension());
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        table.setComponentPopupMenu(new TablePopupMenu());
        add(new JScrollPane(table));
        setPreferredSize(new Dimension(320, 240));
    }

    class TestCreateAction extends AbstractAction{
        public TestCreateAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent e) {
            model.addRow(new Object[] {"New row", 0, true});
            Rectangle r = table.getCellRect(model.getRowCount()-1, 0, true);
            table.scrollRectToVisible(r);
        }
    }
    class DeleteAction extends AbstractAction{
        public DeleteAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent e) {
            int[] selection = table.getSelectedRows();
            if(selection==null || selection.length<=0) return;
            for(int i=selection.length-1;i>=0;i--) {
                model.removeRow(table.convertRowIndexToModel(selection[i]));
            }
        }
    }
    class ClearAction extends AbstractAction{
        public ClearAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent e) {
            table.clearSelection();
        }
    }
    private class TablePopupMenu extends JPopupMenu {
        private final Action deleteAction = new DeleteAction("delete", null);
        public TablePopupMenu() {
            super();
            add(new TestCreateAction("add", null));
            add(new ClearAction("clearSelection", null));
            addSeparator();
            add(deleteAction);
        }
        @Override public void show(Component c, int x, int y) {
            int[] l = table.getSelectedRows();
            deleteAction.setEnabled(l!=null && l.length>0);
            super.show(c, x, y);
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
// class TestRenderer extends DefaultTableCellRenderer {
//     private static final DotBorder dotBorder = new DotBorder(2,2,2,2);
//     private static final Border emptyBorder  = BorderFactory.createEmptyBorder(2,2,2,2);
//     @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//         Component c = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
//         if(c instanceof JComponent) {
//             int lsi = table.getSelectionModel().getLeadSelectionIndex();
//             ((JComponent)c).setBorder(row==lsi?dotBorder:emptyBorder);
//             dotBorder.setLastCellFlag(row==lsi&&column==table.getColumnCount()-1);
//         }
//         return c;
//     }
// }
class DotBorder extends EmptyBorder {
    private static final BasicStroke dashed = new BasicStroke(
        1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
        10.0f, (new float[] {1.0f}), 0.0f);
    private static final Color dotColor = new Color(200,150,150);
    public DotBorder(int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
    }
    private boolean isLastCell = false;
    public void setLastCellFlag(boolean flag) {
        isLastCell = flag;
    }
    @Override public boolean isBorderOpaque() {
        return true;
    }
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D)g;
        g2.translate(x,y);
        g2.setPaint(dotColor);
        g2.setStroke(dashed);
        int cbx = c.getBounds().x;
        if(cbx==0) {
            g2.drawLine(0,0,0,h);
        }
        if(isLastCell) {
            g2.drawLine(w-1,0,w-1,h);
        }
        if(cbx%2==0) {
            g2.drawLine(0,0,w,0);
            g2.drawLine(0,h-1,w,h-1);
        }else{
            g2.drawLine(1,0,w,0);
            g2.drawLine(1,h-1,w,h-1);
        }
        g2.translate(-x,-y);
    }
}
