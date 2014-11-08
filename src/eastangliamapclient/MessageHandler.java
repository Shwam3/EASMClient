package eastangliamapclient;

import eastangliamapclient.gui.ListDialog;
import eastangliamapclient.gui.SysTrayHandler;
import java.awt.TrayIcon;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;

public class MessageHandler implements Runnable
{
    private static int errors = 0;
    private boolean stop = false;

    private long  lastMessageTime    = System.currentTimeMillis();
    private long  timeoutTime        = 30000;
    private final Timer timeoutTimer = new Timer("timeoutTimer");

    public MessageHandler()
    {
        Thread thread = new Thread(this, "messageHandler");
        thread.start();
    }

    @Override
    public void run()
    {
        startTimeoutTimer();

        while (!stop)
        {
            try
            {
                Object obj = new ObjectInputStream(EastAngliaMapClient.serverSocket.getInputStream()).readObject();
                if (obj instanceof HashMap)
                {
                    lastMessageTime = System.currentTimeMillis();

                    final HashMap<String, Object> message = (HashMap<String, Object>) obj;

                    switch (MessageType.getType((int) message.get("type")))
                    {
                        case SOCKET_CLOSE:
                            //JOptionPane.showMessageDialog(null, "Connection to the host has been closed.", "Connection closed by host", JOptionPane.WARNING_MESSAGE);
                            EastAngliaMapClient.connected = false;
                            SysTrayHandler.trayTooltip("Disconnected (Closed" + (message.get("reason") != null ? ", " + message.get("reason") : ")"));
                            SysTrayHandler.popup("Connection closed by host" + (message.get("reason") != null ? "\n" + message.get("reason") : ""), TrayIcon.MessageType.WARNING);
                            printMsgHandler("Connection to host closed (" + String.valueOf(message.get("reason")) + ")", false);
                            break;

                        case HEARTBEAT_REQUEST:
                            printMsgHandler("Sent heartbeat reply", false);
                            sendHeartbeatReply();
                            break;

                        case HEARTBEAT_REPLY:
                            printMsgHandler("Received heartbeat reply", false);
                            break;

                        case SEND_ALL:
                            HashMap<String, String> fullMap = (HashMap<String, String>) message.get("message");
                            EastAngliaMapClient.CClassMap.putAll(fullMap);
                            printMsgHandler("Received full map (" + fullMap.size() + ")", false);

                            if (EastAngliaMapClient.SignalMap != null)
                                EastAngliaMapClient.SignalMap.readFromMap(fullMap);
                            break;

                        case SEND_UPDATE:
                            HashMap<String, String> updateMap = (HashMap<String, String>) message.get("message");
                            printMsgHandler("Received update map (" + updateMap.size() + ")", false);
                            EastAngliaMapClient.CClassMap.putAll(updateMap);

                            if (EastAngliaMapClient.SignalMap != null)
                                EastAngliaMapClient.SignalMap.readFromMap(updateMap);
                            break;

                        case SEND_HIST_TRAIN:
                            if (message.get("history") == null)
                            {
                                EventHandler.getRidOfBerth();
                                printMsgHandler("Received no history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.SignalMap.frame, "No history for train \"" + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : "") + "\"", "Train's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                new Thread("trainHist" + message.get("berth_id")) { @Override public void run() {
                                        new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Train's history", "Berths train '" + message.get("headcode") + "' passed through", (ArrayList<String>) message.get("history"));
                                    }}.start();
                            }
                            break;

                        case SEND_HIST_BERTH:
                            if (message.get("history") == null)
                            {
                                EventHandler.getRidOfBerth();
                                printMsgHandler("Received no history for berth " + message.get("berth_descr"), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.SignalMap.frame, "No history for berth \"" + message.get("berth_descr") + "\"", "Berth's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for berth " + message.get("berth_descr"), false);
                                new Thread("trainHist" + message.get("berth_id"))  { @Override public void run() {
                                        new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Berth's history", "Trains passed through berth '" + message.get("berth_descr") + "'", (ArrayList<String>) message.get("history"));
                                    }}.start();
                            }
                            break;

                        case SEND_MESSAGE:
                            JOptionPane.showMessageDialog(EastAngliaMapClient.SignalMap.frame, message.get("message"), "Message from host", JOptionPane.PLAIN_MESSAGE);
                            break;

                        default:
                            printMsgHandler("Undefined Message Type: " + message.get("type"), true);
                            break;
                    }
                }
                errors = 0;
            }
            catch (NullPointerException | ClassNotFoundException e) {}
            catch (IOException e)
            {
                errors++;
                testErrors();
            }
        }
    }

    public void stop()
    {
        timeoutTimer.cancel();
        stop = true;

        sendSocketClose();
        closeSocket();

        printMsgHandler("Handler & Socket closed", false);
    }

    //<editor-fold defaultstate="collapsed" desc="SOCKET_CLOSE">
    public void sendSocketClose()
    {
        if (EastAngliaMapClient.serverSocket != null)
        {
            HashMap<String, Object> message = new HashMap<>();

            message.put("type", MessageType.SOCKET_CLOSE.getValue());

            try
            {
                new ObjectOutputStream(EastAngliaMapClient.serverSocket.getOutputStream()).writeObject(message);
                errors = 0;
            }
            catch (IOException e) {}
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REQUEST">
    public boolean sendHeartbeatRequest()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.HEARTBEAT_REQUEST.getValue());

        return sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REPLY">
    public boolean sendHeartbeatReply()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.HEARTBEAT_REPLY.getValue());

        return sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_ALL">
    public boolean requestAll()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.REQUEST_ALL.getValue());

        return sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_TRAIN">
    public boolean requestHistoryOfTrain(String id, String... headcode)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type",     MessageType.REQUEST_HIST_TRAIN.getValue());
        message.put("id",       id);
        message.put("headcode", headcode.length == 1 ? headcode[0] : "in berth " + id);

        return sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_BERTH">
    public boolean requestHistoryOfBerth(String berthId)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.REQUEST_HIST_BERTH.getValue());
        message.put("berth_id", berthId);

        return sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SEND_NAME">
    public boolean sendName(String name)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type",  MessageType.SET_NAME.getValue());
        message.put("name",  name + " (" + (EastAngliaMapClient.screencap ? "sc " : "") + "v" + EastAngliaMapClient.VERSION + ")");
        message.put("props", System.getProperties()); // Felt like it

        return sendMessage(message);
    }
    //</editor-fold>

    private boolean sendMessage(Object message)
    {
        if (EastAngliaMapClient.serverSocket != null)
        {
            try
            {
                new ObjectOutputStream(EastAngliaMapClient.serverSocket.getOutputStream()).writeObject(message);
                errors = 0;
                return true;
            }
            catch (IOException e)
            {
                errors++;
                testErrors();
                return false;
            }
        }
        return false;
    }

    private void testErrors()
    {
        if (errors >= 3)
        {
            sendSocketClose();
            closeSocket();
        }
        else if (errors >= 2)
        {
            EastAngliaMapClient.reconnect();
        }
    }

    private void startTimeoutTimer()
    {
        timeoutTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (System.currentTimeMillis() - lastMessageTime >= timeoutTime)
                {
                    if (timeoutTime == 30000)
                    {
                        timeoutTime = 60000;
                        sendHeartbeatRequest();
                    }
                    else
                    {
                        printMsgHandler("Connection timed out", true);

                        if (System.currentTimeMillis() - lastMessageTime < 120000)
                            SysTrayHandler.popup("Disconnected from host\nTimed out", TrayIcon.MessageType.ERROR);
                        SysTrayHandler.trayTooltip("Disconnected (Timed out)");

                        sendSocketClose();
                        closeSocket();

                        EastAngliaMapClient.reconnect();
                        timeoutTime = 30000;
                    }
                }
                else
                    timeoutTime = 30000;
            }
        }, 30000, 30000);
    }

    public void closeSocket()
    {
        try { EastAngliaMapClient.serverSocket.close(); }
        catch (IOException e) {}

        try { EastAngliaMapClient.serverSocket.getInputStream().close(); }
        catch (IOException e) {}

        try { EastAngliaMapClient.serverSocket.getOutputStream().close(); }
        catch (IOException e) {}
    }

    private void printMsgHandler(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Handler] " + message);
        else
            EastAngliaMapClient.printOut("[Handler] " + message);
    }
}