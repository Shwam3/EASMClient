package eastangliamapclient;

import eastangliamapclient.json.JSONParser;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class VersionChecker
{
    static void checkVersion()
    {
        checkClientVersion();
        checkMapVersion();
    }

    private static void checkClientVersion()
    {
        try
        {
            int localVersion = Integer.parseInt(EastAngliaMapClient.CLIENT_VERSION);
            int remoteVersion = -1;

            URL downloadLocation = null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://easignalmap.altervista.org/downloads/latest.txt").openStream())))
            {
                remoteVersion = Integer.parseInt(br.readLine());
                downloadLocation = new URL(br.readLine());
            }
            catch (MalformedURLException | NumberFormatException e) {}

            downloadLocation = (downloadLocation == null ? new URL("http://easignalmap.altervista.org/downloads/http://easignalmap.altervista.org/downloads/EastAngliaMapClient-v" + remoteVersion + ".exe") : downloadLocation);

            if (remoteVersion > localVersion)
            {
                EastAngliaMapClient.printStartup("New version available", false);
                if (JOptionPane.showConfirmDialog(null, "A new version of the client (v" + remoteVersion + ") is available\nDownload now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    File newFile = downloadFile(downloadLocation, new File(EastAngliaMapClient.class.getProtectionDomain().getCodeSource().getLocation().getPath()), false);

                    if (newFile == null || !newFile.exists())
                    {
                        JOptionPane.showMessageDialog(null, "Unable to download new version", "Updater", JOptionPane.ERROR_MESSAGE);
                    }
                    else if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
                    {
                        try { Runtime.getRuntime().exec("explorer.exe /select," + newFile.getAbsolutePath()); }
                        catch (IOException e) { JOptionPane.showMessageDialog(null, "Download complete\nFile located at " + newFile.getAbsolutePath(), "Updater", JOptionPane.INFORMATION_MESSAGE); }
                        System.exit(0);
                    }
                    else if (Desktop.isDesktopSupported())
                    {
                        try { Desktop.getDesktop().open(newFile); }
                        catch (IOException e) { JOptionPane.showMessageDialog(null, "Download complete\nFile located at " + newFile.getAbsolutePath(), "Updater", JOptionPane.INFORMATION_MESSAGE); }
                        System.exit(0);
                    }
                    else
                        JOptionPane.showMessageDialog(null, "Download complete\nFile located at " + newFile.getAbsolutePath(), "Updater", JOptionPane.INFORMATION_MESSAGE);
                }

                EastAngliaMapClient.printStartup("New version not downloaded", false);
            }
            else
            {
                if (remoteVersion < Integer.parseInt(EastAngliaMapClient.CLIENT_VERSION))
                    EastAngliaMapClient.isPreRelease = true;

                EastAngliaMapClient.printStartup("Client up to date", false);
            }
        }
        catch (FileNotFoundException e) { EastAngliaMapClient.printStartup("Cant find remote version file", true); }
        catch (IOException e) { EastAngliaMapClient.printStartup("Error reading remote version file", true); }
    }

    private static void checkMapVersion()
    {
        int newVersion = 0;

        try
        {
            File mapFile = new File(EastAngliaMapClient.storageDir, "data" + File.separator + "signalmap.json");
            URL archiveLocation = null;

            int versionLocal = -1;
            int versionRemote = -1;

            if (mapFile.exists())
            {
                StringBuilder jsonString = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(mapFile)))
                {
                    String line;
                    while ((line = br.readLine()) != null)
                        jsonString.append(line);
                }
                catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Updater"); }

                Map<String, Object> json = (Map<String, Object>) JSONParser.parseJSON(jsonString.toString());

                if (json.containsKey("version"))
                    versionLocal = (int) ((long) json.get("version"));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("http://easignalmap.altervista.org/downloads/latestMap.txt").openStream())))
            {
                versionRemote = Integer.parseInt(br.readLine());
                archiveLocation = new URL(br.readLine());
            }
            catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Updater"); }

            newVersion = versionLocal;

            if (versionRemote > versionLocal)
            {
                File dataFolder = mapFile.getParentFile();

                EastAngliaMapClient.printStartup("Map update available (v" + versionRemote + ")", false);

                if (JOptionPane.showConfirmDialog(null, "A new version of the map files (v" + versionRemote+ ") is available\nDownload now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    File updateArchive = downloadFile(archiveLocation, mapFile.getParentFile(), true);

                    if (updateArchive != null && updateArchive.exists())
                    {
                        if (dataFolder.listFiles() != null)
                            for (File file : dataFolder.listFiles())
                                if (!file.equals(updateArchive))
                                    file.delete();

                        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(updateArchive))))
                        {
                            ZipEntry entry;
                            while ((entry = zis.getNextEntry()) != null)
                            {
                                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(EastAngliaMapClient.newFile(new File(dataFolder, entry.getName())))))
                                {
                                    int read;
                                    byte[] data = new byte[8192];
                                    while ((read = zis.read(data, 0, 8192)) != -1)
                                        bos.write(data, 0, read);
                                }
                            }
                        }
                        catch (IOException e)
                        {
                            EastAngliaMapClient.printThrowable(e, "Updater");
                        }

                        updateArchive.delete();

                        EastAngliaMapClient.printStartup("Downloaded new map files (v" + versionRemote + ")", false);
                    }
                    else
                    {
                        EastAngliaMapClient.printStartup("Unable to download map files (file = " + String.valueOf(updateArchive) + ")", true);
                    }

                    newVersion = versionRemote;
                }

                if (!mapFile.exists())
                    JOptionPane.showMessageDialog(null, "Unable to download map files.\nPlease go to \"" + archiveLocation.toExternalForm() + "\"\n and extract the files into \"" + dataFolder + "\".", "Updater", JOptionPane.ERROR_MESSAGE);
            }
            else
                EastAngliaMapClient.printStartup("Map files up to date", false);
        }
        catch (Exception e)
        {
            EastAngliaMapClient.printThrowable(e, "Updater");
            JOptionPane.showMessageDialog(null, "Unable to update map files\n" + e.toString(), "Updater", JOptionPane.ERROR_MESSAGE);
        }

        EastAngliaMapClient.DATA_VERSION = Integer.toString(newVersion);
    }

    private static File downloadFile(URL location, File destinationFolder, boolean forceOverwrite)
    {
        final Downloader downloader = new Downloader(location, destinationFolder, forceOverwrite);

        try
        {
            EventQueue.invokeAndWait(() ->
            {
                final JDialog dialog = new JDialog((Frame) null, "Updater", true);
                dialog.setLayout(null);
                dialog.setResizable(false);

                final JProgressBar progress = new JProgressBar();
                progress.setIndeterminate(true);
                progress.setSize(250, 15);
                progress.setLocation(10, 25);
                dialog.add(progress);

                final JLabel lbl = new JLabel("Downloading...");
                lbl.setSize(250, 15);
                lbl.setLocation(10, 5);
                dialog.add(lbl);

                final JButton but = new JButton("Cancel");
                but.setLocation(97, 45);
                but.setSize(75, 23);
                but.addActionListener((ActionEvent e) ->
                {
                    lbl.setText("Cancelling... " + downloader.getProgressString());
                    downloader.cancel();
                    dialog.dispose();
                });
                dialog.add(but);

                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.setPreferredSize(new Dimension(270+6, 83+28));
                dialog.pack();
                dialog.setLocationRelativeTo(null);
                dialog.addWindowListener(new WindowAdapter()
                {
                    @Override
                    public void windowClosing(WindowEvent e)
                    {
                        dialog.dispose();
                        downloader.cancel();
                    }
                });

                EastAngliaMapClient.printStartup("Downloading file from \"" + location.toExternalForm() + "\" to \"" + destinationFolder.getAbsolutePath() + "\"", false);

                new Thread(() -> { dialog.setVisible(true); }).start();

                new Thread(() -> { downloader.download(); }).start();

                /*try { Thread.sleep(5); }
                catch (InterruptedException e) {}*/

                while (true)
                {
                    lbl.setText("Downloading... " + downloader.getProgressString());

                    if (progress.isIndeterminate() != progress.getValue() >= progress.getMaximum())
                        progress.setIndeterminate(progress.getValue() >= progress.getMaximum());

                    if (!progress.isIndeterminate())
                        progress.setValue((int) downloader.getProgress());

                    try { Thread.sleep(5); }
                    catch (InterruptedException e) {}

                    if (downloader.getStatus() == Downloader.COMPLETE || downloader.getStatus() == Downloader.CANCELLED || downloader.getStatus() == Downloader.ERROR)
                        break;
                }

                dialog.setVisible(false);

                if (downloader.getStatus() == Downloader.ERROR && !downloader.getError().isEmpty())
                    JOptionPane.showMessageDialog(null, "The download could not be completed\n" + downloader.getError(), "Updater", JOptionPane.ERROR_MESSAGE);
            });
        }
        catch (InterruptedException | InvocationTargetException e) { EastAngliaMapClient.printThrowable(e, "Updater"); }

        return downloader.getFile();
    }
}