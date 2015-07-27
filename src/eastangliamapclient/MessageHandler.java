package eastangliamapclient;

import eastangliamapclient.gui.ListDialog;
import eastangliamapclient.gui.SysTrayHandler;
import eastangliamapclient.gui.mapelements.Berths;
import eastangliamapclient.json.JSONParser;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StreamCorruptedException;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

public class MessageHandler
{
    private static int     errors = 0;
    private static boolean stop = true;

    private static long  lastMessageTime      = System.currentTimeMillis();
    private static long  timeoutTime          = 30000;
    private static final Timer timeoutTimer   = new Timer("timeoutTimer");
    private static       Thread messageHandler;
    private static       boolean ready        = false;
    private static       BufferedReader in;
    private static       BufferedWriter out;

    private static void run()
    {
        startTimeoutTimer();

        if (EastAngliaMapClient.serverSocket == null)
        {
            printMsgHandler("Cannot start Message Handler", true);
            return;
        }

        try { in = new BufferedReader(new InputStreamReader(EastAngliaMapClient.serverSocket.getInputStream())); }
        catch (IOException e) { EastAngliaMapClient.printThrowable(e, "MessageHandler"); }
        try { out = new BufferedWriter(new OutputStreamWriter(EastAngliaMapClient.serverSocket.getOutputStream())); }
        catch (IOException e) { EastAngliaMapClient.printThrowable(e, "MessageHandler"); }

        while (!stop && !messageHandler.isInterrupted() && (EastAngliaMapClient.serverSocket != null && !EastAngliaMapClient.serverSocket.isClosed()))
        {
            try
            {
                String jsonMessage = in.readLine();

                if (jsonMessage != null && !jsonMessage.isEmpty())
                {
                    Map<String, Object> message = (Map<String, Object>) ((Map<String, Object>) JSONParser.parseJSON(jsonMessage)).get("Message");

                    lastMessageTime = System.currentTimeMillis();

                    switch (MessageType.getType(String.valueOf(message.get("type"))))
                    {
                        case SOCKET_CLOSE:
                            //JOptionPane.showMessageDialog(null, "Connection to the host has been closed.", "Connection closed by host", JOptionPane.WARNING_MESSAGE);
                            EastAngliaMapClient.connected = false;
                            SysTrayHandler.trayTooltip("Disconnected (Closed" + (message.get("reason") != null ? ", " + message.get("reason") : "") + ")");
                            SysTrayHandler.popup("Connection closed by host" + (message.get("reason") != null ? "\n" + message.get("reason") : ""), TrayIcon.MessageType.WARNING);
                            printMsgHandler("Connection to host closed (" + String.valueOf(message.get("reason")) + ")", false);

                            if (String.valueOf(message.get("reason")).startsWith("You have been kicked"))
                                EastAngliaMapClient.kicked = true;
                            break;

                        case HEARTBEAT_REQUEST:
                            printMsgHandler("Sent heartbeat reply", false);
                            sendHeartbeatReply();
                            break;

                        case HEARTBEAT_REPLY:
                            printMsgHandler("Received heartbeat reply", false);
                            break;

                        case SEND_ALL:
                            Map<String, String> fullMap = (Map<String, String>) message.get("message");
                            EastAngliaMapClient.DataMap.putAll(fullMap);
                            printMsgHandler("Received full map (" + fullMap.size() + ")", false);

                            if (EastAngliaMapClient.frameSignalMap != null)
                                EastAngliaMapClient.frameSignalMap.readFromMap(EastAngliaMapClient.DataMap);

                            ready = true;
                            break;

                        case SEND_UPDATE:
                            Map<String, String> updateMap = (Map<String, String>) message.get("message");
                            printMsgHandler("Received update map (" + updateMap.size() + ")", false);
                            EastAngliaMapClient.DataMap.putAll(updateMap);

                            if (EastAngliaMapClient.frameSignalMap != null)
                                EastAngliaMapClient.frameSignalMap.readFromMap(updateMap);
                            break;

                        case SEND_HIST_TRAIN:
                            if (message.get("history") == null || message.get("headcode").equals(""))
                            {
                                Berths.setOpaqueBerth(null);

                                printMsgHandler("Received no history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "No history for train \"" + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : "") + "\"", "Train's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                new Thread(
                                    () -> new ListDialog(
                                            Berths.getBerth((String) message.get("berth_id")),
                                            "Train's history",
                                            "Berths train '" + message.get("headcode") + "' passed through",
                                            (List<String>) message.get("history")),
                                    "trainHist" + message.get("berth_id")
                                ).start();
                            }
                            break;

                        case SEND_HIST_BERTH:
                            if (message.get("history") == null)
                            {
                                Berths.setOpaqueBerth(null);

                                printMsgHandler("Received no history for berth " + message.get("berth_descr"), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "No history for berth \"" + message.get("berth_descr") + "\"", "Berth's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for berth " + message.get("berth_descr"), false);
                                new Thread(
                                    () -> new ListDialog(
                                        Berths.getBerth((String) message.get("berth_id")),
                                        "Berth's history",
                                        "Trains passed through berth '" + message.get("berth_descr") + "'",
                                        (List<String>) message.get("history")),
                                    "trainHist" + message.get("berth_id")
                                ).start();
                            }
                            break;

                        case SEND_MESSAGE:
                            JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, message.get("message"), "Message from host", JOptionPane.PLAIN_MESSAGE);
                            break;

                        default:
                            printMsgHandler("Undefined Message Type: " + message.get("type"), true);
                            break;
                    }
                }

                EastAngliaMapClient.frameDataViewer.updateData();
            }
            catch (EOFException | StreamCorruptedException | NullPointerException e) { errors++; }
            catch (SocketException e)
            {
                if (!EastAngliaMapClient.serverSocket.isClosed() && !messageHandler.isInterrupted())
                {
                    errors++;
                    EastAngliaMapClient.printThrowable(e, "Handler");
                    testErrors();
                }
            }
            catch (IOException e)
            {
                if (!messageHandler.isInterrupted())
                {
                    errors++;
                    EastAngliaMapClient.printThrowable(e, "Handler");
                    testErrors();
                }
            }

