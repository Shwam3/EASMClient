package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.awt.*;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class EastAngliaMapClient
{
    public static int VERSION = 6;
    //<editor-fold defaultstate="collapsed" desc="Program variables">
    private static final String host = "shwam3.ddns.net";
    private static final int    port = 6321;

    public  static Socket       serverSocket;
    public  static InputStream  in;
    public  static OutputStream out;

    public  static HashMap<String, String> CClassMap = new HashMap<>();

    public  static boolean logToFile        = true;
    public  static boolean opaque           = false;
    public  static boolean showDescriptions = false; // not headcodes
    public  static boolean visible          = true;

    public  static SignalMap      SignalMap;
    public  static MessageHandler handler;

    public  static SimpleDateFormat sdf     = new SimpleDateFormat("HH:mm:ss");
    public  static SimpleDateFormat sdfLog  = new SimpleDateFormat("dd-MM-YY HH.mm.ss");
    private static final Object     logLock = new Object();
            static final Desktop    desktop = Desktop.getDesktop();

    public  static       Font  TD_FONT = new Font("TDBerth DM", 0, 16);
    public  static final Color GREEN   = new Color(0,  153, 0);
    public  static final Color GREY    = new Color(64, 64,  64);
    public  static final Color BLACK   = Color.BLACK;
    public  static final Color WHITE   = Color.WHITE;
    public  static final Color RED     = new Color(190, 20, 20);
    public  static final Color BLUE    = new Color(0, 255, 255);

    public  static String launchTime      = getTime();
    public  static long   lastMessageTime = new Date().getTime();

    public  static boolean connect       = true;
    public  static boolean blockKeyInput = false;
    //</editor-fold>

    public static void main(String[] args)
    {
        //<editor-fold defaultstate="collapsed" desc="Look and feel">
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}
        //</editor-fold>

        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    TD_FONT = Font.createFont(Font.TRUETYPE_FONT, EastAngliaMapClient.class.getResourceAsStream("/eastangliamapclient/resources/TDBerth-DM.ttf")).deriveFont(16f);
                }
                catch (FontFormatException | IOException e)
                {
                    TD_FONT = Font.getFont(Font.SANS_SERIF);
                    printBootstrap("Couldn\'t create font, stuff will look strange", true);
                    e.printStackTrace(System.out);
                }

                SignalMap = new SignalMap();

                try
                {
                    serverSocket = new Socket(host, port); // Throws the errors
                    printBootstrap("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);

                    in  = serverSocket.getInputStream();
                    out = serverSocket.getOutputStream();

                    handler = new MessageHandler();
                    handler.sendName(System.getProperty("user.name"));

                    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                handler.sendSocketClose();
                                handler.closeSocket();
                            }
                            catch (NullPointerException e) {}
                        }
                    }, "shutdownHook"));
                }
                catch (ConnectException e)
                {
                    printBootstrap("Couldnt connect, server probably down.\n" + e, true);
                    JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (ConnEx)", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException e)
                {
                    printBootstrap("Unable to connect to server\n" + e, true);
                    JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (IOEx)", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        EventHandler.addKeyboardEvents();
        EventHandler.startTimerTasks();
    }

    public static void refresh()
    {
        try
        {
            for (Map.Entry pairs : Berths.getEntrySet())
            {
                Berth berth = (Berth) pairs.getValue();
                berth.setOpaque(false);
            }
        }
        catch (ConcurrentModificationException e) {}

        try { SignalMap.frame.repaint(); }
        catch (NullPointerException e) {}
    }

    public static void reconnect()
    {
        handler.sendSocketClose();
        handler.closeSocket();

        try
        {
            serverSocket = new Socket(host, port);
            printBootstrap("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);

            in  = serverSocket.getInputStream();
            out = serverSocket.getOutputStream();

            handler = new MessageHandler();
            handler.sendName(System.getProperty("user.name"));
        }
        catch (ConnectException e)
        {
            printBootstrap("Couldnt connect, server probably down.\n" + e, true);
            JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (ConnEx)", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e)
        {
            printBootstrap("Unable to connect to server\n" + e, true);
            JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (IOEx)", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static String getTime()
    {
        return sdf.format(new Date());
    }

    private static void printBootstrap(String message, boolean toErr)
    {
        if (toErr)
            printErr("[Bootstrap] " + message);
        else
            printOut("[Bootstrap] " + message);
    }

    public static void printOut(String message)
    {
        synchronized (logLock)
        {
            System.out.println("[" + sdf.format(new Date()) + "] " + message);
        }
    }

    public static void printErr(String message)
    {
        synchronized (logLock)
        {
            System.err.println("!!!> [" + sdf.format(new Date()) + "] " + message + " <!!!");
        }
    }
}