package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;
import javax.swing.plaf.LayerUI;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private final JCheckBox check = new JCheckBox("Lock all(JScrollPane, JTable, JPopupMenu)");
    private final String[] columnNames = {"String", "Integer", "Boolean"};
    private final Object[][] data = {
        {"aaa", 12, true}, {"bbb", 5, false},
        {"CCC", 92, true}, {"DDD", 0, false},
    };
    private final DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        @Override public Class<?> getColumnClass(int column) {
            return getValueAt(0, column).getClass();
        }
    };
    private final JTable table = new JTable(model);

    public MainPanel() {
        super(new BorderLayout());
        for(int i=0;i<100;i++) {
            model.addRow(new Object[] {"Name "+i, Integer.valueOf(i), Boolean.FALSE});
        }

        JScrollPane scroll = new JScrollPane(table);
//         {
//             @Override public JPopupMenu getComponentPopupMenu() {
//                 System.out.println("JScrollPane#getComponentPopupMenu");
//                 return check.isSelected()? null: super.getComponentPopupMenu();
//             }
//         };
        scroll.setComponentPopupMenu(new TablePopupMenu());
        table.setInheritsPopupMenu(true);

        final DisableInputLayerUI layerUI = new DisableInputLayerUI();
        check.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent ie) {
                layerUI.setLocked(((JCheckBox)ie.getSource()).isSelected());
            }
        });

        JLayer<JComponent> layer = new JLayer<JComponent>(scroll, layerUI);
        add(layer);
        add(check, BorderLayout.NORTH);
        setPreferredSize(new Dimension(320, 240));
    }

    class TestCreateAction extends AbstractAction{
        public TestCreateAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            testCreateActionPerformed(evt);
        }
    }
    private void testCreateActionPerformed(ActionEvent e) {
        model.addRow(new Object[] {"New Name", Integer.valueOf(0), Boolean.FALSE});
        Rectangle rect = table.getCellRect(model.getRowCount()-1, 0, true);
        table.scrollRectToVisible(rect);
    }

    class DeleteAction extends AbstractAction{
        public DeleteAction(String label, Icon icon) {
            super(label,icon);
        }
        @Override public void actionPerformed(ActionEvent evt) {
            deleteActionPerformed(evt);
        }
    }
    public void deleteActionPerformed(ActionEvent evt) {
        int[] selection = table.getSelectedRows();
        if(selection==null || selection.length<=0) return;
        for(int i=selection.length-1;i>=0;i--) {
            model.removeRow(table.convertRowIndexToModel(selection[i]));
        }
    }

    private class TablePopupMenu extends JPopupMenu {
        private final Action createAction = new TestCreateAction("add", null);
        private final Action deleteAction = new DeleteAction("delete", null);
        public TablePopupMenu() {
            super();
            add(createAction);
            addSeparator();
            add(deleteAction);
        }
        @Override public void show(Component c, int x, int y) {
            createAction.setEnabled(!check.isSelected());
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
class DisableInputLayerUI extends LayerUI<JComponent> {
    private static final MouseAdapter dummyMouseListener = new MouseAdapter() {};
    private boolean isBlocking = false;
    @Override public void installUI(JComponent c) {
        super.installUI(c);
        JLayer jlayer = (JLayer)c;
        jlayer.getGlassPane().addMouseListener(dummyMouseListener);
        jlayer.setLayerEventMask(
            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK |
            AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }
    @Override public void uninstallUI(JComponent c) {
        JLayer jlayer = (JLayer)c;
        jlayer.setLayerEventMask(0);
        jlayer.getGlassPane().removeMouseListener(dummyMouseListener);
        super.uninstallUI(c);
    }
    @Override public void eventDispatched(AWTEvent e, JLayer l) {
        if(isBlocking && e instanceof InputEvent) {
            ((InputEvent)e).consume();
        }
    }
    private static final String CMD_REPAINT = "lock";
    public void setLocked(boolean flag) {
        firePropertyChange(CMD_REPAINT,isBlocking,flag);
        isBlocking = flag;
    }
    @Override public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
        String cmd = pce.getPropertyName();
        if(CMD_REPAINT.equals(cmd)) {
            l.getGlassPane().setVisible((Boolean)pce.getNewValue());
        }
    }
}
