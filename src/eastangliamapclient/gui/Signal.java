package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.ScreencapManager;
import eastangliamapclient.gui.Signals.SignalType;
import eastangliamapclient.gui.SignalMapGui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JComponent;

public class Signal extends JComponent
{
    //private static final Color TEXT_COLOUR          = Color.LIGHT_GRAY;
    private static final Color COLOUR_STATE_BLANK   = new Color(64, 64, 64); // blank - grey (border)
    private static final Color COLOUR_STATE_UNKNOWN = new Color(64, 64, 64); // unknown - grey (solid)
    private static final Color COLOUR_STATE_ON      = new Color(153,  0, 0); // on - red
    private static final Color COLOUR_STATE_OFF     = new Color(0,  153, 0); // off - green
    private static final Color COLOUR_BACKGROUND    = new Color(0, 0, 0, 64);

    private static final Color SIGNAL_POST_COLOUR = Color.WHITE;

    private static final int STATE_BLANK   = -1;
    private static final int STATE_0       = 0;
    private static final int STATE_1       = 1;
    private static final int STATE_UNKNOWN = 2;

    private boolean isShunt = false;
    private String TEXT_0 = null;
    private String TEXT_1 = null;

    private final String     SIGNAL_ID;
    private final String     DATA_ID;
    private       SignalType SIGNAL_TYPE;
    private       Point      LOCATION;

    private int currentState = STATE_UNKNOWN;

    public Signal(SignalMapGui.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalType type)
    {
        SIGNAL_ID = description;
        DATA_ID = dataId;
        LOCATION = new Point(x, y);

        setType(type);
        setOpaque(false);
        setFont(EastAngliaMapClient.TD_FONT.deriveFont(8f));

        if (pnl != null)
            pnl.add(this, SignalMapGui.LAYER_SIGNALS);

        if (dataId.endsWith("PRED"))
            currentState = STATE_0;
        else if (dataId.trim().length() == 6)
        {
            Signals.putSignal(dataId, this);
            currentState = STATE_UNKNOWN;
        }
        else
            currentState = STATE_BLANK;

        setForeground(currentState == STATE_0 ? COLOUR_STATE_ON : (currentState == STATE_1 ? COLOUR_STATE_OFF : (currentState == STATE_UNKNOWN ? COLOUR_STATE_UNKNOWN : COLOUR_STATE_BLANK)));

        if (description == null || description.trim().equals(""))
            description = "Unnamed";

        setToolTipText(description + " (" + DATA_ID + ")");

        setVisible(true);
    }

    public void isShunt() { isShunt = true; }
    public void set0Text(String text) { TEXT_0 = text; setType(SIGNAL_TYPE); }
    public void set1Text(String text) { TEXT_1 = text; setType(SIGNAL_TYPE); }

    public void setType(SignalType direction)
    {
        SIGNAL_TYPE = direction;
        int x = LOCATION.x;
        int y = LOCATION.y;

        switch (direction)
        {
            case TEXT:
                int widthTX = 24;
                if (TEXT_0 != null && TEXT_1 != null) widthTX = Math.max(TEXT_0.length(), TEXT_1.length()) * 6;
                if (TEXT_0 == null && TEXT_1 != null) widthTX = TEXT_1.length() * 6;
                if (TEXT_0 != null && TEXT_1 == null) widthTX = TEXT_0.length() * 6;
                setBounds(x, y, widthTX, 8);
                setToolTipText(SIGNAL_ID + " (" + DATA_ID + ") [" + TEXT_0 + "/" + TEXT_1 + "]");
                break;

            case TRTS:
                setBounds(x - 12, y - 4, 24, 8);
                break;

            case TRACK_CIRCUIT:
                int widthTC  = TEXT_0 == null ? 16 : (int) Long.parseLong(TEXT_0);
                int heightTC = TEXT_1 == null ? 8  : (int) Long.parseLong(TEXT_1);
                setBounds(x, y, widthTC, heightTC);
                setToolTipText(SIGNAL_ID + " (" + DATA_ID + ") [" + TEXT_0 + "/" + TEXT_1 + "]");
                break;

            case POST_NONE:
            case POST_NONE_HIDDEN:
                setBounds(x - 4, y - 4, 8, 8);
                break;

            case POST_LEFT:
                setBounds(x - 12, y - 4, 14, 8); //sub: x-12 -> x-10
                break;

            case POST_RIGHT:
                setBounds(x - 4, y - 4, 14, 8);
                break;

            case POST_UP:
                setBounds(x - 4, y - 10, 8, 14);
                break;

            case POST_DOWN:
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
        LOCATION = new Point(x, y);
        setType(SIGNAL_TYPE);
    }

    @Override
    public void setLocation(Point p)
    {
        LOCATION = p;
        setType(SIGNAL_TYPE);
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
                    EastAngliaMapClient.printOut("[" + DATA_ID + " (" + SIGNAL_ID + ")] Change state from " + currentState + " to " + state);
            }

            currentState = state;

            setForeground(currentState == STATE_0 ? COLOUR_STATE_ON : (currentState == STATE_1 ? (isShunt ? Color.WHITE : COLOUR_STATE_OFF) : (currentState == STATE_UNKNOWN ? COLOUR_STATE_UNKNOWN : COLOUR_STATE_BLANK)));
        }

