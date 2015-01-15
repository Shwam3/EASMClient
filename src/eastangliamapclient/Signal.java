package eastangliamapclient;

import eastangliamapclient.Signals.SignalDirection;
import eastangliamapclient.gui.SignalMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class Signal extends JComponent
{
    private static final Color STATE_COLOUR_ON = new Color(153,  0, 0); // on - red
    private static final Color STATE_COLOUR_OFF = new Color(0,  153, 0); // off - green
    private static final Color STATE_COLOUR_UNKNOWN = new Color(64, 64, 64); // unknown - grey

    private static final Color SIGNAL_POST_COLOUR = new Color(0xBBBBBB);

    private static final int STATE_OFF     = 1;
    private static final int STATE_ON      = 0;
    private static final int STATE_UNKNOWN = 2;

    private final String          SIGNAL_ID;
    private final String          DATA_ID;
    private final SignalDirection SIGNAL_DIRECTION;

    private int currentState = STATE_UNKNOWN;

    public Signal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalDirection direction)
    {
        SIGNAL_ID = description;
        DATA_ID = dataId;
        SIGNAL_DIRECTION = direction;

        setOpaque(false);
        setForeground(STATE_COLOUR_UNKNOWN);

        switch (direction)
        {
            case NONE:
                setBounds(x - 4, y - 4, 8, 8);
                break;

            case LEFT:
                setBounds(x - 12, y - 4, 14, 8);
                break;

            case RIGHT:
                setBounds(x - 4, y - 4, 14, 8);
                break;

            case UP:
                setBounds(x - 4, y - 12, 8, 14);
                break;

            case DOWN:
                setBounds(x - 4, y - 4, 8, 14);
                break;

            default:
                setBounds(x, y, 8, 8);
                break;
        }

        pnl.add(this);
        Signals.putSignal(dataId, this);

        setToolTipText(description + " (" + DATA_ID + ")");
        setVisible(true);
    }

    public void setState(int state)
    {
        if (currentState != state)
        {
            EastAngliaMapClient.printOut("[" + SIGNAL_ID + " (" + DATA_ID + ")] Change state from " + currentState + " to " + state);

            currentState = state;

            setForeground(currentState == STATE_ON ? STATE_COLOUR_ON : currentState == STATE_OFF ? STATE_COLOUR_OFF : STATE_COLOUR_UNKNOWN);
        }

        repaint();
    }

    @Override
    public void setOpaque(boolean b) { super.setOpaque(false); }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (EastAngliaMapClient.visible || EventHandler.isScreencapping)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            //Color c = g2d.getColor();
            //g2d.setColor(new Color(0x333300));
            //g2d.fillRect(0, 0, getWidth(), getHeight());

            //g2d.setColor(c);

            switch (SIGNAL_DIRECTION)
            {
                case RIGHT:
                    drawSignal(0, 0, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(8, 3, 6, 2);
                    break;

                case LEFT:
                    drawSignal(6, 0, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(0, 3, 6, 2);
                    break;

                case UP:
                    drawSignal(0, 8, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(3, 0, 2, 6);
                    break;

                case DOWN:
                    drawSignal(0, 0, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(3, 8, 2, 6);
                    break;

                default:
                    drawSignal(0, 0, g2d);
                    break;
            }

            g2d.dispose();
        }
    }

    private static void drawSignal(int x, int y, Graphics2D g2d)
    {
        g2d.drawLine(x+2, y,   x+5, y);
        g2d.drawLine(x+1, y+1, x+6, y+1);

        g2d.fillRect(x, y+2, x+8, y+4);

        g2d.drawLine(x+1, y+6, x+6, y+6);
        g2d.drawLine(x+2, y+7, x+5, y+7);
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Signal=[signalId=" + SIGNAL_ID + ",dataId=" + DATA_ID + ",currentState=" + currentState + "]";
    }
}