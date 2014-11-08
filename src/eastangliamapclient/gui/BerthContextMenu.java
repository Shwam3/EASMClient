package eastangliamapclient.gui;

import eastangliamapclient.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.ArrayList;
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

        JMenuItem problemHere  = new JMenuItem("Problem Here");
        JMenuItem search       = new JMenuItem("Search Headcode");
        ArrayList<JMenuItem> berthHistories = new ArrayList<>();

        if (berth.getIds().length == 1)
        {
            JMenuItem berthHistory = new JMenuItem("Berth\'s History");
            berthHistory.addActionListener(clickEvent);
            berthHistory.addFocusListener(menuFocus);
            berthHistories.add(berthHistory);
        }
        else if (berth.getIds().length > 1)
        {
            for (String id : berth.getIds())
            {
                JMenuItem berthHistory = new JMenuItem("Berth\'s History (" + id + ")");
                berthHistory.addActionListener(clickEvent);
                berthHistory.addFocusListener(menuFocus);
                berthHistories.add(berthHistory);
            }
        }

        JMenuItem trainHistory = new JMenuItem("Train\'s History");

        problemHere .addActionListener(clickEvent);
        search      .addActionListener(clickEvent);
        trainHistory.addActionListener(clickEvent);

        problemHere .addFocusListener(menuFocus);
        search      .addFocusListener(menuFocus);
        trainHistory.addFocusListener(menuFocus);

        add(problemHere);

        if (berth.isProperHeadcode())
        {
            addSeparator();
            add(search);
        }

        addSeparator();

        for (JMenuItem jmi : berthHistories)
            add(jmi);

        if (berth.hasTrain())
            add(trainHistory);

        addFocusListener(menuFocus);
        show(component, x, y);
        requestFocus();
    }
}