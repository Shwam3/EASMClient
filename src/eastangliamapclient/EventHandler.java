package eastangliamapclient;

import eastangliamapclient.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EventHandler
{
    public static Berth tempOpaqueBerth = null;
    public static BerthContextMenu berthContextMenu = null;
    public static boolean isScreencapping = false;

    private static BufferedImage logo = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

    //<editor-fold defaultstate="collapsed" desc="Station Click">
    public static void stnClick(MouseEvent evt)
    {
        if (SwingUtilities.isLeftMouseButton(evt))
        {
            JLabel lbl = (JLabel) evt.getComponent();

            try
            {
                if (evt.isControlDown())
                    Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + lbl.getToolTipText() + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=wtt"));
                else
                    Desktop.getDesktop().browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + lbl.getToolTipText() + "?stp=WVS&show=all&order=wtt"));

                evt.consume();
            }
            catch (URISyntaxException | IOException e) {}
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Click">
    public static void berthClick(MouseEvent evt)
    {
        Berth berth = (Berth) evt.getComponent();

        if (berth == null)
            return;

        if (SwingUtilities.isRightMouseButton(evt) || evt.isPopupTrigger())
        {
            tempOpaqueBerth = berth;
            tempOpaqueBerth.setOpaque(true);

            berthContextMenu = new BerthContextMenu(berth, evt.getComponent(), evt.getX(), evt.getY());
            evt.consume();
        }

        try {
            if (SwingUtilities.isLeftMouseButton(evt))
            {
                berth.setOpaque(true);

                if (berth.isProperHeadcode())
                    Desktop.getDesktop().browse(new URI(String.format("http://www.realtimetrains.co.uk/search/advancedhandler?type=advanced&qs=true&search=%s%s", berth.getHeadcode(), evt.isControlDown() || berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));

                getRidOfBerth();
                evt.consume();
            }
        }
        catch (URISyntaxException | IOException e) {}

        berth.setOpaque(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Menu Click">
    public static void berthMenuClick(ActionEvent evt, Berth berth)
    {
        String cmd = evt.getActionCommand();

        if (cmd.equals("Search Headcode"))
        {
            try
            {
                Desktop.getDesktop().browse(new URI(String.format("http://www.realtimetrains.co.uk/search/advancedhandler?type=advanced&qs=true&search=%s%s", berth.getHeadcode(), berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));
            }
            catch (URISyntaxException | IOException e) {}
        }
        else if (cmd.startsWith("Train\'s History"))
        {
            if (berth.hasTrain())
                MessageHandler.requestHistoryOfTrain(berth.getCurrentId(true));
            else
                getRidOfBerth();
        }
        else if (cmd.startsWith("Berth\'s History"))
        {
            String id = evt.getActionCommand().substring(17, 23);
            MessageHandler.requestHistoryOfBerth(id);
        }

        EastAngliaMapClient.blockKeyInput = false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Keyboard Events">
    public static void addKeyboardEvents()
    {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher()
        {
            @Override
            public boolean dispatchKeyEvent(KeyEvent evt)
            {
                if (evt.isConsumed())
                    return false;

                if (evt.getID() == KeyEvent.KEY_PRESSED && !EastAngliaMapClient.blockKeyInput)
                {
                    int keyCode = evt.getKeyCode();

                    if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F1 + EastAngliaMapClient.frameSignalMap.TabBar.getTabCount() - 1) // Function keys
                    {
                        if (evt.isControlDown()) // Control for 13-24
                            keyCode += 13;
                        if (evt.isShiftDown()) // Shift for 25-36, both for 37-48
                            keyCode += 25;

                        if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F1 + EastAngliaMapClient.frameSignalMap.TabBar.getTabCount() - 1)
                        {
                            EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(keyCode - KeyEvent.VK_F1);
                            evt.consume();
                            return true;
                        }
                    }

                    if (!EastAngliaMapClient.frameSignalMap.TabBar.hasFocus())
                        if (keyCode == KeyEvent.VK_LEFT)
                            EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(Math.max(0, EastAngliaMapClient.frameSignalMap.TabBar.getSelectedIndex() - 1));
                        else if (keyCode == KeyEvent.VK_RIGHT)
                            EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(Math.min(EastAngliaMapClient.frameSignalMap.TabBar.getTabCount() - 1, EastAngliaMapClient.frameSignalMap.TabBar.getSelectedIndex() + 1));
                }

                if (evt.getID() == KeyEvent.KEY_TYPED)
                {
                    switch (Character.toString(evt.getKeyChar()).toLowerCase())
                    {
                        case "o":
                            if (!EastAngliaMapClient.blockKeyInput)
                            {
                                Berths.toggleBerthsOpacities();
                                evt.consume();
                            }
                            break;

                        case "h":
                            if (!EastAngliaMapClient.blockKeyInput)
                            {
                                new HelpDialog();
                                evt.consume();
                            }
                            break;

                        case "r":
                            EastAngliaMapClient.blockKeyInput = false;
                            //EastAngliaMapClient.refresh();
                            EastAngliaMapClient.clean();
                            break;

                        case "d":
                            if (!EastAngliaMapClient.blockKeyInput)
                            {
                                Berths.toggleBerthDescriptions();
                                evt.consume();
                            }
                            break;

                        case "v":
                            if (!EastAngliaMapClient.blockKeyInput)
                            {
                                Berths.toggleBerthVisibilities();
                                evt.consume();
                            }
                            break;

                        case "t":
                            if (!EastAngliaMapClient.blockKeyInput)
                            {
                                EastAngliaMapClient.frameSignalMap.frame.pack();
                                EastAngliaMapClient.frameSignalMap.frame.setLocationRelativeTo(null);

                                evt.consume();
                            }
                            EastAngliaMapClient.clean();
                            break;
                    }
                }
                return evt.isConsumed();
            }
        });
    }
    //</editor-fold>

    public static void startTimerTasks()
    {
        Timer keepAwake = new Timer("keepAwake", true);
        keepAwake.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    EastAngliaMapClient.clean();

                    if (EastAngliaMapClient.preventSleep)
                    {

                        Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                        new Robot().mouseMove(mouseLoc.x, mouseLoc.y);
                    }
                }
                catch (AWTException | NullPointerException e) {}
            }
        }, 30000, 30000);

        javax.swing.Timer clockTimer = new javax.swing.Timer(250, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (SignalMap.BackgroundPanel bp : EastAngliaMapClient.frameSignalMap.getPanels())
                    bp.repaint(780, 10, 280, 50);
            }
        });
        clockTimer.start();
    }

    public static void startScreenCapture(int interval)
    {
        try
        {
            BufferedImage tmpLogo = ImageIO.read(EastAngliaMapClient.newFile(new File(EastAngliaMapClient.storageDir, "logo.png")));
            Graphics2D g2d = logo.createGraphics();
            g2d.drawImage(tmpLogo, 0, 0, null);
            g2d.dispose();
        }
        catch (IOException e) {}

        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                int mins = calendar.get(Calendar.MINUTE);
                calendar.add(Calendar.MINUTE, 5 - (mins % 5));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 500);

                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            if (EastAngliaMapClient.serverSocket != null && EastAngliaMapClient.screencap && EastAngliaMapClient.serverSocket.isConnected())
                                EventQueue.invokeAndWait(new Runnable() { @Override public void run() { takeScreencaps(); }});
                            else
                                printScreencap("Not taking screencaps", false);
                        }
                        catch (InterruptedException | InvocationTargetException e) { isScreencapping = false; }
                        catch (Throwable t) { isScreencapping = false; }
                    }
                }, calendar.getTime().getTime() - System.currentTimeMillis(), 300000, TimeUnit.MILLISECONDS);
            }
        });

        takeScreencaps();
    }

    public static void takeScreencaps()
    {
        final long startTime = System.currentTimeMillis();

        if (EastAngliaMapClient.frameSignalMap == null)
            return;

        if (!EventQueue.isDispatchThread())
        {
            printScreencap("Not on EDT", true);
            return;
        }

        if (isScreencapping)
        {
            printScreencap("Already screencapping", true);
            return;
        }

        EastAngliaMapClient.frameSignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));

        printScreencap("Updating images (" + EastAngliaMapClient.getTime() + ")", false);

        MessageHandler.requestAll();

        final java.util.List<BufferedImage> images = new ArrayList<>();
        final java.util.List<String> names = new ArrayList<>();

        EastAngliaMapClient.frameSignalMap.prepForScreencap();
        isScreencapping = true;

        for (SignalMap.BackgroundPanel bp : EastAngliaMapClient.frameSignalMap.getPanels())
        {
            BufferedImage image = bp.getBufferedImage();
            names.add(bp.getName().replace("/", " + "));

            Graphics2D g2d = image.createGraphics();
            g2d.setBackground(bp.getBackground());
            g2d.clearRect(0, 0, SignalMap.BackgroundPanel.BP_DEFAULT_WIDTH, SignalMap.BackgroundPanel.BP_DEFAULT_HEIGHT);
            bp.invalidate();
            bp.paint(g2d);
            bp.revalidate();

            g2d.dispose();

            images.add(overlayImage(image, bp.getName().replace("/", " + ")));
        }

        isScreencapping = false;
        EastAngliaMapClient.frameSignalMap.finishScreencap();

        new Thread("uploadScreencaps")
        {
            @Override
            public void run()
            {
                File screencapPath = new File(EastAngliaMapClient.storageDir, "images");

                try
                {
                    BufferedImage bigImage = new BufferedImage(5555, 3419, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = bigImage.createGraphics();
                    g2d.clearRect(0, 0, bigImage.getWidth(), bigImage.getHeight());

                    g2d.drawImage(images.get(0),  0,    0,    null);
                    g2d.drawImage(images.get(1),  1852, 0,    null);
                    g2d.drawImage(images.get(2),  3704, 0,    null);
                    g2d.drawImage(images.get(3),  0,    855,  null);
                    g2d.drawImage(images.get(4),  1852, 855,  null);
                    g2d.drawImage(images.get(5),  3704, 855,  null);
                    g2d.drawImage(images.get(6),  0,    1710, null);
                    g2d.drawImage(images.get(7),  1852, 1710, null);
                    g2d.drawImage(images.get(8),  3704, 1710, null);
                    g2d.drawImage(images.get(9),  0,    2565, null);
                    g2d.drawImage(images.get(10), 1852, 2565, null);
                    g2d.drawImage(images.get(11), 3704, 2565, null);

                    for (int i = 0; i < images.size(); i++)
                    {
                        try
                        {
                            ImageIO.write(images.get(i), "png", EastAngliaMapClient.newFile(new File(screencapPath, names.get(i) + ".png")));
                            //printScreencap("    > " + names.get(i) + ".png", !ImageIO.write(images.get(i), "png", imageFile));
                        }
                        catch (FileNotFoundException e)
                        {
                            SysTrayHandler.popup("Unable to create screencap \"" + names.get(i) + "\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                        }
                        catch (IOException e)
                        {
                            printScreencap("IOException creating screencap \"" + names.get(i) + "\"", true);
                            EastAngliaMapClient.printThrowable(e, "Screencap");
                            SysTrayHandler.popup("Unable to create screencap \"" + names.get(i) + "\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                        }
                    }

                    ImageIO.write(bigImage, "png", EastAngliaMapClient.newFile(new File(screencapPath, "All.png")));
                    //printScreencap("    > All.png", !ImageIO.write(bigImage, "png", new File(screencapPath, "All.png")));
                }
                catch (FileNotFoundException e)
                {
                    printScreencap("FileNotFoundException creating screencap \"All\"", true);
                    EastAngliaMapClient.printThrowable(e, "Screencap");
                    SysTrayHandler.popup("Unable to create screencap \"All\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                }
                catch (IOException e)
                {
                    printScreencap("IOException creating screencap \"All\"", true);
                    EastAngliaMapClient.printThrowable(e, "Screencap");
                    SysTrayHandler.popup("Unable to create screencap \"All\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                }

                String[] imageURLs = {"LiverpoolStStratford", "Ilford", "Shenfield", "Witham", "HackneyBrimsdown", "Harlow", "Colchester", "Clacton", "Ipswich", "CambridgeCA",
                    "CambridgeEN", "Norwich", "All"};

                Collections.reverse(names);
                names.add("All");

                for (int i = 0; i < 13; i++)
                {
                    try
                    {
                        URLConnection con = new URL(EastAngliaMapClient.ftpBaseUrl + imageURLs[i] + "/image.png;type=i").openConnection();
                        con.setConnectTimeout(10000);
                        try (FileInputStream in = new FileInputStream(new File(screencapPath, names.get(i) + ".png")); BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream()))
                        {
                            byte[] buffer = new byte[8192];
                            int read;
                            while ((read = in.read(buffer)) != -1)
                                out.write(buffer, 0, read);
                        }
                        catch (IOException e) {}

                        //printScreencap("    > " + names.get(i), false);
                    }
                    catch (IOException e) {}
                }

                printScreencap("Finished updating website images in ~ " + (System.currentTimeMillis() - startTime) / 1000f + " secs", false);

                EastAngliaMapClient.clean();
            }
        }.start();
    }

    public static void screencap()
    {
        EastAngliaMapClient.screencap = !EastAngliaMapClient.screencap;

        try { EastAngliaMapClient.frameSignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "") +  ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : "")); }
        catch (NullPointerException e) {}
    }

    private static BufferedImage overlayImage(BufferedImage image, String name)
    {
        Graphics2D g2d = image.createGraphics();

        g2d.drawImage(image, 0, 0, null);

        g2d.drawImage(logo, 70, 10, null);
        g2d.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 36));
        g2d.setColor(Color.WHITE);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.drawString(name, 145, 54);

        g2d.dispose();

        return image;
    }

    public static void printScreencap(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Screencap] " + message);
        else
            EastAngliaMapClient.printOut("[Screencap] " + message);
    }

    public static void getRidOfBerth()
    {
        if (tempOpaqueBerth != null)
        {
            Berth berth = tempOpaqueBerth;
            tempOpaqueBerth = null;
            berth.setOpaque(false);
        }
    }
}