            //<editor-fold defaultstate="collapsed" desc="Old protcol">
            /*try
            {
                Object obj = new ObjectInputStream(new BufferedInputStream(EastAngliaMapClient.serverSocket.getInputStream())).readObject();

                if (obj instanceof Map)
                {
                    lastMessageTime = System.currentTimeMillis();

                    final Map<String, Object> message = new HashMap<>((Map<String, Object>) obj);

                    switch (MessageType.getType((int) message.get("type")))
                    {
                        case SOCKET_CLOSE:
                            //JOptionPane.showMessageDialog(null, "Connection to the host has been closed.", "Connection closed by host", JOptionPane.WARNING_MESSAGE);
                            EastAngliaMapClient.connected = false;
                            SysTrayHandler.trayTooltip("Disconnected (Closed" + (message.get("reason") != null ? ", " + message.get("reason") : "") + ")");
                            SysTrayHandler.popup("Connection closed by host" + (message.get("reason") != null ? "\n" + message.get("reason") : ""), TrayIcon.MessageType.WARNING);
                            printMsgHandler("Connection to host closed (" + String.valueOf(message.get("reason")) + ")", false);

                            if (String.valueOf(message.get("reason")).startsWith("You have been kicked"))
                                EastAngliaMapClient.kicked = true;
                            break;

                        case HEARTBEAT_REQUEST:
                            printMsgHandler("Sent heartbeat reply", false);
                            sendHeartbeatReply();
                            break;

                        case HEARTBEAT_REPLY:
                            printMsgHandler("Received heartbeat reply", false);
                            break;

                        case SEND_ALL:
                            Map<String, String> fullMap = (Map<String, String>) message.get("message");
                            EastAngliaMapClient.DataMap.putAll(fullMap);
                            printMsgHandler("Received full map (" + fullMap.size() + ")", false);

                            if (EastAngliaMapClient.frameSignalMap != null)
                                EastAngliaMapClient.frameSignalMap.readFromMap(EastAngliaMapClient.DataMap);

                            ready = true;
                            break;

                        case SEND_UPDATE:
                            Map<String, String> updateMap = (Map<String, String>) message.get("message");
                            printMsgHandler("Received update map (" + updateMap.size() + ")", false);
                            EastAngliaMapClient.DataMap.putAll(updateMap);

                            if (EastAngliaMapClient.frameSignalMap != null)
                                EastAngliaMapClient.frameSignalMap.readFromMap(updateMap);
                            break;

                        case SEND_HIST_TRAIN:
                            if (message.get("history") == null || message.get("headcode").equals(""))
                            {
                                Berths.setOpaqueBerth(null);

                                printMsgHandler("Received no history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "No history for train \"" + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : "") + "\"", "Train's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for train " + message.get("headcode") + (message.get("berth_id") != null ? " in berth " + message.get("berth_id") : ""), false);
                                new Thread("trainHist" + message.get("berth_id")) { @Override public void run() {
                                        new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Train's history", "Berths train '" + message.get("headcode") + "' passed through", (List<String>) message.get("history"));
                                    }}.start();
                            }
                            break;

                        case SEND_HIST_BERTH:
                            if (message.get("history") == null)
                            {
                                Berths.setOpaqueBerth(null);

                                printMsgHandler("Received no history for berth " + message.get("berth_descr"), false);
                                JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, "No history for berth \"" + message.get("berth_descr") + "\"", "Berth's history", JOptionPane.WARNING_MESSAGE);
                            }
                            else
                            {
                                printMsgHandler("Received history for berth " + message.get("berth_descr"), false);
                                new Thread("trainHist" + message.get("berth_id")) { @Override public void run() {
                                        new ListDialog(Berths.getBerth((String) message.get("berth_id")), "Berth's history", "Trains passed through berth '" + message.get("berth_descr") + "'", (List<String>) message.get("history"));
                                    }}.start();
                            }
                            break;

                        case SEND_MESSAGE:
                            JOptionPane.showMessageDialog(EastAngliaMapClient.frameSignalMap.frame, message.get("message"), "Message from host", JOptionPane.PLAIN_MESSAGE);
                            break;

                        default:
                            printMsgHandler("Undefined Message Type: " + message.get("type"), true);
                            break;
                    }
                }
                else
                    EastAngliaMapClient.printErr(String.valueOf(obj));

                errors = 0;
            }
            catch (NullPointerException | ClassNotFoundException | ClassCastException e) {}
            catch (EOFException | StreamCorruptedException e) { errors++; }
            catch (SocketException e)
            {
                if (!EastAngliaMapClient.serverSocket.isClosed() && !messageHandler.isInterrupted())
                {
                    errors++;
                    EastAngliaMapClient.printThrowable(e, "Handler");
                    testErrors();
                }
            }
            catch (IOException e)
            {
                if (!messageHandler.isInterrupted())
                {
                    errors++;
                    EastAngliaMapClient.printThrowable(e, "Handler");
                    testErrors();
                }
            }*/
            //</editor-fold>
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Start/Stop methods">
    public static void start()
    {
        if (stop)
        {
            errors = 0;
            stop = false;

            messageHandler = new Thread(() -> MessageHandler.run(), "MessageHandler");
            messageHandler.start();
            printMsgHandler("Started", false);

            try { Thread.sleep(500); }
            catch(InterruptedException e) {}

            if (!isReady())
                MessageHandler.requestAll();
        }
    }

