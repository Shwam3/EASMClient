package eastangliamapclient.gui;

import eastangliamapclient.*;
import static eastangliamapclient.EastAngliaMapClient.newFile;
import static eastangliamapclient.EastAngliaMapClient.storageDir;
import eastangliamapclient.Signals.SignalPostDirection;
import eastangliamapclient.json.JSONParser;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SignalMap
{
    public JFrame frame;

    private final java.util.List<JButton>         buttons   = new ArrayList<>();
    private final java.util.List<JScrollPane>     motdPanes = new ArrayList<>();
    private final java.util.List<BackgroundPanel> panelList = new ArrayList<>();
    private final Map<String, BackgroundPanel>    panelMap  = new HashMap<>();

    public JTabbedPane TabBar;

    public static final int DEFAULT_WIDTH  = 1877;
    public static final int DEFAULT_HEIGHT = 928;

    public static final int LAYER_IMAGES  = 0;
    public static final int LAYER_SIGNALS = 1;
    public static final int LAYER_BERTHS  = 2;
    public static final int LAYER_LABELS  = 3;
    public static final int LAYER_TOPS    = 4;

    public SignalMap()
    {
        this(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public SignalMap(Dimension dim)
    {
        frame = new JFrame("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));
        TabBar = new JTabbedPane();

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (SystemTray.isSupported())
        {
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent evt)
                {
                    EastAngliaMapClient.writeSetting("windowSize", ((int) frame.getSize().getWidth()) + "," + ((int) frame.getSize().getHeight()));
                    EastAngliaMapClient.writeSetting("lastTab", Integer.toString(TabBar.getSelectedIndex()));

                    if (EastAngliaMapClient.minimiseToSysTray)
                    {
                        frame.setVisible(false);

                        if (!EastAngliaMapClient.shownSystemTrayWarn)
                        {
                            EastAngliaMapClient.shownSystemTrayWarn = true;
                            SysTrayHandler.popup("App now in System Tray", TrayIcon.MessageType.INFO);
                        }
                    }
                    else
                    {
                        dispose();
                        System.exit(0);
                    }
                }
            });
        }
        else
        {
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent evt)
                {
                    EastAngliaMapClient.writeSetting("windowSize", frame.getSize().getWidth() + "," + frame.getSize().getHeight());
                    EastAngliaMapClient.writeSetting("lastTab", Integer.toString(TabBar.getSelectedIndex()));

                    dispose();
                    System.exit(0);
                }
            });
        }

        frame.setLocationByPlatform(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(Math.min(Math.max((int) dim.getWidth(), 800), DEFAULT_WIDTH), Math.min(Math.max((int) dim.getHeight(), 600), DEFAULT_HEIGHT)));
        frame.setMaximumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.setLayout(new BorderLayout());

        {
            String jsonString = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(storageDir, "data" + File.separator + "signalmap.json")))))
            {
                String line;
                while ((line = br.readLine()) != null)
                    jsonString += line.trim();
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(frame, "Unable to read map file\n" + e.toString(), "Signal Map", JOptionPane.ERROR_MESSAGE);
                EastAngliaMapClient.printThrowable(e, "SignalMap");

                System.exit(-1);
            }

            if (jsonString.isEmpty())
            {
                JOptionPane.showMessageDialog(null, "Error in map file\nEmpty file", "Signal Map", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

            Map<String, Object> json = (Map<String, Object>) JSONParser.parseJSON(jsonString);
            jsonString = null;

            EastAngliaMapClient.printOut("Map version v" + json.get("version"));

            java.util.List<Map<String, Object>> panelsJson = (java.util.List<Map<String, Object>>) json.get("signalMap");

            for (Map<String, Object> panel : panelsJson)
            {
                String name = panel.get("panelId") + ". " + panel.get("panelName");

                BackgroundPanel bp = new BackgroundPanel((String) panel.get("imageName"));
                bp.setName(name);

                panelMap.put(String.valueOf(panel.get("panelId")), bp);

                TabBar.addTab(name, null, new SideScrollPane(bp), "<html>" + panel.get("panelDescription") + "</html>");

                for (Map<String, Object> berthData : (java.util.List<Map<String, Object>>) panel.get("berths"))
                {
                    Berth berth = Berths.getOrCreateBerth(bp,
                            (int) ((long) berthData.get("posX")),
                            (int) ((long) berthData.get("posY")),
                            ((java.util.List<String>) berthData.get("berthIds")).toArray(new String[0]));

                    if (berthData.containsKey("hasBorder") && (Boolean) berthData.get("hasBorder"))
                        berth.hasBorder();
                }

                for (Map<String, Object> signalData : (java.util.List<Map<String, Object>>) panel.get("signals"))
                {
                    Signal signal = Signals.getOrCreateSignal(bp,
                            (int) ((long) signalData.get("posX")),
                            (int) ((long) signalData.get("posY")),
                            (String) signalData.get("signalId"),
                            (String) signalData.get("dataId"),
                            SignalPostDirection.getDirection(signalData.get("direction")));

                    if (signalData.containsKey("isShunt") && (Boolean) signalData.get("isShunt"))
                        signal.isShunt();
                    if (signalData.containsKey("text0"))
                        signal.set0Text(String.valueOf(signalData.get("text0")));
                    if (signalData.containsKey("text1"))
                        signal.set1Text(String.valueOf(signalData.get("text1")));
                }

                for (Map<String, Object> stationData : (java.util.List<Map<String, Object>>) panel.get("stations"))
                {
                    if (stationData.containsKey("isLarge") && (Boolean) stationData.get("isLarge"))
                    {
                        largeStation(bp,
                                (int) ((long) stationData.get("posX")),
                                (int) ((long) stationData.get("posY")),
                                (String) stationData.get("name"),
                                (String) stationData.get("url"));
                    }
                    else
                    {
                        smallStation(bp,
                                (int) ((long) stationData.get("posX")),
                                (int) ((long) stationData.get("posY")),
                                (String) stationData.get("name"),
                                (String) stationData.get("url"));
                    }
                }

                for (Map<String, Object> navButtonData : (java.util.List<Map<String, Object>>) panel.get("navButtons"))
                {
                    makeNavButton(bp,
                            (int) ((long) navButtonData.get("posX")),
                            (int) ((long) navButtonData.get("posY")),
                            (String) navButtonData.get("name"),
                            (int) ((long) navButtonData.get("linkedPanel")));
                }
            }
        }

        /*placeTestSignals(panelMap.get("11"), 1100, 15, 0,  "EN05:7","EN09:8","EN0A:1","EN0A:2","EN0A:5","EN0A:6","EN0A:8","EN0B:1","EN0B:2","","EN14:4","EN17:1","EN17:2","EN17:3","EN17:4","EN17:5","EN17:7","EN17:8","EN18:1");

        placeTestSignals(panelMap.get("9"), 300, 550, 35, "SX05:1","SX05:2","SX05:3","SX05:4","SX06:1","SX06:2",
                "SX06:3","SX06:4","SX06:5","SX06:6","SX07:3","SX07:4","SX07:6","SX07:7","SX08:4","SX09:1","SX09:4",
                "SX09:5","SX09:6","SX09:7","SX0A:1","SX0A:3","SX0B:1","SX0B:2","SX0B:3","SX0B:4","SX0B:5","SX0B:6",
                "SX0C:3","SX0C:4","SX0C:5","SX0C:6","SX0C:7","SX0C:8","SX0D:1","SX0D:2","SX0E:1","SX0E:2","SX0E:3",
                "SX0E:4","SX0E:5","SX0E:6","SX0E:7","SX0E:8","SX0F:1","SX0F:2","SX0F:4","SX0F:5","SX10:5","SX10:6",
                "SX10:7","SX11:2","SX11:3","SX12:2","SX12:5","SX12:6","SX12:7","SX12:8","SX13:1","SX14:2","SX15:1",
                "SX15:2","SX15:3","SX15:4","SX16:2","SX16:3","SX16:6","SX16:7","SX17:2","SX17:3","SX17:6");

        placeTestSignals(panelMap.get("9"), 300, 590, 35, "SX0C:2","SX0D:3","SX0D:4","SX0D:5","SX0D:6","SX0D:7",
                "SX0D:8","SX0F:3","SX10:1","SX10:2","SX10:3","SX10:4","SX10:8","SX11:1","SX11:4","SX11:5","SX11:6",
                "SX11:7","SX11:8","SX12:1","SX12:3","SX12:4","SX13:2","SX13:3","SX13:4","SX13:5","SX13:6","SX13:7",
                "SX13:8","SX14:1","SX14:3");*/

        frame.add(TabBar, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/eastangliamapclient/resources/Icon.png")));

        try
        {
            File preferencesFile = newFile(new File(storageDir, "preferences.txt"));

            Properties preferences = new Properties();
            try (FileInputStream fis = new FileInputStream(preferencesFile))
            {
                preferences.load(fis);
            }

            TabBar.setSelectedIndex(Integer.parseInt(preferences.getProperty("lastTab", Integer.toString(TabBar.getSelectedIndex()))));
        }
        catch (IOException | IndexOutOfBoundsException e) {}
    }

    //<editor-fold defaultstate="collapsed" desc="Util methods">
    private void largeStation(BackgroundPanel bp, int x, int y, String name, String crsCode)
    {
        JLabel lbl = new JLabel(name.toUpperCase());

        lbl.setBackground(EastAngliaMapClient.GREY);
        lbl.setFont(EastAngliaMapClient.TD_FONT);
        lbl.setForeground(EastAngliaMapClient.GREEN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFocusable(false);
        lbl.setToolTipText(crsCode.toUpperCase());
        lbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                EventHandler.stnClick(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                JLabel stationLbl = (JLabel) evt.getComponent();

                if (stationLbl != null)
                    stationLbl.setOpaque(true);

                frame.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                JLabel stationLbl = (JLabel) evt.getComponent();

                if (stationLbl != null)
                    stationLbl.setOpaque(false);

                frame.repaint();
            }
        });

        lbl.setBounds(x, y, name.length() * 12, 16);
        bp.add(lbl, LAYER_LABELS);
    }

    private void smallStation(BackgroundPanel bp, int x, int y, String name, String crsCode)
    {
        JLabel lbl = new JLabel(name.toUpperCase());

        lbl.setBackground(EastAngliaMapClient.GREY);
        lbl.setFont(EastAngliaMapClient.TD_FONT.deriveFont(8f));
        lbl.setForeground(EastAngliaMapClient.GREEN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFocusable(false);
        lbl.setToolTipText(crsCode.toUpperCase());
        lbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                EventHandler.stnClick(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                JLabel stationLbl = (JLabel) evt.getComponent();

                if (stationLbl != null)
                    stationLbl.setOpaque(true);

                frame.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                JLabel stationLbl = (JLabel) evt.getComponent();

                if (stationLbl != null)
                    stationLbl.setOpaque(false);

                frame.repaint();
            }
        });

        lbl.setBounds(x, y, name.length() * 6, 8);
        bp.add(lbl, LAYER_LABELS);
    }

    private void makeNavButton(BackgroundPanel bp, int x, int y, String text, final int tabIndex)
    {
        JLabel lbl = new JLabel(text.toUpperCase());

        lbl.setBackground(EastAngliaMapClient.GREY);
        lbl.setFont(EastAngliaMapClient.TD_FONT);
        lbl.setForeground(EastAngliaMapClient.GREEN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFocusable(false);
        lbl.setToolTipText("Tab: " + String.valueOf(tabIndex));
        lbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                TabBar.setSelectedIndex(tabIndex - 1);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                JLabel navLbl = (JLabel) evt.getComponent();

                if (navLbl != null)
                    navLbl.setOpaque(true);

                frame.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                JLabel navLbl = (JLabel) evt.getComponent();

                if (navLbl != null)
                    navLbl.setOpaque(false);

                frame.repaint();
            }
        });

        lbl.setBounds(x, y, text.length() * 12, 16);
        bp.add(lbl, LAYER_LABELS);
    }

    private void placeTopBits(final BackgroundPanel bp)
    {
        JButton menu = new JButton("▼");
        menu.setToolTipText("Options");
        menu.setFocusable(false);
        menu.setOpaque(false);
        menu.setBounds(70, 10, 43, 23);
        menu.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                if (evt.getButton() == 1)
                    new OptionContexMenu(evt.getComponent(), evt.getX(), evt.getY());
            }
        });
        bp.add(menu, LAYER_TOPS);

        JButton help = new JButton("?");
        help.setToolTipText("Help");
        help.setFocusable(false);
        help.setOpaque(false);
        help.setBounds(123, 10, 37, 23);
        help.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                if (evt.getButton() == 1)
                    new HelpDialog();
            }
        });
        bp.add(help, LAYER_TOPS);

        JLabel motd = new JLabel();
        motd.setText("XXMOTD");
        motd.setFocusable(false);
        motd.setOpaque(true);

        motd.setForeground(EastAngliaMapClient.GREEN);
        motd.setBackground(new Color(30, 30, 30));
        motd.setVerticalAlignment(JLabel.TOP);
        motd.setHorizontalAlignment(JLabel.LEFT);
        motd.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 16));

        motd.setPreferredSize(new Dimension(520, 16));
        motd.setBorder(new EmptyBorder(0, 10, 0, 10));

        JScrollPane spMOTD = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spMOTD.setViewportView(motd);
        spMOTD.setBounds(200, 10, 550, 50);
        bp.add(spMOTD, LAYER_TOPS);
        motdPanes.add(spMOTD);

        buttons.add(menu);
        buttons.add(help);
    }

    private void placeTestSignals(BackgroundPanel pnl, String areaId, int x, int y, int width, int min, int max)
    {
        java.util.List<String> bytes = new ArrayList<>();

        for (int i = min; i < max; i++)
        {
            String currByte = Integer.toHexString(i).toUpperCase();
            currByte = areaId + (currByte.length() % 2 != 0 ? "0" + currByte : currByte);

            for (int j = 1; j < 9; j++)
                bytes.add(currByte + ":" + j);

            bytes.add("");
        }

        placeTestSignals(pnl, x, y, width, bytes.toArray(new String[0]));
    }

    private void placeTestSignals(BackgroundPanel pnl, int x, int y, int width, String... ids)
    {
        java.util.List<String> bytes = Arrays.asList(ids);

        int curWidth = 0;
        for (String id : bytes)
        {
            if (curWidth > width - 1 && width > 0)
            {
                y += 12;
                curWidth = 0;
            }

            if (!id.isEmpty())
            {
                Signal sig = Signals.getOrCreateSignal(pnl, x + curWidth*27, y, "", id + (Signals.signalExists(id) ? " " : ""), SignalPostDirection.TEXT);
                sig.set0Text(id.substring(2));
            }

            curWidth++;
        }
    }
    //</editor-fold>

    public void dispose()
    {
        frame.dispose();
        /*Berths.reset();
        Signals.reset();*/
    }

    public void setVisible(boolean visible)
    {
        if (visible && frame.isVisible())
            frame.requestFocus();
        else
        {
            frame.setVisible(visible);
            readFromMap(EastAngliaMapClient.DataMap);
        }
    }

    public void readFromMap(Map<String, String> map)
    {
        EastAngliaMapClient.DataMap.putAll(map);

        for (Map.Entry<String, String> pairs : new HashMap<>(map).entrySet())
        {
            try
            {
                if (pairs.getKey().equals("XXMOTD"))
                {
                    setMOTD(pairs.getValue());
                    continue;
                }

                Berth berth = Berths.getBerth(pairs.getKey().toUpperCase());
                Signal signal = Signals.getSignal(pairs.getKey().toUpperCase());

                if (berth != null)
                    if (!pairs.getValue().equals("") || pairs.getKey().toUpperCase().equals(berth.getCurrentId(false)))
                        berth.interpose(pairs.getValue(), pairs.getKey().toUpperCase());

                if (signal != null)
                    signal.setState(String.valueOf(pairs.getValue()).equals("0") ? 0 : (String.valueOf(pairs.getValue()).equals("1") ? 1 : 2));
            }
            catch (Throwable t) { EastAngliaMapClient.printThrowable(t, "Handler"); }
        }
    }

    public java.util.List<BackgroundPanel> getPanels()
    {
        return panelList;
    }

    public void prepForScreencap()
    {
        for (JButton button : buttons)
            button.setVisible(false);

        for (JScrollPane sp : motdPanes)
            sp.setVisible(false);
    }

    public void finishScreencap()
    {
        for (JButton button : buttons)
            button.setVisible(true);

        for (JScrollPane sp : motdPanes)
            sp.setVisible(true);
    }

    public void setTitle(String title)
    {
        if (!frame.getTitle().equals(title))
            frame.setTitle(title);
    }

    public boolean hasFocus()
    {
        return frame.hasFocus();
    }

    public void repaint()
    {
        frame.repaint();
    }

    public void setMOTD(String motd)
    {
        motd = "<html><body style='width:auto;height:auto'>" + (motd == null || motd.isEmpty() ? "No problems" : motd.trim()) + "</body></html>";
        int size = (((motd.length() - motd.replace("<br>", "").length()) / 4) + (motd.replaceAll("\\<.*?\\>", "").length() / 30)) * 24 + 24;

        for (JScrollPane sp : motdPanes)
        {
            JLabel lbl = (JLabel) sp.getViewport().getView();
            lbl.setText(motd);
            lbl.setPreferredSize(new Dimension(520, size));
        }
    }

    public class BackgroundPanel extends JLayeredPane
    {
        private final Font clockFont = EastAngliaMapClient.TD_FONT.deriveFont(45f);
        private BufferedImage image;
        private BufferedImage bufferedImage;

        public final static int BP_DEFAULT_WIDTH  = DEFAULT_WIDTH - 23;
        public final static int BP_DEFAULT_HEIGHT = DEFAULT_HEIGHT - 68;

        BackgroundPanel(String imageName)
        {
            super();

            setFont(clockFont);
            setLayout(null);
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            setPreferredSize(new Dimension(1854, 860));
            setOpaque(true);

            setImage(imageName);

            placeTopBits(this);
            panelList.add(this);
        }

        public void setImage(String imageName)
        {
            File imageFile = new File(storageDir, "data" + File.separator + imageName + ".png");
            try (InputStream in = new FileInputStream(imageFile))
            {
                BufferedImage inImage = ImageIO.read(in);

                BufferedImage bimg = new BufferedImage(BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = bimg.createGraphics();
                g2d.drawImage(inImage, 0, 0, inImage.getWidth(), inImage.getHeight(), Color.WHITE, null);
                g2d.dispose();

                image = bimg;
            }
            catch (IllegalArgumentException | IOException e)
            {
                EastAngliaMapClient.printErr("Unable to read image file: \"" + imageFile.getAbsolutePath() + "\"");
            }

            bufferedImage = new BufferedImage(BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.clearRect(0, 0, Math.max(g.getClipBounds().width, BP_DEFAULT_WIDTH), Math.max(g.getClipBounds().height, BP_DEFAULT_HEIGHT));
            if (isOpaque())
            {
                g2d.setColor(EastAngliaMapClient.BLACK);
                g2d.fillRect(0, 0, Math.max(g.getClipBounds().width, BP_DEFAULT_WIDTH), Math.max(g.getClipBounds().height, BP_DEFAULT_HEIGHT));
            }

            g2d.drawImage(image, 0, 0, BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, null);

            g2d.setColor(EastAngliaMapClient.GREY);
            g2d.fillRect(780, 10, 280, 50);

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

            g2d.setColor(EastAngliaMapClient.GREEN);
            g2d.drawString(EastAngliaMapClient.getTime(), 787, 55);

            g2d.dispose();
        }

        public BufferedImage getBufferedImage()
        {
            return bufferedImage;
        }

        public Component add(Component comp) { return super.add(comp); }
        public Component add(Component comp, int index) { return super.add(comp, index); }
    }
}