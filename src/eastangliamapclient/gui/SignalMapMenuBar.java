package eastangliamapclient.gui;

import eastangliamapclient.gui.mapelements.Berths;
import eastangliamapclient.gui.mapelements.Points;
import eastangliamapclient.gui.mapelements.Signals;
import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.MessageHandler;
import eastangliamapclient.ScreencapManager;
import static eastangliamapclient.gui.SignalMapGui.DEFAULT_HEIGHT;
import static eastangliamapclient.gui.SignalMapGui.DEFAULT_WIDTH;
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

    private /*final*/ JMenu menuFile;
    private /*final*/ JMenu menuView;
    private /*final*/ JMenu menuConnection;
    private /*final*/ JMenu menuScreencap;
    private /*final*/ JMenu menuWindow;
    private /*final*/ JMenu menuHelp;

    private /*final*/ JMenuItem fileTrainHistory;
    private /*final*/ JCheckBoxMenuItem filePreventSleep;
    private /*final*/ JCheckBoxMenuItem fileMinToSysTray;
    private /*final*/ JMenuItem fileExit;

    private /*final*/ JCheckBoxMenuItem viewVisibleBerths;
    private /*final*/ JCheckBoxMenuItem viewVisibleSignals;
    private /*final*/ JCheckBoxMenuItem viewVisiblePoints;
    private /*final*/ JCheckBoxMenuItem viewBerthOpacity;
    private /*final*/ JCheckBoxMenuItem viewBerthIDs;

    private /*final*/ JMenuItem connectionChangeName;
    private /*final*/ JMenuItem connectionRefresh;
    private /*final*/ JMenuItem connectionReconnect;

    private /*final*/ JMenuItem screencapTakeScreencaps;
    private /*final*/ JCheckBoxMenuItem screencapAutoScreencap;

    private /*final*/ JMenuItem windowResize;
    private /*final*/ JMenuItem windowReposition;
    private /*final*/ JMenuItem windowReset;

    private /*final*/ JMenuItem helpAbout;
    private /*final*/ JMenuItem helpHelp;

    private /*final*/ ActionListener listenerFile = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == fileTrainHistory)
        {
            String UUID = JOptionPane.showInputDialog(EastAngliaMapClient.frameSignalMap.frame, "Enter Train UUID:", "Train History", JOptionPane.QUESTION_MESSAGE);

            if (UUID != null)
                if (UUID.length() >= 5 && UUID.matches("[0-9]+"))
                    MessageHandler.requestHistoryOfTrain(UUID);
                else
                    JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "'" + UUID + "' is not a valid train UUID", "Error", JOptionPane.WARNING_MESSAGE);
        }
        else if (src == filePreventSleep)
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

            System.exit(0);
        }
    };
    private /*final*/ ActionListener listenerView = evt ->
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
    };
    private /*final*/ ActionListener listenerConnection = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == connectionChangeName)
        {
            EastAngliaMapClient.blockKeyInput = true;
            String newName = JOptionPane.showInputDialog("New Name:", EastAngliaMapClient.clientName);
            if (newName != null)
            {
                EastAngliaMapClient.writeSetting("clientName", newName);
                EastAngliaMapClient.clientName = newName;
                MessageHandler.sendName(EastAngliaMapClient.clientName);
            }
            EastAngliaMapClient.blockKeyInput = false;
        }
        else if (src == connectionRefresh)
            MessageHandler.requestAll();
        else if (src == connectionReconnect)
            EastAngliaMapClient.reconnect(true);
    };
    private /*final*/ ActionListener listenerScreencap = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == screencapTakeScreencaps)
            ScreencapManager.takeScreencaps();
        else if (src == screencapAutoScreencap)
            ScreencapManager.autoScreencap();

    };
    private /*final*/ ActionListener listenerWindow = evt ->
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
    };
    private /*final*/ ActionListener listenerHelp = evt ->
    {
        Object src = evt.getSource();
        if (src == null)
            return;

        if (src == helpAbout)
            JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "East Anglia Signalling Map - Client\n© Cameron Bird 2014", "About", JOptionPane.PLAIN_MESSAGE);
        else if (src == helpHelp)
            new HelpDialog();
    };

    public SignalMapMenuBar()
    {
        super();

        if (instance != null)
            throw new ExceptionInInitializerError("An instance of " + getClass().getCanonicalName() + " already exists.");
        instance = this;

        menuFile       = new JMenu("File");
        menuView       = new JMenu("View");
        menuConnection = new JMenu("Connection");
        menuScreencap  = new JMenu("Screencaps");
        menuWindow     = new JMenu("Window");
        menuHelp       = new JMenu("Help");

        fileTrainHistory = new JMenuItem("Train History...");
        filePreventSleep = new JCheckBoxMenuItem("Keep Your PC Awake");
        fileMinToSysTray = new JCheckBoxMenuItem("Minimise to System Tray");
        fileExit         = new JMenuItem("Exit");

        viewVisibleBerths  = new JCheckBoxMenuItem("Berths");
        viewVisibleSignals = new JCheckBoxMenuItem("Signals, Routes & Indications");
        viewVisiblePoints  = new JCheckBoxMenuItem("Point Indications");
        viewBerthOpacity   = new JCheckBoxMenuItem("Opaque Berth Mode");
        viewBerthIDs       = new JCheckBoxMenuItem("Show Berth IDs");

        connectionChangeName = new JMenuItem("Change Client Name");
        connectionRefresh    = new JMenuItem("Refresh Data");
        connectionReconnect  = new JMenuItem("Reconnect");

        screencapTakeScreencaps = new JMenuItem("Take Screencaps");
        screencapAutoScreencap  = new JCheckBoxMenuItem("Auto-Screencap");

        windowResize     = new JMenuItem("Reset Size");
        windowReposition = new JMenuItem("Reset Position");
        windowReset      = new JMenuItem("Reset All");

        helpAbout = new JMenuItem("About");
        helpHelp  = new JMenuItem("Help");

        menuFile.add(fileTrainHistory).addActionListener(listenerFile);
        menuFile.addSeparator();
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

        menuConnection.add(connectionChangeName).addActionListener(listenerConnection);
        menuConnection.addSeparator();
        menuConnection.add(connectionRefresh).addActionListener(listenerConnection);
        menuConnection.add(connectionReconnect).addActionListener(listenerConnection);

        menuScreencap.add(screencapTakeScreencaps).addActionListener(listenerScreencap);
        menuScreencap.add(screencapAutoScreencap).addActionListener(listenerScreencap);

        menuWindow.add(windowResize).addActionListener(listenerWindow);
        menuWindow.add(windowReposition).addActionListener(listenerWindow);
        menuWindow.add(windowReset).addActionListener(listenerWindow);

        menuHelp.add(helpAbout).addActionListener(listenerHelp);
        menuHelp.addSeparator();
        menuHelp.add(helpHelp).addActionListener(listenerHelp);

        add(menuFile);
        add(menuView);
        add(menuConnection);
        if (EastAngliaMapClient.screencappingActive)
            add(menuScreencap);
        add(menuWindow);
        add(menuHelp);

        updateCheckBoxes();
    }

    public static SignalMapMenuBar instance()
    {
        return instance;
    }

    public void updateCheckBoxes()
    {
        filePreventSleep.setSelected(EastAngliaMapClient.preventSleep);
        fileMinToSysTray.setSelected(EastAngliaMapClient.minimiseToSysTray);
        viewVisibleBerths.setSelected(EastAngliaMapClient.berthsVisible);
        viewVisibleSignals.setSelected(EastAngliaMapClient.signalsVisible);
        viewVisiblePoints.setSelected(EastAngliaMapClient.pointsVisible);
        viewBerthOpacity.setSelected(EastAngliaMapClient.opaque);
        viewBerthIDs.setSelected(EastAngliaMapClient.showDescriptions);
        screencapAutoScreencap.setSelected(EastAngliaMapClient.autoScreencap);
    }
}
