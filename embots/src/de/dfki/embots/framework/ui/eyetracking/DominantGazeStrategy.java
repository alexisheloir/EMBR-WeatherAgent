/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.framework.ui.eyetracking.actions.ComputeStateAction;
import de.dfki.embots.framework.ui.eyetracking.actions.GenerateBMLAction;
import de.dfki.embots.framework.ui.eyetracking.actions.GenerateScriptAction;
import de.dfki.visp.exception.NodeNotExistException;
import de.dfki.visp.graph.Machine;
import de.dfki.visp.graph.Node;
import de.dfki.visp.graph.Supernode;
import de.dfki.visp.graph.impl.ReadChannel;
import de.dfki.visp.graph.impl.condition.ConditionEquals;
import java.util.Collection;
import java.util.Random;

/**
 *
 * @author dapu01
 */
public class DominantGazeStrategy implements GazeStrategy{

    private Gaze gaze;
    private GazeFollowingBehavior gazeFollowB = new GazeFollowingBehavior();
    private GazeAwayBehavior gazeAwayB = new GazeAwayBehavior();
    private Random rand = new Random();
    private int lowerRandomBound = 3000;
    private int upperRandomBound = 8000;
    private long timeToWait = 0;
    private boolean userLookingAtAmber;
    private boolean isLookingAtUser;
    Node startNode;
    Node lookedAtState;
    Node notLookedAtState;



    public DominantGazeStrategy(Gaze gaze,Machine fsm, Supernode sn){
        this.gaze = gaze;
        lookedAtState = sn.createNode("Looked_At_Init");
        notLookedAtState = sn.createNode("Not_Looked_At_Init");
        sn.setStartNode(lookedAtState);
        try{
            sn.createConditionalEdge(lookedAtState, notLookedAtState, new ConditionEquals(new ReadChannel(fsm, "State"), new UserStartsLookingAwayEvent()));
            sn.createConditionalElseEdge(lookedAtState, lookedAtState);
            (sn.createConditionalEdge(notLookedAtState, lookedAtState, new ConditionEquals(new ReadChannel(fsm, "State"), new UserStartsLookingAtEvent()))).addAction(new GenerateBMLAction(this));
            sn.createConditionalElseEdge(notLookedAtState, notLookedAtState);
        }catch(NodeNotExistException e){
            e.printStackTrace();
        }
        notLookedAtState.setWaitForCondition(lowerRandomBound+rand.nextInt(upperRandomBound-lowerRandomBound));
        lookedAtState.setWaitForCondition(100);
        lookedAtState.addAction(new GenerateScriptAction(this, gazeFollowB));
        notLookedAtState.addAction(new GenerateScriptAction(this, gazeAwayB));

    }


    @Override
    public void setOffset() {
        gazeAwayB.setOffsetUpDown(0);
        gazeAwayB.setOffsetRightLeft(0);
        gazeFollowB.setOffsetUpDown(0.5);
        gazeFollowB.setOffsetRightLeft(-1.0);
        gazeAwayB.setLookConstraint(false);
    }

    @Override
    public long timeToWait() {
        return timeToWait;
    }

    @Override
    public boolean isLookingAtUser() {
        return isLookingAtUser;
    }

    @Override
    public void setLowerRandomBound(int min){
        lowerRandomBound = min;
        notLookedAtState.setWaitForCondition(lowerRandomBound+rand.nextInt(upperRandomBound-lowerRandomBound));
    }

    @Override
    public void setUpperRandomBound(int length){
        upperRandomBound = length;
        notLookedAtState.setWaitForCondition(lowerRandomBound+rand.nextInt(upperRandomBound-lowerRandomBound));
    }

    public GazeAwayBehavior getGazeAwayBehavior(){
        return gazeAwayB;
    }

    @Override
    public void setEMBRScript(EMBRScript s) {
        gaze.setEMBRScript(s);
    }

    @Override
    public void checkIfEMBRisLookedAt() {
        gaze.checkIfEMBRisLookedAt();
    }

    @Override
    public void setBMLInput(String bml){
        gaze.setBMLInput(bml);
    }


}
