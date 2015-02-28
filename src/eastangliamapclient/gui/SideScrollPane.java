package eastangliamapclient.gui;

import eastangliamapclient.EastAngliaMapClient;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class SideScrollPane extends JScrollPane
{

    public SideScrollPane(Component view)
    {
        super(view);

        verticalScrollBar.setUnitIncrement(20);
        horizontalScrollBar.setUnitIncrement(20);

        setWheelScrollingEnabled(false);

        addMouseWheelListener(new MouseAdapter()
        {
            JScrollBar scrollBar = null;

            @Override
            public void mouseWheelMoved(MouseWheelEvent evt)
            {
                if (evt.isControlDown())
                {
                    int newValue = EastAngliaMapClient.frameSignalMap.TabBar.getSelectedIndex() + evt.getWheelRotation();

                    if (newValue < 0)
                        EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(EastAngliaMapClient.frameSignalMap.TabBar.getTabCount() - 1);
                    else if (newValue > EastAngliaMapClient.frameSignalMap.TabBar.getTabCount() - 1)
                        EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(0);
                    else
                        EastAngliaMapClient.frameSignalMap.TabBar.setSelectedIndex(newValue);

                    EastAngliaMapClient.frameSignalMap.TabBar.requestFocusInWindow();
                    evt.consume();
                    return;
                }
                else if (evt.isShiftDown())
                    scrollBar = getHorizontalScrollBar();
                else if (!evt.isControlDown() && !evt.isShiftDown() && !evt.isAltDown() && !evt.isAltGraphDown())
                    scrollBar = getVerticalScrollBar();
                else
                    return;

                if (evt.getWheelRotation() >= 1) // Down/Right
                    scrollBar.setValue(Math.max(Math.min(scrollBar.getValue() + scrollBar.getUnitIncrement() * evt.getScrollAmount(), scrollBar.getMaximum()), scrollBar.getMinimum()));
                else if (evt.getWheelRotation() <= -1) // Up/Left
                    scrollBar.setValue(Math.max(Math.min(scrollBar.getValue() - scrollBar.getUnitIncrement() * evt.getScrollAmount(), scrollBar.getMaximum()), scrollBar.getMinimum()));

                evt.consume();
            }
        });
    }
}