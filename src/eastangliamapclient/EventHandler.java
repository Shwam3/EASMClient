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
    public static Berth tempOpaqueBerth = null;
    public static BerthContextMenu berthContextMenu = null;
    private static String screencapPath;

    //<editor-fold defaultstate="collapsed" desc="Update Last Message Clocks">
    public static void updateLastMsgClock()
    {
        /*Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        EastAngliaMapClient.lastMessageTime = currentTime.getTime();

        for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
            lbl.setText(sdf.format(currentTime));*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Station Click">
    public static void stnClick(MouseEvent evt)
    {
        if (evt.getButton() == 1)
        {
            JLabel lbl = (JLabel) evt.getComponent();

            try
            {
                if (evt.isControlDown())
                    EastAngliaMapClient.desktop.browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + lbl.getToolTipText() + new SimpleDateFormat("/yyyy/MM/dd").format(new Date()) + "/0000-2359?stp=WVS&show=all&order=wtt"));
                else
                    EastAngliaMapClient.desktop.browse(new URI("http://www.realtimetrains.co.uk/search/advanced/" + lbl.getToolTipText() + "?stp=WVS&show=all&order=wtt"));
            }
            catch (URISyntaxException | IOException ex) {}
            finally
            {
                evt.consume();
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Last Messsage Click">
    public static void lastMessageClick(MouseEvent evt)
    {
        /*JLabel evtLabel = (JLabel)evt.getComponent();

        if (evtLabel.getText().equals("NO  DATA") && EastAngliaMapClient.connect)
            lastMessageNoData();

        for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
        {
            lbl.setForeground(EastAngliaMapClient.GREEN);
            lbl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Last Message (No Data)">
    public static void lastMessageNoData()
    {
        /*if (EastAngliaMapClient.connect)
        {
            for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
            {
                lbl.setText("NO  DATA");
                lbl.setForeground(Color.red);
                lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Log File Button Click">
    public static void butOutputClicked(MouseEvent evt)
    {
        /*if (evt.getButton() == 1)
        {
            try
            {
                EastAngliaMapClient.desktop.open(EastAngliaMapClient.outFile);
            }
            catch (IOException | NullPointerException e) {}
            evt.consume();
        }*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Filtered Log File Button Click">
    public static void butErrorClicked(MouseEvent evt)
    {
        /*if (evt.getButton() == 1)
        {
            try
            {
                EastAngliaMapClient.desktop.open(EastAngliaMapClient.errFile);
            }
            catch (IOException | NullPointerException e) {}
            evt.consume();
        }*/
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Click">
    public static void tdMouseClick(MouseEvent evt)
    {
        JLabel lbl = (JLabel) evt.getComponent();
        Berth berth = Berths.getBerth(lbl);

        if (evt.getButton() == 3 || evt.isPopupTrigger())
        {
            tempOpaqueBerth = berth;
            tempOpaqueBerth.setOpaque(true);

            berthContextMenu = new BerthContextMenu(berth, evt.getComponent(), evt.getX(), evt.getY());
            evt.consume();

        }

        if (evt.getButton() == 1)
        {
            berth.setOpaque(true);

            if (berthEvent(evt.isShiftDown(), evt.isControlDown(), evt.isAltDown() || evt.isAltGraphDown(), berth.isProperHeadcode(), berth))
            {
                tempOpaqueBerth = null;
                evt.consume();
            }
        }

        berth.setOpaque(false);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Event">
    private static boolean berthEvent(boolean shift, boolean control, boolean alt, boolean properHeadcode, Berth berth)
    {
        EastAngliaMapClient.blockKeyInput = true;

        if (alt && !shift && !control)
        {
            if (berth.setProblematicBerth(!berth.isProblematic()))
            {
                if (!berth.hasTrain())
                    berth.interpose("-LP-", berth.getCurrentId(true));
            }
            else
                if (berth.getHeadcode().equals("-LP-"))
                    berth.cancel(berth.getCurrentId(true));

            EastAngliaMapClient.blockKeyInput = false;
            return true;
        }

        try
        {
            if (properHeadcode)
            {
                EastAngliaMapClient.desktop.browse(new URI(String.format("http://www.realtimetrains.co.uk/search/advancedhandler?type=advanced&qs=true&search=%s%s",
                        berth.getHeadcode(), control || berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));

                EastAngliaMapClient.blockKeyInput = false;
                return true;
            }
        }
        finally
        {
            EastAngliaMapClient.blockKeyInput = false;
            return false;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Berth Menu Click">
    public static void berthMenuItemClick(ActionEvent evt, Berth berth)
    {
        boolean isProperHeadcode = berth.isProperHeadcode();

        switch (evt.getActionCommand().toLowerCase())
        {
            case "search headcode":
                berthEvent(false, false, false, isProperHeadcode, berth);
                break;

            case "train\'s history":
                if (berth.hasTrain())
                    EastAngliaMapClient.handler.requestHistoryOfTrain(berth.getCurrentId(true));
                break;

            case "berth\'s history":
                EastAngliaMapClient.handler.requestHistoryOfBerth(berth.getCurrentId(true));
                break;

            default:
                if (evt.getActionCommand().toLowerCase().startsWith("berth\'s history"))
                {
                    String id = evt.getActionCommand().substring(17, 23);
                    EastAngliaMapClient.handler.requestHistoryOfBerth(id);
                    //System.out.println("id: " + id);
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
                if (!EastAngliaMapClient.blockKeyInput)
                {
                    int keyCode = evt.getKeyCode();
                    int newIndex = -1;

                    if (((keyCode >= 49) && (keyCode <= 57))) // Number keys
                    {
                        newIndex = keyCode - 49;
                    }
                    else if (((keyCode >= 112) && (keyCode <= 123))) // Function keys
                    {
                        newIndex = keyCode - 112;
                    }

                    if (newIndex != -1 || newIndex >= 0 && newIndex <= SignalMap.TabBar.getTabCount() - 1)
                    {
                        SignalMap.TabBar.setSelectedIndex(newIndex);
                        evt.consume();
                    }
                }

                if (!evt.isConsumed() && evt.getID() == KeyEvent.KEY_TYPED)
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
                            EastAngliaMapClient.refresh();
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
                            int tab = EastAngliaMapClient.SignalMap.TabBar.getSelectedIndex();

                            EastAngliaMapClient.SignalMap.dispose();
                            EastAngliaMapClient.SignalMap = new SignalMap();
                            EastAngliaMapClient.SignalMap.setVisible(true);
                            EastAngliaMapClient.SignalMap.readFromMap(EastAngliaMapClient.CClassMap);

                            EastAngliaMapClient.SignalMap.TabBar.setSelectedIndex(tab);

                            // Lovely code
                            Berths.toggleBerthDescriptions();
                            Berths.toggleBerthDescriptions();

                            Berths.toggleBerthsOpacities();
                            Berths.toggleBerthsOpacities();

                            Berths.toggleBerthVisibilities();
                            Berths.toggleBerthVisibilities();

                            System.gc();
                            evt.consume();
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
                for (JLabel lbl : EastAngliaMapClient.SignalMap.clockLbls)
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
                if (EastAngliaMapClient.serverSocket != null && EastAngliaMapClient.screencap && EastAngliaMapClient.serverSocket.isConnected())
                    takeScreencaps();
                else
                    printScreencap("Not taking screencaps", false);
            }
        }, calendar.getTime(), interval);

        takeScreencaps();
    }

    public static void takeScreencaps()
    {
        try
        {
            EastAngliaMapClient.SignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));

            printScreencap("Updating images (" + EastAngliaMapClient.getTime() + ")", false);
            EastAngliaMapClient.handler.requestAll();

            java.util.List<BufferedImage> images = new ArrayList<>();
            java.util.List<String> names = new ArrayList<>();

            EastAngliaMapClient.SignalMap.toggleButtonVisibility();
            for (JPanel pnl : EastAngliaMapClient.SignalMap.getPanels())
            {
                Dimension dim = pnl.getSize();
                BufferedImage image = (BufferedImage) pnl.createImage(dim.width, dim.height);

                Graphics2D g2d = image.createGraphics();
                g2d.setBackground(pnl.getBackground());
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.clearRect(0, 0, dim.width, dim.width);

                pnl.paint(g2d);

                images.add(image);
                names.add(pnl.getName().replace("/", " + "));
            }

            EastAngliaMapClient.SignalMap.toggleButtonVisibility();

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
                        printScreencap("FileNotFoundException creating screencap \"" + names.get(i) + "\":\n" + e, true);
                    }
                    catch (IOException e)
                    {
                        printScreencap("IOException creating screencap \"" + names.get(i) + "\":\n" + e, true);
                    }
                }

                ImageIO.write(bigImage, "png", new File(screencapPath, "All.png"));
                printScreencap("    All.png", false);

                ProcessBuilder pb = new ProcessBuilder(System.getProperty("java.home") + File.separator + "bin" + File.separator + "java.exe",
                        "-jar", "\"C:\\Users\\Shwam\\Documents\\GitHub\\FTPUploader\\dist\\FTPUploader.jar\"");
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.start();
            }
            catch (FileNotFoundException e)
            {
                printScreencap("FileNotFoundException creating screencap \"All\":\n" + e, true);
            }
            catch (IOException e)
            {
                printScreencap("IOException creating screencap \"All\":\n" + e, true);
            }
        }
        catch (NullPointerException e)
        {
            printScreencap("NPE", true);
        }
        finally
        {
            System.gc();
        }
    }

    public static void screencap()
    {
        EastAngliaMapClient.screencap = !EastAngliaMapClient.screencap;

        EastAngliaMapClient.SignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.VERSION + ")" + (EastAngliaMapClient.screencap ? " - Screencapping" : ""));
        EastAngliaMapClient.handler.sendName(EastAngliaMapClient.clientName);
    }

    public static void printScreencap(String message, boolean toErr)
    {
        if (toErr)
            EastAngliaMapClient.printErr("[Screencap] " + message);
        else
            EastAngliaMapClient.printOut("[Screencap] " + message);
    }
}