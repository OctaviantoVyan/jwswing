package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.*;
import javax.swing.table.*;

public class MainPanel extends JPanel {
    private static final String TEXT = "drag select table cells 1 22 333 4444 55555 666666 7777777 88888888";
    private final String[] columnNames = {"html"};
    private final Object[][] data = {
        {"<html><font color=red>font color red</font><br /> " + TEXT},
        {"<html><font color=green>font color green</font> " + TEXT},
        {"<html><font color=blue>font color blue</font> " + TEXT},
        {"<html><font color=black>font color black</font><br />  " + TEXT},
        {"<html><font color=orange>font color orange</font> " + TEXT},
        {"<html><font color=gray>font color gray</font> " + TEXT}
    };
    private final DefaultTableModel model = new DefaultTableModel(data, columnNames) {
        @Override public Class<?> getColumnClass(int column) {
            return String.class;
        }
        @Override public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JRadioButton centerRadio = new JRadioButton("CENTER");
    private final JRadioButton topRadio = new JRadioButton("TOP");
    private final JRadioButton bottomRadio = new JRadioButton("BOTTOM");

    public MainPanel() {
        super(new BorderLayout());

        table.setAutoCreateRowSorter(true);
        table.setRowHeight(16);

        ActionListener al = new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                TableCellRenderer r = table.getDefaultRenderer(String.class);
                if (r instanceof JLabel) {
                    JLabel label = (JLabel) r;
                    if (topRadio.isSelected()) {
                        label.setVerticalAlignment(SwingConstants.TOP);
                    } else if (bottomRadio.isSelected()) {
                        label.setVerticalAlignment(SwingConstants.BOTTOM);
                    } else {
                        label.setVerticalAlignment(SwingConstants.CENTER);
                    }
                    table.repaint();
                }
            }
        };
        ButtonGroup bg = new ButtonGroup();
        JPanel p = new JPanel();
        for (JRadioButton b : Arrays.asList(centerRadio, topRadio, bottomRadio)) {
            b.addActionListener(al);
            bg.add(b);
            p.add(b);
        }
        centerRadio.setSelected(true);

        add(new JScrollPane(table));
        add(p, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(320, 240));
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
