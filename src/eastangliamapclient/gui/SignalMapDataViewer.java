package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import eastangliamapclient.gui.mapelements.Berths;
import eastangliamapclient.gui.mapelements.Points;
import eastangliamapclient.gui.mapelements.Signals;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Objects;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

public class SignalMapDataViewer extends JDialog
{
    public final JList<String> listCClass = new JList<>(new DefaultListModel<>());
    public final JList<String> listSClass = new JList<>(new DefaultListModel<>());

    private boolean hideBlanks = false;
    private String  filterString = "";

    public SignalMapDataViewer()
    {
        super(EastAngliaMapClient.frameSignalMap.frame);

        setTitle("East Anglia Signal Map - Data");
        setPreferredSize(new Dimension(600, 400));
        setMinimumSize(new Dimension(715, 350));
        setLayout(new BorderLayout());
        setLocationByPlatform(true);
        setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.MODELESS);
        setIconImage(EastAngliaMapClient.frameSignalMap.frame.getIconImage());

        JPanel mainPanel = new JPanel(new BorderLayout());

        Font font = new Font("Monospaced", 0, 12);
        listCClass.setFont(font);
        listSClass.setFont(font);
        listCClass.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listCClass.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane jspCClass = new JScrollPane(listCClass);
        JScrollPane jspSClass = new JScrollPane(listSClass);
        jspCClass.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspSClass.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jspCClass.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jspSClass.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        JPanel pnlCClass = new JPanel(new BorderLayout());
        JPanel pnlSClass = new JPanel(new BorderLayout());
        pnlCClass.add(jspCClass);
        pnlSClass.add(jspSClass);
        pnlCClass.setBorder(new TitledBorder(new EtchedBorder(), "C Class"));
        pnlSClass.setBorder(new TitledBorder(new EtchedBorder(), "S Class"));
        JSplitPane pnlSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pnlSplit.add(pnlCClass, JSplitPane.LEFT);
        pnlSplit.add(pnlSClass, JSplitPane.RIGHT);
        pnlSplit.setDividerLocation(0.5);
        pnlSplit.setResizeWeight(0.5);
        mainPanel.add(pnlSplit, BorderLayout.CENTER);

        JPanel pnlFilters = new JPanel();
        pnlFilters.setBorder(new TitledBorder(new EtchedBorder(), "Filters"));
        pnlFilters.setPreferredSize(new Dimension(350, 60));
        JCheckBox cbBlank = new JCheckBox("Hide Blank berths");
        cbBlank.setSelected(hideBlanks);
        cbBlank.addItemListener(evt -> { hideBlanks = evt.getStateChange() == ItemEvent.SELECTED; updateData(); });
        pnlFilters.add(cbBlank);
        JLabel lblTextFilter = new JLabel("Search:");
        pnlFilters.add(lblTextFilter);
        JTextField jtfTextFilter = new JTextField();
        jtfTextFilter.setPreferredSize(new Dimension(200, 20));
        jtfTextFilter.addKeyListener(new KeyAdapter() { public void keyReleased(KeyEvent evt) {
            ((JTextField) evt.getComponent()).setText(((JTextField) evt.getComponent()).getText().toUpperCase());
            filterString = (((JTextField) evt.getComponent()).getText()).trim();

            super.keyReleased(evt);

            updateData();
        }});
        pnlFilters.add(jtfTextFilter, BorderLayout.CENTER);
        mainPanel.add(pnlFilters, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> setVisible0(false));
        okButton.setPreferredSize(new Dimension(73, 23));
        okButton.setOpaque(false);

        JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPnl.add(okButton);
        buttonPnl.setOpaque(false);
        add(buttonPnl, BorderLayout.SOUTH);

        getRootPane().registerKeyboardAction(e -> setVisible0(false), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> setVisible0(false), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();
        updateData();
        setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);
    }

    public void setVisible0(boolean visible)
    {
        if (visible)
            EventQueue.invokeLater(() -> setVisible(true));
        else
            setVisible(false);
    }

    public void updateData()
    {
        setTitle("Data" + (filterString.isEmpty() ? "" : " - " + filterString));

        DefaultListModel<String> modelCClass = new DefaultListModel<>();
        EastAngliaMapClient.DataMap.keySet().stream()
                .filter(id -> !id.startsWith("XX"))
                .filter(id -> !id.contains(":"))
                .filter(id -> id.length() == 6)
                .filter(id -> elementFitsFilter(id))
                .filter(id -> hideBlanks ? !String.valueOf(EastAngliaMapClient.DataMap.get(id)).replace("null", "").trim().equals("") : true)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEachOrdered(id -> modelCClass.addElement(String.format("%s: '%s' (%s)", id, EastAngliaMapClient.DataMap.get(id).length() == 4 ? EastAngliaMapClient.DataMap.get(id) : "    ", Berths.berthExists(id) ? Berths.getBerth(id).getParent().getName() : "not shown")));

        if (modelCClass.isEmpty())
        {
            EastAngliaMapClient.DataMap.keySet().stream()
                .filter(id -> !id.startsWith("XX"))
                .filter(id -> !id.contains(":"))
                .filter(id -> id.length() == 6)
                .filter(id -> hideBlanks ? !String.valueOf(EastAngliaMapClient.DataMap.get(id)).replace("null", "").trim().equals("") : true)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEachOrdered(id -> modelCClass.addElement(String.format("%s: '%s' (%s)", id, EastAngliaMapClient.DataMap.get(id).length() == 4 ? EastAngliaMapClient.DataMap.get(id) : "    ", Berths.berthExists(id) ? Berths.getBerth(id).getParent().getName() : "not shown")));
        }
        listCClass.setModel(modelCClass);

        DefaultListModel<String> modelSClass = new DefaultListModel<>();
        EastAngliaMapClient.DataMap.keySet().stream()
                .filter(id -> !id.startsWith("XX"))
                .filter(id -> id.length() != 6 || id.contains(":"))
                .filter(id -> elementFitsFilter(id))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEachOrdered(id -> modelSClass.addElement(String.format("%s: %s (%s)",
                        id,
                        EastAngliaMapClient.DataMap.get(id),
                        Signals.getSignal(id) != null && !Points.pointExists(id) ? "not shown" :
                            (Signals.getSignal(id) != null ? Signals.getSignal(id)[0].getDescription() + " (sig - " + Signals.getSignal(id)[0].getType().name() + ")" :
                                (Points.pointExists(id) ? Points.getPoints(id).get(0).getDescription() + " (pts - " + Points.getPoints(id).get(0).getType0().name() + "/" + Points.getPoints(id).get(0).getType1().name() + ")" :
                                    "not shown")))));

        if (modelSClass.isEmpty())
        {
            EastAngliaMapClient.DataMap.keySet().stream()
                .filter(id -> !id.startsWith("XX"))
                .filter(id -> id.length() != 6 || id.contains(":"))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .forEachOrdered(id -> modelSClass.addElement(id + ": " + Objects.requireNonNull(EastAngliaMapClient.DataMap.get(id), id + " is null")));
        }
        listSClass.setModel(modelSClass);
    }

    private boolean elementFitsFilter(String element)
    {
        return element.toLowerCase().contains(filterString.toLowerCase());
    }
}