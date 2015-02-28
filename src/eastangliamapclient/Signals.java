package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.HashMap;
import java.util.Map;

public class Signals
{
    private static Map<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalPostDirection direction)
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
            return new Signal(pnl, x, y, description == null ? "" : description, dataId, direction == null ? SignalPostDirection.TEST : direction);
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

        for (SignalMap.BackgroundPanel bp : EastAngliaMapClient.frameSignalMap.getPanels())
            bp.repaint(0, 0, bp.getWidth(), bp.getHeight());
    }

    public static enum SignalPostDirection
    {
        TEXT,
        TRTS,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        GANTRY_LEFT,
        GANTRY_RIGHT,
        GANTRY_UP,
        GANTRY_DOWN,
        NO_POST,
        TEST;

        public static SignalPostDirection getDirection(Object obj)
        {
            if (obj instanceof SignalPostDirection)
                return (SignalPostDirection) obj;

            switch (String.valueOf(obj).toLowerCase().trim())
            {
                case "left":
                    return LEFT;
                case "right":
                    return RIGHT;
                case "up":
                    return UP;
                case "down":
                    return DOWN;
                case "trts":
                    return TRTS;
                case "text":
                    return TEXT;
                case "none":
                    return NO_POST;
                case "test":
                    return TEST;
            }

            return null;
        }
    }
}