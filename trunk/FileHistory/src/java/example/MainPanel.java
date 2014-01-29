package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;
import javax.swing.*;

public class MainPanel extends JPanel {
    private static final String COPYRIGHT = "Copyright(C) 2006";
    private static final String APP_NAME  = "@title@";
    private static final String VERSION   = "0.0";
    private static final int    RELEASE   = 1;

    private static final int MAXHISTORY = 3;
    private final BarFactory barFactory;
    private final JFrame frame;

    private final List<String> fh = new ArrayList<>();
    private final JMenuItem noFile = new JMenuItem("なし");
    private JMenu fileHistory;

    public MainPanel(final JFrame frame) {
        super(new BorderLayout());
        this.frame = frame;
        barFactory = new BarFactory("resources.Main");
        initActions(getActions());
        JPanel menupanel = new JPanel(new BorderLayout());
        JMenuBar menuBar = barFactory.createMenubar();
        if(menuBar!=null) {
            menupanel.add(menuBar, BorderLayout.NORTH);
            initHistory(barFactory);
        }
        JToolBar toolBar = barFactory.createToolbar();
        if(toolBar!=null) {
            menupanel.add(toolBar, BorderLayout.SOUTH);
        }
        add(menupanel, BorderLayout.NORTH);
        add(new JScrollPane(new JTextArea()));
        setPreferredSize(new Dimension(320, 240));
    }

    private void initHistory(BarFactory barFactory) {
        JMenu fm = barFactory.getMenu("file");
        if(fileHistory==null) {
            fileHistory = new JMenu("最近使ったファイル(F)");
            fileHistory.setMnemonic('F');
            JMenuItem exit = barFactory.getMenuItem("exit");
            fm.remove(exit);
            fm.add(fileHistory);
            fm.addSeparator();
            fm.add(exit);
        }else{
            fileHistory.removeAll();
        }
        if(fh.size()<=0) {
            noFile.setEnabled(false);
            fileHistory.add(noFile);
        }else{
            fm.remove(noFile);
            for(int i=0;i<fh.size();i++) {
                String name = fh.get(i);
                String num  = Integer.toString(i+1);
                JMenuItem mi = new JMenuItem(new HistoryAction(new File(name).getAbsolutePath()));
                mi.setText(num + ": "+ name);
                //byte[] bt = num.getBytes();
                mi.setMnemonic((int) num.charAt(0));
                fileHistory.add(mi);
            }
        }
    }
    private void updateHistory(String str) {
        fileHistory.removeAll();
        fh.remove(str);
        fh.add(0, str);
        if(fh.size()>MAXHISTORY) {
            fh.remove(fh.size()-1);
        }
        for(int i=0;i<fh.size();i++) {
            String name = fh.get(i);
            String num  = Integer.toString(i+1);
            // JMenuItem mi = new JMenuItem(new HistoryAction(new File(name)));
            JMenuItem mi = new JMenuItem(new HistoryAction(name));
            mi.setText(num + ": "+ name);
            //byte[] bt = num.getBytes();
            mi.setMnemonic((int) num.charAt(0));
            fileHistory.add(mi, i);
        }
    }
    class HistoryAction extends AbstractAction {
//         private final File file;
//         public HistoryAction(File file) {
//             super();
//             this.file = file;
//         }
        private final String fileName;
        public HistoryAction(String fileName) {
            super();
            this.fileName = fileName;
        }
        @Override public void actionPerformed(ActionEvent evt) {
            Object[] obj = {"本来はファイルを開いたりする。\n",
                "このサンプルではなにもせずに\n",
                "履歴の先頭にファイルを移動する。"};
            JOptionPane.showMessageDialog(frame, obj, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            updateHistory(fileName);
        }
    }

    protected void initActions(Action[] actlist) {
        barFactory.initActions(actlist);
    }
    private Action[] getActions() {
        return new Action[] {
            new NewAction(),
            new OpenAction(),
            new ExitAction(),
            new HelpAction(),
            new VersionAction()
        };
        //return defaultActions;
    }
//     private final Action[] defaultActions = {
//         new NewAction(),
//         new OpenAction(),
//         new ExitAction(),
//         new HelpAction(),
//         new VersionAction(),
//     };

    private static class NewAction extends AbstractAction {
        public NewAction() {
            super("new");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            // dummy
        }
    }

    private class OpenAction extends AbstractAction {
        private int count = 0;
        public OpenAction() {
            super("open");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            System.out.println("-------- OpenAction --------");
            //File file = null;
            //JFileChooser fileChooser = new JFileChooser();
            //int retvalue = fileChooser.showOpenDialog(this);
            //if(retvalue==JFileChooser.APPROVE_OPTION) {
            //    file = fileChooser.getSelectedFile();
            //}
            Object[] obj = {"本来はJFileChooserなどでファイルを選択する。\n",
                "このサンプルではなにもせずに\n",
                "適当なファイル名を生成して開いたふりをする。"};
            JOptionPane.showMessageDialog(frame, obj, APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            String fileName = "C:/tmp/dummy.jpg."+count+"~";
            updateHistory(fileName);
            count++;
        }
    }

    private static class SaveAsAction extends AbstractAction {
        public SaveAsAction() {
            super("saveAs");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            // dummy
        }
    }

    private class ExitAction extends AbstractAction {
        public ExitAction() {
            super("exit");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            //exitActionPerformed();
            //saveLocation(prefs);
            frame.dispose();
            //System.exit(0);
        }
    }

    protected static class HelpAction extends AbstractAction {
        public HelpAction() {
            super("help");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            // dummy
        }
    }

    protected class VersionAction extends AbstractAction {
        public VersionAction() {
            super("version");
        }
        @Override public void actionPerformed(ActionEvent evt) {
            Object[] obj = {APP_NAME + " - Version " + VERSION + "." + RELEASE, COPYRIGHT};
            JOptionPane.showMessageDialog(MainPanel.this, obj, APP_NAME,
                                          JOptionPane.INFORMATION_MESSAGE);
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
        }catch(ClassNotFoundException | InstantiationException |
               IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel(frame));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
