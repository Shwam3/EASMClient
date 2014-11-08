package eastangliamapclient.gui;

import eastangliamapclient.Berth;
import eastangliamapclient.Berths;
import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.EventHandler;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SignalMap
{
    public JFrame frame;

    public  final ArrayList<JLabel>      clocks    = new ArrayList<>();
    private final ArrayList<JButton>     buttons   = new ArrayList<>();
    private final ArrayList<JScrollPane> motdPanes = new ArrayList<>();

    //<editor-fold defaultstate="collapsed" desc="Form variables">
    public        JTabbedPane TabBar;

    private final JPanel pnlNorwich     = new JPanel();
    private final JPanel pnlCambridgeEN = new JPanel();
    private final JPanel pnlCambridgeCA = new JPanel();
    private final JPanel pnlIpswich     = new JPanel();
    private final JPanel pnlClacton     = new JPanel();
    private final JPanel pnlColchester  = new JPanel();
    private final JPanel pnlIlford      = new JPanel();
    private final JPanel pnlShenfield   = new JPanel();
    private final JPanel pnlStratford   = new JPanel();
    private final JPanel pnlWitham      = new JPanel();
    private final JPanel pnlHackney     = new JPanel();
    private final JPanel pnlHarlow      = new JPanel();

    private final CustomScrollPane spNorwich     = new CustomScrollPane();
    private final CustomScrollPane spCambridgeEN = new CustomScrollPane();
    private final CustomScrollPane spCambridgeCA = new CustomScrollPane();
    private final CustomScrollPane spIpswich     = new CustomScrollPane();
    private final CustomScrollPane spClacton     = new CustomScrollPane();
    private final CustomScrollPane spColchester  = new CustomScrollPane();
    private final CustomScrollPane spIlford      = new CustomScrollPane();
    private final CustomScrollPane spShenfield   = new CustomScrollPane();
    private final CustomScrollPane spStratford   = new CustomScrollPane();
    private final CustomScrollPane spWitham      = new CustomScrollPane();
    private final CustomScrollPane spHackney     = new CustomScrollPane();
    private final CustomScrollPane spHarlow      = new CustomScrollPane();
    //</editor-fold>

    public SignalMap()
    {
        this(new Dimension(1874, 922));
    }

    public SignalMap(Dimension dim)
    {
        frame = new JFrame("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));
        TabBar = new JTabbedPane();

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (SystemTray.isSupported())
            frame.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosing(WindowEvent evt)
                {
                    EastAngliaMapClient.writeSetting("windowSize", ((int) frame.getSize().getWidth()) + "," + ((int) frame.getSize().getHeight()));

                    if (EastAngliaMapClient.minimiseToSysTray)
                    {
                        frame.setVisible(false);
                        SysTrayHandler.popup("App now in System Tray", TrayIcon.MessageType.INFO);
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
                    dispose();
                    System.exit(0);
                }
            });

        frame.setLocationByPlatform(true);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.setPreferredSize(new Dimension(Math.min(Math.max((int) dim.getWidth(), 800), 1874), Math.min(Math.max((int) dim.getHeight(), 600), 922)));
        frame.setMaximumSize(new Dimension(1874, 922));
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

        pnlStratford  .setLayout(null);
        pnlIlford     .setLayout(null);
        pnlShenfield  .setLayout(null);
        pnlWitham     .setLayout(null);
        pnlHackney    .setLayout(null);
        pnlHarlow     .setLayout(null);
        pnlColchester .setLayout(null);
        pnlClacton    .setLayout(null);
        pnlIpswich    .setLayout(null);
        pnlCambridgeCA.setLayout(null);
        pnlCambridgeEN.setLayout(null);
        pnlNorwich    .setLayout(null);

        pnlStratford  .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlIlford     .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlShenfield  .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlWitham     .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlHackney    .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlHarlow     .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlColchester .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlClacton    .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlIpswich    .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlCambridgeCA.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlCambridgeEN.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        pnlNorwich    .setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        pnlStratford  .setPreferredSize(new Dimension(1841, 844));
        pnlIlford     .setPreferredSize(new Dimension(1841, 844));
        pnlShenfield  .setPreferredSize(new Dimension(1841, 844));
        pnlWitham     .setPreferredSize(new Dimension(1841, 844));
        pnlHackney    .setPreferredSize(new Dimension(1841, 844));
        pnlHarlow     .setPreferredSize(new Dimension(1841, 844));
        pnlColchester .setPreferredSize(new Dimension(1841, 844));
        pnlClacton    .setPreferredSize(new Dimension(1841, 844));
        pnlIpswich    .setPreferredSize(new Dimension(1841, 844));
        pnlCambridgeCA.setPreferredSize(new Dimension(1841, 844));
        pnlCambridgeEN.setPreferredSize(new Dimension(1841, 844));
        pnlNorwich    .setPreferredSize(new Dimension(1841, 844));

        pnlStratford  .setBackground(EastAngliaMapClient.BLACK);
        pnlIlford     .setBackground(EastAngliaMapClient.BLACK);
        pnlShenfield  .setBackground(EastAngliaMapClient.BLACK);
        pnlWitham     .setBackground(EastAngliaMapClient.BLACK);
        pnlHackney    .setBackground(EastAngliaMapClient.BLACK);
        pnlHarlow     .setBackground(EastAngliaMapClient.BLACK);
        pnlColchester .setBackground(EastAngliaMapClient.BLACK);
        pnlClacton    .setBackground(EastAngliaMapClient.BLACK);
        pnlIpswich    .setBackground(EastAngliaMapClient.BLACK);
        pnlCambridgeCA.setBackground(EastAngliaMapClient.BLACK);
        pnlCambridgeEN.setBackground(EastAngliaMapClient.BLACK);
        pnlNorwich    .setBackground(EastAngliaMapClient.BLACK);

        pnlStratford  .setOpaque(true);
        pnlIlford     .setOpaque(true);
        pnlShenfield  .setOpaque(true);
        pnlWitham     .setOpaque(true);
        pnlHackney    .setOpaque(true);
        pnlHarlow     .setOpaque(true);
        pnlColchester .setOpaque(true);
        pnlClacton    .setOpaque(true);
        pnlIpswich    .setOpaque(true);
        pnlCambridgeCA.setOpaque(true);
        pnlCambridgeEN.setOpaque(true);
        pnlNorwich    .setOpaque(true);

        pnlStratford  .setName("1. Liverpool St/Stratford");
        pnlIlford     .setName("2. Ilford");
        pnlShenfield  .setName("3. Shenfield");
        pnlWitham     .setName("4. Witham");
        pnlHackney    .setName("5. Hackney/Brimsdown");
        pnlHarlow     .setName("6. Harlow");
        pnlColchester .setName("7. Colchester");
        pnlClacton    .setName("8. Clacton/Thorpe/East Gates");
        pnlIpswich    .setName("9. Ipswich");
        pnlCambridgeCA.setName("10. Cambridge (CA)");
        pnlCambridgeEN.setName("11. Cambridge (EN)");
        pnlNorwich    .setName("12. Norwich/Trowse Bridge");

        spStratford  .setViewportView(pnlStratford);
        spIlford     .setViewportView(pnlIlford);
        spShenfield  .setViewportView(pnlShenfield);
        spWitham     .setViewportView(pnlWitham);
        spHackney    .setViewportView(pnlHackney);
        spHarlow     .setViewportView(pnlHarlow);
        spColchester .setViewportView(pnlColchester);
        spClacton    .setViewportView(pnlClacton);
        spIpswich    .setViewportView(pnlIpswich);
        spCambridgeCA.setViewportView(pnlCambridgeCA);
        spCambridgeEN.setViewportView(pnlCambridgeEN);
        spNorwich    .setViewportView(pnlNorwich);

        TabBar.addTab(pnlStratford  .getName(), null, spStratford,   "<html>London Liverpool Street - Manor Park, Coppermill Jnc &amp; Orient Way</html>");
        TabBar.addTab(pnlIlford     .getName(), null, spIlford,      "<html>Forest Gate             - Harold Wood</html>");
        TabBar.addTab(pnlShenfield  .getName(), null, spShenfield,   "<html>Harold Wood             - Ingatestone</html>");
        TabBar.addTab(pnlWitham     .getName(), null, spWitham,      "<html>Shenfield               - Colchester</html>");
        TabBar.addTab(pnlHackney    .getName(), null, spHackney,     "<html>Hackney Downs           - Chingford, Enfield Town &amp; Cheshunt</html>");
        TabBar.addTab(pnlHarlow     .getName(), null, spHarlow,      "<html>Cheshunt                - Elsenham, Hertford East &amp; Stansted Airport</html>");
        TabBar.addTab(pnlColchester .getName(), null, spColchester,  "<html>Colchester              - Ipswich</html>");
        TabBar.addTab(pnlClacton    .getName(), null, spClacton,     "<html>Colchester              - Colchester Town, Clacton-on-Sea &amp; Walton-on-the-Naze</html>");
        TabBar.addTab(pnlIpswich    .getName(), null, spIpswich,     "<html>Ipswich                 - Stowmarket</html>");
        TabBar.addTab(pnlCambridgeCA.getName(), null, spCambridgeCA, "<html>Elsenham                - Ely</html>");
        TabBar.addTab(pnlCambridgeEN.getName(), null, spCambridgeEN, "<html>Ely North Jnc           - Manea, Kings Lynn &amp; Wymondham<br>Bury St. Edmunds - Ely &amp; Cambridge</html>");
        TabBar.addTab(pnlNorwich    .getName(), null, spNorwich,     "<html>Stowmarket, Sheringham &amp; Whitlingham Jnc - Norwich</html>");

        frame.add(TabBar, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/eastangliamapclient/resources/Icon.png")));
    }

    //<editor-fold defaultstate="collapsed" desc="Panels">
    //<editor-fold defaultstate="collapsed" desc="Norwich">
    private void initNorwich()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlNorwich, 154,  282, "CC0392");
        new Berth(pnlNorwich, 154,  250, "CC0393");
        new Berth(pnlNorwich, 218,  250, "CC0395");
        new Berth(pnlNorwich, 298,  282, "CC0396");
        new Berth(pnlNorwich, 298,  250, "CC0413");
        new Berth(pnlNorwich, 362,  250, "CC0415");
        new Berth(pnlNorwich, 362,  282, "CC0416");
        new Berth(pnlNorwich, 490,  282, "CC0422");
        new Berth(pnlNorwich, 426,  250, "CC0423");
        new Berth(pnlNorwich, 554,  282, "CC0424");
        new Berth(pnlNorwich, 490,  250, "CC0427");
        new Berth(pnlNorwich, 554,  250, "CC0429");
        new Berth(pnlNorwich, 618,  282, "CC0430", "CC0433");
        new Berth(pnlNorwich, 618,  250, "CC0431");
        new Berth(pnlNorwich, 762,  282, "CC0436");
        new Berth(pnlNorwich, 762,  250, "CC0439", "CC1416");
        new Berth(pnlNorwich, 826,  282, "CC0442");
        new Berth(pnlNorwich, 826,  250, "CC0443");
        new Berth(pnlNorwich, 890,  250, "CC0447");
        new Berth(pnlNorwich, 890,  282, "CC0448");
        new Berth(pnlNorwich, 954,  250, "CC0453");
        new Berth(pnlNorwich, 954,  282, "CC0454");
        new Berth(pnlNorwich, 1018, 282, "CC0460");
        new Berth(pnlNorwich, 1434, 410, "CC0499");
        new Berth(pnlNorwich, 1322, 378, "CC0500");
        new Berth(pnlNorwich, 1322, 410, "CC0501");
        new Berth(pnlNorwich, 1434, 378, "CC0531");
        new Berth(pnlNorwich, 1418, 314, "CC0533");
        new Berth(pnlNorwich, 1418, 346, "CC0535");
        new Berth(pnlNorwich, 1018, 250, "CC0541");
        new Berth(pnlNorwich, 1082, 250, "CC0543");
        new Berth(pnlNorwich, 1082, 282, "CC0546");
        new Berth(pnlNorwich, 1146, 250, "CC0549");
        new Berth(pnlNorwich, 1210, 250, "CC0550");
        new Berth(pnlNorwich, 1210, 282, "CC0552");
        new Berth(pnlNorwich, 1210, 314, "CC0554");
        new Berth(pnlNorwich, 1258, 250, "CC0557");
        new Berth(pnlNorwich, 1258, 282, "CC0559");
        new Berth(pnlNorwich, 1258, 314, "CC0561");
        new Berth(pnlNorwich, 1386, 250, "CC0564");
        new Berth(pnlNorwich, 1434, 250, "CC0565");
        new Berth(pnlNorwich, 1386, 282, "CC0566");
        new Berth(pnlNorwich, 1434, 282, "CC0567");
        new Berth(pnlNorwich, 1578, 154, "CC0570");
        new Berth(pnlNorwich, 1578, 186, "CC0572");
        new Berth(pnlNorwich, 1642, 218, "CC0574");
        new Berth(pnlNorwich, 1642, 250, "CC0576");
        new Berth(pnlNorwich, 1642, 282, "CC0578");
        new Berth(pnlNorwich, 1642, 314, "CC0580");
        new Berth(pnlNorwich, 1642, 378, "CC0584");
        new Berth(pnlNorwich, 1642, 410, "CC0586");
        new Berth(pnlNorwich, 1562, 362, "CC0588");
        new Berth(pnlNorwich, 554,  186, "CC0679");
        new Berth(pnlNorwich, 554,  154, "CC0680");
        new Berth(pnlNorwich, 490,  186, "CC0689");
        new Berth(pnlNorwich, 426,  186, "CC0691");
        new Berth(pnlNorwich, 362,  154, "CC0692");
        new Berth(pnlNorwich, 298,  154, "CC0698");
        new Berth(pnlNorwich, 298,  186, "CC0699");
        new Berth(pnlNorwich, 1002, 314, "CC0877");
        new Berth(pnlNorwich, 1002, 346, "CC0878");
        new Berth(pnlNorwich, 1066, 314, "CC0889");
        new Berth(pnlNorwich, 1130, 346, "CC0890");
        new Berth(pnlNorwich, 1130, 314, "CC0891");
        new Berth(pnlNorwich, 682,  314, "CC1408", "CC1409");
        new Berth(pnlNorwich, 682,  346, "CC1411");
        new Berth(pnlNorwich, 682,  282, "CC1413");
        new Berth(pnlNorwich, 1770, 218, "CC1574");
        new Berth(pnlNorwich, 1770, 250, "CC1576");
        new Berth(pnlNorwich, 1770, 282, "CC1578");
        new Berth(pnlNorwich, 1770, 314, "CC1580");
        new Berth(pnlNorwich, 1770, 378, "CC1584");
        new Berth(pnlNorwich, 1770, 410, "CC1586");
        new Berth(pnlNorwich, 1370, 362, "CC1491", "CCCPT1");
        new Berth(pnlNorwich, 1354, 298, "CC1762", "CCCPT2");
        new Berth(pnlNorwich, 1706, 346, "CC1792");
        new Berth(pnlNorwich, 938,  314, "CC8237"); // CBG sig on NRW panel
        new Berth(pnlNorwich, 938,  346, "CC8246"); // CBG sig on NRW panel
        new Berth(pnlNorwich, 1114, 410, "CCBL03");
        new Berth(pnlNorwich, 1114, 378, "CCBL28");
        new Berth(pnlNorwich, 650,  154, "CCBURD");
        new Berth(pnlNorwich, 1658, 186, "CCCSDG");
        new Berth(pnlNorwich, 1514, 386, "CCENGN");
        new Berth(pnlNorwich, 1514, 346, "CCFUEL");
        new Berth(pnlNorwich, 1562, 410, "CCJUBS");
        new Berth(pnlNorwich, 1418, 442, "CCL533");
        new Berth(pnlNorwich, 1418, 466, "CCL535");
        new Berth(pnlNorwich, 1362, 466, "CCLSR1");
        new Berth(pnlNorwich, 1362, 442, "CCLSR2");
        new Berth(pnlNorwich, 1706, 218, "CCR574");
        new Berth(pnlNorwich, 1706, 250, "CCR576");
        new Berth(pnlNorwich, 1706, 282, "CCR578");
        new Berth(pnlNorwich, 1706, 314, "CCR580");
        new Berth(pnlNorwich, 1706, 378, "CCR584");
        new Berth(pnlNorwich, 1706, 410, "CCR586");
        new Berth(pnlNorwich, 1258, 378, "CCTB02");
        new Berth(pnlNorwich, 1258, 410, "CCTB03");
        new Berth(pnlNorwich, 1178, 378, "CCTB04");
        new Berth(pnlNorwich, 1178, 410, "CCTB05");
        new Berth(pnlNorwich, 1178, 474, "CCTB06");
        new Berth(pnlNorwich, 1178, 506, "CCTB09");
        new Berth(pnlNorwich, 1050, 474, "CCTB10");
        new Berth(pnlNorwich, 1114, 506, "CCTB13");
        new Berth(pnlNorwich, 970,  474, "CCTB14");
        new Berth(pnlNorwich, 1050, 506, "CCTB17");
        new Berth(pnlNorwich, 842,  442, "CCTB18");
        new Berth(pnlNorwich, 906,  474, "CCTB21");
        new Berth(pnlNorwich, 810,  506, "CCTB25");
        new Berth(pnlNorwich, 810,  474, "CCTB26");
        new Berth(pnlNorwich, 730,  474, "CCTB28");
        new Berth(pnlNorwich, 554,  570, "CCTB31");
        new Berth(pnlNorwich, 570,  474, "CCTB32");
        new Berth(pnlNorwich, 666,  474, "CCTB33");
        new Berth(pnlNorwich, 570,  506, "CCTB34");
        new Berth(pnlNorwich, 1290, 330, "CCHSSD");
        new Berth(pnlNorwich, 1770, 346, "CCMSDG");
        new Berth(pnlNorwich, 474,  570, "CCX034", "CCX032");
        new Berth(pnlNorwich, 1658, 154, "CCYARD");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlNorwich, 30,  266, "IPSWICH",   9);
        makeNavButton(pnlNorwich, 790, 322, "CAMBRIDGE", 11);
        makeNavButton(pnlNorwich, 820, 338, "(EN)",      11);
        makeNavButton(pnlNorwich, 704, 162, "BURY ST",   11);
        makeNavButton(pnlNorwich, 704, 178, "EDMUNDS",   11);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(pnlNorwich, 1660, 442, "NORWICH", "NRW");

        smallStation(pnlNorwich, 630,  226, "DISS",        "DIS");
        smallStation(pnlNorwich, 573,  458, "CROMER",      "CMR");
        smallStation(pnlNorwich, 426,  130, "ELMSWELL",    "ESW");
        smallStation(pnlNorwich, 744,  450, "GUNTON",      "GNT");
        smallStation(pnlNorwich, 1047, 538, "HOVETON &",   "HXM");
        smallStation(pnlNorwich, 807,  538, "N WALSHAM",   "NWA");
        smallStation(pnlNorwich, 666,  506, "ROUGHTON RD", "RNR");
        smallStation(pnlNorwich, 1122, 538, "SALHOUSE",    "SAH");
        smallStation(pnlNorwich, 548,  546, "SHERINGHAM",  "SHM");
        smallStation(pnlNorwich, 554,  130, "THURSTON",    "TRS");
        smallStation(pnlNorwich, 616,  602, "W RUNTON",    "WRN");
        smallStation(pnlNorwich, 938,  450, "WORSTEAD",    "WRT");
        smallStation(pnlNorwich, 1053, 546, "WROXHAM",     "HXM");
        //</editor-fold>

        placeTopBits(pnlNorwich);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Norwich+Trowse.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlNorwich.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cambridge (EN)">
    private void initCambridgeEN()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        //NRW - ELY
        new Berth(pnlCambridgeEN, 186,  138, "ENA808");
        new Berth(pnlCambridgeEN, 1658, 106, "ENC877");
        new Berth(pnlCambridgeEN, 186,  106, "EN8019");
        new Berth(pnlCambridgeEN, 266,  138, "EN8024");
        new Berth(pnlCambridgeEN, 330,  138, "EN8034");
        new Berth(pnlCambridgeEN, 330,  106, "EN8041");
        new Berth(pnlCambridgeEN, 410,  138, "EN8044", "EN8063");
        new Berth(pnlCambridgeEN, 410,  106, "EN8061");
        new Berth(pnlCambridgeEN, 474,  106, "EN8064");
        new Berth(pnlCambridgeEN, 474,  138, "EN8066");
        new Berth(pnlCambridgeEN, 546,  74,  "EN8081", "EN8078");
        new Berth(pnlCambridgeEN, 474,  74,  "EN8069");
        new Berth(pnlCambridgeEN, 554,  106, "EN8083");
        new Berth(pnlCambridgeEN, 570,  138, "EN8086");
        new Berth(pnlCambridgeEN, 634,  138, "EN8103");
        new Berth(pnlCambridgeEN, 698,  170, "EN8104");
        new Berth(pnlCambridgeEN, 650,  106, "EN8109", "EN8084");
        new Berth(pnlCambridgeEN, 698,  138, "EN8110", "EN8113");
        new Berth(pnlCambridgeEN, 762,  138, "EN8118");
        new Berth(pnlCambridgeEN, 762,  106, "EN8129", "EN8114");
        new Berth(pnlCambridgeEN, 842,  138, "EN8134");
        new Berth(pnlCambridgeEN, 906,  138, "EN8148", "EN8153");
        new Berth(pnlCambridgeEN, 906,  106, "EN8149");
        new Berth(pnlCambridgeEN, 938,  74,  "EN8155");
        new Berth(pnlCambridgeEN, 1002, 106, "EN8158");
        new Berth(pnlCambridgeEN, 1050, 138, "EN8164");
        new Berth(pnlCambridgeEN, 1050, 106, "EN8167");
        new Berth(pnlCambridgeEN, 1114, 106, "EN8173");
        new Berth(pnlCambridgeEN, 1114, 138, "EN8176");
        new Berth(pnlCambridgeEN, 1178, 106, "EN8177");
        new Berth(pnlCambridgeEN, 1258, 138, "EN8178");
        new Berth(pnlCambridgeEN, 1258, 106, "EN8179");
        new Berth(pnlCambridgeEN, 1322, 106, "EN8195");
        new Berth(pnlCambridgeEN, 1402, 138, "EN8198", "EN8225");
        new Berth(pnlCambridgeEN, 1402, 106, "EN8219");
        new Berth(pnlCambridgeEN, 1434, 58,  "EN8221");
        new Berth(pnlCambridgeEN, 1418, 90,  "EN8223");
        new Berth(pnlCambridgeEN, 1498, 170, "EN8230");
        new Berth(pnlCambridgeEN, 1498, 138, "EN8234");
        new Berth(pnlCambridgeEN, 1498, 106, "EN8236");
        new Berth(pnlCambridgeEN, 1562, 106, "EN8237");
        new Berth(pnlCambridgeEN, 1562, 138, "EN8246");

        //KLN - ELY
        new Berth(pnlCambridgeEN, 1738, 330, "CAK22R");
        new Berth(pnlCambridgeEN, 1738, 298, "CAK23R");
        new Berth(pnlCambridgeEN, 1530, 346, "CAKL7A");
        new Berth(pnlCambridgeEN, 1674, 330, "CAK22B");
        new Berth(pnlCambridgeEN, 1674, 298, "CAK23B");
        new Berth(pnlCambridgeEN, 1610, 330, "CAK22A");
        new Berth(pnlCambridgeEN, 1610, 298, "CAK23A");
        new Berth(pnlCambridgeEN, 1594, 266, "CAKL24");
        new Berth(pnlCambridgeEN, 1530, 298, "CAKL25");
        new Berth(pnlCambridgeEN, 1418, 298, "CAKL35");
        new Berth(pnlCambridgeEN, 1306, 298, "CAKL36");
        new Berth(pnlCambridgeEN, 1466, 298, "CAKL43");
        new Berth(pnlCambridgeEN, 1354, 298, "CAKL44");
        new Berth(pnlCambridgeEN, 1242, 298, "CAKL45");
        new Berth(pnlCambridgeEN, 938,  298, "CADM02");
        new Berth(pnlCambridgeEN, 858,  298, "CADM03");
        new Berth(pnlCambridgeEN, 858,  266, "CADM11");
        new Berth(pnlCambridgeEN, 938,  266, "CADM26");
        new Berth(pnlCambridgeEN, 794,  266, "CADM27");
        new Berth(pnlCambridgeEN, 490,  234, "CALDAP");
        new Berth(pnlCambridgeEN, 730,  266, "CALT04");
        new Berth(pnlCambridgeEN, 666,  266, "CALT05");
        new Berth(pnlCambridgeEN, 602,  266, "CALT06");
        new Berth(pnlCambridgeEN, 666,  234, "CALT22");
        new Berth(pnlCambridgeEN, 602,  234, "CALT24");
        new Berth(pnlCambridgeEN, 1002, 266, "CAMR01");
        new Berth(pnlCambridgeEN, 1066, 266, "CAMR02");
        new Berth(pnlCambridgeEN, 1130, 266, "CAMR03");
        new Berth(pnlCambridgeEN, 1066, 298, "CAMR04");
        new Berth(pnlCambridgeEN, 1130, 298, "CAMR05");
        new Berth(pnlCambridgeEN, 1194, 298, "CAMR06");
        new Berth(pnlCambridgeEN, 1194, 330, "CAMUAP");

        //MNE - ELY
        new Berth(pnlCambridgeEN, 314,  362, "CA0301");
        new Berth(pnlCambridgeEN, 346,  426, "CA0908");
        new Berth(pnlCambridgeEN, 346,  394, "CA0911");
        new Berth(pnlCambridgeEN, 410,  426, "CA0912");
        new Berth(pnlCambridgeEN, 410,  362, "CA0915");
        new Berth(pnlCambridgeEN, 490,  426, "CA0916");
        new Berth(pnlCambridgeEN, 490,  394, "CA0917");
        new Berth(pnlCambridgeEN, 554,  394, "CA0919");
        new Berth(pnlCambridgeEN, 554,  426, "CA0920");
        new Berth(pnlCambridgeEN, 618,  394, "CA0921");
        new Berth(pnlCambridgeEN, 618,  426, "CA0922");
        new Berth(pnlCambridgeEN, 682,  394, "CA0923");
        new Berth(pnlCambridgeEN, 682,  426, "CA0924");
        new Berth(pnlCambridgeEN, 746,  394, "CAM043");
        new Berth(pnlCambridgeEN, 810,  394, "CAM002");
        new Berth(pnlCambridgeEN, 810,  426, "CAM021");
        new Berth(pnlCambridgeEN, 874,  394, "CAM003");

        //Ely North Jnc
        new Berth(pnlCambridgeEN, 186,  474, "CA0296");
        new Berth(pnlCambridgeEN, 186,  442, "CA0298");
        new Berth(pnlCambridgeEN, 234,  442, "CA0303");
        new Berth(pnlCambridgeEN, 234,  474, "CA0305");
        new Berth(pnlCambridgeEN, 394,  506, "CA0306");
        new Berth(pnlCambridgeEN, 394,  474, "CA0307");
        new Berth(pnlCambridgeEN, 458,  506, "CA0308");
        new Berth(pnlCambridgeEN, 458,  474, "CA0311");
        new Berth(pnlCambridgeEN, 364,  586, "CA0800");
        new Berth(pnlCambridgeEN, 362,  554, "CA0801");
        new Berth(pnlCambridgeEN, 426,  586, "CA0802");
        new Berth(pnlCambridgeEN, 490,  554, "CA0803");
        new Berth(pnlCambridgeEN, 490,  586, "CA0804");
        new Berth(pnlCambridgeEN, 554,  586, "CA0808");
        new Berth(pnlCambridgeEN, 282,  506, "CA0774");
        new Berth(pnlCambridgeEN, 554,  554, "CA8019");
        new Berth(pnlCambridgeEN, 570,  506, "CALAPP");
        new Berth(pnlCambridgeEN, 666,  554, "CALSCM");
        new Berth(pnlCambridgeEN, 666,  586, "CASAPP");
      //new Berth(pnlCambridgeEN, 378,  330, "CAR4AB");

        //BSE - CBG & ELY
        new Berth(pnlCambridgeEN, 1066, 682, "CA0402");
        new Berth(pnlCambridgeEN, 794,  778, "CA0483");
        new Berth(pnlCambridgeEN, 858,  810, "CA0486");
        new Berth(pnlCambridgeEN, 922,  810, "CA0488");
        new Berth(pnlCambridgeEN, 922,  778, "CA0491");
        new Berth(pnlCambridgeEN, 986,  778, "CA0494");
        new Berth(pnlCambridgeEN, 1066, 778, "CA0495");
        new Berth(pnlCambridgeEN, 1130, 778, "CA0498");
        new Berth(pnlCambridgeEN, 474,  714, "CCB001");
        new Berth(pnlCambridgeEN, 410,  714, "CCB002");
        //new Berth(pnlCambridgeEN, 314,  770, "CCB004");
        new Berth(pnlCambridgeEN, 314,  746, "CCB013");
        new Berth(pnlCambridgeEN, 250,  714, "CCB017");
        new Berth(pnlCambridgeEN, 346,  682, "CCB019");
        //new Berth(pnlCambridgeEN, 410,  650, "CCB031");
        new Berth(pnlCambridgeEN, 346,  650, "CCB032", "CCB033");
        //new Berth(pnlCambridgeEN, 282,  634, "CCB034");
        new Berth(pnlCambridgeEN, 410,  682, "CCB048"/*, "CCB045"*/);
        new Berth(pnlCambridgeEN, 250,  682, "CCB049");
        new Berth(pnlCambridgeEN, 186,  682, "CCB050");
        new Berth(pnlCambridgeEN, 602,  682, "CCB302");
        new Berth(pnlCambridgeEN, 602,  714, "CCB303");
        new Berth(pnlCambridgeEN, 538,  682, "CCB306");
        new Berth(pnlCambridgeEN, 538,  714, "CCB307");
        new Berth(pnlCambridgeEN, 474,  682, "CCB310");
        new Berth(pnlCambridgeEN, 778,  682, "CACM02");
        new Berth(pnlCambridgeEN, 794,  810, "CACM04");
        new Berth(pnlCambridgeEN, 666,  714, "CACM05");
        new Berth(pnlCambridgeEN, 666,  682, "CACM14");
        new Berth(pnlCambridgeEN, 666,  650, "CACAPP");
        new Berth(pnlCambridgeEN, 922,  682, "CAD002");
        new Berth(pnlCambridgeEN, 986,  682, "CAD003");
        new Berth(pnlCambridgeEN, 858,  682, "CAD004");
        new Berth(pnlCambridgeEN, 922,  714, "CAD005");
        new Berth(pnlCambridgeEN, 1130, 682, "CAX200");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlCambridgeEN, 1714, 122, "NORWICH", 12);

        makeNavButton(pnlCambridgeEN, 38, 450, "CAMBRIDGE", 10);
        makeNavButton(pnlCambridgeEN, 68, 466, "(CA)",      10);

        makeNavButton(pnlCambridgeEN, 50,   698, "THURSTON",  12);
        makeNavButton(pnlCambridgeEN, 1210, 682, "CAMBRIDGE", 10);
        makeNavButton(pnlCambridgeEN, 1226, 778, "ELY",       12);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlCambridgeEN, 1206, 82,  "ATTLEBOROUGH", "ATL");
        smallStation(pnlCambridgeEN, 477,  170, "BRANDON",      "BND");
        smallStation(pnlCambridgeEN, 991,  170, "ECCLES RD",    "ECS");
        smallStation(pnlCambridgeEN, 794,  170, "HARLING RD",   "HRD");
        smallStation(pnlCambridgeEN, 364,  170, "LAKENHEATH",   "LAK");
        smallStation(pnlCambridgeEN, 214,  170, "SHIPPEA HILL", "SPP");
        smallStation(pnlCambridgeEN, 1353, 82,  "SPOONER ROW",  "SPN");
        smallStation(pnlCambridgeEN, 698,  170, "THETFORD",     "TTF");
        smallStation(pnlCambridgeEN, 1495, 82,  "WYMONDHAM",    "WMD");

        smallStation(pnlCambridgeEN, 852,  330, "DOWNHAM MKT", "DOW");
        smallStation(pnlCambridgeEN, 1640, 354, "KINGS LYNN",  "KLN");
        smallStation(pnlCambridgeEN, 660,  298, "LITTLEPORT",  "LTP");
        smallStation(pnlCambridgeEN, 1121, 242, "WATLINGTON",  "WTG");

        smallStation(pnlCambridgeEN, 875,  372, "MANEA", "MNE");

        smallStation(pnlCambridgeEN, 229,  658, "BURY ST EDMUNDS", "BSE");
        smallStation(pnlCambridgeEN, 916,  658, "DULLINGHAM",      "DUL");
        smallStation(pnlCambridgeEN, 597,  658, "KENNETT",         "KNE");
        smallStation(pnlCambridgeEN, 815,  710, "NEWMARKET",       "NMK");
        //</editor-fold>

        placeTopBits(pnlCambridgeEN);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/CambridgeEN.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlCambridgeEN.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Cambridge (CA)">
    private void initCambridgeCA()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlCambridgeCA, 272,  154, "CA0211");
        new Berth(pnlCambridgeCA, 272,  186, "CA0212");
        new Berth(pnlCambridgeCA, 352,  186, "CA0220");
        new Berth(pnlCambridgeCA, 352,  122, "CA0223");
        new Berth(pnlCambridgeCA, 352,  154, "CA0225");
        new Berth(pnlCambridgeCA, 432,  186, "CA0228");
        new Berth(pnlCambridgeCA, 432,  154, "CA0229");
        new Berth(pnlCambridgeCA, 496,  186, "CA0230");
        new Berth(pnlCambridgeCA, 496,  154, "CA0231");
        new Berth(pnlCambridgeCA, 560,  186, "CA0232");
        new Berth(pnlCambridgeCA, 624,  154, "CA0235");
        new Berth(pnlCambridgeCA, 688,  186, "CA0236");
        new Berth(pnlCambridgeCA, 688,  154, "CA0237");
        new Berth(pnlCambridgeCA, 752,  186, "CA0238");
        new Berth(pnlCambridgeCA, 752,  154, "CA0239");
        new Berth(pnlCambridgeCA, 816,  186, "CA0240");
        new Berth(pnlCambridgeCA, 816,  154, "CA0241");
        new Berth(pnlCambridgeCA, 880,  186, "CA0244");
        new Berth(pnlCambridgeCA, 880,  154, "CA0245");
        new Berth(pnlCambridgeCA, 944,  186, "CA0246");
        new Berth(pnlCambridgeCA, 944,  154, "CA0247");
        new Berth(pnlCambridgeCA, 1008, 186, "CA0248");
        new Berth(pnlCambridgeCA, 1008, 154, "CA0249");
        new Berth(pnlCambridgeCA, 1072, 186, "CA0250");
        new Berth(pnlCambridgeCA, 1072, 154, "CA0251");
        new Berth(pnlCambridgeCA, 1136, 186, "CA0252");
        new Berth(pnlCambridgeCA, 1136, 154, "CA0253");
        new Berth(pnlCambridgeCA, 1200, 186, "CA0254");
        new Berth(pnlCambridgeCA, 1200, 122, "CA0255");
        new Berth(pnlCambridgeCA, 1200, 154, "CA0257");
        new Berth(pnlCambridgeCA, 1216, 250, "CA0259");
        new Berth(pnlCambridgeCA, 1280, 218, "CA0262");
        new Berth(pnlCambridgeCA, 1344, 250, "CA0270");
        new Berth(pnlCambridgeCA, 1360, 186, "CA0271");
        new Berth(pnlCambridgeCA, 1424, 234, "CA0272");
        new Berth(pnlCambridgeCA, 1360, 122, "CA0273");
        new Berth(pnlCambridgeCA, 1440, 218, "CA0274");
        new Berth(pnlCambridgeCA, 1440, 186, "CA0276");
        new Berth(pnlCambridgeCA, 1440, 154, "CA0278");
        new Berth(pnlCambridgeCA, 1504, 154, "CA0281");
        new Berth(pnlCambridgeCA, 1504, 186, "CA0283");
        new Berth(pnlCambridgeCA, 1504, 218, "CA0285");
        new Berth(pnlCambridgeCA, 1520, 250, "CA0287");
        new Berth(pnlCambridgeCA, 1600, 186, "CA0288");
        new Berth(pnlCambridgeCA, 1600, 154, "CA0290");
        new Berth(pnlCambridgeCA, 1648, 154, "CA0293");
        new Berth(pnlCambridgeCA, 1648, 186, "CA0295");
        new Berth(pnlCambridgeCA, 1360, 90,  "CA0765");
        new Berth(pnlCambridgeCA, 1280, 122, "CA1273");
        new Berth(pnlCambridgeCA, 1280, 90,  "CA1765");

        new Berth(pnlCambridgeCA, 160,  474, "CA0078");
        new Berth(pnlCambridgeCA, 224,  474, "CA0080");
        new Berth(pnlCambridgeCA, 288,  474, "CA0082");
        new Berth(pnlCambridgeCA, 160,  442, "CA0083");
        new Berth(pnlCambridgeCA, 448,  474, "CA0084");
        new Berth(pnlCambridgeCA, 368,  410, "CA0085");
        new Berth(pnlCambridgeCA, 368,  442, "CA0087");
        new Berth(pnlCambridgeCA, 512,  474, "CA0090");
        new Berth(pnlCambridgeCA, 448,  442, "CA0091");
        new Berth(pnlCambridgeCA, 576,  474, "CA0092");
        new Berth(pnlCambridgeCA, 512,  442, "CA0093");
        new Berth(pnlCambridgeCA, 640,  474, "CA0094");
        new Berth(pnlCambridgeCA, 576,  442, "CA0095");
        new Berth(pnlCambridgeCA, 704,  474, "CA0096");
        new Berth(pnlCambridgeCA, 640,  442, "CA0097");
        new Berth(pnlCambridgeCA, 704,  442, "CA0099");
        new Berth(pnlCambridgeCA, 768,  474, "CA0100");
        new Berth(pnlCambridgeCA, 224,  314, "CA0103");
        new Berth(pnlCambridgeCA, 224,  346, "CA0104");
        new Berth(pnlCambridgeCA, 288,  314, "CA0105");
        new Berth(pnlCambridgeCA, 352,  346, "CA0106");
        new Berth(pnlCambridgeCA, 352,  314, "CA0107");
        new Berth(pnlCambridgeCA, 416,  346, "CA0108");
        new Berth(pnlCambridgeCA, 416,  314, "CA0109");
        new Berth(pnlCambridgeCA, 480,  346, "CA0110");
        new Berth(pnlCambridgeCA, 480,  314, "CA0113");
        new Berth(pnlCambridgeCA, 576,  346, "CA0114");
        new Berth(pnlCambridgeCA, 576,  314, "CA0115");
        new Berth(pnlCambridgeCA, 640,  346, "CA0116");
        new Berth(pnlCambridgeCA, 640,  314, "CA0117");
        new Berth(pnlCambridgeCA, 704,  346, "CA0118");
        new Berth(pnlCambridgeCA, 704,  314, "CA0119");
        new Berth(pnlCambridgeCA, 768,  346, "CA0120");
        new Berth(pnlCambridgeCA, 768,  314, "CA0123");
        new Berth(pnlCambridgeCA, 864,  474, "CA0140");
        new Berth(pnlCambridgeCA, 864,  442, "CA0141");
        new Berth(pnlCambridgeCA, 928,  474, "CA0142");
        new Berth(pnlCambridgeCA, 928,  442, "CA0143");
        new Berth(pnlCambridgeCA, 992,  474, "CA0144");
        new Berth(pnlCambridgeCA, 992,  442, "CA0145");
        new Berth(pnlCambridgeCA, 1056, 410, "CA0147");
        new Berth(pnlCambridgeCA, 1056, 474, "CA0148");
        new Berth(pnlCambridgeCA, 1056, 442, "CA0149");
        new Berth(pnlCambridgeCA, 1200, 378, "CA0150");
        new Berth(pnlCambridgeCA, 1200, 410, "CA0152");
        new Berth(pnlCambridgeCA, 1200, 442, "CA0154");
        new Berth(pnlCambridgeCA, 1200, 474, "CA0156");
        new Berth(pnlCambridgeCA, 1232, 506, "CA0158");
        new Berth(pnlCambridgeCA, 1232, 538, "CA0160");
        new Berth(pnlCambridgeCA, 1251, 442, "CA0161");
        new Berth(pnlCambridgeCA, 1341, 442, "CA0162");
        new Berth(pnlCambridgeCA, 1251, 474, "CA0163");
        new Berth(pnlCambridgeCA, 1341, 474, "CA0164");
        new Berth(pnlCambridgeCA, 1392, 378, "CA0171");
        new Berth(pnlCambridgeCA, 1392, 410, "CA0173");
        new Berth(pnlCambridgeCA, 1392, 442, "CA0175");
        new Berth(pnlCambridgeCA, 1392, 474, "CA0177");
        new Berth(pnlCambridgeCA, 1360, 506, "CA0179");
        new Berth(pnlCambridgeCA, 1360, 538, "CA0181");
        new Berth(pnlCambridgeCA, 1520, 474, "CA0180");
        new Berth(pnlCambridgeCA, 1600, 490, "CA0188");
        new Berth(pnlCambridgeCA, 1632, 474, "CA0190");
        new Berth(pnlCambridgeCA, 1632, 410, "CA0193");
        new Berth(pnlCambridgeCA, 1632, 442, "CA0195");
        new Berth(pnlCambridgeCA, 1680, 490, "CA0200");
        new Berth(pnlCambridgeCA, 1736, 506, "CA0401");
        new Berth(pnlCambridgeCA, 1712, 474, "CA0210");
        new Berth(pnlCambridgeCA, 1112, 370, "CA0653");
        new Berth(pnlCambridgeCA, 1112, 394, "CA0655");
        new Berth(pnlCambridgeCA, 1680, 522, "CADAPP");
        new Berth(pnlCambridgeCA, 1264, 378, "CAX150");
        new Berth(pnlCambridgeCA, 1264, 410, "CAX152");
        new Berth(pnlCambridgeCA, 1328, 378, "CAX171");
        new Berth(pnlCambridgeCA, 1328, 410, "CAX173");
        new Berth(pnlCambridgeCA, 144,  314, "CA0981");
        new Berth(pnlCambridgeCA, 144,  346, "CA0986");

        new Berth(pnlCambridgeCA, 128,  666, "CA0019");
        new Berth(pnlCambridgeCA, 192,  698, "CA0020");
        new Berth(pnlCambridgeCA, 192,  666, "CA0021");
        new Berth(pnlCambridgeCA, 272,  698, "CA0022");
        new Berth(pnlCambridgeCA, 336,  666, "CA0023");
        new Berth(pnlCambridgeCA, 336,  698, "CA0024");
        new Berth(pnlCambridgeCA, 400,  698, "CA0026");
        new Berth(pnlCambridgeCA, 400,  666, "CA0027");
        new Berth(pnlCambridgeCA, 464,  698, "CA0028");
        new Berth(pnlCambridgeCA, 464,  666, "CA0029");
        new Berth(pnlCambridgeCA, 528,  698, "CA0030");
        new Berth(pnlCambridgeCA, 528,  666, "CA0031");
        new Berth(pnlCambridgeCA, 592,  698, "CA0032");
        new Berth(pnlCambridgeCA, 592,  666, "CA0033");
        new Berth(pnlCambridgeCA, 656,  698, "CA0034");
        new Berth(pnlCambridgeCA, 656,  666, "CA0037");
        new Berth(pnlCambridgeCA, 720,  698, "CA0038");
        new Berth(pnlCambridgeCA, 720,  666, "CA0039");
        new Berth(pnlCambridgeCA, 784,  698, "CA0040");
        new Berth(pnlCambridgeCA, 784,  666, "CA0041");
        new Berth(pnlCambridgeCA, 848,  698, "CA0042");
        new Berth(pnlCambridgeCA, 848,  666, "CA0043");
        new Berth(pnlCambridgeCA, 912,  698, "CA0044", "CA0047");
        new Berth(pnlCambridgeCA, 912,  666, "CA0045");
        new Berth(pnlCambridgeCA, 976,  698, "CA0050");
        new Berth(pnlCambridgeCA, 976,  666, "CA0051");
        new Berth(pnlCambridgeCA, 1040, 698, "CA0052");
        new Berth(pnlCambridgeCA, 1040, 666, "CA0053");
        new Berth(pnlCambridgeCA, 1104, 698, "CA0054");
        new Berth(pnlCambridgeCA, 1104, 666, "CA0055");
        new Berth(pnlCambridgeCA, 1168, 698, "CA0056");
        new Berth(pnlCambridgeCA, 1168, 666, "CA0057");
        new Berth(pnlCambridgeCA, 1232, 698, "CA0058");
        new Berth(pnlCambridgeCA, 1296, 666, "CA0059");
        new Berth(pnlCambridgeCA, 1296, 698, "CA0060");
        new Berth(pnlCambridgeCA, 1360, 666, "CA0061");
        new Berth(pnlCambridgeCA, 1360, 698, "CA0062");
        new Berth(pnlCambridgeCA, 1360, 730, "CA0064");
        new Berth(pnlCambridgeCA, 1520, 698, "CA0066");
        new Berth(pnlCambridgeCA, 1456, 666, "CA0067");
        new Berth(pnlCambridgeCA, 1584, 698, "CA0072");
        new Berth(pnlCambridgeCA, 1520, 666, "CA0073");
        new Berth(pnlCambridgeCA, 1648, 698, "CA0074");
        new Berth(pnlCambridgeCA, 1584, 666, "CA0075");
        new Berth(pnlCambridgeCA, 1712, 698, "CA0076");
        new Berth(pnlCambridgeCA, 1648, 666, "CA0077");
        new Berth(pnlCambridgeCA, 1712, 666, "CA0079");
        new Berth(pnlCambridgeCA, 32,   666, "CAL187");
        new Berth(pnlCambridgeCA, 32,   698, "CAL188");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlCambridgeCA, 1748, 162, "ELY",   11);
        makeNavButton(pnlCambridgeCA, 1736, 178, "N JNC", 11);

        makeNavButton(pnlCambridgeCA, 20, 642, "HARLOW", 6);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(pnlCambridgeCA, 1266, 354, "CAMBRIDGE", "CBG");
        largeStation(pnlCambridgeCA, 1478, 122, "ELY",       "ELY");

        smallStation(pnlCambridgeCA, 906,  730, "AUDLEY END",   "AUD");
        smallStation(pnlCambridgeCA, 1447, 738, "CHESTERFORD",  "GRC"); //GREAT
        smallStation(pnlCambridgeCA, 192,  730, "ELSENHAM",     "ESM");
        smallStation(pnlCambridgeCA, 723,  738, "(ESSEX)",      "NWE"); //NEWPORT
        smallStation(pnlCambridgeCA, 582,  378, "FOXTON",       "FXN");
        smallStation(pnlCambridgeCA, 1465, 730, "GREAT",        "GRC"); //CHESTERFORD
        smallStation(pnlCambridgeCA, 352,  378, "MELDRETH",     "MEL");
        smallStation(pnlCambridgeCA, 723,  730, "NEWPORT",      "NWE"); //(ESSEX)
        smallStation(pnlCambridgeCA, 227,  514, "PARKWAY",      "WLF"); //WHITTLESFORD
        smallStation(pnlCambridgeCA, 712,  506, "SHELFORD",     "SED");
        smallStation(pnlCambridgeCA, 480,  378, "SHEPRETH",     "STH");
        smallStation(pnlCambridgeCA, 586,  218, "WATERBEACH",   "WBC");
        smallStation(pnlCambridgeCA, 212,  506, "WHITTLESFORD", "WLF"); //PARKWAY
        //</editor-fold>

        placeTopBits(pnlCambridgeCA);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/CambridgeCA.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlCambridgeCA.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Ipswich">
    private void initIpswich()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlIpswich, 90,   298, "CC0290");
        new Berth(pnlIpswich, 122,  250, "CC0295", "CC0292");
        new Berth(pnlIpswich, 122,  282, "CC0294", "CC0297");
        new Berth(pnlIpswich, 202,  218, "CC0302");
        new Berth(pnlIpswich, 202,  250, "CC0304");
        new Berth(pnlIpswich, 202,  282, "CC0306");
        new Berth(pnlIpswich, 202,  314, "CC0308");
        new Berth(pnlIpswich, 266,  218, "CC0311");
        new Berth(pnlIpswich, 266,  250, "CC0313");
        new Berth(pnlIpswich, 266,  282, "CC0315");
        new Berth(pnlIpswich, 266,  314, "CC0317");
        new Berth(pnlIpswich, 282,  346, "CC0319");
        new Berth(pnlIpswich, 394,  346, "CC0322");
        new Berth(pnlIpswich, 426,  282, "CC0326");
        new Berth(pnlIpswich, 410,  314, "CC0328");
        new Berth(pnlIpswich, 474,  250, "CC0331");
        new Berth(pnlIpswich, 474,  282, "CC0333");
        new Berth(pnlIpswich, 458,  314, "CC0335");
        new Berth(pnlIpswich, 458,  346, "CC0337");
        new Berth(pnlIpswich, 490,  362, "CC0341");
        new Berth(pnlIpswich, 554,  378, "CC0343");
        new Berth(pnlIpswich, 682,  410, "CC0348");
        new Berth(pnlIpswich, 714,  314, "CC0351");
        new Berth(pnlIpswich, 746,  346, "CC0352");
        new Berth(pnlIpswich, 682,  282, "CC0346");
        new Berth(pnlIpswich, 778,  282, "CC0366");
        new Berth(pnlIpswich, 682,  250, "CC0367");
        new Berth(pnlIpswich, 778,  250, "CC0369");
        new Berth(pnlIpswich, 842,  282, "CC0370");
        new Berth(pnlIpswich, 842,  250, "CC0371");
        new Berth(pnlIpswich, 922,  282, "CC0374");
        new Berth(pnlIpswich, 922,  250, "CC0375");
        new Berth(pnlIpswich, 922,  218, "CC0377", "CC1372");
        new Berth(pnlIpswich, 1018, 282, "CC0378");
        new Berth(pnlIpswich, 1018, 250, "CC0379");
        new Berth(pnlIpswich, 1082, 282, "CC0380");
        new Berth(pnlIpswich, 1082, 250, "CC0381");
        new Berth(pnlIpswich, 1146, 250, "CC0383");
        new Berth(pnlIpswich, 1210, 282, "CC0384");
        new Berth(pnlIpswich, 1210, 250, "CC0385");
        new Berth(pnlIpswich, 1306, 218, "CC0386");
        new Berth(pnlIpswich, 1306, 282, "CC0388");
        new Berth(pnlIpswich, 1370, 218, "CC0389");
        new Berth(pnlIpswich, 1434, 250, "CC0391", "CC0390");
        new Berth(pnlIpswich, 778,  410, "CC0400");
        new Berth(pnlIpswich, 682,  378, "CC0401");
        new Berth(pnlIpswich, 778,  378, "CC0403");
        new Berth(pnlIpswich, 842,  410, "CC0404");
        new Berth(pnlIpswich, 842,  378, "CC0405");
        new Berth(pnlIpswich, 906,  378, "CC0611");
        new Berth(pnlIpswich, 970,  410, "CC0612");
        new Berth(pnlIpswich, 1082, 474, "CC0613");
        new Berth(pnlIpswich, 1050, 410, "CC0614");
        new Berth(pnlIpswich, 1146, 474, "CC0615");
        new Berth(pnlIpswich, 1034, 474, "CC0616");
        new Berth(pnlIpswich, 1258, 474, "CC0617");
        new Berth(pnlIpswich, 1146, 506, "CC0618");
        new Berth(pnlIpswich, 1370, 474, "CC0619");
        new Berth(pnlIpswich, 1210, 474, "CC0620");
        new Berth(pnlIpswich, 1482, 474, "CC0621");
        new Berth(pnlIpswich, 1322, 474, "CC0622");
        new Berth(pnlIpswich, 1434, 474, "CC0624");
        new Berth(pnlIpswich, 1546, 474, "CC0626");
        new Berth(pnlIpswich, 1402, 490, "CC0632");
        new Berth(pnlIpswich, 1402, 522, "CC0634");
        new Berth(pnlIpswich, 1594, 506, "CC0641");
        new Berth(pnlIpswich, 1546, 506, "CC0642");
        new Berth(pnlIpswich, 1658, 506, "CC0644");
        new Berth(pnlIpswich, 282,  154, "CC0797");
        new Berth(pnlIpswich, 282,  186, "CC0799");
        new Berth(pnlIpswich, 210,  186, "CCLOCS");
        new Berth(pnlIpswich, 410,  362, "CC0812");
        new Berth(pnlIpswich, 410,  378, "CC0814");
        new Berth(pnlIpswich, 410,  394, "CC0816");
        new Berth(pnlIpswich, 410,  410, "CC0818");
        new Berth(pnlIpswich, 490,  378, "CC0827");
        new Berth(pnlIpswich, 490,  394, "CC0829");
        new Berth(pnlIpswich, 618,  410, "CC0834");
        new Berth(pnlIpswich, 474,  410, "CC0835");
        new Berth(pnlIpswich, 474,  426, "CC0837");
        new Berth(pnlIpswich, 1706, 506, "CC0841");
        new Berth(pnlIpswich, 218,  346, "CC1319");
        new Berth(pnlIpswich, 1050, 378, "CC2003");
        new Berth(pnlIpswich, 1178, 378, "CC2012");
        new Berth(pnlIpswich, 986,  298, "CCBARS");
        new Berth(pnlIpswich, 1498, 554, "CCFDAP");
        new Berth(pnlIpswich, 1578, 554, "CCFDLS");
        new Berth(pnlIpswich, 1610, 474, "CCFTSN");
        new Berth(pnlIpswich, 410,  426, "CCIPYA");
        new Berth(pnlIpswich, 1434, 554, "CCNQ02");
        new Berth(pnlIpswich, 1434, 538, "CCNQ04");
        new Berth(pnlIpswich, 90,   346, "CCR290");
        new Berth(pnlIpswich, 1370, 194, "CCR388");
        new Berth(pnlIpswich, 1770, 538, "CCSDCR");
        new Berth(pnlIpswich, 1770, 506, "CCSDG1");
        new Berth(pnlIpswich, 1770, 522, "CCSDG2");
        new Berth(pnlIpswich, 1114, 410, "CCULAP");

        new Berth(pnlIpswich, 586,  634, "SX2003");
        new Berth(pnlIpswich, 650,  666, "SX2004");
        new Berth(pnlIpswich, 650,  634, "SX2011", "SX2006");
        new Berth(pnlIpswich, 714,  634, "SX2012");
        new Berth(pnlIpswich, 858,  666, "SX2028");
        new Berth(pnlIpswich, 794,  634, "SX2029");
        new Berth(pnlIpswich, 858,  634, "SX2033", "SX2032");
        new Berth(pnlIpswich, 922,  666, "SX2035");
        new Berth(pnlIpswich, 986,  666, "SX2042");
        new Berth(pnlIpswich, 1082, 634, "SX2045");
        new Berth(pnlIpswich, 1082, 666, "SX2046");
        new Berth(pnlIpswich, 1146, 666, "SX2052");
        new Berth(pnlIpswich, 1226, 666, "SX2055");
        new Berth(pnlIpswich, 1290, 698, "SX2056");
        new Berth(pnlIpswich, 1290, 666, "SX2057", "SX2058"); //2058 for possible bi-di
        new Berth(pnlIpswich, 1354, 666, "SX2060");
        new Berth(pnlIpswich, 1482, 666, "SX2066");
        new Berth(pnlIpswich, 1658, 666, "SXB21R");
        new Berth(pnlIpswich, 378,  634, "SXC405");
        new Berth(pnlIpswich, 442,  634, "SXC611");
        new Berth(pnlIpswich, 506,  666, "SXC612");
        new Berth(pnlIpswich, 586,  666, "SXC614");
        new Berth(pnlIpswich, 1754, 666, "SXLSOB");
        new Berth(pnlIpswich, 1418, 666, "SXOB18");
        new Berth(pnlIpswich, 1530, 666, "SXOB19");
        new Berth(pnlIpswich, 1594, 666, "SXOB21");
        new Berth(pnlIpswich, 1594, 634, "SXOSTO");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlIpswich, 26, 226, "COLCHESTER", 7);

        makeNavButton(pnlIpswich, 1538, 266, "NORWICH", 12);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(pnlIpswich, 216, 370, "IPSWICH", "IPS");

        smallStation(pnlIpswich, 1293, 642, "BECCLES",        "BCC");
        smallStation(pnlIpswich, 1186, 698, "BRAMPTON",       "BRP");
        smallStation(pnlIpswich, 1024, 610, "DARSHAM",        "DSM");
        smallStation(pnlIpswich, 1140, 450, "DERBY ROAD",     "DBR");
        smallStation(pnlIpswich, 1604, 450, "FELIXSTOWE",     "FLX");
        smallStation(pnlIpswich, 1082, 610, "HALESWORTH",     "HAS");
        smallStation(pnlIpswich, 728,  610, "MELTON",         "MES");
        smallStation(pnlIpswich, 1137, 314, "NEEDHAM MKT",    "NMT");
        smallStation(pnlIpswich, 1488, 698, "OULTON BROAD S", "OUS");
        smallStation(pnlIpswich, 852,  610, "SAXMUNDHAM",     "SAX");
        smallStation(pnlIpswich, 1428, 314, "STOWMARKET",     "SMK");
        smallStation(pnlIpswich, 1453, 450, "TRIMLEY",        "TRM");
        smallStation(pnlIpswich, 777,  610, "WICKHAM MKT",    "WCM");
        smallStation(pnlIpswich, 961,  354, "WESTERFIELD",    "WFI"); // COL
        smallStation(pnlIpswich, 497,  610, "WESTERFIELD",    "WFI"); // SAX
        smallStation(pnlIpswich, 644,  610, "WOODBRIDGE",     "WDB");
        //</editor-fold>

        placeTopBits(pnlIpswich);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Ipswich+Saxmundham.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlIpswich.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Clacton">
    private void initClacton()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlClacton, 138,  330, "CC1088");
        new Berth(pnlClacton, 138,  266, "CC1091");
        new Berth(pnlClacton, 250,  346, "CC1092");
        new Berth(pnlClacton, 138,  298, "CC1093");
        new Berth(pnlClacton, 218,  378, "CC1094");
        new Berth(pnlClacton, 202,  298, "CC1095");
        new Berth(pnlClacton, 218,  410, "CC1099");
        new Berth(pnlClacton, 154,  378, "CC1096");
        new Berth(pnlClacton, 314,  330, "CC1100");
        new Berth(pnlClacton, 314,  298, "CC1101");
        new Berth(pnlClacton, 410,  330, "CC1102");
        new Berth(pnlClacton, 346,  362, "CC1103");
        new Berth(pnlClacton, 538,  330, "CC1104");
        new Berth(pnlClacton, 410,  298, "CC1105");
        new Berth(pnlClacton, 474,  298, "CC1109");
        new Berth(pnlClacton, 602,  330, "CC1110");
        new Berth(pnlClacton, 538,  298, "CC1111");
        new Berth(pnlClacton, 666,  298, "CC1115");
        new Berth(pnlClacton, 666,  330, "CC1114");
        new Berth(pnlClacton, 730,  330, "CC1118");
        new Berth(pnlClacton, 794,  298, "CC1121");
        new Berth(pnlClacton, 794,  330, "CC1122");
        new Berth(pnlClacton, 874,  298, "CC1125");
        new Berth(pnlClacton, 874,  330, "CC1128");
        new Berth(pnlClacton, 938,  298, "CC1133");
        new Berth(pnlClacton, 970,  330, "CC1134");
        new Berth(pnlClacton, 1034, 330, "CC1136");
        new Berth(pnlClacton, 1098, 298, "CC1137");
        new Berth(pnlClacton, 1098, 330, "CC1138");
        new Berth(pnlClacton, 1194, 330, "CC1144");
        new Berth(pnlClacton, 1258, 298, "CC1145");
        new Berth(pnlClacton, 1194, 298, "CC1146");
        new Berth(pnlClacton, 1258, 330, "CC1147");
        new Berth(pnlClacton, 1354, 330, "CC1152");
        new Berth(pnlClacton, 1354, 266, "CC1154");
        new Berth(pnlClacton, 1418, 330, "CC1172");
        new Berth(pnlClacton, 1418, 298, "CC1173");
        new Berth(pnlClacton, 1482, 330, "CC1174");
        new Berth(pnlClacton, 1546, 330, "CC1176");
        new Berth(pnlClacton, 1546, 298, "CC1177");
        new Berth(pnlClacton, 1722, 330, "CCAPP1");
        new Berth(pnlClacton, 1658, 330, "CCAPP2");
        new Berth(pnlClacton, 90,   378, "CCR096");

        new Berth(pnlClacton, 250, 538, "CC1157");
        new Berth(pnlClacton, 186, 538, "CC1158");
        new Berth(pnlClacton, 314, 538, "CC1159");
        new Berth(pnlClacton, 314, 570, "CC1160");
        new Berth(pnlClacton, 378, 538, "CC1164");
        new Berth(pnlClacton, 442, 538, "CC1165");
        new Berth(pnlClacton, 506, 538, "CC1168");
        new Berth(pnlClacton, 570, 538, "CCWARR");

        new Berth(pnlClacton, 762, 522, "CCCN26");
        new Berth(pnlClacton, 826, 522, "CCCN27");
        new Berth(pnlClacton, 762, 554, "CCCN42");
        new Berth(pnlClacton, 970, 522, "CCCN44");
        new Berth(pnlClacton, 970, 554, "CCCN48");
        new Berth(pnlClacton, 970, 586, "CCCN55");
        new Berth(pnlClacton, 970, 618, "CCCN60");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlClacton, 26, 242, "COLCHESTER", 7);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlClacton, 727,  266, "ALRESFORD",       "ALR"); // (ESSEX)
        smallStation(pnlClacton, 1034, 570, "CLACTON",         "CLT"); // ON-SEA
        smallStation(pnlClacton, 144,  354, "COLCH TOWN",      "CET");
        smallStation(pnlClacton, 446,  506, "FRINTON",         "FRI"); // ON-SEA
        smallStation(pnlClacton, 1037, 578, "ON-SEA",          "CLT"); // CLACTON
        smallStation(pnlClacton, 448,  514, "ON-SEA",          "FRI"); // FRINTON
        smallStation(pnlClacton, 828,  274, "GT BENTLEY",      "GRB");
        smallStation(pnlClacton, 733,  274, "(ESSEX)",         "ALR"); // ALRESFORD
        smallStation(pnlClacton, 419,  274, "HYTHE",           "HYH");
        smallStation(pnlClacton, 305,  514, "KIRBY CROSS",     "KBX");
        smallStation(pnlClacton, 1205, 282, "THORPE-LE-SOKEN", "TLS");
        smallStation(pnlClacton, 507,  570, "WALTON-ON",       "WON");
        smallStation(pnlClacton, 510,  578, "THE-NAZE",        "WON");
        smallStation(pnlClacton, 984,  274, "WEELEY",          "WEE");
        smallStation(pnlClacton, 530,  274, "WIVENHOE",        "WIV");
        //</editor-fold>

        placeTopBits(pnlClacton);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/EastGates+Thorpe+Clacton.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlClacton.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Colchester">
    private void initColchester()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlColchester, 214,  282, "CC0190");
        new Berth(pnlColchester, 214,  250, "CC0191");
        new Berth(pnlColchester, 294,  282, "CC0194");
        new Berth(pnlColchester, 374,  346, "CC0196");
        new Berth(pnlColchester, 342,  250, "CC0197");
        new Berth(pnlColchester, 438,  282, "CC0198");
        new Berth(pnlColchester, 342,  282, "CC0199");
        new Berth(pnlColchester, 326,  314, "CC0201");
        new Berth(pnlColchester, 438,  250, "CC0203");
        new Berth(pnlColchester, 470,  314, "CC0205", "CC0207");
        new Berth(pnlColchester, 438,  346, "CC0209");
        new Berth(pnlColchester, 438,  378, "CC0211");
        new Berth(pnlColchester, 534,  378, "CC0228");
        new Berth(pnlColchester, 534,  346, "CC0229");
        new Berth(pnlColchester, 678,  378, "CC0230");
        new Berth(pnlColchester, 678,  346, "CC0231", "CC0766");
        new Berth(pnlColchester, 742,  378, "CC0232");
        new Berth(pnlColchester, 742,  346, "CC0233");
        new Berth(pnlColchester, 806,  378, "CC0234");
        new Berth(pnlColchester, 534,  282, "CC0268");
        new Berth(pnlColchester, 598,  250, "CC0269");
        new Berth(pnlColchester, 598,  282, "CC0270");
        new Berth(pnlColchester, 662,  250, "CC0271");
        new Berth(pnlColchester, 662,  282, "CC0272");
        new Berth(pnlColchester, 726,  250, "CC0273");
        new Berth(pnlColchester, 726,  282, "CC0274");
        new Berth(pnlColchester, 790,  250, "CC0275");
        new Berth(pnlColchester, 790,  282, "CC0276");
        new Berth(pnlColchester, 854,  250, "CC0277");
        new Berth(pnlColchester, 854,  282, "CC0278");
        new Berth(pnlColchester, 918,  250, "CC0279");
        new Berth(pnlColchester, 918,  282, "CC0280");
        new Berth(pnlColchester, 982,  250, "CC0281");
        new Berth(pnlColchester, 982,  282, "CC0282");
        new Berth(pnlColchester, 1046, 250, "CC0283");
        new Berth(pnlColchester, 1046, 282, "CC0284");
        new Berth(pnlColchester, 1110, 250, "CC0285");
        new Berth(pnlColchester, 1110, 282, "CC0286");
        new Berth(pnlColchester, 374,  218, "CC0753");
        new Berth(pnlColchester, 614,  314, "CC0762", "CC0763");
        new Berth(pnlColchester, 1206, 282, "CCAPIP");
        new Berth(pnlColchester, 806,  410, "CCAPPA");
        new Berth(pnlColchester, 806,  346, "CCP001");
        new Berth(pnlColchester, 870,  378, "CCP002");
        new Berth(pnlColchester, 870,  346, "CCP003");
        new Berth(pnlColchester, 998,  378, "CCP004");
        new Berth(pnlColchester, 966,  346, "CCP005");
        new Berth(pnlColchester, 1062, 378, "CCP006");
        new Berth(pnlColchester, 1062, 346, "CCP007");
        new Berth(pnlColchester, 1126, 378, "CCP008");
        new Berth(pnlColchester, 1126, 346, "CCP009");
        new Berth(pnlColchester, 1206, 314, "CCP010");
        new Berth(pnlColchester, 1206, 346, "CCP012");
        new Berth(pnlColchester, 1206, 378, "CCP014");
        new Berth(pnlColchester, 1254, 346, "CCP021");
        new Berth(pnlColchester, 1398, 346, "CCP022");
        new Berth(pnlColchester, 1334, 378, "CCP024");
        new Berth(pnlColchester, 1398, 378, "CCP026");
        new Berth(pnlColchester, 1398, 410, "CCP028");
        new Berth(pnlColchester, 1446, 346, "CCP037");
        new Berth(pnlColchester, 1446, 378, "CCP039");
        new Berth(pnlColchester, 1510, 378, "CCP050");
        new Berth(pnlColchester, 1558, 378, "CCP057");
        new Berth(pnlColchester, 1622, 378, "CCP058");
        new Berth(pnlColchester, 1670, 378, "CCP065");
        new Berth(pnlColchester, 1734, 378, "CCP074");
        new Berth(pnlColchester, 1254, 330, "CCPKSD");
        new Berth(pnlColchester, 1206, 410, "CCPKTP");
        new Berth(pnlColchester, 254,  314, "CCR201");

        new Berth(pnlColchester, 1062, 602, "CC0177");
        new Berth(pnlColchester, 1126, 634, "CC0178");
        new Berth(pnlColchester, 1126, 602, "CC0179");
        new Berth(pnlColchester, 1190, 634, "CC0180");
        new Berth(pnlColchester, 1190, 602, "CC0181");
        new Berth(pnlColchester, 1254, 634, "CC0182");
        new Berth(pnlColchester, 1254, 602, "CC0183");
        new Berth(pnlColchester, 1334, 634, "CC0184");
        new Berth(pnlColchester, 1334, 602, "CC0185");
        new Berth(pnlColchester, 1398, 634, "CC0186");
        new Berth(pnlColchester, 1398, 602, "CC0187");
        new Berth(pnlColchester, 1462, 634, "CC0188");
        new Berth(pnlColchester, 1462, 602, "CC0189");
        new Berth(pnlColchester, 326,  634, "CC1004");
        new Berth(pnlColchester, 326,  602, "CC1005");
        new Berth(pnlColchester, 390,  634, "CC1006", "CC4009");
        new Berth(pnlColchester, 390,  602, "CC1007");
        new Berth(pnlColchester, 454,  634, "CC1008");
        new Berth(pnlColchester, 454,  602, "CC1009", "CC4006");
        new Berth(pnlColchester, 518,  634, "CC1010", "CC4013");
        new Berth(pnlColchester, 518,  602, "CC1011");
        new Berth(pnlColchester, 614,  666, "CC1020");
        new Berth(pnlColchester, 630,  490, "CC1021");
        new Berth(pnlColchester, 614,  634, "CC1022");
        new Berth(pnlColchester, 630,  522, "CC1023");
        new Berth(pnlColchester, 662,  602, "CC1027");
        new Berth(pnlColchester, 598,  538, "CC1028");
        new Berth(pnlColchester, 630,  554, "CC1030");
        new Berth(pnlColchester, 662,  634, "CC1033");
        new Berth(pnlColchester, 710,  458, "CC1034");
        new Berth(pnlColchester, 662,  666, "CC1035");
        new Berth(pnlColchester, 694,  522, "CC1036");
        new Berth(pnlColchester, 678,  586, "CC1039");
        new Berth(pnlColchester, 774,  634, "CC1040");
        new Berth(pnlColchester, 806,  714, "CC1046");
        new Berth(pnlColchester, 758,  458, "CC1047");
        new Berth(pnlColchester, 806,  666, "CC1048");
        new Berth(pnlColchester, 758,  490, "CC1051");
        new Berth(pnlColchester, 806,  570, "CC1054");
        new Berth(pnlColchester, 758,  522, "CC1055");
        new Berth(pnlColchester, 806,  602, "CC1056");
        new Berth(pnlColchester, 726,  545, "CC1057");
        new Berth(pnlColchester, 726,  563, "CC1059");
        new Berth(pnlColchester, 822,  634, "CC1063");
        new Berth(pnlColchester, 870,  570, "CC1065");
        new Berth(pnlColchester, 870,  602, "CC1067");
        new Berth(pnlColchester, 886,  666, "CC1069", "CC1058");
        new Berth(pnlColchester, 870,  698, "CC1071");
        new Berth(pnlColchester, 998,  666, "CC1072", "CC1085");
        new Berth(pnlColchester, 934,  722, "CC1073");
        new Berth(pnlColchester, 998,  634, "CC1074");
        new Berth(pnlColchester, 998,  602, "CC1075");
        new Berth(pnlColchester, 1062, 634, "CC1076");
        new Berth(pnlColchester, 998,  570, "CC1080", "CC1083");
        new Berth(pnlColchester, 614,  602, "CC4010");
        new Berth(pnlColchester, 230,  602, "CCAPDM");
        new Berth(pnlColchester, 230,  634, "CCAPUM");
        new Berth(pnlColchester, 870,  722, "CCP5AR");
        new Berth(pnlColchester, 806,  690, "CCP6AR");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlColchester, 1262, 266, "IPSWICH", 9);

        makeNavButton(pnlColchester, 142,  618, "WITHAM",  4);
        makeNavButton(pnlColchester, 1094, 570, "CLACTON", 8);
        makeNavButton(pnlColchester, 1094, 666, "CLACTON", 8);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(pnlColchester, 802, 746, "COLCHESTER", "COL");
        largeStation(pnlColchester, 276, 194, "MANNINGTREE", "MNG");

        smallStation(pnlColchester, 1616, 410, "DOVERCOURT",   "DVC");
        smallStation(pnlColchester, 1414, 322, "HARWICH INT",  "HPQ");
        smallStation(pnlColchester, 1722, 410, "HARWICH TOWN", "HWC");
        smallStation(pnlColchester, 681,  322, "MISTLEY",      "MIS");
        smallStation(pnlColchester, 950,  322, "WRABNESS",     "WRB");
        //</editor-fold>

        placeTopBits(pnlColchester);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Colchester+Parkeston.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlColchester.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Harlow">
    private void initHarlow()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlHarlow, 122,  186, "WG1061");
        new Berth(pnlHarlow, 122,  218, "WG1062");
        new Berth(pnlHarlow, 186,  186, "WG1063");
        new Berth(pnlHarlow, 186,  218, "WG1064");
        new Berth(pnlHarlow, 250,  186, "WG1065");
        new Berth(pnlHarlow, 250,  218, "WG1066");
        new Berth(pnlHarlow, 314,  186, "WG1067");
        new Berth(pnlHarlow, 314,  218, "WG1068");
        new Berth(pnlHarlow, 378,  186, "WG1069");
        new Berth(pnlHarlow, 378,  218, "WG1070");
        new Berth(pnlHarlow, 442,  186, "WG1071");
        new Berth(pnlHarlow, 442,  218, "WG1072", "WG5315");
        new Berth(pnlHarlow, 554,  186, "WG1073");
        new Berth(pnlHarlow, 554,  218, "WG1074", "WG5319");
        new Berth(pnlHarlow, 618,  186, "WG1077");
        new Berth(pnlHarlow, 618,  218, "WG1080");
        new Berth(pnlHarlow, 746,  186, "WG1081");
        new Berth(pnlHarlow, 858,  186, "WG1083");
        new Berth(pnlHarlow, 922,  186, "WG1085");
        new Berth(pnlHarlow, 682,  218, "WG1086");
        new Berth(pnlHarlow, 746,  218, "WG1088");
        new Berth(pnlHarlow, 986,  186, "WG1089");
        new Berth(pnlHarlow, 858,  218, "WG1090");
        new Berth(pnlHarlow, 1050, 186, "WG1091");
        new Berth(pnlHarlow, 922,  218, "WG1092", "WG5331");
        new Berth(pnlHarlow, 986,  218, "WG1094");
        new Berth(pnlHarlow, 1114, 186, "WG1095");
        new Berth(pnlHarlow, 1178, 186, "WG1097");
        new Berth(pnlHarlow, 1050, 218, "WG1098");
        new Berth(pnlHarlow, 1242, 186, "WG1099");
        new Berth(pnlHarlow, 1114, 218, "WG1100");
        new Berth(pnlHarlow, 1306, 186, "WG1101");
        new Berth(pnlHarlow, 1402, 186, "WG1103");
        new Berth(pnlHarlow, 1178, 218, "WG1104");
        new Berth(pnlHarlow, 1242, 218, "WG1106");
        new Berth(pnlHarlow, 1306, 218, "WG1108");
        new Berth(pnlHarlow, 1466, 186, "WG1109");
        new Berth(pnlHarlow, 1402, 218, "WG1110");
        new Berth(pnlHarlow, 1530, 186, "WG1111");
        new Berth(pnlHarlow, 1594, 186, "WG1115");
        new Berth(pnlHarlow, 1466, 218, "WG1116");
        new Berth(pnlHarlow, 1530, 218, "WG1118");
        new Berth(pnlHarlow, 1690, 186, "WG1121", "WG5342");
        new Berth(pnlHarlow, 1594, 218, "WG1122");
        new Berth(pnlHarlow, 538,  282, "WG1224");
        new Berth(pnlHarlow, 618,  282, "WG1226");
        new Berth(pnlHarlow, 554,  154, "WG1455", "WG1192");
        new Berth(pnlHarlow, 618,  154, "WG5322");
        new Berth(pnlHarlow, 650,  170, "WG1459", "WG5328");
        new Berth(pnlHarlow, 1402, 154, "WG1461");
        new Berth(pnlHarlow, 1626, 154, "WG1463");
        new Berth(pnlHarlow, 1594, 250, "WG1494", "WG1465");
        new Berth(pnlHarlow, 554,  250, "WG1480", "WG5321");
        new Berth(pnlHarlow, 618,  250, "WG1484");
        new Berth(pnlHarlow, 1402, 250, "WG1490");
        new Berth(pnlHarlow, 554,  114, "WG5325");
        new Berth(pnlHarlow, 650,  138, "WG5327");
        new Berth(pnlHarlow, 890,  234, "WG5329");
        new Berth(pnlHarlow, 1594, 274, "WGHMYD");
        new Berth(pnlHarlow, 650,  98,  "WGSDG1");
        new Berth(pnlHarlow, 482,  114, "WGSDG2");

        new Berth(pnlHarlow, 186,  346, "WG1123");
        new Berth(pnlHarlow, 250,  346, "WG1125");
        new Berth(pnlHarlow, 122,  378, "WG1126");
        new Berth(pnlHarlow, 186,  378, "WG1128");
        new Berth(pnlHarlow, 314,  346, "WG1129");
        new Berth(pnlHarlow, 250,  378, "WG1130");
        new Berth(pnlHarlow, 378,  346, "WG1131");
        new Berth(pnlHarlow, 442,  346, "WG1133");
        new Berth(pnlHarlow, 314,  378, "WG1134");
        new Berth(pnlHarlow, 506,  346, "WG1135");
        new Berth(pnlHarlow, 378,  378, "WG1136");
        new Berth(pnlHarlow, 570,  346, "WG1139");
        new Berth(pnlHarlow, 442,  378, "WG1138");
        new Berth(pnlHarlow, 506,  378, "WG1140");
        new Berth(pnlHarlow, 570,  378, "WG1142");
        new Berth(pnlHarlow, 634,  346, "WG1145");
        new Berth(pnlHarlow, 698,  346, "WG1147");
        new Berth(pnlHarlow, 634,  378, "WG1148");
        new Berth(pnlHarlow, 698,  378, "WG1150");
        new Berth(pnlHarlow, 762,  346, "WG1151");
        new Berth(pnlHarlow, 826,  346, "WG1153");
        new Berth(pnlHarlow, 762,  378, "WG1154");
        new Berth(pnlHarlow, 1002, 346, "WG1155");
        new Berth(pnlHarlow, 1098, 346, "WG1157", "WG5396");
        new Berth(pnlHarlow, 1162, 346, "WG1163");
        new Berth(pnlHarlow, 1226, 346, "WG1165");
        new Berth(pnlHarlow, 1290, 346, "WG1167");
        new Berth(pnlHarlow, 1354, 346, "WG1169");
        new Berth(pnlHarlow, 1418, 346, "WG1173");
        new Berth(pnlHarlow, 1514, 346, "WG1175");
        new Berth(pnlHarlow, 1578, 346, "WG1183");
        new Berth(pnlHarlow, 1722, 346, "WG1187");
        new Berth(pnlHarlow, 826,  378, "WG1158");
        new Berth(pnlHarlow, 938,  378, "WG1160");
        new Berth(pnlHarlow, 1098, 378, "WG1162");
        new Berth(pnlHarlow, 1162, 378, "WG1170");
        new Berth(pnlHarlow, 1226, 378, "WG1172");
        new Berth(pnlHarlow, 1290, 378, "WG1174");
        new Berth(pnlHarlow, 1354, 378, "WG1176");
        new Berth(pnlHarlow, 1418, 378, "WG1178", "WG5397");
        new Berth(pnlHarlow, 1578, 378, "WG1184");
        new Berth(pnlHarlow, 1658, 378, "WG1186");
        new Berth(pnlHarlow, 1722, 378, "WG1188");
        new Berth(pnlHarlow, 1002, 378, "WG1281");
        new Berth(pnlHarlow, 938,  346, "WG1284");
        new Berth(pnlHarlow, 1002, 410, "WG1493");
        new Berth(pnlHarlow, 826,  410, "WG1496");
        new Berth(pnlHarlow, 1506, 314, "WG1497", "WG1492");
        new Berth(pnlHarlow, 938,  410, "WG1498");
        new Berth(pnlHarlow, 706,  402, "WG5383");
        new Berth(pnlHarlow, 762,  402, "WG5385");
        new Berth(pnlHarlow, 762,  426, "WG5387");
        new Berth(pnlHarlow, 706,  426, "WGGF03");
        new Berth(pnlHarlow, 762,  450, "WGUPSD");

        new Berth(pnlHarlow, 186,  538, "WG1230");
        new Berth(pnlHarlow, 122,  506, "WG1231");
        new Berth(pnlHarlow, 250,  538, "WG1232");
        new Berth(pnlHarlow, 186,  506, "WG1233");
        new Berth(pnlHarlow, 314,  538, "WG1234");
        new Berth(pnlHarlow, 378,  538, "WG1236");
        new Berth(pnlHarlow, 314,  506, "WG1237");
        new Berth(pnlHarlow, 442,  538, "WG1238");
        new Berth(pnlHarlow, 378,  506, "WG1239");
        new Berth(pnlHarlow, 506,  538, "WG1240");
        new Berth(pnlHarlow, 442,  506, "WG1241");
        new Berth(pnlHarlow, 570,  538, "WG1242");
        new Berth(pnlHarlow, 570,  506, "WG1243");
        new Berth(pnlHarlow, 634,  538, "WG1244");
        new Berth(pnlHarlow, 634,  506, "WG1245");
        new Berth(pnlHarlow, 698,  538, "WG1246");
        new Berth(pnlHarlow, 746,  538, "WG1247");
        new Berth(pnlHarlow, 810,  538, "WG1252");
        new Berth(pnlHarlow, 810,  506, "WG1253");
        new Berth(pnlHarlow, 874,  538, "WG1254");
        new Berth(pnlHarlow, 874,  506, "WG1255");
        new Berth(pnlHarlow, 938,  538, "WG1256");
        new Berth(pnlHarlow, 938,  506, "WG1257");
        new Berth(pnlHarlow, 1018, 506, "WGA262");
        new Berth(pnlHarlow, 1018, 538, "WGA264");
        new Berth(pnlHarlow, 1082, 506, "WGB262");
        new Berth(pnlHarlow, 1082, 538, "WGB264");
        new Berth(pnlHarlow, 1146, 506, "WGR262");
        new Berth(pnlHarlow, 1146, 538, "WGR264");

        new Berth(pnlHarlow, 122,  602, "WG1220", "WG1213");
        new Berth(pnlHarlow, 426,  666, "WG1143");
        new Berth(pnlHarlow, 122,  634, "WG1193");
        new Berth(pnlHarlow, 122,  666, "WG1194");
        new Berth(pnlHarlow, 202,  666, "WG1196");
        new Berth(pnlHarlow, 202,  634, "WG1197");
        new Berth(pnlHarlow, 266,  666, "WG1198");
        new Berth(pnlHarlow, 314,  666, "WG1199");
        new Berth(pnlHarlow, 426,  634, "WG1201");
        new Berth(pnlHarlow, 378,  634, "WG1202");
        new Berth(pnlHarlow, 378,  666, "WG1366");
        new Berth(pnlHarlow, 538,  666, "WG1168");
        new Berth(pnlHarlow, 522,  698, "WGA206");
        new Berth(pnlHarlow, 522,  634, "WGA370");
        new Berth(pnlHarlow, 586,  698, "WGB206");
        new Berth(pnlHarlow, 586,  634, "WGB370");
        new Berth(pnlHarlow, 650,  698, "WGC206");
        new Berth(pnlHarlow, 650,  634, "WGC370");
        new Berth(pnlHarlow, 714,  666, "WGR168");
        new Berth(pnlHarlow, 714,  698, "WGR206");
        new Berth(pnlHarlow, 714,  634, "WGR370");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlHarlow, 102, 250, "BRIMSDOWN", 5);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlHarlow, 548,  138, "BROXBOURNE",     "BXB");
        smallStation(pnlHarlow, 973,  314, "BISHOPS",        "BIS"); // STORTFORD
        smallStation(pnlHarlow, 1690, 250, "HARLOW MILL",    "HWM");
        smallStation(pnlHarlow, 1393, 274, "HARLOW TOWN",    "HWN");
        smallStation(pnlHarlow, 1406, 322, "MOUNTFITCHET",   "SST"); // STANSTED
        smallStation(pnlHarlow, 1056, 250, "ROYDON",         "RYN");
        smallStation(pnlHarlow, 360,  322, "SAWBRIDGEWORTH", "SAW");
        smallStation(pnlHarlow, 1418, 314, "STANSTED",       "SST"); // MOUNTFITCHET
        smallStation(pnlHarlow, 967,  322, "STORTFORD",      "BIS"); // BISHOPS

        smallStation(pnlHarlow, 1039, 570, "HERTFORD EAST", "HFE");
        smallStation(pnlHarlow, 183,  570, "RYE HOUSE",     "RYH");
        smallStation(pnlHarlow, 430,  570, "ST MARGARETS",  "SMT");
        smallStation(pnlHarlow, 734,  570, "WARE",          "WAR");

        smallStation(pnlHarlow, 569,  722, "STANSTED AIRPORT", "SSD");
        //</editor-fold>

        placeTopBits(pnlHarlow);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Harlow.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlHarlow.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Hackney">
    private void initHackney()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        //Cambs heath - Clapton Jnc
        new Berth(pnlHackney, 346,  186, "LS0120");
        new Berth(pnlHackney, 346,  154, "LS0121");
        new Berth(pnlHackney, 346,  250, "LS0122");
        new Berth(pnlHackney, 346,  218, "LS0123");
        new Berth(pnlHackney, 426,  186, "LS0124");
        new Berth(pnlHackney, 426,  154, "LS0125");
        new Berth(pnlHackney, 426,  250, "LS0126");
        new Berth(pnlHackney, 426,  218, "LS0127");
        new Berth(pnlHackney, 554,  186, "LS0128", "LS1271");
        new Berth(pnlHackney, 490,  154, "LS0129");
        new Berth(pnlHackney, 554,  250, "LS0130");
        new Berth(pnlHackney, 490,  218, "LS0131");
        new Berth(pnlHackney, 618,  186, "LS0132");
        new Berth(pnlHackney, 618,  250, "LS0134");
        new Berth(pnlHackney, 554,  154, "LS0135");
        new Berth(pnlHackney, 666,  122, "LS0136");
        new Berth(pnlHackney, 554,  218, "LS0137");
        new Berth(pnlHackney, 698,  154, "LS0139");
        new Berth(pnlHackney, 794,  186, "LS0140");
        new Berth(pnlHackney, 698,  218, "LS0141");
        new Berth(pnlHackney, 794,  250, "LS0142");
        new Berth(pnlHackney, 794,  154, "LS0145");
        new Berth(pnlHackney, 794,  218, "LS0147");
        new Berth(pnlHackney, 890,  250, "LS0156");
        new Berth(pnlHackney, 890,  218, "LS0157");
        new Berth(pnlHackney, 954,  250, "LS0158");
        new Berth(pnlHackney, 954,  218, "LS0159");
        new Berth(pnlHackney, 1018, 250, "LS0160");
        new Berth(pnlHackney, 1018, 218, "LS0161");
        new Berth(pnlHackney, 1082, 250, "LS0162");
        new Berth(pnlHackney, 1146, 250, "LS0164");
        new Berth(pnlHackney, 1082, 218, "LS0165");
        new Berth(pnlHackney, 1210, 250, "LS0166");
        new Berth(pnlHackney, 1146, 218, "LS0167");
        new Berth(pnlHackney, 1322, 170, "LS0170");
        new Berth(pnlHackney, 890,  186, "LS1302");
        new Berth(pnlHackney, 890,  154, "LS1303");
        new Berth(pnlHackney, 954,  186, "LS1304");
        new Berth(pnlHackney, 954,  154, "LS1305");
        new Berth(pnlHackney, 1322, 250, "LS1404");
        new Berth(pnlHackney, 1322, 218, "LS1405");
        new Berth(pnlHackney, 1386, 218, "LS1407");

        //Clpton Jnc - Chingford
        new Berth(pnlHackney, 106,  778, "LS1408");
        new Berth(pnlHackney, 170,  778, "LS1410");
        new Berth(pnlHackney, 106,  746, "LS1411");
        new Berth(pnlHackney, 234,  778, "LS1412");
        new Berth(pnlHackney, 202,  746, "LS1413");
        new Berth(pnlHackney, 298,  778, "LS1414");
        new Berth(pnlHackney, 298,  746, "LS1415");
        new Berth(pnlHackney, 362,  778, "LS1418");
        new Berth(pnlHackney, 426,  746, "LS1419");
        new Berth(pnlHackney, 426,  778, "LS1420");
        new Berth(pnlHackney, 490,  746, "LS1421");
        new Berth(pnlHackney, 490,  778, "LS1422");
        new Berth(pnlHackney, 554,  746, "LS1423");
        new Berth(pnlHackney, 554,  778, "LS1424");
        new Berth(pnlHackney, 618,  746, "LS1425");
        new Berth(pnlHackney, 682,  746, "LS1427");
        new Berth(pnlHackney, 618,  778, "LS1428");
        new Berth(pnlHackney, 746,  746, "LS1429");
        new Berth(pnlHackney, 682,  778, "LS1430");
        new Berth(pnlHackney, 874,  746, "LS1431");
        new Berth(pnlHackney, 938,  746, "LS1433");
        new Berth(pnlHackney, 746,  778, "LS1432");
        new Berth(pnlHackney, 810,  778, "LS1434");
        new Berth(pnlHackney, 874,  778, "LS1438");
        new Berth(pnlHackney, 938,  778, "LS1440", "LS5557");
        new Berth(pnlHackney, 1050, 698, "LS1442");
        new Berth(pnlHackney, 970,  698, "LS5561");
        new Berth(pnlHackney, 1098, 714, "LSA444");
        new Berth(pnlHackney, 1098, 746, "LSA446");
        new Berth(pnlHackney, 1098, 778, "LSA448");
        new Berth(pnlHackney, 1162, 714, "LSB444");
        new Berth(pnlHackney, 1162, 746, "LSB446");
        new Berth(pnlHackney, 1162, 778, "LSB448");
        new Berth(pnlHackney, 1226, 714, "LSR444");
        new Berth(pnlHackney, 1226, 746, "LSR446");
        new Berth(pnlHackney, 1226, 778, "LSR448");
        new Berth(pnlHackney, 1122, 698, "LSCHAC");
        new Berth(pnlHackney, 898,  698, "LSCHAL");

        //Hackney - Bury St Jnc
        new Berth(pnlHackney, 138,  362, "WG1306");
        new Berth(pnlHackney, 138,  330, "WG1307");
        new Berth(pnlHackney, 202,  362, "WG1308");
        new Berth(pnlHackney, 266,  362, "WG1310");
        new Berth(pnlHackney, 202,  330, "WG1311");
        new Berth(pnlHackney, 330,  362, "WG1312");
        new Berth(pnlHackney, 266,  330, "WG1315");
        new Berth(pnlHackney, 394,  362, "WG1316");
        new Berth(pnlHackney, 330,  330, "WG1317");
        new Berth(pnlHackney, 458,  362, "WG1318");
        new Berth(pnlHackney, 394,  330, "WG1319");
        new Berth(pnlHackney, 522,  362, "WG1320");
        new Berth(pnlHackney, 458,  330, "WG1321");
        new Berth(pnlHackney, 586,  362, "WG1324");
        new Berth(pnlHackney, 586,  330, "WG1325");
        new Berth(pnlHackney, 666,  362, "WG1326");
        new Berth(pnlHackney, 618,  378, "WG1327");
        new Berth(pnlHackney, 666,  330, "WG1329", "WG5514");
        new Berth(pnlHackney, 794,  362, "WG1330");
        new Berth(pnlHackney, 730,  330, "WG1331");
        new Berth(pnlHackney, 858,  362, "WG1332");
        new Berth(pnlHackney, 794,  330, "WG1333");
        new Berth(pnlHackney, 922,  362, "WG1334");
        new Berth(pnlHackney, 986,  362, "WG1336");
        new Berth(pnlHackney, 858,  330, "WG1337");
        new Berth(pnlHackney, 1050, 362, "WG1338");
        new Berth(pnlHackney, 922,  330, "WG1339");
        new Berth(pnlHackney, 1114, 362, "WG1340");
        new Berth(pnlHackney, 986,  330, "WG1341");
        new Berth(pnlHackney, 1178, 362, "WG1342");
        new Berth(pnlHackney, 1242, 362, "WG1344");
        new Berth(pnlHackney, 1050, 330, "WG1345");
        new Berth(pnlHackney, 1306, 362, "WG1346");
        new Berth(pnlHackney, 1114, 330, "WG1347");
        new Berth(pnlHackney, 1370, 362, "WG1348");
        new Berth(pnlHackney, 1178, 330, "WG1349");
        new Berth(pnlHackney, 1242, 330, "WG1351");
        new Berth(pnlHackney, 1370, 330, "WG1353");
        new Berth(pnlHackney, 1514, 458, "WG1368");
        new Berth(pnlHackney, 1450, 426, "WG1371");
        new Berth(pnlHackney, 1578, 458, "WG1372");
        new Berth(pnlHackney, 1514, 426, "WG1373");
        new Berth(pnlHackney, 1578, 426, "WG1375");

        //Bury St Jnc - Enfield Town
        new Berth(pnlHackney, 1386, 762, "WG1350");
        new Berth(pnlHackney, 1450, 762, "WG1352");
        new Berth(pnlHackney, 1514, 762, "WG1356");
        new Berth(pnlHackney, 1626, 730, "WGA360");
        new Berth(pnlHackney, 1626, 762, "WGA362");
        new Berth(pnlHackney, 1626, 794, "WGA364");
        new Berth(pnlHackney, 1690, 730, "WGB360");
        new Berth(pnlHackney, 1690, 762, "WGB362");
        new Berth(pnlHackney, 1690, 794, "WGB364");
        new Berth(pnlHackney, 1754, 730, "WGR360");
        new Berth(pnlHackney, 1754, 762, "WGR362");
        new Berth(pnlHackney, 1754, 794, "WGR364");
        new Berth(pnlHackney, 1450, 330, "WG1361");
        new Berth(pnlHackney, 1386, 730, "WG1363");
        new Berth(pnlHackney, 1514, 730, "WG1365");

        //Temple Mills/Southbury - Cheshunt
        new Berth(pnlHackney, 106,  602, "WG1003");
        new Berth(pnlHackney, 170,  602, "WG1005");
        new Berth(pnlHackney, 394,  570, "WG1004");
        new Berth(pnlHackney, 202,  522, "WG1007");
        new Berth(pnlHackney, 266,  602, "WG1009");
        new Berth(pnlHackney, 266,  634, "WG1010");
        new Berth(pnlHackney, 330,  602, "WG1011");
        new Berth(pnlHackney, 330,  634, "WG1012");
        new Berth(pnlHackney, 426,  602, "WG1013");
        new Berth(pnlHackney, 490,  634, "WG1014");
        new Berth(pnlHackney, 490,  602, "WG1015", "WG1016");
        new Berth(pnlHackney, 554,  602, "WG1017");
        new Berth(pnlHackney, 554,  634, "WG1018");
        new Berth(pnlHackney, 618,  634, "WG1020");
        new Berth(pnlHackney, 618,  602, "WG1021");
        new Berth(pnlHackney, 682,  634, "WG1022");
        new Berth(pnlHackney, 682,  602, "WG1023");
        new Berth(pnlHackney, 746,  602, "WG1025");
        new Berth(pnlHackney, 746,  634, "WG1026");
        new Berth(pnlHackney, 810,  602, "WG1027");
        new Berth(pnlHackney, 810,  634, "WG1028");
        new Berth(pnlHackney, 874,  602, "WG1029");
        new Berth(pnlHackney, 874,  634, "WG1030");
        new Berth(pnlHackney, 938,  602, "WG1031");
        new Berth(pnlHackney, 1002, 602, "WG1033");
        new Berth(pnlHackney, 938,  634, "WG1034");
        new Berth(pnlHackney, 1002, 634, "WG1036");
        new Berth(pnlHackney, 1066, 602, "WG1037");
        new Berth(pnlHackney, 1066, 634, "WG1038");
        new Berth(pnlHackney, 1130, 602, "WG1039");
        new Berth(pnlHackney, 1130, 634, "WG1040");
        new Berth(pnlHackney, 1194, 602, "WG1041");
        new Berth(pnlHackney, 1194, 634, "WG1042");
        new Berth(pnlHackney, 1274, 634, "WG1044");
        new Berth(pnlHackney, 1274, 602, "WG1045");
        new Berth(pnlHackney, 1338, 634, "WG1046");
        new Berth(pnlHackney, 1338, 602, "WG1047");
        new Berth(pnlHackney, 1402, 602, "WG1049");
        new Berth(pnlHackney, 1402, 634, "WG1050");
        new Berth(pnlHackney, 1466, 634, "WG1052");
        new Berth(pnlHackney, 1466, 602, "WG1053");
        new Berth(pnlHackney, 1530, 634, "WG1054");
        new Berth(pnlHackney, 1530, 602, "WG1055");
        new Berth(pnlHackney, 1594, 634, "WG1056");
        new Berth(pnlHackney, 1594, 602, "WG1057");
        new Berth(pnlHackney, 1706, 602, "WG1059", "WG1058");
        new Berth(pnlHackney, 1706, 634, "WG1060");
        new Berth(pnlHackney, 1706, 570, "WG1144");
        new Berth(pnlHackney, 890,  538, "WG1374");
        new Berth(pnlHackney, 954,  538, "WG1376");
        new Berth(pnlHackney, 890,  506, "WG1377");
        new Berth(pnlHackney, 1082, 538, "WG1378");
        new Berth(pnlHackney, 954,  506, "WG1379");
        new Berth(pnlHackney, 1146, 538, "WG1380");
        new Berth(pnlHackney, 1018, 506, "WG1381");
        new Berth(pnlHackney, 1210, 538, "WG1382");
        new Berth(pnlHackney, 1082, 506, "WG1383");
        new Berth(pnlHackney, 1274, 538, "WG1384");
        new Berth(pnlHackney, 1146, 506, "WG1385");
        new Berth(pnlHackney, 1338, 538, "WG1386");
        new Berth(pnlHackney, 1210, 506, "WG1387");
        new Berth(pnlHackney, 1402, 538, "WG1388");
        new Berth(pnlHackney, 1338, 506, "WG1389");
        new Berth(pnlHackney, 1466, 538, "WG1390");
        new Berth(pnlHackney, 1402, 506, "WG1391");
        new Berth(pnlHackney, 1530, 538, "WG1392");
        new Berth(pnlHackney, 1466, 506, "WG1393");
        new Berth(pnlHackney, 1594, 538, "WG1394");
        new Berth(pnlHackney, 1594, 506, "WG1395");
        new Berth(pnlHackney, 1762, 570, "WGR144");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlHackney, 150, 202, "LIVERPOOL ST", 1);
        makeNavButton(pnlHackney, 38,  666, "STRATFORD",    1);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlHackney, 746,  578, "ANGEL RD",       "AGR");
        smallStation(pnlHackney, 1191, 578, "BRIMSDOWN",      "BMD");
        smallStation(pnlHackney, 849,  306, "BRUCE GROVE",    "BVC");
        smallStation(pnlHackney, 1383, 698, "BUSH HILL",      "BHK"); // PARK
        smallStation(pnlHackney, 383,  122, "CAMBRIDGE",      "CBH"); // HEATH
        smallStation(pnlHackney, 301,  722, "CENTRAL",        "WHC"); // WALTHAMSTOW
        smallStation(pnlHackney, 1706, 666, "CHESHUNT",       "CHN");
        smallStation(pnlHackney, 1131, 810, "CHINGFORD",      "CHI");
        smallStation(pnlHackney, 1085, 194, "CLAPTON",        "CPT");
        smallStation(pnlHackney, 803,  130, "DOWNS",          "HAC"); // HACKNEY
        smallStation(pnlHackney, 1242, 298, "EDMONTON",       "EDR"); // GREEN
        smallStation(pnlHackney, 1326, 578, "ENFIELD LOCK",   "ENL");
        smallStation(pnlHackney, 1650, 826, "ENFIELD TOWN",   "ENF");
        smallStation(pnlHackney, 560,  130, "FIELDS",         "LOF"); // LONDON
        smallStation(pnlHackney, 1251, 306, "GREEN",          "EDR"); // EDMONTON
        smallStation(pnlHackney, 1219, 482, "GROVE",          "TEO"); // THEOBALDS
        smallStation(pnlHackney, 797,  122, "HACKNEY",        "HAC"); // DOWNS
        smallStation(pnlHackney, 502,  578, "HALE",           "TOM"); // TOTTENAM
        smallStation(pnlHackney, 395,  130, "HEATH",          "CBH"); // CAMBRIDGE
        smallStation(pnlHackney, 749,  714, "HIGHAMS",        "HIP"); // PARK
        smallStation(pnlHackney, 470,  306, "HILL",           "HIP"); // STAMFORD
        smallStation(pnlHackney, 560,  122, "LONDON",         "LOF"); // FIELDS
        smallStation(pnlHackney, 263,  306, "NEWINGTON",      "SKW"); // STOKE
        smallStation(pnlHackney, 600,  570, "NORTHUMBERLAND", "NUM"); // PARK
        smallStation(pnlHackney, 1398, 706, "PARK",           "BHK"); // BUSH HILL
        smallStation(pnlHackney, 758,  722, "PARK",           "HIP"); // HIGHAMS
        smallStation(pnlHackney, 630,  578, "PARK",           "NUM"); // NORTHUMBERLAND
        smallStation(pnlHackney, 1057, 578, "PONDERS END",    "PON");
        smallStation(pnlHackney, 132,  306, "RECTORY RD",     "REC");
        smallStation(pnlHackney, 675,  298, "SEVEN",          "SVS"); // SISTERS
        smallStation(pnlHackney, 1111, 306, "SILVER ST",      "SLV");
        smallStation(pnlHackney, 669,  306, "SISTERS",        "SVS"); // SEVEN
        smallStation(pnlHackney, 887,  482, "SOUTHBURY",      "SBU");
        smallStation(pnlHackney, 161,  722, "ST JAMES ST",    "SJS");
        smallStation(pnlHackney, 458,  298, "STAMFORD",       "SMH"); // HILL
        smallStation(pnlHackney, 275,  298, "STOKE",          "SKW"); // NEWINGTON
        smallStation(pnlHackney, 1463, 482, "TURKEY ST",      "TUR");
        smallStation(pnlHackney, 1207, 474, "THEOBALDS",      "TEO"); // GROVE
        smallStation(pnlHackney, 487,  570, "TOTTENHAM",      "TOM"); // HALE
        smallStation(pnlHackney, 289,  714, "WALTHAMSTOW",    "WHC"); // CENTRAL
        smallStation(pnlHackney, 1451, 578, "WALTHAM CROSS",  "WLC");
        smallStation(pnlHackney, 971,  306, "WHITE HART LN",  "WHL");
        smallStation(pnlHackney, 493,  722, "WOOD ST",        "WST");
        //</editor-fold>

        placeTopBits(pnlHackney);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Hackney+Brimsdown.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlHackney.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Witham">
    private void initWitham()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlWitham, 186,  202, "SE0747");
        new Berth(pnlWitham, 250,  202, "SE0751", "SE0794");
        new Berth(pnlWitham, 314,  202, "SE0755");
        new Berth(pnlWitham, 378,  202, "SE0759");
        new Berth(pnlWitham, 442,  202, "SE0761");
        new Berth(pnlWitham, 602,  170, "SE0765");
        new Berth(pnlWitham, 666,  138, "SE0767");
        new Berth(pnlWitham, 602,  202, "SE0769");
        new Berth(pnlWitham, 602,  234, "SE0771");
        new Berth(pnlWitham, 602,  266, "SE0773");
        new Berth(pnlWitham, 682,  170, "SE0779", "SE0820");
        new Berth(pnlWitham, 682,  202, "SE0781", "SE0822");
        new Berth(pnlWitham, 746,  202, "SE0785", "SE0828");
        new Berth(pnlWitham, 810,  202, "SE0787");
        new Berth(pnlWitham, 874,  202, "SE0789");
        new Berth(pnlWitham, 938,  202, "SE0791");
        new Berth(pnlWitham, 186,  234, "SE0792");
        new Berth(pnlWitham, 250,  234, "SE0796", "SE0753");
        new Berth(pnlWitham, 1018, 202, "SE0799", "SE0844");
        new Berth(pnlWitham, 314,  234, "SE0800");
        new Berth(pnlWitham, 1082, 202, "SE0801");
        new Berth(pnlWitham, 1146, 202, "SE0803");
        new Berth(pnlWitham, 378,  234, "SE0804");
        new Berth(pnlWitham, 1210, 202, "SE0805", "SE0854");
        new Berth(pnlWitham, 442,  234, "SE0806", "SE0763");
        new Berth(pnlWitham, 538,  170, "SE0810");
        new Berth(pnlWitham, 538,  202, "SE0812");
        new Berth(pnlWitham, 1274, 202, "SE0815");
        new Berth(pnlWitham, 538,  234, "SE0814");
        new Berth(pnlWitham, 538,  266, "SE0816");
        new Berth(pnlWitham, 1338, 202, "SE0817");
        new Berth(pnlWitham, 714,  138, "SE0818");
        new Berth(pnlWitham, 1322, 154, "SE0823");
        new Berth(pnlWitham, 682,  234, "SE0824");
        new Berth(pnlWitham, 682,  266, "SE0826");
        new Berth(pnlWitham, 1402, 202, "SE0827", "SE0862");
        new Berth(pnlWitham, 746,  234, "SE0830");
        new Berth(pnlWitham, 810,  234, "SE0832");
        new Berth(pnlWitham, 874,  234, "SE0834");
        new Berth(pnlWitham, 938,  234, "SE0836", "SE0793");
        new Berth(pnlWitham, 1498, 202, "SE0837", "SE0884");
        new Berth(pnlWitham, 1018, 234, "SE0846");
        new Berth(pnlWitham, 1082, 234, "SE0848");
        new Berth(pnlWitham, 1146, 234, "SE0850", "SE0807");
        new Berth(pnlWitham, 1210, 234, "SE0856");
        new Berth(pnlWitham, 1274, 234, "SE0858");
        new Berth(pnlWitham, 1338, 234, "SE0860", "SE0819");
        new Berth(pnlWitham, 1402, 234, "SE0864", "SE0829");
        new Berth(pnlWitham, 1402, 170, "SE0868", "SE0825");
        new Berth(pnlWitham, 1562, 202, "SE0872");
        new Berth(pnlWitham, 1498, 234, "SE0874");
        new Berth(pnlWitham, 1498, 266, "SE0876", "SE5203");
        new Berth(pnlWitham, 1498, 298, "SE0878");
        new Berth(pnlWitham, 1570, 298, "SEUSDG");
        new Berth(pnlWitham, 1562, 234, "SE0882", "SE0839");
        new Berth(pnlWitham, 906,  138, "SE0896");
        new Berth(pnlWitham, 970,  138, "SER896");
        new Berth(pnlWitham, 1178, 154, "SE0898");
        new Berth(pnlWitham, 1114, 154, "SER898");
        new Berth(pnlWitham, 602,  290, "SE5189");
        new Berth(pnlWitham, 1466, 170, "SE5202");
        new Berth(pnlWitham, 810,  138, "SECRES");
        new Berth(pnlWitham, 1538, 170, "SEDEPO");
        new Berth(pnlWitham, 1250, 154, "SESHAL");

        new Berth(pnlWitham, 250,  394, "SE0669");
        new Berth(pnlWitham, 314,  394, "SE0671");
        new Berth(pnlWitham, 378,  362, "SE0675");
        new Berth(pnlWitham, 378,  394, "SE0677");
        new Berth(pnlWitham, 442,  394, "SE0683", "SE0712");
        new Berth(pnlWitham, 506,  394, "SE0685", "SE0716");
        new Berth(pnlWitham, 586,  394, "SE0691");
        new Berth(pnlWitham, 650,  394, "SE0693", "SE0724");
        new Berth(pnlWitham, 714,  394, "SE0695");
        new Berth(pnlWitham, 778,  394, "SE0697");
        new Berth(pnlWitham, 906,  394, "SE0703", "SE0736");
        new Berth(pnlWitham, 970,  394, "SE0705");
        new Berth(pnlWitham, 378,  426, "SE0710", "SE0679");
        new Berth(pnlWitham, 1034, 394, "SE0711");
        new Berth(pnlWitham, 442,  426, "SE0714");
        new Berth(pnlWitham, 1098, 394, "SE0715", "SE0758");
        new Berth(pnlWitham, 506,  426, "SE0718", "SE0687");
        new Berth(pnlWitham, 586,  426, "SE0722");
        new Berth(pnlWitham, 1162, 362, "SE0723", "SE0762");
        new Berth(pnlWitham, 1162, 394, "SE0725", "SE0764");
        new Berth(pnlWitham, 650,  426, "SE0726");
        new Berth(pnlWitham, 1242, 394, "SE0729", "SE0770");
        new Berth(pnlWitham, 714,  426, "SE0730");
        new Berth(pnlWitham, 1306, 394, "SE0731");
        new Berth(pnlWitham, 778,  426, "SE0732", "SE0701");
        new Berth(pnlWitham, 1370, 394, "SE0733");
        new Berth(pnlWitham, 842,  426, "SE0734");
        new Berth(pnlWitham, 1434, 394, "SE0735");
        new Berth(pnlWitham, 1514, 394, "SE0739", "SE0786");
        new Berth(pnlWitham, 906,  426, "SE0740");
        new Berth(pnlWitham, 1578, 394, "SE0741");
        new Berth(pnlWitham, 970,  426, "SE0742");
        new Berth(pnlWitham, 1034, 426, "SE0756", "SE0713");
        new Berth(pnlWitham, 1098, 426, "SE0760", "SE0717");
        new Berth(pnlWitham, 1162, 426, "SE0766", "SE0727");
        new Berth(pnlWitham, 1242, 426, "SE0772");
        new Berth(pnlWitham, 1306, 426, "SE0778");
        new Berth(pnlWitham, 1370, 426, "SE0782");
        new Berth(pnlWitham, 1434, 426, "SE0784", "SE0737");
        new Berth(pnlWitham, 1514, 426, "SE0788");
        new Berth(pnlWitham, 1578, 426, "SE0790");
        new Berth(pnlWitham, 1162, 330, "SE5186");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlWitham, 1650, 218, "COLCHESTER", 7);
        makeNavButton(pnlWitham, 102,  410, "SHENFIELD",  3);
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlWitham, 907,  122, "BRAINTREE",   "BTR");
        smallStation(pnlWitham, 1092, 458, "CHELMSFORD",  "CHM");
        smallStation(pnlWitham, 250,  266, "HATFIELD",    "HAP");
        smallStation(pnlWitham, 433,  458, "INGATESTONE", "INT");
        smallStation(pnlWitham, 1018, 266, "KELVEDON",    "KEL");
        smallStation(pnlWitham, 1399, 154, "MARKS TEY",   "MKT");
        smallStation(pnlWitham, 253,  274, "PEVERAL",     "HAP");
        smallStation(pnlWitham, 1177, 130, "SUDBURY",     "SUY");
        smallStation(pnlWitham, 576,  154, "WITHAM",      "WTM");
        //</editor-fold>

        placeTopBits(pnlWitham);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Witham.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlWitham.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Shenfield">
    private void initShenfield()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlShenfield, 234,  154, "SE0445");
        new Berth(pnlShenfield, 298,  154, "SE0447");
        new Berth(pnlShenfield, 298,  218, "SE0449");
        new Berth(pnlShenfield, 362,  154, "SE0451");
        new Berth(pnlShenfield, 362,  218, "SE0453");
        new Berth(pnlShenfield, 426,  154, "SE0455");
        new Berth(pnlShenfield, 426,  218, "SE0457");
        new Berth(pnlShenfield, 490,  154, "SE0459");
        new Berth(pnlShenfield, 490,  218, "SE0461");
        new Berth(pnlShenfield, 554,  154, "SE0463");
        new Berth(pnlShenfield, 554,  218, "SE0465");
        new Berth(pnlShenfield, 618,  154, "SE0467");
        new Berth(pnlShenfield, 618,  218, "SE0469");
        new Berth(pnlShenfield, 682,  154, "SE0471");
        new Berth(pnlShenfield, 682,  218, "SE0473");
        new Berth(pnlShenfield, 746,  154, "SE0475");
        new Berth(pnlShenfield, 746,  218, "SE0477");
        new Berth(pnlShenfield, 298,  186, "SE0478");
        new Berth(pnlShenfield, 810,  154, "SE0479");
        new Berth(pnlShenfield, 298,  250, "SE0480");
        new Berth(pnlShenfield, 810,  218, "SE0481");
        new Berth(pnlShenfield, 362,  186, "SE0482");
        new Berth(pnlShenfield, 874,  154, "SE0483");
        new Berth(pnlShenfield, 362,  250, "SE0484");
        new Berth(pnlShenfield, 874,  218, "SE0485");
        new Berth(pnlShenfield, 938,  154, "SE0487");
        new Berth(pnlShenfield, 938,  218, "SE0489");
        new Berth(pnlShenfield, 490,  186, "SE0486");
        new Berth(pnlShenfield, 490,  250, "SE0488");
        new Berth(pnlShenfield, 554,  186, "SE0490");
        new Berth(pnlShenfield, 1034, 154, "SE0491");
        new Berth(pnlShenfield, 554,  250, "SE0492");
        new Berth(pnlShenfield, 1034, 218, "SE0493");
        new Berth(pnlShenfield, 618,  186, "SE0494");
        new Berth(pnlShenfield, 1242, 154, "SE0495");
        new Berth(pnlShenfield, 618,  250, "SE0496");
        new Berth(pnlShenfield, 1242, 186, "SE0497");
        new Berth(pnlShenfield, 746,  186, "SE0498");
        new Berth(pnlShenfield, 1242, 218, "SE0499");
        new Berth(pnlShenfield, 746,  250, "SE0500");
        new Berth(pnlShenfield, 1242, 250, "SE0501");
        new Berth(pnlShenfield, 810,  186, "SE0502");
        new Berth(pnlShenfield, 1242, 282, "SE0503");
        new Berth(pnlShenfield, 810,  250, "SE0504");
        new Berth(pnlShenfield, 938,  186, "SE0506");
        new Berth(pnlShenfield, 938,  250, "SE0508");
        new Berth(pnlShenfield, 1450, 218, "SE0509", "SE0536");
        new Berth(pnlShenfield, 1034, 282, "SE0510");
        new Berth(pnlShenfield, 1034, 186, "SE0512");
        new Berth(pnlShenfield, 1514, 218, "SE0513", "SE0540");
        new Berth(pnlShenfield, 1034, 250, "SE0514");
        new Berth(pnlShenfield, 1370, 282, "SE0515");
        new Berth(pnlShenfield, 1178, 154, "SE0516");
        new Berth(pnlShenfield, 1178, 186, "SE0518");
        new Berth(pnlShenfield, 1178, 250, "SE0522");
        new Berth(pnlShenfield, 1178, 282, "SE0524");
        new Berth(pnlShenfield, 1434, 154, "SE0528");
        new Berth(pnlShenfield, 1482, 186, "SE0505");
        new Berth(pnlShenfield, 1418, 186, "SE0530", "SE0507");
        new Berth(pnlShenfield, 1370, 250, "SE0534");
        new Berth(pnlShenfield, 1450, 250, "SE0538", "SE0511");
        new Berth(pnlShenfield, 1514, 250, "SE0542");
        new Berth(pnlShenfield, 1578, 250, "SE0544");
        new Berth(pnlShenfield, 1482, 298, "SE0550");
        new Berth(pnlShenfield, 1322, 314, "SE0552");
        new Berth(pnlShenfield, 1386, 314, "SE0554");
        new Berth(pnlShenfield, 1178, 218, "SE5132");
        new Berth(pnlShenfield, 1242, 314, "SE5134");
        new Berth(pnlShenfield, 1082, 122, "SE5137");
        new Berth(pnlShenfield, 1098, 282, "SE5139");
        new Berth(pnlShenfield, 1358, 202, "SEA136");
        new Berth(pnlShenfield, 1358, 170, "SEA138");
        new Berth(pnlShenfield, 1002, 122, "SEDNSG");
        new Berth(pnlShenfield, 1306, 122, "SER136");
        new Berth(pnlShenfield, 1306, 98,  "SER138");
        new Berth(pnlShenfield, 1306, 202, "SESMS1");
        new Berth(pnlShenfield, 1306, 170, "SESMS2");
        new Berth(pnlShenfield, 1242, 338, "SEUPSG");

        new Berth(pnlShenfield, 202,  410, "SE0517");
        new Berth(pnlShenfield, 202,  442, "SE0519");
        new Berth(pnlShenfield, 282,  442, "SE0521", "SE0558");
        new Berth(pnlShenfield, 346,  442, "SE0523");
        new Berth(pnlShenfield, 410,  442, "SE0525");
        new Berth(pnlShenfield, 474,  442, "SE0527");
        new Berth(pnlShenfield, 538,  442, "SE0529");
        new Berth(pnlShenfield, 602,  442, "SE0533");
        new Berth(pnlShenfield, 682,  442, "SE0537", "SE0572");
        new Berth(pnlShenfield, 746,  442, "SE0539");
        new Berth(pnlShenfield, 810,  442, "SE0541");
        new Berth(pnlShenfield, 938,  442, "SE0543");
        new Berth(pnlShenfield, 1002, 442, "SE0545");
        new Berth(pnlShenfield, 1066, 442, "SE0547");
        new Berth(pnlShenfield, 1130, 442, "SE0549");
        new Berth(pnlShenfield, 1258, 506, "SE0555");
        new Berth(pnlShenfield, 202,  474, "SE0556");
        new Berth(pnlShenfield, 1258, 410, "SE0557");
        new Berth(pnlShenfield, 1258, 442, "SE0559", "SE0594");
        new Berth(pnlShenfield, 282,  474, "SE0560");
        new Berth(pnlShenfield, 346,  474, "SE0562");
        new Berth(pnlShenfield, 410,  474, "SE0564");
        new Berth(pnlShenfield, 474,  474, "SE0566");
        new Berth(pnlShenfield, 538,  474, "SE0568");
        new Berth(pnlShenfield, 1354, 442, "SE0569", "SE0602");
        new Berth(pnlShenfield, 602,  474, "SE0570", "SE0535");
        new Berth(pnlShenfield, 1418, 442, "SE0571");
        new Berth(pnlShenfield, 1482, 442, "SE0573");
        new Berth(pnlShenfield, 682,  474, "SE0574");
        new Berth(pnlShenfield, 1546, 442, "SE0575");
        new Berth(pnlShenfield, 746,  474, "SE0576");
        new Berth(pnlShenfield, 1610, 442, "SE0577");
        new Berth(pnlShenfield, 810,  474, "SE0578");
        new Berth(pnlShenfield, 1674, 442, "SE0579");
        new Berth(pnlShenfield, 874,  474, "SE0580");
        new Berth(pnlShenfield, 1738, 442, "SE0581");
        new Berth(pnlShenfield, 938,  474, "SE0582");
        new Berth(pnlShenfield, 186,  570, "SE0583");
        new Berth(pnlShenfield, 1002, 474, "SE0584");
        new Berth(pnlShenfield, 250,  570, "SE0585");
        new Berth(pnlShenfield, 1066, 474, "SE0586");
        new Berth(pnlShenfield, 314,  570, "SE0587");
        new Berth(pnlShenfield, 1130, 474, "SE0588", "SE0551");
        new Berth(pnlShenfield, 378,  570, "SE0589");
        new Berth(pnlShenfield, 442,  570, "SE0591");
        new Berth(pnlShenfield, 1258, 474, "SE0592", "SE0553");
        new Berth(pnlShenfield, 506,  570, "SE0593");
        new Berth(pnlShenfield, 570,  570, "SE0595");
        new Berth(pnlShenfield, 1354, 410, "SE0596");
        new Berth(pnlShenfield, 634,  570, "SE0597");
        new Berth(pnlShenfield, 714,  570, "SE0603", "SE0636");
        new Berth(pnlShenfield, 1354, 474, "SE0604");
        new Berth(pnlShenfield, 778,  570, "SE0605");
        new Berth(pnlShenfield, 1418, 474, "SE0606");
        new Berth(pnlShenfield, 906,  570, "SE0607");
        new Berth(pnlShenfield, 1482, 474, "SE0608");
        new Berth(pnlShenfield, 970,  570, "SE0609");
        new Berth(pnlShenfield, 1546, 474, "SE0610");
        new Berth(pnlShenfield, 1034, 570, "SE0611");
        new Berth(pnlShenfield, 1610, 474, "SE0612");
        new Berth(pnlShenfield, 1674, 474, "SE0614");
        new Berth(pnlShenfield, 1098, 570, "SE0615");
        new Berth(pnlShenfield, 1738, 474, "SE0616");
        new Berth(pnlShenfield, 186,  602, "SE0618");
        new Berth(pnlShenfield, 1162, 570, "SE0619", "SE0652");
        new Berth(pnlShenfield, 250,  602, "SE0620");
        new Berth(pnlShenfield, 1226, 570, "SE0621");
        new Berth(pnlShenfield, 314,  602, "SE0622");
        new Berth(pnlShenfield, 1290, 570, "SE0623");
        new Berth(pnlShenfield, 378,  602, "SE0624");
        new Berth(pnlShenfield, 1354, 570, "SE0625");
        new Berth(pnlShenfield, 442,  602, "SE0626");
        new Berth(pnlShenfield, 506,  602, "SE0628");
        new Berth(pnlShenfield, 1418, 570, "SE0629");
        new Berth(pnlShenfield, 570,  602, "SE0630");
        new Berth(pnlShenfield, 634,  602, "SE0634", "SE0599");
        new Berth(pnlShenfield, 714,  602, "SE0638");
        new Berth(pnlShenfield, 186,  698, "SE0639");
        new Berth(pnlShenfield, 778,  602, "SE0640");
        new Berth(pnlShenfield, 842,  602, "SE0642");
        new Berth(pnlShenfield, 906,  602, "SE0644");
        new Berth(pnlShenfield, 970,  602, "SE0646");
        new Berth(pnlShenfield, 1034, 602, "SE0648", "SE0613");
        new Berth(pnlShenfield, 250,  698, "SE0649");
        new Berth(pnlShenfield, 1098, 602, "SE0650");
        new Berth(pnlShenfield, 314,  698, "SE0651");
        new Berth(pnlShenfield, 1162, 602, "SE0654");
        new Berth(pnlShenfield, 1226, 602, "SE0658");
        new Berth(pnlShenfield, 1290, 602, "SE0660");
        new Berth(pnlShenfield, 1354, 602, "SE0662", "SE0627");
        new Berth(pnlShenfield, 1418, 602, "SE0664", "SE0631");
        new Berth(pnlShenfield, 1530, 514, "SE0666", "SEDNSS");
        new Berth(pnlShenfield, 1546, 538, "SEA668");
        new Berth(pnlShenfield, 1546, 570, "SEA670");
        new Berth(pnlShenfield, 1546, 602, "SEA672");
        new Berth(pnlShenfield, 1546, 634, "SEA674");
        new Berth(pnlShenfield, 1610, 538, "SEB668");
        new Berth(pnlShenfield, 1610, 570, "SEB670");
        new Berth(pnlShenfield, 1610, 602, "SEB672");
        new Berth(pnlShenfield, 1610, 634, "SEB674");
        new Berth(pnlShenfield, 1674, 538, "SEC668");
        new Berth(pnlShenfield, 1674, 570, "SEC670");
        new Berth(pnlShenfield, 1674, 602, "SEC672");
        new Berth(pnlShenfield, 1674, 634, "SEC674");
      /*new Berth(pnlShenfield, 1546, 538, "SEF668");
        new Berth(pnlShenfield, 1546, 570, "SEF670");
        new Berth(pnlShenfield, 1546, 602, "SEF672");
        new Berth(pnlShenfield, 1546, 634, "SEF674");*/
        new Berth(pnlShenfield, 1738, 538, "SER668");
        new Berth(pnlShenfield, 1738, 570, "SER670");
        new Berth(pnlShenfield, 1738, 602, "SER672");
        new Berth(pnlShenfield, 1738, 634, "SER674");
        new Berth(pnlShenfield, 314,  730, "SE0694");
        new Berth(pnlShenfield, 378,  698, "SE0696");
        new Berth(pnlShenfield, 474,  698, "SEBURN");
        new Berth(pnlShenfield, 538,  698, "SE0708");
        new Berth(pnlShenfield, 1194, 506, "SE5146");
        new Berth(pnlShenfield, 1162, 410, "SE5157");
        new Berth(pnlShenfield, 602,  698, "SER708");
        new Berth(pnlShenfield, 1090, 410, "SEWDNS");
        new Berth(pnlShenfield, 538,  722, "SEX708");
        new Berth(pnlShenfield, 1434, 514, "SE5165", "SEDNSN");
        new Berth(pnlShenfield, 1434, 658, "SE5167", "SEUPSN");
        new Berth(pnlShenfield, 1530, 658, "SE5170", "SEUPSS");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlShenfield, 745,  130, "BRENTWOOD",     "BRE");
        smallStation(pnlShenfield, 289,  130, "HAROLD WOOD",   "HRO");
        smallStation(pnlShenfield, 1207, 130, "SHENFIELD",     "SNF");

        smallStation(pnlShenfield, 596,  418, "BILLERICAY",    "BIC");
        smallStation(pnlShenfield, 1258, 394, "WICKFORD",      "WIC");

        smallStation(pnlShenfield, 1165, 546+99, "AIRPORT",       "SIA"); // SOUTHEND
        smallStation(pnlShenfield, 637,  546+88, "HOCKLEY",       "HOC");
        smallStation(pnlShenfield, 1345, 546+88, "PRITTLEWELL",   "PRL");
        smallStation(pnlShenfield, 186,  546+88, "RAYLEIGH",      "RLG");
        smallStation(pnlShenfield, 1034, 546+88, "ROCHFORD",      "RFD");
        smallStation(pnlShenfield, 1162, 538+99, "SOUTHEND",      "SIA"); // AIRPORT
        smallStation(pnlShenfield, 1790, 586, "SOUTHEND",      "SOV"); // VICTORIA
        smallStation(pnlShenfield, 1790, 594, "VICTORIA",      "SOV"); // SOUTHEND

        smallStation(pnlShenfield, 418,  674, "ALTHORNE",      "ALN");
        smallStation(pnlShenfield, 163,  730, "BATTLESBRIDGE", "BLB");
        smallStation(pnlShenfield, 474,  730, "BURNHAM-",      "BUU"); // ON-CROUCH
        smallStation(pnlShenfield, 229,  674, "FERRERS",       "SOF"); // S WOODHAM
        smallStation(pnlShenfield, 305,  762, "N FAMBRIDGE",   "NFA");
        smallStation(pnlShenfield, 471,  738, "ON-CROUCH",     "BUU"); // BURNHAM-
        smallStation(pnlShenfield, 223,  666, "S WOODHAM",     "SOF"); // FERRERS
        smallStation(pnlShenfield, 526,  674, "SOUTHMINSTER",  "SMN");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlShenfield, 1674, 234, "WITHAM", 4);
        makeNavButton(pnlShenfield, 112,  202, "ILFORD", 2);
        //</editor-fold>

        placeTopBits(pnlShenfield);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Shenfield.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlShenfield.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Ilford">
    private void initIlford()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlIlford, 234,  122, "SI0383");
        new Berth(pnlIlford, 298,  122, "SI0385");
        new Berth(pnlIlford, 362,  122, "SI0387");
        new Berth(pnlIlford, 362,  186, "SI0389");
        new Berth(pnlIlford, 426,  122, "SI0391");
        new Berth(pnlIlford, 490,  122, "SI0393");
        new Berth(pnlIlford, 490,  186, "SI0395");
        new Berth(pnlIlford, 554,  122, "SI0397");
        new Berth(pnlIlford, 618,  122, "SI0399");
        new Berth(pnlIlford, 618,  186, "SI0401");
        new Berth(pnlIlford, 682,  122, "SI0403");
        new Berth(pnlIlford, 682,  186, "SI0405");
        new Berth(pnlIlford, 746,  122, "SI0407");
        new Berth(pnlIlford, 810,  170, "SI0409");
        new Berth(pnlIlford, 234,  154, "SI0410");
        new Berth(pnlIlford, 810,  186, "SI0411");
        new Berth(pnlIlford, 298,  154, "SI0412");
        new Berth(pnlIlford, 810,  122, "SI0413");
        new Berth(pnlIlford, 362,  154, "SI0414");
        new Berth(pnlIlford, 874,  122, "SI0415");
        new Berth(pnlIlford, 362,  218, "SI0416", "SI5101");
        new Berth(pnlIlford, 954,  122, "SI0417", "SI5102");
        new Berth(pnlIlford, 426,  154, "SI0418");
        new Berth(pnlIlford, 954,  186, "SI0419", "SI5100");
        new Berth(pnlIlford, 490,  154, "SI0420");
        new Berth(pnlIlford, 1018, 122, "SI0421");
        new Berth(pnlIlford, 554,  154, "SI0422");
        new Berth(pnlIlford, 1082, 122, "SI0423");
        new Berth(pnlIlford, 554,  218, "SI0424");
        new Berth(pnlIlford, 1082, 186, "SI0425");
        new Berth(pnlIlford, 618,  154, "SI0426");
        new Berth(pnlIlford, 1082, 122, "SI0427");
        new Berth(pnlIlford, 682,  154, "SI0428");
        new Berth(pnlIlford, 1210, 186, "SI0429");
        new Berth(pnlIlford, 682,  218, "SI0430");
        new Berth(pnlIlford, 1274, 122, "SI0431", "SI0458");
        new Berth(pnlIlford, 746,  154, "SI0432");
        new Berth(pnlIlford, 1370, 122, "SI0433");
        new Berth(pnlIlford, 1370, 186, "SI0435");
        new Berth(pnlIlford, 810,  154, "SI0436");
        new Berth(pnlIlford, 794,  218, "SI0438");
        new Berth(pnlIlford, 1434, 82,  "SI0439");
        new Berth(pnlIlford, 874,  154, "SI0440", "SI5139");
        new Berth(pnlIlford, 1578, 122, "SI0441", "SI5116");
        new Berth(pnlIlford, 954,  154, "SI0442");
        new Berth(pnlIlford, 1578, 186, "SI0443");
        new Berth(pnlIlford, 954,  218, "SI0444", "SI5111");
        new Berth(pnlIlford, 1018, 154, "SI0446");
        new Berth(pnlIlford, 1082, 154, "SI0448");
        new Berth(pnlIlford, 1130, 250, "SI0450");
        new Berth(pnlIlford, 1082, 154, "SI0452");
        new Berth(pnlIlford, 1082, 218, "SI0454");
        new Berth(pnlIlford, 1210, 154, "SI0456");
        new Berth(pnlIlford, 1274, 154, "SI0460", "SI5113");
        new Berth(pnlIlford, 1274, 218, "SI0462");
        new Berth(pnlIlford, 1354, 82,  "SI0464");
        new Berth(pnlIlford, 1370, 138, "SI0468");
        new Berth(pnlIlford, 1370, 154, "SI0470");
        new Berth(pnlIlford, 1370, 218, "SI0472");
        new Berth(pnlIlford, 1578, 154, "SI0474");
        new Berth(pnlIlford, 1578, 218, "SI0476");
        new Berth(pnlIlford, 298,  250, "SI5099");
        new Berth(pnlIlford, 746,  170, "SI5104");
        new Berth(pnlIlford, 826,  234, "SI5105");
        new Berth(pnlIlford, 858,  218, "SI5107");
        new Berth(pnlIlford, 1514, 106, "SI5114");
        new Berth(pnlIlford, 1434, 154, "SI5125");
        new Berth(pnlIlford, 226,  250, "SIRCHS");
        new Berth(pnlIlford, 1570, 106, "SIRGP1");
        new Berth(pnlIlford, 1426, 138, "SIRGP2");
        new Berth(pnlIlford, 1360, 26,  "SIRGP3");
        new Berth(pnlIlford, 1412, 26,  "SIRGP4");
        new Berth(pnlIlford, 1360, 46,  "SIRGP5");
        new Berth(pnlIlford, 1412, 46,  "SIRGP6");
        new Berth(pnlIlford, 1434, 102, "SIRGPC");
        new Berth(pnlIlford, 1354, 102, "SIRGPL");
        new Berth(pnlIlford, 1002, 250, "SIROM1");
        new Berth(pnlIlford, 826,  274, "SIRRED");
        new Berth(pnlIlford, 938,  250, "SIRROM");
        new Berth(pnlIlford, 1002, 282, "SIXROM");
        new Berth(pnlIlford, 1194, 250, "SIUMIN");

        new Berth(pnlIlford, 218,  394, "SI0321");
        new Berth(pnlIlford, 218,  458, "SI0323");
        new Berth(pnlIlford, 282,  458, "SI0325");
        new Berth(pnlIlford, 346,  458, "SI0327");
        new Berth(pnlIlford, 474,  394, "SI0329");
        new Berth(pnlIlford, 474,  458, "SI0331");
        new Berth(pnlIlford, 538,  458, "SI0333");
        new Berth(pnlIlford, 537,  362, "SI0335");
        new Berth(pnlIlford, 282,  522, "SI0336");
        new Berth(pnlIlford, 346,  490, "SI0338");
        new Berth(pnlIlford, 618,  410, "SI0339");
        new Berth(pnlIlford, 346,  426, "SI0340");
        new Berth(pnlIlford, 665,  362, "SI0341");
        new Berth(pnlIlford, 666,  394, "SI0343");
        new Berth(pnlIlford, 666,  458, "SI0345");
        new Berth(pnlIlford, 410,  490, "SI0346");
        new Berth(pnlIlford, 762,  394, "SI0347");
        new Berth(pnlIlford, 826,  394, "SI0349", "SI0368");
        new Berth(pnlIlford, 474,  490, "SI0350");
        new Berth(pnlIlford, 890,  458, "SI0351");
        new Berth(pnlIlford, 538,  490, "SI0352");
        new Berth(pnlIlford, 890,  394, "SI0353");
        new Berth(pnlIlford, 538,  522, "SI0354");
        new Berth(pnlIlford, 1082, 394, "SI0355");
        new Berth(pnlIlford, 538,  426, "SI0356");
        new Berth(pnlIlford, 1146, 394, "SI0357");
        new Berth(pnlIlford, 586,  474, "SI0358");
        new Berth(pnlIlford, 1146, 458, "SI0359");
        new Berth(pnlIlford, 666,  522, "SI0360");
        new Berth(pnlIlford, 1193, 330, "SI0361");
        new Berth(pnlIlford, 826,  426, "SI0362");
        new Berth(pnlIlford, 1274, 394, "SI0363", "SI5088");
        new Berth(pnlIlford, 826,  490, "SI0364");
        new Berth(pnlIlford, 1338, 394, "SI0365");
        new Berth(pnlIlford, 1338, 458, "SI0369");
        new Berth(pnlIlford, 890,  426, "SI0370");
        new Berth(pnlIlford, 1402, 394, "SI0371");
        new Berth(pnlIlford, 954,  426, "SI0372");
        new Berth(pnlIlford, 1466, 394, "SI0373");
        new Berth(pnlIlford, 1082, 426, "SI0374");
        new Berth(pnlIlford, 1530, 394, "SI0375");
        new Berth(pnlIlford, 1082, 490, "SI0376");
        new Berth(pnlIlford, 1658, 394, "SI0377");
        new Berth(pnlIlford, 1106, 310, "SI0378");
        new Berth(pnlIlford, 1658, 458, "SI0379");
        new Berth(pnlIlford, 1106, 330, "SI0380");
        new Berth(pnlIlford, 1722, 394, "SI0381");
        new Berth(pnlIlford, 1106, 350, "SI0382");
        new Berth(pnlIlford, 1106, 370, "SI0384");
        new Berth(pnlIlford, 1146, 426, "SI0386");
        new Berth(pnlIlford, 1210, 426, "SI0388");
        new Berth(pnlIlford, 1210, 490, "SI0390");
        new Berth(pnlIlford, 1338, 426, "SI0392", "SI0367");
        new Berth(pnlIlford, 1402, 426, "SI0394");
        new Berth(pnlIlford, 1466, 426, "SI0396");
        new Berth(pnlIlford, 1530, 426, "SI0398");
        new Berth(pnlIlford, 1530, 490, "SI0400");
        new Berth(pnlIlford, 1594, 426, "SI0402");
        new Berth(pnlIlford, 1658, 426, "SI0404");
        new Berth(pnlIlford, 1722, 426, "SI0406");
        new Berth(pnlIlford, 1722, 490, "SI0408");
        new Berth(pnlIlford, 629,  557, "SI5078");
        new Berth(pnlIlford, 629,  577, "SI5080");
        new Berth(pnlIlford, 629,  597, "SI5082");
        new Berth(pnlIlford, 629,  617, "SI5084");
        new Berth(pnlIlford, 629,  637, "SI5086");
        new Berth(pnlIlford, 665,  426, "SI5087");
        new Berth(pnlIlford, 761,  362, "SIA366");
        new Berth(pnlIlford, 818,  362, "SIB366");
        new Berth(pnlIlford, 1257, 362, "SIICIN");
        new Berth(pnlIlford, 881,  362, "SIR366");
        new Berth(pnlIlford, 681,  557, "SIRAS1");
        new Berth(pnlIlford, 681,  577, "SIRAS2");
        new Berth(pnlIlford, 681,  597, "SIRAS3");
        new Berth(pnlIlford, 681,  617, "SIRAS4");
        new Berth(pnlIlford, 681,  637, "SIRAS5");
        new Berth(pnlIlford, 1193, 362, "SIRIFC");
        new Berth(pnlIlford, 1017, 378, "SIRIFL");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        smallStation(pnlIlford, 1268, 106, "GIDEA PARK",  "GDP");
        smallStation(pnlIlford, 957,  98,  "ROMFORD",     "RMF");
        smallStation(pnlIlford, 362,  90,  "CHADWELL",    "CTH");
        smallStation(pnlIlford, 371,  98,  "HEATH",       "CTH");
        smallStation(pnlIlford, 1654, 370, "GOODMAYES",   "GMY");
        smallStation(pnlIlford, 1328, 370, "SEVEN KINGS", "SVK");
        smallStation(pnlIlford, 831,  346, "ILFORD",      "IFD");
        smallStation(pnlIlford, 339,  370, "MANOR PARK",  "MNP");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlIlford, 1674, 170, "SHENFIELD", 3);
        makeNavButton(pnlIlford, 61,   442, "STRATFORD", 1);
        //</editor-fold>

        placeTopBits(pnlIlford);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Ilford.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlIlford.add(background);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Stratford + Liv St">
    private void initStratford()
    {
        //<editor-fold defaultstate="collapsed" desc="TD Berths">
        new Berth(pnlStratford, 42,   186, "LSR009");
        new Berth(pnlStratford, 106,  186, "LSFC09");
        new Berth(pnlStratford, 170,  186, "LSFB09");
        new Berth(pnlStratford, 234,  186, "LSFA09");

        new Berth(pnlStratford, 42,   218, "LSR011");
        new Berth(pnlStratford, 106,  218, "LSFC11");
        new Berth(pnlStratford, 170,  218, "LSFB11");
        new Berth(pnlStratford, 234,  218, "LSFA11");

        new Berth(pnlStratford, 42,   250, "LSR013");
        new Berth(pnlStratford, 106,  250, "LSFC13");
        new Berth(pnlStratford, 170,  250, "LSFB13");
        new Berth(pnlStratford, 234,  250, "LSFA13");

        new Berth(pnlStratford, 42,   282, "LSR015");
        new Berth(pnlStratford, 106,  282, "LSFC15");
        new Berth(pnlStratford, 170,  282, "LSFB15");
        new Berth(pnlStratford, 234,  282, "LSFA15");

        new Berth(pnlStratford, 42,   314, "LSR017");
        new Berth(pnlStratford, 106,  314, "LSFC17");
        new Berth(pnlStratford, 170,  314, "LSFB17");
        new Berth(pnlStratford, 234,  314, "LSFA17");

        new Berth(pnlStratford, 42,   346, "LSR019");
        new Berth(pnlStratford, 106,  346, "LSFC19");
        new Berth(pnlStratford, 170,  346, "LSFB19");
        new Berth(pnlStratford, 234,  346, "LSFA19");

        new Berth(pnlStratford, 42,   378, "LSR021");
        new Berth(pnlStratford, 106,  378, "LSFC21");
        new Berth(pnlStratford, 170,  378, "LSFB21");
        new Berth(pnlStratford, 234,  378, "LSFA21");

        new Berth(pnlStratford, 42,   410, "LSR023");
        new Berth(pnlStratford, 106,  410, "LSFC23");
        new Berth(pnlStratford, 170,  410, "LSFB23");
        new Berth(pnlStratford, 234,  410, "LSFA23");

        new Berth(pnlStratford, 42,   442, "LSR025");
        new Berth(pnlStratford, 106,  442, "LSFC25");
        new Berth(pnlStratford, 170,  442, "LSFB25");
        new Berth(pnlStratford, 234,  442, "LSFA25");
        new Berth(pnlStratford, 330,  442, "LS0027");

        new Berth(pnlStratford, 42,   474, "LSR029");
        new Berth(pnlStratford, 106,  474, "LSFC29");
        new Berth(pnlStratford, 170,  474, "LSFB29");
        new Berth(pnlStratford, 234,  474, "LSFA29");
        new Berth(pnlStratford, 330,  474, "LS0031");

        new Berth(pnlStratford, 42,   506, "LSR033");
        new Berth(pnlStratford, 106,  506, "LSFC33");
        new Berth(pnlStratford, 170,  506, "LSFB33");
        new Berth(pnlStratford, 234,  506, "LSFA33");
        new Berth(pnlStratford, 298,  506, "LS0035");

        new Berth(pnlStratford, 42,   538, "LSR037");
        new Berth(pnlStratford, 106,  538, "LSFC37");
        new Berth(pnlStratford, 170,  538, "LSFB37");
        new Berth(pnlStratford, 234,  538, "LSFA37");
        new Berth(pnlStratford, 298,  538, "LS0039");

        new Berth(pnlStratford, 42,   570, "LSR041");
        new Berth(pnlStratford, 106,  570, "LSFC41");
        new Berth(pnlStratford, 170,  570, "LSFB41");
        new Berth(pnlStratford, 234,  570, "LSFA41");

        new Berth(pnlStratford, 42,   602, "LSR043");
        new Berth(pnlStratford, 106,  602, "LSFC43");
        new Berth(pnlStratford, 170,  602, "LSFB43");
        new Berth(pnlStratford, 234,  602, "LSFA43");

        new Berth(pnlStratford, 42,   634, "LSR045");
        new Berth(pnlStratford, 106,  634, "LSFC45");
        new Berth(pnlStratford, 170,  634, "LSFB45");
        new Berth(pnlStratford, 234,  634, "LSFA45");
        new Berth(pnlStratford, 282,  634, "LS0047");

        new Berth(pnlStratford, 42,   666, "LSR049");
        new Berth(pnlStratford, 106,  666, "LSFC49");
        new Berth(pnlStratford, 170,  666, "LSFB49");
        new Berth(pnlStratford, 234,  666, "LSFA49");

        new Berth(pnlStratford, 42,   698, "LSR051");
        new Berth(pnlStratford, 106,  698, "LSFC51");
        new Berth(pnlStratford, 170,  698, "LSFB51");
        new Berth(pnlStratford, 234,  698, "LSFA51");

        new Berth(pnlStratford, 42,   730, "LSR053");
        new Berth(pnlStratford, 106,  730, "LSFC53");
        new Berth(pnlStratford, 170,  730, "LSFB53");
        new Berth(pnlStratford, 234,  730, "LSFA53");

        new Berth(pnlStratford, 522,  474, "LS0054");
        new Berth(pnlStratford, 522,  442, "LS0055");
        new Berth(pnlStratford, 442,  506, "LS0056");
        new Berth(pnlStratford, 522,  570, "LS0057");
        new Berth(pnlStratford, 442,  602, "LS0058");
        new Berth(pnlStratford, 506,  634, "LS0059");
        new Berth(pnlStratford, 458,  666, "LS0060");
        new Berth(pnlStratford, 586,  442, "LS0061");
        new Berth(pnlStratford, 586,  570, "LS0063");
        new Berth(pnlStratford, 586,  474, "LS0064");
        new Berth(pnlStratford, 570,  634, "LS0065");
        new Berth(pnlStratford, 522,  602, "LS0066");
        new Berth(pnlStratford, 522,  666, "LS0068");
        new Berth(pnlStratford, 650,  474, "LS0070");
        new Berth(pnlStratford, 650,  442, "LS0071");
        new Berth(pnlStratford, 650,  602, "LS0072");
        new Berth(pnlStratford, 650,  570, "LS0073");
        new Berth(pnlStratford, 650,  666, "LS0074");
        new Berth(pnlStratford, 650,  634, "LS0075");
        new Berth(pnlStratford, 730,  474, "LS0080");
        new Berth(pnlStratford, 730,  442, "LS0081");
        new Berth(pnlStratford, 730,  546, "LS0082");
        new Berth(pnlStratford, 730,  570, "LS0083");
        new Berth(pnlStratford, 730,  602, "LS0084");
        new Berth(pnlStratford, 730,  634, "LS0085");
        new Berth(pnlStratford, 730,  666, "LS0086");
        new Berth(pnlStratford, 842,  474, "LS0090");
        new Berth(pnlStratford, 842,  442, "LS0091");
        new Berth(pnlStratford, 842,  602, "LS0092");
        new Berth(pnlStratford, 842,  570, "LS0093");
        new Berth(pnlStratford, 842,  666, "LS0094");
        new Berth(pnlStratford, 842,  634, "LS0095");
        new Berth(pnlStratford, 954,  474, "LS0100");
        new Berth(pnlStratford, 1034, 442, "LS0101");
        new Berth(pnlStratford, 954,  538, "LS0102");
        new Berth(pnlStratford, 1034, 506, "LS0103");
        new Berth(pnlStratford, 1034, 474, "LS0110");
        new Berth(pnlStratford, 1034, 538, "LS0112");
        new Berth(pnlStratford, 970,  602, "LS0200");
        new Berth(pnlStratford, 1034, 570, "LS0201");
        new Berth(pnlStratford, 970,  666, "LS0202");
        new Berth(pnlStratford, 1034, 634, "LS0203");
        new Berth(pnlStratford, 1034, 602, "LS0210");
        new Berth(pnlStratford, 1034, 666, "LS0212");

        new Berth(pnlStratford, 1098, 602, "SI0220");
        new Berth(pnlStratford, 1098, 570, "SI0221");
        new Berth(pnlStratford, 1098, 666, "SI0222");
        new Berth(pnlStratford, 1098, 634, "SI0223");
        new Berth(pnlStratford, 1178, 602, "SI0224");
        new Berth(pnlStratford, 1178, 570, "SI0225");
        new Berth(pnlStratford, 1178, 666, "SI0226");
        new Berth(pnlStratford, 1178, 634, "SI0227");
        new Berth(pnlStratford, 1242, 570, "SI0229");
        new Berth(pnlStratford, 1242, 602, "SI0228");
        new Berth(pnlStratford, 1242, 666, "SI0230");
        new Berth(pnlStratford, 1242, 634, "SI0231");
        new Berth(pnlStratford, 1354, 538, "SI0234", "SI0239");
        new Berth(pnlStratford, 1274, 698, "SI0235");
        new Berth(pnlStratford, 1354, 506, "SI0237");
        new Berth(pnlStratford, 1354, 570, "SI0241", "SI0240");
        new Berth(pnlStratford, 1354, 602, "SI0242");
        new Berth(pnlStratford, 1354, 666, "SI0244");
        new Berth(pnlStratford, 1338, 634, "SI0245");
        new Berth(pnlStratford, 1434, 506, "SI0249", "SI5070");
        new Berth(pnlStratford, 1434, 538, "SI0251");
        new Berth(pnlStratford, 1514, 538, "SI0250", "SI0261");
        new Berth(pnlStratford, 1434, 570, "SI0253", "SI0240");
        new Berth(pnlStratford, 1402, 634, "SI0255");
        new Berth(pnlStratford, 1514, 602, "SI0254", "SI0265");
        new Berth(pnlStratford, 1466, 666, "SI0256");
        new Berth(pnlStratford, 1466, 634, "SI0257");
        new Berth(pnlStratford, 1482, 490, "SI0258");
        new Berth(pnlStratford, 1514, 506, "SI0259", "SI0248");
        new Berth(pnlStratford, 1610, 570, "SI0262");
        new Berth(pnlStratford, 1514, 570, "SI0263", "SI0252");
        new Berth(pnlStratford, 1610, 602, "SI0264");
        new Berth(pnlStratford, 1610, 634, "SI0266");
        new Berth(pnlStratford, 1546, 634, "SI0267");
        new Berth(pnlStratford, 1610, 682, "SI0268");
        new Berth(pnlStratford, 1582, 474, "SI0269", "SI1294");
        new Berth(pnlStratford, 1658, 570, "SI0273", "SI0270");
        new Berth(pnlStratford, 1658, 474, "SI0274");
        new Berth(pnlStratford, 1658, 602, "SI0275");
        new Berth(pnlStratford, 1658, 506, "SI0276");
        new Berth(pnlStratford, 1658, 634, "SI0277");
        new Berth(pnlStratford, 1658, 538, "SI0278");
        new Berth(pnlStratford, 1658, 682, "SI0279");
        new Berth(pnlStratford, 1706, 538, "SI0281");
        new Berth(pnlStratford, 1722, 634, "SI0283");
        new Berth(pnlStratford, 1722, 682, "SI0284");
        new Berth(pnlStratford, 778,  330, "SI0285");
        new Berth(pnlStratford, 714,  362, "SI0286");
        new Berth(pnlStratford, 778,  362, "SI0288");
        new Berth(pnlStratford, 842,  330, "SI0287");
        new Berth(pnlStratford, 906,  266, "SI0289", "SI0290");
        new Berth(pnlStratford, 906,  298, "SI0292", "SI0291");
        new Berth(pnlStratford, 970,  330, "SI0293");
        new Berth(pnlStratford, 906,  330, "SI0294");
        new Berth(pnlStratford, 746,  234, "SI0295");
        new Berth(pnlStratford, 906,  362, "SI0296");
        new Berth(pnlStratford, 1098, 330, "SI0297", "SI0308");
        new Berth(pnlStratford, 970,  362, "SI0298");
        new Berth(pnlStratford, 1162, 266, "SI0299");
        new Berth(pnlStratford, 1034, 362, "SI0300");
        new Berth(pnlStratford, 1162, 330, "SI0303");
        new Berth(pnlStratford, 1098, 266, "SI0304");
        new Berth(pnlStratford, 1226, 330, "SI0305");
        new Berth(pnlStratford, 1162, 298, "SI0306", "SI0301");
        new Berth(pnlStratford, 1290, 330, "SI0307");
        new Berth(pnlStratford, 1354, 266, "SI0309");
        new Berth(pnlStratford, 1162, 362, "SI0310");
        new Berth(pnlStratford, 1354, 330, "SI0313");
        new Berth(pnlStratford, 1290, 362, "SI0314");
        new Berth(pnlStratford, 1482, 330, "SI0317", "SI0328");
        new Berth(pnlStratford, 1354, 298, "SI0316", "SI0311");
        new Berth(pnlStratford, 1354, 362, "SI0320", "SI0315");
        new Berth(pnlStratford, 1098, 362, "SI0324");
        new Berth(pnlStratford, 1482, 298, "SI0326");
        new Berth(pnlStratford, 1482, 362, "SI0330", "SI0319");
        new Berth(pnlStratford, 1562, 378, "SI0332");
        new Berth(pnlStratford, 1514, 378, "SI0334");
        new Berth(pnlStratford, 1546, 394, "SI0909");
        new Berth(pnlStratford, 1498, 394, "SI0911");
        new Berth(pnlStratford, 1546, 490, "SI1292");
        new Berth(pnlStratford, 1482, 450, "SI1296");
        new Berth(pnlStratford, 1258, 74,  "SI1424");
        new Berth(pnlStratford, 1370, 474, "SI5065");
        new Berth(pnlStratford, 1298, 474, "SILSBY");
        new Berth(pnlStratford, 1274, 738, "SIU518");

        new Berth(pnlStratford, 1610, 186, "SI0032");
        new Berth(pnlStratford, 1786, 90,  "SI1003");
        new Berth(pnlStratford, 1226, 122, "SIS700");
        new Berth(pnlStratford, 1226, 90,  "SIS701");
        new Berth(pnlStratford, 1290, 58,  "SIS703");
        new Berth(pnlStratford, 1338, 122, "SIS704");
        new Berth(pnlStratford, 1338, 90,  "SIS705");
        new Berth(pnlStratford, 1482, 154, "SIS706");
        new Berth(pnlStratford, 1402, 90,  "SIS707");
        new Berth(pnlStratford, 1482, 186, "SIS708");
        new Berth(pnlStratford, 1482, 122, "SIS712");
        new Berth(pnlStratford, 1546, 90,  "SIS713");
        new Berth(pnlStratford, 1546, 154, "SIS715");
        new Berth(pnlStratford, 1626, 122, "SIS716");
        new Berth(pnlStratford, 1546, 186, "SIS717");
        new Berth(pnlStratford, 1690, 122, "SIS718");
        new Berth(pnlStratford, 1418, 154, "SIS767");
        new Berth(pnlStratford, 1626, 90,  "SIS770");
        new Berth(pnlStratford, 1610, 154, "SIS772");
        new Berth(pnlStratford, 1706, 474, "SIS697");
        new Berth(pnlStratford, 1706, 506, "SIS901");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Stations">
        largeStation(pnlStratford, 1620, 434, "STRATFORD",               "SRA");
        largeStation(pnlStratford, 56,   140, "LONDON LIVERPOOL STREET", "LST");

        smallStation(pnlStratford, 845,  410, "BETHNAL",     "BET"); // GREEN
        smallStation(pnlStratford, 1281, 242, "FOREST GATE", "FOG");
        smallStation(pnlStratford, 851,  418, "GREEN",       "BET"); // BETHNAL
        smallStation(pnlStratford, 906,  242, "MARYLAND",    "MYL");
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Nav buttons">
        makeNavButton(pnlStratford, 1700, 58,  "BRIMSDOWN", 5);
        makeNavButton(pnlStratford, 1124, 490, "HACKNEY",   5);
        makeNavButton(pnlStratford, 1622, 314, "ILFORD",    2);
        //</editor-fold>

        placeTopBits(pnlStratford);

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(getClass().getResource("/eastangliamapclient/resources/Stratford+LivSt.png")));
        background.setFocusable(false);
        background.setBounds(0, 0, 1854, 860);
        pnlStratford.add(background);
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

    private void smallStation(JPanel pnl, int x, int y, String name, String url)
    {
        JLabel lbl = new JLabel(name.toUpperCase());

        lbl.setBackground(EastAngliaMapClient.GREY);
        lbl.setFont(EastAngliaMapClient.TD_FONT.deriveFont(8f));
        lbl.setForeground(EastAngliaMapClient.GREEN);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        lbl.setFocusable(false);
        lbl.setToolTipText(url.toUpperCase());
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
        JLabel lblClock = new JLabel(EastAngliaMapClient.getTime());

        lblClock.setBackground(EastAngliaMapClient.GREY);
        lblClock.setFont(EastAngliaMapClient.TD_FONT.deriveFont(45f));
        lblClock.setForeground(EastAngliaMapClient.GREEN);
        lblClock.setHorizontalAlignment(SwingConstants.CENTER);
        lblClock.setToolTipText("Clock");
        lblClock.setFocusable(false);
        lblClock.setOpaque(true);
        lblClock.setHorizontalTextPosition(SwingConstants.CENTER);
        pnl.add(lblClock);
        lblClock.setSize(280, 50);
        lblClock.setLocation(780, 10);
        clocks.add(lblClock);

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

        final JLabel motdLabel = new JLabel("XXMOTD");
        motdLabel.setFocusable(false);
        motdLabel.setOpaque(true);
        motdLabel.setBackground(new Color(30, 30, 30));

        motdLabel.setVerticalAlignment(JLabel.TOP);
        motdLabel.setHorizontalAlignment(JLabel.LEFT);

        motdLabel.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 16));
        motdLabel.setForeground(EastAngliaMapClient.GREEN);
        //motdLabel.setBounds(200, 11, 550, 48);
        motdLabel.setPreferredSize(new Dimension(520, 48));
        motdLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
        JScrollPane spMOTD = new JScrollPane(motdLabel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spMOTD.setBounds(200, 10, 550, 50);
        pnl.add(spMOTD);
        motdPanes.add(spMOTD);

        buttons.add(menu);
        buttons.add(help);
    }
    //</editor-fold>

    public void dispose()
    {
        Berths.clearMaps();
        frame.dispose();
    }

    public void setVisible(boolean visible)
    {
        if (visible == true && isVisible() == true)
            frame.requestFocus();
        else
            frame.setVisible(visible);
    }

    public boolean isVisible()
    {
        return frame.isVisible();
    }

    public void readFromMap()
    {
        readFromMap(EastAngliaMapClient.CClassMap);
    }

    public void readFromMap(Map<String, String> map)
    {
        for (Map.Entry pairs : map.entrySet())
        {
            String id = (String) pairs.getKey();
            String hc = (String) pairs.getValue();

            if (id.equals("XXMOTD"))
                setMOTD(hc);

            Berth berth = Berths.getBerth(id);

            if (berth != null)
            {
                if (map.containsKey(id))
                    if (!hc.equals("") || id.equals(berth.getCurrentId(false)))
                        berth.interpose(map.get(id), id);

                berth.setOpaque(false);
            }
        }
    }

    public ArrayList<JPanel> getPanels()
    {
        ArrayList<JPanel> panels = new ArrayList<>();

        panels.add(pnlStratford);
        panels.add(pnlIlford);
        panels.add(pnlShenfield);
        panels.add(pnlWitham);
        panels.add(pnlHackney);
        panels.add(pnlHarlow);
        panels.add(pnlColchester);
        panels.add(pnlClacton);
        panels.add(pnlIpswich);
        panels.add(pnlCambridgeCA);
        panels.add(pnlCambridgeEN);
        panels.add(pnlNorwich);

        for (JPanel panel : panels)
        {
            //pnl.repaint();//pnl.getBounds());
            panel.addNotify();
            panel.doLayout();
        }

        return panels;
    }

    public void prepForScreencap()
    {
        for (JButton button : buttons)
            button.setVisible(false);

        ArrayList<String> sortedKeys = new ArrayList<>(Berths.getKeySet());
        for (String key : sortedKeys)
        {
            Berths.getBerth(key).showDescription(false);
            Berths.getBerth(key).setOpaque(false);
            Berths.getBerth(key).setVisible(true);
        }

        for (JScrollPane sp : motdPanes)
            sp.setVisible(false);

        try { Thread.sleep(10); }
        catch (InterruptedException e) {}
    }

    public void finishScreencap()
    {
        for (JButton button : buttons)
            button.setVisible(true);

        ArrayList<String> sortedKeys = new ArrayList<>(Berths.getKeySet());
        for (String key : sortedKeys)
        {
            Berths.getBerth(key).showDescription(EastAngliaMapClient.showDescriptions);
            Berths.getBerth(key).setOpaque(EastAngliaMapClient.opaque);
            Berths.getBerth(key).setVisible(EastAngliaMapClient.visible);
        }

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
            ((JLabel) sp.getViewport().getView()).setText("<html><body style='width:auto'>" + motd + "</body></html>");
            //lbl.setText(" " + (motd.length() > 68 ? motd.substring(16, 64) + " -->" : motd.substring(16)));
            //lbl.setToolTipText((motd.length() > 68 ? "Full " : "") + "MOTD: " + motd);
    }
}