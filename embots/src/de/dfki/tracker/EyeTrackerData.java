/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.tracker;

import de.dfki.embots.embrscript.Triple;
import de.dfki.embots.framework.ui.eyetracking.LogDataSingleton;

/**
 *
 * @author Mahendiran
 */
public class EyeTrackerData {

    private LogDataSingleton liveData=LogDataSingleton.getInstance();

    public  Triple computeTriple() {
        float x = liveData.x_eyepos_lefteye;
        float y = liveData.y_eyepos_lefteye;
        float z = 0;
        return new Triple(x, y, z);
    }
}
