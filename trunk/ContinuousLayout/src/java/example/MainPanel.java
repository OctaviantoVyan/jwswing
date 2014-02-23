package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.Objects;
import javax.swing.*;

public final class MainPanel extends JPanel {
    private final JSplitPane leftPane   = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JSplitPane rightPane  = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private final JSplitPane centerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    public MainPanel() {
        super(new BorderLayout());

        //leftPane.setContinuousLayout(true);
        //rightPane.setContinuousLayout(true);
        //centerPane.setContinuousLayout(true);

        leftPane.setTopComponent(new JScrollPane(new JTextArea("aaaaaaa")));
        leftPane.setBottomComponent(new JScrollPane(new JTextArea("bbbb")));
        rightPane.setTopComponent(new JScrollPane(new JTree()));
        rightPane.setBottomComponent(new JScrollPane(new JTree()));
        centerPane.setLeftComponent(leftPane);
        centerPane.setRightComponent(rightPane);

        leftPane.setResizeWeight(.5);
        rightPane.setResizeWeight(.5);
        centerPane.setResizeWeight(.5);

        PropertyChangeListener pcl = new PropertyChangeListener() {
            @Override public void propertyChange(PropertyChangeEvent e) {
                if(JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(e.getPropertyName())) {
                    JSplitPane source = (JSplitPane)e.getSource();
                    int location = ((Integer)e.getNewValue()).intValue();
                    JSplitPane target = Objects.equals(source, leftPane) ? rightPane : leftPane;
                    if(location != target.getDividerLocation()) {
                        target.setDividerLocation(location);
                    }
                }
            }
        };
        leftPane.addPropertyChangeListener(pcl);
        rightPane.addPropertyChangeListener(pcl);

        add(new JCheckBox(new AbstractAction("setContinuousLayout") {
            @Override public void actionPerformed(ActionEvent e) {
                boolean flag = ((JCheckBox)e.getSource()).isSelected();
                leftPane.setContinuousLayout(flag);
                rightPane.setContinuousLayout(flag);
                centerPane.setContinuousLayout(flag);
            }
        }), BorderLayout.NORTH);
        add(centerPane);
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
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(ClassNotFoundException | InstantiationException |
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
