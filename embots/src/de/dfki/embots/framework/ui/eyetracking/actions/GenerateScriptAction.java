package de.dfki.embots.framework.ui.eyetracking.actions;

import de.dfki.embots.framework.ui.eyetracking.*;
import de.dfki.visp.actions.Action;

/**
 *
 * @author Daniel Puschmann
 */
public class GenerateScriptAction implements Action{

    private GazeStrategy gazeStrat;
    private GazeBehavior gazeBehavior;
    private LogDataSingleton logData;

    public GenerateScriptAction(GazeStrategy gStrat,GazeBehavior gBehavior){
        gazeStrat = gStrat;
        gazeBehavior = gBehavior;
    }

    @Override
    public void run() {
        logData = LogDataSingleton.getInstance();
        gazeStrat.setEMBRScript(gazeBehavior.generateScript(logData));
    }

}
