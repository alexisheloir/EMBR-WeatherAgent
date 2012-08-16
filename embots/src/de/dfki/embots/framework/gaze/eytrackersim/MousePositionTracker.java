package de.dfki.embots.framework.gaze.eytrackersim;
import java.awt.*;
import java.awt.event.*;

/**
 *
 * @author Daniel Puschmann
 */
public class MousePositionTracker extends MouseMotionAdapter
{

    private Point pos;


    @Override
    public synchronized void mouseDragged(MouseEvent e) 
    {
        
        pos = e.getLocationOnScreen();
    }


    @Override
    public synchronized void mouseMoved(MouseEvent e) 
    {
        
        pos = e.getLocationOnScreen();
    }


    public synchronized Point getPosition() 
    {

        if (pos == null){
            return new Point(0,0);
        }else
            return pos;
        }
}

