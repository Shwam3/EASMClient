package eastangliamapclient;

import eastangliamapclient.gui.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import javax.imageio.ImageIO;
import javax.swing.*;

public class EventHandler
{
    public  static Berth tempOpaqueBerth = null;
    public  static BerthContextMenu berthContextMenu = null;
    private static String screencapPath;

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
            catch (URISyntaxException | IOException ex) {}
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Click">
    public static void berthClick(MouseEvent evt)
    {
        Berth berth = Berths.getBerth((JLabel) evt.getComponent());

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
        switch (evt.getActionCommand())
        {
            case "Search Headcode":
                try
                {
                    Desktop.getDesktop().browse(new URI(String.format("http://www.realtimetrains.co.uk/search/advancedhandler?type=advanced&qs=true&search=%s%s", berth.getHeadcode(), berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));
                }
                catch (URISyntaxException | IOException e) {}
                break;

            case "Train\'s History":
                if (berth.hasTrain())
                    EastAngliaMapClient.handler.requestHistoryOfTrain(berth.getCurrentId(true));
                else
                    getRidOfBerth();
                break;

            case "Berth\'s History":
                EastAngliaMapClient.handler.requestHistoryOfBerth(berth.getCurrentId(true));
                break;

            default:
                if (evt.getActionCommand().startsWith("Berth\'s History"))
                {
                    String id = evt.getActionCommand().substring(17, 23);
                    EastAngliaMapClient.handler.requestHistoryOfBerth(id);
                }
                break;
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

                if (evt.getID() == KeyEvent.KEY_PRESSED || evt.getID() == KeyEvent.KEY_TYPED && !EastAngliaMapClient.blockKeyInput)
                {
                    int keyCode = evt.getKeyCode() - 112;

                    if (keyCode >= 0 && keyCode <= EastAngliaMapClient.SignalMap.TabBar.getTabCount() - 1) // Function keys
                    {
                        if (evt.isControlDown()) // Control for 13-24
                            keyCode += 13;
                        if (evt.isShiftDown()) // Shift for 25-36, both for 37-48
                            keyCode += 25;

                        if (keyCode >= 0 && keyCode <= EastAngliaMapClient.SignalMap.TabBar.getTabCount() - 1)
                        {
                            EastAngliaMapClient.SignalMap.TabBar.setSelectedIndex(keyCode);
                            evt.consume();
                            return true;
                        }
                    }
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
                            System.gc();
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
                                int tab = EastAngliaMapClient.SignalMap.TabBar.getSelectedIndex();

                                EastAngliaMapClient.SignalMap.dispose();
                                EastAngliaMapClient.SignalMap = new SignalMap();
                                EastAngliaMapClient.SignalMap.setVisible(true);
                                EastAngliaMapClient.SignalMap.readFromMap(EastAngliaMapClient.CClassMap);

                                EastAngliaMapClient.SignalMap.TabBar.setSelectedIndex(tab);

                                evt.consume();
                            }
                            System.gc();
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
                    System.gc();

                    Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                    Robot rob = new Robot();
                    rob.mouseMove(mouseLoc.x, mouseLoc.y);
                }
                catch (AWTException e) {}
            }
        }, 30000, 30000);

        javax.swing.Timer clockTimer = new javax.swing.Timer(250, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (JLabel lbl : EastAngliaMapClient.SignalMap.clocks)
                    lbl.setText(EastAngliaMapClient.getTime());
            }
        });
        clockTimer.start();
    }

    public static void startScreenCapture(int interval, final String path)
    {
        EastAngliaMapClient.printOut("[Screencap] Screencap path \"" + path + "\"");
        screencapPath = path;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        int mins = calendar.get(Calendar.MINUTE);
        int mod = mins % 5;
        calendar.add(Calendar.MINUTE, mod < 3 ? -mod : (5-mod));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 500);

        Timer screencapTimer = new Timer("screencapTimer");
        screencapTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                try
                {
                    if (EastAngliaMapClient.serverSocket != null && EastAngliaMapClient.screencap && EastAngliaMapClient.serverSocket.isConnected())
                        takeScreencaps();
                    else if (Arrays.deepToString(EastAngliaMapClient.args).contains("-screencap"))
                        printScreencap("Not taking screencaps", false);
                }
                finally { return; }
            }
        }, calendar.getTime(), interval);

        takeScreencaps();
    }

    public static void takeScreencaps()
    {
        try
        {
            //final long startTime = System.currentTimeMillis();

            EastAngliaMapClient.SignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));

            printScreencap("Updating images (" + EastAngliaMapClient.getTime() + ")", false);
            EastAngliaMapClient.handler.requestAll();

            ArrayList<BufferedImage> images = new ArrayList<>();
            ArrayList<String> names = new ArrayList<>();

            EastAngliaMapClient.SignalMap.prepForScreencap();
            EastAngliaMapClient.SignalMap.repaint();

            for (JPanel panel : EastAngliaMapClient.SignalMap.getPanels())
            {
                BufferedImage image = (BufferedImage) panel.createImage(1851, 854);

                Graphics2D g2d = image.createGraphics();
                g2d.setBackground(panel.getBackground());
                //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.clearRect(0, 0, 1851, 854);

                panel.paint(g2d);
                panel.paintAll(g2d);

                images.add(image);
                names.add(panel.getName().replace("/", " + "));
            }

            // Fix for Liv St panel
            JPanel panel = EastAngliaMapClient.SignalMap.getPanels().get(0);

            BufferedImage image = (BufferedImage) panel.createImage(1851, 854);

            Graphics2D g = image.createGraphics();
            g.setBackground(panel.getBackground());
            //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.clearRect(0, 0, 1851, 854);

            panel.paint(g);
            panel.paintAll(g);

            images.set(0, image);
            names.set(0, panel.getName().replace("/", " + "));

            EastAngliaMapClient.SignalMap.finishScreencap();

            try
            {
                BufferedImage bigImage = new BufferedImage(5555, 3419, BufferedImage.TYPE_INT_RGB);
                //BufferedImage bigImage = new BufferedImage(7407, 2564, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = bigImage.createGraphics();
                g2d.setBackground(new Color(64, 64, 64));
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.clearRect(0, 0, bigImage.getWidth(), bigImage.getHeight());

                //3 x 4
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

                //4 x 3
                //g2d.drawImage(images.get(0),  0,    0,    null);
                //g2d.drawImage(images.get(1),  1852, 0,    null);
                //g2d.drawImage(images.get(2),  3704, 0,    null);
                //g2d.drawImage(images.get(3),  5556, 0,    null);
                //g2d.drawImage(images.get(4),  0,    855,  null);
                //g2d.drawImage(images.get(5),  1852, 855,  null);
                //g2d.drawImage(images.get(6),  3704, 855,  null);
                //g2d.drawImage(images.get(7),  5556, 855,  null);
                //g2d.drawImage(images.get(8),  0,    1710, null);
                //g2d.drawImage(images.get(9),  1852, 1710, null);
                //g2d.drawImage(images.get(10), 3704, 1710, null);
                //g2d.drawImage(images.get(11), 5556, 1710, null);

                for (int i = 0; i < images.size(); i++)
                {
                    try
                    {
                        ImageIO.write(images.get(i), "png", new File(screencapPath, names.get(i) + ".png"));
                        printScreencap("    " + names.get(i) + ".png", false);
                    }
                    catch (FileNotFoundException e)
                    {
                        printScreencap("FileNotFoundException creating screencap \"" + names.get(i) + "\":\n" + String.valueOf(e), true);
                        SysTrayHandler.popup("Unable to create screencap \"" + names.get(i) + "\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                    }
                    catch (IOException e)
                    {
                        printScreencap("IOException creating screencap \"" + names.get(i) + "\":\n" + String.valueOf(e), true);
                        SysTrayHandler.popup("Unable to create screencap \"" + names.get(i) + "\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
                    }
                }

                ImageIO.write(bigImage, "png", new File(screencapPath, "All.png"));
                printScreencap("    All.png", false);
            }
            catch (FileNotFoundException e)
            {
                printScreencap("FileNotFoundException creating screencap \"All\":\n" + String.valueOf(e), true);
                SysTrayHandler.popup("Unable to create screencap \"All\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
            }
            catch (IOException e)
            {
                printScreencap("IOException creating screencap \"All\":\n" + String.valueOf(e), true);
                SysTrayHandler.popup("Unable to create screencap \"All\"\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
            }

            ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe",
                    "-jar", "\"C:\\Users\\Shwam\\Documents\\GitHub\\FTPUploader\\dist\\FTPUploader.jar\"");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

            final Process p = pb.start();

//            final Thread t = new Thread("screencapProcThing")
//            {
//                @Override
//                public void run()
//                {
//                    int exit = -9999;
//                    try { exit = p.waitFor(); }
//                    catch (InterruptedException e) { printScreencap("Interrupted", true); }
//
//                    String doneTime = ((System.currentTimeMillis() - startTime) / 1000) + "." + ((System.currentTimeMillis() - startTime) - (Math.round((System.currentTimeMillis() - startTime) / 1000) * 1000));
//                    if (!EastAngliaMapClient.SignalMap.isVisible())
//                        SysTrayHandler.popup("Screencaps done in " + doneTime + "secs" + (exit != 0 ? " (" + exit + ")" : ""), TrayIcon.MessageType.INFO);
//
//                    printScreencap("Screencap time: " + doneTime + "s, exit: " + exit, false);
//                }
//            };
//            t.start();
//
//            new Timer().schedule(new TimerTask()
//            {
//                @Override
//                public void run()
//                {
//                    t.interrupt();
//                }
//            }, 120000);
        }
        catch (NullPointerException e) {}
        catch (IOException e)
        {
            printScreencap("Unable to start FTP Uploader:\n" + String.valueOf(e), true);
            SysTrayHandler.popup("Unable to start FTP Uploader\n(" + e.getClass().getSimpleName() + ")", TrayIcon.MessageType.WARNING);
        }
        finally
        {
            System.gc();
        }
    }

    public static void screencap()
    {
        EastAngliaMapClient.screencap = !EastAngliaMapClient.screencap;

        try { EastAngliaMapClient.SignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : "")); }
        catch (NullPointerException e) {}
        EastAngliaMapClient.handler.sendName(EastAngliaMapClient.clientName);
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