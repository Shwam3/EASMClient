package eastangliamapclient;

import eastangliamapclient.gui.ListDialog;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Berth
{
    public        JLabel   label = new JLabel();

    private final String[] BERTH_IDs;
    private final String   BERTH_DESCRIPTION;
    private       String   currentHeadcode = "";
    private       String   currentBerthId  = "";
    private       boolean  isProblematic  = false;
    private boolean showDescription = false;

    public Berth(JPanel pnl, int x, int y, String... berthIds)
    {
        this.BERTH_IDs = berthIds;

        initLabel(pnl, x, y);

        BERTH_DESCRIPTION = label.getToolTipText().substring(3);
    }

    public void cancel(String berthId)
    {
        if (currentBerthId.equals(berthId) || berthId.equals("*"))
        {
            currentHeadcode = "";
            currentBerthId  = "";

            if (!EastAngliaMapClient.showDescriptions)
                label.setText("");
        }

        setOpaque(false);
    }

    public void interpose(String headcode, String berthId)
    {
        if (headcode.equals(currentHeadcode) && berthId.equals(currentBerthId))
            return;

        if (headcode.equals("") && !berthId.equals(currentBerthId))
            return;

        headcode = headcode.substring(0, Math.min(headcode.length(), 4));
        currentHeadcode = headcode;
        currentBerthId = berthId;

        if (!EastAngliaMapClient.showDescriptions)
            label.setText(currentHeadcode);

        setOpaque(false);
    }

    public void setOpaque(boolean opaque)
    {
        label.setOpaque(EventHandler.tempOpaqueBerth == this || opaque || (label.getText() != null && !label.getText().isEmpty()) || EastAngliaMapClient.opaque || isProblematic || EastAngliaMapClient.showDescriptions);
        colourise();
    }

    public boolean isProperHeadcode()
    {
        return Berths.isProperHeadcode(label.getText());
    }

    public String[] getIds()
    {
        return BERTH_IDs;
    }

    public String getCurrentId(boolean noBlank)
    {
        return currentBerthId.equals("") && noBlank ? BERTH_IDs[0] : currentBerthId;
    }

    public String getBerthDescription()
    {
        return BERTH_DESCRIPTION;
    }

    //<editor-fold defaultstate="collapsed" desc="Init Label">
    private void initLabel(JPanel pnl, int x, int y)
    {
        label.setBackground(EastAngliaMapClient.GREY);
        label.setFont(EastAngliaMapClient.TD_FONT);
        label.setForeground(EastAngliaMapClient.GREEN);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setFocusable(false);
        label.setBounds(x, y, 48, 16);
        label.setText("");
        label.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                EventHandler.tdMouseClick(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                Berth berth = Berths.getBerth((JLabel) evt.getComponent());

                if (berth != null)
                    if (evt.isControlDown())
                        berth.showDescription(true);
                    else
                        berth.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                Berth berth = Berths.getBerth((JLabel) evt.getComponent());

                if (berth != null)
                {
                    berth.showDescription(false);
                    berth.setOpaque(false);
                }
            }
        });

        StringBuilder tooltip = new StringBuilder();

        for (int i = 0; i < BERTH_IDs.length; i++)
        {
            if (i == 0)
                tooltip.append(BERTH_IDs[i]);
            else if (i == BERTH_IDs.length - 1)
                tooltip.append(" & " + BERTH_IDs[i]);
            else
                tooltip.append(", " + BERTH_IDs[i]);

            Berths.putBerth(BERTH_IDs[i], this);
        }

        label.setToolTipText("TD " + tooltip.toString());

        pnl.add(label);
    }
    //</editor-fold>

    public boolean setProblematicBerth(boolean isProblematic)
    {
        this.isProblematic = isProblematic;

        setOpaque(false);

        return this.isProblematic;
    }

    public boolean isProblematic()
    {
        return isProblematic;
    }

    public void showDescription(boolean show)
    {
        showDescription = EastAngliaMapClient.showDescriptions || show;

        if (showDescription)
            label.setText(BERTH_DESCRIPTION.substring(2, 6));
        else
            label.setText(currentHeadcode);

        setOpaque(false);
    }

    public void displayBerthsHistory()
    {
        List<String> berthsHistory = new ArrayList<>();

        new ListDialog(this, "Berth's History", "Trains which have passed through this berth (" + getBerthDescription() + "):", berthsHistory);

        EventHandler.berthContextMenu.actionInProgress = false;
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Berth=[description=" + BERTH_DESCRIPTION + ",berthIds=" + Arrays.deepToString(BERTH_IDs) + ",train=" + currentHeadcode + "]";
    }

    public boolean hasTrain()
    {
        return label.getText() != null && !label.getText().isEmpty();
    }

    public void displayTrainsHistory()
    {
        List<String> trainsHistory = new ArrayList<>();

        new ListDialog(this, "Train's History", "Berths which this train has passed through (" + getBerthDescription() + ") :", trainsHistory);

        EventHandler.berthContextMenu.actionInProgress = false;
    }

    public String getHeadcode()
    {
        return label.getText();
    }

    private void colourise()
    {
        if (isProblematic)
            label.setBackground(EastAngliaMapClient.RED);
        else
            label.setBackground(EastAngliaMapClient.GREY);

        if (showDescription)
            label.setForeground(EastAngliaMapClient.BLACK);
        else if (isProperHeadcode())
            label.setForeground(EastAngliaMapClient.GREEN);
        else
            label.setForeground(EastAngliaMapClient.WHITE);

        try { EastAngliaMapClient.SignalMap.frame.repaint(); }
        catch (NullPointerException e) {}
    }
}