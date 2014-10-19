package eastangliamapclient.gui;

import eastangliamapclient.Berths;
import eastangliamapclient.EastAngliaMapClient;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

public class OptionContexMenu extends JPopupMenu
{
    JCheckBoxMenuItem toggleOpacity;
    JCheckBoxMenuItem toggleVisibility;
    JCheckBoxMenuItem toggleDescriptions;

    JMenuItem refresh;
    JMenuItem reconnect;
    JMenuItem reset;
    JMenuItem trainHistory;

    JMenuItem changeName;

    ActionListener clickEvent = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            Object src = evt.getSource();
            if (src == toggleOpacity)
                Berths.toggleBerthsOpacities();
            else if (src == toggleVisibility)
                Berths.toggleBerthVisibilities();
            else if (src == toggleDescriptions)
                Berths.toggleBerthDescriptions();
            else if (src == refresh)
                EastAngliaMapClient.handler.requestAll();
            else if (src == reconnect)
                EastAngliaMapClient.reconnect();
            else if (src == reset)
                EastAngliaMapClient.refresh();
            else if (src == trainHistory)
            {
                String UUID = JOptionPane.showInputDialog(EastAngliaMapClient.SignalMap.frame, "Enter Train UUID:", "Train History", JOptionPane.QUESTION_MESSAGE);

                    if (UUID != null)
                        if (UUID.length() >= 5 && UUID.matches("[0-9]+"))
                            EastAngliaMapClient.handler.requestHistoryOfTrain(UUID);
                        else
                            JOptionPane.showMessageDialog(EastAngliaMapClient.SignalMap.frame, "'" + UUID + "' is not a valid train UUID", "Error", JOptionPane.WARNING_MESSAGE);
            }
            else if (src == changeName)
            {
                String newName = JOptionPane.showInputDialog("New Name:", EastAngliaMapClient.clientName);
                if (newName != null)
                {
                    EastAngliaMapClient.clientName = newName;
                    EastAngliaMapClient.handler.sendName(EastAngliaMapClient.clientName);
                }
            }
        }
    };

    public OptionContexMenu(Component invoker, int x, int y)
    {
        toggleOpacity      = new JCheckBoxMenuItem("Toggle Opacity",      EastAngliaMapClient.opaque);
        toggleDescriptions = new JCheckBoxMenuItem("Toggle Descriptions", EastAngliaMapClient.showDescriptions);
        toggleVisibility   = new JCheckBoxMenuItem("Toggle Visibility",   EastAngliaMapClient.visible);
        refresh            = new JMenuItem("Refresh Data");
        reconnect          = new JMenuItem("Reconnect");
        reset              = new JMenuItem("Reset Window");
        trainHistory       = new JMenuItem("Train History");
        changeName         = new JMenuItem("Change Name");

        toggleOpacity.addActionListener(clickEvent);
        toggleDescriptions.addActionListener(clickEvent);
        toggleVisibility.addActionListener(clickEvent);
        refresh.addActionListener(clickEvent);
        reconnect.addActionListener(clickEvent);
        reset.addActionListener(clickEvent);
        trainHistory.addActionListener(clickEvent);
        changeName.addActionListener(clickEvent);

        add(toggleOpacity);
        add(toggleDescriptions);
        add(toggleVisibility);
        addSeparator();
        add(refresh);
        add(reconnect);
        add(reset);
        addSeparator();
        add(trainHistory);
        addSeparator();
        add(changeName);

        show(invoker, x, y);
        requestFocus();
    }
}