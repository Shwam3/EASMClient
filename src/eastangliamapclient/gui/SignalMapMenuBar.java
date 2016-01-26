package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.VersionChecker;
import static eastangliamapclient.gui.SignalMapGui.DEFAULT_HEIGHT;
import static eastangliamapclient.gui.SignalMapGui.DEFAULT_WIDTH;
import eastangliamapclient.gui.mapelements.Berths;
import eastangliamapclient.gui.mapelements.Points;
import eastangliamapclient.gui.mapelements.Signals;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class SignalMapMenuBar extends JMenuBar
{
    private static SignalMapMenuBar instance;

    private JMenu menuFile;
    private JMenu menuView;
    private JMenu menuConnection;
    private JMenu menuWindow;
    private JMenu menuHelp;

    private JCheckBoxMenuItem filePreventSleep;
    private JCheckBoxMenuItem fileMinToSysTray;
    private JMenuItem fileExit;

    private JCheckBoxMenuItem viewVisibleBerths;
    private JCheckBoxMenuItem viewVisibleSignals;
    private JCheckBoxMenuItem viewVisiblePoints;
    private JCheckBoxMenuItem viewBerthOpacity;
    private JCheckBoxMenuItem viewBerthIDs;

    private JMenuItem connectionViewData;
    private JMenuItem connectionReplay;
    private JMenuItem connectionReconnect;

    private JMenuItem windowResize;
    private JMenuItem windowReposition;
    private JMenuItem windowReset;

    private JMenuItem helpAbout;
    private JMenuItem helpUpdate;
    private JMenuItem helpHelp;

    private ActionListener listenerFile = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == filePreventSleep)
        {
            EastAngliaMapClient.preventSleep = filePreventSleep.isSelected();
            EastAngliaMapClient.writeSetting("preventSleep", String.valueOf(filePreventSleep.isSelected()));
        }
        else if (src == fileMinToSysTray)
        {
            EastAngliaMapClient.minimiseToSysTray = fileMinToSysTray.isSelected();
            EastAngliaMapClient.writeSetting("minimiseToSysTray", String.valueOf(fileMinToSysTray.isSelected()));
        }
        else if (src == fileExit)
        {
            EastAngliaMapClient.writeSetting("windowSize", ((int) EastAngliaMapClient.frameSignalMap.frame.getSize().getWidth()) + "," + ((int) EastAngliaMapClient.frameSignalMap.frame.getSize().getHeight()));
            EastAngliaMapClient.writeSetting("lastTab", Integer.toString(EastAngliaMapClient.frameSignalMap.TabBar.getSelectedIndex()));

            EastAngliaMapClient.frameSignalMap.setVisible(false);

            System.exit(0);
        }

        updateCheckBoxes();
    };
    private ActionListener listenerView = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == viewVisibleBerths)
            Berths.toggleBerthVisibilities();
        else if (src == viewVisibleSignals)
            Signals.toggleSignalVisibilities();
        else if (src == viewVisiblePoints)
            Points.togglePointVisibilities();
        else if (src == viewBerthOpacity)
            Berths.toggleBerthsOpacities();
        else if (src == viewBerthIDs)
            Berths.toggleBerthDescriptions();

        updateCheckBoxes();
    };
    private ActionListener listenerConnection = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == connectionViewData)
            EastAngliaMapClient.frameDataViewer.setVisible0(true);
        else if (src == connectionReplay)
            EastAngliaMapClient.frameReplayControls.setVisible0(true);
        else if (src == connectionReconnect)
            EastAngliaMapClient.reconnect(true);

        updateCheckBoxes();
    };
    private ActionListener listenerWindow = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == windowResize)
        {
            EastAngliaMapClient.frameSignalMap.frame.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
            EastAngliaMapClient.frameSignalMap.frame.pack();
        }
        else if (src == windowReposition)
            EastAngliaMapClient.frameSignalMap.frame.setLocationRelativeTo(null);
        else if (src == windowReset)
        {
            EastAngliaMapClient.frameSignalMap.frame.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
            EastAngliaMapClient.frameSignalMap.frame.pack();
            EastAngliaMapClient.frameSignalMap.frame.setLocationRelativeTo(null);
        }

        updateCheckBoxes();
    };
    private ActionListener listenerHelp = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == helpAbout)
            JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "East Anglia Signalling Map - Client\n"
                    + "Client Version: " + EastAngliaMapClient.CLIENT_VERSION + "\n"
                    + "Data Version: " + EastAngliaMapClient.DATA_VERSION + "\n"
                    + "© Cameron Bird 2014", "About", JOptionPane.INFORMATION_MESSAGE);
        else if (src == helpUpdate)
        {
            if (VersionChecker.checkVersion())
            {
                EastAngliaMapClient.frameSignalMap.dispose();
                JOptionPane.showMessageDialog(null, "Rhe program will now close\nYou must restart the program for the updates to take effect", "Updater", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
            else
                JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "All up to date!\nv" + EastAngliaMapClient.CLIENT_VERSION + " / v" + EastAngliaMapClient.DATA_VERSION + " are the latest client/data versions", "Updater", JOptionPane.INFORMATION_MESSAGE);
        }
        else if (src == helpHelp)
            new HelpDialog();

        updateCheckBoxes();
    };

    private SignalMapMenuBar()
    {
        super();

        if (instance != null)
            throw new ExceptionInInitializerError("An instance of " + getClass().getCanonicalName() + " already exists.");

        menuFile       = new JMenu("File");
        menuView       = new JMenu("View");
        menuConnection = new JMenu("Connection");
      //menuScreencap  = new JMenu("Screencaps");
        menuWindow     = new JMenu("Window");
        menuHelp       = new JMenu("Help");

        filePreventSleep = new JCheckBoxMenuItem("Keep Your PC Awake");
        fileMinToSysTray = new JCheckBoxMenuItem("Minimise to System Tray");
        fileExit         = new JMenuItem("Exit");

        filePreventSleep.setToolTipText("Prevent this computer from sleeping/hibernating automatically");
        fileMinToSysTray.setToolTipText("Minimise to System Tray on close");
        fileExit.setToolTipText("<html>Exit the program<br>(not to system tray)</html>");

        viewVisibleBerths  = new JCheckBoxMenuItem("Berths");
        viewVisibleSignals = new JCheckBoxMenuItem("Signals, Routes & Indications");
        viewVisiblePoints  = new JCheckBoxMenuItem("Point Indications");
        viewBerthOpacity   = new JCheckBoxMenuItem("Opaque Berth Mode");
        viewBerthIDs       = new JCheckBoxMenuItem("Show Berth IDs");

        viewVisibleBerths.setToolTipText("Toggle visibility of berths/headcodes");
        viewVisibleSignals.setToolTipText("Toggle visibility of signals & route/LC indicators");
        viewVisiblePoints.setToolTipText("Toggle visibility of points indicators");
        viewBerthOpacity.setToolTipText("<html>Toggle the opacity mode of berths<br>(always visible/only when occupied)</html>");
        viewBerthIDs.setToolTipText("Show the berth IDs");

        connectionViewData   = new JMenuItem("View Data...");
        connectionReplay     = new JMenuItem("View Replay...");
        connectionReconnect  = new JMenuItem("Reconnect");

        connectionViewData.setToolTipText("View the internal data");
        connectionReplay.setToolTipText("<html>Replay historical data (where available)<br><i>NOTE: Under construction</i></html>");
        connectionReconnect.setToolTipText("Manually attempt to reconnect to the server");

        windowResize     = new JMenuItem("Reset Size");
        windowReposition = new JMenuItem("Reset Position");
        windowReset      = new JMenuItem("Reset All");

        helpAbout  = new JMenuItem("About...");
        helpUpdate = new JMenuItem("Check for Updates...");
        helpHelp   = new JMenuItem("Help...");

        menuFile.add(filePreventSleep).addActionListener(listenerFile);
        menuFile.add(fileMinToSysTray).addActionListener(listenerFile);
        menuFile.addSeparator();
        menuFile.add(fileExit).addActionListener(listenerFile);

        menuView.add(viewVisibleBerths).addActionListener(listenerView);
        menuView.add(viewVisibleSignals).addActionListener(listenerView);
        menuView.add(viewVisiblePoints).addActionListener(listenerView);
        menuView.addSeparator();
        menuView.add(viewBerthIDs).addActionListener(listenerView);
        menuView.add(viewBerthOpacity).addActionListener(listenerView);

        menuConnection.add(connectionViewData).addActionListener(listenerConnection);
        menuConnection.add(connectionReplay).addActionListener(listenerConnection);
        menuConnection.addSeparator();
        menuConnection.add(connectionReconnect).addActionListener(listenerConnection);

        menuWindow.add(windowResize).addActionListener(listenerWindow);
        menuWindow.add(windowReposition).addActionListener(listenerWindow);
        menuWindow.add(windowReset).addActionListener(listenerWindow);

        menuHelp.add(helpAbout).addActionListener(listenerHelp);
        menuHelp.add(helpUpdate).addActionListener(listenerHelp);
        menuHelp.addSeparator();
        menuHelp.add(helpHelp).addActionListener(listenerHelp);

        add(menuFile);
        add(menuView);
        add(menuConnection);
        add(menuWindow);
        add(menuHelp);

        updateCheckBoxes();
    }

    public static SignalMapMenuBar instance()
    {
        if (instance == null)
            instance = new SignalMapMenuBar();
        return instance;
    }

    public void updateCheckBoxes()
    {
        filePreventSleep.setSelected(EastAngliaMapClient.preventSleep);
        fileMinToSysTray.setSelected(EastAngliaMapClient.minimiseToSysTray);
        viewVisibleBerths.setSelected(EastAngliaMapClient.berthsVisible);
        viewVisibleSignals.setSelected(EastAngliaMapClient.signalsVisible);
        viewVisiblePoints.setSelected(EastAngliaMapClient.pointsVisible);
        viewBerthOpacity.setSelected(EastAngliaMapClient.opaqueBerths);
        viewBerthIDs.setSelected(EastAngliaMapClient.showDescriptions);
    }
}
