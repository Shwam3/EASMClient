package eastangliamapclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SignalMapReplayController
{
    public static final int MODE_STOP          = -10;
    public static final int MODE_PLAY_BACKWARD = -1;
    public static final int MODE_PAUSE         = 0;
    public static final int MODE_PLAY_FORWARD  = 1;

    private static int     playMode = MODE_STOP;
    private static int     lastMode = MODE_PLAY_FORWARD;
    private static int     speedModifier = 1;
    private static long    timeOffset = 0;
    private static long    requestedOffset = -1;
    private static boolean isActive = false;
    private static ScheduledFuture replayExecutor;

    private static List<ReplayEvent> replayData = null;
    private static Map<String, String> replayInit = null;

    public static void initReplayForDate(Date date)
    {
        replayData = null;
        replayInit = null;
        isActive = false;

        String dateString = new SimpleDateFormat("dd-MM-yy").format(date);
        File replayInitFile = new File(EastAngliaMapClient.storageDir, "Logs" + File.separator + "ReplaySaves" + File.separator + dateString + ".json");
        if (!replayInitFile.exists())
        {
            printReplay("Date doesnt exist for Init file (not required)", true);
        }

        File replayDataFile = new File(EastAngliaMapClient.storageDir, "Logs" + File.separator + "TD" + File.separator + dateString + ".log");
        if (!replayDataFile.exists())
        {
            printReplay("Date doesnt exist for Data file (required)", true);
            return;
        }

        EastAngliaMapClient.disconnectReason = "Using replay function";
        EastAngliaMapClient.requireManualConnect = true;

        EastAngliaMapClient.DataMap.clear();

        dateString = new SimpleDateFormat("dd/MM/yy").format(date);
        replayInit = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(replayInitFile)))
        {
            br.readLine();

            String line;
            while ((line = br.readLine()) != null)
            {
                if (line.length() < 12 || line.length() > 16)
                    continue;

                replayInit.put(line.substring(1, 7), line.substring(10, line.length() - 2));
            }
        }
        catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Replay"); }

        replayData = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(replayDataFile)))
        {
            String line;
            while ((line = br.readLine()) != null)
            {
                try { replayData.add(new ReplayEvent(line, dateString)); }
                catch(IllegalArgumentException e) {}
            }

            replayData.sort((ReplayEvent o1, ReplayEvent o2) ->
            {
                return o1.getEventOffset() - o2.getEventOffset();
            });
            lastMode = MODE_PLAY_FORWARD;
        }
        catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Replay"); }

        EastAngliaMapClient.frameSignalMap.updateGuiComponents(replayInit);

        isActive = true;
        playMode = MODE_PAUSE;

        if (replayExecutor != null)
            replayExecutor.cancel(true);

        replayExecutor = Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() ->
        {
            if (playMode == MODE_STOP)
            {
                replayExecutor.cancel(false);
            }
            else if (playMode == MODE_PLAY_FORWARD || playMode == MODE_PLAY_BACKWARD)
            {
                Map<String, String> replayMap = new HashMap<>();

                if (requestedOffset >= 0 && requestedOffset > timeOffset)
                {
                    if (lastMode == MODE_PLAY_BACKWARD)
                        Collections.reverse(replayData);

                    replayData.stream()
                            .filter(evt -> (evt.getEventOffset() > timeOffset && evt.getEventOffset() <= requestedOffset))
                            .map(evt -> evt.getEventData())
                            .forEachOrdered(evtData ->
                    {
                        if (evtData.startsWith("Change"))
                            replayMap.put(evtData.substring(7, 13), evtData.substring(24));
                        else if (evtData.startsWith("Interpose"))
                            replayMap.put(evtData.substring(18), evtData.substring(10, 14));
                        else if (evtData.startsWith("Cancel"))
                            replayMap.put(evtData.substring(17), "");
                        else if (evtData.startsWith("Step"))
                        {
                            replayMap.put(evtData.substring(25), evtData.substring(5, 9));
                            replayMap.put(evtData.substring(15, 21), "");
                        }
                    });

                    if (lastMode == MODE_PLAY_BACKWARD)
                        Collections.reverse(replayData);

                    timeOffset = requestedOffset;

                    EastAngliaMapClient.frameSignalMap.updateGuiComponents(replayMap);
                    replayMap.clear();
                }

                final long newTimeOffset = Math.min(86399, Math.max(0, timeOffset + (speedModifier * playMode)));
                requestedOffset = -1;

                if (newTimeOffset > timeOffset)
                {
                    replayData.stream()
                            .filter(evt -> (evt.getEventOffset() > timeOffset && evt.getEventOffset() <= newTimeOffset))
                            .map(evt -> evt.getEventData())
                            .forEachOrdered(evtData ->
                    {
                        if (evtData.startsWith("Change"))
                            replayMap.put(evtData.substring(7, 13), evtData.substring(24));
                        else if (evtData.startsWith("Interpose"))
                            replayMap.put(evtData.substring(18), evtData.substring(10, 14));
                        else if (evtData.startsWith("Cancel"))
                            replayMap.put(evtData.substring(17), "");
                        else if (evtData.startsWith("Step"))
                        {
                            replayMap.put(evtData.substring(25), evtData.substring(5, 9));
                            replayMap.put(evtData.substring(15, 21), "");
                        }
                    });
                }
                else
                {
                    replayData.stream()
                            .filter(evt -> (evt.getEventOffset() <= timeOffset && evt.getEventOffset() > newTimeOffset))
                            .map(evt -> evt.getEventData())
                            .forEachOrdered((evtData) ->
                    {
                        if (evtData.startsWith("Change"))
                            replayMap.put(evtData.substring(7, 13), evtData.substring(19, 20));
                        else if (evtData.startsWith("Interpose"))
                            replayMap.put(evtData.substring(18), "");
                        else if (evtData.startsWith("Cancel"))
                            replayMap.put(evtData.substring(17), evtData.substring(7, 11));
                        else if (evtData.startsWith("Step"))
                        {
                            replayMap.put(evtData.substring(25), "");
                            replayMap.put(evtData.substring(15, 21), evtData.substring(5, 9));
                        }
                    });
                }

                if (newTimeOffset <= 0 || newTimeOffset >= 86399)
                {
                    playMode = MODE_PAUSE;
                    EastAngliaMapClient.frameReplayControls.updateMode();
                }

                EastAngliaMapClient.frameSignalMap.updateGuiComponents(replayMap);
                EastAngliaMapClient.frameDataViewer.updateData();
                timeOffset = newTimeOffset;
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void setPlayMode(int mode)
    {
        if (mode == MODE_STOP)
        {
            replayData = null;
            replayInit = null;
            timeOffset = 0;

            isActive = false;

            EastAngliaMapClient.reconnect(true);
        }
        else if ((mode == MODE_PLAY_FORWARD && lastMode == MODE_PLAY_BACKWARD) || (mode == MODE_PLAY_BACKWARD && lastMode == MODE_PLAY_FORWARD))
        {
            Collections.reverse(replayData);
            lastMode *= -1;
        }

        playMode = mode;
    }

    public static int getPlayMode()
    {
        return playMode;
    }

    public static void setPlaySpeedModifier(int modifier)
    {
        speedModifier = modifier;
    }

    public static void requestOffset(long offset)
    {
        if (offset < 86399 && offset >= -1 && offset > timeOffset)
            requestedOffset = offset;
        else
            requestedOffset = -1;

    }

    public static void printReplay(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Replay] " + message);
        else
            EastAngliaMapClient.printOut("[Replay] " + message);
    }

    public static boolean isActive()
    {
        return isActive;
    }

    public static long getTimeOffset()
    {
        return timeOffset;
    }

    static class ReplayEvent
    {
        private int     eventOffset = 0;
        private String  eventData   = "";

        public ReplayEvent(String eventString, String actualDate)
        {
            if (!actualDate.equals(eventString.substring(1, 9)))
                throw new IllegalArgumentException("Event doesnt apply to supplied date (" + eventString.substring(1, 9) + ")");

            String[] timeOffsetString = eventString.substring(10, 18).split(":");
            eventOffset = Integer.parseInt(timeOffsetString[0]) * 60 * 60;
            eventOffset += Integer.parseInt(timeOffsetString[1]) * 60;
            eventOffset += Integer.parseInt(timeOffsetString[2]);

            eventData = eventString.substring(20);
        }

        public String  getEventData()   { return eventData; }
        public int     getEventOffset() { return eventOffset; }

        @Override
        public String toString()
        {
            return "[" + eventOffset + "] " + eventData;
        }
    }
}