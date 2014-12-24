package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.HashMap;

public class Signals
{
    private static HashMap<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMap.BackgroundPanel pnl, int x, int y, String signalId, SignalDirection direction)
    {
        if (signalMap.containsKey(signalId))
            return signalMap.get(signalId);
        else
            return new Signal(pnl, x, y, signalId, direction);
    }

    public static boolean containsSignal(String signalId)
    {
        return signalMap.containsKey(signalId);
    }

    public static void putSignal(String signalId, Signal berth)
    {
        if (!containsSignal(signalId))
            signalMap.put(signalId, berth);
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