package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.util.HashMap;
import java.util.Map;

public class Signals
{
    private static Map<String, Signal> signalMap = new HashMap<>();

    public static Signal getOrCreateSignal(SignalMap.BackgroundPanel pnl, int x, int y, String description, String dataId, SignalType type)
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
            return new Signal(pnl, x, y, description == null ? "" : description, dataId, type == null ? SignalType.POST_NONE_HIDDEN : type);
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
                .forEach((bp) -> bp.repaint(0, 0, bp.getWidth(), bp.getHeight()));
    }

    public static enum SignalType
    {
        TEXT          ("text"),
        TRTS          ("trts"),
        TRACK_CIRCUIT ("tc"),
        POST_LEFT     ("left"),
        POST_RIGHT    ("right"),
        POST_UP       ("up"),
        POST_DOWN     ("down"),
        POST_NONE     ("none"),
        POST_NONE_HIDDEN     ("text");

        private final String code;

        private SignalType(String code) { this.code = code; }
        public String getCode() { return code; }

        public static SignalType getType(Object obj)
        {
            if (obj instanceof SignalType)
                return (SignalType) obj;

            for (SignalType type : values())
                if (type.getCode().equals(String.valueOf(obj).toLowerCase()))
                    return type;
            return null;
        }
    }
}