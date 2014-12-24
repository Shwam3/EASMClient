package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Berth extends JComponent
{
    private final String[] BERTH_IDs;
    private final String   BERTH_DESCRIPTION;
    private       String   currentHeadcode = "";
    private       String   currentBerthId  = "";
    private       boolean  mouseIn = false;
    private       boolean  showDescription = false;

    private       boolean  hasBorder  = false;
    private       boolean  isOpaque   = false;

    public Berth(SignalMap.BackgroundPanel pnl, int x, int y, String... berthIds)
    {
        this.BERTH_IDs = berthIds;

        initComponent(pnl, x, y);

        BERTH_DESCRIPTION = getToolTipText();

        showDescription = EastAngliaMapClient.showDescriptions;

        setOpaque(false);
    }

    public void hasBorder() { hasBorder = true; }

    public void cancel(String berthId)
    {
        if (currentBerthId.equals(berthId) || berthId.equals("*"))
        {
            currentHeadcode = "";
            currentBerthId  = "";
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
        currentHeadcode = headcode == null ? "" : headcode;
        currentBerthId = berthId;

        setOpaque(false);
    }

    @Override
    public void setOpaque(boolean opaque)
    {
        isOpaque = opaque ||
                mouseIn ||
                /*isProblematic ||*/
                EventHandler.tempOpaqueBerth == this ||
                (currentHeadcode != null && !currentHeadcode.isEmpty()) ||
                EastAngliaMapClient.opaque ||
                EastAngliaMapClient.showDescriptions;

        super.setOpaque(false);

        repaint();
    }

    public boolean isProperHeadcode()
    {
        if (currentHeadcode != null && currentHeadcode.length() < 4)
            return false;

        return Berths.isProperHeadcode(currentHeadcode);
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

    private void initComponent(SignalMap.BackgroundPanel pnl, int x, int y)
    {
        setFocusable(false);
        setBounds(x, y, 48, 16);
        setBackground(EastAngliaMapClient.GREY);
        setFont(EastAngliaMapClient.TD_FONT);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                EventHandler.berthClick(evt);
            }

            @Override
            public void mouseEntered(MouseEvent evt)
            {
                mouseIn = true;

                if (evt.isControlDown())
                    showDescription(true);
                else
                    setOpaque(false);
            }

            @Override
            public void mouseExited(MouseEvent evt)
            {
                mouseIn = false;

                showDescription(false);
                setOpaque(false);
            }
        });

        StringBuilder tooltip = new StringBuilder();

        for (int i = 0; i < BERTH_IDs.length; i++)
        {
            if (i == 0)
                tooltip.append(BERTH_IDs[i]);
            else if (i == BERTH_IDs.length - 1)
                tooltip.append(" & ").append(BERTH_IDs[i]);
            else
                tooltip.append(", ").append(BERTH_IDs[i]);

            Berths.putBerth(BERTH_IDs[i], this);
        }

        setToolTipText(tooltip.toString());

        pnl.add(this);
    }

    /*public boolean setProblematicBerth(boolean isProblematic)
    {
        this.isProblematic = isProblematic;

        setOpaque(false);

        return this.isProblematic;
    }

    public boolean isProblematic()
    {
        return isProblematic;
    }*/

    public void showDescription(boolean show)
    {
        showDescription = show;

        setOpaque(false);
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Berth=[description=" + BERTH_DESCRIPTION + ",berthIds=" + Arrays.deepToString(BERTH_IDs) + ",train=" + currentHeadcode + "]";
    }

    public boolean hasTrain()
    {
        return currentHeadcode != null && !currentHeadcode.isEmpty();
    }

    public String getHeadcode()
    {
        return currentHeadcode;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();

        if (EventHandler.isScreencapping)
        {
            g2d.setColor(EastAngliaMapClient.GREY);
            if (hasBorder)
                g2d.drawRect(0, 0, 47, 15);

            if (currentHeadcode != null && !currentHeadcode.isEmpty())
            {
                g2d.fillRect(0, 0, getWidth(), getHeight());

                if (isProperHeadcode())
                    g2d.setColor(EastAngliaMapClient.GREEN);
                else
                    g2d.setColor(EastAngliaMapClient.WHITE);

                g2d.drawString(currentHeadcode, 1, 15);
            }
        }
        else
        {
            if (EastAngliaMapClient.visible)
            {
                g2d.setColor(EastAngliaMapClient.GREY);
                if (hasBorder)
                    g2d.drawRect(0, 0, 47, 15);

                if (EastAngliaMapClient.showDescriptions || isOpaque || currentHeadcode.length() > 0)
                    g2d.fillRect(0, 0, 48, 16);

                if (EastAngliaMapClient.showDescriptions || showDescription)
                {
                    g2d.setColor(EastAngliaMapClient.BLACK);
                    g2d.drawString(BERTH_DESCRIPTION.substring(2, 6), 1, 15);
                }
                else if (currentHeadcode != null && !currentHeadcode.isEmpty())
                {
                    if (isProperHeadcode())
                        g2d.setColor(EastAngliaMapClient.GREEN);
                    else
                        g2d.setColor(EastAngliaMapClient.WHITE);

                    g2d.drawString(currentHeadcode, 1, 15);
                }
            }
        }

        g2d.dispose();
    }
}