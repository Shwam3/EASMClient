package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Berths
{
    private static Map<String, Berth> berthMap = new HashMap<>();

    public static Berth getOrCreateBerth(SignalMap.BackgroundPanel pnl, int x, int y, String... berthIds)
    {
        Berth berth = berthMap.get(berthIds[0]);
        if (berth != null)
        {
            if (berth.getParent() != pnl)
                pnl.add(berth);

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
        if (berthMap.containsKey(berthId.toUpperCase()))
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

    public static void printIds()
    {
        printBerths("Berth Ids:", false);

        List<String> berthIds = new ArrayList<>();

        for (String id : berthMap.keySet())
            berthIds.add(id);

        Collections.sort(berthIds);

        for (String id : berthIds)
            printBerths("  " + id, false);
    }

    public static void toggleBerthsOpacities()
    {
        EastAngliaMapClient.opaque = !EastAngliaMapClient.opaque;

        for (Map.Entry<String, Berth> pairs : Berths.getEntrySet())
            pairs.getValue().setOpaque(EastAngliaMapClient.opaque);

        EastAngliaMapClient.frameSignalMap.frame.repaint();
    }

    public static void toggleBerthDescriptions()
    {
        EastAngliaMapClient.showDescriptions = !EastAngliaMapClient.showDescriptions;

        for (SignalMap.BackgroundPanel bp : EastAngliaMapClient.frameSignalMap.getPanels())
            bp.repaint(0, 0, bp.getWidth(), bp.getHeight());
    }

    public static void toggleBerthVisibilities()
    {
        EastAngliaMapClient.visible = !EastAngliaMapClient.visible;

        for (SignalMap.BackgroundPanel bp : EastAngliaMapClient.frameSignalMap.getPanels())
            bp.repaint(0, 0, bp.getWidth(), bp.getHeight());
    }

    private static void printBerths(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Berths] " + message);
        else
            EastAngliaMapClient.printOut("[Berths] " + message);
    }
}