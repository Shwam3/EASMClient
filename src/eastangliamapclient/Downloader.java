package eastangliamapclient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Downloader
{
    private static final int MAX_BUFFER_SIZE = 8192;

    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private final URL url;
    private File destination;
    private int size; // in bytes
    private int downloaded; // in bytes
    private int status;
    private String errorString = "";

    public Downloader(URL url, File destinationFolder, boolean forceOverwrite)
    {
        this.url = url;
        size = -1;
        downloaded = 0;
        status = PAUSED;
        destination = new File(destinationFolder, url.getFile().substring(url.getFile().lastIndexOf('/') + 1));

        if (destination.exists())
        {
            if (forceOverwrite)
            {
                destination.delete();
            }
            else
            {
                int opt = JOptionPane.showConfirmDialog(null, destination.getAbsolutePath() + " already exists, overwrite?", "Updater", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == JOptionPane.NO_OPTION)
                {
                    JFileChooser chooser = new JFileChooser(destinationFolder);
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
                        destination = chooser.getSelectedFile();
                }
                else if (opt != JOptionPane.YES_OPTION)
                    error("");
            }
        }
        else
        {
            try
            {
                EastAngliaMapClient.newFile(destination);
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(null, "Unable to create new file\n" + e.toString(), "Downloader", JOptionPane.ERROR_MESSAGE);
                EastAngliaMapClient.printThrowable(e, "Downloader");
            }
        }
    }

    public String getUrl() { return url.toExternalForm(); }
    public int    getSize() { return size; }
    public File   getFile() { return destination; }
    public String getProgressString() { return String.format("%s%% (%sKB of %sKB)", (int) Math.floor(((float) downloaded / size) * 100), (int) Math.floor(downloaded / 1024), (int) Math.floor(size / 1024)); }
    public float  getProgress() { return ((float) downloaded / size) * 100; }
    public int    getStatus() { return status; }
    public String getError() { return errorString; }
    public void   pause() { status = PAUSED; }
    public void   resume() { download(); }
    public void   cancel() { status = CANCELLED; destination.deleteOnExit(); }
    private void  error(String error)
    {
        status = ERROR;
        this.errorString = String.valueOf(error);
    }

    public void download()
    {
        if (status == CANCELLED || status == ERROR || status == DOWNLOADING)
            return;

        status = DOWNLOADING;

        new Thread(() ->
        {
            RandomAccessFile file = null;
            InputStream stream = null;

            try
            {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
                connection.connect();

                if (connection.getResponseCode() / 100 != 2)
                    error("Server responded with " + connection.getResponseCode() + " " + connection.getResponseMessage());

                int contentLength = connection.getContentLength();
                if (contentLength < 1)
                    error("No content");

                if (size == -1)
                    size = contentLength;

                file = new RandomAccessFile(destination, "rw");
                file.seek(downloaded);

                stream = connection.getInputStream();
                while (status == DOWNLOADING)
                {
                    byte buffer[];
                    if (size - downloaded > MAX_BUFFER_SIZE)
                        buffer = new byte[MAX_BUFFER_SIZE];
                    else
                        buffer = new byte[size - downloaded];

                    int read = stream.read(buffer);
                    if (read == -1)
                        break;

                    file.write(buffer, 0, read);
                    downloaded += read;
                }

                if (status == DOWNLOADING)
                {
                    if (size <= downloaded)
                        status = COMPLETE;
                    else
                        status = ERROR;
                }
            }
            catch (Exception e) { error(e.toString()); }
            finally
            {
                if (file != null)
                    try { file.close(); }
                    catch (Exception e) {}

                if (stream != null)
                    try { stream.close(); }
                    catch (Exception e) {}
            }
        }).start();
    }

    @Override
    public String toString()
    {
        return String.format("[url=%s,destination=%s,status=%s,downloaded=%s,size=%s,errorString=%s]", url.toExternalForm(), destination.getAbsoluteFile(), status, downloaded, size, errorString);
    }
}