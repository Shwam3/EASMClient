package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import static eastangliamapclient.EastAngliaMapClient.newFile;
import static eastangliamapclient.EastAngliaMapClient.storageDir;
import eastangliamapclient.SignalMapReplayController;
import eastangliamapclient.gui.mapelements.Berth;
import eastangliamapclient.gui.mapelements.Berths;
import eastangliamapclient.gui.mapelements.Point;
import eastangliamapclient.gui.mapelements.Points;
import eastangliamapclient.gui.mapelements.Signal;
import eastangliamapclient.gui.mapelements.Signals;
import eastangliamapclient.gui.mapelements.Signals.SignalType;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignalMapGui
{
    public JFrame frame;

    private final List<JScrollPane>            motdPanes = new ArrayList<>();
    private final List<BackgroundPanel>        panelList = new ArrayList<>();
    private final Map<String, BackgroundPanel> panelMap  = new HashMap<>();

    public final JTabbedPane TabBar;
    public final KeyEventDispatcher dispatcher;

    private static final Font CLOCK_FONT = EastAngliaMapClient.TD_FONT.deriveFont(45f);

    public static final int DEFAULT_WIDTH  = 1877;
    public static final int DEFAULT_HEIGHT = 928 + 21;

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
        frame = new JFrame("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  ")" /*+ (EastAngliaMapClient.autoScreencap ? " - Screencapping" : "")*/);
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

        frame.setJMenuBar(SignalMapMenuBar.instance());

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
            JSONObject json = new JSONObject(jsonString.toString());

            EastAngliaMapClient.DATA_VERSION = "-1"; //json.getString("version");
            frame.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "")
                +  " / v" + EastAngliaMapClient.DATA_VERSION + ")"
                + (EastAngliaMapClient.connected ? "" : " - Not Connected")
            );

            JSONArray panelsJson = json.getJSONArray("signalMap");

            for (Object pnlObj : panelsJson)
            {
                JSONObject panel = (JSONObject) pnlObj;
                
                if (!panel.has("panelUID") || !panel.has("panelName") || !panel.has("imageName") || !panel.has("panelDescription") ||
                        !panel.has("berths") || !panel.has("signals") || !panel.has("points") || !panel.has("text"))
                {
                    JOptionPane.showMessageDialog(frame, "Error in data files\nData not complete for \"" + String.valueOf(panel.get("panelName")) + "\"", "Error", JOptionPane.ERROR_MESSAGE);
                }
                String name = (panelMap.size()+1) + ". " + panel.getString("panelName");

                BackgroundPanel bp = new BackgroundPanel(panel.getString("imageName"));
                bp.setName(name);

                panelMap.put(panel.getString("panelUID"), bp);

                TabBar.addTab(name, null, new SideScrollPane(bp), "<html>" + panel.getString("panelDescription") + "</html>");

                //<editor-fold defaultstate="collapsed" desc="Berths">
                if (panel.has("berths"))
                {
                    for (Object bthObj : panel.getJSONArray("berths"))
                    {
                        JSONObject berthData = (JSONObject) bthObj;
                        Berth berth = Berths.getOrCreateBerth(bp, berthData.getInt("posX"), berthData.getInt("posY"), berthData.getJSONArray("dataIDs").join(",").replace("\"", "").split(","));

                        if (berthData.optBoolean("hasBorder",false))
                            berth.hasBorder();
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Signals">
                if (panel.has("signals"))
                {
                    for (Object sigObj : panel.getJSONArray("signals"))
                    {
                        JSONObject signalData = (JSONObject) sigObj;
                        
                        Signal signal = Signals.getOrCreateSignal(bp,
                                signalData.getInt("posX"),
                                signalData.getInt("posY"),
                                signalData.getString("description"),
                                signalData.has("dataIDs") ?
                                    signalData.getJSONArray("dataIDs").join(",").replace("\"", "").split(",") :
                                    signalData.has("dataID") ? new String[] { signalData.optString("dataID") } : new String[]{},
                                SignalType.getType(signalData.getString("type")));

                        if (signalData.optBoolean("isAuto", false)) signal.isAuto();
                        if (signalData.optBoolean("isShunt", false)) signal.isShunt();
                        if (signalData.optBoolean("isSubs", false)) signal.isSubs();
                        if (signalData.has("text0")) signal.set0Text(String.valueOf(signalData.get("text0")));
                        if (signalData.has("text1")) signal.set1Text(String.valueOf(signalData.get("text1")));
                        if (signalData.has("routes")) signal.setRoutes(signalData.getJSONArray("routes").join(",").replace("\"", "").split(","));
                        if (signalData.has("width")) signal.set0Text(signalData.getString("width"));
                        if (signalData.has("height")) signal.set1Text(signalData.getString("height"));
                    }
                }
                //</editor-fold>

                //<editor-fold defaultstate="collapsed" desc="Points">
                if (panel.has("points"))
                    for (Object ptsObj : panel.getJSONArray("points"))
                    {
                        JSONObject pointsData = (JSONObject) ptsObj;
                        Points.getOrCreatePoint(bp,
                                pointsData.getInt("posX"),
                                pointsData.getInt("posY"),
                                pointsData.getString("description"),
                                Arrays.asList(pointsData.getJSONArray("dataIDs").join(",").replace("\"", "").split(",")),
                                Points.PointType.getType(pointsData.getString("type0")),
                                Points.PointType.getType(pointsData.getString("type1"))
                        );
                    }
                //</editor-fold>

                /*
                //<editor-fold defaultstate="collapsed" desc="Stations">
                if (panel.has("stations"))
                    ((List<Map<String, Object>>) panel.get("stations")).stream().forEachOrdered(stationData ->
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
                if (panel.has("navButtons"))
                    ((List<Map<String, Object>>) panel.get("navButtons")).stream().forEachOrdered(navButtonData ->
                    {
                        makeNavButton(bp,
                                (int) ((long) navButtonData.get("posX")),
                                (int) ((long) navButtonData.get("posY")),
                                (String) navButtonData.get("name"),
                                (int) ((long) navButtonData.get("linkedPanel")));
                    });
                //</editor-fold>
                */
            }
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
                frame.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
                frame.pack();
                frame.setLocationRelativeTo(null);
                //frame.setPreferredSize(new Dimension(frame.getSize().width, frame.getSize().height));
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_T, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        frame.getRootPane().registerKeyboardAction(e ->
        {
            if (!EastAngliaMapClient.blockKeyInput)
                Points.togglePointVisibilities();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
            SignalMapMenuBar.instance().updateCheckBoxes();
            EastAngliaMapClient.frameSignalMap.frame.repaint();
        }, KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        //frame.getRootPane().registerKeyboardAction(e ->
        //{
        //    TimeoutHandler.stop();
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

        new javax.swing.Timer(250, e -> panelList.parallelStream().forEach(bp -> bp.repaint(780, 10, 280, 50))).start(); // Clock section only
        SignalMapMenuBar.instance().updateCheckBoxes();
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
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=actual"));
                        else
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + "?stp=WVS&show=all&order=actual"));

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
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=actual"));
                        else
                            Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + crsCode + "?stp=WVS&show=all&order=actual"));

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
        JEditorPane motd = new JEditorPane(new HTMLEditorKit().getContentType(), "XXMOTD");
        motd.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
        //motd.setContentType("text/html");
        //motd.setText("XXMOTD");
        //motd.setFocusable(false);
        motd.setEditable(false);
        motd.setOpaque(true);
        motd.setBackground(new Color(30, 30, 30));
        //motd.setForeground(EastAngliaMapClient.GREEN);
        //motd.setVerticalAlignment(JLabel.TOP);
        //motd.setHorizontalAlignment(JLabel.LEFT);
        //motd.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 16));

        ((HTMLDocument) motd.getDocument()).getStyleSheet().addRule("body{"
                +   "font-family:Monospaced;"
                +   "font-size:16pt;"
                +   "color:#009900"
                + "}"
                + "a{"
                +   "color:#EE8800"
                + "}");

        motd.setPreferredSize(new Dimension(520, 16));
        motd.setBorder(new EmptyBorder(0, 10, 0, 10));
        motd.addHyperlinkListener((HyperlinkEvent evt) ->
        {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED && Desktop.isDesktopSupported())
            {
                try { Desktop.getDesktop().browse(evt.getURL().toURI()); }
                catch (IOException | URISyntaxException | NullPointerException e) { EastAngliaMapClient.printErr(evt.getDescription()); EastAngliaMapClient.printThrowable(e, "MOTD"); }
            }
        });

        JScrollPane spMOTD = new JScrollPane(motd, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spMOTD.setBounds(100, 10, 650, 50);
        bp.add(spMOTD, LAYER_TOP);
        motdPanes.add(spMOTD);
    }
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
            updateGuiComponents(EastAngliaMapClient.DataMap);
        }
    }

    public void updateGuiComponents(Map<String, String> map)
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

                Signal[] sigs = Signals.getSignal(pairs.getKey().toUpperCase());
                if (sigs != null)
                    for (Signal signal : sigs)
                        signal.setState(String.valueOf(pairs.getValue()).equals("0") ? 0 : (String.valueOf(pairs.getValue()).equals("1") ? 1 : 2));

                List<Point> points = Points.getPoints(pairs.getKey().toUpperCase());
                if (points != null)
                    points.parallelStream().filter(p -> p != null).forEach(point ->
                        point.setState(String.valueOf(pairs.getValue()).equals("0") ? 0 : (String.valueOf(pairs.getValue()).equals("1") ? 1 : 2), pairs.getKey().toUpperCase())
                    );
            }
            catch (Exception e) { EastAngliaMapClient.printThrowable(e, "Handler"); }
        }

        frame.repaint();
    }

    public List<BackgroundPanel> getPanels()
    {
        return panelList;
    }

    //public void prepForScreencap()
    //{
    //    motdPanes.stream().forEach(sp -> sp.setVisible(false));
    //}

    //public void finishScreencap()
    //{
    //    motdPanes.stream().forEach(sp -> sp.setVisible(true));
    //}

    public void setTitle(String title)
    {
        frame.setTitle(title);
    }

    public boolean hasFocus()
    {
        return frame.hasFocus();
    }

    public void setMOTD(String motd)
    {
        String motdHTML = "<html><body style='width:auto;height:auto'>" + (motd == null || motd.isEmpty() ? "No problems" : motd.trim()) + "</body></html>";
        int height = (int) (Math.ceil(((motdHTML.length() - motdHTML.replace("<br>", "").length()) / 4) + motdHTML.replaceAll("\\<.*?\\>", "").length() / 30) * 16 + 12);

        motdPanes.parallelStream().forEach(sp ->
        {
            JEditorPane lbl = (JEditorPane) sp.getViewport().getView();
            lbl.setText(motdHTML);
            lbl.setPreferredSize(new Dimension(520, height));
        });
    }

    public class BackgroundPanel extends JLayeredPane
    {
        private BufferedImage image;

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
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setColor(EastAngliaMapClient.BLACK);
            g2d.fillRect(0, 0, Math.max(g.getClipBounds().width, BP_DEFAULT_WIDTH), Math.max(g.getClipBounds().height, BP_DEFAULT_HEIGHT));

            g2d.drawImage(image, 0, 0, BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, null);

            g2d.setColor(EastAngliaMapClient.GREY);
            g2d.fillRect(780, 10, 280, 50);

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

            g2d.setColor(EastAngliaMapClient.GREEN);
            g2d.drawString(SignalMapGui.getTime(), 787, 55);

            g2d.dispose();
        }

        public Component add(Component comp) { return super.add(comp, LAYER_TOP); }
        public Component add(Component comp, int index) { return super.add(comp, index); }
    }

    public static final SimpleDateFormat clockSDF = new SimpleDateFormat("HH:mm:ss");
    public static String getTime()
    {
        if (SignalMapReplayController.isActive())
           return LocalTime.ofSecondOfDay(SignalMapReplayController.getTimeOffset()).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        else
            return clockSDF.format(new Date());
    }
}