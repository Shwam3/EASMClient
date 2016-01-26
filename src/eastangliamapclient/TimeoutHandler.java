package eastangliamapclient;

import eastangliamapclient.gui.SysTrayHandler;
import java.awt.TrayIcon;
import java.util.Timer;
import java.util.TimerTask;

public class TimeoutHandler
{
    public  static long    lastMessageTime = System.currentTimeMillis();
    private static long    timeoutTime     = 30000;
    private static Timer   timeoutTimer    = null;
    public  static boolean ready           = false;

    public static void run()
    {
        if (timeoutTimer == null)
        {
            timeoutTimer = new Timer("timeoutTimer");
            timeoutTimer.scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    long time = System.currentTimeMillis() - lastMessageTime;
                    if (time >= timeoutTime && !EastAngliaMapClient.requireManualConnect)
                    {
                        EastAngliaMapClient.connected = false;
                        EastAngliaMapClient.disconnectReason = EastAngliaMapClient.disconnectReason == null || EastAngliaMapClient.disconnectReason.startsWith("Connection timed out ") ? "Connection timed out (" + time / 1000L + "s)" : EastAngliaMapClient.disconnectReason;

                        if (!EastAngliaMapClient.requireManualConnect)
                        {
                            EastAngliaMapClient.printErr("[Timeout] Connection timed out (" + time / 1000L + "s)");

                            if (time <= 600000)
                                SysTrayHandler.popup("Disconnected from host" + (EastAngliaMapClient.disconnectReason == null ? "" : "\n" + EastAngliaMapClient.disconnectReason), TrayIcon.MessageType.ERROR);

                            SysTrayHandler.updateTrayTooltip();

                            EastAngliaMapClient.reconnect(false);
                        }
                    }
                }
            }, 30000, 30000);
        }
    }

    public static long getLastMessageTime()
    {
        return lastMessageTime;
    }

    public static void waitForFullMap() { ready = false; }
    public static boolean isReady() { return ready; }
}