        setVisible(true);
        repaint();
    }

    @Override
    public void setOpaque(boolean b) { super.setOpaque(false); }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (ScreencapManager.isScreencapping && SIGNAL_TYPE == SignalType.POST_NONE_HIDDEN)
            return;

        if (EastAngliaMapClient.signalsVisible || ScreencapManager.isScreencapping)
        {
            Graphics2D g2d = (Graphics2D) g.create();

            //if (EastAngliaMapClient.opaque)
            //{
            //    Color c = g2d.getColor();
            //    g2d.setColor(EastAngliaMapClient.GREY);
            //    g2d.fillRect(0, 0, getWidth(), getHeight());
            //    g2d.setColor(c);
            //}
            /*else*/ if (SIGNAL_TYPE != SignalType.TEXT)
            {
                Color c = g2d.getColor();
                g2d.setColor(COLOUR_BACKGROUND);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(c);
            }

            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            switch (SIGNAL_TYPE)
            {
                case TEXT:
                    String toDraw = "ERR";
                    Color colour = null;
                    if (TEXT_0 != null && TEXT_1 != null)
                    {
                        colour = EastAngliaMapClient.GREEN;
                        toDraw = currentState == STATE_1 ? TEXT_1 : currentState == STATE_0 ? TEXT_0 : "N/A";
                    }
                    else if (TEXT_0 != null && TEXT_1 == null)
                    {
                        colour = currentState == STATE_0 ? EastAngliaMapClient.GREEN : new Color(20, 20, 20);
                        toDraw = currentState == STATE_1 || currentState == STATE_0 ? TEXT_0 : "N/A";
                    }
                    else if (TEXT_0 == null && TEXT_1 != null)
                    {
                        colour = currentState == STATE_1 ? EastAngliaMapClient.GREEN : new Color(20, 20, 20);
                        toDraw = currentState == STATE_1 || currentState == STATE_0 ? TEXT_1 : "N/A";
                    }

                    if (toDraw != null && !toDraw.trim().isEmpty())
                    {
                        //if (!EastAngliaMapClient.opaque)
                        //{
                              g2d.setColor(COLOUR_BACKGROUND);
                              g2d.fillRect(0, 0, getWidth(), getHeight());
                        //}

                        g2d.setColor(colour);
                        g2d.drawString(toDraw, 0, 8);

                        Component comp = EastAngliaMapClient.frameSignalMap.frame.getFocusOwner();
                        requestFocusInWindow();
                        if (comp != null)
                            comp.requestFocusInWindow();
                    }
                    else
                        setVisible(false);

                    break;

                case TRTS:
                    g2d.setColor(currentState == STATE_1 ? EastAngliaMapClient.GREEN : new Color(20, 20, 20));
                    g2d.drawString("TRTS", 0, 8);
                    break;

                case TRACK_CIRCUIT:
                    g2d.setColor(currentState == STATE_0 ? COLOUR_STATE_ON : COLOUR_STATE_UNKNOWN);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    break;

                case POST_RIGHT:
                    drawSignal(0, 0, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(8, 3, 6, 2);
                    break;

                case POST_LEFT:
                    drawSignal(6, 0, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(0, 3, 6, 2);
                    break;

                case POST_UP:
                    drawSignal(0, 6, g2d);

                    g2d.setColor(SIGNAL_POST_COLOUR);
                    g2d.fillRect(3, 0, 2, 6);
                    break;

                case POST_DOWN:
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

    private void drawSignal(int x, int y, Graphics2D g2d)
    {
        if (currentState == STATE_BLANK)
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
        else if (isShunt)
        {
            if (SIGNAL_TYPE == SignalType.POST_DOWN)
            {
                g2d.fillRect(x,   y,   2, 8);
                g2d.fillRect(x+2, y+1, 2, 7);
                g2d.fillRect(x+4, y+2, 1, 6);
                g2d.fillRect(x+5, y+3, 1, 5);
                g2d.fillRect(x+6, y+4, 1, 4);
                g2d.fillRect(x+7, y+6, 1, 2);
            }
            else if (SIGNAL_TYPE == SignalType.POST_RIGHT)
            {
                g2d.fillRect(x,   y+6, 1, 2);
                g2d.fillRect(x+1, y+4, 1, 4);
                g2d.fillRect(x+2, y+3, 1, 5);
                g2d.fillRect(x+3, y+2, 1, 6);
                g2d.fillRect(x+4, y+1, 2, 7);
                g2d.fillRect(x+6, y,   2, 8);
            }
            else if (SIGNAL_TYPE == SignalType.POST_LEFT)
            {
                g2d.fillRect(x,   y, 2, 8);
                g2d.fillRect(x+2, y, 2, 7);
                g2d.fillRect(x+4, y, 1, 6);
                g2d.fillRect(x+5, y, 1, 5);
                g2d.fillRect(x+6, y, 1, 4);
                g2d.fillRect(x+7, y, 1, 2);
            }
            else if (SIGNAL_TYPE == SignalType.POST_UP)
            {
                g2d.fillRect(x,   y,   8, 2);
                g2d.fillRect(x+1, y+2, 7, 2);
                g2d.fillRect(x+2, y+4, 6, 1);
                g2d.fillRect(x+3, y+5, 5, 1);
                g2d.fillRect(x+4, y+6, 4, 1);
                g2d.fillRect(x+6, y+7, 2, 1);
            }
            else
            {
                g2d.drawLine(x+2, y,   x+5, y);
                g2d.drawLine(x,   y+2, x,   y+5);
                g2d.drawLine(x+7, y+2, x+7, y+5);
                g2d.drawLine(x+2, y+7, x+5, y+7);

                g2d.fillRect(x+1, y+1, 6, 6);
            }
        }
        else
        {
            g2d.drawLine(x+2, y,   x+5, y);
            g2d.drawLine(x,   y+2, x,   y+5);
            g2d.drawLine(x+7, y+2, x+7, y+5);
            g2d.drawLine(x+2, y+7, x+5, y+7);

            g2d.fillRect(x+1, y+1, 6, 6);

        }
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Signal=[signalId=" + SIGNAL_ID + ",dataId=" + DATA_ID + ",currentState=" + currentState + "]";
    }
}