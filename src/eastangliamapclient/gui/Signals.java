package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import java.util.HashMap;
import java.util.Map;

public class Signals
{
    private static final Map<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMapGui.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalType type)
    {
        Signal signal = signalMap.get(dataId);
        if (signal != null)
        {
            if (signal.getParent() != pnl)
                pnl.add(signal, SignalMapGui.LAYER_SIGNALS);

            signal.setLocation(x, y);
            return signal;
        }
        else
            return new Signal(pnl, x, y, description == null ? "" : description, dataId, type == null ? SignalType.HIDDEN : type);
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

    //public static void reset()
    //{
    //    signalMap = new HashMap<>(signalMap.size());
    //}

    public static void toggleSignalVisibilities()
    {
        EastAngliaMapClient.signalsVisible = !EastAngliaMapClient.signalsVisible;

        EastAngliaMapClient.frameSignalMap.getPanels().parallelStream()
                .forEach(bp -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    }

    public static enum SignalType
    {
        TEXT         ("text"),
        TRTS         ("trts"),
        TRACK_CIRCUIT("tc"),
        POST_LEFT    ("left"),
        POST_RIGHT   ("right"),
        POST_UP      ("up"),
        POST_DOWN    ("down"),
        POST_NONE    ("none"),
        HIDDEN       ("test");

        private final String id;

        private SignalType(String code) { this.id = code; }
        public String getId() { return id; }

        public static SignalType getType(Object obj)
        {
            if (obj instanceof SignalType)
                return (SignalType) obj;

            for (SignalType type : values())
                if (type.getId().equals(String.valueOf(obj).toLowerCase()))
                    return type;
            return HIDDEN;
        }
    }
}