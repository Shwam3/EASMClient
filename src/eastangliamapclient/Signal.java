package eastangliamapclient;

import eastangliamapclient.Signals.SignalPostDirection;
import eastangliamapclient.gui.SignalMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JComponent;

public class Signal extends JComponent
{
    //private static final Color TEXT_COLOUR          = Color.LIGHT_GRAY;
    private static final Color STATE_COLOUR_BLANK   = new Color(64, 64, 65); // blank - grey
    private static final Color STATE_COLOUR_ON      = new Color(153,  0, 0); // on - red
    private static final Color STATE_COLOUR_OFF     = new Color(0,  153, 0); // off - green
    private static final Color STATE_COLOUR_UNKNOWN = new Color(64, 64, 64); // unknown - grey

    private static final Color SIGNAL_POST_COLOUR = Color.WHITE;

    private static final int STATE_BLANK   = -1;
    private static final int STATE_ON      = 0;
    private static final int STATE_OFF     = 1;
    private static final int STATE_UNKNOWN = 2;

    private final String          SIGNAL_ID;
    private final String          DATA_ID;
    private       SignalPostDirection SIGNAL_DIRECTION;
    private       Point           LOCATION;

    private       boolean         isShunt    = false;
    //private       boolean         isCrossing = false;

    private int currentState = STATE_UNKNOWN;

    public Signal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalPostDirection direction)
    {
        SIGNAL_ID = description;
        DATA_ID = dataId;
        LOCATION = new Point(x, y);

        setDirection(direction);
        setOpaque(false);
        setFont(EastAngliaMapClient.TD_FONT.deriveFont(8f));

        if (pnl != null)
            pnl.add(this);

        if (dataId.endsWith("PRED"))
            currentState = STATE_ON;
        else if (dataId.trim().length() == 6)
        {
            Signals.putSignal(dataId, this);
            currentState = STATE_UNKNOWN;
        }
        else
            currentState = STATE_BLANK;

        setForeground(currentState == STATE_ON ? STATE_COLOUR_ON : (currentState == STATE_OFF ? STATE_COLOUR_OFF : (currentState == STATE_UNKNOWN ? STATE_COLOUR_UNKNOWN : STATE_COLOUR_BLANK)));

        if (description == null || description.trim().equals(""))
            description = "Unnamed";

        setToolTipText(description + " (" + DATA_ID + ")");

        setVisible(true);
    }

    public void isShunt() { isShunt = true; }
    //public void isCrossing() { isCrossing = true; }

    public void setDirection(SignalPostDirection direction)
    {
        SIGNAL_DIRECTION = direction;
        int x = LOCATION == null ? getX() : LOCATION.x;
        int y = LOCATION == null ? getY() : LOCATION.y;

        switch (direction)
        {
            case TRTS:
                setBounds(x - 12, y - 4, 24, 8);
                break;

            case NO_POST:
            case TEST:
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
    }

    @Override
    public void setLocation(int x, int y)
    {
        super.setLocation(x, y);
        LOCATION = new Point(x, y);
    }

    @Override
    public void setLocation(Point p)
    {
        super.setLocation(p);
        LOCATION = p;
    }

    public void setState(int state)
    {
        if (currentState != state)
        {
            if (currentState != 2 && EastAngliaMapClient.verbose)
            {
                if (SIGNAL_ID.isEmpty())
                    EastAngliaMapClient.printOut("[" + DATA_ID + "] Change state from " + currentState + " to " + state);
                else
                    EastAngliaMapClient.printOut("[" + SIGNAL_ID + " (" + DATA_ID + ")] Change state from " + currentState + " to " + state);
            }

            currentState = state;

            setForeground(currentState == STATE_ON ? STATE_COLOUR_ON : (currentState == STATE_OFF ? (isShunt ? Color.WHITE : STATE_COLOUR_OFF) : (currentState == STATE_UNKNOWN ? STATE_COLOUR_UNKNOWN : STATE_COLOUR_BLANK)));
        }

        repaint();
    }

    @Override
    public void setOpaque(boolean b) { super.setOpaque(false); }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (EventHandler.isScreencapping && SIGNAL_DIRECTION == SignalPostDirection.TEST)
            return;

        if (EastAngliaMapClient.signalsVisible || EventHandler.isScreencapping)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            Color c = g2d.getColor();
            g2d.setColor(new Color(1073741824, true));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            g2d.setColor(c);

            switch (SIGNAL_DIRECTION)
            {
                case TRTS:
                    g2d.setColor(currentState == STATE_OFF ? EastAngliaMapClient.GREEN : new Color(10, 10, 10));
                    g2d.drawString("TRTS", 0, 8);
                    break;

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
                    drawSignal(0, 6, g2d);

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
        if (g2d.getColor().equals(STATE_COLOUR_BLANK))
        {
            g2d.drawLine(x+2, y,   x+5, y);
            g2d.drawLine(x,   y+2, x,   y+5);
            g2d.drawLine(x+2, y+7, x+5, y+7);
            g2d.drawLine(x+7, y+2, x+7, y+5);

            g2d.fillRect(x+1, y+1, 1, 1);
            g2d.fillRect(x+6, y+1, 1, 1);
            g2d.fillRect(x+1, y+6, 1, 1);
            g2d.fillRect(x+6, y+6, 1, 1);
        }
        else
        {
            g2d.drawLine(x+2, y,   x+5, y);
            g2d.drawLine(x+1, y+1, x+6, y+1);

            g2d.fillRect(x, y+2, 8, 4);

            g2d.drawLine(x+1, y+6, x+6, y+6);
            g2d.drawLine(x+2, y+7, x+5, y+7);
        }
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Signal=[signalId=" + SIGNAL_ID + ",dataId=" + DATA_ID + ",currentState=" + currentState + "]";
    }
}