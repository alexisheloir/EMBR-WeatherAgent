package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;

/**
 * This class computes in which state Amber is at either through eyetracker data
 * or through setting the state via the eyetracker simulator
 * @author Daniel Puschmann
 */
public class GazeState
{
    private States state = States.LOOKED_AT;
    private float embrPosX = 0.47F;
    private float embrPosY = 0.3F;
    private boolean stateChanged = false;

    /**
     * Ignored when simulator is used.
     */
    public void computeState(LogData logdata) {
        if (((logdata.x_eyepos_lefteye >= embrPosX && logdata.x_eyepos_lefteye <= (embrPosX + 0.03F))
                && (logdata.y_eyepos_lefteye >= (embrPosY) && logdata.y_eyepos_lefteye <= embrPosY + 0.05F))
                || ((logdata.x_eyepos_righteye >= embrPosX && logdata.x_eyepos_righteye <= (embrPosX + 0.03F))
                && (logdata.y_eyepos_righteye >= (embrPosY) && logdata.y_eyepos_righteye <= embrPosY + 0.05F))) {

            //User is now looking at Amber
               state = States.LOOKED_AT;
        } else {
                state = States.NOT_LOOKED_AT;
        }

    }

    /**
     *  Ignored when Eyetracker is used. 
     */
    public void setState(States s)
    {
        state = s;
        stateChanged = true;
    }

    public States getState()
    {
        return state;
    }

    public boolean changedState(){
        boolean temp = stateChanged;
        if(stateChanged){
            stateChanged = false;
        }
        return temp;
    }
}
