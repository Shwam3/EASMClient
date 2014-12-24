package eastangliamapclient.gui;

import eastangliamapclient.*;
import java.awt.Component;
import java.awt.event.*;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class BerthContextMenu extends JPopupMenu
{
    private Berth berth;
    public boolean actionInProgress = false;

    ActionListener clickEvent = new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent evt)
        {
            actionInProgress = true;
            EventHandler.berthMenuClick(evt, berth);
        }
    };

    FocusListener menuFocus = new FocusListener()
    {
        @Override
        public void focusGained(FocusEvent evt)
        {
            EastAngliaMapClient.blockKeyInput = true;
        }

        @Override
        public void focusLost(FocusEvent evt)
        {
            if (!actionInProgress)
            {
                EastAngliaMapClient.blockKeyInput = false;
                EventHandler.getRidOfBerth();
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
            JMenuItem search = new JMenuItem("Search Headcode");
            search.addActionListener(clickEvent);
            search.addFocusListener(menuFocus);
            add(search);
            addSeparator();
        }

        for (String id : berth.getIds())
        {
            JMenuItem berthHistory = new JMenuItem("Berth\'s History (" + id + ")");
            berthHistory.addActionListener(clickEvent);
            berthHistory.addFocusListener(menuFocus);
            add(berthHistory);
        }

        if (berth.hasTrain())
        {
            JMenuItem trainHistory = new JMenuItem("Train\'s History (" + berth.getHeadcode() + ")");
            trainHistory.addActionListener(clickEvent);
            trainHistory.addFocusListener(menuFocus);
            add(trainHistory);
        }

        addFocusListener(menuFocus);
        show(component, x, y);
        requestFocus();
    }
}