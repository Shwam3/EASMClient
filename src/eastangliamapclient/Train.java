package eastangliamapclient;

public class Train
{
    /*private final String  UID;
    private final String  TRUST_ID;
    public  final String  UUID;
    private       String  headcode = "";
    private       Berth   currentBerth;
    public        boolean isCancelled = false;
    private final HashMap<Long, Berth> berthHistory = new HashMap<>();*/

    public Train(String ignore) {}

    /*public Train(String headcode)
    {
        UUID = EastAngliaSignallingMap.getNextUUID();

        UID = "";
        TRUST_ID = "";
        this.headcode = headcode.trim();

        addCurrentBerthToHistory();
    }

    public Train(String headcode, String UID, String trustId)
    {
        UUID = EastAngliaSignallingMap.getNextUUID();

        this.UID = UID;
        TRUST_ID = trustId;
        this.headcode = headcode;

        addCurrentBerthToHistory();
    }

    public void setBerth(Berth berth)
    {
        currentBerth = berth;

        addCurrentBerthToHistory();
    }

    private void addCurrentBerthToHistory()
    {
        if (currentBerth == null)
            isCancelled = true;
        else
            berthHistory.put(new Date().getTime(), currentBerth);
    }

    public String getUID()
    {
        return UID;
    }

    public String getTrustId()
    {
        return TRUST_ID;
    }

    public String getHeadcode()
    {
        return headcode;
    }

    public Berth getCurrentBerth()
    {
        return currentBerth;
    }

    public HashMap<Long, Berth> getBerthHistory()
    {
        return berthHistory;
    }

    public void displayHistory()
    {
        if (berthHistory.isEmpty())
        {
            JOptionPane.showMessageDialog(null, "No trains have yet passed through this berth (" + currentBerth.getBerthDescription() + ")", "Train History", JOptionPane.PLAIN_MESSAGE);
            EventHandler.berthContextMenu.actionInProgress = false;
            EventHandler.tempOpaqueBerth = null;
            currentBerth.setOpaque(false);
            return;
        }

        List<String> berths = new ArrayList<>();
        List<Long> sortedKeys = new ArrayList<>(berthHistory.keySet());
        Collections.sort(sortedKeys, Collections.reverseOrder());

        for (Long sortedKey : sortedKeys)
        {
            Berth berth = berthHistory.get(sortedKey);

            String currentBerthState = " (" + (berth.hasTrain() ? berth.getTrain() == this ? "current berth" : berth.getTrain().getHeadcode() + (berth == currentBerth && isCancelled ? " (last berth)" : "") : "no train") + ")";
            berths.add(EastAngliaSignallingMap.sdf.format(new Date(sortedKey)) + ": " + berth.getBerthDescription() + currentBerthState);
        }

        new ListDialog(currentBerth, "Berth History", "Berths this train (" + getHeadcode() + ") has passed through", berths);

        EventHandler.berthContextMenu.actionInProgress = false;
    }

    @Override
    public String toString()
    {
        return "eastangliamap.Train[headcode=" + headcode + ",uuid=" + UUID + "]";
    }*/
}