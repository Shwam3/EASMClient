package eastangliamapclient.gui;

import eastangliamapclient.Berth;
import eastangliamapclient.Berths;
import eastangliamapclient.EastAngliaMapClient;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

public class ListDialog
{
    private JDialog dialog;

    public ListDialog(final Berth berth, String title, String message, List<String> list)
    {
        if (list == null)
            list = new ArrayList<>();

        dialog = new JDialog();
        dialog.setIconImage(EastAngliaMapClient.frameSignalMap.frame.getIconImage());

        dialog.setTitle(title);
        dialog.setPreferredSize(new Dimension(305, 319));
        dialog.setResizable(true);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationByPlatform(true);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setModalityType(ModalityType.APPLICATION_MODAL);

        if (berth != null)
        {
            dialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent evt)
                {
                    if (Berths.getOpaqueBerth() == berth)
                        Berths.setOpaqueBerth(null);
                }
            });
        }

        JPanel pnl = new JPanel(null);
        pnl.setLayout(new BorderLayout(10, 10));
        pnl.setBorder(new EmptyBorder(10, 10, 10, 10));

        if (message != null && !message.equals(""))
        {
            JLabel lblMessage = new JLabel(message);
            lblMessage.setVerticalAlignment(SwingConstants.CENTER);
            lblMessage.setHorizontalAlignment(SwingConstants.LEFT);
            lblMessage.setToolTipText(message);
            pnl.add(lblMessage, BorderLayout.NORTH);
        }

        JList<String> jList = new JList<>(list.toArray(new String[0]));
        jList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        jList.setVisibleRowCount(5);
        jList.setLayoutOrientation(JList.VERTICAL);
        JScrollPane jListSP = new JScrollPane(jList);
        jListSP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jListSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pnl.add(jListSP, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.setPreferredSize(new Dimension(73, 23));
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPnl.add(okButton);
        pnl.add(buttonPnl, BorderLayout.SOUTH);

        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.add(pnl);
        dialog.pack();
        dialog.setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);

        dialog.setVisible(true);
    }
}