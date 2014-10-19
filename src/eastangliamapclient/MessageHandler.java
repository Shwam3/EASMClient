package eastangliamapclient;

import eastangliamapclient.gui.ListDialog;
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
                Object obj = new ObjectInputStream(EastAngliaMapClient.in).readObject();

                if (obj instanceof HashMap)
                {
                    lastMessageTime = System.currentTimeMillis();

                    HashMap<String, Object> message = (HashMap<String, Object>) obj;
                    MessageType type = MessageType.getType((int) message.get("type"));

                    switch (type)
                    {
                        case SOCKET_CLOSE:
                            JOptionPane.showMessageDialog(null, "Connection to the host has closed/lost.\nYou will need to relaunch the application to reconnect.", "Connection closed by host", JOptionPane.PLAIN_MESSAGE);
                            printMsgHandler("Connection to host ended", false);
                            System.exit(0);
                            break;

                        case HEARTBEAT_REQUEST:
                            printMsgHandler("Sent heartbeat reply", false);
                            sendHeartbeatReply();
                            break;

                        case SEND_ALL:
                            printMsgHandler("Received full map", false);
                            EastAngliaMapClient.CClassMap.putAll((HashMap) message.get("message"));

                            if (EastAngliaMapClient.SignalMap != null)
                                EastAngliaMapClient.SignalMap.readFromMap();
                            break;

                        case SEND_UPDATE:
                            printMsgHandler("Received update map", false);
                            Map updateMap = (Map) message.get("message");
                            EastAngliaMapClient.CClassMap.putAll(updateMap);

                            if (EastAngliaMapClient.SignalMap != null)
                                EastAngliaMapClient.SignalMap.readFromMap(updateMap);
                            break;

                        case SEND_HIST_TRAIN:
                            printMsgHandler("Received history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                            new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Train's history", "Berths train '" + message.get("headcode") + "' passed through", (List<String>) message.get("history"));
                            break;

                        case SEND_HIST_BERTH:
                            printMsgHandler("Received history for berth " + message.get("berth_descr"), false);
                            new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Berth's history", "Trains passed through berth '" + message.get("berth_descr") + "'", (List<String>) message.get("history"));
                            break;

                        default:
                            printMsgHandler("Undefined Message Type: " + type.getName(), true);
                            break;
                    }
                }
                errors = 0;
            }
            catch (EOFException e) { errors++; }
            catch (IOException | ClassNotFoundException e) {}
        }
    }

    public void stop()
    {
        stop = true;
    }

    //<editor-fold defaultstate="collapsed" desc="SOCKET_CLOSE">
    public void sendSocketClose()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.SOCKET_CLOSE.getValue());

        try { new ObjectOutputStream(EastAngliaMapClient.out).writeObject(message); errors = 0; }
        catch (IOException e) {}
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REQUEST">
    public void sendHeartbeatRequest()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.HEARTBEAT_REQUEST.getValue());

        sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REPLY">
    public void sendHeartbeatReply()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.HEARTBEAT_REPLY.getValue());

        sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_ALL">
    public void requestAll()
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.REQUEST_ALL.getValue());

        sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_TRAIN">
    public void requestHistoryOfTrain(String id, String... headcode)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type",     MessageType.REQUEST_HIST_TRAIN.getValue());
        message.put("id",       id);
        message.put("headcode", headcode.length == 1 ? headcode[0] : "in berth " + id);

        sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_BERTH">
    public void requestHistoryOfBerth(String berthId)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.REQUEST_HIST_BERTH.getValue());
        message.put("berth_id", berthId);

        sendMessage(message);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SEND_NAME">
    public void sendName(String name)
    {
        HashMap<String, Object> message = new HashMap<>();

        message.put("type", MessageType.SET_NAME.getValue());
        message.put("name", name + " (" + (EastAngliaMapClient.screencap ? "sc " : "") + "v" + EastAngliaMapClient.VERSION + ")");

        sendMessage(message);
    }
    //</editor-fold>

    private void sendMessage(Object message)
    {
        try { new ObjectOutputStream(EastAngliaMapClient.out).writeObject(message); errors = 0; }
        catch (IOException e) { errors++; }

        testErrors();
    }

    private void testErrors()
    {
        if (errors > 3)
        {
            sendSocketClose();
            closeSocket();
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
        if (!stop)
        {
            try { EastAngliaMapClient.serverSocket.close(); }
            catch (IOException e) {}

            try { EastAngliaMapClient.serverSocket.getInputStream().close(); }
            catch (IOException e) {}

            try { EastAngliaMapClient.serverSocket.getOutputStream().close(); }
            catch (IOException e) {}

            stop = true;

            printMsgHandler("Connection closed", false);
        }
    }

    private void printMsgHandler(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Handler] " + message);
        else
            EastAngliaMapClient.printOut("[Handler] " + message);
    }
}