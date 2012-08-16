package de.dfki.embots.framework.gaze.eytrackersim;

import de.dfki.embots.framework.ui.eyetracking.Gaze;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Daniel Puschmann
 */
public class RandomSetting extends JFrame implements ChangeListener{

    static final int minLowerRandomBound = 1;
    static final int maxLowerRandomBound = 5;
    static final int initialLowerRandomBound = 3;
    static final int minIntervalLength = 1;
    static final int maxIntervalLength = 10;
    static final int initialIntervalLength = 5;

    private JSlider lowerBoundSlider = new JSlider(minLowerRandomBound,maxLowerRandomBound,initialLowerRandomBound);
    private JSlider intervalSlider = new JSlider(minIntervalLength,maxIntervalLength,initialIntervalLength);
    private Gaze gaze;
    private JLabel lowerBoundLabel = new JLabel("random time lower bound");
    private JLabel intervalLabel = new JLabel("random time interval");
    private FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER);



    public RandomSetting(Gaze gaze){
        super("Random Setting");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(250,250);
        setLayout(flowLayout);
        this.gaze = gaze;

        lowerBoundSlider.setMajorTickSpacing(5);
        lowerBoundSlider.setMinorTickSpacing(1);
        lowerBoundSlider.setPaintTicks(true);
        lowerBoundSlider.setPaintLabels(true);

        intervalSlider.setMajorTickSpacing(10);
        intervalSlider.setMinorTickSpacing(1);
        intervalSlider.setPaintTicks(true);
        intervalSlider.setPaintLabels(true);
        
        intervalSlider.addChangeListener(this);
        lowerBoundSlider.addChangeListener(this);

        getContentPane().add(lowerBoundLabel);
        getContentPane().add(lowerBoundSlider);
        getContentPane().add(intervalLabel);
        getContentPane().add(intervalSlider);


    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if(source.equals(lowerBoundSlider)){
            if(!source.getValueIsAdjusting()){
               gaze.getGazeStrategy().setLowerRandomBound(source.getValue()*1000);
            }
        }else
        if(source.equals(intervalSlider)){
            if(!source.getValueIsAdjusting()){
                gaze.getGazeStrategy().setUpperRandomBound(source.getValue()*1000);
            }
        }

    }

    public void setToInitialValues(){
        lowerBoundSlider.setValue(initialLowerRandomBound);
        intervalSlider.setValue(initialIntervalLength);
    }

}
