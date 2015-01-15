package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.HashMap;
import java.util.Map;

public class Signals
{
    private static Map<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalDirection direction)
    {
        Signal signal = signalMap.get(dataId);
        if (signal != null)
        {
            if (signal.getParent() != pnl)
                pnl.add(signal);

            signal.setLocation(x, y);
            return signal;
        }
        else
            return new Signal(pnl, x, y, description, dataId, direction == null ? SignalDirection.NONE : direction);
    }

    public static boolean containsSignal(String signalId)
    {
        return signalMap.containsKey(signalId);
    }

    public static void putSignal(String signalId, Signal signal)
    {
        if (!containsSignal(signalId))
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

    public static enum SignalDirection
    {
        //LEFT_DOWN,
        //LEFT_UP,
        //RIGHT_DOWN,
        //RIGHT_UP,
        //UP_LEFT,
        //UP_RIGHT,
        //DOWN_LEFT,
        //DOWN_RIGHT,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        GANTRY_LEFT,
        GANTRY_RIGHT,
        GANTRY_UP,
        GANTRY_DOWN,
        NONE;
    }
}