package de.dfki.embots.bml.lex;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores a single lexeme.
 * 
 * @author Michael Kipp
 */
public class BehaviorLexemeImpl implements BehaviorLexeme
{

    private String _name;
    private EMBRPoseSequence _embrScript;
    private HashMap<BMLSyncLabel, PoseSemantics> _syncPointTable = 
            new HashMap<BMLSyncLabel, PoseSemantics>();
    private boolean isHoldBehavior = false;

    public BehaviorLexemeImpl(String name, EMBRPoseSequence script)
    {
        _name = name;
        _embrScript = script;
        createSyncPointTable();
        isHoldBehavior = !_syncPointTable.keySet().contains(BMLSyncLabel.STROKE_END);
    }

    @Override
    public String getName()
    {
        return _name;
    }

    @Override
    public EMBRPoseSequence getEMBRScript()
    {
        return _embrScript;
    }

    @Override
    public PoseSemantics getPoseSemantics(BMLSyncLabel label) {
        return _syncPointTable.get(label);
    }

    @Override
    public boolean isHoldBehavior() {
        return isHoldBehavior;
    }

    /**
     * Create table of explicit and implicit BML sync points, based on the
     * meta-data labels in the EMBRScript.
     */

    private void createSyncPointTable()
    {
        long startTime = 0;
        for (EMBRPose p : _embrScript.getPoses()) {
            if (p.getSemantics() != null) {
                PoseSemantics sem = new PoseSemantics(p);
                switch (p.getSemantics()) {
                    case START:
                        startTime = p.getTime();
                        sem.sequenceTime = 0;
                        break;
                    case READY:
                        sem.sequenceTime = (p.getTime() - startTime) / 1000d;
                        // add implicit STROKE_START point
                        double strokeStartTime = (p.getTime() - startTime + p.getHoldDuration()) / 1000d;
                        _syncPointTable.put(BMLSyncLabel.STROKE_START, new PoseSemantics(p, strokeStartTime, true));
                        break;
                    case STROKE_END:
                        sem.sequenceTime = (p.getTime() - startTime) / 1000d;
                        // add implicit RELAX point
                        double relaxTime = (p.getTime() - startTime + p.getHoldDuration()) / 1000d;
                        _syncPointTable.put(BMLSyncLabel.RELAX, new PoseSemantics(p, relaxTime, true));
                        break;
                    case END:
                        sem.sequenceTime = p.getTime()/1000d;
                        break;
                }
                _syncPointTable.put(p.getSemantics(), sem);
            }
        }
    }

    @Override
    public String toString() {
        return "[BehaviorLexemeImpl " + _name + " " + _syncPointTable.size() + " syncpoints "
                + (isHoldBehavior ? " HOLD-behavior" : "STROKE-behavior") + "]";
    }

    @Override
    public List<BMLSyncLabel> getOrderedSyncLabels()
    {
        List<BMLSyncLabel> result = new ArrayList<BMLSyncLabel>();
        for (EMBRPose pose : _embrScript.getPoses()) {
            if (pose.getSemantics() != null) {
                result.add(pose.getSemantics());
            }
        }
        return result;
    }
}
