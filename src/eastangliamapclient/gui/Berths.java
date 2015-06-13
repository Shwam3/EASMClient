package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.gui.SignalMapGui;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Berths
{
    private static Map<String, Berth> berthMap = new HashMap<>();
    private static Berth OPAQUE_BERTH = null;

    public static Berth getOrCreateBerth(SignalMapGui.BackgroundPanel pnl, int x, int y, String... berthIds)
    {
        Berth berth = berthMap.get(berthIds[0]);
        if (berth != null)
        {
            if (berth.getParent() != pnl)
                pnl.add(berth, SignalMapGui.LAYER_BERTHS);

            berth.setLocation(x, y);
            return berth;
        }
        else
            return new Berth(pnl, x, y, berthIds);
    }

    public static boolean containsBerth(String berthId)
    {
        return berthMap.containsKey(berthId);
    }

    public static void putBerth(String berthId, Berth berth)
    {
        if (!containsBerth(berthId))
            berthMap.put(berthId, berth);
    }

    public static boolean isProperHeadcode(String headcode)
    {
        try
        {
            return Pattern.matches("([0-9][A-Z][0-9]{2}|[0-9]{3}[A-Z])", headcode);
        }
        catch (PatternSyntaxException e)
        {
            return false;
        }
    }

    public static Berth getBerth(String berthId)
    {
        if (berthId != null && berthMap.containsKey(berthId.toUpperCase()))
            return berthMap.get(berthId.toUpperCase());

        return null;
    }

    public static Set<Map.Entry<String, Berth>> getEntrySet()
    {
        return berthMap.entrySet();
    }

    public static Set<String> getKeySet()
    {
        return berthMap.keySet();
    }

    public static void setOpaqueBerth(Berth berth)
    {
        if (OPAQUE_BERTH != null)
        {
            Berth opaqueBerth = OPAQUE_BERTH;
            OPAQUE_BERTH = berth;
            opaqueBerth.setOpaque(false);
        }
        else
            OPAQUE_BERTH = berth;

        if (berth != null)
            berth.repaint();
    }

    public static Berth getOpaqueBerth()
    {
        return OPAQUE_BERTH;
    }

    public static void printIds()
    {
        printBerths("Berth Ids:", false);

        List<String> berthIds = new ArrayList<>();

        berthMap.keySet().parallelStream().forEach(id ->  berthIds.add(id));

        Collections.sort(berthIds);

        berthIds.stream().forEachOrdered(id -> printBerths("  " + id, false));
    }

    //public static void reset()
    //{
    //    berthMap = new HashMap<>(berthMap.size());
    //}

    public static void toggleBerthsOpacities()
    {
        EastAngliaMapClient.opaque = !EastAngliaMapClient.opaque;

        Berths.getEntrySet().parallelStream().forEach(pairs -> pairs.getValue().setOpaque(EastAngliaMapClient.opaque));

        EastAngliaMapClient.frameSignalMap.frame.repaint();
    }

    public static void toggleBerthDescriptions()
    {
        EastAngliaMapClient.showDescriptions = !EastAngliaMapClient.showDescriptions;

        EastAngliaMapClient.frameSignalMap.getPanels().parallelStream()
                .forEach(bp -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    }

    public static void toggleBerthVisibilities()
    {
        EastAngliaMapClient.berthsVisible = !EastAngliaMapClient.berthsVisible;

        EastAngliaMapClient.frameSignalMap.getPanels().parallelStream()
                .forEach(bp -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    }

    private static void printBerths(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Berths] " + message);
        else
            EastAngliaMapClient.printOut("[Berths] " + message);
    }
}