package de.dfki.embots.behaviorbuilder.view;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Michael Kipp
 */
public class TimeWarpPane extends JPanel
{
    private JSlider timeWarpSlider, motionSlider, holdSlider;
    private List<ChangeListener> _changeListeners = new ArrayList<ChangeListener>();


    public TimeWarpPane()
    {
        final JLabel timingLabel = new JLabel("Time factor = 1,0");
        timeWarpSlider = new JSlider(1, 50, 10);
        final JLabel motionFactor = new JLabel("Motion = 1,0");
        motionSlider = new JSlider(0, 20, 10);
        final JLabel holdFactor = new JLabel("Hold factor = 1,0");
        holdSlider = new JSlider(1, 50, 10);

//        timeWarpSlider.setPreferredSize(new Dimension(180, 30));
//        motionSlider.setPreferredSize(new Dimension(120, 20));
//        holdSlider.setPreferredSize(new Dimension(180, 30));

        timeWarpSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                timingLabel.setText("Time factor = " + String.format("%2.1f", timeWarpSlider.getValue() / 10f));
                for (ChangeListener li: _changeListeners) {
                    li.stateChanged(ce);
                }
            }
        });
        motionSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                motionFactor.setText("Motion = " + String.format("%2.1f", motionSlider.getValue() / 10f));
            }
        });
        holdSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent ce)
            {
                holdFactor.setText("Hold factor = " + String.format("%2.1f", holdSlider.getValue() / 10f));
            }
        });
        add(timingLabel);
        add(timeWarpSlider);
//        add(motionFactor);
//        add(motionSlider);
        add(holdFactor);
        add(holdSlider);
    }

    public void addChangeListener(ChangeListener li) {
        _changeListeners.add(li);
    }
    
    public float getTimeWarp() {
        return timeWarpSlider.getValue()/10f;
    }

//    public float getMotionFactor() {
//        return motionSlider.getValue()/10f;
//    }

    public float getHoldFactor() {
        return holdSlider.getValue()/10f;
    }

}
