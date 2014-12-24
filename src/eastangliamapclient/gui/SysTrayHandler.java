package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import static eastangliamapclient.EastAngliaMapClient.minimiseToSysTray;
import static eastangliamapclient.EastAngliaMapClient.trayIcon;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.imageio.ImageIO;

public class SysTrayHandler
{
    public static void initSysTray()
    {
        if (SystemTray.isSupported() && minimiseToSysTray)
        {
            ActionListener actionListener = new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent evt)
                {
                    EastAngliaMapClient.frameSignalMap.setVisible(true);
                }
            };
            MouseListener mouseListener = new MouseAdapter()
            {
                @Override
                public void mousePressed(MouseEvent evt)
                {
                    trayIcon.setPopupMenu(getPopupMenu());
                }
            };

            try
            {
                trayIcon = new TrayIcon(ImageIO.read(SysTrayHandler.class.getResource("/eastangliamapclient/resources/TrayIcon.png")));
                trayIcon.setToolTip("East Anglia Signal Map - v" + EastAngliaMapClient.VERSION);
                trayIcon.setImageAutoSize(true);
                trayIcon.setPopupMenu(getPopupMenu());
                trayIcon.addActionListener(actionListener);
                trayIcon.addMouseListener(mouseListener);
                SystemTray.getSystemTray().add(trayIcon);
            }
            catch (IOException | AWTException e) {}
        }
    }

    private static PopupMenu getPopupMenu()
    {
        final PopupMenu pm = new PopupMenu();
        final MenuItem exit = new MenuItem("Exit");
        final MenuItem showWindow = new MenuItem("Show window");
        final MenuItem reconnect = new MenuItem("Reconnect");
        //final CheckboxMenuItem screenshot = new CheckboxMenuItem("Auto Screenshot", EastAngliaMapClient.screencap);

        ActionListener menuListener = new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent evt)
            {
                Object src = evt.getSource();
                if (src == exit)
                    System.exit(0);
                else if (src == showWindow)
                    EastAngliaMapClient.frameSignalMap.setVisible(true);
                else if (src == reconnect)
                    EastAngliaMapClient.reconnect(true);
            }
        };

        showWindow.addActionListener(menuListener);
        reconnect.addActionListener(menuListener);
        /*screenshot.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(ItemEvent evt)
            {
                EventHandler.screencap();
            }
        });*/
        exit.addActionListener(menuListener);

        pm.add(showWindow);
        pm.add(reconnect);
        /*if (EastAngliaMapClient.screencappingActive)
            pm.add(screenshot);*/
        pm.addSeparator();
        pm.add(exit);

        return pm;
    }

    public static void popup(String message, TrayIcon.MessageType type)
    {
        if (minimiseToSysTray && trayIcon != null)
            trayIcon.displayMessage("East Anglia Signal Map - v" + EastAngliaMapClient.VERSION, message, type);
    }

    public static void trayTooltip(String tooltip)
    {
        if (minimiseToSysTray && trayIcon != null)
            trayIcon.setToolTip("East Anglia Signal Map - v" + EastAngliaMapClient.VERSION + (tooltip.equals("") ? "" : "\n") + tooltip);
    }
}