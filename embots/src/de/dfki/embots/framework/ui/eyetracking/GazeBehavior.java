package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import de.dfki.embots.embrscript.EMBRScript;

/**
 *
 * @author Daniel Puschmann
 */
public interface GazeBehavior {


    /**
     * Generates the script for the gaze according to the eyetracker data and the desired behavior.
     * @param logdata Represents the eyetracker data.
     * @return The script for the gaze.
     */
    public EMBRScript generateScript(LogDataSingleton logdata);


    /**
     * Sets the offset between the up-down-direction of the eyes and the up-down-direction of the head.
     */
    public void setOffsetUpDown(double offset);

    /**
     * Sets the offset between the right-left-direction of the eyes and the right-left-direction of the head.
     */
    public void setOffsetRightLeft(double offset);
}
