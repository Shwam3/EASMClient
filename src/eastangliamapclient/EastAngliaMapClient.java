package eastangliamapclient;

import eastangliamapclient.gui.SignalMap;
import eastangliamapclient.gui.SysTrayHandler;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class EastAngliaMapClient
{
    public static final String VERSION = "14";
    public static final String host = "shwam3.ddns.net";
    public static final int    port = 6321;

    public static final File storageDir = new File(System.getProperty("user.home", "C:") + File.separator + ".easigmap");

    public static Socket serverSocket;

    public static Map<String, String> DataMap = new HashMap<>();

    public static boolean logToFile        = true;
    public static boolean screencap        = false;
    public static boolean opaque           = false;
    public static boolean showDescriptions = false; // not headcodes
    public static boolean berthsVisible    = true;
    public static boolean signalsVisible   = true;

    public static SignalMap frameSignalMap;
    public static TrayIcon  trayIcon;
    public static String    clientName;
    public static boolean   connected = false;
    public static boolean   kicked = false;
    public static boolean   minimiseToSysTray = false;
    public static Dimension windowSize = new Dimension();
    public static String    ftpBaseUrl = "";

    public static SimpleDateFormat sdf      = new SimpleDateFormat("dd/MM/YY HH:mm:ss");
    public static SimpleDateFormat clockSDF = new SimpleDateFormat("HH:mm:ss");

    public static       Font  TD_FONT  = new Font("TDBerth DM", 0, 16);
    public static final Color GREEN    = new Color(0,   153, 0);   // proper headcode berth colour
    public static final Color GREY     = new Color(64,  64,  64);  // background coplour of berth
    public static final Color BLACK    = new Color(0,   0,   0);   // background colour
    public static final Color BERTH_ID = new Color(0,   0,   0);   // colour of berth while displaying berth id
    public static final Color WHITE    = new Color(255, 255, 255); // improper headcode berth colour
    public static final Color RED      = new Color(190, 20,  20);  // not used
    public static final Color BLUE     = new Color(0,   255, 255); // not used

    public static long    lastReconnectAttempt = System.currentTimeMillis();
    public static boolean isPreRelease         = false;
    public static boolean screencappingActive  = false;
    public static boolean blockKeyInput        = false;
    public static boolean shownSystemTrayWarn  = false;
    public static boolean preventSleep         = true;

    public static final boolean verbose = true;

    public static void main(String[] args)
    {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}

        try
        {
            int localVersion = Integer.parseInt(VERSION);

            int remoteVersion = -1;
            String downloadLocation = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/Shwam3/EastAngliaSignalMapClient/master/version.txt").openStream())))
            {
                remoteVersion = Integer.parseInt(br.readLine());
                downloadLocation = br.readLine();
            }
            catch (NumberFormatException e) {}

            downloadLocation = (downloadLocation == null ? "http://easignalmap.altervista.org/downloads/" : downloadLocation);

            if (remoteVersion > localVersion)
            {
                printStartup("New version available", false);
                if (JOptionPane.showConfirmDialog(null, "A new version is available, download now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    Desktop.getDesktop().browse(new URI(downloadLocation));
                    printStartup("Downloading new version from " + downloadLocation, false);
                    System.exit(0);
                }
            }
            else if (remoteVersion < Integer.parseInt(VERSION))
                isPreRelease = true;
        }
        catch (FileNotFoundException e) { printStartup("Cant find remote version file", true); }
        catch (URISyntaxException e) {}
        catch (IOException e) { printStartup("Error reading remote version file", true); }

        System.setProperty("args", Arrays.deepToString(args));

        // User preferences
        try
        {
            File preferencesFile = newFile(new File(storageDir, "preferences.txt"));

            Properties preferences = new Properties();
            try (FileInputStream fis = new FileInputStream(preferencesFile))
            {
                preferences.load(fis);
            }

            clientName        = preferences.getProperty("clientName",        System.getProperty("user.name"));
            minimiseToSysTray = preferences.getProperty("minimiseToSysTray", SystemTray.isSupported() ? "true" : "false").equals("true");
            String[] sizeStr  = preferences.getProperty("windowSize",        "1877,928").split(",");
            preventSleep      = preferences.getProperty("preventSleep",      "true").equals("true");

            try
            {
                windowSize = new Dimension(Math.min(Math.max((int) Double.parseDouble(sizeStr[0].trim()), 800), SignalMap.DEFAULT_WIDTH), Math.min(Math.max((int) Double.parseDouble(sizeStr[1].trim()), 600), SignalMap.DEFAULT_HEIGHT));
            }
            catch (IndexOutOfBoundsException e)
            {
                windowSize = new Dimension(SignalMap.DEFAULT_WIDTH, SignalMap.DEFAULT_HEIGHT);
            }

            preferences.setProperty("clientName",        clientName);
            preferences.setProperty("minimiseToSysTray", String.valueOf(minimiseToSysTray));
            preferences.setProperty("windowSize",        ((int) windowSize.getWidth()) + "," + ((int) windowSize.getHeight()));
            preferences.setProperty("preventSleep",      String.valueOf(preventSleep));

            try (FileOutputStream fos = new FileOutputStream(preferencesFile))
            {
                preferences.store(fos, "EA Signal Map Preferences");
            }
        }
        catch (FileNotFoundException e) { printThrowable(e, "Preferences"); }
        catch (IOException e) { printThrowable(e, "Preferences"); }

        try
        {
            Properties ftpLogin = new Properties();
            ftpLogin.load(new FileInputStream(newFile(new File(storageDir, "Website_FTP_Login.properties"))));

            ftpBaseUrl = "ftp://" + ftpLogin.getProperty("Username", "") + ":" + ftpLogin.getProperty("Password", "") + "@ftp.easignalmap.altervista.org/";
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) { printThrowable(e, "FTP Login"); }

        if (Arrays.deepToString(args).contains("-screencap"))
        {
            screencappingActive = true;
            EventHandler.startScreenCapture(60000 * 5);
        }

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
                    printThrowable(e, "Startup - Font");
                }

                frameSignalMap = new SignalMap(windowSize);

                SysTrayHandler.initSysTray();

                Runtime.getRuntime().addShutdownHook(new Thread("shutdownHook")
                {
                    @Override
                    public void run()
                    {
                        try { MessageHandler.stop(); }
                        catch (NullPointerException e) {}
                    }
                });

                try
                {
                    lastReconnectAttempt = System.currentTimeMillis();

                    serverSocket = new Socket(getHostIp(), port); // Throws the errors

                    if (serverSocket.isConnected() && MessageHandler.sendName(clientName))
                    {
                        connected = true;
                        printStartup("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);
                        SysTrayHandler.trayTooltip("Connected");
                    }
                    else
                    {
                        connected = false;
                        printStartup("Not connected to server: " + InetAddress.getByName(host).toString() + ":" + port, true);
                        SysTrayHandler.trayTooltip("Not conected");
                    }
                }
                catch (ConnectException e)
                {
                    printStartup("Couldnt connect, server probably down", true);
                    printThrowable(e, "Startup");
                    //JOptionPane.showMessageDialog(null, "Unable to connected to host, the server may be down but check your internet connection", "Connection error (ConnEx)", JOptionPane.ERROR_MESSAGE);
                    SysTrayHandler.popup("Unable to connect", TrayIcon.MessageType.ERROR);
                    SysTrayHandler.trayTooltip("Not Connected");
                }
                catch (IOException e)
                {
                    printStartup("Unable to connect to server", true);
                    printThrowable(e, "Startup");
                    //JOptionPane.showMessageDialog(null, "Unable to connected to host, the server may be down but check your internet connection", "Connection error (IOEx)", JOptionPane.ERROR_MESSAGE);
                    SysTrayHandler.popup("Unable to connect", TrayIcon.MessageType.ERROR);
                    SysTrayHandler.trayTooltip("Not Connected");
                }

                MessageHandler.start(); // Should be already but make sure

                frameSignalMap.setVisible(true);
            }
        });

        EventHandler.addKeyboardEvents();
        EventHandler.startTimerTasks();

        if (Arrays.deepToString(args).contains("-screencap"))
            EventHandler.screencap();
    }

    public static synchronized boolean reconnect(boolean force)
    {
        long time = System.currentTimeMillis();

        if (time - lastReconnectAttempt > 5000 && (time - MessageHandler.getLastMessageTime() > 30000 && !kicked || force))
        {
            lastReconnectAttempt = time;
            kicked = false;

            MessageHandler.sendSocketClose();
            MessageHandler.closeSocket();

            try
            {
                serverSocket = new Socket(getHostIp(), port);

                if (serverSocket.isConnected() && MessageHandler.sendName(clientName))
                {
                    printStartup("Connected to server: " + serverSocket.getInetAddress().toString() + ":" + serverSocket.getPort(), false);
                    SysTrayHandler.popup("Reconnected", TrayIcon.MessageType.INFO);
                    SysTrayHandler.trayTooltip("Connected");

                    connected = true;

                    return true;
                }
            }
            catch (ConnectException e)
            {
                printStartup("Unable connect, server probably down", true);
                //JOptionPane.showMessageDialog(null, "Unable to connected to host, the server may be down but check your internet connection", "Connection error", JOptionPane.ERROR_MESSAGE);
                SysTrayHandler.popup("Unable to reconnect", TrayIcon.MessageType.ERROR);
                SysTrayHandler.trayTooltip("Not Connected");

                connected = false;
            }
            catch (IOException e)
            {
                printStartup("Unable to connect to server", true);
                printThrowable(e, "Startup");
                //JOptionPane.showMessageDialog(null, "Unable to connected to host, the server may be down but check your internet connection", "Connection error", JOptionPane.ERROR_MESSAGE);
                SysTrayHandler.popup("Unable to reconnect", TrayIcon.MessageType.ERROR);
                SysTrayHandler.trayTooltip("Not Connected");

                connected = false;
            }

            return false;
        }

        return false;
    }

    public static String getTime()
    {
        return clockSDF.format(new Date());
    }

    private static void printStartup(String message, boolean toErr)
    {
        if (toErr)
            printErr("[Startup] " + message);
        else
            printOut("[Startup] " + message);
    }

    //<editor-fold defaultstate="collapsed" desc="Print methods">
    public static void printOut(String message)
    {
        print("[" + sdf.format(new Date()) + "] " + message, System.out);
    }

    public static void printErr(String message)
    {
        print("!!!> [" + sdf.format(new Date()) + "] " + message + " <!!!", System.err);
    }

    public static void printThrowable(Throwable t, String name)
    {
        printErr((name != null && !name.isEmpty() ? "[" + name + "] " : "") + t.toString());

        for (StackTraceElement element : t.getStackTrace())
            printErr((name != null && !name.isEmpty() ? "[" + name + "] -> " : "-> ") + element.toString());

        for (Throwable sup : t.getSuppressed())
            printThrowable0(sup, name);

        printThrowable0(t.getCause(), name);
    }

    private static void printThrowable0(Throwable t, String name)
    {
        if (t != null)
        {
            printErr((name != null && !name.isEmpty() ? "[" + name + "] " : "") + t.toString());

            for (StackTraceElement element : t.getStackTrace())
                printErr((name != null && !name.isEmpty() ? "[" + name + "] -> " : " -> ") + element.toString());
        }
    }

    private static synchronized void print(String message, PrintStream stream)
    {
        stream.println(message);
    }
