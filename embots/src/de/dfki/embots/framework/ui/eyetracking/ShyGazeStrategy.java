/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.framework.ui.eyetracking;

import de.dfki.carmina.eyeTrackerLogger.dataProcessor.LogData;
import de.dfki.embots.embrscript.EMBRScript;
import de.dfki.embots.framework.ui.eyetracking.actions.ComputeStateAction;
import de.dfki.embots.framework.ui.eyetracking.actions.GenerateScriptAction;
import java.util.Collection;
import java.util.Random;
import de.dfki.visp.graph.*;
import de.dfki.visp.graph.impl.*;
import de.dfki.visp.graph.impl.condition.*;
import de.dfki.visp.exception.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.w3c.dom.events.Event;

/**
 *
 * @author dapu01
 */
public class ShyGazeStrategy implements GazeStrategy{

    private Gaze gaze;
    private GazeFollowingBehavior gazeFollowB = new GazeFollowingBehavior();
    private GazeAwayBehavior gazeAwayB = new GazeAwayBehavior();
    private HeadAwayGazeTowardBehavior gazeTowardHeadAwayB = new HeadAwayGazeTowardBehavior();
    private HeadTowardGazeAwayBehavior gazeAwayHeadTowardB = new HeadTowardGazeAwayBehavior();
    private Random rand = new Random();
    private int lowerRandomBound = 3000;
    private int upperRandomBound = 8000;
    private long timeToWait = 0;
    private boolean userLookingAtAmber;
    private boolean isLookingAtUser;
    Node startNode;
    Supernode lookedAtInit;
    Node lookedAtState;
    Supernode notLookedAtInit;
    Node notLookedAtState;
    Node gazeAway;
    Node gazeFollow;
    Node gazeTowardHeadAway;
    Node gazeAwayHeadToward;
    Edge edge;
    List<RandomWaitEdge> randomWaitEdges = new ArrayList<RandomWaitEdge>();


    public ShyGazeStrategy(Gaze gaze,Machine fsm, Supernode sn){
        this.gaze = gaze;
        startNode = sn.createNode("Start");
        lookedAtInit = sn.createSupernode("Looked_At_Init");
        lookedAtState = lookedAtInit.createNode("Looked_At_State");
        notLookedAtInit = sn.createSupernode("Not_Looked_At_Init");
        notLookedAtState = notLookedAtInit.createNode("Not_Looked_At_State");
        gazeAway = lookedAtInit.createNode("Gaze_Away");
        gazeTowardHeadAway = lookedAtInit.createNode("Gaze_Toward_Head_Away");
        gazeFollow = notLookedAtInit.createNode("Gaze_Follow");
        gazeAwayHeadToward = notLookedAtInit.createNode("Gaze_Away_Head_Toward");
//        sn.setStartNode(startNode);
        sn.setStartNode(lookedAtInit);
        lookedAtInit.setStartNode(lookedAtState);
        notLookedAtInit.setStartNode(notLookedAtState);
        try{
//            sn.createConditionalEdge(startNode, lookedAtInit,new ConditionEquals(new ReadChannel(fsm, "State"),new UserStartsLookingAtEvent()));
//            sn.createConditionalEdge(startNode, notLookedAtInit, new ConditionEquals(new ReadChannel(fsm, "State"),new UserStartsLookingAwayEvent()));
//            edge = sn.createConditionalElseEdge(startNode, startNode);
            sn.createInterruptEdge(lookedAtInit, notLookedAtInit, new ConditionEquals(new ReadChannel(fsm, "State"),new UserStartsLookingAwayEvent()));
            sn.createInterruptEdge(notLookedAtInit, lookedAtInit, new ConditionEquals(new ReadChannel(fsm, "State"),new UserStartsLookingAtEvent()));
            lookedAtInit.createProbabilisticEdge(lookedAtState, gazeAway, (float) 0.9);
            lookedAtInit.createProbabilisticEdge(lookedAtState, gazeTowardHeadAway, (float) 0.1);
            randomWaitEdges.add(lookedAtInit.createRandomWaitEdge(gazeAway,lookedAtState,lowerRandomBound,upperRandomBound));
            randomWaitEdges.add(lookedAtInit.createRandomWaitEdge(gazeTowardHeadAway,lookedAtState,lowerRandomBound,upperRandomBound));
            notLookedAtInit.createProbabilisticEdge(notLookedAtState, gazeFollow, (float) 0.9999999);
            notLookedAtInit.createProbabilisticEdge(notLookedAtState, gazeAwayHeadToward, (float) 0.0000001);
            notLookedAtInit.createEpsilonEdge(gazeFollow,notLookedAtState);
            notLookedAtInit.createRandomWaitEdge(gazeAwayHeadToward,notLookedAtState,0,1000);
        }catch(NodeNotExistException e){
            e.printStackTrace();
        }
//        startNode.setWaitForCondition(1000);
        notLookedAtState.setWaitForCondition(100);
        lookedAtState.setWaitForCondition(100);
//        edge.addAction(new ComputeStateAction(this));
        lookedAtInit.addAction(new GenerateScriptAction(this, gazeAwayHeadTowardB));
        notLookedAtInit.addAction(new GenerateScriptAction(this, gazeFollowB));
        gazeAway.addAction(new GenerateScriptAction(this, gazeAwayB));
        gazeAwayHeadToward.addAction(new GenerateScriptAction(this, gazeAwayHeadTowardB));
        gazeTowardHeadAway.addAction(new GenerateScriptAction(this, gazeTowardHeadAwayB));
        gazeFollow.addAction(new GenerateScriptAction(this, gazeFollowB));
    }



    @Override
    public void setOffset() {

        gazeAwayB.setOffsetUpDown(0.0);
        gazeAwayB.setOffsetRightLeft(0.0);
        gazeFollowB.setOffsetUpDown(-10.0);
        gazeFollowB.setOffsetRightLeft(5.0);
        gazeAwayB.setLookConstraint(true);

    }

    @Override
    public void setLowerRandomBound(int min){
        lowerRandomBound = min;
        for(RandomWaitEdge rwe : randomWaitEdges){
            rwe.setMin(min);
        }
    }

    @Override
    public void setUpperRandomBound(int max){
        upperRandomBound = max;
        for(RandomWaitEdge rwe : randomWaitEdges){
            rwe.setMax(max);
        }
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
