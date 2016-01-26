package eastangliamapclient;

import eastangliamapclient.gui.SignalMapDataViewer;
import eastangliamapclient.gui.SignalMapGui;
import eastangliamapclient.gui.SignalMapReplayGui;
import eastangliamapclient.gui.SysTrayHandler;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.SystemTray;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.java_websocket.WebSocket;

public class EastAngliaMapClient
{
    public static final String CLIENT_VERSION = "17";
    public static       String DATA_VERSION   = "0";

    public static URI host;

    public static final File storageDir = new File(System.getProperty("user.home", "C:") + File.separator + ".easigmap");

  //public static Socket serverSocket;
    public static WebSocket serverSocket;

    public static Map<String, String> DataMap = new HashMap<>();

  //public static boolean autoScreencap    = false;
    public static boolean opaqueBerths     = false;
    public static boolean showDescriptions = false; // not headcodes
    public static boolean berthsVisible    = true;
    public static boolean signalsVisible   = true;
    public static boolean pointsVisible    = true;

    public static SignalMapGui        frameSignalMap;
    public static SignalMapDataViewer frameDataViewer;
    public static SignalMapReplayGui  frameReplayControls;

    public static String       clientName;
    public static boolean      connected = false;
    public static String       disconnectReason = null;
    public static boolean      requireManualConnect = false;
    public static boolean      minimiseToSysTray = false;
    public static Dimension    windowSize = new Dimension();
    public static PrintStream  logStream = null;

    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YY HH:mm:ss");

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
    public static boolean blockKeyInput        = false;
    public static boolean shownSystemTrayWarn  = false;
    public static boolean preventSleep         = true;

    public static final boolean verbose = false;

    public static void main(String[] args)
    {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {}

        System.setProperty("args", Arrays.deepToString(args));

        try
        {
            File logFile  = new File(storageDir, "Logs" + File.separator + "Client" + File.separator + "output.log");
            File logFile2 = new File(storageDir, "Logs" + File.separator + "Client" + File.separator + sdf.format(new Date()).replace("/", "-").replace(":", ".") + ".1.log");
            if (logFile2.exists())
                logFile2.delete();
            if (logFile.exists())
                logFile.renameTo(logFile2);

            logFile.getParentFile().mkdirs();
            logFile.createNewFile();

            try { logStream = new PrintStream(new FileOutputStream(logFile), true); }
            catch (FileNotFoundException e) { EastAngliaMapClient.printThrowable(e, "LogFile"); }
        }
        catch (IOException e) { EastAngliaMapClient.printThrowable(e, "LogFile"); }

        VersionChecker.checkVersion();

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
                windowSize = new Dimension(Math.min(Math.max((int) Double.parseDouble(sizeStr[0].trim()), 800), SignalMapGui.DEFAULT_WIDTH), Math.min(Math.max((int) Double.parseDouble(sizeStr[1].trim()), 600), SignalMapGui.DEFAULT_HEIGHT));
            }
            catch (IndexOutOfBoundsException e)
            {
                windowSize = new Dimension(SignalMapGui.DEFAULT_WIDTH, SignalMapGui.DEFAULT_HEIGHT);
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

        try { host = new URI("ws://shwam3.ddns.net:6322"); }
        catch (URISyntaxException ex) { ex.printStackTrace(); }

        EventQueue.invokeLater(() ->
        {
            try
            {
                TD_FONT = Font.createFont(0, EastAngliaMapClient.class.getResourceAsStream("/eastangliamapclient/resources/TDBerth-DM.ttf")).deriveFont(16f);
            }
            catch (FontFormatException | IOException e)
            {
                TD_FONT = new Font("Monospaced", 0, 19);

                printStartup("Couldn\'t create font, stuff will look strange", true);
                printThrowable(e, "Font");
            }

            frameSignalMap = new SignalMapGui(windowSize);
            frameDataViewer = new SignalMapDataViewer();
            frameDataViewer.updateData();
            frameReplayControls = new SignalMapReplayGui();

            SysTrayHandler.initSysTray();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> serverSocket.close(), "shutdownHook"));

            lastReconnectAttempt = System.currentTimeMillis();

            serverSocket = new EASMWebSocket();

            TimeoutHandler.run();

            frameSignalMap.setVisible(true);
        });
    }

    public static synchronized boolean reconnect(boolean force)
    {
        long time = System.currentTimeMillis();

        if (time - lastReconnectAttempt > 5000 && (time - TimeoutHandler.getLastMessageTime() > 30000 && !requireManualConnect || force))
        {
            lastReconnectAttempt = time;
            requireManualConnect = false;

            serverSocket = new EASMWebSocket();
        }

        return false;
    }

    //public static String getTime()
    //{
    //    return clockSDF.format(new Date());
    //}

    static void printStartup(String message, boolean toErr)
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
        if (message != null)
        {
            if (stream != null)
                stream.println(message);

            if (logStream != null)
                logStream.println(message);
        }
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