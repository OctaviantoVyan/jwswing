package example;
//-*- mode:java; encoding:utf8n; coding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainPanel extends JPanel{
    private final JComboBox combo1 = new JComboBox();
    private final JComboBox combo2 = new JComboBox();
    public MainPanel() {
        super(new BorderLayout());
        combo1.setRenderer(new MultiColumnCellRenderer(80));
//         //TEST
//         combo1.addPopupMenuListener(new PopupMenuListener() {
//             @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
//                 JComboBox combo = (JComboBox)e.getSource();
//                 Accessible a = combo.getUI().getAccessibleChild(combo, 0);
//                 if(a instanceof BasicComboPopup) {
//                     BasicComboPopup pop = (BasicComboPopup)a;
//                     if(pop.getLayout() instanceof BoxLayout) {
//                         Component[] c = pop.getComponents();
//                         pop.setLayout(new BorderLayout());
//                         pop.add(c[0]);
//                     }
//                 }
//             }
//             @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
//             @Override public void popupMenuCanceled(PopupMenuEvent e) {}
//         });
        combo1.setModel(makeModel());
        combo2.setModel(makeModel());
        add(makeTitledBox("MultiColumnComboBox", combo1), BorderLayout.NORTH);
        add(makeTitledBox("DefaultComboBox", combo2), BorderLayout.SOUTH);
        setPreferredSize(new Dimension(320, 240));
    }
    private static Box makeTitledBox(String title, JComboBox combo) {
        final JTextField leftTextField  = new JTextField();
        final JTextField rightTextField = new JTextField();
        leftTextField.setEditable(false);
        rightTextField.setEditable(false);
        Box box = Box.createVerticalBox();
        box.setBorder(BorderFactory.createTitledBorder(title));
        box.add(Box.createVerticalStrut(2));
        box.add(combo);
        box.add(Box.createVerticalStrut(5));
        box.add(new JSeparator());
        box.add(leftTextField);
        box.add(Box.createVerticalStrut(2));
        box.add(rightTextField);
        combo.addItemListener(new ItemListener() {
            @Override public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange()==ItemEvent.SELECTED) {
                    JComboBox c = (JComboBox)e.getSource();
                    LRItem item = (LRItem)c.getSelectedItem();
                    leftTextField.setText(item.getLeftText());
                    rightTextField.setText(item.getRightText());
                }
            }
        });
        return box;
    }
    private static DefaultComboBoxModel makeModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        model.addElement(new LRItem("asdfasdf", "846876"));
        model.addElement(new LRItem("bxcvzx", "asdfaasdfasdfasdfasdfsasd"));
        model.addElement(new LRItem("asdfasdf.1234567890.1234567890.1234567890.e",
                                    "qwerqwer.1234567890.1234567890.1234567890"));
        model.addElement(new LRItem("14234125", "64345424543523452345234523684"));
        model.addElement(new LRItem("hjklhjk", "addElement"));
        model.addElement(new LRItem("aaaaaaaa", "ddd"));
        model.addElement(new LRItem("bbbbbbbb", "eeeee"));
        model.addElement(new LRItem("cccccccc", "fffffff"));
        return model;
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
        frame.setMinimumSize(new Dimension(256, 100));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
class MultiColumnCellRenderer extends JPanel implements ListCellRenderer{
    private final JLabel leftLabel = new JLabel();
    private final JLabel rightLabel;
    private boolean isFirstTime = true;

    public MultiColumnCellRenderer(int rightWidth) {
        super(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));

        leftLabel.setOpaque(false);
        leftLabel.setBorder(BorderFactory.createEmptyBorder(0,2,0,0));

        final Dimension dim = new Dimension(rightWidth, 0);
        rightLabel = new JLabel() {
            @Override public Dimension getPreferredSize() {
                return dim;
            }
        };
        rightLabel.setOpaque(false);
        rightLabel.setBorder(BorderFactory.createEmptyBorder(0,2,0,2));
        rightLabel.setForeground(Color.GRAY);
        rightLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        this.add(leftLabel);
        this.add(rightLabel, BorderLayout.EAST);
    }
    @Override public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        LRItem item = (LRItem)value;
        leftLabel.setText(item.getLeftText());
        rightLabel.setText(item.getRightText());

        leftLabel.setFont(list.getFont());
        rightLabel.setFont(list.getFont());

        if(index<0) {
            if(isFirstTime) {
                //XXX: Trick on BoxLayout
                list.setPreferredSize(new Dimension());
                isFirstTime = false;
            }
            leftLabel.setForeground(list.getForeground());
            this.setOpaque(false);
        }else{
            leftLabel.setForeground(isSelected?list.getSelectionForeground():list.getForeground());
            this.setBackground(isSelected?list.getSelectionBackground():list.getBackground());
            this.setOpaque(true);
        }
        return this;
    }
    @Override public void updateUI() {
        super.updateUI();
        this.setName("List.cellRenderer");
        isFirstTime = true;
    }
}

class LRItem{
    private final String leftText;
    private final String rightText;
    public LRItem(String strLeft, String strRight) {
        this.leftText = strLeft;
        this.rightText = strRight;
    }
    public String getLeftText() {
        return leftText;
    }
    public String getRightText() {
        return rightText;
    }
    @Override public String toString() {
        return leftText + " / " + rightText;
    }
}