    public static void stop()
    {
        if (!stop)
        {
            sendSocketClose();

            EastAngliaMapClient.connected = false;
            stop = true;
            ready = false;

            if (EastAngliaMapClient.serverSocket != null && !EastAngliaMapClient.serverSocket.isClosed())
            {
                try { EastAngliaMapClient.serverSocket.close(); }
                catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Handler"); }
            }

            messageHandler.interrupt();

            printMsgHandler("Handler stopped & Socket closed", false);
        }
    }

    private static void sendSocketClose()
    {
        if (EastAngliaMapClient.serverSocket != null && EastAngliaMapClient.connected)
        {
            try
            {
                out.write("{\"Message\":{\"type\":\"" + MessageType.SOCKET_CLOSE.getName() + "\"}}");
                out.write("\r\n");
                out.flush();

                errors = 0;
            }
            catch (IOException e) {}

            /*Map<String, Object> message = new HashMap<>();
            message.put("type", MessageType.SOCKET_CLOSE.getValue());

            try
            {
                new ObjectOutputStream(EastAngliaMapClient.serverSocket.getOutputStream()).writeObject(message);
                errors = 0;
            }
            catch (IOException e) {}*/
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REQUEST">
    public static boolean sendHeartbeatRequest()
    {
        /*Map<String, Object> message = new HashMap<>();
        message.put("type", MessageType.HEARTBEAT_REQUEST.getValue());*/

        return sendMessage("{\"Message\":{\"type\":\"" + MessageType.HEARTBEAT_REQUEST.getName() + "\"}}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="HEARTBEAT_REPLY">
    public static boolean sendHeartbeatReply()
    {
        /*Map<String, Object> message = new HashMap<>();
        message.put("type", MessageType.HEARTBEAT_REPLY.getValue());*/

        return sendMessage("{\"Message\":{\"type\":\"" + MessageType.HEARTBEAT_REPLY.getName() + "\"}}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_ALL">
    public static boolean requestAll()
    {
        /*Map<String, Object> message = new HashMap<>();
        message.put("type", MessageType.REQUEST_ALL.getValue());*/

        return sendMessage("{\"Message\":{\"type\":\"" + MessageType.REQUEST_ALL.getName() + "\"}}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_TRAIN">
    public static boolean requestHistoryOfTrain(String id)
    {
        /*Map<String, Object> message = new HashMap<>();
        message.put("type", MessageType.REQUEST_HIST_TRAIN.getValue());
        message.put("id",   id);*/

        return sendMessage("{\"Message\":{\"type\":\"" + MessageType.REQUEST_HIST_TRAIN.getName() + "\",\"id\":\"" + id + "\"}}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="REQUEST_HIST_BERTH">
    public static boolean requestHistoryOfBerth(String berthId)
    {
        /*Map<String, Object> message = new HashMap<>();
        message.put("type", MessageType.REQUEST_HIST_BERTH.getValue());
        message.put("berth_id", berthId);*/

        return sendMessage("{\"Message\":{\"type\":\"" + MessageType.REQUEST_HIST_BERTH.getName() + "\",\"berth_id\":\"" + berthId + "\"}}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="SEND_NAME">
    public static boolean sendName(String name)
    {
        /*Map<String, Object> message = new HashMap<>();

        message.put("type",  MessageType.SET_NAME.getValue());
        message.put("name",  name + " (v" + EastAngliaMapClient.CLIENT_VERSION + ")");
        message.put("props", System.getProperties());*/

        StringBuilder sb = new StringBuilder("{\"Message\":{");
        sb.append("\"type\":\"").append(MessageType.SET_NAME.getName()).append("\",");
        sb.append("\"name\":\"").append(name).append(" (v").append(EastAngliaMapClient.CLIENT_VERSION).append(" / v").append(EastAngliaMapClient.DATA_VERSION).append(")\",");
        sb.append("\"props\":{")
                .append("\"java.version\":\"")    .append(System.getProperty("java.version", ""))    .append("\",")
                .append("\"user.name\":\"")       .append(System.getProperty("user.name", ""))       .append("\",")
                .append("\"user.language\":\"")   .append(System.getProperty("user.language", ""))   .append("\",")
                .append("\"user.country\":\"")    .append(System.getProperty("user.country", ""))    .append("\",")
                .append("\"user.timezone\":\"")   .append(System.getProperty("user.timezone", ""))   .append("\",")
                .append("\"os.name\":\"")         .append(System.getProperty("os.name", ""))         .append("\",")
                .append("\"sun.java.command\":\"").append(System.getProperty("sun.java.command", "")).append("\"")
                .append("}");
        sb.append("}}");
        return sendMessage(sb.toString());
    }
    //</editor-fold>

    private static boolean sendMessage(String message)
    {
        if (EastAngliaMapClient.serverSocket != null)
        {
            if (stop)
                start();

            try
            {
                out.write(message);
                out.write("\r\n");
                out.flush();

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

    private static void testErrors()
    {
        if (errors >= 10)
            stop();
        else if (errors >= 2)
            EastAngliaMapClient.reconnect(false);
    }

    private static void startTimeoutTimer()
    {
        timeoutTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                long time = System.currentTimeMillis() - lastMessageTime;
                if (time >= timeoutTime)
                {
                    if (timeoutTime == 30000)
                    {
                        timeoutTime = 60000;
                        sendHeartbeatRequest();
                    }
                    else
                    {
                        EastAngliaMapClient.connected = false;

                        if (!EastAngliaMapClient.kicked)
                        {
                            printMsgHandler("Connection timed out (" + time / 1000L + "s)", true);

                            if (time < 120000)
                                SysTrayHandler.popup("Disconnected from host\nTimed out", TrayIcon.MessageType.ERROR);

                            SysTrayHandler.trayTooltip("Disconnected (Timed out " + time / 1000L + "s)");

                            EastAngliaMapClient.reconnect(false);
                        }
                        timeoutTime = 30000;
                    }
                }
                else
                    timeoutTime = 30000;
            }
        }, 30000, 30000);
    }

    private static void printMsgHandler(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Handler] " + message);
        else
            EastAngliaMapClient.printOut("[Handler] " + message);
    }

    public static long getLastMessageTime()
    {
        return lastMessageTime;
    }

    public static void waitForFullMap() { ready = false; }
    public static boolean isReady() { return ready; }
}