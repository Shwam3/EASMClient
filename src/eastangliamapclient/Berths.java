package eastangliamapclient;

import java.util.*;
import java.util.regex.Pattern;
import javax.swing.JLabel;

public class Berths
{
    private static HashMap<String, Berth> berthMap = new HashMap<>();
    private static HashMap<JLabel, Berth> labelMap = new HashMap<>();

    public static boolean containsBerth(String berthId)
    {
        boolean containsKey = berthMap.containsKey(berthId);
        return containsKey;
    }

    public static void putBerth(String berthId, Berth berth)
    {
        if (!containsBerth(berthId))
        {
            berthMap.put(berthId, berth);
            labelMap.put(berth.label, berth);
        }
    }

    public static boolean isProperHeadcode(String headcode)
    {
        try
        {
            return Pattern.matches("([0-9][A-Z][0-9][0-9]|[0-9][0-9][0-9][A-Z])", headcode);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static Berth getBerth(String berthId)
    {
        if (berthMap.containsKey(berthId))
            return berthMap.get(berthId);

        return null;
    }

    public static Berth getBerth(JLabel berthLabel)
    {
        if (labelMap.containsKey(berthLabel))
            return labelMap.get(berthLabel);

        return null;
    }

    public static Object[] getAsArray()
    {
        List<String> list = new ArrayList<>();

        for (Map.Entry pairs : berthMap.entrySet())
        {
            Berth berth = (Berth) pairs.getValue();

            list.addAll(Arrays.asList(berth.getIds()));
        }

        return list.toArray();
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

        for (Map.Entry pairs : berthMap.entrySet())
            berthIds.add(pairs.getKey().toString());

        Collections.sort(berthIds);

        for (String id : berthIds)
            printBerths("  " + id, false);
    }

    public static void toggleBerthsOpacities()
    {
        EastAngliaMapClient.opaque = !EastAngliaMapClient.opaque;

        for (Map.Entry pairs : Berths.getEntrySet())
            ((Berth) pairs.getValue()).setOpaque(EastAngliaMapClient.opaque);

        EastAngliaMapClient.SignalMap.frame.repaint();
    }

    public static void toggleBerthDescriptions()
    {
        EastAngliaMapClient.showDescriptions = !EastAngliaMapClient.showDescriptions;

        List<String> sortedKeys = new ArrayList<>(Berths.getKeySet());
        for (String key : sortedKeys)
            Berths.getBerth(key).showDescription(EastAngliaMapClient.showDescriptions);
    }

    public static void toggleBerthVisibilities()
    {
        EastAngliaMapClient.visible = !EastAngliaMapClient.visible;

        List<String> sortedKeys = new ArrayList<>(Berths.getKeySet());
        for (String key : sortedKeys)
            Berths.getBerth(key).label.setVisible(EastAngliaMapClient.visible);
    }

    public static void clearMaps()
    {
        berthMap = new HashMap<>();
        labelMap = new HashMap<>();
    }

    private static void printBerths(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Berths] " + message);
        else
            EastAngliaMapClient.printOut("[Berths] " + message);
    }
}