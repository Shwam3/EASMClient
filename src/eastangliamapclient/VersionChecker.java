package eastangliamapclient;

import eastangliamapclient.json.JSONParser;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class VersionChecker
{
    public static boolean checkVersion()
    {
        checkClientVersion();
        return checkDataVersion();
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

            downloadLocation = (downloadLocation == null ? new URL("http://easignalmap.altervista.org/downloads/EastAngliaMapClient-v" + remoteVersion + ".exe") : downloadLocation);

            if (remoteVersion > localVersion)
            {
                EastAngliaMapClient.printStartup("New version available", false);
                if (JOptionPane.showConfirmDialog(null, "A new version of the client (v" + remoteVersion + ") is available\nDownload now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    File newFile = downloadFile(downloadLocation, EastAngliaMapClient.storageDir, false);

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

    private static boolean checkDataVersion()
    {
        boolean hasUpdated = false;
        int newVersion = 0;

        try
        {
            File dataFile = new File(EastAngliaMapClient.storageDir, "data" + File.separator + "signalmap.json");

            int versionLocal = -1;
            int versionRemote = -1;

            if (dataFile.exists())
            {
                StringBuilder jsonString = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(dataFile)))
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

            String newmapJSON = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/Shwam3/EASMData/master/signalmap.json").openStream())))
            {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    sb.append(line).append("\r\n");
                newmapJSON = sb.toString();

                Map<String, Object> json = (Map<String, Object>) JSONParser.parseJSON(newmapJSON.toString());

                if (json.containsKey("version"))
                    versionLocal = (int) ((long) json.get("version"));
            }
            catch (IOException e) { EastAngliaMapClient.printThrowable(e, "Updater"); }

            newVersion = versionLocal;

            if (versionRemote > versionLocal)
            {
                EastAngliaMapClient.printStartup("Data update available (v" + versionRemote + ")", false);

                if (JOptionPane.showConfirmDialog(null, "A new version of the data files is available (v" + versionRemote+ ")\nDownload now?", "Updater", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    File backup = new File(EastAngliaMapClient.storageDir, "data" + File.separator + "oldmap.json");
                    if (dataFile.exists())
                        dataFile.renameTo(backup);
                    dataFile.getParentFile().mkdirs();
                    dataFile.createNewFile();

                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile)))
                    {
                        bw.write(newmapJSON);
                    }
                    catch (Exception e) { e.printStackTrace(); dataFile.delete(); backup.renameTo(dataFile); }

                    Map<String, Object> json = (Map<String, Object>) JSONParser.parseJSON(newmapJSON.toString());
                    for (Map<String, Object> obj : (List<Map<String, Object>>)json.get("signalMap"))
                    {

                    }

                    newVersion = versionRemote;
                }
            }
            else
                EastAngliaMapClient.printStartup("Data files up to date", false);
        }
        catch (Exception e)
        {
            EastAngliaMapClient.printThrowable(e, "Updater");
            JOptionPane.showMessageDialog(null, "Unable to update data files\n" + e.toString(), "Updater", JOptionPane.ERROR_MESSAGE);
        }

        EastAngliaMapClient.DATA_VERSION = Integer.toString(newVersion);
        return hasUpdated;
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
                but.addActionListener(e ->
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

                new Thread(() -> dialog.setVisible(true)).start();

                new Thread(() -> downloader.download()).start();

                /*try { Thread.sleep(5); }
                catch (InterruptedException e) {}*/

                while (true)
                {
                    lbl.setText("Downloading... " + downloader.getProgressString());

                    if (progress.isIndeterminate() != progress.getValue() >= progress.getMaximum())
                        progress.setIndeterminate(progress.getValue() >= progress.getMaximum());

                    if (!progress.isIndeterminate())
                        progress.setValue((int) downloader.getProgress());

                    try { Thread.sleep(10); }
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