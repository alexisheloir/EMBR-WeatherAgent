/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking.actions;

import de.dfki.embots.framework.ui.eyetracking.GazeBehavior;
import de.dfki.embots.framework.ui.eyetracking.GazeStrategy;
import de.dfki.embots.framework.ui.eyetracking.LogDataSingleton;
import de.dfki.visp.actions.Action;

/**
 *
 * @author dapu01
 */
public class ComputeStateAction implements Action{

    private GazeStrategy gazeStrat;


    public ComputeStateAction(GazeStrategy gStrat){
        gazeStrat = gStrat;
    }

    @Override
    public void run() {
        gazeStrat.checkIfEMBRisLookedAt();
    }

}
