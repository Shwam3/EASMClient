package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.MessageHandler;
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
        else if (cmd.startsWith("Train\'s History"))
        {
            if (berth.hasTrain())
                MessageHandler.requestHistoryOfTrain(berth.getCurrentId(true));
            else
                Berths.setOpaqueBerth(null);
        }
        else if (cmd.startsWith("Berth\'s History"))
        {
            String id = evt.getActionCommand().substring(17, 23);
            MessageHandler.requestHistoryOfBerth(id);
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

      //JMenuItem problemHere = new JMenuItem("Problem Here");
      //problemHere.addActionListener(clickEvent);
      //problemHere.addFocusListener(menuFocus);
      //add(problemHere);

        if (berth.isProperHeadcode())
        {
            JMenuItem search = new JMenuItem("<html><b>Search Headcode (RTT)</b></html>");
            search.addActionListener(clickEvent);
            search.addFocusListener(menuFocus);
            search.setToolTipText("<html>Search for this train on realtimetrains.co.uk<br>Note: Hold <i>Shift</i> if the train doesnt appear first time</html>");
            add(search);
            addSeparator();
        }

        for (String id : berth.getIds())
        {
            JMenuItem berthHistory = new JMenuItem("Berth\'s History (" + id + ")" + (berth.getIds().length > 1 && !EastAngliaMapClient.DataMap.getOrDefault(id, "").isEmpty() ? " [" + EastAngliaMapClient.DataMap.getOrDefault(id, "") + "]" : ""));
            berthHistory.addActionListener(clickEvent);
            berthHistory.addFocusListener(menuFocus);
            berthHistory.setEnabled(EastAngliaMapClient.connected);
            berthHistory.setToolTipText("<html>" + (EastAngliaMapClient.connected ? "" : "Disconnected from Server<br>") + "Show the history of this berth</html>");
            add(berthHistory);
        }

        if (berth.hasTrain())
        {
            JMenuItem trainHistory = new JMenuItem("Train\'s History (" + berth.getHeadcode() + ")");
            trainHistory.addActionListener(clickEvent);
            trainHistory.addFocusListener(menuFocus);
            trainHistory.setEnabled(EastAngliaMapClient.connected);
            trainHistory.setToolTipText("<html>" + (EastAngliaMapClient.connected ? "" : "Disconnected from Server<br>") + "Show the history or this train (unreliable)</html>");
            add(trainHistory);
        }

        addFocusListener(menuFocus);
        show(component, x, y);
        requestFocus();
    }
}