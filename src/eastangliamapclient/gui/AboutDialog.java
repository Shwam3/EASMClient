/*package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class AboutDialog extends JDialog
{
    public AboutDialog()
    {
        setTitle("About");
        setPreferredSize(new Dimension(600, 458));
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationByPlatform(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        JLabel mainText = new JLabel("");
        //        "<html>"
        //            + "<p><b>Navigation:</b></p>"
        //            + "<p>Use the <i>function</i> keys (F1, F2 ...) (CTRL 13-24 SHIFT 25-36 CTRL+SHIFT 36-48) to change the tabs as well as the arrow keys.</p>"
        //            + "<br>"
        //            + "<p><b>Train headcodes:</b></p>"
        //            + "<p>Left clicking on a green headcode will bring up an RTT search for the train.</p>"
        //            + "<br>"
        //            + "<p><b>Key Bindings:</b></p>"
        //            + "<p>'O' - Toggle berth opacity mode</p>"
        //            + "<p>'D' - Toggle berth id's</p>"
        //            + "<p>'B' - Toggle berth visibility</p>"
        //            + "<p>'S' - Toggle signals visibility</p>"
        //            + "<p>'H' - Opens this dialog</p>"
        //            + "<br>"
        //            + "<p>Note: This program may regularly disconnect/be unavailable as the server program is run on my local PC which may not always "
        //            +    "be on/available. In such cases I highly recommend using opentraintimes.com or railcam.org.uk as alternatives.</p>"
        //            + "<br>"
        //            + "<p>The source code is available at http://www.github.com/Shwam3/EastAngliaSignalMapClient"
        //            +    " and http://www.github.com/Shwam3/EastAngliaSignalMapServer</p>"
        //            + "<p>&copy; Cameron Bird 2014</p>"
        //        + "</html>");

        mainText.setOpaque(true);
        mainText.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12));
        mainText.setForeground(EastAngliaMapClient.GREEN);
        mainText.setBackground(Color.BLACK);
        mainText.setVerticalAlignment(SwingConstants.TOP);
        mainText.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        add(mainText, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> { dispose(); EastAngliaMapClient.blockKeyInput = false; });
        okButton.setPreferredSize(new Dimension(73, 23));
        okButton.setOpaque(false);

        //JButton gitButton = new JButton("GitHub...");
        //gitButton.addActionListener(evt ->
        //{
        //    try { Desktop.getDesktop().browse(new URI("https://www.github.com/Shwam3/")); }
        //    catch (URISyntaxException | IOException e) { EastAngliaMapClient.printThrowable(e, "HelpDialog"); }
        //});
        //gitButton.setPreferredSize(new Dimension(76, 23));
        //gitButton.setOpaque(false);

        JPanel buttonPnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPnl.add(okButton);
        //buttonPnl.add(gitButton);
        buttonPnl.setOpaque(false);
        add(buttonPnl, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                EastAngliaMapClient.blockKeyInput = false;
            }
        });

        getRootPane().registerKeyboardAction(e -> { dispose(); EastAngliaMapClient.blockKeyInput = false; }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
        getRootPane().registerKeyboardAction(e -> { dispose(); EastAngliaMapClient.blockKeyInput = false; }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,  0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        getContentPane().setBackground(EastAngliaMapClient.BLACK);
        pack();
        setLocationRelativeTo(EastAngliaMapClient.frameSignalMap.frame);

        EastAngliaMapClient.blockKeyInput = true;

        setVisible(true);
    }
}*/