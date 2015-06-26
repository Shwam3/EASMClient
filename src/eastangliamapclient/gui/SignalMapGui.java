package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import static eastangliamapclient.EastAngliaMapClient.newFile;
import static eastangliamapclient.EastAngliaMapClient.storageDir;
import eastangliamapclient.MessageHandler;
import eastangliamapclient.gui.Signals.SignalType;
import eastangliamapclient.json.JSONParser;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class SignalMapGui
{
    public JFrame frame;

    private final List<JButton>                buttons   = new ArrayList<>();
    private final List<JScrollPane>            motdPanes = new ArrayList<>();
    private final List<BackgroundPanel>        panelList = new ArrayList<>();
    private final Map<String, BackgroundPanel> panelMap  = new HashMap<>();

    public final JTabbedPane TabBar;
    public final KeyEventDispatcher dispatcher;

    private static final Font CLOCK_FONT = EastAngliaMapClient.TD_FONT.deriveFont(45f);

    public static final int DEFAULT_WIDTH  = 1877;
    public static final int DEFAULT_HEIGHT = 928;

    public static final int LAYER_IMAGES  = 0;
    public static final int LAYER_SIGNALS = 1;
    public static final int LAYER_BERTHS  = 2;
    public static final int LAYER_LABELS  = 3;
    public static final int LAYER_TOP     = 4;

    public SignalMapGui()
    {
        this(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }

    public SignalMapGui(Dimension dim)
    {
        frame = new JFrame("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));
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

        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(Math.min(Math.max((int) dim.getWidth(), 800), DEFAULT_WIDTH), Math.min(Math.max((int) dim.getHeight(), 600), DEFAULT_HEIGHT)));
        frame.setMaximumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.setLayout(new BorderLayout());

        {
            StringBuilder jsonString = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new FileReader(new File(storageDir, "data" + File.separator + "signalmap.json"))))
            {
                String line;
                while ((line = br.readLine()) != null)
                    jsonString.append(line);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(frame, "Unable to read map file\n" + e.toString(), "Signal Map", JOptionPane.ERROR_MESSAGE);
                EastAngliaMapClient.printThrowable(e, "SignalMap");

                System.exit(-1);
            }

            if (jsonString.length() == 0)
            {
                JOptionPane.showMessageDialog(null, "Error in map file\nEmpty file", "Signal Map", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }

            jsonString.trimToSize();
            Map<String, Object> json = (Map<String, Object>) JSONParser.parseJSON(jsonString.toString());

            EastAngliaMapClient.DATA_VERSION = String.valueOf(json.get("version"));
            frame.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  " / v" + EastAngliaMapClient.DATA_VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));

            List<Map<String, Object>> panelsJson = (List<Map<String, Object>>) json.get("signalMap");

            panelsJson.stream().forEachOrdered(panel ->
            {
                String name = panel.get("panelId") + ". " + panel.get("panelName");

                BackgroundPanel bp = new BackgroundPanel((String) panel.get("imageName"));
                bp.setName(name);

                panelMap.put(String.valueOf(panel.get("panelId")), bp);

                TabBar.addTab(name, null, new SideScrollPane(bp), "<html>" + panel.get("panelDescription") + "</html>");

                //<editor-fold defaultstate="collapsed" desc="Berths">
                ((List<Map<String, Object>>) panel.get("berths")).parallelStream().forEach(berthData ->
                {
                    Berth berth = Berths.getOrCreateBerth(bp,
                            (int) ((long) berthData.get("posX")),
                            (int) ((long) berthData.get("posY")),
                            ((List<String>) berthData.get("berthIds")).toArray(new String[0]));

                    if (berthData.containsKey("hasBorder") && (Boolean) berthData.get("hasBorder"))
                        berth.hasBorder();
                });
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Signals">
                ((List<Map<String, Object>>) panel.get("signals")).parallelStream().forEach(signalData ->
                {
                    Signal signal = Signals.getOrCreateSignal(bp,
                            (int) ((long) signalData.get("posX")),
                            (int) ((long) signalData.get("posY")),
                            (String) signalData.get("signalId"),
                            (String) signalData.get("dataId"),
                            SignalType.getType(signalData.get("direction")));

                    if (signalData.containsKey("isShunt") && (Boolean) signalData.get("isShunt"))
                        signal.isShunt();
                    if (signalData.containsKey("text0"))
                        signal.set0Text(String.valueOf(signalData.get("text0")));
                    if (signalData.containsKey("text1"))
                        signal.set1Text(String.valueOf(signalData.get("text1")));
                  //if (signalData.containsKey("width"))
                  //    signal.set0Text(String.valueOf(signalData.get("width")));
                  //if (signalData.containsKey("height"))
                  //    signal.set1Text(String.valueOf(signalData.get("height")));
                });
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Stations">
                ((List<Map<String, Object>>) panel.get("stations")).parallelStream().forEach(stationData ->
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
                });
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Nav Buttons">
                ((List<Map<String, Object>>) panel.get("navButtons")).parallelStream().forEach(navButtonData ->
                {
                    makeNavButton(bp,
                            (int) ((long) navButtonData.get("posX")),
                            (int) ((long) navButtonData.get("posY")),
                            (String) navButtonData.get("name"),
                            (int) ((long) navButtonData.get("linkedPanel")));
                });
                //</editor-fold>
            });
        }

        //<editor-fold defaultstate="collapsed" desc="Keyboard Commands">
        dispatcher = e ->
        {
            if (e.getID() == KeyEvent.KEY_PRESSED && !EastAngliaMapClient.blockKeyInput)
            {
                int keyCode = e.getKeyCode();

                if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F1 + TabBar.getTabCount() - 1) // Function keys
                {
                    keyCode -= KeyEvent.VK_F1;

                    if (e.isControlDown()) // Control for tabs 13-24
                        keyCode += 12;
                    if (e.isShiftDown()) // Shift for tabs 25-36, both for tabs 37-48
                        keyCode += 24;

                    if (keyCode >= 0 && keyCode <= TabBar.getTabCount() - 1)
                        TabBar.setSelectedIndex(keyCode);

                    return true;
                }

                if (!TabBar.hasFocus())
                {
                    TabBar.requestFocusInWindow();

                    if (keyCode == KeyEvent.VK_LEFT)
                        TabBar.setSelectedIndex(Math.max(0, TabBar.getSelectedIndex() - 1));
                    else if (keyCode == KeyEvent.VK_RIGHT)
                        TabBar.setSelectedIndex(Math.min(TabBar.getTabCount() - 1, TabBar.getSelectedIndex() + 1));

                    return keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT;
                }
            }
            return false;
        };

        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
            {
                //frame.setPreferredSize(new Dimension(1877, 928));
                frame.pack();
                frame.setLocationRelativeTo(null);
                //frame.setPreferredSize(new Dimension(frame.getSize().width, frame.getSize().height));
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                Signals.toggleSignalVisibilities();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                Berths.toggleBerthVisibilities();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                Berths.toggleBerthsOpacities();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_O, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                Berths.toggleBerthDescriptions();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                new HelpDialog();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_H, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            EastAngliaMapClient.blockKeyInput = false;
            EastAngliaMapClient.clean();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //frame.getRootPane().registerKeyboardAction(e ->
        //{
        //    MessageHandler.stop();
        //    Cursor origCursor = frame.getRootPane().getCursor();
        //    frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        //    Signals.reset();
        //    Berths.reset();

        //    EastAngliaMapClient.frameSignalMap = new SignalMapGui();

        //    KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
        //    frame.getContentPane().setCursor(origCursor);
        //    frame.dispose();

        //    EastAngliaMapClient.reconnect(true);
        //    EastAngliaMapClient.frameSignalMap.setVisible(true);

        //    EastAngliaMapClient.blockKeyInput = false;
        //    EastAngliaMapClient.clean();
        //}, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        //</editor-fold>

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

        Timer timer = new Timer("keepAwake", true);
        timer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    PointerInfo mouseInfo = MouseInfo.getPointerInfo();
                    if (mouseInfo != null)
                        new Robot(mouseInfo.getDevice()).mouseMove(mouseInfo.getLocation().x, mouseInfo.getLocation().y);
                }
                catch (AWTException | NullPointerException e) {}
            }
        }, 30000, 10000);
        timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try { MessageHandler.requestAll(); }
                catch (Exception e) {}
            }
        }, 30000, 120000);

        new javax.swing.Timer(250, e -> panelList.parallelStream().forEach(bp -> bp.repaint(780, 10, 280, 50))).start(); // Clock section only
    }

    //<editor-fold defaultstate="collapsed" desc="Util methods">
    private void largeStation(BackgroundPanel bp, int x, int y, String name, String crsCode)
    {
        final JLabel stationLbl = new JLabel(name.toUpperCase());

        stationLbl.setBackground(EastAngliaMapClient.GREY);
        stationLbl.setFont(EastAngliaMapClient.TD_FONT);
        stationLbl.setForeground(EastAngliaMapClient.GREEN);
        stationLbl.setHorizontalAlignment(SwingConstants.CENTER);
        stationLbl.setFocusable(false);
        stationLbl.setToolTipText(crsCode.toUpperCase());
        stationLbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                if (SwingUtilities.isLeftMouseButton(evt))
                {
                    try
                    {
                        if (evt.isControlDown())
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=wtt"));
                        else
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + "?stp=WVS&show=all&order=wtt"));

                        evt.consume();
                    }
                    catch (URISyntaxException | IOException e) {}
                }
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                stationLbl.setOpaque(true);
                stationLbl.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                stationLbl.setOpaque(false);
                stationLbl.repaint();
            }
        });

        stationLbl.setBounds(x, y, name.length() * 12, 16);
        bp.add(stationLbl, LAYER_LABELS);
    }

    private void smallStation(BackgroundPanel bp, int x, int y, String name, String crsCode)
    {
        final JLabel stationLbl = new JLabel(name.toUpperCase());

        stationLbl.setBackground(EastAngliaMapClient.GREY);
        stationLbl.setFont(EastAngliaMapClient.TD_FONT.deriveFont(8f));
        stationLbl.setForeground(EastAngliaMapClient.GREEN);
        stationLbl.setHorizontalAlignment(SwingConstants.CENTER);
        stationLbl.setFocusable(false);
        stationLbl.setToolTipText(crsCode.toUpperCase());
        stationLbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                if (SwingUtilities.isLeftMouseButton(evt))
                {
                    try
                    {
                        if (evt.isControlDown())
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=wtt"));
                        else
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + "?stp=WVS&show=all&order=wtt"));

                        evt.consume();
                    }
                    catch (URISyntaxException | IOException e) {}
                }
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                stationLbl.setOpaque(true);
                stationLbl.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                stationLbl.setOpaque(false);
                stationLbl.repaint();
            }
        });

        stationLbl.setBounds(x, y, name.length() * 6, 8);
        bp.add(stationLbl, LAYER_LABELS);
    }

    private void makeNavButton(BackgroundPanel bp, int x, int y, String text, final int tabIndex)
    {
        final JLabel navLbl = new JLabel(text.toUpperCase());

        navLbl.setBackground(EastAngliaMapClient.GREY);
        navLbl.setFont(EastAngliaMapClient.TD_FONT);
        navLbl.setForeground(EastAngliaMapClient.GREEN);
        navLbl.setHorizontalAlignment(SwingConstants.CENTER);
        navLbl.setFocusable(false);
        navLbl.setToolTipText("Tab: " + String.valueOf(tabIndex));
        navLbl.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                TabBar.setSelectedIndex(tabIndex - 1);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                navLbl.setOpaque(true);
                navLbl.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                navLbl.setOpaque(false);
                navLbl.repaint();
            }
        });

        navLbl.setBounds(x, y, text.length() * 12, 16);
        bp.add(navLbl, LAYER_LABELS);
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
                if (SwingUtilities.isLeftMouseButton(evt))
                    new OptionContextMenu(evt.getComponent());
            }
        });
        bp.add(menu, LAYER_TOP);

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
                if (SwingUtilities.isLeftMouseButton(evt))
                    new HelpDialog();
            }
        });
        bp.add(help, LAYER_TOP);

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
        bp.add(spMOTD, LAYER_TOP);
        motdPanes.add(spMOTD);

        buttons.add(menu);
        buttons.add(help);
    }

    //private void placeTestSignals(BackgroundPanel pnl, String areaId, int x, int y, int width, int min, int max)
    //{
    //    List<String> bytes = new ArrayList<>();
    //
    //    for (int i = min; i < max; i++)
    //    {
    //        String currByte = Integer.toHexString(i).toUpperCase();
    //        currByte = areaId + (currByte.length() % 2 != 0 ? "0" + currByte : currByte);
    //
    //        for (int j = 1; j < 9; j++)
    //            bytes.add(currByte + ":" + j);
    //
    //        bytes.add("");
    //    }
    //
    //    placeTestSignals(pnl, x, y, width, bytes.toArray(new String[0]));
    //}

    //private void placeTestSignals(BackgroundPanel pnl, int x, int y, int width, String... ids)
    //{
    //    List<String> bytes = Arrays.asList(ids);
    //
    //    int curWidth = 0;
    //    for (String id : bytes)
    //    {
    //        if (curWidth > width - 1 && width > 0)
    //        {
    //            y += 12;
    //            curWidth = 0;
    //        }
    //
    //        if (!id.isEmpty())
    //        {
    //            Signal sig = Signals.getOrCreateSignal(pnl, x + curWidth*27, y, "", id + (Signals.signalExists(id) ? " " : ""), SignalType.TEXT);
    //            sig.set0Text(id.substring(2));
    //        }
    //
    //        curWidth++;
    //    }
    //}
    //</editor-fold>

    public void dispose()
    {
        frame.dispose();
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
                if (berth != null)
                    if (!pairs.getValue().equals("") || pairs.getKey().toUpperCase().equals(berth.getCurrentId(false)))
                        berth.interpose(pairs.getValue(), pairs.getKey().toUpperCase());

                Signal signal = Signals.getSignal(pairs.getKey().toUpperCase());
                if (signal != null)
                    signal.setState(String.valueOf(pairs.getValue()).equals("0") ? 0 : (String.valueOf(pairs.getValue()).equals("1") ? 1 : 2));
            }
            catch (Exception e) { EastAngliaMapClient.printThrowable(e, "Handler"); }
        }
    }

    public List<BackgroundPanel> getPanels()
    {
        return panelList;
    }

    public void prepForScreencap()
    {
        buttons.stream().forEach(button -> button.setVisible(false));
        motdPanes.stream().forEach(sp -> sp.setVisible(false));
    }

    public void finishScreencap()
    {
        buttons.stream().forEach(button -> button.setVisible(true));
        motdPanes.stream().forEach(sp -> sp.setVisible(true));
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

    public void setMOTD(String motd)
    {
        String motdHTML = "<html><body style='width:auto;height:auto'>" + (motd == null || motd.isEmpty() ? "No problems" : motd.trim()) + "</body></html>";
        int height = (((motdHTML.length() - motdHTML.replace("<br>", "").length()) / 4) + (motdHTML.replaceAll("\\<.*?\\>", "").length() / 30)) * 12 + 12;

        motdPanes.parallelStream().forEach(sp ->
        {
            JLabel lbl = (JLabel) sp.getViewport().getView();
            lbl.setText(motdHTML);
            lbl.setPreferredSize(new Dimension(520, height));
        });
    }

    public class BackgroundPanel extends JLayeredPane
    {
        private BufferedImage image;
        private BufferedImage bufferedImage;

        public final static int BP_DEFAULT_WIDTH  = DEFAULT_WIDTH - 23;
        public final static int BP_DEFAULT_HEIGHT = DEFAULT_HEIGHT - 68;

        BackgroundPanel(String imageName)
        {
            super();

            setFont(CLOCK_FONT);
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