//</editor-fold>

    public static void writeSetting(String preferenceName, String preferenceValue)
    {
        try
        {
            File preferencesFile = newFile(new File(storageDir, "preferences.txt"));

            Properties preferences = new Properties();
            try (FileInputStream fis = new FileInputStream(preferencesFile))
            {
                preferences.load(fis);
            }
            catch (FileNotFoundException e) {}
            catch (IOException e) {}

            preferences.setProperty(preferenceName, preferenceValue);

            try (FileOutputStream fos = new FileOutputStream(preferencesFile))
            {
                preferences.store(fos, "EA Signal Map Preferences");
            }
            catch (FileNotFoundException e) {}
            catch (IOException e) {}
        }
        catch (IOException e) {}
    }

    public static String getHostIp()
    {
        try
        {
            URLConnection con = new URL("http://easignalmap.altervista.org/server.ip").openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(5000);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream())))
            {
                return br.readLine();
            }
        }
        catch (MalformedURLException e) {}
        catch (IOException e)
        {
            try
            {
                return InetAddress.getByName(host).getHostAddress();
            }
            catch (UnknownHostException e2) {}
        }
        return "error";
    }

    public static File newFile(File file) throws IOException
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }

    public static synchronized void clean()
    {
        DataMap = new HashMap<>(DataMap);
        System.gc();
    }
}