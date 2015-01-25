package eastangliamapclient.gui;

import eastangliamapclient.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class OptionContexMenu extends JPopupMenu
{
    JCheckBoxMenuItem toggleOpacity;
    JCheckBoxMenuItem toggleDescriptions;
    JCheckBoxMenuItem berthVisibility;
    JCheckBoxMenuItem signalVisibility;

    JMenuItem refresh;
    JMenuItem reconnect;
    JMenuItem trainHistory;

    JMenuItem changeName;
    JCheckBoxMenuItem screencap; //TODO: Remove for release
    JMenuItem takeScreencaps; //TODO: Remove for release

    JCheckBoxMenuItem preventSleep;
    JCheckBoxMenuItem minToSys;
    JMenuItem exit;

    ActionListener clickEvent = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            Object src = evt.getSource();
            if (src == toggleOpacity)
                Berths.toggleBerthsOpacities();
            else if (src == toggleDescriptions)
                Berths.toggleBerthDescriptions();
            else if (src == berthVisibility)
                Berths.toggleBerthVisibilities();
            else if (src == signalVisibility)
                Signals.toggleSignalVisibilities();
            else if (src == refresh)
                MessageHandler.requestAll();
            else if (src == reconnect)
                EastAngliaMapClient.reconnect(true);
            else if (src == trainHistory)
            {
                String UUID = JOptionPane.showInputDialog(EastAngliaMapClient.frameSignalMap.frame, "Enter Train UUID:", "Train History", JOptionPane.QUESTION_MESSAGE);

                if (UUID != null)
                    if (UUID.length() >= 5 && UUID.matches("[0-9]+"))
                        MessageHandler.requestHistoryOfTrain(UUID);
                    else
                        JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "'" + UUID + "' is not a valid train UUID", "Error", JOptionPane.WARNING_MESSAGE);
            }
            else if (src == changeName)
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
            else if (src == screencap)
                EventHandler.screencap();
            else if (src == takeScreencaps)
                EventHandler.takeScreencaps();
            else if (src == preventSleep)
            {
               EastAngliaMapClient.preventSleep = preventSleep.isSelected();
               EastAngliaMapClient.writeSetting("preventSleep", String.valueOf(preventSleep.isSelected()));
            }
            else if (src == minToSys)
            {
                EastAngliaMapClient.minimiseToSysTray = minToSys.isSelected();
                EastAngliaMapClient.writeSetting("minimiseToSysTray", String.valueOf(minToSys.isSelected()));
            }
            else if (src == exit)
            {
                EastAngliaMapClient.writeSetting("windowSize", ((int) EastAngliaMapClient.frameSignalMap.frame.getSize().getWidth()) + "," + ((int) EastAngliaMapClient.frameSignalMap.frame.getSize().getHeight()));
                EastAngliaMapClient.writeSetting("lastTab", Integer.toString(EastAngliaMapClient.frameSignalMap.TabBar.getSelectedIndex()));

                System.exit(0);
            }
        }
    };

    public OptionContexMenu(Component invoker, int x, int y)
    {
        super();

        toggleOpacity      = new JCheckBoxMenuItem("Toggle Opacity",      EastAngliaMapClient.opaque);
        toggleDescriptions = new JCheckBoxMenuItem("Toggle Descriptions", EastAngliaMapClient.showDescriptions);
        berthVisibility    = new JCheckBoxMenuItem("Berths Visibility",   EastAngliaMapClient.berthsVisible);
        signalVisibility   = new JCheckBoxMenuItem("Signals Visibility",  EastAngliaMapClient.signalsVisible);
        trainHistory       = new JMenuItem("Train History");
        refresh            = new JMenuItem("Refresh Data");
        reconnect          = new JMenuItem("Reconnect");
        changeName         = new JMenuItem("Change Name");
        screencap          = new JCheckBoxMenuItem("Auto Screencap", EastAngliaMapClient.screencap);
        takeScreencaps     = new JMenuItem("Take Screencaps");
        preventSleep       = new JCheckBoxMenuItem("Keep your PC Awake",      EastAngliaMapClient.preventSleep);
        minToSys           = new JCheckBoxMenuItem("Minimise to System Tray", EastAngliaMapClient.minimiseToSysTray);
        exit               = new JMenuItem("Exit");

        add(toggleOpacity).addActionListener(clickEvent);
        add(toggleDescriptions).addActionListener(clickEvent);
        add(berthVisibility).addActionListener(clickEvent);
        add(signalVisibility).addActionListener(clickEvent);
        addSeparator();
        add(refresh).addActionListener(clickEvent);
        add(reconnect).addActionListener(clickEvent);
        addSeparator();
        add(trainHistory).addActionListener(clickEvent);
        add(changeName).addActionListener(clickEvent);

        if (EastAngliaMapClient.screencappingActive)
        {
            addSeparator();
            add(screencap).addActionListener(clickEvent);
            add(takeScreencaps).addActionListener(clickEvent);
        }

        addSeparator();
        add(preventSleep).addActionListener(clickEvent);
        add(minToSys).addActionListener(clickEvent);
        add(exit).addActionListener(clickEvent);

        show(invoker, x, y);
        requestFocus();
    }
}