package eastangliamapclient;

import eastangliamapclient.gui.SignalMapGui;
import eastangliamapclient.gui.SysTrayHandler;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

public class ScreencapManager
{
    public static boolean isScreencapping = false;
    private static final BufferedImage logo = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);

    public static void initScreenCapture()
    {
        try
        {
            // Redraw a copy, uses less memory for some reason
            BufferedImage tmpLogo = ImageIO.read(EastAngliaMapClient.newFile(new File(EastAngliaMapClient.storageDir, "logo.png")));
            Graphics2D g2d = logo.createGraphics();
            g2d.drawImage(tmpLogo, 0, 0, null);
            g2d.dispose();
        }
        catch (IOException e) {}

        try
        {
            EventQueue.invokeAndWait(() ->
            {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
                int mins = calendar.get(Calendar.MINUTE);
                calendar.add(Calendar.MINUTE, 10 - (mins % 10));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 100);

                Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() ->
                {
                    try
                    {
                        // 00:00 to 06:00 images every 5 mins
                        if (Integer.parseInt(EastAngliaMapClient.getTime().substring(0, 2)) < 6 && Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE) % 5 != 0)
                        {
                            printScreencap("Not taking screencaps, waiting for time", false);
                        }
                        else if (Calendar.getInstance(TimeZone.getTimeZone("GMT")).get(Calendar.MINUTE) % 2 != 0)
                        {
                            if (EastAngliaMapClient.serverSocket != null && EastAngliaMapClient.autoScreencap && EastAngliaMapClient.serverSocket.isConnected())
                                EventQueue.invokeAndWait(() -> takeScreencaps());
                            else
                                printScreencap("Not taking screencaps, disconnected or turned off", false);
                        }
                    }
                    catch (Exception e) { isScreencapping = false; }
                }, calendar.getTimeInMillis() - System.currentTimeMillis(), 60000, TimeUnit.MILLISECONDS);

                printScreencap("Auto-Screencapping started", false);
            });
        }
        catch (InterruptedException  | InvocationTargetException e) {}
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

        if (!MessageHandler.isReady())
        {
            printScreencap("Not received full map, waiting 5 secs and retrying", true);
            try { Thread.sleep(4000); }
            catch(InterruptedException e) {}

            if (!MessageHandler.isReady())
            {
                MessageHandler.requestAll();
                try { Thread.sleep(1000); }
                catch(InterruptedException e) {}
            }

            if (!MessageHandler.isReady())
            {
                printScreencap("Not received full map, aborting screencap", true);
                return;
            }
        }

        if (isScreencapping)
        {
            printScreencap("Already screencapping", true);
            return;
        }

        isScreencapping = true;
        if (EastAngliaMapClient.frameSignalMap != null)
            EastAngliaMapClient.frameSignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "")
                +  " / v" + EastAngliaMapClient.DATA_VERSION + ")"
                + (EastAngliaMapClient.autoScreencap ? " - Screencapping" + (isScreencapping ? " in progress" : "") : "")
                + (EastAngliaMapClient.connected ? "" : " - Not Connected")
            );

        printScreencap("Updating images (" + EastAngliaMapClient.getTime() + ")", false);

        final List<BufferedImage> images = new ArrayList<>();
        final List<String> names = new ArrayList<>();

        try
        {
            EastAngliaMapClient.frameSignalMap.prepForScreencap();

            EastAngliaMapClient.frameSignalMap.getPanels().stream().forEachOrdered(bp ->
            {
                BufferedImage image = new BufferedImage(SignalMapGui.BackgroundPanel.BP_DEFAULT_WIDTH, SignalMapGui.BackgroundPanel.BP_DEFAULT_HEIGHT, BufferedImage.TYPE_INT_ARGB);
                names.add(bp.getName().replace("/", " + "));

                Graphics2D g2d = image.createGraphics();
                g2d.clearRect(0, 0, SignalMapGui.BackgroundPanel.BP_DEFAULT_WIDTH, SignalMapGui.BackgroundPanel.BP_DEFAULT_HEIGHT);
                bp.invalidate();
                bp.paint(g2d);

                g2d.dispose();

                images.add(overlayImage(image, bp.getName().replace("/", " + ")));
            });
        }
        catch (Exception e) { EastAngliaMapClient.printThrowable(e, "Screencap"); }
        finally
        {
            isScreencapping = false;
            EastAngliaMapClient.frameSignalMap.finishScreencap();
        }

        new Thread(() ->
        {
            File screencapPath = new File(EastAngliaMapClient.storageDir, "images");
            int width  = SignalMapGui.BackgroundPanel.BP_DEFAULT_WIDTH + 2;
            int height = SignalMapGui.BackgroundPanel.BP_DEFAULT_HEIGHT + 2;

            try
            {
                BufferedImage bigImage = new BufferedImage(width*3 - 2, height*4 - 2, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = bigImage.createGraphics();
              //g2d.clearRect(0, 0, bigImage.getWidth(), bigImage.getHeight());
                g2d.setColor(EastAngliaMapClient.GREY);
                g2d.fillRect(0, 0, bigImage.getWidth(), bigImage.getHeight());

                g2d.drawImage(images.get(0),  0,       0,        null);
                g2d.drawImage(images.get(1),  width,   0,        null);
                g2d.drawImage(images.get(2),  width*2, 0,        null);
                g2d.drawImage(images.get(3),  0,       height,   null);
                g2d.drawImage(images.get(4),  width,   height,   null);
                g2d.drawImage(images.get(5),  width*2, height,   null);
                g2d.drawImage(images.get(6),  0,       height*2, null);
                g2d.drawImage(images.get(7),  width,   height*2, null);
                g2d.drawImage(images.get(8),  width*2, height*2, null);
                g2d.drawImage(images.get(9),  0,       height*3, null);
                g2d.drawImage(images.get(10), width,   height*3, null);
                g2d.drawImage(images.get(11), width*2, height*3, null);

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

            String[] imageURLs = {"Norwich", "CambridgeEN", "CambridgeCA", "Ipswich", "Clacton", "Colchester",
                "Harlow", "HackneyBrimsdown",  "Witham", "Shenfield", "Ilford", "LiverpoolStStratford", "All"};

            Collections.reverse(names);
            names.add("All");

            for (int i = 0; i < 13; i++)
            {
                try
                {
                    URLConnection con = new URL(EastAngliaMapClient.ftpBaseUrl + imageURLs[i] + "/image.png;type=i").openConnection();
                    con.setConnectTimeout(10000);
                    File imageFile = new File(screencapPath, names.get(i) + ".png");
                    try (FileInputStream in = new FileInputStream(imageFile); BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream()))
                    {
                        byte[] buffer = new byte[(int) imageFile.length()];
                        int read;
                        while ((read = in.read(buffer)) != -1)
                            out.write(buffer, 0, read);
                    }
                    catch (IOException e) {}

                    //printScreencap("    > " + names.get(i), false);
                }
                catch (IOException e) {}
            }

            if (EastAngliaMapClient.frameSignalMap != null)
                EastAngliaMapClient.frameSignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "")
                    +  " / v" + EastAngliaMapClient.DATA_VERSION + ")"
                    + (EastAngliaMapClient.autoScreencap ? " - Screencapping" + (isScreencapping ? " in progress" : "") : "")
                    + (EastAngliaMapClient.connected ? "" : " - Not Connected")
                );
            printScreencap("Finished updating website images in ~ " + (System.currentTimeMillis() - startTime) / 1000f + " secs", false);

            EastAngliaMapClient.clean();
        }, "uploadScreencaps").start();
    }

    public static void autoScreencap()
    {
        EastAngliaMapClient.autoScreencap = !EastAngliaMapClient.autoScreencap;

        if (EastAngliaMapClient.frameSignalMap != null)
            EastAngliaMapClient.frameSignalMap.setTitle("East Anglia Signal Map - Client (v" + EastAngliaMapClient.CLIENT_VERSION + (EastAngliaMapClient.isPreRelease ? " prerelease" : "")
                +  " / v" + EastAngliaMapClient.DATA_VERSION + ")"
                + (EastAngliaMapClient.autoScreencap ? " - Screencapping" + (isScreencapping ? " in progress" : "") : "")
                + (EastAngliaMapClient.connected ? "" : " - Not Connected")
            );
    }

    private static BufferedImage overlayImage(BufferedImage image, String name)
    {
        Graphics2D g2d = image.createGraphics();

        g2d.drawImage(logo, 70, 10, null);

        g2d.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 36));
        g2d.setColor(Color.WHITE);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.drawString(name, 145, 54);

        if (!EastAngliaMapClient.connected)
        {
            g2d.setFont(new Font("Verdana", Font.TRUETYPE_FONT, 14));
            g2d.drawString("Not connected to server since " + EastAngliaMapClient.sdf.format(new Date(MessageHandler.getLastMessageTime())), 150, 72);
        }

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
}