package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class HelpDialog extends JDialog
{
    public HelpDialog()
    {
        setTitle("Help");
        setPreferredSize(new Dimension(550, 328));
        setResizable(false);
        setLayout(null);
        setLocationByPlatform(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);

        JLabel mainText = new JLabel(
                "<html>"
                    + "<p><b>Navigation:</b></p>"
                    + "<p>Use the <i>number</i> keys or the <i>function</i> keys (F1, F2 ...) (CTRL 13-24 SHIFT 25-36 CTRL+SHIFT 36-48) to change the tabs as well as the arrow keys.</p>"
                    + "<br>"
                    + "<p><b>Train headcodes:</b></p>"
                    + "<p>Left clicking on a green headcode will bring up an RTT search for the train.</p>"
                    //+ "<p>Right clicking a berth/train offers a context menu.</p>"
                    + "<br>"
                    + "<p><b>Key Bindings:</b></p>"
                    + "<p>'O'&nbsp;- Toggle berth opacity</p>"
                    + "<p>'D'&nbsp;- Toggle berth id's</p>"
                    + "<p>'H'&nbsp;- Opens this dialog</p>"
                    + "<br>"
                    + "<p>The source code is available at http://www.github.com/Shwam3/EastAngliaSignalMapClient"
                    +    " and http://www.github.com/Shwam3/EastAngliaSignalMapServer</p>"
                    + "<br>"
                    + "<p>Note: This program may regularly disconnect/be unavailable as the server program is run on my local PC which may not always "
                    +    "be on/active. In such cases I highly recommend using opentraintimes.com and/or railcam.org.uk as alternatives.</p>"
                    + "<br>"
                    + "<p>&copy; Cameron Bird 2014</p>"
                + "</html>");

        mainText.setOpaque(false);
        mainText.setForeground(EastAngliaMapClient.GREEN);
        mainText.setVerticalAlignment(SwingConstants.CENTER);
        mainText.setBounds(10, 10, 524, 280);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent evt)
            {
                EastAngliaMapClient.blockKeyInput = false;
            }
        });

        add(mainText);
        getContentPane().setBackground(EastAngliaMapClient.BLACK);
        pack();
        setLocationRelativeTo(EastAngliaMapClient.SignalMap.frame);

        setVisible(true);

        if (isVisible())
            EastAngliaMapClient.blockKeyInput = true;
    }
}