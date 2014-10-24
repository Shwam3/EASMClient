package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;

public class EastAngliaMapClient
{
    public static String VERSION = "10";
    //<editor-fold defaultstate="collapsed" desc="Program variables">
    private static final String host = "shwam3.ddns.net";
    private static final int    port = 6321;

    public  static Socket       serverSocket;
    public  static InputStream  in;
    public  static OutputStream out;

    public  static HashMap<String, String> CClassMap = new HashMap<>();

    public  static boolean logToFile        = true;
    public  static boolean screencap        = false;
    public  static boolean opaque           = false;
    public  static boolean showDescriptions = false; // not headcodes
    public  static boolean visible          = true;

    public  static SignalMap      SignalMap;
    public  static MessageHandler handler;
    public  static String         clientName = System.getProperty("user.name");

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

    public  static String[] args;

    private static long    lastReconnectAttempt;
    public  static boolean connect       = true;
    public  static boolean blockKeyInput = false;
    //</editor-fold>

    public static void main(String[] args)
    {
        EastAngliaMapClient.args = args;

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}

        try
        {
            URL url = new URL("https://raw.githubusercontent.com/Shwam3/EastAngliaSignalMapClient/master/version.txt");
            Scanner s = new Scanner(url.openStream());
            int remoteVersion = s.nextInt();

            printStartup("local: " + VERSION + ", remote: " + remoteVersion, false);
            if (remoteVersion > Integer.parseInt(VERSION))
            {
                printStartup("New version available", false);
                if (JOptionPane.showConfirmDialog(null, "A new version is available, download now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    Desktop.getDesktop().browse(new URI("http://easignalmap.altervista.org/EastAngliaSignalMapClient.exe"));
                    printStartup("Downloading new version", false);
                    System.exit(0);
                }
            }
        }
        catch (FileNotFoundException e) { printStartup("Cant find remote version file", true); }
        catch (URISyntaxException e) {}
        catch (IOException e) { printStartup("Error reading remote version file", true); }

        //EventHandler.startScreenCapture(60000 * 5, "C:\\Users\\Shwam\\Dropbox\\EASignalMapMobile");
        //EventHandler.startScreenCapture(60000 * 5, "C:\\Users\\Shwam\\Copy cambird@f2s.com\\EASigMapMobile");
        EventHandler.startScreenCapture(60000 * 5, "C:\\Users\\Shwam\\Documents\\GitHub\\EastAngliaSignalMapWebsite\\images");

        handler = new MessageHandler();

        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    TD_FONT = Font.createFont(0, EastAngliaMapClient.class.getResourceAsStream("/eastangliamapclient/resources/TDBerth-DM.ttf")).deriveFont(16f);
                }
                catch (FontFormatException | IOException e)
                {
                    TD_FONT = new Font("Monospaced", 0, 19);
                    printStartup("Couldn\'t create font, stuff will look strange", true);
                    e.printStackTrace(System.err);
                }

                SignalMap = new SignalMap();

                try
                {
                    lastReconnectAttempt = System.currentTimeMillis();

                    serverSocket = new Socket(host, port); // Throws the errors

                    handler.sendName(clientName);

                    if (serverSocket.isConnected())
                        printStartup("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);

                    Runtime.getRuntime().addShutdownHook(new Thread("shutdownHook")
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                handler.stop();
                            }
                            catch (NullPointerException e) {}
                        }
                    });
                }
                catch (ConnectException e)
                {
                    printStartup("Couldnt connect, server probably down:\n" + String.valueOf(e), true);
                    JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (ConnEx)", JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException e)
                {
                    printStartup("Unable to connect to server:\n" + String.valueOf(e), true);
                    JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (IOEx)", JOptionPane.ERROR_MESSAGE);
                }

                SignalMap.setVisible(true);
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

    public static synchronized void reconnect()
    {
        if (System.currentTimeMillis() - lastReconnectAttempt > 5000)
        {
            lastReconnectAttempt = System.currentTimeMillis();

            handler.sendSocketClose();
            handler.closeSocket();

            try
            {
                serverSocket = new Socket(InetAddress.getByName(host), port);

                handler.sendName(EastAngliaMapClient.clientName);

                if (serverSocket.isConnected())
                    printStartup("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);
            }
            catch (ConnectException e)
            {
                printStartup("Unable connect, server probably down.\n" + e, true);
                //JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (ConnEx)", JOptionPane.ERROR_MESSAGE);
            }
            catch (IOException e)
            {
                printStartup("Unable to connect to server\n" + e, true);
                //JOptionPane.showMessageDialog(null, "Unable to connect to host, the server may be down but check your internet connection", "Connection error (IOEx)", JOptionPane.ERROR_MESSAGE);
            }
        }
        else
            try { Thread.sleep(1000); }
            catch (InterruptedException e) {}

        System.gc();
    }

    public static String getTime()
    {
        return sdf.format(new Date());
    }

    private static void printStartup(String message, boolean toErr)
    {
        if (toErr)
            printErr("[Startup] " + message);
        else
            printOut("[Startup] " + message);
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