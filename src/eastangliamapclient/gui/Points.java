package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Points
{
    private static final Map<String, List<Point>> pointMap = new HashMap<>();

    public static Point getOrCreatePoint(SignalMapGui.BackgroundPanel pnl, int x, int y, String description, List<String> dataIds, PointType type0, PointType type1)
    {
        try
        {
            Point point = new Point(pnl, x, y, description == null ? "" : description, dataIds, type0, type1);

            dataIds.stream().forEachOrdered(dataId ->
            {
                List<Point> points = pointMap.getOrDefault(dataId, new ArrayList<>());

                if (!EastAngliaMapClient.DataMap.containsKey(dataId))
                    EastAngliaMapClient.DataMap.put(dataId, "0");

                points.add(point);
                pointMap.put(dataId, points);
            });
            return point;
        }
        catch (Error e) { EastAngliaMapClient.printThrowable(e, "Points"); }

        return null;
    }

    public static List<Point> getPoints(String pointId)
    {
        return pointMap.get(pointId.toUpperCase());
    }

    //public static void reset()
    //{
    //    pointMap = new HashMap<>(pointMap.size());
    //}

    // Controlled by signal visible state
    //public static void togglePointVisibilities()
    //{
    //    EastAngliaMapClient.frameSignalMap.getPanels().parallelStream()
    //            .forEach(bp -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    //}

    public static enum PointType
    {
        NONE         ("NONE",    false, false),
        N_HORZ_LF_UP ("NHzLfUp", false, false),
        R_HORZ_LF_UP ("RHzLfUp", false, false),
        N_HORZ_LF_DN ("NHzLfDn", false, false),
        R_HORZ_LF_DN ("RHzLfDn", false, false),
        N_HORZ_RT_UP ("NHzRtUp", false, false),
        R_HORZ_RT_UP ("RHzRtUp", false, false),
        N_HORZ_RT_DN ("NHzRtDn", false, false),
        R_HORZ_RT_DN ("RHzRtDn", false, false),
        N_HORZ_LF_UR ("NHzLfUR", false, true),
        R_HORZ_LF_UR ("RHzLfUR", false, true),
        N_HORZ_LF_LR ("NHzLfLR", false, true),
        R_HORZ_LF_LR ("RHzLfLR", false, true),
        N_HORZ_RT_UL ("NHzRtUL", false, true),
        R_HORZ_RT_UL ("RHzRtUL", false, true),
        N_HORZ_RT_LL ("NHzRtLL", false, true),
        R_HORZ_RT_LL ("RHzRtLL", false, true),
        N_VERT_LF_UP ("NVtLfUp", true,  false),
        R_VERT_LF_UP ("RVtLfUp", true,  false),
        N_VERT_LF_DN ("NVtLfDn", true,  false),
        R_VERT_LF_DN ("RVtLfDn", true,  false),
        N_VERT_RT_UP ("NVtRtUp", true,  false),
        R_VERT_RT_UP ("RVtRtUp", true,  false),
        N_VERT_RT_DN ("NVtRtDn", true,  false),
        R_VERT_RT_DN ("RVtRtDn", true,  false),
        N_VERT_LL_UP ("NVtLLUp", true,  true),
        R_VERT_LL_UP ("RVtLLUp", true,  true),
        N_VERT_UL_DN ("NVtULDn", true,  true),
        R_VERT_UL_DN ("RVtULDn", true,  true),
        N_VERT_LR_UP ("NVtLRUp", true,  true),
        R_VERT_LR_UP ("RVtLRUp", true,  true),
        N_VERT_UR_DN ("NVtURDn", true,  true),
        R_VERT_UR_DN ("RVtURDn", true,  true),
        UP_BOTH_LEFT ("UBtLf",   false, false),
        DN_BOTH_LEFT ("DBtLf",   false, false),
        UP_BOTH_RIGHT("UBtRt",   false, false),
        DN_BOTH_RIGHT("DBtRt",   false, false),
        LF_BOTH_UP   ("LBtUp",   true,  false),
        RT_BOTH_UP   ("RBtUp",   true,  false),
        LF_BOTH_DN   ("LBtDn",   true,  false),
        RT_BOTH_DN   ("RBtDn",   true,  false),
        XO_FULL_VERT ("XOVt",    true,  false),
        XO_FULL_HORZ ("XOHz",    false, false),
        XO_HORZ_DN   ("XOHzRt",  false, false),
        XO_HORZ_UP   ("XOHzLf",  false, false),
        XO_VERT_RT   ("XOVtRt",  true,  false),
        XO_VERT_LF   ("XOVtLf",  true,  false);

        private final String id;
        private final boolean isVert;
        private final boolean isLong;
        private final boolean isNormal;

        private PointType(String id, boolean isVert, boolean isLong) { this.id = id; this.isVert = isVert; this.isLong = isLong; this.isNormal = id.startsWith("N"); }
        private PointType(String id, boolean isVert, boolean isLong, boolean isNormal) { this.id = id; this.isVert = isVert; this.isLong = isLong; this.isNormal = isNormal; }
        public String getId() { return id; }
        //public boolean isVert() { return isVert; }
        //public boolean isLong() { return isLong; }
        //public boolean isNormal() { return isNormal; }

        public static PointType getType(Object obj)
        {
            if (obj instanceof PointType)
                return (PointType) obj;

            for (PointType type : values())
                if (type.getId().equals(String.valueOf(obj)))
                    return type;
            return NONE;
        }
    }
}