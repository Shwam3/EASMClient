package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.EventHandler;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
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

    ActionListener clickEvent = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            EventHandler.optionMenuItemClick(evt);
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

        toggleOpacity.addActionListener(clickEvent);
        toggleDescriptions.addActionListener(clickEvent);
        toggleVisibility.addActionListener(clickEvent);
        refresh.addActionListener(clickEvent);
        reconnect.addActionListener(clickEvent);
        reset.addActionListener(clickEvent);
        trainHistory.addActionListener(clickEvent);

        add(toggleOpacity);
        add(toggleDescriptions);
        add(toggleVisibility);
        addSeparator();
        add(refresh);
        add(reconnect);
        add(reset);
        addSeparator();
        add(trainHistory);

        show(invoker, x, y);
        requestFocus();
    }
}