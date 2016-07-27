package eastangliamapclient.gui.mapelements;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.gui.SignalMapGui;
import eastangliamapclient.gui.SignalMapMenuBar;
import java.util.ArrayList;
import java.util.List;

public class Signals
{
    private static final List<Signal> signalMap = new ArrayList<>();

    public static Signal getOrCreateSignal(SignalMapGui.BackgroundPanel pnl, int x, int y, String description, String[] dataIDs, SignalType type)
    {
        //Signal signal = signalMap.get(dataIDs);
        //if (signal != null)
        //{
        //    if (signal.getParent() != pnl)
        //        pnl.add(signal, SignalMapGui.LAYER_SIGNALS);

        //    signal.setLocation(x, y);
        //    return signal;
        //}
        //else
            return new Signal(pnl, x, y, description == null ? "" : description, dataIDs, type == null ? SignalType.HIDDEN : type);
    }

    public static boolean signalExists(Signal signal)
    {
        return signalMap.contains(signal);
    }

    public static void putSignal(Signal signal)
    {
        if (!signalExists(signal))
            signalMap.add(signal);

        for (String id : signal.getIDs())
            EastAngliaMapClient.DataMap.putIfAbsent(id, "0");
    }

    public static Signal[] getSignal(String signalId)
    {
        List<Signal> sigs = new ArrayList<>();
        signalMap.stream().forEach(sig ->
        {
            for (String id : sig.getIDs())
                if (!sigs.contains(sig) && id.equals(signalId))
                    sigs.add(sig);
        });

        return sigs.isEmpty() ? null : sigs.toArray(new Signal[0]);
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

        SignalMapMenuBar.instance().updateCheckBoxes();
    }

    public static enum SignalType
    {
        TEXT         ("text"),
      //TRTS         ("trts"),
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