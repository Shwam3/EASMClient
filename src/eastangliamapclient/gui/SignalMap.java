package eastangliamapclient.gui;

import eastangliamapclient.*;
import static eastangliamapclient.EastAngliaMapClient.newFile;
import static eastangliamapclient.EastAngliaMapClient.storageDir;
import eastangliamapclient.Signals.SignalDirection;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SignalMap
{
    public JFrame frame;

    private final java.util.List<JButton>         buttons   = new ArrayList<>();
    private final java.util.List<JScrollPane>     motdPanes = new ArrayList<>();
    private final java.util.List<BackgroundPanel> panels    = new ArrayList<>();

    public static final int DEFAULT_WIDTH  = 1877; //1874;
    public static final int DEFAULT_HEIGHT = 928; //922;

    //<editor-fold defaultstate="collapsed" desc="Form variables">
    public        JTabbedPane TabBar;

    private final BackgroundPanel bpNorwich     = new BackgroundPanel("Norwich+Trowse.png");
    private final BackgroundPanel bpCambridgeEN = new BackgroundPanel("CambridgeEN.png");
    private final BackgroundPanel bpCambridgeCA = new BackgroundPanel("CambridgeCA.png");
    private final BackgroundPanel bpIpswich     = new BackgroundPanel("Ipswich+Saxmundham.png");
    private final BackgroundPanel bpClacton     = new BackgroundPanel("EastGates+Thorpe+Clacton.png");
    private final BackgroundPanel bpColchester  = new BackgroundPanel("Colchester+Parkeston.png");
    private final BackgroundPanel bpHarlow      = new BackgroundPanel("Harlow.png");
    private final BackgroundPanel bpHackney     = new BackgroundPanel("Hackney+Brimsdown.png");
    private final BackgroundPanel bpWitham      = new BackgroundPanel("Witham.png");
    private final BackgroundPanel bpShenfield   = new BackgroundPanel("Shenfield.png");
    private final BackgroundPanel bpIlford      = new BackgroundPanel("Ilford.png");
    private final BackgroundPanel bpStratford   = new BackgroundPanel("Stratford+LivSt.png");

    private final SideScrollPane spNorwich     = new SideScrollPane();
    private final SideScrollPane spCambridgeEN = new SideScrollPane();
    private final SideScrollPane spCambridgeCA = new SideScrollPane();
    private final SideScrollPane spIpswich     = new SideScrollPane();
    private final SideScrollPane spClacton     = new SideScrollPane();
    private final SideScrollPane spColchester  = new SideScrollPane();
    private final SideScrollPane spIlford      = new SideScrollPane();
    private final SideScrollPane spShenfield   = new SideScrollPane();
    private final SideScrollPane spStratford   = new SideScrollPane();
    private final SideScrollPane spWitham      = new SideScrollPane();
    private final SideScrollPane spHackney     = new SideScrollPane();
    private final SideScrollPane spHarlow      = new SideScrollPane();
    //</editor-fold>

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
        else
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

        frame.setLocationByPlatform(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(Math.min(Math.max((int) dim.getWidth(), 800), DEFAULT_WIDTH), Math.min(Math.max((int) dim.getHeight(), 600), DEFAULT_HEIGHT)));
        frame.setMaximumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        frame.setLayout(new BorderLayout());

        initStratford();
        initIlford();
        initShenfield();
        initWitham();
        initHackney();
        initHarlow();
        initColchester();
        initClacton();
        initIpswich();
        initCambridgeCA();
        initCambridgeEN();
        initNorwich();

        placeTestSignals(bpCambridgeEN, "EN", 1100, 10,  7,  0x00, 0x1C);
      //placeTestSignals(bpCambridgeEN, "CA", 970,  367, 8,  0x00, 0xC7);
        placeTestSignals(bpWitham,      "SE", 250,  490, 12, 0x00, 0x41);
        placeTestSignals(bpIlford,      "SI", 820,  550, 8,  0x00, 0x36);
        placeTestSignals(bpStratford,   "LS", 325,  700, 7,  0x00, 0x41);
        placeTestSignals(bpIpswich,     "SX", 300,  550, 6,  0x01, 0x18);
        //placeTestSignals(bpIpswich,     "CC", 10,   70,  17, 0x08, 0xD5);
        placeTestSignals(bpNorwich,     "CC", 1224, 120, 3,  0x90, 0xA0);
        placeTestSignals(bpNorwich,     "CC", 300,  220, 10, 0x58, 0x5E);
      //placeTestSignals(bpNorwich,     "CC", 300,  110, 10, 0xC7, 0xCB);

        bpStratford  .setName("1. Liverpool St/Stratford");
        bpIlford     .setName("2. Ilford");
        bpShenfield  .setName("3. Shenfield");
        bpWitham     .setName("4. Witham");
        bpHackney    .setName("5. Hackney/Brimsdown");
        bpHarlow     .setName("6. Harlow");
        bpColchester .setName("7. Colchester");
        bpClacton    .setName("8. Clacton/Thorpe/East Gates");
        bpIpswich    .setName("9. Ipswich");
        bpCambridgeCA.setName("10. Cambridge (CA)");
        bpCambridgeEN.setName("11. Cambridge (EN)");
        bpNorwich    .setName("12. Norwich/Trowse Bridge");

        spStratford  .setViewportView(bpStratford);
        spIlford     .setViewportView(bpIlford);
        spShenfield  .setViewportView(bpShenfield);
        spWitham     .setViewportView(bpWitham);
        spHackney    .setViewportView(bpHackney);
        spHarlow     .setViewportView(bpHarlow);
        spColchester .setViewportView(bpColchester);
        spClacton    .setViewportView(bpClacton);
        spIpswich    .setViewportView(bpIpswich);
        spCambridgeCA.setViewportView(bpCambridgeCA);
        spCambridgeEN.setViewportView(bpCambridgeEN);
        spNorwich    .setViewportView(bpNorwich);

        TabBar.addTab(bpStratford  .getName(), null, spStratford,   "<html>London Liverpool Street - Manor Park, Coppermill Jnc &amp; Orient Way</html>");
        TabBar.addTab(bpIlford     .getName(), null, spIlford,      "<html>Forest Gate - Harold Wood</html>");
        TabBar.addTab(bpShenfield  .getName(), null, spShenfield,   "<html>Harold Wood - Ingatestone</html>");
        TabBar.addTab(bpWitham     .getName(), null, spWitham,      "<html>Shenfield - Colchester</html>");
        TabBar.addTab(bpHackney    .getName(), null, spHackney,     "<html>Hackney Downs - Chingford, Enfield Town &amp; Cheshunt</html>");
        TabBar.addTab(bpHarlow     .getName(), null, spHarlow,      "<html>Cheshunt - Elsenham, Hertford East &amp; Stansted Airport</html>");
        TabBar.addTab(bpColchester .getName(), null, spColchester,  "<html>Colchester - Ipswich</html>");
        TabBar.addTab(bpClacton    .getName(), null, spClacton,     "<html>Colchester - Colchester Town, Clacton-on-Sea &amp; Walton-on-the-Naze</html>");
        TabBar.addTab(bpIpswich    .getName(), null, spIpswich,     "<html>Ipswich  - Stowmarket</html>");
        TabBar.addTab(bpCambridgeCA.getName(), null, spCambridgeCA, "<html>Elsenham - Ely</html>");
        TabBar.addTab(bpCambridgeEN.getName(), null, spCambridgeEN, "<html>Ely North Jnc - Manea, Kings Lynn &amp; Wymondham<br>Bury St. Edmunds - Ely &amp; Cambridge</html>");
        TabBar.addTab(bpNorwich    .getName(), null, spNorwich,     "<html>Stowmarket, Sheringham &amp; Whitlingham Jnc - Norwich</html>");

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
        catch (IOException e) {}
    }

    //<editor-fold defaultstate="collapsed" desc="Panels">
    //<editor-fold defaultstate="collapsed" desc="Norwich">
    private void initNorwich()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpNorwich, 154,  282, "CC0392");
        Berths.getOrCreateBerth(bpNorwich, 154,  250, "CC0393");
        Berths.getOrCreateBerth(bpNorwich, 218,  250, "CC0395");
        Berths.getOrCreateBerth(bpNorwich, 298,  282, "CC0396");
        Berths.getOrCreateBerth(bpNorwich, 298,  250, "CC0413");
        Berths.getOrCreateBerth(bpNorwich, 362,  250, "CC0415");
        Berths.getOrCreateBerth(bpNorwich, 362,  282, "CC0416");
        Berths.getOrCreateBerth(bpNorwich, 490,  282, "CC0422");
        Berths.getOrCreateBerth(bpNorwich, 426,  250, "CC0423");
        Berths.getOrCreateBerth(bpNorwich, 554,  282, "CC0424");
        Berths.getOrCreateBerth(bpNorwich, 490,  250, "CC0427");
        Berths.getOrCreateBerth(bpNorwich, 554,  250, "CC0429");
        Berths.getOrCreateBerth(bpNorwich, 618,  282, "CC0430", "CC0433");
        Berths.getOrCreateBerth(bpNorwich, 618,  250, "CC0431");
        Berths.getOrCreateBerth(bpNorwich, 762,  282, "CC0436");
        Berths.getOrCreateBerth(bpNorwich, 762,  250, "CC0439", "CC1416");
        Berths.getOrCreateBerth(bpNorwich, 826,  282, "CC0442");
        Berths.getOrCreateBerth(bpNorwich, 826,  250, "CC0443");
        Berths.getOrCreateBerth(bpNorwich, 890,  250, "CC0447");
        Berths.getOrCreateBerth(bpNorwich, 890,  282, "CC0448");
        Berths.getOrCreateBerth(bpNorwich, 954,  250, "CC0453");
        Berths.getOrCreateBerth(bpNorwich, 954,  282, "CC0454");
        Berths.getOrCreateBerth(bpNorwich, 1018, 282, "CC0460");
        Berths.getOrCreateBerth(bpNorwich, 1434, 410, "CC0499");
        Berths.getOrCreateBerth(bpNorwich, 1322, 378, "CC0500");
        Berths.getOrCreateBerth(bpNorwich, 1322, 410, "CC0501");
        Berths.getOrCreateBerth(bpNorwich, 1434, 378, "CC0531");
        Berths.getOrCreateBerth(bpNorwich, 1418, 314, "CC0533");
        Berths.getOrCreateBerth(bpNorwich, 1418, 346, "CC0535");
        Berths.getOrCreateBerth(bpNorwich, 1018, 250, "CC0541");
        Berths.getOrCreateBerth(bpNorwich, 1082, 250, "CC0543");
        Berths.getOrCreateBerth(bpNorwich, 1082, 282, "CC0546");
        Berths.getOrCreateBerth(bpNorwich, 1146, 250, "CC0549");
        Berths.getOrCreateBerth(bpNorwich, 1210, 250, "CC0550");
        Berths.getOrCreateBerth(bpNorwich, 1210, 282, "CC0552");
        Berths.getOrCreateBerth(bpNorwich, 1210, 314, "CC0554");
        Berths.getOrCreateBerth(bpNorwich, 1258, 250, "CC0557");
        Berths.getOrCreateBerth(bpNorwich, 1258, 282, "CC0559");
        Berths.getOrCreateBerth(bpNorwich, 1258, 314, "CC0561");
        Berths.getOrCreateBerth(bpNorwich, 1386, 250, "CC0564");
        Berths.getOrCreateBerth(bpNorwich, 1434, 250, "CC0565");
        Berths.getOrCreateBerth(bpNorwich, 1386, 282, "CC0566");
        Berths.getOrCreateBerth(bpNorwich, 1434, 282, "CC0567");
        Berths.getOrCreateBerth(bpNorwich, 1578, 154, "CC0570");
        Berths.getOrCreateBerth(bpNorwich, 1578, 186, "CC0572");
        Berths.getOrCreateBerth(bpNorwich, 1642, 218, "CC0574");
        Berths.getOrCreateBerth(bpNorwich, 1642, 250, "CC0576");
        Berths.getOrCreateBerth(bpNorwich, 1642, 282, "CC0578");
        Berths.getOrCreateBerth(bpNorwich, 1642, 314, "CC0580");
        Berths.getOrCreateBerth(bpNorwich, 1642, 378, "CC0584");
        Berths.getOrCreateBerth(bpNorwich, 1642, 410, "CC0586");
        Berths.getOrCreateBerth(bpNorwich, 1562, 362, "CC0588");
        Berths.getOrCreateBerth(bpNorwich, 554,  186, "CC0679");
        Berths.getOrCreateBerth(bpNorwich, 554,  154, "CC0680");
        Berths.getOrCreateBerth(bpNorwich, 490,  186, "CC0689");
        Berths.getOrCreateBerth(bpNorwich, 426,  186, "CC0691");
        Berths.getOrCreateBerth(bpNorwich, 362,  154, "CC0692");
        Berths.getOrCreateBerth(bpNorwich, 298,  154, "CC0698");
        Berths.getOrCreateBerth(bpNorwich, 298,  186, "CC0699");
        Berths.getOrCreateBerth(bpNorwich, 1002, 314, "CC0877");
        Berths.getOrCreateBerth(bpNorwich, 1002, 346, "CC0878");
        Berths.getOrCreateBerth(bpNorwich, 1066, 314, "CC0889");
        Berths.getOrCreateBerth(bpNorwich, 1130, 346, "CC0890");
        Berths.getOrCreateBerth(bpNorwich, 1130, 314, "CC0891");
        Berths.getOrCreateBerth(bpNorwich, 682,  314, "CC1408", "CC1409");
        Berths.getOrCreateBerth(bpNorwich, 682,  346, "CC1411");
        Berths.getOrCreateBerth(bpNorwich, 682,  282, "CC1413");
        Berths.getOrCreateBerth(bpNorwich, 1770, 218, "CC1574").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 250, "CC1576").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 282, "CC1578").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 314, "CC1580").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 378, "CC1584").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 410, "CC1586").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1370, 362, "CC1491", "CCCPT1");
        Berths.getOrCreateBerth(bpNorwich, 1354, 306, "CC1762", "CCCPT2");
        Berths.getOrCreateBerth(bpNorwich, 1706, 346, "CC1792");
        Berths.getOrCreateBerth(bpNorwich, 938,  314, "CC8237");
        Berths.getOrCreateBerth(bpNorwich, 938,  346, "CC8246");
        Berths.getOrCreateBerth(bpNorwich, 1114, 410, "CCBL03");
        Berths.getOrCreateBerth(bpNorwich, 1114, 378, "CCBL28");
        Berths.getOrCreateBerth(bpNorwich, 650,  154, "CCBURD").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1658, 186, "CCCSDG").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1290, 330, "CCHSSD");
        Berths.getOrCreateBerth(bpNorwich, 1514, 386, "CCENGN");
        Berths.getOrCreateBerth(bpNorwich, 1514, 346, "CCFUEL");
        Berths.getOrCreateBerth(bpNorwich, 1562, 410, "CCJUBS").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1418, 442, "CCL533").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1418, 466, "CCL535").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1362, 466, "CCLSR1").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1362, 442, "CCLSR2").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1770, 346, "CCMSDG").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1706, 218, "CCR574");
        Berths.getOrCreateBerth(bpNorwich, 1706, 250, "CCR576");
        Berths.getOrCreateBerth(bpNorwich, 1706, 282, "CCR578");
        Berths.getOrCreateBerth(bpNorwich, 1706, 314, "CCR580");
        Berths.getOrCreateBerth(bpNorwich, 1706, 378, "CCR584");
        Berths.getOrCreateBerth(bpNorwich, 1706, 410, "CCR586");
        Berths.getOrCreateBerth(bpNorwich, 1258, 378, "CCTB02");
        Berths.getOrCreateBerth(bpNorwich, 1258, 410, "CCTB03");
        Berths.getOrCreateBerth(bpNorwich, 1178, 378, "CCTB04");
        Berths.getOrCreateBerth(bpNorwich, 1178, 410, "CCTB05");
        Berths.getOrCreateBerth(bpNorwich, 1178, 474, "CCTB06");
        Berths.getOrCreateBerth(bpNorwich, 1178, 506, "CCTB09");
        Berths.getOrCreateBerth(bpNorwich, 1050, 474, "CCTB10");
        Berths.getOrCreateBerth(bpNorwich, 1114, 506, "CCTB13");
        Berths.getOrCreateBerth(bpNorwich, 970,  474, "CCTB14");
        Berths.getOrCreateBerth(bpNorwich, 1050, 506, "CCTB17");
        Berths.getOrCreateBerth(bpNorwich, 858,  442, "CCTB18");
        Berths.getOrCreateBerth(bpNorwich, 906,  474, "CCTB21");
        Berths.getOrCreateBerth(bpNorwich, 810,  506, "CCTB24");
        Berths.getOrCreateBerth(bpNorwich, 762,  506, "CCTB25");
        Berths.getOrCreateBerth(bpNorwich, 810,  474, "CCTB26");
        Berths.getOrCreateBerth(bpNorwich, 682,  474, "CCTB28");
        Berths.getOrCreateBerth(bpNorwich, 506,  570, "CCTB31");
        Berths.getOrCreateBerth(bpNorwich, 522,  474, "CCTB32");
        Berths.getOrCreateBerth(bpNorwich, 618,  474, "CCTB33");
        Berths.getOrCreateBerth(bpNorwich, 522,  506, "CCTB34");
        Berths.getOrCreateBerth(bpNorwich, 874,  498, "CCTB59")/*.hasBorder()*/;
        Berths.getOrCreateBerth(bpNorwich, 426,  570, "CCX034", "CCX032").hasBorder();
        Berths.getOrCreateBerth(bpNorwich, 1658, 154, "CCYARD").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        Signals.getOrCreateSignal(bpNorwich, 152,  302, "CC0392", "CCC7:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 206,  246, "CC0393", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 270,  246, "CC0395", "CCC7:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 296,  302, "CC0396", "CCC7:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 350,  246, "CC0413", "CCC7:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 414,  246, "CC0415", "CCC8:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 360,  302, "CC0416", "CCC8:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 488,  302, "CC0422", "CCC8:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 478,  246, "CC0423", "CC58:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 552,  302, "CC0424", "CC58:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 552,  312, "CC0424", "CC58:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 542,  246, "CC0427", "CC58:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 606,  246, "CC0429", "CC59:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 614,  302, "CC0430", "CC59:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 672,  246, "CC0431", "CC59:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 760,  302, "CC0436", "CC5A:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 814,  246, "CC0439", "CC5B:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 942,  246, "CC0447", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 824,  302, "CC0442", "CC5B:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 878,  246, "CC0443", "CC5B:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 888,  302, "CC0448", "CC5C:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1006, 246, "CC0453", "CC5C:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 952,  302, "CC0454", "CC5D:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1016, 302, "CC0460", "CC5D:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1432, 430, "CC0499", "CC9A:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1368, 374, "CC0500", "CC9A:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1320, 430, "CC0501", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1486, 374, "CC0531", "CC9A:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1470, 310, "CC0533", "CC9A:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1470, 342, "CC0535", "CC9A:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1070, 246, "CC0541", "CC90:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1134, 246, "CC0543", "CC90:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1080, 302, "CC0546", "CC90:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1198, 246, "CC0549", "CC90:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1208, 270, "CC0550", "CC91:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1208, 302, "CC0552", "CC91:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1208, 334, "CC0554", "CC91:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1310, 246, "CC0557", "CC92:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1310, 278, "CC0559", "CC92:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1310, 310, "CC0561", "CC92:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1384, 270, "CC0564", "CC93:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1486, 246, "CC0565", "CC9B:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1384, 302, "CC0566", "CC93:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1486, 278, "CC0567", "CC9B:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1574, 214, "CC0570", "CC9B:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1574, 174, "CC0572", "CC9B:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1638, 214, "CC0574", "CC9B:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1638, 270, "CC0576", "CC9B:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1638, 302, "CC0578", "CC9B:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1640, 334, "CC0580", "CC9B:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1640, 398, "CC0584", "CC9C:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1640, 430, "CC0586", "CC9C:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 550,  206, "CC0679", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 608,  150, "CC0680", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 488,  206, "CC0689", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 422,  206, "CC0691", "CCC9:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 414,  150, "CC0692", "CCCA:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 350,  150, "CC0698", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 296,  206, "CC0699", "CCCA:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1054, 310, "CC0877", "CC93:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1000, 366, "CC0878", "CC94:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1118, 310, "CC0889", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpNorwich, 1128, 366, "CC0890", "CC94:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpNorwich, 1182, 310, "CC0891", "CC94:8", SignalDirection.LEFT);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpNorwich, 30,  266, "IPSWICH",   9);
        makeNavButton(bpNorwich, 790, 322, "CAMBRIDGE", 11);
        makeNavButton(bpNorwich, 820, 338, "(EN)",      11);
        makeNavButton(bpNorwich, 704, 162, "BURY ST",   11);
        makeNavButton(bpNorwich, 704, 178, "EDMUNDS",   11);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(bpNorwich, 1660, 442, "NORWICH", "NRW");

        smallStation(bpNorwich, 630,  226, "DISS",        "DIS");
        smallStation(bpNorwich, 525,  458, "CROMER",      "CMR");
        smallStation(bpNorwich, 426,  130, "ELMSWELL",    "ESW");
        smallStation(bpNorwich, 696,  450, "GUNTON",      "GNT");
        smallStation(bpNorwich, 1047, 538, "HOVETON &",   "HXM");
        smallStation(bpNorwich, 783,  538, "N WALSHAM",   "NWA");
        smallStation(bpNorwich, 618,  506, "ROUGHTON RD", "RNR");
        smallStation(bpNorwich, 1122, 538, "SALHOUSE",    "SAH");
        smallStation(bpNorwich, 500,  546, "SHERINGHAM",  "SHM");
        smallStation(bpNorwich, 554,  130, "THURSTON",    "TRS");
        smallStation(bpNorwich, 562,  602, "W RUNTON",    "WRN");
        smallStation(bpNorwich, 938,  450, "WORSTEAD",    "WRT");
        smallStation(bpNorwich, 1053, 546, "WROXHAM",     "HXM");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cambridge (EN)">
    private void initCambridgeEN()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        //<editor-fold defaultstate="collapsed" desc="NRW - ELY">
        Berths.getOrCreateBerth(bpCambridgeEN, 186,  138, "ENA808");
        Berths.getOrCreateBerth(bpCambridgeEN, 1658, 106, "ENC877").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 186,  106, "EN8019");
        Berths.getOrCreateBerth(bpCambridgeEN, 266,  138, "EN8024");
        Berths.getOrCreateBerth(bpCambridgeEN, 330,  138, "EN8034");
        Berths.getOrCreateBerth(bpCambridgeEN, 330,  106, "EN8041");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  138, "EN8044", "EN8063");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  106, "EN8061");
        Berths.getOrCreateBerth(bpCambridgeEN, 474,  106, "EN8064");
        Berths.getOrCreateBerth(bpCambridgeEN, 474,  138, "EN8066");
        Berths.getOrCreateBerth(bpCambridgeEN, 546,  74,  "EN8081", "EN8078");
        Berths.getOrCreateBerth(bpCambridgeEN, 474,  74,  "EN8069");
        Berths.getOrCreateBerth(bpCambridgeEN, 554,  106, "EN8083");
        Berths.getOrCreateBerth(bpCambridgeEN, 570,  138, "EN8086");
        Berths.getOrCreateBerth(bpCambridgeEN, 634,  138, "EN8103");
        Berths.getOrCreateBerth(bpCambridgeEN, 698,  170, "EN8104");
        Berths.getOrCreateBerth(bpCambridgeEN, 650,  106, "EN8109", "EN8084");
        Berths.getOrCreateBerth(bpCambridgeEN, 698,  138, "EN8110", "EN8113");
        Berths.getOrCreateBerth(bpCambridgeEN, 762,  138, "EN8118");
        Berths.getOrCreateBerth(bpCambridgeEN, 762,  106, "EN8129", "EN8114");
        Berths.getOrCreateBerth(bpCambridgeEN, 842,  138, "EN8134");
        Berths.getOrCreateBerth(bpCambridgeEN, 906,  138, "EN8148", "EN8153");
        Berths.getOrCreateBerth(bpCambridgeEN, 906,  106, "EN8149");
        Berths.getOrCreateBerth(bpCambridgeEN, 938,  74,  "EN8155");
        Berths.getOrCreateBerth(bpCambridgeEN, 1002, 106, "EN8158");
        Berths.getOrCreateBerth(bpCambridgeEN, 1050, 138, "EN8164");
        Berths.getOrCreateBerth(bpCambridgeEN, 1050, 106, "EN8167");
        Berths.getOrCreateBerth(bpCambridgeEN, 1114, 106, "EN8173");
        Berths.getOrCreateBerth(bpCambridgeEN, 1114, 138, "EN8176");
        Berths.getOrCreateBerth(bpCambridgeEN, 1178, 106, "EN8177");
        Berths.getOrCreateBerth(bpCambridgeEN, 1258, 138, "EN8178");
        Berths.getOrCreateBerth(bpCambridgeEN, 1258, 106, "EN8179");
        Berths.getOrCreateBerth(bpCambridgeEN, 1322, 106, "EN8195");
        Berths.getOrCreateBerth(bpCambridgeEN, 1402, 138, "EN8198", "EN8225");
        Berths.getOrCreateBerth(bpCambridgeEN, 1402, 106, "EN8219");
        Berths.getOrCreateBerth(bpCambridgeEN, 1434, 58,  "EN8221");
        Berths.getOrCreateBerth(bpCambridgeEN, 1418, 90,  "EN8223");
        Berths.getOrCreateBerth(bpCambridgeEN, 1498, 170, "EN8230");
        Berths.getOrCreateBerth(bpCambridgeEN, 1498, 138, "EN8234");
        Berths.getOrCreateBerth(bpCambridgeEN, 1498, 106, "EN8236");
        Berths.getOrCreateBerth(bpCambridgeEN, 1562, 106, "EN8237");
        Berths.getOrCreateBerth(bpCambridgeEN, 1562, 138, "EN8246");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="KLN - ELY">
        Berths.getOrCreateBerth(bpCambridgeEN, 1738, 330, "CAK22R").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 1738, 298, "CAK23R").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 1530, 346, "CAKL7A");
        Berths.getOrCreateBerth(bpCambridgeEN, 1674, 330, "CAK22B");
        Berths.getOrCreateBerth(bpCambridgeEN, 1674, 298, "CAK23B");
        Berths.getOrCreateBerth(bpCambridgeEN, 1610, 330, "CAK22A");
        Berths.getOrCreateBerth(bpCambridgeEN, 1610, 298, "CAK23A");
        Berths.getOrCreateBerth(bpCambridgeEN, 1594, 266, "CAKL24");
        Berths.getOrCreateBerth(bpCambridgeEN, 1530, 298, "CAKL25");
        Berths.getOrCreateBerth(bpCambridgeEN, 1418, 298, "CAKL35");
        Berths.getOrCreateBerth(bpCambridgeEN, 1306, 298, "CAKL36");
        Berths.getOrCreateBerth(bpCambridgeEN, 1466, 298, "CAKL43");
        Berths.getOrCreateBerth(bpCambridgeEN, 1354, 298, "CAKL44");
        Berths.getOrCreateBerth(bpCambridgeEN, 1242, 298, "CAKL45");
        Berths.getOrCreateBerth(bpCambridgeEN, 938,  298, "CADM02");
        Berths.getOrCreateBerth(bpCambridgeEN, 858,  298, "CADM03");
        Berths.getOrCreateBerth(bpCambridgeEN, 858,  266, "CADM11");
        Berths.getOrCreateBerth(bpCambridgeEN, 938,  266, "CADM26");
        Berths.getOrCreateBerth(bpCambridgeEN, 794,  266, "CADM27");
        Berths.getOrCreateBerth(bpCambridgeEN, 490,  234, "CALDAP").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 730,  266, "CALT04");
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  266, "CALT05");
        Berths.getOrCreateBerth(bpCambridgeEN, 602,  266, "CALT06");
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  234, "CALT22");
        Berths.getOrCreateBerth(bpCambridgeEN, 602,  234, "CALT24");
        Berths.getOrCreateBerth(bpCambridgeEN, 1002, 266, "CAMR01");
        Berths.getOrCreateBerth(bpCambridgeEN, 1066, 266, "CAMR02");
        Berths.getOrCreateBerth(bpCambridgeEN, 1130, 266, "CAMR03");
        Berths.getOrCreateBerth(bpCambridgeEN, 1066, 298, "CAMR04");
        Berths.getOrCreateBerth(bpCambridgeEN, 1130, 298, "CAMR05");
        Berths.getOrCreateBerth(bpCambridgeEN, 1194, 298, "CAMR06");
        Berths.getOrCreateBerth(bpCambridgeEN, 1194, 330, "CAMUAP").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="MNE - ELY">
        Berths.getOrCreateBerth(bpCambridgeEN, 314,  362, "CA0301");
        Berths.getOrCreateBerth(bpCambridgeEN, 346,  426, "CA0908");
        Berths.getOrCreateBerth(bpCambridgeEN, 346,  394, "CA0911");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  426, "CA0912");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  362, "CA0915");
        Berths.getOrCreateBerth(bpCambridgeEN, 490,  426, "CA0916");
        Berths.getOrCreateBerth(bpCambridgeEN, 490,  394, "CA0917");
        Berths.getOrCreateBerth(bpCambridgeEN, 554,  394, "CA0919");
        Berths.getOrCreateBerth(bpCambridgeEN, 554,  426, "CA0920");
        Berths.getOrCreateBerth(bpCambridgeEN, 618,  394, "CA0921");
        Berths.getOrCreateBerth(bpCambridgeEN, 618,  426, "CA0922");
        Berths.getOrCreateBerth(bpCambridgeEN, 682,  394, "CA0923");
        Berths.getOrCreateBerth(bpCambridgeEN, 682,  426, "CA0924");
        Berths.getOrCreateBerth(bpCambridgeEN, 810,  394, "CAM002");
        Berths.getOrCreateBerth(bpCambridgeEN, 874,  394, "CAM003");
        Berths.getOrCreateBerth(bpCambridgeEN, 810,  426, "CAM021");
        Berths.getOrCreateBerth(bpCambridgeEN, 746,  394, "CAM043");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Ely Notrh Jnc">
        Berths.getOrCreateBerth(bpCambridgeEN, 186,  474, "CA0296");
        Berths.getOrCreateBerth(bpCambridgeEN, 186,  442, "CA0298");
        Berths.getOrCreateBerth(bpCambridgeEN, 234,  442, "CA0303");
        Berths.getOrCreateBerth(bpCambridgeEN, 234,  474, "CA0305");
        Berths.getOrCreateBerth(bpCambridgeEN, 394,  506, "CA0306");
        Berths.getOrCreateBerth(bpCambridgeEN, 394,  474, "CA0307");
        Berths.getOrCreateBerth(bpCambridgeEN, 458,  506, "CA0308");
        Berths.getOrCreateBerth(bpCambridgeEN, 458,  474, "CA0311");
        Berths.getOrCreateBerth(bpCambridgeEN, 364,  586, "CA0800");
        Berths.getOrCreateBerth(bpCambridgeEN, 362,  554, "CA0801");
        Berths.getOrCreateBerth(bpCambridgeEN, 426,  586, "CA0802");
        Berths.getOrCreateBerth(bpCambridgeEN, 490,  554, "CA0803");
        Berths.getOrCreateBerth(bpCambridgeEN, 490,  586, "CA0804");
        Berths.getOrCreateBerth(bpCambridgeEN, 554,  586, "CA0808");
        Berths.getOrCreateBerth(bpCambridgeEN, 282,  506, "CA0774").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 554,  554, "CA8019");
        Berths.getOrCreateBerth(bpCambridgeEN, 570,  506, "CALAPP").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  554, "CALSCM").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  586, "CASAPP").hasBorder();
        //Berths.getOrCreateBerth(pnlCambridgeEN, 378,  330, "CAR4AB");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="BSE - CBG & ELY">
        Berths.getOrCreateBerth(bpCambridgeEN, 1066, 682, "CA0402");
        Berths.getOrCreateBerth(bpCambridgeEN, 794,  778, "CA0483");
        Berths.getOrCreateBerth(bpCambridgeEN, 858,  810, "CA0486");
        Berths.getOrCreateBerth(bpCambridgeEN, 922,  810, "CA0488");
        Berths.getOrCreateBerth(bpCambridgeEN, 922,  778, "CA0491");
        Berths.getOrCreateBerth(bpCambridgeEN, 986,  778, "CA0494");
        Berths.getOrCreateBerth(bpCambridgeEN, 1066, 778, "CA0495");
        Berths.getOrCreateBerth(bpCambridgeEN, 1130, 778, "CA0498");
        Berths.getOrCreateBerth(bpCambridgeEN, 474,  714, "CCB001");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  714, "CCB002");
      //Berths.getOrCreateBerth(pnlCambridgeEN, 314,  770, "CCB004");
        Berths.getOrCreateBerth(bpCambridgeEN, 314,  746, "CCB013");
        Berths.getOrCreateBerth(bpCambridgeEN, 250,  714, "CCB017");
        Berths.getOrCreateBerth(bpCambridgeEN, 346,  682, "CCB019");
      //Berths.getOrCreateBerth(pnlCambridgeEN, 410,  650, "CCB031");
        Berths.getOrCreateBerth(bpCambridgeEN, 346,  650, "CCB032", "CCB033");
      //Berths.getOrCreateBerth(pnlCambridgeEN, 282,  634, "CCB034");
        Berths.getOrCreateBerth(bpCambridgeEN, 410,  682, "CCB048"/*, "CCB045"*/);
        Berths.getOrCreateBerth(bpCambridgeEN, 250,  682, "CCB049");
        Berths.getOrCreateBerth(bpCambridgeEN, 186,  682, "CCB050");
        Berths.getOrCreateBerth(bpCambridgeEN, 602,  682, "CCB302");
        Berths.getOrCreateBerth(bpCambridgeEN, 602,  714, "CCB303");
        Berths.getOrCreateBerth(bpCambridgeEN, 538,  682, "CCB306");
        Berths.getOrCreateBerth(bpCambridgeEN, 538,  714, "CCB307");
        Berths.getOrCreateBerth(bpCambridgeEN, 474,  682, "CCB310");
        Berths.getOrCreateBerth(bpCambridgeEN, 778,  682, "CACM02");
        Berths.getOrCreateBerth(bpCambridgeEN, 794,  810, "CACM04");
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  714, "CACM05");
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  682, "CACM14");
        Berths.getOrCreateBerth(bpCambridgeEN, 666,  650, "CACAPP").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeEN, 922,  682, "CAD002");
        Berths.getOrCreateBerth(bpCambridgeEN, 986,  682, "CAD003");
        Berths.getOrCreateBerth(bpCambridgeEN, 858,  682, "CAD004");
        Berths.getOrCreateBerth(bpCambridgeEN, 922,  714, "CAD005");
        Berths.getOrCreateBerth(bpCambridgeEN, 1130, 682, "CAX200");
        //</editor-fold>
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        //<editor-fold defaultstate="collapsed" desc="NRW - ELY">
        Signals.getOrCreateSignal(bpCambridgeEN, 238,  126, "EN8019", "EN05:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 264,  134, "EN8024", "EN05:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 328,  158, "EN8034", "EN05:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 382,  126, "EN8041", "EN06:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 408,  134, "EN8044", "EN06:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 462,  102, "EN8061", "EN06:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 470,  158, "EN8066", "EN07:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 606,  102, "EN8083", "EN07:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 568,  158, "EN8086", "EN07:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 702,  102, "EN8109", "EN08:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 696,  158, "EN8110", "EN08:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 760,  158, "EN8118", "EN08:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 814,  126, "EN8129", "EN09:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 840,  134, "EN8134", "EN09:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 904,  158, "EN8148", "EN14:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 958,  102, "EN8149", "EN14:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1048, 158, "EN8164", "EN14:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1102, 102, "EN8167", "EN14:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1166, 102, "EN8173", "EN15:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1112, 158, "EN8176", "EN15:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1230, 126, "EN8177", "EN15:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1256, 134, "EN8178", "EN15:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1310, 102, "EN8179", "EN15:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1374, 126, "EN8195", "EN15:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1400, 134, "EN8198", "EN15:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1454, 126, "EN8219", "EN15:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1496, 158, "EN8234", "EN16:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1496, 102, "EN8236", "EN16:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1614, 102, "EN8237", "EN16:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 1560, 158, "EN8246", "EN16:6", SignalDirection.RIGHT);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="BSE - CBG & ELY">
        Signals.getOrCreateSignal(bpCambridgeEN, 472, 734, "CCB001", "CCD0:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 408, 734, "CCB002", "CCD1:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 246, 734, "CCB017", "CCD0:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 398, 678, "CCB019", "CCD1:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 462, 678, "CCB048", "CCD0:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 304, 678, "CCB049", "CCD0:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 238, 678, "CCB050", "CCD1:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 598, 734, "CCB303", "CCD0:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 536, 734, "CCB307", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpCambridgeEN, 526, 678, "CCB310", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 590, 678, "CCB306", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpCambridgeEN, 654, 678, "CCB302", "CC",     SignalDirection.LEFT);
        //</editor-fold>
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpCambridgeEN, 1714, 122, "NORWICH", 12);

        makeNavButton(bpCambridgeEN, 38, 450, "CAMBRIDGE", 10);
        makeNavButton(bpCambridgeEN, 68, 466, "(CA)",      10);

        makeNavButton(bpCambridgeEN, 50,   698, "THURSTON",  12);
        makeNavButton(bpCambridgeEN, 1210, 682, "CAMBRIDGE", 10);
        makeNavButton(bpCambridgeEN, 1226, 778, "ELY",       12);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpCambridgeEN, 1206, 82,  "ATTLEBOROUGH", "ATL");
        smallStation(bpCambridgeEN, 477,  170, "BRANDON",      "BND");
        smallStation(bpCambridgeEN, 991,  170, "ECCLES RD",    "ECS");
        smallStation(bpCambridgeEN, 794,  170, "HARLING RD",   "HRD");
        smallStation(bpCambridgeEN, 364,  170, "LAKENHEATH",   "LAK");
        smallStation(bpCambridgeEN, 214,  170, "SHIPPEA HILL", "SPP");
        smallStation(bpCambridgeEN, 1353, 82,  "SPOONER ROW",  "SPN");
        smallStation(bpCambridgeEN, 698,  82,  "THETFORD",     "TTF");
        smallStation(bpCambridgeEN, 1495, 82,  "WYMONDHAM",    "WMD");

        smallStation(bpCambridgeEN, 852,  330, "DOWNHAM MKT", "DOW");
        smallStation(bpCambridgeEN, 1640, 354, "KINGS LYNN",  "KLN");
        smallStation(bpCambridgeEN, 660,  298, "LITTLEPORT",  "LTP");
        smallStation(bpCambridgeEN, 1121, 242, "WATLINGTON",  "WTG");

        smallStation(bpCambridgeEN, 875,  372, "MANEA", "MNE");

        smallStation(bpCambridgeEN, 229,  658, "BURY ST EDMUNDS", "BSE");
        smallStation(bpCambridgeEN, 916,  658, "DULLINGHAM",      "DUL");
        smallStation(bpCambridgeEN, 597,  658, "KENNETT",         "KNE");
        smallStation(bpCambridgeEN, 815,  710, "NEWMARKET",       "NMK");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cambridge (CA)">
    private void initCambridgeCA()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpCambridgeCA, 272,  154, "CA0211");
        Berths.getOrCreateBerth(bpCambridgeCA, 272,  186, "CA0212");
        Berths.getOrCreateBerth(bpCambridgeCA, 352,  186, "CA0220");
        Berths.getOrCreateBerth(bpCambridgeCA, 352,  122, "CA0223");
        Berths.getOrCreateBerth(bpCambridgeCA, 352,  154, "CA0225");
        Berths.getOrCreateBerth(bpCambridgeCA, 432,  186, "CA0228");
        Berths.getOrCreateBerth(bpCambridgeCA, 432,  154, "CA0229");
        Berths.getOrCreateBerth(bpCambridgeCA, 496,  186, "CA0230");
        Berths.getOrCreateBerth(bpCambridgeCA, 496,  154, "CA0231");
        Berths.getOrCreateBerth(bpCambridgeCA, 560,  186, "CA0232");
        Berths.getOrCreateBerth(bpCambridgeCA, 624,  154, "CA0235");
        Berths.getOrCreateBerth(bpCambridgeCA, 688,  186, "CA0236");
        Berths.getOrCreateBerth(bpCambridgeCA, 688,  154, "CA0237");
        Berths.getOrCreateBerth(bpCambridgeCA, 752,  186, "CA0238");
        Berths.getOrCreateBerth(bpCambridgeCA, 752,  154, "CA0239");
        Berths.getOrCreateBerth(bpCambridgeCA, 816,  186, "CA0240");
        Berths.getOrCreateBerth(bpCambridgeCA, 816,  154, "CA0241");
        Berths.getOrCreateBerth(bpCambridgeCA, 880,  186, "CA0244");
        Berths.getOrCreateBerth(bpCambridgeCA, 880,  154, "CA0245");
        Berths.getOrCreateBerth(bpCambridgeCA, 944,  186, "CA0246");
        Berths.getOrCreateBerth(bpCambridgeCA, 944,  154, "CA0247");
        Berths.getOrCreateBerth(bpCambridgeCA, 1008, 186, "CA0248");
        Berths.getOrCreateBerth(bpCambridgeCA, 1008, 154, "CA0249");
        Berths.getOrCreateBerth(bpCambridgeCA, 1072, 186, "CA0250");
        Berths.getOrCreateBerth(bpCambridgeCA, 1072, 154, "CA0251");
        Berths.getOrCreateBerth(bpCambridgeCA, 1136, 186, "CA0252");
        Berths.getOrCreateBerth(bpCambridgeCA, 1136, 154, "CA0253");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 186, "CA0254");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 122, "CA0255");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 154, "CA0257", "CA0256");
        Berths.getOrCreateBerth(bpCambridgeCA, 1216, 250, "CA0259");
        Berths.getOrCreateBerth(bpCambridgeCA, 1280, 218, "CA0262");
        Berths.getOrCreateBerth(bpCambridgeCA, 1344, 250, "CA0270");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 186, "CA0271");
        Berths.getOrCreateBerth(bpCambridgeCA, 1424, 234, "CA0272");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 122, "CA0273");
        Berths.getOrCreateBerth(bpCambridgeCA, 1440, 218, "CA0274");
        Berths.getOrCreateBerth(bpCambridgeCA, 1440, 186, "CA0276");
        Berths.getOrCreateBerth(bpCambridgeCA, 1440, 154, "CA0278");
        Berths.getOrCreateBerth(bpCambridgeCA, 1504, 154, "CA0281");
        Berths.getOrCreateBerth(bpCambridgeCA, 1504, 186, "CA0283");
        Berths.getOrCreateBerth(bpCambridgeCA, 1504, 218, "CA0285");
        Berths.getOrCreateBerth(bpCambridgeCA, 1520, 250, "CA0287");
        Berths.getOrCreateBerth(bpCambridgeCA, 1600, 186, "CA0288");
        Berths.getOrCreateBerth(bpCambridgeCA, 1600, 154, "CA0290");
        Berths.getOrCreateBerth(bpCambridgeCA, 1648, 154, "CA0293");
        Berths.getOrCreateBerth(bpCambridgeCA, 1648, 186, "CA0295");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 90,  "CA0765");
        Berths.getOrCreateBerth(bpCambridgeCA, 1280, 122, "CA1273");
        Berths.getOrCreateBerth(bpCambridgeCA, 1280, 90,  "CA1765");

        Berths.getOrCreateBerth(bpCambridgeCA, 160,  474, "CA0078");
        Berths.getOrCreateBerth(bpCambridgeCA, 224,  474, "CA0080");
        Berths.getOrCreateBerth(bpCambridgeCA, 288,  474, "CA0082");
        Berths.getOrCreateBerth(bpCambridgeCA, 160,  442, "CA0083");
        Berths.getOrCreateBerth(bpCambridgeCA, 448,  474, "CA0084");
        Berths.getOrCreateBerth(bpCambridgeCA, 368,  410, "CA0085");
        Berths.getOrCreateBerth(bpCambridgeCA, 368,  442, "CA0087");
        Berths.getOrCreateBerth(bpCambridgeCA, 512,  474, "CA0090");
        Berths.getOrCreateBerth(bpCambridgeCA, 448,  442, "CA0091");
        Berths.getOrCreateBerth(bpCambridgeCA, 576,  474, "CA0092");
        Berths.getOrCreateBerth(bpCambridgeCA, 512,  442, "CA0093");
        Berths.getOrCreateBerth(bpCambridgeCA, 640,  474, "CA0094");
        Berths.getOrCreateBerth(bpCambridgeCA, 576,  442, "CA0095");
        Berths.getOrCreateBerth(bpCambridgeCA, 704,  474, "CA0096");
        Berths.getOrCreateBerth(bpCambridgeCA, 640,  442, "CA0097");
        Berths.getOrCreateBerth(bpCambridgeCA, 704,  442, "CA0099");
        Berths.getOrCreateBerth(bpCambridgeCA, 768,  474, "CA0100");
        Berths.getOrCreateBerth(bpCambridgeCA, 224,  314, "CA0103");
        Berths.getOrCreateBerth(bpCambridgeCA, 224,  346, "CA0104");
        Berths.getOrCreateBerth(bpCambridgeCA, 288,  314, "CA0105");
        Berths.getOrCreateBerth(bpCambridgeCA, 352,  346, "CA0106");
        Berths.getOrCreateBerth(bpCambridgeCA, 352,  314, "CA0107");
        Berths.getOrCreateBerth(bpCambridgeCA, 416,  346, "CA0108");
        Berths.getOrCreateBerth(bpCambridgeCA, 416,  314, "CA0109");
        Berths.getOrCreateBerth(bpCambridgeCA, 480,  346, "CA0110");
        Berths.getOrCreateBerth(bpCambridgeCA, 480,  314, "CA0113");
        Berths.getOrCreateBerth(bpCambridgeCA, 576,  346, "CA0114");
        Berths.getOrCreateBerth(bpCambridgeCA, 576,  314, "CA0115");
        Berths.getOrCreateBerth(bpCambridgeCA, 640,  346, "CA0116");
        Berths.getOrCreateBerth(bpCambridgeCA, 640,  314, "CA0117");
        Berths.getOrCreateBerth(bpCambridgeCA, 704,  346, "CA0118");
        Berths.getOrCreateBerth(bpCambridgeCA, 704,  314, "CA0119");
        Berths.getOrCreateBerth(bpCambridgeCA, 768,  346, "CA0120");
        Berths.getOrCreateBerth(bpCambridgeCA, 768,  314, "CA0123");
        Berths.getOrCreateBerth(bpCambridgeCA, 864,  474, "CA0140");
        Berths.getOrCreateBerth(bpCambridgeCA, 864,  442, "CA0141");
        Berths.getOrCreateBerth(bpCambridgeCA, 928,  474, "CA0142");
        Berths.getOrCreateBerth(bpCambridgeCA, 928,  442, "CA0143");
        Berths.getOrCreateBerth(bpCambridgeCA, 992,  474, "CA0144");
        Berths.getOrCreateBerth(bpCambridgeCA, 992,  442, "CA0145");
        Berths.getOrCreateBerth(bpCambridgeCA, 1056, 410, "CA0147");
        Berths.getOrCreateBerth(bpCambridgeCA, 1056, 474, "CA0148");
        Berths.getOrCreateBerth(bpCambridgeCA, 1056, 442, "CA0149");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 378, "CA0150");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 410, "CA0152");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 442, "CA0154");
        Berths.getOrCreateBerth(bpCambridgeCA, 1200, 474, "CA0156");
        Berths.getOrCreateBerth(bpCambridgeCA, 1232, 506, "CA0158");
        Berths.getOrCreateBerth(bpCambridgeCA, 1232, 538, "CA0160");
        Berths.getOrCreateBerth(bpCambridgeCA, 1251, 442, "CA0161");
        Berths.getOrCreateBerth(bpCambridgeCA, 1341, 442, "CA0162");
        Berths.getOrCreateBerth(bpCambridgeCA, 1251, 474, "CA0163");
        Berths.getOrCreateBerth(bpCambridgeCA, 1341, 474, "CA0164");
        Berths.getOrCreateBerth(bpCambridgeCA, 1392, 378, "CA0171");
        Berths.getOrCreateBerth(bpCambridgeCA, 1392, 410, "CA0173");
        Berths.getOrCreateBerth(bpCambridgeCA, 1392, 442, "CA0175");
        Berths.getOrCreateBerth(bpCambridgeCA, 1392, 474, "CA0177");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 506, "CA0179");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 538, "CA0181");
        Berths.getOrCreateBerth(bpCambridgeCA, 1520, 474, "CA0180");
        Berths.getOrCreateBerth(bpCambridgeCA, 1600, 490, "CA0188");
        Berths.getOrCreateBerth(bpCambridgeCA, 1632, 474, "CA0190");
        Berths.getOrCreateBerth(bpCambridgeCA, 1632, 410, "CA0193");
        Berths.getOrCreateBerth(bpCambridgeCA, 1632, 442, "CA0195");
        Berths.getOrCreateBerth(bpCambridgeCA, 1680, 490, "CA0200");
        Berths.getOrCreateBerth(bpCambridgeCA, 1712, 474, "CA0210");
        Berths.getOrCreateBerth(bpCambridgeCA, 1736, 506, "CA0401").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 1112, 370, "CA0653");
        Berths.getOrCreateBerth(bpCambridgeCA, 1112, 394, "CA0655");
        Berths.getOrCreateBerth(bpCambridgeCA, 1680, 522, "CADAPP").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 1264, 378, "CAX150").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 1264, 410, "CAX152").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 1328, 378, "CAX171").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 1328, 410, "CAX173").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 144,  314, "CA0981").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 144,  346, "CA0986").hasBorder();

        Berths.getOrCreateBerth(bpCambridgeCA, 128,  666, "CA0019");
        Berths.getOrCreateBerth(bpCambridgeCA, 192,  698, "CA0020");
        Berths.getOrCreateBerth(bpCambridgeCA, 192,  666, "CA0021");
        Berths.getOrCreateBerth(bpCambridgeCA, 272,  698, "CA0022");
        Berths.getOrCreateBerth(bpCambridgeCA, 336,  666, "CA0023");
        Berths.getOrCreateBerth(bpCambridgeCA, 336,  698, "CA0024");
        Berths.getOrCreateBerth(bpCambridgeCA, 400,  698, "CA0026");
        Berths.getOrCreateBerth(bpCambridgeCA, 400,  666, "CA0027");
        Berths.getOrCreateBerth(bpCambridgeCA, 464,  698, "CA0028");
        Berths.getOrCreateBerth(bpCambridgeCA, 464,  666, "CA0029");
        Berths.getOrCreateBerth(bpCambridgeCA, 528,  698, "CA0030");
        Berths.getOrCreateBerth(bpCambridgeCA, 528,  666, "CA0031");
        Berths.getOrCreateBerth(bpCambridgeCA, 592,  698, "CA0032");
        Berths.getOrCreateBerth(bpCambridgeCA, 592,  666, "CA0033");
        Berths.getOrCreateBerth(bpCambridgeCA, 656,  698, "CA0034");
        Berths.getOrCreateBerth(bpCambridgeCA, 656,  666, "CA0037");
        Berths.getOrCreateBerth(bpCambridgeCA, 720,  698, "CA0038");
        Berths.getOrCreateBerth(bpCambridgeCA, 720,  666, "CA0039");
        Berths.getOrCreateBerth(bpCambridgeCA, 784,  698, "CA0040");
        Berths.getOrCreateBerth(bpCambridgeCA, 784,  666, "CA0041");
        Berths.getOrCreateBerth(bpCambridgeCA, 848,  698, "CA0042");
        Berths.getOrCreateBerth(bpCambridgeCA, 848,  666, "CA0043");
        Berths.getOrCreateBerth(bpCambridgeCA, 912,  698, "CA0044", "CA0047");
        Berths.getOrCreateBerth(bpCambridgeCA, 912,  666, "CA0045");
        Berths.getOrCreateBerth(bpCambridgeCA, 976,  698, "CA0050");
        Berths.getOrCreateBerth(bpCambridgeCA, 976,  666, "CA0051");
        Berths.getOrCreateBerth(bpCambridgeCA, 1040, 698, "CA0052");
        Berths.getOrCreateBerth(bpCambridgeCA, 1040, 666, "CA0053");
        Berths.getOrCreateBerth(bpCambridgeCA, 1104, 698, "CA0054");
        Berths.getOrCreateBerth(bpCambridgeCA, 1104, 666, "CA0055");
        Berths.getOrCreateBerth(bpCambridgeCA, 1168, 698, "CA0056");
        Berths.getOrCreateBerth(bpCambridgeCA, 1168, 666, "CA0057");
        Berths.getOrCreateBerth(bpCambridgeCA, 1232, 698, "CA0058");
        Berths.getOrCreateBerth(bpCambridgeCA, 1296, 666, "CA0059");
        Berths.getOrCreateBerth(bpCambridgeCA, 1296, 698, "CA0060");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 666, "CA0061");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 698, "CA0062");
        Berths.getOrCreateBerth(bpCambridgeCA, 1360, 730, "CA0064");
        Berths.getOrCreateBerth(bpCambridgeCA, 1520, 698, "CA0066");
        Berths.getOrCreateBerth(bpCambridgeCA, 1456, 666, "CA0067");
        Berths.getOrCreateBerth(bpCambridgeCA, 1584, 698, "CA0072");
        Berths.getOrCreateBerth(bpCambridgeCA, 1520, 666, "CA0073");
        Berths.getOrCreateBerth(bpCambridgeCA, 1648, 698, "CA0074");
        Berths.getOrCreateBerth(bpCambridgeCA, 1584, 666, "CA0075");
        Berths.getOrCreateBerth(bpCambridgeCA, 1712, 698, "CA0076");
        Berths.getOrCreateBerth(bpCambridgeCA, 1648, 666, "CA0077");
        Berths.getOrCreateBerth(bpCambridgeCA, 1712, 666, "CA0079");
        Berths.getOrCreateBerth(bpCambridgeCA, 32,   666, "CAL187").hasBorder();
        Berths.getOrCreateBerth(bpCambridgeCA, 32,   698, "CAL188").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpCambridgeCA, 1748, 162, "ELY",   11);
        makeNavButton(bpCambridgeCA, 1736, 178, "N JNC", 11);

        makeNavButton(bpCambridgeCA, 20, 642, "HARLOW", 6);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(bpCambridgeCA, 1266, 354, "CAMBRIDGE", "CBG");
        largeStation(bpCambridgeCA, 1478, 122, "ELY",       "ELY");

        smallStation(bpCambridgeCA, 906,  730, "AUDLEY END",   "AUD");
        smallStation(bpCambridgeCA, 1447, 738, "CHESTERFORD",  "GRC"); //GREAT
        smallStation(bpCambridgeCA, 192,  730, "ELSENHAM",     "ESM");
        smallStation(bpCambridgeCA, 723,  738, "(ESSEX)",      "NWE"); //NEWPORT
        smallStation(bpCambridgeCA, 582,  378, "FOXTON",       "FXN");
        smallStation(bpCambridgeCA, 1465, 730, "GREAT",        "GRC"); //CHESTERFORD
        smallStation(bpCambridgeCA, 352,  378, "MELDRETH",     "MEL");
        smallStation(bpCambridgeCA, 723,  730, "NEWPORT",      "NWE"); //(ESSEX)
        smallStation(bpCambridgeCA, 227,  514, "PARKWAY",      "WLF"); //WHITTLESFORD
        smallStation(bpCambridgeCA, 712,  506, "SHELFORD",     "SED");
        smallStation(bpCambridgeCA, 480,  378, "SHEPRETH",     "STH");
        smallStation(bpCambridgeCA, 586,  218, "WATERBEACH",   "WBC");
        smallStation(bpCambridgeCA, 212,  506, "WHITTLESFORD", "WLF"); //PARKWAY
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Ipswich">
    private void initIpswich()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpIpswich, 90,   306, "CC0290");
        Berths.getOrCreateBerth(bpIpswich, 122,  250, "CC0295", "CC0292");
        Berths.getOrCreateBerth(bpIpswich, 122,  282, "CC0294", "CC0297");
        Berths.getOrCreateBerth(bpIpswich, 202,  218, "CC0302");
        Berths.getOrCreateBerth(bpIpswich, 202,  250, "CC0304");
        Berths.getOrCreateBerth(bpIpswich, 202,  282, "CC0306");
        Berths.getOrCreateBerth(bpIpswich, 202,  314, "CC0308");
        Berths.getOrCreateBerth(bpIpswich, 266,  218, "CC0311");
        Berths.getOrCreateBerth(bpIpswich, 266,  250, "CC0313");
        Berths.getOrCreateBerth(bpIpswich, 266,  282, "CC0315");
        Berths.getOrCreateBerth(bpIpswich, 266,  314, "CC0317");
        Berths.getOrCreateBerth(bpIpswich, 266,  346, "CC0319");
        Berths.getOrCreateBerth(bpIpswich, 394,  346, "CC0322");
        Berths.getOrCreateBerth(bpIpswich, 426,  282, "CC0326");
        Berths.getOrCreateBerth(bpIpswich, 410,  314, "CC0328");
        Berths.getOrCreateBerth(bpIpswich, 474,  250, "CC0331");
        Berths.getOrCreateBerth(bpIpswich, 474,  282, "CC0333");
        Berths.getOrCreateBerth(bpIpswich, 458,  314, "CC0335");
        Berths.getOrCreateBerth(bpIpswich, 458,  346, "CC0337");
        Berths.getOrCreateBerth(bpIpswich, 490,  362, "CC0341");
        Berths.getOrCreateBerth(bpIpswich, 554,  378, "CC0343");
        Berths.getOrCreateBerth(bpIpswich, 682,  410, "CC0348");
        Berths.getOrCreateBerth(bpIpswich, 714,  314, "CC0351");
        Berths.getOrCreateBerth(bpIpswich, 746,  346, "CC0352");
        Berths.getOrCreateBerth(bpIpswich, 682,  282, "CC0346");
        Berths.getOrCreateBerth(bpIpswich, 778,  282, "CC0366");
        Berths.getOrCreateBerth(bpIpswich, 682,  250, "CC0367");
        Berths.getOrCreateBerth(bpIpswich, 778,  250, "CC0369");
        Berths.getOrCreateBerth(bpIpswich, 842,  282, "CC0370");
        Berths.getOrCreateBerth(bpIpswich, 842,  250, "CC0371");
        Berths.getOrCreateBerth(bpIpswich, 922,  282, "CC0374");
        Berths.getOrCreateBerth(bpIpswich, 922,  250, "CC0375");
        Berths.getOrCreateBerth(bpIpswich, 922,  218, "CC0377", "CC1372");
        Berths.getOrCreateBerth(bpIpswich, 1018, 282, "CC0378");
        Berths.getOrCreateBerth(bpIpswich, 1018, 250, "CC0379");
        Berths.getOrCreateBerth(bpIpswich, 1082, 282, "CC0380");
        Berths.getOrCreateBerth(bpIpswich, 1082, 250, "CC0381");
        Berths.getOrCreateBerth(bpIpswich, 1146, 250, "CC0383");
        Berths.getOrCreateBerth(bpIpswich, 1210, 282, "CC0384");
        Berths.getOrCreateBerth(bpIpswich, 1210, 250, "CC0385");
        Berths.getOrCreateBerth(bpIpswich, 1306, 218, "CC0386");
        Berths.getOrCreateBerth(bpIpswich, 1306, 282, "CC0388");
        Berths.getOrCreateBerth(bpIpswich, 1370, 218, "CC0389");
        Berths.getOrCreateBerth(bpIpswich, 1434, 250, "CC0391", "CC0390");
        Berths.getOrCreateBerth(bpIpswich, 778,  410, "CC0400");
        Berths.getOrCreateBerth(bpIpswich, 682,  378, "CC0401");
        Berths.getOrCreateBerth(bpIpswich, 778,  378, "CC0403");
        Berths.getOrCreateBerth(bpIpswich, 842,  410, "CC0404");
        Berths.getOrCreateBerth(bpIpswich, 842,  378, "CC0405");
        Berths.getOrCreateBerth(bpIpswich, 906,  378, "CC0611");
        Berths.getOrCreateBerth(bpIpswich, 970,  410, "CC0612");
        Berths.getOrCreateBerth(bpIpswich, 1082, 474, "CC0613");
        Berths.getOrCreateBerth(bpIpswich, 1050, 410, "CC0614");
        Berths.getOrCreateBerth(bpIpswich, 1146, 474, "CC0615");
        Berths.getOrCreateBerth(bpIpswich, 1034, 474, "CC0616");
        Berths.getOrCreateBerth(bpIpswich, 1258, 474, "CC0617");
        Berths.getOrCreateBerth(bpIpswich, 1146, 506, "CC0618");
        Berths.getOrCreateBerth(bpIpswich, 1370, 474, "CC0619");
        Berths.getOrCreateBerth(bpIpswich, 1210, 474, "CC0620");
        Berths.getOrCreateBerth(bpIpswich, 1482, 474, "CC0621");
        Berths.getOrCreateBerth(bpIpswich, 1322, 474, "CC0622");
        Berths.getOrCreateBerth(bpIpswich, 1434, 474, "CC0624");
        Berths.getOrCreateBerth(bpIpswich, 1546, 474, "CC0626");
        Berths.getOrCreateBerth(bpIpswich, 1402, 490, "CC0632");
        Berths.getOrCreateBerth(bpIpswich, 1402, 522, "CC0634");
        Berths.getOrCreateBerth(bpIpswich, 1594, 506, "CC0641");
        Berths.getOrCreateBerth(bpIpswich, 1546, 506, "CC0642");
        Berths.getOrCreateBerth(bpIpswich, 1658, 506, "CC0644");
        Berths.getOrCreateBerth(bpIpswich, 282,  154, "CC0797");
        Berths.getOrCreateBerth(bpIpswich, 282,  186, "CC0799");
        Berths.getOrCreateBerth(bpIpswich, 210,  186, "CCLOCS").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 410,  362, "CC0812");
        Berths.getOrCreateBerth(bpIpswich, 410,  378, "CC0814");
        Berths.getOrCreateBerth(bpIpswich, 410,  394, "CC0816");
        Berths.getOrCreateBerth(bpIpswich, 410,  410, "CC0818");
        Berths.getOrCreateBerth(bpIpswich, 490,  378, "CC0827");
        Berths.getOrCreateBerth(bpIpswich, 490,  394, "CC0829");
        Berths.getOrCreateBerth(bpIpswich, 618,  410, "CC0834");
        Berths.getOrCreateBerth(bpIpswich, 474,  410, "CC0835");
        Berths.getOrCreateBerth(bpIpswich, 474,  426, "CC0837");
        Berths.getOrCreateBerth(bpIpswich, 1706, 506, "CC0841");
        Berths.getOrCreateBerth(bpIpswich, 210,  346, "CC1319");
        Berths.getOrCreateBerth(bpIpswich, 1050, 378, "CC2003");
        Berths.getOrCreateBerth(bpIpswich, 1178, 378, "CC2012");
        Berths.getOrCreateBerth(bpIpswich, 1044, 306, "CCBARS").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 1498, 554, "CCFDAP");
        Berths.getOrCreateBerth(bpIpswich, 1578, 554, "CCFDLS").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 1610, 474, "CCFTSN");
        Berths.getOrCreateBerth(bpIpswich, 410,  426, "CCIPYA");
        Berths.getOrCreateBerth(bpIpswich, 1434, 554, "CCNQ02");
        Berths.getOrCreateBerth(bpIpswich, 1434, 538, "CCNQ04");
        Berths.getOrCreateBerth(bpIpswich, 90,   346, "CCR290").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 1370, 194, "CCR388").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 1770, 538, "CCSDCR");
        Berths.getOrCreateBerth(bpIpswich, 1770, 506, "CCSDG1");
        Berths.getOrCreateBerth(bpIpswich, 1770, 522, "CCSDG2");
        Berths.getOrCreateBerth(bpIpswich, 1114, 410, "CCULAP");

        Berths.getOrCreateBerth(bpIpswich, 442,  634, "SX2003");
        Berths.getOrCreateBerth(bpIpswich, 506,  666, "SX2004");
        Berths.getOrCreateBerth(bpIpswich, 506,  634, "SX2006");
        Berths.getOrCreateBerth(bpIpswich, 554,  634, "SX2011");
        Berths.getOrCreateBerth(bpIpswich, 618,  634, "SX2012");
        Berths.getOrCreateBerth(bpIpswich, 762,  666, "SX2028");
        Berths.getOrCreateBerth(bpIpswich, 698,  634, "SX2029");
        Berths.getOrCreateBerth(bpIpswich, 762,  634, "SX2032");
        Berths.getOrCreateBerth(bpIpswich, 810,  634, "SX2033");
        Berths.getOrCreateBerth(bpIpswich, 874,  666, "SX2035");
        Berths.getOrCreateBerth(bpIpswich, 938,  666, "SX2042");
        Berths.getOrCreateBerth(bpIpswich, 1034, 634, "SX2045");
        Berths.getOrCreateBerth(bpIpswich, 1034, 666, "SX2046");
        Berths.getOrCreateBerth(bpIpswich, 1098, 666, "SX2052");
        Berths.getOrCreateBerth(bpIpswich, 1178, 666, "SX2055");
        Berths.getOrCreateBerth(bpIpswich, 1242, 698, "SX2056");
        Berths.getOrCreateBerth(bpIpswich, 1290, 666, "SX2057");
        Berths.getOrCreateBerth(bpIpswich, 1242, 666, "SX2058");
        Berths.getOrCreateBerth(bpIpswich, 1354, 666, "SX2060");
        Berths.getOrCreateBerth(bpIpswich, 1482, 666, "SX2066");
        Berths.getOrCreateBerth(bpIpswich, 1658, 666, "SXB21R");
        Berths.getOrCreateBerth(bpIpswich, 234,  634, "SXC405");
        Berths.getOrCreateBerth(bpIpswich, 298,  634, "SXC611");
        Berths.getOrCreateBerth(bpIpswich, 362,  666, "SXC612");
        Berths.getOrCreateBerth(bpIpswich, 442,  666, "SXC614");
        Berths.getOrCreateBerth(bpIpswich, 1754, 666, "SXLSOB").hasBorder();
        Berths.getOrCreateBerth(bpIpswich, 1418, 666, "SXOB18");
        Berths.getOrCreateBerth(bpIpswich, 1530, 666, "SXOB19");
        Berths.getOrCreateBerth(bpIpswich, 1594, 666, "SXOB21");
        Berths.getOrCreateBerth(bpIpswich, 1594, 634, "SXOSTO").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        //<editor-fold defaultstate="collapsed" desc="CC">
        Signals.getOrCreateSignal(bpIpswich, 198,  238, "CC0302", "CC70:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 198,  246, "CC0304", "CC71:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 200,  302, "CC0306", "CC71:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 198,  334, "CC0308", "CC71:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 320,  238, "CC0311", "CC71:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 320,  246, "CC0313", "CC71:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 318,  278, "CC0315", "CC72:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 320,  334, "CC0317", "CC72:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 318,  366, "CC0319", "CC73:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 392,  342, "CC0322", "CC73:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 424,  302, "CC0326", "CC74:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 408,  334, "CC0328", "CC74:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 526,  246, "CC0331", "CC81:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 526,  278, "CC0333", "CC81:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 510,  310, "CC0335", "CC81:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 510,  342, "CC0337", "CC81:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 542,  358, "CC0341", "CC82:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 606,  374, "CC0343", "CC85:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 680,  302, "CC0346", "CC82:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 680,  430, "CC0348", "CC82:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 726,  304, "CC0351", "CC84:3", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpIpswich, 782,  374, "CC0352", "CC84:5", SignalDirection.UP);
        Signals.getOrCreateSignal(bpIpswich, 776,  302, "CC0366", "CC84:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 734,  246, "CC0367", "CC85:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 830,  246, "CC0369", "CC4C:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 840,  302, "CC0370", "CC84:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 894,  246, "CC0371", "CC4D:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 920,  302, "CC0374", "CC4D:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 974,  246, "CC0375", "CC4D:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1016, 302, "CC0378", "CC4E:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1070, 246, "CC0379", "CC4F:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1080, 302, "CC0380", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1134, 246, "CC0381", "CCC4:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1200, 246, "CC0383", "CCC4:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1208, 302, "CC0384", "CCC5:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1262, 246, "CC0385", "CCC5:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1304, 214, "CC0386", "CCC5:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1304, 302, "CC0388", "CCC5:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1422, 214, "CC0389", "CCC6:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1488, 246, "CC0391", "CCC6:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 776,  430, "CC0400", "CC83:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 734,  374, "CC0401", "CC83:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 830,  374, "CC0403", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 840,  430, "CC0404", "CC69:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 894,  374, "CC0405", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 958,  374, "CC0611", "CC66:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 966,  430, "CC0612", "CC66:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1134, 470, "CC0613", "CC66:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1048, 430, "CC0614", "CC66:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1200, 470, "CC0615", "CC65:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1032, 494, "CC0616", "CC67:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1310, 470, "CC0617", "CC67:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1142, 526, "CC0618", "CC67:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1422, 470, "CC0619", "CC66:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1208, 494, "CC0620", "CC68:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1534, 470, "CC0621", "CC68:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1320, 494, "CC0622", "CC68:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1432, 470, "CC0624", "CC68:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1544, 494, "CC0626", "CC67:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1398, 494, "CC0632", "CC69:6", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpIpswich, 1398, 526, "CC0634", "CC69:5", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpIpswich, 1646, 502, "CC0641", "CC69:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1544, 526, "CC0642", "CC69:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1656, 526, "CC0644", "CC69:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 334,  150, "CC0797", "CC75:7", SignalDirection.LEFT).isShunt();
        Signals.getOrCreateSignal(bpIpswich, 334,  182, "CC0799", "CC76:1", SignalDirection.LEFT).isShunt();
        Signals.getOrCreateSignal(bpIpswich, 616,  430, "CC0834", "CC82:7", SignalDirection.RIGHT).isShunt();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="SX">
        Signals.getOrCreateSignal(bpIpswich, 494,  630, "SX2003", "SX01:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 503,  686, "SX2004", "SX01:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 608,  630, "SX2011", "SX01:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 616,  654, "SX2012", "SX01:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 758,  686, "SX2028", "SX01:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 752,  630, "SX2029", "SX01:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 864,  630, "SX2033", "SX02:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 938,  686, "SX2042", "SX02:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1088, 630, "SX2045", "SX03:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1030, 686, "SX2046", "SX03:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1098, 686, "SX2052", "SX03:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1230, 662, "SX2055", "SX03:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1238, 718, "SX2056", "SX03:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1344, 662, "SX2057", "SX04:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpIpswich, 1354, 686, "SX2060", "SX04:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpIpswich, 1478, 686, "SX2066", "SX04:5", SignalDirection.RIGHT);
        //</editor-fold>
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpIpswich, 26, 226, "COLCHESTER", 7);

        makeNavButton(bpIpswich, 1538, 266, "NORWICH", 12);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(bpIpswich, 216, 370, "IPSWICH", "IPS");

        smallStation(bpIpswich, 1269, 642, "BECCLES",        "BCC");
        smallStation(bpIpswich, 1139, 698, "BRAMPTON",       "BRP");
        smallStation(bpIpswich, 977,  610, "DARSHAM",        "DSM");
        smallStation(bpIpswich, 1140, 450, "DERBY ROAD",     "DBR");
        smallStation(bpIpswich, 1604, 450, "FELIXSTOWE",     "FLX");
        smallStation(bpIpswich, 1034, 610, "HALESWORTH",     "HAS");
        smallStation(bpIpswich, 632,  610, "MELTON",         "MES");
        smallStation(bpIpswich, 1137, 314, "NEEDHAM MKT",    "NMT");
        smallStation(bpIpswich, 1488, 698, "OULTON BROAD S", "OUS");
        smallStation(bpIpswich, 780,  610, "SAXMUNDHAM",     "SAX");
        smallStation(bpIpswich, 1428, 314, "STOWMARKET",     "SMK");
        smallStation(bpIpswich, 1453, 450, "TRIMLEY",        "TRM");
        smallStation(bpIpswich, 681,  610, "WICKHAM MKT",    "WCM");
        smallStation(bpIpswich, 961,  354, "WESTERFIELD",    "WFI"); // CC
        smallStation(bpIpswich, 353,  610, "WESTERFIELD",    "WFI"); // SX
        smallStation(bpIpswich, 524,  610, "WOODBRIDGE",     "WDB");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Clacton">
    private void initClacton()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpClacton, 202,  330, "CC1088");
        Berths.getOrCreateBerth(bpClacton, 202,  266, "CC1091");
        Berths.getOrCreateBerth(bpClacton, 315,  362, "CC1092");
        Berths.getOrCreateBerth(bpClacton, 202,  298, "CC1093");
        Berths.getOrCreateBerth(bpClacton, 282,  378, "CC1094");
        Berths.getOrCreateBerth(bpClacton, 266,  298, "CC1095");
        Berths.getOrCreateBerth(bpClacton, 282,  410, "CC1099");
        Berths.getOrCreateBerth(bpClacton, 218,  378, "CC1096");
        Berths.getOrCreateBerth(bpClacton, 378,  330, "CC1100");
        Berths.getOrCreateBerth(bpClacton, 378,  298, "CC1101");
        Berths.getOrCreateBerth(bpClacton, 474,  330, "CC1102");
        Berths.getOrCreateBerth(bpClacton, 410,  362, "CC1103");
        Berths.getOrCreateBerth(bpClacton, 540,  330, "CC1104");
        Berths.getOrCreateBerth(bpClacton, 474,  298, "CC1105");
        Berths.getOrCreateBerth(bpClacton, 538,  298, "CC1109");
        Berths.getOrCreateBerth(bpClacton, 602,  330, "CC1110");
        Berths.getOrCreateBerth(bpClacton, 602,  298, "CC1111");
        Berths.getOrCreateBerth(bpClacton, 666,  298, "CC1115");
        Berths.getOrCreateBerth(bpClacton, 666,  330, "CC1114");
        Berths.getOrCreateBerth(bpClacton, 730,  330, "CC1118");
        Berths.getOrCreateBerth(bpClacton, 794,  298, "CC1121");
        Berths.getOrCreateBerth(bpClacton, 794,  330, "CC1122");
        Berths.getOrCreateBerth(bpClacton, 874,  298, "CC1125");
        Berths.getOrCreateBerth(bpClacton, 874,  330, "CC1128");
        Berths.getOrCreateBerth(bpClacton, 938,  298, "CC1133");
        Berths.getOrCreateBerth(bpClacton, 970,  330, "CC1134");
        Berths.getOrCreateBerth(bpClacton, 1034, 330, "CC1136");
        Berths.getOrCreateBerth(bpClacton, 1098, 298, "CC1137");
        Berths.getOrCreateBerth(bpClacton, 1098, 330, "CC1138");
        Berths.getOrCreateBerth(bpClacton, 1162, 282, "CC1143");
        Berths.getOrCreateBerth(bpClacton, 1194, 330, "CC1144");
        Berths.getOrCreateBerth(bpClacton, 1258, 298, "CC1145");
        Berths.getOrCreateBerth(bpClacton, 1194, 298, "CC1146");
        Berths.getOrCreateBerth(bpClacton, 1258, 330, "CC1147");
        Berths.getOrCreateBerth(bpClacton, 1354, 330, "CC1152");
        Berths.getOrCreateBerth(bpClacton, 1354, 266, "CC1154");
        Berths.getOrCreateBerth(bpClacton, 1418, 330, "CC1172");
        Berths.getOrCreateBerth(bpClacton, 1418, 298, "CC1173");
        Berths.getOrCreateBerth(bpClacton, 1482, 330, "CC1174");
        Berths.getOrCreateBerth(bpClacton, 1546, 330, "CC1176");
        Berths.getOrCreateBerth(bpClacton, 1546, 298, "CC1177");
        Berths.getOrCreateBerth(bpClacton, 1722, 330, "CCAPP1").hasBorder();
        Berths.getOrCreateBerth(bpClacton, 1658, 330, "CCAPP2").hasBorder();
        Berths.getOrCreateBerth(bpClacton, 154,  378, "CCR096").hasBorder();

        Berths.getOrCreateBerth(bpClacton, 186, 538, "CC1157");
        Berths.getOrCreateBerth(bpClacton, 250, 570, "CC1158");
        Berths.getOrCreateBerth(bpClacton, 250, 538, "CC1159");
        Berths.getOrCreateBerth(bpClacton, 314, 538, "CC1160");
        Berths.getOrCreateBerth(bpClacton, 442, 538, "CC1164");
        Berths.getOrCreateBerth(bpClacton, 378, 538, "CC1165");
        Berths.getOrCreateBerth(bpClacton, 506, 538, "CC1168");
        Berths.getOrCreateBerth(bpClacton, 570, 538, "CCWARR").hasBorder();

        Berths.getOrCreateBerth(bpClacton, 762, 522, "CCCN26");
        Berths.getOrCreateBerth(bpClacton, 826, 522, "CCCN27");
        Berths.getOrCreateBerth(bpClacton, 762, 554, "CCCN42");
        Berths.getOrCreateBerth(bpClacton, 970, 522, "CCCN44");
        Berths.getOrCreateBerth(bpClacton, 970, 554, "CCCN48");
        Berths.getOrCreateBerth(bpClacton, 970, 586, "CCCN55");
        Berths.getOrCreateBerth(bpClacton, 970, 618, "CCCN60");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        Signals.getOrCreateSignal(bpClacton, 200,  350, "CC1088", "CC45:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 254,  262, "CC1091", "CC04:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 329,  352, "CC1092", "CC07:7", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpClacton, 254,  318, "CC1093", "CC04:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 318,  294, "CC1095", "CC04:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 334,  398, "CC1094", "CC07:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 272,  374, "CC1096", "CC08:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 280,  430, "CC1099", "CC07:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 380,  350, "CC1100", "CC06:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 430,  294, "CC1101", "CC04:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 476,  350, "CC1102", "CC06:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 424,  352, "CC1103", "CC04:5", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpClacton, 538,  350, "CC1104", "CC06:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 528,  294, "CC1105", "CC04:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 590,  294, "CC1109", "CC04:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 600,  350, "CC1110", "CC06:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 656,  294, "CC1111", "CC05:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 664,  350, "CC1114", "CC06:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 718,  294, "CC1115", "CC05:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 726,  350, "CC1118", "CC07:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 846,  318, "CC1121", "CC05:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 792,  350, "CC1122", "CC07:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 926,  294, "CC1125", "CC12:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 872,  326, "CC1128", "CC12:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 990,  318, "CC1133", "CC12:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 968,  350, "CC1134", "CC12:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1032, 350, "CC1136", "CC13:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1150, 294, "CC1137", "CC13:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 1096, 350, "CC1138", "CC13:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1192, 350, "CC1144", "CC14:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1310, 294, "CC1145", "CC14:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 1190, 318, "CC1146", "CC14:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1310, 350, "CC1147", "CC14:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 1352, 350, "CC1152", "CC14:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1352, 262, "CC1154", "CC14:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1416, 350, "CC1172", "CC1D:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1480, 350, "CC1174", "CC1D:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1544, 350, "CC1176", "CC1D:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 1470, 294, "CC1173", "CC16:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 1598, 294, "CC1177", "CC1D:5", SignalDirection.LEFT);

        Signals.getOrCreateSignal(bpClacton, 238, 534, "CC1157", "CC15:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 246, 590, "CC1158", "CC15:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 304, 534, "CC1159", "CC15:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 312, 534, "CC1160", "CC15:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 440, 558, "CC1164", "CC16:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpClacton, 430, 534, "CC1165", "CC16:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpClacton, 502, 558, "CC1168", "CC16:6", SignalDirection.RIGHT);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpClacton, 38, 281, "COLCHESTER", 7);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpClacton, 727,  266, "ALRESFORD",       "ALR"); // (ESSEX)
        smallStation(bpClacton, 1034, 570, "CLACTON",         "CLT"); // ON-SEA
        smallStation(bpClacton, 208,  354, "COLCH TOWN",      "CET");
        smallStation(bpClacton, 382,  570, "FRINTON",         "FRI"); // ON-SEA
        smallStation(bpClacton, 384,  578, "ON-SEA",          "FRI"); // FRINTON
        smallStation(bpClacton, 1037, 578, "ON-SEA",          "CLT"); // CLACTON
        smallStation(bpClacton, 828,  274, "GT BENTLEY",      "GRB");
        smallStation(bpClacton, 733,  274, "(ESSEX)",         "ALR"); // ALRESFORD
        smallStation(bpClacton, 483,  274, "HYTHE",           "HYH");
        smallStation(bpClacton, 241,  514, "KIRBY CROSS",     "KBX");
        smallStation(bpClacton, 1205, 354, "THORPE-LE-SOKEN", "TLS");
        smallStation(bpClacton, 507,  570, "WALTON-ON",       "WON");
        smallStation(bpClacton, 507,  578, "-THE-NAZE",        "WON");
        smallStation(bpClacton, 984,  274, "WEELEY",          "WEE");
        smallStation(bpClacton, 610,  274, "WIVENHOE",        "WIV");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Colchester">
    private void initColchester()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpColchester, 214,  266, "CC0190");
        Berths.getOrCreateBerth(bpColchester, 214,  234, "CC0191");
        Berths.getOrCreateBerth(bpColchester, 294,  266, "CC0194");
        Berths.getOrCreateBerth(bpColchester, 374,  330, "CC0196");
        Berths.getOrCreateBerth(bpColchester, 342,  234, "CC0197");
        Berths.getOrCreateBerth(bpColchester, 438,  266, "CC0198");
        Berths.getOrCreateBerth(bpColchester, 342,  266, "CC0199");
        Berths.getOrCreateBerth(bpColchester, 326,  298, "CC0201");
        Berths.getOrCreateBerth(bpColchester, 438,  234, "CC0203");
        Berths.getOrCreateBerth(bpColchester, 470,  298, "CC0205");
        Berths.getOrCreateBerth(bpColchester, 470,  314, "CC0207");
        Berths.getOrCreateBerth(bpColchester, 438,  346, "CC0209");
        Berths.getOrCreateBerth(bpColchester, 438,  378, "CC0211");
        Berths.getOrCreateBerth(bpColchester, 534,  378, "CC0228");
        Berths.getOrCreateBerth(bpColchester, 534,  346, "CC0229");
        Berths.getOrCreateBerth(bpColchester, 678,  378, "CC0230");
        Berths.getOrCreateBerth(bpColchester, 678,  346, "CC0231", "CC0766");
        Berths.getOrCreateBerth(bpColchester, 742,  378, "CC0232");
        Berths.getOrCreateBerth(bpColchester, 742,  346, "CC0233");
        Berths.getOrCreateBerth(bpColchester, 806,  378, "CC0234");
        Berths.getOrCreateBerth(bpColchester, 534,  266, "CC0268");
        Berths.getOrCreateBerth(bpColchester, 598,  234, "CC0269");
        Berths.getOrCreateBerth(bpColchester, 598,  266, "CC0270");
        Berths.getOrCreateBerth(bpColchester, 662,  234, "CC0271");
        Berths.getOrCreateBerth(bpColchester, 662,  266, "CC0272");
        Berths.getOrCreateBerth(bpColchester, 726,  234, "CC0273");
        Berths.getOrCreateBerth(bpColchester, 726,  266, "CC0274");
        Berths.getOrCreateBerth(bpColchester, 790,  234, "CC0275");
        Berths.getOrCreateBerth(bpColchester, 790,  266, "CC0276");
        Berths.getOrCreateBerth(bpColchester, 854,  234, "CC0277");
        Berths.getOrCreateBerth(bpColchester, 854,  266, "CC0278");
        Berths.getOrCreateBerth(bpColchester, 918,  234, "CC0279");
        Berths.getOrCreateBerth(bpColchester, 918,  266, "CC0280");
        Berths.getOrCreateBerth(bpColchester, 982,  234, "CC0281");
        Berths.getOrCreateBerth(bpColchester, 982,  266, "CC0282");
        Berths.getOrCreateBerth(bpColchester, 1046, 234, "CC0283");
        Berths.getOrCreateBerth(bpColchester, 1046, 266, "CC0284");
        Berths.getOrCreateBerth(bpColchester, 1110, 234, "CC0285");
        Berths.getOrCreateBerth(bpColchester, 1110, 266, "CC0286");
        Berths.getOrCreateBerth(bpColchester, 374,  202, "CC0753");
        Berths.getOrCreateBerth(bpColchester, 614,  314, "CC0762", "CC0763");
        Berths.getOrCreateBerth(bpColchester, 982,  290, "CCAPIP").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 806,  410, "CCAPPA").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 806,  346, "CCP001");
        Berths.getOrCreateBerth(bpColchester, 870,  378, "CCP002");
        Berths.getOrCreateBerth(bpColchester, 870,  346, "CCP003");
        Berths.getOrCreateBerth(bpColchester, 998,  378, "CCP004");
        Berths.getOrCreateBerth(bpColchester, 966,  346, "CCP005");
        Berths.getOrCreateBerth(bpColchester, 1062, 378, "CCP006");
        Berths.getOrCreateBerth(bpColchester, 1062, 346, "CCP007");
        Berths.getOrCreateBerth(bpColchester, 1126, 378, "CCP008");
        Berths.getOrCreateBerth(bpColchester, 1126, 346, "CCP009");
        Berths.getOrCreateBerth(bpColchester, 1206, 314, "CCP010");
        Berths.getOrCreateBerth(bpColchester, 1206, 346, "CCP012");
        Berths.getOrCreateBerth(bpColchester, 1206, 378, "CCP014");
        Berths.getOrCreateBerth(bpColchester, 1254, 346, "CCP021");
        Berths.getOrCreateBerth(bpColchester, 1398, 346, "CCP022");
        Berths.getOrCreateBerth(bpColchester, 1334, 378, "CCP024");
        Berths.getOrCreateBerth(bpColchester, 1398, 378, "CCP026");
        Berths.getOrCreateBerth(bpColchester, 1398, 410, "CCP028");
        Berths.getOrCreateBerth(bpColchester, 1446, 346, "CCP037");
        Berths.getOrCreateBerth(bpColchester, 1446, 378, "CCP039");
        Berths.getOrCreateBerth(bpColchester, 1510, 378, "CCP050");
        Berths.getOrCreateBerth(bpColchester, 1558, 378, "CCP057");
        Berths.getOrCreateBerth(bpColchester, 1622, 378, "CCP058");
        Berths.getOrCreateBerth(bpColchester, 1670, 378, "CCP065");
        Berths.getOrCreateBerth(bpColchester, 1734, 378, "CCP074");
        Berths.getOrCreateBerth(bpColchester, 1398, 434, "CCP128");
        Berths.getOrCreateBerth(bpColchester, 1734, 426, "CCPI74");
        Berths.getOrCreateBerth(bpColchester, 1254, 330, "CCPKSD").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 1206, 410, "CCPKTP");
        Berths.getOrCreateBerth(bpColchester, 254,  298, "CCR201").hasBorder();

        Berths.getOrCreateBerth(bpColchester, 1062, 602, "CC0177");
        Berths.getOrCreateBerth(bpColchester, 1126, 634, "CC0178");
        Berths.getOrCreateBerth(bpColchester, 1126, 602, "CC0179");
        Berths.getOrCreateBerth(bpColchester, 1190, 634, "CC0180");
        Berths.getOrCreateBerth(bpColchester, 1190, 602, "CC0181");
        Berths.getOrCreateBerth(bpColchester, 1254, 634, "CC0182");
        Berths.getOrCreateBerth(bpColchester, 1254, 602, "CC0183");
        Berths.getOrCreateBerth(bpColchester, 1334, 634, "CC0184");
        Berths.getOrCreateBerth(bpColchester, 1334, 602, "CC0185");
        Berths.getOrCreateBerth(bpColchester, 1398, 634, "CC0186");
        Berths.getOrCreateBerth(bpColchester, 1398, 602, "CC0187");
        Berths.getOrCreateBerth(bpColchester, 1462, 634, "CC0188");
        Berths.getOrCreateBerth(bpColchester, 1462, 602, "CC0189");
        Berths.getOrCreateBerth(bpColchester, 294,  634, "CC1004");
        Berths.getOrCreateBerth(bpColchester, 294,  602, "CC1005");
        Berths.getOrCreateBerth(bpColchester, 358,  634, "CC1006", "CC4009");
        Berths.getOrCreateBerth(bpColchester, 358,  602, "CC1007");
        Berths.getOrCreateBerth(bpColchester, 422,  634, "CC1008");
        Berths.getOrCreateBerth(bpColchester, 422,  602, "CC1009", "CC4006");
        Berths.getOrCreateBerth(bpColchester, 486,  634, "CC1010", "CC4013");
        Berths.getOrCreateBerth(bpColchester, 486,  602, "CC1011");
        Berths.getOrCreateBerth(bpColchester, 598,  666, "CC1020");
        Berths.getOrCreateBerth(bpColchester, 630,  482, "CC1021");
        Berths.getOrCreateBerth(bpColchester, 598,  634, "CC1022");
        Berths.getOrCreateBerth(bpColchester, 630,  514, "CC1023");
        Berths.getOrCreateBerth(bpColchester, 662,  602, "CC1027");
        Berths.getOrCreateBerth(bpColchester, 566,  514, "CC1028");
        Berths.getOrCreateBerth(bpColchester, 566,  554, "CC1030");
        Berths.getOrCreateBerth(bpColchester, 662,  634, "CC1033");
        Berths.getOrCreateBerth(bpColchester, 710,  458, "CC1034");
        Berths.getOrCreateBerth(bpColchester, 662,  666, "CC1035");
        Berths.getOrCreateBerth(bpColchester, 694,  514, "CC1036");
        Berths.getOrCreateBerth(bpColchester, 662,  578, "CC1039");
        Berths.getOrCreateBerth(bpColchester, 774,  634, "CC1040");
        Berths.getOrCreateBerth(bpColchester, 806,  714, "CC1046");
        Berths.getOrCreateBerth(bpColchester, 758,  458, "CC1047");
        Berths.getOrCreateBerth(bpColchester, 806,  666, "CC1048");
        Berths.getOrCreateBerth(bpColchester, 758,  482, "CC1051");
        Berths.getOrCreateBerth(bpColchester, 822,  570, "CC1054");
        Berths.getOrCreateBerth(bpColchester, 758,  514, "CC1055");
        Berths.getOrCreateBerth(bpColchester, 806,  602, "CC1056");
        Berths.getOrCreateBerth(bpColchester, 726,  538, "CC1057");
        Berths.getOrCreateBerth(bpColchester, 726,  562, "CC1059");
        Berths.getOrCreateBerth(bpColchester, 822,  634, "CC1063");
        Berths.getOrCreateBerth(bpColchester, 870,  570, "CC1065");
        Berths.getOrCreateBerth(bpColchester, 870,  602, "CC1067");
        Berths.getOrCreateBerth(bpColchester, 886,  666, "CC1069", "CC1058");
        Berths.getOrCreateBerth(bpColchester, 870,  698, "CC1071");
        Berths.getOrCreateBerth(bpColchester, 998,  666, "CC1072", "CC1085");
        Berths.getOrCreateBerth(bpColchester, 934,  722, "CC1073");
        Berths.getOrCreateBerth(bpColchester, 998,  634, "CC1074");
        Berths.getOrCreateBerth(bpColchester, 998,  602, "CC1075");
        Berths.getOrCreateBerth(bpColchester, 1062, 634, "CC1076");
        Berths.getOrCreateBerth(bpColchester, 998,  570, "CC1080", "CC1083");
        Berths.getOrCreateBerth(bpColchester, 598,  602, "CC4010");
        Berths.getOrCreateBerth(bpColchester, 198,  602, "CCAPDM").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 198,  634, "CCAPUM").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 486,  570, "CCDMAP").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 870,  722, "CCP5AR").hasBorder();
        Berths.getOrCreateBerth(bpColchester, 806,  690, "CCP6AR").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        Signals.getOrCreateSignal(bpColchester, 210,  286, "CC0190", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 266,  230, "CC0191", "CCB4:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 290,  286, "CC0194", "CCB4:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 386,  320, "CC0196", "CCB4:5", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpColchester, 396,  230, "CC0197", "CCB4:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 436,  286, "CC0198", "CCB5:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 394,  262, "CC0199", "CCB5:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 394,  294, "CC0201", "CCB5:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 490,  230, "CC0203", "CCB5:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 482,  288, "CC0205", "CCB6:3", SignalDirection.DOWN);
        Signals.getOrCreateSignal(bpColchester, 506,  342, "CC0207", "CCB6:6", SignalDirection.UP);
        Signals.getOrCreateSignal(bpColchester, 490,  342, "CC0209", "CCB6:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 490,  374, "CC0211", "CCB7:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 532,  398, "CC0228", "CCB7:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 586,  342, "CC0229", "CCB7:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 674,  398, "CC0230", "CCB7:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 732,  342, "CC0231", "CCB8:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 740,  398, "CC0232", "CCB8:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 794,  342, "CC0233", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 804,  398, "CC0234", "CCB8:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 532,  286, "CC0268", "CCB9:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 650,  230, "CC0269", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 596,  286, "CC0270", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 714,  230, "CC0271", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 660,  286, "CC0272", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 778,  230, "CC0273", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 724,  286, "CC0274", "CCB9:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 842,  230, "CC0275", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 788,  286, "CC0276", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 906,  230, "CC0277", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 852,  286, "CC0278", "CCBA:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 970,  230, "CC0279", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 916,  286, "CC0280", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1034, 230, "CC0281", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 980,  286, "CC0282", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1098, 230, "CC0283", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1044, 286, "CC0284", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1162, 230, "CC0285", "CC6F:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1108, 286, "CC0286", "CC",     SignalDirection.RIGHT);

        Signals.getOrCreateSignal(bpColchester, 1114, 598, "CC0177", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1124, 654, "CC0178", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1178, 598, "CC0179", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1188, 654, "CC0180", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1242, 598, "CC0181", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1252, 654, "CC0182", "CC55:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1306, 598, "CC0183", "CC55:5", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1332, 654, "CC0184", "CC55:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1386, 598, "CC0185", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1396, 654, "CC0186", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1450, 598, "CC0187", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1460, 654, "CC0188", "CC",     SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1514, 598, "CC0189", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 292,  654, "CC1004", "CC27:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 346,  598, "CC1005", "CC27:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 356,  654, "CC1006", "CC27:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 410,  598, "CC1007", "CC27:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 420,  654, "CC1008", "CC27:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 474,  598, "CC1009", "CC27:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 484,  654, "CC1010", "CC28:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 538,  598, "CC1011", "CC28:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 596,  686, "CC1020", "CC28:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 682,  478, "CC1021", "CC28:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 682,  510, "CC1023", "CC28:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 596,  654, "CC1022", "CC28:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 714,  598, "CC1027", "CC36:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 564,  534, "CC1028", "CC28:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 564,  574, "CC1030", "CC28:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 708,  478, "CC1034", "CC29:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 692,  534, "CC1036", "CC29:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 716,  576, "CC1039", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 772,  654, "CC1040", "CC37:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 796,  686, "CC1046", "CC37:5", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 810,  454, "CC1047", "CC",     SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 796,  734, "CC1048", "CC37:6", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 810,  478, "CC1051", "CC43:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 820,  590, "CC1054", "CC37:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 810,  510, "CC1055", "CC43:8", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 802,  598, "CC1056", "CC37:8", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 778,  534, "CC1057", "CC38:1", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 882,  686, "CC1058", "CC38:2", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 778,  558, "CC1059", "CC38:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 874,  654, "CC1063", "CC38:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 924,  590, "CC1065", "CC44:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 924,  598, "CC1067", "CC44:3", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 940,  686, "CC1069", "CC44:4", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 940,  694, "CC1071", "CC44:6", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 996,  686, "CC1072", "CC44:7", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 986,  718, "CC1073", "CC44:8", SignalDirection.TEST);
        Signals.getOrCreateSignal(bpColchester, 996,  654, "CC1074", "CC45:1", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1050, 598, "CC1075", "CC45:2", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1060, 654, "CC1076", "CC45:3", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 996,  590, "CC1080", "CC45:4", SignalDirection.RIGHT);
        Signals.getOrCreateSignal(bpColchester, 1050, 662, "CC1085", "CC45:7", SignalDirection.LEFT);
        Signals.getOrCreateSignal(bpColchester, 1050, 566, "CC1083", "CC45:6", SignalDirection.LEFT);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpColchester, 1198, 250, "IPSWICH", 9);

        makeNavButton(bpColchester, 110,  618, "WITHAM",  4);
        makeNavButton(bpColchester, 1094, 570, "CLACTON", 8);
        makeNavButton(bpColchester, 1094, 666, "CLACTON", 8);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(bpColchester, 802, 746, "COLCHESTER",  "COL");
        largeStation(bpColchester, 276, 178, "MANNINGTREE", "MNG");

        smallStation(bpColchester, 1616, 410, "DOVERCOURT",   "DVC");
        smallStation(bpColchester, 1414, 322, "HARWICH INT",  "HPQ");
        smallStation(bpColchester, 1722, 410, "HARWICH TOWN", "HWC");
        smallStation(bpColchester, 681,  410, "MISTLEY",      "MIS");
        smallStation(bpColchester, 950,  410, "WRABNESS",     "WRB");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Harlow">
    private void initHarlow()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpHarlow, 122,  186, "WG1061");
        Berths.getOrCreateBerth(bpHarlow, 122,  218, "WG1062");
        Berths.getOrCreateBerth(bpHarlow, 186,  186, "WG1063");
        Berths.getOrCreateBerth(bpHarlow, 186,  218, "WG1064");
        Berths.getOrCreateBerth(bpHarlow, 250,  186, "WG1065");
        Berths.getOrCreateBerth(bpHarlow, 250,  218, "WG1066");
        Berths.getOrCreateBerth(bpHarlow, 314,  186, "WG1067");
        Berths.getOrCreateBerth(bpHarlow, 314,  218, "WG1068");
        Berths.getOrCreateBerth(bpHarlow, 378,  186, "WG1069");
        Berths.getOrCreateBerth(bpHarlow, 378,  218, "WG1070");
        Berths.getOrCreateBerth(bpHarlow, 442,  186, "WG1071");
        Berths.getOrCreateBerth(bpHarlow, 442,  218, "WG1072", "WG5315");
        Berths.getOrCreateBerth(bpHarlow, 554,  186, "WG1073", "WG5318");
        Berths.getOrCreateBerth(bpHarlow, 554,  218, "WG1074", "WG5319");
        Berths.getOrCreateBerth(bpHarlow, 618,  186, "WG1077");
        Berths.getOrCreateBerth(bpHarlow, 618,  218, "WG1080");
        Berths.getOrCreateBerth(bpHarlow, 746,  186, "WG1081");
        Berths.getOrCreateBerth(bpHarlow, 858,  186, "WG1083");
        Berths.getOrCreateBerth(bpHarlow, 922,  186, "WG1085");
        Berths.getOrCreateBerth(bpHarlow, 682,  218, "WG1086");
        Berths.getOrCreateBerth(bpHarlow, 746,  218, "WG1088");
        Berths.getOrCreateBerth(bpHarlow, 986,  186, "WG1089");
        Berths.getOrCreateBerth(bpHarlow, 858,  218, "WG1090");
        Berths.getOrCreateBerth(bpHarlow, 1050, 186, "WG1091");
        Berths.getOrCreateBerth(bpHarlow, 922,  218, "WG1092", "WG5331");
        Berths.getOrCreateBerth(bpHarlow, 986,  218, "WG1094");
        Berths.getOrCreateBerth(bpHarlow, 1114, 186, "WG1095");
        Berths.getOrCreateBerth(bpHarlow, 1178, 186, "WG1097");
        Berths.getOrCreateBerth(bpHarlow, 1050, 218, "WG1098");
        Berths.getOrCreateBerth(bpHarlow, 1242, 186, "WG1099");
        Berths.getOrCreateBerth(bpHarlow, 1114, 218, "WG1100");
        Berths.getOrCreateBerth(bpHarlow, 1306, 186, "WG1101");
        Berths.getOrCreateBerth(bpHarlow, 1402, 186, "WG1103");
        Berths.getOrCreateBerth(bpHarlow, 1178, 218, "WG1104");
        Berths.getOrCreateBerth(bpHarlow, 1242, 218, "WG1106");
        Berths.getOrCreateBerth(bpHarlow, 1306, 218, "WG1108");
        Berths.getOrCreateBerth(bpHarlow, 1466, 186, "WG1109");
        Berths.getOrCreateBerth(bpHarlow, 1402, 218, "WG1110");
        Berths.getOrCreateBerth(bpHarlow, 1530, 186, "WG1111");
        Berths.getOrCreateBerth(bpHarlow, 1594, 186, "WG1115");
        Berths.getOrCreateBerth(bpHarlow, 1466, 218, "WG1116");
        Berths.getOrCreateBerth(bpHarlow, 1530, 218, "WG1118");
        Berths.getOrCreateBerth(bpHarlow, 1690, 186, "WG1121", "WG5342");
        Berths.getOrCreateBerth(bpHarlow, 1594, 218, "WG1122");
        Berths.getOrCreateBerth(bpHarlow, 538,  282, "WG1224");
        Berths.getOrCreateBerth(bpHarlow, 618,  282, "WG1226");
        Berths.getOrCreateBerth(bpHarlow, 554,  154, "WG1455", "WG1192");
        Berths.getOrCreateBerth(bpHarlow, 618,  154, "WG5322");
        Berths.getOrCreateBerth(bpHarlow, 650,  170, "WG1459", "WG5328");
        Berths.getOrCreateBerth(bpHarlow, 1402, 154, "WG1461");
        Berths.getOrCreateBerth(bpHarlow, 1626, 154, "WG1463");
        Berths.getOrCreateBerth(bpHarlow, 1594, 250, "WG1494", "WG1465");
        Berths.getOrCreateBerth(bpHarlow, 554,  250, "WG1480", "WG5321");
        Berths.getOrCreateBerth(bpHarlow, 618,  250, "WG1484");
        Berths.getOrCreateBerth(bpHarlow, 1402, 250, "WG1490");
        Berths.getOrCreateBerth(bpHarlow, 554,  114, "WG5325");
        Berths.getOrCreateBerth(bpHarlow, 650,  138, "WG5327");
        Berths.getOrCreateBerth(bpHarlow, 890,  234, "WG5329");
        Berths.getOrCreateBerth(bpHarlow, 1594, 274, "WGHMYD").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 650,  98,  "WGSDG1").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 482,  114, "WGSDG2").hasBorder();

        Berths.getOrCreateBerth(bpHarlow, 186,  346, "WG1123");
        Berths.getOrCreateBerth(bpHarlow, 250,  346, "WG1125");
        Berths.getOrCreateBerth(bpHarlow, 122,  378, "WG1126");
        Berths.getOrCreateBerth(bpHarlow, 186,  378, "WG1128");
        Berths.getOrCreateBerth(bpHarlow, 314,  346, "WG1129");
        Berths.getOrCreateBerth(bpHarlow, 250,  378, "WG1130");
        Berths.getOrCreateBerth(bpHarlow, 378,  346, "WG1131");
        Berths.getOrCreateBerth(bpHarlow, 442,  346, "WG1133");
        Berths.getOrCreateBerth(bpHarlow, 314,  378, "WG1134");
        Berths.getOrCreateBerth(bpHarlow, 506,  346, "WG1135");
        Berths.getOrCreateBerth(bpHarlow, 378,  378, "WG1136");
        Berths.getOrCreateBerth(bpHarlow, 570,  346, "WG1139");
        Berths.getOrCreateBerth(bpHarlow, 442,  378, "WG1138");
        Berths.getOrCreateBerth(bpHarlow, 506,  378, "WG1140");
        Berths.getOrCreateBerth(bpHarlow, 570,  378, "WG1142");
        Berths.getOrCreateBerth(bpHarlow, 634,  346, "WG1145");
        Berths.getOrCreateBerth(bpHarlow, 698,  346, "WG1147");
        Berths.getOrCreateBerth(bpHarlow, 634,  378, "WG1148");
        Berths.getOrCreateBerth(bpHarlow, 698,  378, "WG1150");
        Berths.getOrCreateBerth(bpHarlow, 762,  346, "WG1151");
        Berths.getOrCreateBerth(bpHarlow, 826,  346, "WG1153");
        Berths.getOrCreateBerth(bpHarlow, 762,  378, "WG1154");
        Berths.getOrCreateBerth(bpHarlow, 1002, 346, "WG1155");
        Berths.getOrCreateBerth(bpHarlow, 1098, 346, "WG1157", "WG5396");
        Berths.getOrCreateBerth(bpHarlow, 1162, 346, "WG1163");
        Berths.getOrCreateBerth(bpHarlow, 1226, 346, "WG1165");
        Berths.getOrCreateBerth(bpHarlow, 1290, 346, "WG1167");
        Berths.getOrCreateBerth(bpHarlow, 1354, 346, "WG1169");
        Berths.getOrCreateBerth(bpHarlow, 1418, 346, "WG1173");
        Berths.getOrCreateBerth(bpHarlow, 1514, 346, "WG1175");
        Berths.getOrCreateBerth(bpHarlow, 1578, 346, "WG1183");
        Berths.getOrCreateBerth(bpHarlow, 1722, 346, "WG1187");
        Berths.getOrCreateBerth(bpHarlow, 826,  378, "WG1158");
        Berths.getOrCreateBerth(bpHarlow, 938,  378, "WG1160");
        Berths.getOrCreateBerth(bpHarlow, 1098, 378, "WG1162");
        Berths.getOrCreateBerth(bpHarlow, 1162, 378, "WG1170");
        Berths.getOrCreateBerth(bpHarlow, 1226, 378, "WG1172");
        Berths.getOrCreateBerth(bpHarlow, 1290, 378, "WG1174");
        Berths.getOrCreateBerth(bpHarlow, 1354, 378, "WG1176");
        Berths.getOrCreateBerth(bpHarlow, 1418, 378, "WG1178", "WG5397");
        Berths.getOrCreateBerth(bpHarlow, 1578, 378, "WG1184");
        Berths.getOrCreateBerth(bpHarlow, 1658, 378, "WG1186");
        Berths.getOrCreateBerth(bpHarlow, 1722, 378, "WG1188");
        Berths.getOrCreateBerth(bpHarlow, 1002, 378, "WG1281");
        Berths.getOrCreateBerth(bpHarlow, 938,  346, "WG1284");
        Berths.getOrCreateBerth(bpHarlow, 1002, 410, "WG1493");
        Berths.getOrCreateBerth(bpHarlow, 826,  410, "WG1496");
        Berths.getOrCreateBerth(bpHarlow, 1506, 314, "WG1497", "WG1492");
        Berths.getOrCreateBerth(bpHarlow, 938,  410, "WG1498");
        Berths.getOrCreateBerth(bpHarlow, 706,  402, "WG5383").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 762,  402, "WG5385").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 762,  426, "WG5387").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 706,  426, "WGGF03").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 762,  450, "WGUPSD").hasBorder();

        Berths.getOrCreateBerth(bpHarlow, 186,  538, "WG1230");
        Berths.getOrCreateBerth(bpHarlow, 122,  506, "WG1231");
        Berths.getOrCreateBerth(bpHarlow, 250,  538, "WG1232");
        Berths.getOrCreateBerth(bpHarlow, 186,  506, "WG1233");
        Berths.getOrCreateBerth(bpHarlow, 314,  538, "WG1234");
        Berths.getOrCreateBerth(bpHarlow, 378,  538, "WG1236");
        Berths.getOrCreateBerth(bpHarlow, 314,  506, "WG1237");
        Berths.getOrCreateBerth(bpHarlow, 442,  538, "WG1238");
        Berths.getOrCreateBerth(bpHarlow, 378,  506, "WG1239");
        Berths.getOrCreateBerth(bpHarlow, 506,  538, "WG1240");
        Berths.getOrCreateBerth(bpHarlow, 442,  506, "WG1241");
        Berths.getOrCreateBerth(bpHarlow, 570,  538, "WG1242");
        Berths.getOrCreateBerth(bpHarlow, 570,  506, "WG1243");
        Berths.getOrCreateBerth(bpHarlow, 634,  538, "WG1244");
        Berths.getOrCreateBerth(bpHarlow, 634,  506, "WG1245");
        Berths.getOrCreateBerth(bpHarlow, 698,  538, "WG1246");
        Berths.getOrCreateBerth(bpHarlow, 746,  538, "WG1247");
        Berths.getOrCreateBerth(bpHarlow, 810,  538, "WG1252");
        Berths.getOrCreateBerth(bpHarlow, 810,  506, "WG1253");
        Berths.getOrCreateBerth(bpHarlow, 874,  538, "WG1254");
        Berths.getOrCreateBerth(bpHarlow, 874,  506, "WG1255");
        Berths.getOrCreateBerth(bpHarlow, 938,  538, "WG1256");
        Berths.getOrCreateBerth(bpHarlow, 938,  506, "WG1257");
        Berths.getOrCreateBerth(bpHarlow, 1018, 506, "WGA262");
        Berths.getOrCreateBerth(bpHarlow, 1018, 538, "WGA264");
        Berths.getOrCreateBerth(bpHarlow, 1082, 506, "WGB262");
        Berths.getOrCreateBerth(bpHarlow, 1082, 538, "WGB264");
        Berths.getOrCreateBerth(bpHarlow, 1146, 506, "WGR262").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 1146, 538, "WGR264").hasBorder();

        Berths.getOrCreateBerth(bpHarlow, 122,  602, "WG1220", "WG1213");
        Berths.getOrCreateBerth(bpHarlow, 426,  666, "WG1143");
        Berths.getOrCreateBerth(bpHarlow, 122,  634, "WG1193");
        Berths.getOrCreateBerth(bpHarlow, 122,  666, "WG1194");
        Berths.getOrCreateBerth(bpHarlow, 202,  666, "WG1196");
        Berths.getOrCreateBerth(bpHarlow, 202,  634, "WG1197");
        Berths.getOrCreateBerth(bpHarlow, 266,  666, "WG1198");
        Berths.getOrCreateBerth(bpHarlow, 314,  666, "WG1199");
        Berths.getOrCreateBerth(bpHarlow, 426,  634, "WG1201");
        Berths.getOrCreateBerth(bpHarlow, 378,  634, "WG1202");
        Berths.getOrCreateBerth(bpHarlow, 378,  666, "WG1366");
        Berths.getOrCreateBerth(bpHarlow, 538,  666, "WG1168");
        Berths.getOrCreateBerth(bpHarlow, 522,  698, "WGA206");
        Berths.getOrCreateBerth(bpHarlow, 522,  634, "WGA370");
        Berths.getOrCreateBerth(bpHarlow, 586,  698, "WGB206");
        Berths.getOrCreateBerth(bpHarlow, 586,  634, "WGB370");
        Berths.getOrCreateBerth(bpHarlow, 650,  698, "WGC206");
        Berths.getOrCreateBerth(bpHarlow, 650,  634, "WGC370");
        Berths.getOrCreateBerth(bpHarlow, 714,  666, "WGR168").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 714,  698, "WGR206").hasBorder();
        Berths.getOrCreateBerth(bpHarlow, 714,  634, "WGR370").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpHarlow, 102, 250, "BRIMSDOWN", 5);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpHarlow, 548,  138, "BROXBOURNE",     "BXB");
        smallStation(bpHarlow, 973,  314, "BISHOPS",        "BIS"); // STORTFORD
        smallStation(bpHarlow, 1690, 250, "HARLOW MILL",    "HWM");
        smallStation(bpHarlow, 1393, 274, "HARLOW TOWN",    "HWN");
        smallStation(bpHarlow, 1406, 322, "MOUNTFITCHET",   "SST"); // STANSTED
        smallStation(bpHarlow, 1056, 250, "ROYDON",         "RYN");
        smallStation(bpHarlow, 360,  322, "SAWBRIDGEWORTH", "SAW");
        smallStation(bpHarlow, 1418, 314, "STANSTED",       "SST"); // MOUNTFITCHET
        smallStation(bpHarlow, 967,  322, "STORTFORD",      "BIS"); // BISHOPS

        smallStation(bpHarlow, 1039, 570, "HERTFORD EAST", "HFE");
        smallStation(bpHarlow, 183,  570, "RYE HOUSE",     "RYH");
        smallStation(bpHarlow, 430,  570, "ST MARGARETS",  "SMT");
        smallStation(bpHarlow, 734,  570, "WARE",          "WAR");

        smallStation(bpHarlow, 569,  722, "STANSTED AIRPORT", "SSD");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Hackney">
    private void initHackney()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        //Cambs heath - Clapton Jnc
        Berths.getOrCreateBerth(bpHackney, 346,  186, "LS0120");
        Berths.getOrCreateBerth(bpHackney, 346,  154, "LS0121");
        Berths.getOrCreateBerth(bpHackney, 346,  250, "LS0122");
        Berths.getOrCreateBerth(bpHackney, 346,  218, "LS0123");
        Berths.getOrCreateBerth(bpHackney, 426,  186, "LS0124");
        Berths.getOrCreateBerth(bpHackney, 426,  154, "LS0125");
        Berths.getOrCreateBerth(bpHackney, 426,  250, "LS0126");
        Berths.getOrCreateBerth(bpHackney, 426,  218, "LS0127");
        Berths.getOrCreateBerth(bpHackney, 554,  186, "LS0128", "LS1271");
        Berths.getOrCreateBerth(bpHackney, 490,  154, "LS0129");
        Berths.getOrCreateBerth(bpHackney, 554,  250, "LS0130");
        Berths.getOrCreateBerth(bpHackney, 490,  218, "LS0131");
        Berths.getOrCreateBerth(bpHackney, 618,  186, "LS0132");
        Berths.getOrCreateBerth(bpHackney, 618,  250, "LS0134");
        Berths.getOrCreateBerth(bpHackney, 554,  154, "LS0135");
        Berths.getOrCreateBerth(bpHackney, 666,  122, "LS0136");
        Berths.getOrCreateBerth(bpHackney, 554,  218, "LS0137");
        Berths.getOrCreateBerth(bpHackney, 698,  154, "LS0139");
        Berths.getOrCreateBerth(bpHackney, 794,  186, "LS0140");
        Berths.getOrCreateBerth(bpHackney, 698,  218, "LS0141");
        Berths.getOrCreateBerth(bpHackney, 794,  250, "LS0142");
        Berths.getOrCreateBerth(bpHackney, 794,  154, "LS0145");
        Berths.getOrCreateBerth(bpHackney, 794,  218, "LS0147");
        Berths.getOrCreateBerth(bpHackney, 890,  250, "LS0156");
        Berths.getOrCreateBerth(bpHackney, 890,  218, "LS0157");
        Berths.getOrCreateBerth(bpHackney, 954,  250, "LS0158");
        Berths.getOrCreateBerth(bpHackney, 954,  218, "LS0159");
        Berths.getOrCreateBerth(bpHackney, 1018, 250, "LS0160");
        Berths.getOrCreateBerth(bpHackney, 1018, 218, "LS0161");
        Berths.getOrCreateBerth(bpHackney, 1082, 250, "LS0162");
        Berths.getOrCreateBerth(bpHackney, 1146, 250, "LS0164");
        Berths.getOrCreateBerth(bpHackney, 1082, 218, "LS0165");
        Berths.getOrCreateBerth(bpHackney, 1210, 250, "LS0166");
        Berths.getOrCreateBerth(bpHackney, 1146, 218, "LS0167");
        Berths.getOrCreateBerth(bpHackney, 1322, 170, "LS0170");
        Berths.getOrCreateBerth(bpHackney, 890,  186, "LS1302");
        Berths.getOrCreateBerth(bpHackney, 890,  154, "LS1303");
        Berths.getOrCreateBerth(bpHackney, 954,  186, "LS1304");
        Berths.getOrCreateBerth(bpHackney, 954,  154, "LS1305");
        Berths.getOrCreateBerth(bpHackney, 1322, 250, "LS1404");
        Berths.getOrCreateBerth(bpHackney, 1322, 218, "LS1405");
        Berths.getOrCreateBerth(bpHackney, 1386, 218, "LS1407");

        //Clpton Jnc - Chingford
        Berths.getOrCreateBerth(bpHackney, 106,  778, "LS1408");
        Berths.getOrCreateBerth(bpHackney, 170,  778, "LS1410");
        Berths.getOrCreateBerth(bpHackney, 106,  746, "LS1411");
        Berths.getOrCreateBerth(bpHackney, 234,  778, "LS1412");
        Berths.getOrCreateBerth(bpHackney, 202,  746, "LS1413");
        Berths.getOrCreateBerth(bpHackney, 298,  778, "LS1414");
        Berths.getOrCreateBerth(bpHackney, 298,  746, "LS1415");
        Berths.getOrCreateBerth(bpHackney, 362,  778, "LS1418");
        Berths.getOrCreateBerth(bpHackney, 426,  746, "LS1419");
        Berths.getOrCreateBerth(bpHackney, 426,  778, "LS1420");
        Berths.getOrCreateBerth(bpHackney, 490,  746, "LS1421");
        Berths.getOrCreateBerth(bpHackney, 490,  778, "LS1422");
        Berths.getOrCreateBerth(bpHackney, 554,  746, "LS1423");
        Berths.getOrCreateBerth(bpHackney, 554,  778, "LS1424");
        Berths.getOrCreateBerth(bpHackney, 618,  746, "LS1425");
        Berths.getOrCreateBerth(bpHackney, 682,  746, "LS1427");
        Berths.getOrCreateBerth(bpHackney, 618,  778, "LS1428");
        Berths.getOrCreateBerth(bpHackney, 746,  746, "LS1429");
        Berths.getOrCreateBerth(bpHackney, 682,  778, "LS1430");
        Berths.getOrCreateBerth(bpHackney, 874,  746, "LS1431");
        Berths.getOrCreateBerth(bpHackney, 938,  746, "LS1433");
        Berths.getOrCreateBerth(bpHackney, 746,  778, "LS1432");
        Berths.getOrCreateBerth(bpHackney, 810,  778, "LS1434");
        Berths.getOrCreateBerth(bpHackney, 874,  778, "LS1438");
        Berths.getOrCreateBerth(bpHackney, 938,  778, "LS1440", "LS5557");
        Berths.getOrCreateBerth(bpHackney, 1050, 698, "LS1442");
        Berths.getOrCreateBerth(bpHackney, 970,  698, "LS5561");
        Berths.getOrCreateBerth(bpHackney, 1098, 714, "LSA444");
        Berths.getOrCreateBerth(bpHackney, 1098, 746, "LSA446");
        Berths.getOrCreateBerth(bpHackney, 1098, 778, "LSA448");
        Berths.getOrCreateBerth(bpHackney, 1162, 714, "LSB444");
        Berths.getOrCreateBerth(bpHackney, 1162, 746, "LSB446");
        Berths.getOrCreateBerth(bpHackney, 1162, 778, "LSB448");
        Berths.getOrCreateBerth(bpHackney, 1226, 714, "LSR444").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1226, 746, "LSR446").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1226, 778, "LSR448").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1122, 698, "LSCHAC").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 898,  698, "LSCHAL").hasBorder();

        //Hackney - Bury St Jnc
        Berths.getOrCreateBerth(bpHackney, 138,  362, "WG1306");
        Berths.getOrCreateBerth(bpHackney, 138,  330, "WG1307");
        Berths.getOrCreateBerth(bpHackney, 202,  362, "WG1308");
        Berths.getOrCreateBerth(bpHackney, 266,  362, "WG1310");
        Berths.getOrCreateBerth(bpHackney, 202,  330, "WG1311");
        Berths.getOrCreateBerth(bpHackney, 330,  362, "WG1312");
        Berths.getOrCreateBerth(bpHackney, 266,  330, "WG1315");
        Berths.getOrCreateBerth(bpHackney, 394,  362, "WG1316");
        Berths.getOrCreateBerth(bpHackney, 330,  330, "WG1317");
        Berths.getOrCreateBerth(bpHackney, 458,  362, "WG1318");
        Berths.getOrCreateBerth(bpHackney, 394,  330, "WG1319");
        Berths.getOrCreateBerth(bpHackney, 522,  362, "WG1320");
        Berths.getOrCreateBerth(bpHackney, 458,  330, "WG1321");
        Berths.getOrCreateBerth(bpHackney, 586,  362, "WG1324");
        Berths.getOrCreateBerth(bpHackney, 586,  330, "WG1325");
        Berths.getOrCreateBerth(bpHackney, 666,  362, "WG1326");
        Berths.getOrCreateBerth(bpHackney, 618,  378, "WG1327");
        Berths.getOrCreateBerth(bpHackney, 666,  330, "WG1329", "WG5514");
        Berths.getOrCreateBerth(bpHackney, 794,  362, "WG1330");
        Berths.getOrCreateBerth(bpHackney, 730,  330, "WG1331");
        Berths.getOrCreateBerth(bpHackney, 858,  362, "WG1332");
        Berths.getOrCreateBerth(bpHackney, 794,  330, "WG1333");
        Berths.getOrCreateBerth(bpHackney, 922,  362, "WG1334");
        Berths.getOrCreateBerth(bpHackney, 986,  362, "WG1336");
        Berths.getOrCreateBerth(bpHackney, 858,  330, "WG1337");
        Berths.getOrCreateBerth(bpHackney, 1050, 362, "WG1338");
        Berths.getOrCreateBerth(bpHackney, 922,  330, "WG1339");
        Berths.getOrCreateBerth(bpHackney, 1114, 362, "WG1340");
        Berths.getOrCreateBerth(bpHackney, 986,  330, "WG1341");
        Berths.getOrCreateBerth(bpHackney, 1178, 362, "WG1342");
        Berths.getOrCreateBerth(bpHackney, 1242, 362, "WG1344");
        Berths.getOrCreateBerth(bpHackney, 1050, 330, "WG1345");
        Berths.getOrCreateBerth(bpHackney, 1306, 362, "WG1346");
        Berths.getOrCreateBerth(bpHackney, 1114, 330, "WG1347");
        Berths.getOrCreateBerth(bpHackney, 1370, 362, "WG1348");
        Berths.getOrCreateBerth(bpHackney, 1178, 330, "WG1349");
        Berths.getOrCreateBerth(bpHackney, 1242, 330, "WG1351");
        Berths.getOrCreateBerth(bpHackney, 1370, 330, "WG1353");
        Berths.getOrCreateBerth(bpHackney, 1514, 458, "WG1368");
        Berths.getOrCreateBerth(bpHackney, 1450, 426, "WG1371");
        Berths.getOrCreateBerth(bpHackney, 1578, 458, "WG1372");
        Berths.getOrCreateBerth(bpHackney, 1514, 426, "WG1373");
        Berths.getOrCreateBerth(bpHackney, 1578, 426, "WG1375");

        //Bury St Jnc - Enfield Town
        Berths.getOrCreateBerth(bpHackney, 1386, 762, "WG1350");
        Berths.getOrCreateBerth(bpHackney, 1450, 762, "WG1352");
        Berths.getOrCreateBerth(bpHackney, 1514, 762, "WG1356");
        Berths.getOrCreateBerth(bpHackney, 1626, 730, "WGA360");
        Berths.getOrCreateBerth(bpHackney, 1626, 762, "WGA362");
        Berths.getOrCreateBerth(bpHackney, 1626, 794, "WGA364");
        Berths.getOrCreateBerth(bpHackney, 1690, 730, "WGB360");
        Berths.getOrCreateBerth(bpHackney, 1690, 762, "WGB362");
        Berths.getOrCreateBerth(bpHackney, 1690, 794, "WGB364");
        Berths.getOrCreateBerth(bpHackney, 1754, 730, "WGR360").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1754, 762, "WGR362").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1754, 794, "WGR364").hasBorder();
        Berths.getOrCreateBerth(bpHackney, 1450, 330, "WG1361");
        Berths.getOrCreateBerth(bpHackney, 1386, 730, "WG1363");
        Berths.getOrCreateBerth(bpHackney, 1514, 730, "WG1365");

        //Temple Mills/Southbury - Cheshunt
        Berths.getOrCreateBerth(bpHackney, 106,  602, "WG1003");
        Berths.getOrCreateBerth(bpHackney, 170,  602, "WG1005");
        Berths.getOrCreateBerth(bpHackney, 394,  570, "WG1004");
        Berths.getOrCreateBerth(bpHackney, 202,  522, "WG1007");
        Berths.getOrCreateBerth(bpHackney, 266,  602, "WG1009");
        Berths.getOrCreateBerth(bpHackney, 266,  634, "WG1010");
        Berths.getOrCreateBerth(bpHackney, 330,  602, "WG1011");
        Berths.getOrCreateBerth(bpHackney, 330,  634, "WG1012");
        Berths.getOrCreateBerth(bpHackney, 426,  602, "WG1013");
        Berths.getOrCreateBerth(bpHackney, 490,  634, "WG1014");
        Berths.getOrCreateBerth(bpHackney, 490,  602, "WG1015", "WG1016");
        Berths.getOrCreateBerth(bpHackney, 554,  602, "WG1017");
        Berths.getOrCreateBerth(bpHackney, 554,  634, "WG1018");
        Berths.getOrCreateBerth(bpHackney, 618,  634, "WG1020");
        Berths.getOrCreateBerth(bpHackney, 618,  602, "WG1021");
        Berths.getOrCreateBerth(bpHackney, 682,  634, "WG1022");
        Berths.getOrCreateBerth(bpHackney, 682,  602, "WG1023");
        Berths.getOrCreateBerth(bpHackney, 746,  602, "WG1025");
        Berths.getOrCreateBerth(bpHackney, 746,  634, "WG1026");
        Berths.getOrCreateBerth(bpHackney, 810,  602, "WG1027");
        Berths.getOrCreateBerth(bpHackney, 810,  634, "WG1028");
        Berths.getOrCreateBerth(bpHackney, 874,  602, "WG1029");
        Berths.getOrCreateBerth(bpHackney, 874,  634, "WG1030");
        Berths.getOrCreateBerth(bpHackney, 938,  602, "WG1031");
        Berths.getOrCreateBerth(bpHackney, 1002, 602, "WG1033");
        Berths.getOrCreateBerth(bpHackney, 938,  634, "WG1034");
        Berths.getOrCreateBerth(bpHackney, 1002, 634, "WG1036");
        Berths.getOrCreateBerth(bpHackney, 1066, 602, "WG1037");
        Berths.getOrCreateBerth(bpHackney, 1066, 634, "WG1038");
        Berths.getOrCreateBerth(bpHackney, 1130, 602, "WG1039");
        Berths.getOrCreateBerth(bpHackney, 1130, 634, "WG1040");
        Berths.getOrCreateBerth(bpHackney, 1194, 602, "WG1041");
        Berths.getOrCreateBerth(bpHackney, 1194, 634, "WG1042");
        Berths.getOrCreateBerth(bpHackney, 1274, 634, "WG1044");
        Berths.getOrCreateBerth(bpHackney, 1274, 602, "WG1045");
        Berths.getOrCreateBerth(bpHackney, 1338, 634, "WG1046");
        Berths.getOrCreateBerth(bpHackney, 1338, 602, "WG1047");
        Berths.getOrCreateBerth(bpHackney, 1402, 602, "WG1049");
        Berths.getOrCreateBerth(bpHackney, 1402, 634, "WG1050");
        Berths.getOrCreateBerth(bpHackney, 1466, 634, "WG1052");
        Berths.getOrCreateBerth(bpHackney, 1466, 602, "WG1053");
        Berths.getOrCreateBerth(bpHackney, 1530, 634, "WG1054");
        Berths.getOrCreateBerth(bpHackney, 1530, 602, "WG1055");
        Berths.getOrCreateBerth(bpHackney, 1594, 634, "WG1056", "WG5311");
        Berths.getOrCreateBerth(bpHackney, 1594, 602, "WG1057");
        Berths.getOrCreateBerth(bpHackney, 1706, 602, "WG1059", "WG1058");
        Berths.getOrCreateBerth(bpHackney, 1706, 634, "WG1060");
        Berths.getOrCreateBerth(bpHackney, 1706, 570, "WG1144");
        Berths.getOrCreateBerth(bpHackney, 890,  538, "WG1374");
        Berths.getOrCreateBerth(bpHackney, 954,  538, "WG1376");
        Berths.getOrCreateBerth(bpHackney, 890,  506, "WG1377");
        Berths.getOrCreateBerth(bpHackney, 1082, 538, "WG1378");
        Berths.getOrCreateBerth(bpHackney, 954,  506, "WG1379");
        Berths.getOrCreateBerth(bpHackney, 1146, 538, "WG1380");
        Berths.getOrCreateBerth(bpHackney, 1018, 506, "WG1381");
        Berths.getOrCreateBerth(bpHackney, 1210, 538, "WG1382");
        Berths.getOrCreateBerth(bpHackney, 1082, 506, "WG1383");
        Berths.getOrCreateBerth(bpHackney, 1274, 538, "WG1384");
        Berths.getOrCreateBerth(bpHackney, 1146, 506, "WG1385");
        Berths.getOrCreateBerth(bpHackney, 1338, 538, "WG1386");
        Berths.getOrCreateBerth(bpHackney, 1210, 506, "WG1387");
        Berths.getOrCreateBerth(bpHackney, 1402, 538, "WG1388");
        Berths.getOrCreateBerth(bpHackney, 1338, 506, "WG1389");
        Berths.getOrCreateBerth(bpHackney, 1466, 538, "WG1390");
        Berths.getOrCreateBerth(bpHackney, 1402, 506, "WG1391");
        Berths.getOrCreateBerth(bpHackney, 1530, 538, "WG1392");
        Berths.getOrCreateBerth(bpHackney, 1466, 506, "WG1393");
        Berths.getOrCreateBerth(bpHackney, 1594, 538, "WG1394", "WG5309");
        Berths.getOrCreateBerth(bpHackney, 1594, 506, "WG1395");
        Berths.getOrCreateBerth(bpHackney, 426,  634, "WG5305");
        Berths.getOrCreateBerth(bpHackney, 1762, 570, "WGR144").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpHackney, 150, 202, "LIVERPOOL ST", 1);
        makeNavButton(bpHackney, 38,  666, "STRATFORD",    1);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpHackney, 746,  578, "ANGEL RD",       "AGR");
        smallStation(bpHackney, 1191, 578, "BRIMSDOWN",      "BMD");
        smallStation(bpHackney, 849,  306, "BRUCE GROVE",    "BCV");
        smallStation(bpHackney, 1383, 698, "BUSH HILL",      "BHK"); // PARK
        smallStation(bpHackney, 383,  122, "CAMBRIDGE",      "CBH"); // HEATH
        smallStation(bpHackney, 301,  722, "CENTRAL",        "WHC"); // WALTHAMSTOW
        smallStation(bpHackney, 1706, 666, "CHESHUNT",       "CHN");
        smallStation(bpHackney, 1131, 810, "CHINGFORD",      "CHI");
        smallStation(bpHackney, 1085, 194, "CLAPTON",        "CPT");
        smallStation(bpHackney, 803,  130, "DOWNS",          "HAC"); // HACKNEY
        smallStation(bpHackney, 1242, 298, "EDMONTON",       "EDR"); // GREEN
        smallStation(bpHackney, 1326, 578, "ENFIELD LOCK",   "ENL");
        smallStation(bpHackney, 1650, 826, "ENFIELD TOWN",   "ENF");
        smallStation(bpHackney, 560,  130, "FIELDS",         "LOF"); // LONDON
        smallStation(bpHackney, 1251, 306, "GREEN",          "EDR"); // EDMONTON
        smallStation(bpHackney, 1219, 482, "GROVE",          "TEO"); // THEOBALDS
        smallStation(bpHackney, 797,  122, "HACKNEY",        "HAC"); // DOWNS
        smallStation(bpHackney, 502,  578, "HALE",           "TOM"); // TOTTENAM
        smallStation(bpHackney, 395,  130, "HEATH",          "CBH"); // CAMBRIDGE
        smallStation(bpHackney, 749,  714, "HIGHAMS",        "HIP"); // PARK
        smallStation(bpHackney, 470,  306, "HILL",           "HIP"); // STAMFORD
        smallStation(bpHackney, 560,  122, "LONDON",         "LOF"); // FIELDS
        smallStation(bpHackney, 263,  306, "NEWINGTON",      "SKW"); // STOKE
        smallStation(bpHackney, 600,  570, "NORTHUMBERLAND", "NUM"); // PARK
        smallStation(bpHackney, 1398, 706, "PARK",           "BHK"); // BUSH HILL
        smallStation(bpHackney, 758,  722, "PARK",           "HIP"); // HIGHAMS
        smallStation(bpHackney, 630,  578, "PARK",           "NUM"); // NORTHUMBERLAND
        smallStation(bpHackney, 1057, 578, "PONDERS END",    "PON");
        smallStation(bpHackney, 132,  306, "RECTORY RD",     "REC");
        smallStation(bpHackney, 675,  298, "SEVEN",          "SVS"); // SISTERS
        smallStation(bpHackney, 1111, 306, "SILVER ST",      "SLV");
        smallStation(bpHackney, 669,  306, "SISTERS",        "SVS"); // SEVEN
        smallStation(bpHackney, 887,  482, "SOUTHBURY",      "SBU");
        smallStation(bpHackney, 161,  722, "ST JAMES ST",    "SJS");
        smallStation(bpHackney, 458,  298, "STAMFORD",       "SMH"); // HILL
        smallStation(bpHackney, 275,  298, "STOKE",          "SKW"); // NEWINGTON
        smallStation(bpHackney, 1463, 482, "TURKEY ST",      "TUR");
        smallStation(bpHackney, 1207, 474, "THEOBALDS",      "TEO"); // GROVE
        smallStation(bpHackney, 487,  570, "TOTTENHAM",      "TOM"); // HALE
        smallStation(bpHackney, 289,  714, "WALTHAMSTOW",    "WHC"); // CENTRAL
        smallStation(bpHackney, 1451, 578, "WALTHAM CROSS",  "WLC");
        smallStation(bpHackney, 971,  306, "WHITE HART LN",  "WHL");
        smallStation(bpHackney, 493,  722, "WOOD ST",        "WST");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Witham">
    private void initWitham()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpWitham, 186,  202, "SE0747");
        Berths.getOrCreateBerth(bpWitham, 250,  202, "SE0751", "SE0794");
        Berths.getOrCreateBerth(bpWitham, 314,  202, "SE0755");
        Berths.getOrCreateBerth(bpWitham, 378,  202, "SE0759");
        Berths.getOrCreateBerth(bpWitham, 442,  202, "SE0761");
        Berths.getOrCreateBerth(bpWitham, 602,  170, "SE0765");
        Berths.getOrCreateBerth(bpWitham, 666,  138, "SE0767");
        Berths.getOrCreateBerth(bpWitham, 602,  202, "SE0769");
        Berths.getOrCreateBerth(bpWitham, 602,  234, "SE0771");
        Berths.getOrCreateBerth(bpWitham, 602,  266, "SE0773");
        Berths.getOrCreateBerth(bpWitham, 682,  170, "SE0779", "SE0820");
        Berths.getOrCreateBerth(bpWitham, 682,  202, "SE0781", "SE0822");
        Berths.getOrCreateBerth(bpWitham, 746,  202, "SE0785", "SE0828");
        Berths.getOrCreateBerth(bpWitham, 810,  202, "SE0787");
        Berths.getOrCreateBerth(bpWitham, 874,  202, "SE0789");
        Berths.getOrCreateBerth(bpWitham, 938,  202, "SE0791");
        Berths.getOrCreateBerth(bpWitham, 186,  234, "SE0792");
        Berths.getOrCreateBerth(bpWitham, 250,  234, "SE0796", "SE0753");
        Berths.getOrCreateBerth(bpWitham, 1018, 202, "SE0799", "SE0844");
        Berths.getOrCreateBerth(bpWitham, 314,  234, "SE0800");
        Berths.getOrCreateBerth(bpWitham, 1082, 202, "SE0801");
        Berths.getOrCreateBerth(bpWitham, 1146, 202, "SE0803");
        Berths.getOrCreateBerth(bpWitham, 378,  234, "SE0804");
        Berths.getOrCreateBerth(bpWitham, 1210, 202, "SE0805", "SE0854");
        Berths.getOrCreateBerth(bpWitham, 442,  234, "SE0806", "SE0763");
        Berths.getOrCreateBerth(bpWitham, 538,  170, "SE0810");
        Berths.getOrCreateBerth(bpWitham, 538,  202, "SE0812");
        Berths.getOrCreateBerth(bpWitham, 1274, 202, "SE0815");
        Berths.getOrCreateBerth(bpWitham, 538,  234, "SE0814");
        Berths.getOrCreateBerth(bpWitham, 538,  266, "SE0816");
        Berths.getOrCreateBerth(bpWitham, 1338, 202, "SE0817");
        Berths.getOrCreateBerth(bpWitham, 714,  138, "SE0818");
        Berths.getOrCreateBerth(bpWitham, 1322, 154, "SE0823");
        Berths.getOrCreateBerth(bpWitham, 682,  234, "SE0824");
        Berths.getOrCreateBerth(bpWitham, 682,  266, "SE0826", "SE5193");
        Berths.getOrCreateBerth(bpWitham, 1402, 202, "SE0827", "SE0862");
        Berths.getOrCreateBerth(bpWitham, 746,  234, "SE0830");
        Berths.getOrCreateBerth(bpWitham, 810,  234, "SE0832");
        Berths.getOrCreateBerth(bpWitham, 874,  234, "SE0834");
        Berths.getOrCreateBerth(bpWitham, 938,  234, "SE0836", "SE0793");
        Berths.getOrCreateBerth(bpWitham, 1498, 202, "SE0837", "SE0884");
        Berths.getOrCreateBerth(bpWitham, 1018, 234, "SE0846");
        Berths.getOrCreateBerth(bpWitham, 1082, 234, "SE0848");
        Berths.getOrCreateBerth(bpWitham, 1146, 234, "SE0850", "SE0807");
        Berths.getOrCreateBerth(bpWitham, 1210, 234, "SE0856");
        Berths.getOrCreateBerth(bpWitham, 1274, 234, "SE0858");
        Berths.getOrCreateBerth(bpWitham, 1338, 234, "SE0860", "SE0819");
        Berths.getOrCreateBerth(bpWitham, 1402, 234, "SE0864", "SE0829");
        Berths.getOrCreateBerth(bpWitham, 1402, 170, "SE0868", "SE0825");
        Berths.getOrCreateBerth(bpWitham, 1562, 202, "SE0872");
        Berths.getOrCreateBerth(bpWitham, 1498, 234, "SE0874");
        Berths.getOrCreateBerth(bpWitham, 1498, 266, "SE0876", "SE5203");
        Berths.getOrCreateBerth(bpWitham, 1498, 298, "SE0878");
        Berths.getOrCreateBerth(bpWitham, 1570, 298, "SEUSDG").hasBorder();
        Berths.getOrCreateBerth(bpWitham, 1562, 234, "SE0882", "SE0839");
        Berths.getOrCreateBerth(bpWitham, 906,  138, "SE0896");
        Berths.getOrCreateBerth(bpWitham, 970,  138, "SER896").hasBorder();
        Berths.getOrCreateBerth(bpWitham, 1178, 154, "SE0898");
        Berths.getOrCreateBerth(bpWitham, 1114, 154, "SER898").hasBorder();
        Berths.getOrCreateBerth(bpWitham, 602,  290, "SE5189");
        Berths.getOrCreateBerth(bpWitham, 1466, 170, "SE5202");
        Berths.getOrCreateBerth(bpWitham, 810,  138, "SECRES");
        Berths.getOrCreateBerth(bpWitham, 1538, 170, "SEDEPO").hasBorder();
        Berths.getOrCreateBerth(bpWitham, 1250, 154, "SESHAL");

        Berths.getOrCreateBerth(bpWitham, 250,  394, "SE0669");
        Berths.getOrCreateBerth(bpWitham, 314,  394, "SE0671");
        Berths.getOrCreateBerth(bpWitham, 378,  362, "SE0675");
        Berths.getOrCreateBerth(bpWitham, 378,  394, "SE0677");
        Berths.getOrCreateBerth(bpWitham, 442,  394, "SE0683", "SE0712");
        Berths.getOrCreateBerth(bpWitham, 506,  394, "SE0685", "SE0716");
        Berths.getOrCreateBerth(bpWitham, 586,  394, "SE0691");
        Berths.getOrCreateBerth(bpWitham, 650,  394, "SE0693", "SE0724");
        Berths.getOrCreateBerth(bpWitham, 714,  394, "SE0695");
        Berths.getOrCreateBerth(bpWitham, 778,  394, "SE0697");
        Berths.getOrCreateBerth(bpWitham, 906,  394, "SE0703", "SE0736");
        Berths.getOrCreateBerth(bpWitham, 970,  394, "SE0705");
        Berths.getOrCreateBerth(bpWitham, 378,  426, "SE0710", "SE0679");
        Berths.getOrCreateBerth(bpWitham, 1034, 394, "SE0711");
        Berths.getOrCreateBerth(bpWitham, 442,  426, "SE0714");
        Berths.getOrCreateBerth(bpWitham, 1098, 394, "SE0715", "SE0758");
        Berths.getOrCreateBerth(bpWitham, 506,  426, "SE0718", "SE0687");
        Berths.getOrCreateBerth(bpWitham, 586,  426, "SE0722");
        Berths.getOrCreateBerth(bpWitham, 1162, 362, "SE0723", "SE0762");
        Berths.getOrCreateBerth(bpWitham, 1162, 394, "SE0725", "SE0764");
        Berths.getOrCreateBerth(bpWitham, 650,  426, "SE0726");
        Berths.getOrCreateBerth(bpWitham, 1242, 394, "SE0729", "SE0770");
        Berths.getOrCreateBerth(bpWitham, 714,  426, "SE0730");
        Berths.getOrCreateBerth(bpWitham, 1306, 394, "SE0731");
        Berths.getOrCreateBerth(bpWitham, 778,  426, "SE0732", "SE0701");
        Berths.getOrCreateBerth(bpWitham, 1370, 394, "SE0733");
        Berths.getOrCreateBerth(bpWitham, 842,  426, "SE0734");
        Berths.getOrCreateBerth(bpWitham, 1434, 394, "SE0735");
        Berths.getOrCreateBerth(bpWitham, 1514, 394, "SE0739", "SE0786");
        Berths.getOrCreateBerth(bpWitham, 906,  426, "SE0740");
        Berths.getOrCreateBerth(bpWitham, 1578, 394, "SE0741");
        Berths.getOrCreateBerth(bpWitham, 970,  426, "SE0742");
        Berths.getOrCreateBerth(bpWitham, 1034, 426, "SE0756", "SE0713");
        Berths.getOrCreateBerth(bpWitham, 1098, 426, "SE0760", "SE0717");
        Berths.getOrCreateBerth(bpWitham, 1162, 426, "SE0766", "SE0727");
        Berths.getOrCreateBerth(bpWitham, 1242, 426, "SE0772");
        Berths.getOrCreateBerth(bpWitham, 1306, 426, "SE0778");
        Berths.getOrCreateBerth(bpWitham, 1370, 426, "SE0782");
        Berths.getOrCreateBerth(bpWitham, 1434, 426, "SE0784", "SE0737");
        Berths.getOrCreateBerth(bpWitham, 1514, 426, "SE0788");
        Berths.getOrCreateBerth(bpWitham, 1578, 426, "SE0790");
        Berths.getOrCreateBerth(bpWitham, 1098, 362, "SE5185");
        Berths.getOrCreateBerth(bpWitham, 1162, 330, "SE5186");
        Berths.getOrCreateBerth(bpWitham, 1234, 321, "SE5188").hasBorder();
        Berths.getOrCreateBerth(bpWitham, 1234, 339, "SEGYNE").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpWitham, 1650, 218, "COLCHESTER", 7);
        makeNavButton(bpWitham, 102,  410, "SHENFIELD",  3);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpWitham, 907,  122, "BRAINTREE",   "BTR");
        smallStation(bpWitham, 1092, 458, "CHELMSFORD",  "CHM");
        smallStation(bpWitham, 250,  266, "HATFIELD",    "HAP");
        smallStation(bpWitham, 433,  458, "INGATESTONE", "INT");
        smallStation(bpWitham, 1018, 266, "KELVEDON",    "KEL");
        smallStation(bpWitham, 1399, 154, "MARKS TEY",   "MKT");
        smallStation(bpWitham, 253,  274, "PEVERAL",     "HAP");
        smallStation(bpWitham, 1177, 130, "SUDBURY",     "SUY");
        smallStation(bpWitham, 576,  154, "WITHAM",      "WTM");
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Shenfield">
    private void initShenfield()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpShenfield, 234,  154, "SE0445");
        Berths.getOrCreateBerth(bpShenfield, 298,  154, "SE0447");
        Berths.getOrCreateBerth(bpShenfield, 298,  218, "SE0449");
        Berths.getOrCreateBerth(bpShenfield, 362,  154, "SE0451");
        Berths.getOrCreateBerth(bpShenfield, 362,  218, "SE0453");
        Berths.getOrCreateBerth(bpShenfield, 426,  154, "SE0455");
        Berths.getOrCreateBerth(bpShenfield, 426,  218, "SE0457");
        Berths.getOrCreateBerth(bpShenfield, 490,  154, "SE0459");
        Berths.getOrCreateBerth(bpShenfield, 490,  218, "SE0461");
        Berths.getOrCreateBerth(bpShenfield, 554,  154, "SE0463");
        Berths.getOrCreateBerth(bpShenfield, 554,  218, "SE0465");
        Berths.getOrCreateBerth(bpShenfield, 618,  154, "SE0467");
        Berths.getOrCreateBerth(bpShenfield, 618,  218, "SE0469");
        Berths.getOrCreateBerth(bpShenfield, 682,  154, "SE0471");
        Berths.getOrCreateBerth(bpShenfield, 682,  218, "SE0473");
        Berths.getOrCreateBerth(bpShenfield, 746,  154, "SE0475");
        Berths.getOrCreateBerth(bpShenfield, 746,  218, "SE0477");
        Berths.getOrCreateBerth(bpShenfield, 298,  186, "SE0478");
        Berths.getOrCreateBerth(bpShenfield, 810,  154, "SE0479");
        Berths.getOrCreateBerth(bpShenfield, 298,  250, "SE0480");
        Berths.getOrCreateBerth(bpShenfield, 810,  218, "SE0481");
        Berths.getOrCreateBerth(bpShenfield, 362,  186, "SE0482");
        Berths.getOrCreateBerth(bpShenfield, 874,  154, "SE0483");
        Berths.getOrCreateBerth(bpShenfield, 362,  250, "SE0484");
        Berths.getOrCreateBerth(bpShenfield, 874,  218, "SE0485");
        Berths.getOrCreateBerth(bpShenfield, 938,  154, "SE0487");
        Berths.getOrCreateBerth(bpShenfield, 938,  218, "SE0489");
        Berths.getOrCreateBerth(bpShenfield, 490,  186, "SE0486");
        Berths.getOrCreateBerth(bpShenfield, 490,  250, "SE0488");
        Berths.getOrCreateBerth(bpShenfield, 554,  186, "SE0490");
        Berths.getOrCreateBerth(bpShenfield, 1034, 154, "SE0491");
        Berths.getOrCreateBerth(bpShenfield, 554,  250, "SE0492");
        Berths.getOrCreateBerth(bpShenfield, 1034, 218, "SE0493");
        Berths.getOrCreateBerth(bpShenfield, 618,  186, "SE0494");
        Berths.getOrCreateBerth(bpShenfield, 1242, 154, "SE0495");
        Berths.getOrCreateBerth(bpShenfield, 618,  250, "SE0496");
        Berths.getOrCreateBerth(bpShenfield, 1242, 186, "SE0497");
        Berths.getOrCreateBerth(bpShenfield, 746,  186, "SE0498");
        Berths.getOrCreateBerth(bpShenfield, 1242, 218, "SE0499");
        Berths.getOrCreateBerth(bpShenfield, 746,  250, "SE0500");
        Berths.getOrCreateBerth(bpShenfield, 1242, 250, "SE0501");
        Berths.getOrCreateBerth(bpShenfield, 810,  186, "SE0502");
        Berths.getOrCreateBerth(bpShenfield, 1242, 282, "SE0503");
        Berths.getOrCreateBerth(bpShenfield, 810,  250, "SE0504");
        Berths.getOrCreateBerth(bpShenfield, 938,  186, "SE0506");
        Berths.getOrCreateBerth(bpShenfield, 938,  250, "SE0508");
        Berths.getOrCreateBerth(bpShenfield, 1450, 218, "SE0509", "SE0536");
        Berths.getOrCreateBerth(bpShenfield, 1034, 282, "SE0510");
        Berths.getOrCreateBerth(bpShenfield, 1034, 186, "SE0512");
        Berths.getOrCreateBerth(bpShenfield, 1514, 218, "SE0513", "SE0540");
        Berths.getOrCreateBerth(bpShenfield, 1034, 250, "SE0514");
        Berths.getOrCreateBerth(bpShenfield, 1370, 282, "SE0515");
        Berths.getOrCreateBerth(bpShenfield, 1178, 154, "SE0516");
        Berths.getOrCreateBerth(bpShenfield, 1178, 186, "SE0518");
        Berths.getOrCreateBerth(bpShenfield, 1178, 250, "SE0522");
        Berths.getOrCreateBerth(bpShenfield, 1178, 282, "SE0524");
        Berths.getOrCreateBerth(bpShenfield, 1434, 154, "SE0528");
        Berths.getOrCreateBerth(bpShenfield, 1482, 186, "SE0505");
        Berths.getOrCreateBerth(bpShenfield, 1418, 186, "SE0530", "SE0507");
        Berths.getOrCreateBerth(bpShenfield, 1370, 250, "SE0534");
        Berths.getOrCreateBerth(bpShenfield, 1450, 250, "SE0538", "SE0511");
        Berths.getOrCreateBerth(bpShenfield, 1514, 250, "SE0542");
        Berths.getOrCreateBerth(bpShenfield, 1578, 250, "SE0544");
        Berths.getOrCreateBerth(bpShenfield, 1482, 298, "SE0550");
        Berths.getOrCreateBerth(bpShenfield, 1322, 314, "SE0552");
        Berths.getOrCreateBerth(bpShenfield, 1386, 314, "SE0554");
        Berths.getOrCreateBerth(bpShenfield, 1178, 218, "SE5132");
        Berths.getOrCreateBerth(bpShenfield, 1242, 314, "SE5134");
        Berths.getOrCreateBerth(bpShenfield, 1082, 122, "SE5137");
        Berths.getOrCreateBerth(bpShenfield, 1098, 282, "SE5139");
        Berths.getOrCreateBerth(bpShenfield, 1358, 202, "SEA136");
        Berths.getOrCreateBerth(bpShenfield, 1358, 170, "SEA138");
        Berths.getOrCreateBerth(bpShenfield, 1362, 122, "SEB136").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1362, 98,  "SEB138").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1002, 122, "SEDNSG").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1306, 122, "SER136").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1306, 98,  "SER138").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1306, 202, "SESMS1");
        Berths.getOrCreateBerth(bpShenfield, 1306, 170, "SESMS2");
        Berths.getOrCreateBerth(bpShenfield, 1242, 338, "SEUPSG").hasBorder();

        Berths.getOrCreateBerth(bpShenfield, 202,  410, "SE0517");
        Berths.getOrCreateBerth(bpShenfield, 202,  442, "SE0519");
        Berths.getOrCreateBerth(bpShenfield, 282,  442, "SE0521", "SE0558");
        Berths.getOrCreateBerth(bpShenfield, 346,  442, "SE0523");
        Berths.getOrCreateBerth(bpShenfield, 410,  442, "SE0525");
        Berths.getOrCreateBerth(bpShenfield, 474,  442, "SE0527");
        Berths.getOrCreateBerth(bpShenfield, 538,  442, "SE0529");
        Berths.getOrCreateBerth(bpShenfield, 602,  442, "SE0533");
        Berths.getOrCreateBerth(bpShenfield, 682,  442, "SE0537", "SE0572");
        Berths.getOrCreateBerth(bpShenfield, 746,  442, "SE0539");
        Berths.getOrCreateBerth(bpShenfield, 810,  442, "SE0541");
        Berths.getOrCreateBerth(bpShenfield, 938,  442, "SE0543");
        Berths.getOrCreateBerth(bpShenfield, 1002, 442, "SE0545");
        Berths.getOrCreateBerth(bpShenfield, 1066, 442, "SE0547");
        Berths.getOrCreateBerth(bpShenfield, 1130, 442, "SE0549");
        Berths.getOrCreateBerth(bpShenfield, 1258, 506, "SE0555");
        Berths.getOrCreateBerth(bpShenfield, 202,  474, "SE0556");
        Berths.getOrCreateBerth(bpShenfield, 1258, 410, "SE0557");
        Berths.getOrCreateBerth(bpShenfield, 1258, 442, "SE0559", "SE0594");
        Berths.getOrCreateBerth(bpShenfield, 282,  474, "SE0560");
        Berths.getOrCreateBerth(bpShenfield, 346,  474, "SE0562");
        Berths.getOrCreateBerth(bpShenfield, 410,  474, "SE0564");
        Berths.getOrCreateBerth(bpShenfield, 474,  474, "SE0566");
        Berths.getOrCreateBerth(bpShenfield, 538,  474, "SE0568");
        Berths.getOrCreateBerth(bpShenfield, 1354, 442, "SE0569", "SE0602");
        Berths.getOrCreateBerth(bpShenfield, 602,  474, "SE0570", "SE0535");
        Berths.getOrCreateBerth(bpShenfield, 1418, 442, "SE0571");
        Berths.getOrCreateBerth(bpShenfield, 1482, 442, "SE0573");
        Berths.getOrCreateBerth(bpShenfield, 682,  474, "SE0574");
        Berths.getOrCreateBerth(bpShenfield, 1546, 442, "SE0575");
        Berths.getOrCreateBerth(bpShenfield, 746,  474, "SE0576");
        Berths.getOrCreateBerth(bpShenfield, 1610, 442, "SE0577");
        Berths.getOrCreateBerth(bpShenfield, 810,  474, "SE0578");
        Berths.getOrCreateBerth(bpShenfield, 1674, 442, "SE0579");
        Berths.getOrCreateBerth(bpShenfield, 874,  474, "SE0580");
        Berths.getOrCreateBerth(bpShenfield, 1738, 442, "SE0581");
        Berths.getOrCreateBerth(bpShenfield, 938,  474, "SE0582");
        Berths.getOrCreateBerth(bpShenfield, 186,  570, "SE0583");
        Berths.getOrCreateBerth(bpShenfield, 1002, 474, "SE0584");
        Berths.getOrCreateBerth(bpShenfield, 250,  570, "SE0585");
        Berths.getOrCreateBerth(bpShenfield, 1066, 474, "SE0586");
        Berths.getOrCreateBerth(bpShenfield, 314,  570, "SE0587");
        Berths.getOrCreateBerth(bpShenfield, 1130, 474, "SE0588", "SE0551");
        Berths.getOrCreateBerth(bpShenfield, 378,  570, "SE0589");
        Berths.getOrCreateBerth(bpShenfield, 442,  570, "SE0591");
        Berths.getOrCreateBerth(bpShenfield, 1258, 474, "SE0592", "SE0553");
        Berths.getOrCreateBerth(bpShenfield, 506,  570, "SE0593");
        Berths.getOrCreateBerth(bpShenfield, 570,  570, "SE0595");
        Berths.getOrCreateBerth(bpShenfield, 1354, 410, "SE0596");
        Berths.getOrCreateBerth(bpShenfield, 634,  570, "SE0597");
        Berths.getOrCreateBerth(bpShenfield, 714,  570, "SE0603", "SE0636");
        Berths.getOrCreateBerth(bpShenfield, 1354, 474, "SE0604");
        Berths.getOrCreateBerth(bpShenfield, 778,  570, "SE0605");
        Berths.getOrCreateBerth(bpShenfield, 1418, 474, "SE0606");
        Berths.getOrCreateBerth(bpShenfield, 906,  570, "SE0607");
        Berths.getOrCreateBerth(bpShenfield, 1482, 474, "SE0608");
        Berths.getOrCreateBerth(bpShenfield, 970,  570, "SE0609");
        Berths.getOrCreateBerth(bpShenfield, 1546, 474, "SE0610");
        Berths.getOrCreateBerth(bpShenfield, 1034, 570, "SE0611");
        Berths.getOrCreateBerth(bpShenfield, 1610, 474, "SE0612");
        Berths.getOrCreateBerth(bpShenfield, 1674, 474, "SE0614");
        Berths.getOrCreateBerth(bpShenfield, 1098, 570, "SE0615");
        Berths.getOrCreateBerth(bpShenfield, 1738, 474, "SE0616");
        Berths.getOrCreateBerth(bpShenfield, 186,  602, "SE0618");
        Berths.getOrCreateBerth(bpShenfield, 1162, 570, "SE0619", "SE0652");
        Berths.getOrCreateBerth(bpShenfield, 250,  602, "SE0620");
        Berths.getOrCreateBerth(bpShenfield, 1226, 570, "SE0621");
        Berths.getOrCreateBerth(bpShenfield, 314,  602, "SE0622");
        Berths.getOrCreateBerth(bpShenfield, 1290, 570, "SE0623");
        Berths.getOrCreateBerth(bpShenfield, 378,  602, "SE0624");
        Berths.getOrCreateBerth(bpShenfield, 1354, 570, "SE0625");
        Berths.getOrCreateBerth(bpShenfield, 442,  602, "SE0626");
        Berths.getOrCreateBerth(bpShenfield, 506,  602, "SE0628");
        Berths.getOrCreateBerth(bpShenfield, 1418, 570, "SE0629");
        Berths.getOrCreateBerth(bpShenfield, 570,  602, "SE0630");
        Berths.getOrCreateBerth(bpShenfield, 634,  602, "SE0634", "SE0599");
        Berths.getOrCreateBerth(bpShenfield, 714,  602, "SE0638");
        Berths.getOrCreateBerth(bpShenfield, 186,  698, "SE0639");
        Berths.getOrCreateBerth(bpShenfield, 778,  602, "SE0640");
        Berths.getOrCreateBerth(bpShenfield, 842,  602, "SE0642");
        Berths.getOrCreateBerth(bpShenfield, 906,  602, "SE0644");
        Berths.getOrCreateBerth(bpShenfield, 970,  602, "SE0646");
        Berths.getOrCreateBerth(bpShenfield, 1034, 602, "SE0648", "SE0613");
        Berths.getOrCreateBerth(bpShenfield, 250,  698, "SE0649");
        Berths.getOrCreateBerth(bpShenfield, 1098, 602, "SE0650");
        Berths.getOrCreateBerth(bpShenfield, 314,  698, "SE0651", "SE0692");
        Berths.getOrCreateBerth(bpShenfield, 1162, 602, "SE0654");
        Berths.getOrCreateBerth(bpShenfield, 1226, 602, "SE0658");
        Berths.getOrCreateBerth(bpShenfield, 1290, 602, "SE0660");
        Berths.getOrCreateBerth(bpShenfield, 1354, 602, "SE0662", "SE0627");
        Berths.getOrCreateBerth(bpShenfield, 1418, 602, "SE0664", "SE0631");
        Berths.getOrCreateBerth(bpShenfield, 1530, 514, "SE0666", "SEDNSS").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1546, 538, "SEA668");
        Berths.getOrCreateBerth(bpShenfield, 1546, 570, "SEA670");
        Berths.getOrCreateBerth(bpShenfield, 1546, 602, "SEA672");
        Berths.getOrCreateBerth(bpShenfield, 1546, 634, "SEA674");
        Berths.getOrCreateBerth(bpShenfield, 1610, 538, "SEB668");
        Berths.getOrCreateBerth(bpShenfield, 1610, 570, "SEB670");
        Berths.getOrCreateBerth(bpShenfield, 1610, 602, "SEB672");
        Berths.getOrCreateBerth(bpShenfield, 1610, 634, "SEB674");
        Berths.getOrCreateBerth(bpShenfield, 1674, 538, "SEC668");
        Berths.getOrCreateBerth(bpShenfield, 1674, 570, "SEC670");
        Berths.getOrCreateBerth(bpShenfield, 1674, 602, "SEC672");
        Berths.getOrCreateBerth(bpShenfield, 1674, 634, "SEC674");
      /*Berths.getOrCreateBerth(pnlShenfield, 1546, 538, "SEF668");
        Berths.getOrCreateBerth(pnlShenfield, 1546, 570, "SEF670");
        Berths.getOrCreateBerth(pnlShenfield, 1546, 602, "SEF672");
        Berths.getOrCreateBerth(pnlShenfield, 1546, 634, "SEF674");*/
        Berths.getOrCreateBerth(bpShenfield, 1738, 538, "SER668").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1738, 570, "SER670").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1738, 602, "SER672").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1738, 634, "SER674").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 314,  730, "SE0694");
        Berths.getOrCreateBerth(bpShenfield, 378,  698, "SE0696");
        Berths.getOrCreateBerth(bpShenfield, 474,  698, "SEBURN");
        Berths.getOrCreateBerth(bpShenfield, 538,  698, "SE0708");
        Berths.getOrCreateBerth(bpShenfield, 1194, 506, "SE5146");
        Berths.getOrCreateBerth(bpShenfield, 1162, 410, "SE5157");
        Berths.getOrCreateBerth(bpShenfield, 602,  698, "SER708").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1090, 410, "SEWDNS").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 538,  722, "SEX708");
        Berths.getOrCreateBerth(bpShenfield, 1434, 514, "SE5165", "SEDNSN").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1434, 658, "SE5167", "SEUPSN").hasBorder();
        Berths.getOrCreateBerth(bpShenfield, 1530, 658, "SE5170", "SEUPSS").hasBorder();
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpShenfield, 745,  130, "BRENTWOOD",     "BRE");
        smallStation(bpShenfield, 289,  130, "HAROLD WOOD",   "HRO");
        smallStation(bpShenfield, 1207, 130, "SHENFIELD",     "SNF");

        smallStation(bpShenfield, 596,  418, "BILLERICAY",    "BIC");
        smallStation(bpShenfield, 1258, 394, "WICKFORD",      "WIC");

        smallStation(bpShenfield, 1165, 546+99, "AIRPORT",       "SIA"); // SOUTHEND
        smallStation(bpShenfield, 637,  546+88, "HOCKLEY",       "HOC");
        smallStation(bpShenfield, 1345, 546+88, "PRITTLEWELL",   "PRL");
        smallStation(bpShenfield, 186,  546+88, "RAYLEIGH",      "RLG");
        smallStation(bpShenfield, 1034, 546+88, "ROCHFORD",      "RFD");
        smallStation(bpShenfield, 1162, 538+99, "SOUTHEND",      "SIA"); // AIRPORT
        smallStation(bpShenfield, 1790, 586, "SOUTHEND",      "SOV"); // VICTORIA
        smallStation(bpShenfield, 1790, 594, "VICTORIA",      "SOV"); // SOUTHEND

        smallStation(bpShenfield, 418,  674, "ALTHORNE",      "ALN");
        smallStation(bpShenfield, 163,  730, "BATTLESBRIDGE", "BLB");
        smallStation(bpShenfield, 474,  730, "BURNHAM-",      "BUU"); // ON-CROUCH
        smallStation(bpShenfield, 229,  674, "FERRERS",       "SOF"); // S WOODHAM
        smallStation(bpShenfield, 305,  762, "N FAMBRIDGE",   "NFA");
        smallStation(bpShenfield, 471,  738, "ON-CROUCH",     "BUU"); // BURNHAM-
        smallStation(bpShenfield, 223,  666, "S WOODHAM",     "SOF"); // FERRERS
        smallStation(bpShenfield, 526,  674, "SOUTHMINSTER",  "SMN");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpShenfield, 1674, 234, "WITHAM", 4);
        makeNavButton(bpShenfield, 112,  202, "ILFORD", 2);
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Ilford">
    private void initIlford()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpIlford, 234,  122, "SI0383");
        Berths.getOrCreateBerth(bpIlford, 298,  122, "SI0385");
        Berths.getOrCreateBerth(bpIlford, 362,  122, "SI0387");

        /**
         * For new Chadwell Heath turnback sidings (OOU until April 2015)
         */
        Berths.getOrCreateBerth(bpIlford, 426,  138, "XXCTS1");
        Berths.getOrCreateBerth(bpIlford, 482,  138, "XXCTS2").hasBorder();

        Berths.getOrCreateBerth(bpIlford, 362,  186, "SI0389");
        Berths.getOrCreateBerth(bpIlford, 426,  122, "SI0391");
        Berths.getOrCreateBerth(bpIlford, 490,  122, "SI0393");
        Berths.getOrCreateBerth(bpIlford, 490,  186, "SI0395");
        Berths.getOrCreateBerth(bpIlford, 554,  122, "SI0397");
        Berths.getOrCreateBerth(bpIlford, 618,  122, "SI0399");
        Berths.getOrCreateBerth(bpIlford, 618,  186, "SI0401");
        Berths.getOrCreateBerth(bpIlford, 682,  122, "SI0403");
        Berths.getOrCreateBerth(bpIlford, 682,  186, "SI0405");
        Berths.getOrCreateBerth(bpIlford, 746,  122, "SI0407");
        Berths.getOrCreateBerth(bpIlford, 810,  170, "SI0409");
        Berths.getOrCreateBerth(bpIlford, 234,  154, "SI0410");
        Berths.getOrCreateBerth(bpIlford, 810,  186, "SI0411");
        Berths.getOrCreateBerth(bpIlford, 298,  154, "SI0412");
        Berths.getOrCreateBerth(bpIlford, 810,  122, "SI0413");
        Berths.getOrCreateBerth(bpIlford, 362,  154, "SI0414");
        Berths.getOrCreateBerth(bpIlford, 874,  122, "SI0415");
        Berths.getOrCreateBerth(bpIlford, 362,  218, "SI0416", "SI5101");
        Berths.getOrCreateBerth(bpIlford, 954,  122, "SI0417", "SI5102");
        Berths.getOrCreateBerth(bpIlford, 426,  154, "SI0418");
        Berths.getOrCreateBerth(bpIlford, 954,  186, "SI0419", "SI5100");
        Berths.getOrCreateBerth(bpIlford, 490,  154, "SI0420");
        Berths.getOrCreateBerth(bpIlford, 1018, 122, "SI0421");
        Berths.getOrCreateBerth(bpIlford, 554,  154, "SI0422");
        Berths.getOrCreateBerth(bpIlford, 1082, 122, "SI0423");
        Berths.getOrCreateBerth(bpIlford, 554,  218, "SI0424");
        Berths.getOrCreateBerth(bpIlford, 1082, 186, "SI0425");
        Berths.getOrCreateBerth(bpIlford, 618,  154, "SI0426");
        Berths.getOrCreateBerth(bpIlford, 1082, 122, "SI0427");
        Berths.getOrCreateBerth(bpIlford, 682,  154, "SI0428");
        Berths.getOrCreateBerth(bpIlford, 1210, 186, "SI0429");
        Berths.getOrCreateBerth(bpIlford, 682,  218, "SI0430");
        Berths.getOrCreateBerth(bpIlford, 1274, 122, "SI0431", "SI0458");
        Berths.getOrCreateBerth(bpIlford, 746,  154, "SI0432");
        Berths.getOrCreateBerth(bpIlford, 1370, 122, "SI0433");
        Berths.getOrCreateBerth(bpIlford, 1370, 186, "SI0435");
        Berths.getOrCreateBerth(bpIlford, 810,  154, "SI0436");
        Berths.getOrCreateBerth(bpIlford, 794,  218, "SI0438");
        Berths.getOrCreateBerth(bpIlford, 1434, 82,  "SI0439");
        Berths.getOrCreateBerth(bpIlford, 874,  154, "SI0440", "SI5139");
        Berths.getOrCreateBerth(bpIlford, 1578, 122, "SI0441", "SI5116");
        Berths.getOrCreateBerth(bpIlford, 954,  154, "SI0442");
        Berths.getOrCreateBerth(bpIlford, 1578, 186, "SI0443");
        Berths.getOrCreateBerth(bpIlford, 954,  218, "SI0444", "SI5111");
        Berths.getOrCreateBerth(bpIlford, 1018, 154, "SI0446");
        Berths.getOrCreateBerth(bpIlford, 1082, 154, "SI0448");
        Berths.getOrCreateBerth(bpIlford, 1130, 250, "SI0450");
        Berths.getOrCreateBerth(bpIlford, 1082, 154, "SI0452");
        Berths.getOrCreateBerth(bpIlford, 1082, 218, "SI0454");
        Berths.getOrCreateBerth(bpIlford, 1210, 154, "SI0456");
        Berths.getOrCreateBerth(bpIlford, 1274, 154, "SI0460", "SI5113");
        Berths.getOrCreateBerth(bpIlford, 1274, 218, "SI0462");
        Berths.getOrCreateBerth(bpIlford, 1354, 82,  "SI0464");
        Berths.getOrCreateBerth(bpIlford, 1370, 138, "SI0468");
        Berths.getOrCreateBerth(bpIlford, 1370, 154, "SI0470");
        Berths.getOrCreateBerth(bpIlford, 1370, 218, "SI0472");
        Berths.getOrCreateBerth(bpIlford, 1578, 154, "SI0474");
        Berths.getOrCreateBerth(bpIlford, 1578, 218, "SI0476");
        Berths.getOrCreateBerth(bpIlford, 298,  250, "SI5099");
        Berths.getOrCreateBerth(bpIlford, 746,  170, "SI5104");
        Berths.getOrCreateBerth(bpIlford, 826,  234, "SI5105");
        Berths.getOrCreateBerth(bpIlford, 858,  218, "SI5107");
        Berths.getOrCreateBerth(bpIlford, 1514, 106, "SI5114");
        Berths.getOrCreateBerth(bpIlford, 1434, 154, "SI5125");
        Berths.getOrCreateBerth(bpIlford, 226,  250, "SIRCHS").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1570, 106, "SIRGP1").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1426, 138, "SIRGP2").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1360, 26,  "SIRGP3");
        Berths.getOrCreateBerth(bpIlford, 1412, 26,  "SIRGP4");
        Berths.getOrCreateBerth(bpIlford, 1360, 46,  "SIRGP5");
        Berths.getOrCreateBerth(bpIlford, 1412, 46,  "SIRGP6");
        Berths.getOrCreateBerth(bpIlford, 1434, 102, "SIRGPC").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1354, 102, "SIRGPL").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1002, 250, "SIROM1");
        Berths.getOrCreateBerth(bpIlford, 826,  274, "SIRRED").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 938,  250, "SIRROM").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1002, 282, "SIXROM");
        Berths.getOrCreateBerth(bpIlford, 1194, 250, "SIUMIN").hasBorder();

        Berths.getOrCreateBerth(bpIlford, 218,  394, "SI0321");
        Berths.getOrCreateBerth(bpIlford, 218,  458, "SI0323");
        Berths.getOrCreateBerth(bpIlford, 282,  458, "SI0325");
        Berths.getOrCreateBerth(bpIlford, 346,  458, "SI0327");
        Berths.getOrCreateBerth(bpIlford, 474,  394, "SI0329");
        Berths.getOrCreateBerth(bpIlford, 474,  458, "SI0331");
        Berths.getOrCreateBerth(bpIlford, 538,  458, "SI0333");
        Berths.getOrCreateBerth(bpIlford, 537,  362, "SI0335");
        Berths.getOrCreateBerth(bpIlford, 282,  522, "SI0336");
        Berths.getOrCreateBerth(bpIlford, 346,  490, "SI0338");
        Berths.getOrCreateBerth(bpIlford, 618,  410, "SI0339");
        Berths.getOrCreateBerth(bpIlford, 346,  426, "SI0340");
        Berths.getOrCreateBerth(bpIlford, 665,  362, "SI0341");
        Berths.getOrCreateBerth(bpIlford, 666,  394, "SI0343");
        Berths.getOrCreateBerth(bpIlford, 666,  458, "SI0345");
        Berths.getOrCreateBerth(bpIlford, 410,  490, "SI0346");
        Berths.getOrCreateBerth(bpIlford, 762,  394, "SI0347");
        Berths.getOrCreateBerth(bpIlford, 826,  394, "SI0349", "SI0368");
        Berths.getOrCreateBerth(bpIlford, 474,  490, "SI0350");
        Berths.getOrCreateBerth(bpIlford, 890,  458, "SI0351");
        Berths.getOrCreateBerth(bpIlford, 538,  490, "SI0352");
        Berths.getOrCreateBerth(bpIlford, 890,  394, "SI0353");
        Berths.getOrCreateBerth(bpIlford, 538,  522, "SI0354", "SI0337");
        Berths.getOrCreateBerth(bpIlford, 1082, 394, "SI0355");
        Berths.getOrCreateBerth(bpIlford, 538,  426, "SI0356");
        Berths.getOrCreateBerth(bpIlford, 1146, 394, "SI0357");
        Berths.getOrCreateBerth(bpIlford, 586,  474, "SI0358");
        Berths.getOrCreateBerth(bpIlford, 1146, 458, "SI0359");
        Berths.getOrCreateBerth(bpIlford, 666,  522, "SI0360");
        Berths.getOrCreateBerth(bpIlford, 1193, 330, "SI0361");
        Berths.getOrCreateBerth(bpIlford, 826,  426, "SI0362");
        Berths.getOrCreateBerth(bpIlford, 1274, 394, "SI0363", "SI5088");
        Berths.getOrCreateBerth(bpIlford, 826,  490, "SI0364");
        Berths.getOrCreateBerth(bpIlford, 1338, 394, "SI0365");
        Berths.getOrCreateBerth(bpIlford, 1338, 458, "SI0369");
        Berths.getOrCreateBerth(bpIlford, 890,  426, "SI0370");
        Berths.getOrCreateBerth(bpIlford, 1402, 394, "SI0371");
        Berths.getOrCreateBerth(bpIlford, 954,  426, "SI0372");
        Berths.getOrCreateBerth(bpIlford, 1466, 394, "SI0373");
        Berths.getOrCreateBerth(bpIlford, 1082, 426, "SI0374");
        Berths.getOrCreateBerth(bpIlford, 1530, 394, "SI0375");
        Berths.getOrCreateBerth(bpIlford, 1082, 490, "SI0376");
        Berths.getOrCreateBerth(bpIlford, 1658, 394, "SI0377");
        Berths.getOrCreateBerth(bpIlford, 1106, 310, "SI0378").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1658, 458, "SI0379");
        Berths.getOrCreateBerth(bpIlford, 1106, 330, "SI0380").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1722, 394, "SI0381");
        Berths.getOrCreateBerth(bpIlford, 1106, 350, "SI0382").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1106, 370, "SI0384").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 1146, 426, "SI0386");
        Berths.getOrCreateBerth(bpIlford, 1210, 426, "SI0388");
        Berths.getOrCreateBerth(bpIlford, 1210, 490, "SI0390");
        Berths.getOrCreateBerth(bpIlford, 1338, 426, "SI0392", "SI0367");
        Berths.getOrCreateBerth(bpIlford, 1402, 426, "SI0394");
        Berths.getOrCreateBerth(bpIlford, 1466, 426, "SI0396");
        Berths.getOrCreateBerth(bpIlford, 1530, 426, "SI0398");
        Berths.getOrCreateBerth(bpIlford, 1530, 490, "SI0400");
        Berths.getOrCreateBerth(bpIlford, 1594, 426, "SI0402");
        Berths.getOrCreateBerth(bpIlford, 1658, 426, "SI0404");
        Berths.getOrCreateBerth(bpIlford, 1722, 426, "SI0406");
        Berths.getOrCreateBerth(bpIlford, 1722, 490, "SI0408");
        Berths.getOrCreateBerth(bpIlford, 629,  557, "SI5078");
        Berths.getOrCreateBerth(bpIlford, 629,  577, "SI5080");
        Berths.getOrCreateBerth(bpIlford, 629,  597, "SI5082");
        Berths.getOrCreateBerth(bpIlford, 629,  617, "SI5084");
        Berths.getOrCreateBerth(bpIlford, 629,  637, "SI5086");
        Berths.getOrCreateBerth(bpIlford, 665,  426, "SI5087");
        Berths.getOrCreateBerth(bpIlford, 761,  362, "SIA366");
        Berths.getOrCreateBerth(bpIlford, 818,  362, "SIB366");
        Berths.getOrCreateBerth(bpIlford, 1257, 362, "SIICIN").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 881,  362, "SIR366").hasBorder();
        Berths.getOrCreateBerth(bpIlford, 681,  557, "SIRAS1");
        Berths.getOrCreateBerth(bpIlford, 681,  577, "SIRAS2");
        Berths.getOrCreateBerth(bpIlford, 681,  597, "SIRAS3");
        Berths.getOrCreateBerth(bpIlford, 681,  617, "SIRAS4");
        Berths.getOrCreateBerth(bpIlford, 681,  637, "SIRAS5");
        Berths.getOrCreateBerth(bpIlford, 1193, 362, "SIRIFC");
        Berths.getOrCreateBerth(bpIlford, 1017, 378, "SIRIFL");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(bpIlford, 1268, 106, "GIDEA PARK",  "GDP");
        smallStation(bpIlford, 957,  98,  "ROMFORD",     "RMF");
        smallStation(bpIlford, 362,  90,  "CHADWELL",    "CTH");
        smallStation(bpIlford, 371,  98,  "HEATH",       "CTH");
        smallStation(bpIlford, 1654, 370, "GOODMAYES",   "GMY");
        smallStation(bpIlford, 1328, 370, "SEVEN KINGS", "SVK");
        smallStation(bpIlford, 831,  346, "ILFORD",      "IFD");
        smallStation(bpIlford, 339,  370, "MANOR PARK",  "MNP");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpIlford, 1674, 170, "SHENFIELD", 3);
        makeNavButton(bpIlford, 61,   442, "STRATFORD", 1);
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Stratford + Liv St">
    private void initStratford()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        Berths.getOrCreateBerth(bpStratford, 42,   186, "LSR009").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  186, "LSFC09");
        Berths.getOrCreateBerth(bpStratford, 170,  186, "LSFB09");
        Berths.getOrCreateBerth(bpStratford, 234,  186, "LSFA09");

        Berths.getOrCreateBerth(bpStratford, 42,   218, "LSR011").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  218, "LSFC11");
        Berths.getOrCreateBerth(bpStratford, 170,  218, "LSFB11");
        Berths.getOrCreateBerth(bpStratford, 234,  218, "LSFA11");

        Berths.getOrCreateBerth(bpStratford, 42,   250, "LSR013").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  250, "LSFC13");
        Berths.getOrCreateBerth(bpStratford, 170,  250, "LSFB13");
        Berths.getOrCreateBerth(bpStratford, 234,  250, "LSFA13");

        Berths.getOrCreateBerth(bpStratford, 42,   282, "LSR015").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  282, "LSFC15");
        Berths.getOrCreateBerth(bpStratford, 170,  282, "LSFB15");
        Berths.getOrCreateBerth(bpStratford, 234,  282, "LSFA15");

        Berths.getOrCreateBerth(bpStratford, 42,   314, "LSR017").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  314, "LSFC17");
        Berths.getOrCreateBerth(bpStratford, 170,  314, "LSFB17");
        Berths.getOrCreateBerth(bpStratford, 234,  314, "LSFA17");

        Berths.getOrCreateBerth(bpStratford, 42,   346, "LSR019").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  346, "LSFC19");
        Berths.getOrCreateBerth(bpStratford, 170,  346, "LSFB19");
        Berths.getOrCreateBerth(bpStratford, 234,  346, "LSFA19");

        Berths.getOrCreateBerth(bpStratford, 42,   378, "LSR021").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  378, "LSFC21");
        Berths.getOrCreateBerth(bpStratford, 170,  378, "LSFB21");
        Berths.getOrCreateBerth(bpStratford, 234,  378, "LSFA21");

        Berths.getOrCreateBerth(bpStratford, 42,   410, "LSR023").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  410, "LSFC23");
        Berths.getOrCreateBerth(bpStratford, 170,  410, "LSFB23");
        Berths.getOrCreateBerth(bpStratford, 234,  410, "LSFA23");

        Berths.getOrCreateBerth(bpStratford, 42,   442, "LSR025").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  442, "LSFC25");
        Berths.getOrCreateBerth(bpStratford, 170,  442, "LSFB25");
        Berths.getOrCreateBerth(bpStratford, 234,  442, "LSFA25");
        Berths.getOrCreateBerth(bpStratford, 330,  442, "LS0027");

        Berths.getOrCreateBerth(bpStratford, 42,   474, "LSR029").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  474, "LSFC29");
        Berths.getOrCreateBerth(bpStratford, 170,  474, "LSFB29");
        Berths.getOrCreateBerth(bpStratford, 234,  474, "LSFA29");
        Berths.getOrCreateBerth(bpStratford, 330,  474, "LS0031");

        Berths.getOrCreateBerth(bpStratford, 42,   506, "LSR033").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  506, "LSFC33");
        Berths.getOrCreateBerth(bpStratford, 170,  506, "LSFB33");
        Berths.getOrCreateBerth(bpStratford, 234,  506, "LSFA33");
        Berths.getOrCreateBerth(bpStratford, 298,  506, "LS0035");

        Berths.getOrCreateBerth(bpStratford, 42,   538, "LSR037").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  538, "LSFC37");
        Berths.getOrCreateBerth(bpStratford, 170,  538, "LSFB37");
        Berths.getOrCreateBerth(bpStratford, 234,  538, "LSFA37");
        Berths.getOrCreateBerth(bpStratford, 298,  538, "LS0039");

        Berths.getOrCreateBerth(bpStratford, 42,   570, "LSR041").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  570, "LSFC41");
        Berths.getOrCreateBerth(bpStratford, 170,  570, "LSFB41");
        Berths.getOrCreateBerth(bpStratford, 234,  570, "LSFA41");

        Berths.getOrCreateBerth(bpStratford, 42,   602, "LSR043").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  602, "LSFC43");
        Berths.getOrCreateBerth(bpStratford, 170,  602, "LSFB43");
        Berths.getOrCreateBerth(bpStratford, 234,  602, "LSFA43");

        Berths.getOrCreateBerth(bpStratford, 42,   634, "LSR045").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  634, "LSFC45");
        Berths.getOrCreateBerth(bpStratford, 170,  634, "LSFB45");
        Berths.getOrCreateBerth(bpStratford, 234,  634, "LSFA45");
        Berths.getOrCreateBerth(bpStratford, 282,  634, "LS0047");

        Berths.getOrCreateBerth(bpStratford, 42,   666, "LSR049").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  666, "LSFC49");
        Berths.getOrCreateBerth(bpStratford, 170,  666, "LSFB49");
        Berths.getOrCreateBerth(bpStratford, 234,  666, "LSFA49");

        Berths.getOrCreateBerth(bpStratford, 42,   698, "LSR051").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  698, "LSFC51");
        Berths.getOrCreateBerth(bpStratford, 170,  698, "LSFB51");
        Berths.getOrCreateBerth(bpStratford, 234,  698, "LSFA51");

        Berths.getOrCreateBerth(bpStratford, 42,   730, "LSR053").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 106,  730, "LSFC53");
        Berths.getOrCreateBerth(bpStratford, 170,  730, "LSFB53");
        Berths.getOrCreateBerth(bpStratford, 234,  730, "LSFA53");

        Berths.getOrCreateBerth(bpStratford, 522,  474, "LS0054");
        Berths.getOrCreateBerth(bpStratford, 522,  442, "LS0055");
        Berths.getOrCreateBerth(bpStratford, 442,  506, "LS0056");
        Berths.getOrCreateBerth(bpStratford, 522,  570, "LS0057");
        Berths.getOrCreateBerth(bpStratford, 442,  602, "LS0058");
        Berths.getOrCreateBerth(bpStratford, 506,  634, "LS0059");
        Berths.getOrCreateBerth(bpStratford, 458,  666, "LS0060");
        Berths.getOrCreateBerth(bpStratford, 586,  442, "LS0061");
        Berths.getOrCreateBerth(bpStratford, 586,  570, "LS0063");
        Berths.getOrCreateBerth(bpStratford, 586,  474, "LS0064");
        Berths.getOrCreateBerth(bpStratford, 570,  634, "LS0065");
        Berths.getOrCreateBerth(bpStratford, 522,  602, "LS0066");
        Berths.getOrCreateBerth(bpStratford, 522,  666, "LS0068");
        Berths.getOrCreateBerth(bpStratford, 650,  474, "LS0070");
        Berths.getOrCreateBerth(bpStratford, 650,  442, "LS0071");
        Berths.getOrCreateBerth(bpStratford, 650,  602, "LS0072");
        Berths.getOrCreateBerth(bpStratford, 650,  570, "LS0073");
        Berths.getOrCreateBerth(bpStratford, 650,  666, "LS0074");
        Berths.getOrCreateBerth(bpStratford, 650,  634, "LS0075");
        Berths.getOrCreateBerth(bpStratford, 730,  474, "LS0080");
        Berths.getOrCreateBerth(bpStratford, 730,  442, "LS0081");
        Berths.getOrCreateBerth(bpStratford, 730,  546, "LS0082");
        Berths.getOrCreateBerth(bpStratford, 730,  570, "LS0083");
        Berths.getOrCreateBerth(bpStratford, 730,  602, "LS0084");
        Berths.getOrCreateBerth(bpStratford, 730,  634, "LS0085");
        Berths.getOrCreateBerth(bpStratford, 730,  666, "LS0086");
        Berths.getOrCreateBerth(bpStratford, 842,  474, "LS0090");
        Berths.getOrCreateBerth(bpStratford, 842,  442, "LS0091");
        Berths.getOrCreateBerth(bpStratford, 842,  602, "LS0092");
        Berths.getOrCreateBerth(bpStratford, 842,  570, "LS0093");
        Berths.getOrCreateBerth(bpStratford, 842,  666, "LS0094");
        Berths.getOrCreateBerth(bpStratford, 842,  634, "LS0095");
        Berths.getOrCreateBerth(bpStratford, 954,  474, "LS0100");
        Berths.getOrCreateBerth(bpStratford, 1034, 442, "LS0101");
        Berths.getOrCreateBerth(bpStratford, 954,  538, "LS0102");
        Berths.getOrCreateBerth(bpStratford, 1034, 506, "LS0103");
        Berths.getOrCreateBerth(bpStratford, 1034, 474, "LS0110");
        Berths.getOrCreateBerth(bpStratford, 1034, 538, "LS0112");
        Berths.getOrCreateBerth(bpStratford, 970,  602, "LS0200");
        Berths.getOrCreateBerth(bpStratford, 1034, 570, "LS0201");
        Berths.getOrCreateBerth(bpStratford, 970,  666, "LS0202");
        Berths.getOrCreateBerth(bpStratford, 1034, 634, "LS0203");
        Berths.getOrCreateBerth(bpStratford, 1034, 602, "LS0210");
        Berths.getOrCreateBerth(bpStratford, 1034, 666, "LS0212");

        Berths.getOrCreateBerth(bpStratford, 1098, 602, "SI0220");
        Berths.getOrCreateBerth(bpStratford, 1098, 570, "SI0221");
        Berths.getOrCreateBerth(bpStratford, 1098, 666, "SI0222");
        Berths.getOrCreateBerth(bpStratford, 1098, 634, "SI0223");
        Berths.getOrCreateBerth(bpStratford, 1178, 602, "SI0224");
        Berths.getOrCreateBerth(bpStratford, 1178, 570, "SI0225");
        Berths.getOrCreateBerth(bpStratford, 1178, 666, "SI0226");
        Berths.getOrCreateBerth(bpStratford, 1178, 634, "SI0227");
        Berths.getOrCreateBerth(bpStratford, 1242, 570, "SI0229");
        Berths.getOrCreateBerth(bpStratford, 1242, 602, "SI0228");
        Berths.getOrCreateBerth(bpStratford, 1242, 666, "SI0230");
        Berths.getOrCreateBerth(bpStratford, 1242, 634, "SI0231");
        Berths.getOrCreateBerth(bpStratford, 1354, 538, "SI0234", "SI0239");
        Berths.getOrCreateBerth(bpStratford, 1274, 698, "SI0235");
        Berths.getOrCreateBerth(bpStratford, 1354, 506, "SI0237");
        Berths.getOrCreateBerth(bpStratford, 1354, 570, "SI0241", "SI0240");
        Berths.getOrCreateBerth(bpStratford, 1354, 602, "SI0242");
        Berths.getOrCreateBerth(bpStratford, 1354, 666, "SI0244");
        Berths.getOrCreateBerth(bpStratford, 1338, 634, "SI0245");
        Berths.getOrCreateBerth(bpStratford, 1434, 506, "SI0249", "SI5070");
        Berths.getOrCreateBerth(bpStratford, 1434, 538, "SI0251");
        Berths.getOrCreateBerth(bpStratford, 1514, 538, "SI0250", "SI0261");
        Berths.getOrCreateBerth(bpStratford, 1434, 570, "SI0253", "SI0240");
        Berths.getOrCreateBerth(bpStratford, 1402, 634, "SI0255");
        Berths.getOrCreateBerth(bpStratford, 1514, 602, "SI0254", "SI0265");
        Berths.getOrCreateBerth(bpStratford, 1466, 666, "SI0256");
        Berths.getOrCreateBerth(bpStratford, 1466, 634, "SI0257");
        Berths.getOrCreateBerth(bpStratford, 1482, 490, "SI0258");
        Berths.getOrCreateBerth(bpStratford, 1514, 506, "SI0259", "SI0248");
        Berths.getOrCreateBerth(bpStratford, 1610, 570, "SI0262");
        Berths.getOrCreateBerth(bpStratford, 1514, 570, "SI0263", "SI0252");
        Berths.getOrCreateBerth(bpStratford, 1610, 602, "SI0264");
        Berths.getOrCreateBerth(bpStratford, 1610, 634, "SI0266");
        Berths.getOrCreateBerth(bpStratford, 1546, 634, "SI0267");
        Berths.getOrCreateBerth(bpStratford, 1610, 682, "SI0268");
        Berths.getOrCreateBerth(bpStratford, 1582, 474, "SI0269", "SI1294");
        Berths.getOrCreateBerth(bpStratford, 1658, 570, "SI0273", "SI0270");
        Berths.getOrCreateBerth(bpStratford, 1658, 474, "SI0274");
        Berths.getOrCreateBerth(bpStratford, 1658, 602, "SI0275");
        Berths.getOrCreateBerth(bpStratford, 1658, 506, "SI0276");
        Berths.getOrCreateBerth(bpStratford, 1658, 634, "SI0277");
        Berths.getOrCreateBerth(bpStratford, 1658, 538, "SI0278");
        Berths.getOrCreateBerth(bpStratford, 1658, 682, "SI0279");
        Berths.getOrCreateBerth(bpStratford, 1706, 538, "SI0281");
        Berths.getOrCreateBerth(bpStratford, 1722, 634, "SI0283");
        Berths.getOrCreateBerth(bpStratford, 1722, 682, "SI0284");
        Berths.getOrCreateBerth(bpStratford, 778,  330, "SI0285");
        Berths.getOrCreateBerth(bpStratford, 714,  362, "SI0286");
        Berths.getOrCreateBerth(bpStratford, 778,  362, "SI0288");
        Berths.getOrCreateBerth(bpStratford, 842,  330, "SI0287");
        Berths.getOrCreateBerth(bpStratford, 906,  266, "SI0289", "SI0290");
        Berths.getOrCreateBerth(bpStratford, 906,  298, "SI0292", "SI0291");
        Berths.getOrCreateBerth(bpStratford, 970,  330, "SI0293");
        Berths.getOrCreateBerth(bpStratford, 906,  330, "SI0294");
        Berths.getOrCreateBerth(bpStratford, 746,  234, "SI0295");
        Berths.getOrCreateBerth(bpStratford, 906,  362, "SI0296");
        Berths.getOrCreateBerth(bpStratford, 1098, 330, "SI0297", "SI0308");
        Berths.getOrCreateBerth(bpStratford, 970,  362, "SI0298");
        Berths.getOrCreateBerth(bpStratford, 1162, 266, "SI0299");
        Berths.getOrCreateBerth(bpStratford, 1034, 362, "SI0300");
        Berths.getOrCreateBerth(bpStratford, 1162, 330, "SI0303");
        Berths.getOrCreateBerth(bpStratford, 1098, 266, "SI0304");
        Berths.getOrCreateBerth(bpStratford, 1226, 330, "SI0305");
        Berths.getOrCreateBerth(bpStratford, 1162, 298, "SI0306", "SI0301");
        Berths.getOrCreateBerth(bpStratford, 1290, 330, "SI0307");
        Berths.getOrCreateBerth(bpStratford, 1354, 266, "SI0309");
        Berths.getOrCreateBerth(bpStratford, 1162, 362, "SI0310");
        Berths.getOrCreateBerth(bpStratford, 1354, 330, "SI0313");
        Berths.getOrCreateBerth(bpStratford, 1290, 362, "SI0314");
        Berths.getOrCreateBerth(bpStratford, 1482, 330, "SI0317", "SI0328");
        Berths.getOrCreateBerth(bpStratford, 1354, 298, "SI0316", "SI0311");
        Berths.getOrCreateBerth(bpStratford, 1354, 362, "SI0320", "SI0315");
        Berths.getOrCreateBerth(bpStratford, 1098, 362, "SI0324");
        Berths.getOrCreateBerth(bpStratford, 1482, 298, "SI0326");
        Berths.getOrCreateBerth(bpStratford, 1482, 362, "SI0330", "SI0319");
        Berths.getOrCreateBerth(bpStratford, 1562, 378, "SI0332");
        Berths.getOrCreateBerth(bpStratford, 1514, 378, "SI0334");
        Berths.getOrCreateBerth(bpStratford, 1546, 394, "SI0909");
        Berths.getOrCreateBerth(bpStratford, 1498, 394, "SI0911");
        Berths.getOrCreateBerth(bpStratford, 1546, 490, "SI1292");
        Berths.getOrCreateBerth(bpStratford, 1482, 450, "SI1296").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 1258, 74,  "SI1424");
        Berths.getOrCreateBerth(bpStratford, 1370, 474, "SI5065");
        Berths.getOrCreateBerth(bpStratford, 1298, 474, "SILSBY").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 1274, 738, "SIU518").hasBorder();

        Berths.getOrCreateBerth(bpStratford, 1610, 186, "SI0032");
        Berths.getOrCreateBerth(bpStratford, 1786, 90,  "SI1003").hasBorder();
        Berths.getOrCreateBerth(bpStratford, 1226, 122, "SIS700");
        Berths.getOrCreateBerth(bpStratford, 1226, 90,  "SIS701");
        Berths.getOrCreateBerth(bpStratford, 1290, 58,  "SIS703");
        Berths.getOrCreateBerth(bpStratford, 1338, 122, "SIS704");
        Berths.getOrCreateBerth(bpStratford, 1338, 90,  "SIS705");
        Berths.getOrCreateBerth(bpStratford, 1482, 154, "SIS706");
        Berths.getOrCreateBerth(bpStratford, 1402, 90,  "SIS707");
        Berths.getOrCreateBerth(bpStratford, 1482, 186, "SIS708");
        Berths.getOrCreateBerth(bpStratford, 1482, 122, "SIS712");
        Berths.getOrCreateBerth(bpStratford, 1546, 90,  "SIS713");
        Berths.getOrCreateBerth(bpStratford, 1546, 154, "SIS715");
        Berths.getOrCreateBerth(bpStratford, 1626, 122, "SIS716");
        Berths.getOrCreateBerth(bpStratford, 1546, 186, "SIS717");
        Berths.getOrCreateBerth(bpStratford, 1690, 122, "SIS718");
        Berths.getOrCreateBerth(bpStratford, 1418, 154, "SIS767");
        Berths.getOrCreateBerth(bpStratford, 1626, 90,  "SIS770");
        Berths.getOrCreateBerth(bpStratford, 1610, 154, "SIS772");
        Berths.getOrCreateBerth(bpStratford, 1706, 474, "SIS697");
        Berths.getOrCreateBerth(bpStratford, 1706, 506, "SIS901");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Signals">
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(bpStratford, 1620, 434, "STRATFORD",               "SRA");
        largeStation(bpStratford, 56,   140, "LONDON LIVERPOOL STREET", "LST");

        smallStation(bpStratford, 845,  410, "BETHNAL",     "BET"); // GREEN
        smallStation(bpStratford, 1281, 242, "FOREST GATE", "FOG");
        smallStation(bpStratford, 851,  418, "GREEN",       "BET"); // BETHNAL
        smallStation(bpStratford, 906,  242, "MARYLAND",    "MYL");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(bpStratford, 1700, 58,  "BRIMSDOWN", 5);
        makeNavButton(bpStratford, 1124, 490, "HACKNEY",   5);
        makeNavButton(bpStratford, 1622, 314, "ILFORD",    2);
        //</editor-fold>
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Util methods">
    private void largeStation(JPanel pnl, int x, int y, String name, String crsCode)
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
        pnl.add(lbl);
    }

    private void smallStation(JPanel pnl, int x, int y, String name, String crsCode)
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
        pnl.add(lbl);
    }

    private void makeNavButton(JPanel pnl, int x, int y, String text, final int tabIndex)
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
        pnl.add(lbl);
    }

    private void placeTopBits(final JPanel pnl)
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
        pnl.add(menu);

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
        pnl.add(help);

        JLabel motd = new JLabel();
        motd.setText("XXMOTD");
        motd.setFocusable(false);
        motd.setOpaque(true);
        //motdTextArea.setEditable(false);

        motd.setForeground(EastAngliaMapClient.GREEN);
        motd.setBackground(new Color(30, 30, 30));
        motd.setVerticalAlignment(JLabel.TOP);
        motd.setHorizontalAlignment(JLabel.LEFT);
        motd.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 16));

        /*Font font = new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 16);
        MutableAttributeSet mas = motdTextArea.getInputAttributes();
        StyleConstants.setFontFamily(mas, font.getFamily());
        StyleConstants.setFontSize(mas, font.getSize());
        StyleConstants.setForeground(mas, Color.GREEN);
        StyleConstants.setBackground(mas, new Color(30, 30, 30));
        StyledDocument doc = motdTextArea.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength() + 1, mas, true);

        motdTextArea.setContentType("text/html");*/

        motd.setPreferredSize(new Dimension(520, 16));
        motd.setBorder(new EmptyBorder(0, 10, 0, 10));

        JScrollPane spMOTD = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spMOTD.setViewportView(motd);
        spMOTD.setBounds(200, 10, 550, 50);
        pnl.add(spMOTD);
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

        int curWidth = 0;
        for (String id : bytes)
        {
            if (curWidth > width*9 - 1)
            {
                y += 12;
                curWidth = 0;
            }

            if (!id.isEmpty())
                Signals.getOrCreateSignal(pnl, x + curWidth*12, y, "", id + (Signals.signalExists(id) ? " " : ""), SignalDirection.TEST);

            curWidth++;
        }
    }
    //</editor-fold>

    public void dispose()
    {
        frame.dispose();
    }

    public void setVisible(boolean visible)
    {
        if (visible == true && frame.isVisible() == true)
            frame.requestFocus();
        else
            frame.setVisible(visible);
    }

    public void readFromMap(Map<String, String> map)
    {
        EastAngliaMapClient.DataMap.putAll(map);

        for (Map.Entry<String, String> pairs : map.entrySet())
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
        return panels;
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
        for (JScrollPane sp : motdPanes)
        {
            JLabel lbl = (JLabel) sp.getViewport().getView();
            lbl.setText("<html><body style='width:auto;height:auto'>" + motd + "</body></html>");
            lbl.setPreferredSize(new Dimension(520, (((motd.length() - motd.replace("<br>", "").length()) / 4) + (motd.replaceAll("\\<.*?\\>", "").length() / 30)) * 24 + 24));
        }
    }

    public class BackgroundPanel extends JPanel
    {
        private static final String imageDir  = "/eastangliamapclient/resources/";
        private final Font clockFont = EastAngliaMapClient.TD_FONT.deriveFont(45f);
        private BufferedImage image;
        private BufferedImage bufferedImage;

        public final static int BP_DEFAULT_WIDTH  = DEFAULT_WIDTH  - 23;
        public final static int BP_DEFAULT_HEIGHT = DEFAULT_HEIGHT - 68;

        BackgroundPanel(String imageName)
        {
            super();

            setFont(clockFont);
            setLayout(null);
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            setPreferredSize(new Dimension(BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT));
            setOpaque(true);

            setImage(imageName);

            placeTopBits(this);
            panels.add(this);
        }

        public void setImage(String imageName)
        {
            try (InputStream in = getClass().getResourceAsStream(imageDir + imageName))
            {
                BufferedImage inImage = ImageIO.read(in);

                BufferedImage bimg = new BufferedImage(BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);

                Graphics2D g2d = bimg.createGraphics();
                g2d.drawImage(inImage, 0, 0, BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, Color.WHITE, null);
                g2d.dispose();

                image = bimg;
            }
            catch (IllegalArgumentException | IOException e)
            {
                EastAngliaMapClient.printErr("Unable to read image file: \"" + imageDir + String.valueOf(imageName) + "\"");
            }

            bufferedImage = new BufferedImage(BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.clearRect(0, 0, BP_DEFAULT_WIDTH, BP_DEFAULT_HEIGHT);
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
    }
}