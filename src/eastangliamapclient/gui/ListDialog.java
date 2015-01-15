package eastangliamapclient.gui;

import eastangliamapclient.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
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
            dialog.addWindowListener(new WindowAdapter()
            {
                @Override
                public void windowClosed(WindowEvent evt)
                {
                    if (berth != null)
                    {
                        EventHandler.getRidOfBerth();
                    }
                }
            });

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
        okButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent evt)
            {
                dialog.dispose();
            }
        });
        JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        buttonPnl.add(okButton);
        pnl.add(buttonPnl, BorderLayout.SOUTH);

        dialog.getRootPane().registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);

        dialog.getRootPane().registerKeyboardAction(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                dialog.dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_FOCUSED);

        dialog.add(pnl);
        dialog.pack();
        dialog.setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);

        okButton.requestFocusInWindow();
        dialog.setVisible(true);
    }
}