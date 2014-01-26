package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;
// import javax.swing.plaf.basic.*;

public class MainPanel extends JPanel {
    public MainPanel() {
        super();
        //Icon: refer to http://chrfb.deviantart.com/art/quot-ecqlipse-2-quot-PNG-59941546
        URL url = getClass().getResource("RECYCLE BIN - EMPTY_16x16-32.png");
        Icon icon = new ImageIcon(url);

        AbstractButton b = makeButton(makeTitleWithIcon(url, "align=top", "top"));
        add(b);
        b = makeButton(makeTitleWithIcon(url, "align=middle", "middle"));
        add(b);
        b = makeButton(makeTitleWithIcon(url, "align=bottom", "bottom"));
        add(b);

        JLabel label = new JLabel("JLabel", icon, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setAlignmentX(.5f);
        b = makeButton("");
        b.setAlignmentX(.5f);
        JPanel p = new JPanel();
        p.setLayout(new OverlayLayout(p));
        p.setOpaque(false);
        p.add(label);
        p.add(b);
        add(p);

        add(makeButton("\u260f text"));

        b = new TranslucentButton("TranslucentButton", icon);
        add(b);

        add(makeButton("a"));
        add(makeButton("bb"));
        add(makeButton("ccc"));
        add(makeButton("dddd"));

        BufferedImage bi = getFilteredImage(getClass().getResource("test.jpg"));
        setBorder(new CentredBackgroundBorder(bi));
        //setBackground(new Color(50,50,50));
        setOpaque(false);
        setPreferredSize(new Dimension(320, 240));
    }
    private static String makeTitleWithIcon(URL url, String title, String align) {
        return String.format("<html><p align='%s'><img src='%s' align='%s' />&nbsp;%s</p></html>", align, url, align, title);
    }
    private static AbstractButton makeButton(String title) {
        return new JButton(title) {
            @Override public void updateUI() {
                super.updateUI();
                setVerticalAlignment(SwingConstants.CENTER);
                setVerticalTextPosition(SwingConstants.CENTER);
                setHorizontalAlignment(SwingConstants.CENTER);
                setHorizontalTextPosition(SwingConstants.CENTER);
                setBorder(BorderFactory.createEmptyBorder());
                //setBorderPainted(false);
                setContentAreaFilled(false);
                setFocusPainted(false);
                setOpaque(false);
                setForeground(Color.WHITE);
                setIcon(new TranslucentButtonIcon());
            }
        };
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

    private static BufferedImage getFilteredImage(URL url) {
        BufferedImage image;
        try{
            image = ImageIO.read(url);
        }catch(IOException ioe) {
            ioe.printStackTrace();
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }
        BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        byte[] b = new byte[256];
        for(int i=0;i<256;i++) {
            b[i] = (byte)(i*0.5);
        }
        BufferedImageOp op = new LookupOp(new ByteLookupTable(0, b), null);
        op.filter(image, dest);
        return dest;
    }

    private static TexturePaint makeCheckerTexture() {
        int cs = 6;
        int sz = cs*cs;
        BufferedImage img = new BufferedImage(sz,sz,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setPaint(new Color(120,120,120));
        g2.fillRect(0,0,sz,sz);
        g2.setPaint(new Color(200,200,200,20));
        for(int i=0;i*cs<sz;i++) {
            for(int j=0;j*cs<sz;j++) {
                if((i+j)%2==0) { g2.fillRect(i*cs, j*cs, cs, cs); }
            }
        }
        g2.dispose();
        return new TexturePaint(img, new Rectangle(0,0,sz,sz));
    }
    private final TexturePaint texture = makeCheckerTexture();
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(texture);
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}

class TranslucentButton extends JButton {
    private static final Color TL = new Color(1f,1f,1f,.2f);
    private static final Color BR = new Color(0f,0f,0f,.4f);
    private static final Color ST = new Color(1f,1f,1f,.2f);
    private static final Color SB = new Color(1f,1f,1f,.1f);
    private Color ssc;
    private Color bgc;
    private int r = 8;
    public TranslucentButton(String text) {
        super(text);
    }
    public TranslucentButton(String text, Icon icon) {
        super(text, icon);
    }
    @Override public void updateUI() {
        super.updateUI();
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
    }
    @Override protected void paintComponent(Graphics g) {
        int x = 0;
        int y = 0;
        int w = getWidth();
        int h = getHeight();
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape area = new RoundRectangle2D.Float(x, y, w-1, h-1, r, r);
        ssc = TL;
        bgc = BR;
        ButtonModel m = getModel();
        if(m.isPressed()) {
            ssc = SB;
            bgc = ST;
        }else if(m.isRollover()) {
            ssc = ST;
            bgc = SB;
        }
        g2.setPaint(new GradientPaint(x, y, ssc, x, y+h, bgc, true));
        g2.fill(area);
        g2.setPaint(BR);
        g2.draw(area);
        g2.dispose();
        super.paintComponent(g);
    }
}

class TranslucentButtonIcon implements Icon {
    private static final Color TL = new Color(1f,1f,1f,.2f);
    private static final Color BR = new Color(0f,0f,0f,.4f);
    private static final Color ST = new Color(1f,1f,1f,.2f);
    private static final Color SB = new Color(1f,1f,1f,.1f);
    private Color ssc;
    private Color bgc;
    private int r = 8;
    @Override public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = c.getWidth();
        int h = c.getHeight();

        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape area = new RoundRectangle2D.Float(x, y, w-1, h-1, r, r);
        ssc = TL;
        bgc = BR;
        if(c instanceof AbstractButton) {
            ButtonModel m = ((AbstractButton)c).getModel();
            if(m.isPressed()) {
                ssc = SB;
                bgc = ST;
            }else if(m.isRollover()) {
                ssc = ST;
                bgc = SB;
            }
        }
        g2.setPaint(new GradientPaint(x, y, ssc, x, y+h, bgc, true));
        g2.fill(area);
        g2.setPaint(BR);
        g2.draw(area);
        g2.dispose();
    }
    @Override public int getIconWidth()  {
        return 100;
    }
    @Override public int getIconHeight() {
        return 24;
    }
}

// https://forums.oracle.com/thread/1395763 How can I use TextArea with Background Picture ?
// http://terai.xrea.jp/Swing/CentredBackgroundBorder.html
class CentredBackgroundBorder implements Border {
    private final Insets insets = new Insets(0, 0, 0, 0);
    private final BufferedImage image;
    public CentredBackgroundBorder(BufferedImage image) {
        this.image = image;
    }
    @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        int cx = x + (width-image.getWidth())/2;
        int cy = y + (height-image.getHeight())/2;
        ((Graphics2D)g).drawRenderedImage(image, AffineTransform.getTranslateInstance(cx, cy));
    }
    @Override public Insets getBorderInsets(Component c) {
        return insets;
    }
    @Override public boolean isBorderOpaque() {
        return true;
    }
}
