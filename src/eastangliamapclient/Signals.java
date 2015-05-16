package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.HashMap;
import java.util.Map;

public class Signals
{
    private static Map<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalType direction)
    {
        Signal signal = signalMap.get(dataId);
        if (signal != null)
        {
            if (signal.getParent() != pnl)
                pnl.add(signal, SignalMap.LAYER_SIGNALS);

            signal.setLocation(x, y);
            return signal;
        }
        else
            return new Signal(pnl, x, y, description == null ? "" : description, dataId, direction == null ? SignalType.POST_TEST : direction);
    }

    public static boolean signalExists(String signalId)
    {
        return signalMap.containsKey(signalId);
    }

    public static void putSignal(String signalId, Signal signal)
    {
        if (!signalExists(signalId))
            signalMap.put(signalId, signal);

        if (!EastAngliaMapClient.DataMap.containsKey(signalId))
            EastAngliaMapClient.DataMap.put(signalId, Integer.toString(3));
    }

    public static Signal getSignal(String signalId)
    {
        if (signalMap.containsKey(signalId.toUpperCase()))
            return signalMap.get(signalId.toUpperCase());

        return null;
    }

    /*public static void reset()
    {
        signalMap = new HashMap<>(signalMap.size());
    }*/

    public static void toggleSignalVisibilities()
    {
        EastAngliaMapClient.signalsVisible = !EastAngliaMapClient.signalsVisible;

        EastAngliaMapClient.frameSignalMap.getPanels().parallelStream()
                .forEach((bp) -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    }

    public static enum SignalType
    {
        TEXT,
        TRTS,
        POST_LEFT,
        POST_RIGHT,
        POST_UP,
        POST_DOWN,
        POST_NONE,
        POST_TEST;

        public static SignalType getDirection(Object obj)
        {
            if (obj instanceof SignalType)
                return (SignalType) obj;

            switch (String.valueOf(obj).toLowerCase().trim())
            {
                case "left":
                    return POST_LEFT;
                case "right":
                    return POST_RIGHT;
                case "up":
                    return POST_UP;
                case "down":
                    return POST_DOWN;
                case "trts":
                    return TRTS;
                case "text":
                    return TEXT;
                case "none":
                    return POST_NONE;
                case "test":
                    return POST_TEST;
            }

            return null;
        }
    }
}