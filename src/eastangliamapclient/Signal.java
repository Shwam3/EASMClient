package eastangliamapclient;

import eastangliamapclient.Signals.SignalDirection;
import eastangliamapclient.gui.SignalMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

public class Signal extends JComponent
{
    private static final Color STATE_0 = new Color(153,  0, 0); // on - red
    private static final Color STATE_1 = new Color(0,  153, 0); // off - green
    private static final Color STATE_3 = new Color(64, 64, 64); // unknown - grey

    private final String          SIGNAL_ID;
    private final SignalDirection SIGNAL_DIRECTION;

    private int currentState = 3;

    public Signal(SignalMap.BackgroundPanel pnl, int x, int y, String signalId, SignalDirection direction)
    {
        SIGNAL_ID = signalId;
        SIGNAL_DIRECTION = direction;

        setOpaque(false);
        setForeground(STATE_1);

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
        Signals.putSignal(signalId, this);

        setToolTipText(signalId);
        setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g.create();

        switch (SIGNAL_DIRECTION)
        {
            case RIGHT:
                drawSignal(0, 0, g2d);

                g2d.setColor(new Color(0x7f7f7f));
                g2d.fillRect(8, 3, 6, 2);
                break;

            case LEFT:
                drawSignal(8, 0, g2d);

                g2d.setColor(new Color(0x7f7f7f));
                g2d.fillRect(8, 7, 6, 2);
                break;

            case UP:
                drawSignal(0, 8, g2d);

                g2d.setColor(new Color(0x7f7f7f));
                g2d.fillRect(3, 0, 2, 6);
                break;

            case DOWN:
                drawSignal(0, 0, g2d);

                g2d.setColor(new Color(0x7f7f7f));
                g2d.fillRect(3, 8, 2, 6);
                break;

            default:
                drawSignal(0, 0, g2d);
                break;
        }
    }

    private static void drawSignal(int x, int y, Graphics2D g2d)
    {
        g2d.drawLine(x+2, y, x+5, y);
        g2d.drawLine(x+1, y+1, x+6, y+1);

        g2d.fillRect(x, y+2, x+7, y+6);

        g2d.drawLine(x+1, y+6, x+6, y+6);
        g2d.drawLine(x+2, y+7, x+5, y+7);
    }
}