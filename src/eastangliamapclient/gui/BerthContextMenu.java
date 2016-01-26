package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.gui.mapelements.Berth;
import eastangliamapclient.gui.mapelements.Berths;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class BerthContextMenu extends JPopupMenu
{
    private Berth berth;
    public boolean actionInProgress = false;

    ActionListener clickEvent = evt ->
    {
        actionInProgress = true;

        String cmd = evt.getActionCommand();

        if (cmd.equals("Search Headcode (RTT)"))
        {
            try
            {
                Desktop.getDesktop().browse(new URI(String.format("http://www.realtimetrains.co.uk/search/advancedhandler?type=advanced&search=%s%s", berth.getHeadcode(), (evt.getModifiers() & ActionEvent.SHIFT_MASK) != 0 || berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));
            }
            catch (URISyntaxException | IOException e) {}
        }

        EastAngliaMapClient.blockKeyInput = false;
    };

    FocusListener menuFocus = new FocusListener()
    {
        @Override
        public void focusGained(FocusEvent evt)
        {
            EastAngliaMapClient.blockKeyInput = true;
            EastAngliaMapClient.frameSignalMap.frame.repaint();
        }

        @Override
        public void focusLost(FocusEvent evt)
        {
            if (!actionInProgress)
            {
                EastAngliaMapClient.blockKeyInput = false;

                EastAngliaMapClient.frameSignalMap.frame.repaint();
                Berths.setOpaqueBerth(null);
            }
            else
            {
                actionInProgress = false;
            }

            berth.setOpaque(false);
        }
    };

    public BerthContextMenu(Berth berth, Component component, int x, int y)
    {
        this.berth = berth;

        if (berth.isProperHeadcode())
        {
            JMenuItem search = new JMenuItem("<html><b>Search Headcode (RTT)</b></html>");
            search.addActionListener(clickEvent);
            search.addFocusListener(menuFocus);
            search.setToolTipText("<html>Search for this train on realtimetrains.co.uk<br>Note: Hold <i>Shift</i> if the train doesnt appear first time</html>");
            add(search);
        }

        addFocusListener(menuFocus);
        show(component, x, y);
        requestFocus();
    }
}