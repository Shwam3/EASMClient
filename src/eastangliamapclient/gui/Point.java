package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.ScreencapManager;
import eastangliamapclient.gui.Points.PointType;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;

public class Point extends JComponent
{
    private static final int STATE_0       = 0;
    private static final int STATE_1       = 1;
    private static final int STATE_UNKNOWN = -1;

    private Points.PointType TYPE_0 = PointType.NONE;
    private Points.PointType TYPE_1 = PointType.NONE;

    private final String POINT_ID;
    private final Map<String, Integer> DATA_IDs;

    public Point(SignalMapGui.BackgroundPanel pnl, int x, int y, String description, List<String> dataIds, PointType type0, PointType type1)
    {
        if (description == null || description.trim().equals(""))
            description = "Unnamed";

        POINT_ID = description;
        DATA_IDs = new HashMap<>(dataIds.size());
        dataIds.stream().forEachOrdered(dataId -> DATA_IDs.put(dataId, STATE_UNKNOWN));

        TYPE_0 = type0;
        TYPE_1 = type1;

        setLocation(x, y);
        setSize(16, 16);
        setOpaque(false);

        if (pnl != null)
            pnl.add(this, SignalMapGui.LAYER_SIGNALS);

        setToolTipText(POINT_ID + " (" + DATA_IDs.keySet().toString().replaceAll("[\\[\\]]", "") + ")");

        setVisible(true);
    }

    public void setState(int state, String dataId)
    {
        if (DATA_IDs.get(dataId) != state)
        {
            if (DATA_IDs.get(dataId) != 2 && EastAngliaMapClient.verbose)
            {
                if (POINT_ID.isEmpty())
                    EastAngliaMapClient.printOut("[" + dataId + "] Change state from " + DATA_IDs.get(dataId) + " to " + state);
                else
                    EastAngliaMapClient.printOut("[" + dataId + " (" + POINT_ID + ")] Change state from " + DATA_IDs.get(dataId) + " to " + state);
            }
        }
        DATA_IDs.put(dataId, state);

        setVisible(true);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        //if (DATA_IDs.values().stream().filter(val -> val >= 0).count() == STATE_UNKNOWN || (!EastAngliaMapClient.signalsVisible && !ScreencapManager.isScreencapping))
        //    return;

        if(!EastAngliaMapClient.signalsVisible && !ScreencapManager.isScreencapping)
            return;

        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setColor(Color.BLACK);

        switch(DATA_IDs.values().stream().filter(val -> val > 0).count() == STATE_0 ? TYPE_0 : TYPE_1)
        {
            case N_HORZ_LF_UP:
            case N_HORZ_RT_UP:
            case N_HORZ_LF_UR:
            case N_HORZ_RT_UL:
                g2d.fillRect(2, 5, 12, 2);
                break;
            case N_HORZ_LF_DN:
            case N_HORZ_RT_DN:
            case N_HORZ_LF_LR:
            case N_HORZ_RT_LL:
                g2d.fillRect(2, 9, 12, 2);
                break;
            case R_HORZ_LF_DN:
                g2d.fillRect(4, 7, 5, 1);
                g2d.fillRect(6, 8, 4, 1);
                break;
            case R_HORZ_LF_UP:
                g2d.fillRect(6, 7, 4, 1);
                g2d.fillRect(4, 8, 5, 1);
                break;
            case R_HORZ_RT_DN:
                g2d.fillRect(7, 7, 5, 1);
                g2d.fillRect(6, 8, 4, 1);
                break;
            case R_HORZ_RT_UP:
                g2d.fillRect(7, 8, 5, 1);
                g2d.fillRect(6, 7, 4, 1);
                break;
            case R_HORZ_LF_UR:
                g2d.fillRect(8, 7, 5, 1);
                g2d.fillRect(5, 8, 6, 1);
                break;
            case R_HORZ_LF_LR:
                g2d.fillRect(5, 7, 6, 1);
                g2d.fillRect(8, 8, 5, 1);
                break;
            case R_HORZ_RT_LL:
                g2d.fillRect(5, 7, 6, 1);
                g2d.fillRect(3, 8, 5, 1);
                break;
            case R_HORZ_RT_UL:
                g2d.fillRect(3, 7, 5, 1);
                g2d.fillRect(5, 8, 6, 1);
                break;

            case N_VERT_LF_DN:
            case N_VERT_LF_UP:
                g2d.fillRect(5, 2, 2, 12);
                break;
            case N_VERT_RT_DN:
            case N_VERT_RT_UP:
                g2d.fillRect(9, 2, 2, 12);
                break;
            case R_VERT_LF_DN:
                g2d.fillRect(8, 7, 1, 5);
                g2d.fillRect(7, 6, 1, 4);
                break;
            case R_VERT_LF_UP:
                g2d.fillRect(8, 4, 1, 5);
                g2d.fillRect(7, 6, 1, 4);
                break;
            case R_VERT_RT_DN:
                g2d.fillRect(7, 7, 1, 5);
                g2d.fillRect(8, 6, 1, 4);
                break;
            case R_VERT_RT_UP:
                g2d.fillRect(7, 4, 1, 5);
                g2d.fillRect(8, 6, 1, 4);
                break;

            case UP_BOTH_LEFT:
                g2d.fillRect(4, 8, 2, 1);
                g2d.fillRect(3, 9, 4, 1);
                g2d.fillRect(5, 10,3, 1);
                break;
            case DN_BOTH_LEFT:
                g2d.fillRect(5, 5, 3, 1);
                g2d.fillRect(3, 6, 4, 1);
                g2d.fillRect(4, 7, 2, 1);
                break;
            //case UP_BOTH_RIGHT:
            //    break;
            //case DN_BOTH_RIGHT:
            //    break;
            //case LF_BOTH_UP:
            //    break;
            //case RT_BOTH_UP:
            //    break;
            //case LF_BOTH_DN:
            //    break;
            //case RT_BOTH_DN:
            //    break;

            case XO_FULL_HORZ:
                g2d.fillRect(7, 5, 2, 2);
                g2d.fillRect(7, 9, 2, 2);
                break;
            case XO_FULL_VERT:
                g2d.fillRect(5, 7, 2, 2);
                g2d.fillRect(9, 7, 2, 2);
                break;
            case XO_HORZ_UP:
                g2d.fillRect(7, 5, 2, 2);
                break;
            case XO_HORZ_DN:
                g2d.fillRect(7, 9, 2, 2);
                break;
            case XO_VERT_LF:
                g2d.fillRect(5, 7, 2, 2);
                break;
            case XO_VERT_RT:
                g2d.fillRect(9, 7, 2, 2);
                break;
        }

        g2d.dispose();
    }

    @Override
    public String toString()
    {
        return String.format("eastangliamap.Point=[pointId=%s,data=%s,type0=%s,type1=%s]", POINT_ID, DATA_IDs.toString(), TYPE_0, TYPE_1);
    }
}