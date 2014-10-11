package eastangliamapclient.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class CustomScrollPane extends JScrollPane
{
    public CustomScrollPane()
    {
        super();

        verticalScrollBar.setUnitIncrement(20);
        horizontalScrollBar.setUnitIncrement(20);

        setWheelScrollingEnabled(false);

        addMouseWheelListener(new MouseAdapter()
        {
            JScrollBar scrollBar;

            @Override
            public void mouseWheelMoved(MouseWheelEvent evt)
            {
                if (evt.isShiftDown())
                    scrollBar = getHorizontalScrollBar();
                else
                    scrollBar = getVerticalScrollBar();

                if (evt.getWheelRotation() >= 1) // Down/Right
                {
                    int scrollAmount = evt.getScrollAmount();
                    int newValue = scrollBar.getValue() + scrollBar.getUnitIncrement() * scrollAmount;

                    if (newValue <= scrollBar.getMaximum())
                        scrollBar.setValue(newValue);

                    evt.consume();
                }
                else if (evt.getWheelRotation() <= -1) // Up/Left
                {
                    int scrollAmount = evt.getScrollAmount();
                    int newValue = scrollBar.getValue() - scrollBar.getUnitIncrement() * scrollAmount;

                    if (newValue >= 0)
                        scrollBar.setValue(newValue);

                    evt.consume();
                }
            }
        });
    }
}