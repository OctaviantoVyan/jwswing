package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private final String[] columnNames = {"String", "Integer", "Boolean"};
    private final Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false},
        {"CCC", 92, true}, {"DDD", 0, false}
    };
    private final DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        @Override public Class<?> getColumnClass(int column) {
            switch(column) {
              case 0:
                return String.class;
              case 1:
                return Number.class;
              case 2:
                return Boolean.class;
              default:
                return Object.class;
            }
        }
    };
    private final JTable table = new JTable(model);
    public MainPanel() {
        super(new BorderLayout());

        //table.setAutoCreateRowSorter(true);
        table.setRowSorter(new TableRowSorter<TableModel>(model));

        table.setFillsViewportHeight(true);
        table.setComponentPopupMenu(new TablePopupMenu());

        add(new JButton(new AbstractAction("remove all rows") {
            @Override public void actionPerformed(ActionEvent ae) {
                //model.clear();
                //ArrayIndexOutOfBoundsException:  0 >= 0
                //Bug ID: JDK-6967479 JTable sorter fires even if the model is empty
                //http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6967479
                table.setRowSorter(null);
                table.getTableHeader().repaint();
                model.setRowCount(0);
                //table.setAutoCreateColumnsFromModel(false);
                //table.setModel(new DefaultTableModel());
            }
        }), BorderLayout.SOUTH);
        add(new JScrollPane(table));
        setPreferredSize(new Dimension(320, 240));
    }
    private class TestCreateAction extends AbstractAction{
        public TestCreateAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            if(model.getRowCount()==0) {
                table.setRowSorter(new TableRowSorter<TableModel>(model));
            }
            model.addRow(new Object[] {"", model.getRowCount(), false});
            Rectangle r = table.getCellRect(model.getRowCount()-1, 0, true);
            table.scrollRectToVisible(r);
        }
    }

    private class DeleteAction extends AbstractAction{
        public DeleteAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            int[] selection = table.getSelectedRows();
            if(selection==null || selection.length<=0) return;
            for(int i=selection.length-1;i>=0;i--) {
                model.removeRow(table.convertRowIndexToModel(selection[i]));
            }
            if(model.getRowCount()==0) {
                table.setRowSorter(null);
                table.getTableHeader().repaint();
            }
        }
    }

    private class TablePopupMenu extends JPopupMenu {
        private final Action deleteAction = new DeleteAction("delete", null);
        public TablePopupMenu() {
            super();
            add(new TestCreateAction("add", null));
            //add(new ClearAction("clearSelection", null));
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
