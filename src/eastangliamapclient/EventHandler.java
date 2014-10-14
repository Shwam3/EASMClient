package eastangliamapclient;

import eastangliamapclient.gui.BerthContextMenu;
import eastangliamapclient.gui.HelpDialog;
import eastangliamapclient.gui.SignalMap;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class EventHandler
{
    public static Berth tempOpaqueBerth = null;
    public static BerthContextMenu berthContextMenu = null;

    //<editor-fold defaultstate="collapsed" desc="Update Last Message Clocks">
    public static void updateLastMsgClock()
    {
        Date currentTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

        EastAngliaMapClient.lastMessageTime = currentTime.getTime();

        for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
        {
            lbl.setText(sdf.format(currentTime));
        }
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
        JLabel evtLabel = (JLabel)evt.getComponent();

        if (evtLabel.getText().equals("NO  DATA") && EastAngliaMapClient.connect)
            lastMessageNoData();

        for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
        {
            lbl.setForeground(EastAngliaMapClient.GREEN);
            lbl.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Last Message (No Data)">
    public static void lastMessageNoData()
    {
        if (EastAngliaMapClient.connect)
        {
            for (JLabel lbl : EastAngliaMapClient.SignalMap.lastMsgLbls)
            {
                lbl.setText("NO  DATA");
                lbl.setForeground(Color.red);
                lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
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
    private static boolean berthEvent(boolean forceCancel, boolean forceInterpose, boolean toggleProblem, boolean properHeadcode, Berth berth)
    {
        EastAngliaMapClient.blockKeyInput = true;

        if (toggleProblem && !forceCancel && !forceInterpose)
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
                        berth.getHeadcode(), berth.getHeadcode().matches("[0-9]{3}[A-Z]") ? "" : "&area=" + berth.getBerthDescription().substring(0, 2))));

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
                            EastAngliaMapClient.SignalMap = new SignalMap().readFromMap();

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

    //<editor-fold defaultstate="collapsed" desc="Options Menu Click">
    public static void optionMenuItemClick(ActionEvent evt)
    {
        switch (evt.getActionCommand())
        {
            case "Toggle Opacity":
                Berths.toggleBerthsOpacities();
                break;

            case "Toggle Visibility":
                Berths.toggleBerthVisibilities();
                break;

            case "Toggle Descriptions":
                Berths.toggleBerthDescriptions();
                break;

            case "Refresh Data":
                EastAngliaMapClient.handler.requestAll();
                break;

            case "Reconnect":
                EastAngliaMapClient.reconnect();
                break;

            case "Reset Window":
                EastAngliaMapClient.refresh();
                break;

            case "Train History":
                String UUID = JOptionPane.showInputDialog(EastAngliaMapClient.SignalMap.frame, "Enter Train UUID:", "Train History", JOptionPane.QUESTION_MESSAGE);

                if (UUID != null)
                    if (UUID.length() >= 5 && UUID.matches("[0-9]+"))
                        EastAngliaMapClient.handler.requestHistoryOfTrain(UUID);
                    else
                        JOptionPane.showMessageDialog(EastAngliaMapClient.SignalMap.frame, "'" + UUID + "' is not a valid train UUID", "Error", JOptionPane.WARNING_MESSAGE);
                break;
        }
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
                    Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
                    Robot rob = new Robot();
                    rob.mouseMove(mouseLoc.x, mouseLoc.y);
                }
                catch (AWTException e) {}
            }
        }, 30000, 30000);

        Timer keepAlive = new Timer("keepAlive", true);
        keepAlive.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                SimpleDateFormat sdf = new SimpleDateFormat("mm:ss.SS");
                String timeoutTime = sdf.format(System.currentTimeMillis() - EastAngliaMapClient.lastMessageTime);

                /*boolean connected = false;
                try
                {
                    connected = StompConnectionHandler.isConnected() && !StompConnectionHandler.isTimedOut();
                }
                finally
                {
                    if (!connected)
                    {
                        EastAngliaMapClient.printAll(String.format("Connection lost for %s, reconnecting...", timeoutTime));
                        EventHandler.lastMessageNoData();

                        try
                        {
                            if (StompConnectionHandler.client != null)
                                StompConnectionHandler.client.disconnect();

                            if (StompConnectionHandler.connect())
                            {
                                EastAngliaMapClient.printAll("Reconnected");
                                reconnectAttempts = 0;
                            }
                            else
                            {
                                EastAngliaMapClient.printAll("Unable to reconnect (attempt " + ++reconnectAttempts + ")");
                            }
                        }
                        catch (LoginException e)
                        {
                            EastAngliaMapClient.printAll("Unable to reconnect (attempt " + ++reconnectAttempts + "), may already be connected");
                        }
                    }
                    else
                    {
                        reconnectAttempts = 0;
                    }
                }*/
            }
        }, 30000, 10000);

        javax.swing.Timer clockTimer = new javax.swing.Timer(250, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                for (JLabel lbl : EastAngliaMapClient.SignalMap.clockLbls)
                {
                    lbl.setText(EastAngliaMapClient.getTime());
                }
            }
        });
        clockTimer.start();
    }
}