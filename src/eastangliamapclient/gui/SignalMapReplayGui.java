package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.SignalMapReplayController;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerDateModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;

public class SignalMapReplayGui extends JDialog
{
    private final Map<String, JToggleButton> components;

    public SignalMapReplayGui()
    {
        super(EastAngliaMapClient.frameSignalMap.frame, false);
        setType(Window.Type.UTILITY);
        setLayout(new BorderLayout(5, 5));
        setTitle("Replay");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        components = new HashMap<>();

        JPanel controls = new JPanel();
        controls.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Controls"));
        controls.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JToggleButton pause        = new JToggleButton("||");
        JToggleButton playForward  = new JToggleButton(">");
        JToggleButton playBackward = new JToggleButton("<");
        JSlider speed = new JSlider(1, 60, 1);
        speed.setPreferredSize(new Dimension(128, 30));
        speed.setSnapToTicks(true);
        speed.setPaintTicks(true);
        speed.setMinorTickSpacing(1);
        speed.setMajorTickSpacing(10);
        speed.setToolTipText("Speed (" + speed.getValue() + "x)");
        speed.addChangeListener((ChangeEvent e) ->
        {
            if (!speed.getValueIsAdjusting())
            {
                SignalMapReplayController.setPlaySpeedModifier(speed.getValue());
                speed.setToolTipText("Speed (" + speed.getValue() + "x)");
            }
        });

        ActionListener listener = evt ->
        {
            JComponent src = (JComponent) evt.getSource();

            pause.setSelected(src == pause);
            playForward.setSelected(src == playForward);
            playBackward.setSelected(src == playBackward);

            if (src == pause)
                SignalMapReplayController.setPlayMode(SignalMapReplayController.MODE_PAUSE);
            else if (src == playForward)
                SignalMapReplayController.setPlayMode(SignalMapReplayController.MODE_PLAY_FORWARD);
            else if (src == playBackward)
                SignalMapReplayController.setPlayMode(SignalMapReplayController.MODE_PLAY_BACKWARD);
        };

        pause.addActionListener(listener);
        playForward.addActionListener(listener);
        playBackward.addActionListener(listener);

        controls.add(playBackward);
        controls.add(pause);
        controls.add(playForward);
        controls.add(speed);

        JPanel dates = new JPanel();
        dates.setLayout(new BorderLayout(5, 5));

        File replayDir = new File(EastAngliaMapClient.storageDir, "Logs" + File.separator + "ReplaySaves" + File.separator);
        List<String> usableDates = Arrays.asList(replayDir.list((File dir, String name) -> name.endsWith(".json")));
        Collections.sort(usableDates, (String o1, String o2) ->
        {
            String[] o1Bits = o1.replace(".json", "").split("-");
            String[] o2Bits = o2.replace(".json", "").split("-");

            return ((Integer.parseInt(o2Bits[2]) - Integer.parseInt(o1Bits[2])) * 10000)
                 + ((Integer.parseInt(o2Bits[1]) - Integer.parseInt(o1Bits[1])) * 100)
                 + ((Integer.parseInt(o2Bits[0]) - Integer.parseInt(o1Bits[0])));
        });

        JComboBox<String> dateSelector = new JComboBox<>(usableDates.toArray(new String[0]));
        dates.add(dateSelector, BorderLayout.CENTER);

        JSpinner time = new JSpinner();
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(-3600000);
        Date earliestDate = calendar.getTime();
        calendar.add(Calendar.SECOND, 86399);
        Date latestDate = calendar.getTime();
        SpinnerDateModel model = new SpinnerDateModel(earliestDate, earliestDate, latestDate, Calendar.SECOND);
        time.setModel(model);
        time.setEditor(new JSpinner.DateEditor(time, "HH:mm:ss"));

        dates.add(time, BorderLayout.WEST);

        JButton startStop = new JButton("Start");
        startStop.setPreferredSize(new Dimension(57, 23));
        startStop.addActionListener(evt ->
        {
            if (startStop.getText().equals("Start"))
            {
                startStop.setText("Loading");
                dateSelector.setEnabled(false);

                String[] chosenDateBits = String.valueOf(dateSelector.getSelectedItem()).replace(".json", "").split("-");

                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(chosenDateBits[0]));
                cal.set(Calendar.MONTH, Integer.parseInt(chosenDateBits[1]) - 1);
                cal.set(Calendar.YEAR, Integer.parseInt(chosenDateBits[2]));
                SignalMapReplayController.initReplayForDate(cal.getTime());

                cal.setTime((Date) time.getValue());
                SignalMapReplayController.requestOffset(cal.get(Calendar.HOUR_OF_DAY) * 60 * 60 + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.SECOND));

                SignalMapReplayController.setPlayMode(playForward.isSelected() ? SignalMapReplayController.MODE_PLAY_FORWARD : (playBackward.isSelected() ? SignalMapReplayController.MODE_PLAY_BACKWARD : SignalMapReplayController.MODE_PAUSE));

                boolean isActive = SignalMapReplayController.isActive();
                startStop.setText(isActive ? "Stop": "Start");
                dateSelector.setEnabled(!isActive);
                time.setEnabled(!isActive);
                updateMode();
                components.values().stream().forEach(c -> c.setEnabled(isActive));
                speed.setEnabled(isActive);
            }
            else
            {
                startStop.setText("Loading");

                SignalMapReplayController.setPlayMode(SignalMapReplayController.MODE_STOP);
                dateSelector.setEnabled(true);
                time.setEnabled(true);
                updateMode();
                components.values().stream().forEach(c -> c.setEnabled(false));
                speed.setEnabled(false);

                startStop.setText("Start");

            }
        });
        dates.add(startStop, BorderLayout.EAST);

        components.put("pause", pause);
        components.put("playForward", playForward);
        components.put("playBackward", playBackward);
        components.values().stream().forEach(c -> c.setEnabled(false));
        speed.setEnabled(false);

        add(dates, BorderLayout.NORTH);
        add(controls, BorderLayout.CENTER);

        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        pack();
        setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);

        setVisible0(true);
    }

    public void setVisible0(boolean visible)
    {
        if (visible)
            EventQueue.invokeLater(() -> setVisible(true));
        else
            setVisible(false);
    }

    public void updateMode()
    {
        int mode = SignalMapReplayController.getPlayMode();

        components.get("pause").setSelected(mode == SignalMapReplayController.MODE_PAUSE);
        components.get("playForward").setSelected(mode == SignalMapReplayController.MODE_PLAY_FORWARD);
        components.get("playBackward").setSelected(mode == SignalMapReplayController.MODE_PLAY_BACKWARD);
    }
}