/*
 * FacialExpressionGenerator.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 05.09.2009, 10:43:06
 */
package de.dfki.embots.framework.behavior;

import de.dfki.embots.bml.behavior.BMLFaceBehavior;
import de.dfki.embots.bml.behavior.BMLFaceType;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.embrscript.EMBRFacialExpression;
import de.dfki.embots.embrscript.EMBRMorphKey;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import java.util.ArrayList;
import java.util.List;

/**
 * Static method for creating facial expression EMBRScripts.
 * For a facial expression to work, either stroke_start or stroke_end must
 * be specified!
 *
 * @author Michael Kipp
 */
public abstract class FacialExpressionGenerator
{

    private static final long FADE_IN = 200;
    private static final long FADE_OUT = 200;

    public static EMBRPoseSequence createEMBRScript(String character,
            BMLFaceBehavior beh,
            List<EMBRMorphTargetConstraint> constraints)
    {

        // find start+end times
        BMLSyncPoint start = beh.getSyncPoint(BMLSyncLabel.START.toString());
        BMLSyncPoint end = beh.getSyncPoint(BMLSyncLabel.END.toString());
        BMLSyncPoint strokeStart = beh.getSyncPoint(BMLSyncLabel.STROKE_START.toString());
        BMLSyncPoint strokeEnd = beh.getSyncPoint(BMLSyncLabel.STROKE_END.toString());

        // create embrscript
        EMBRPoseSequence seq = new EMBRPoseSequence(character);
        seq.comment = "FACIAL EXPRESSION: " + beh.getType();
        seq.startTime = 0;

        // default start time is zero
        long strokeStartTime = FADE_IN;
        if (start != null) {
            seq.startTime = (long) (start.getTime() * 1000);
            strokeStartTime = seq.startTime + FADE_IN;
        }
        if (strokeStart != null) {
            strokeStartTime = (long) (strokeStart.getTime() * 1000);

            // case: start was not specified
            if (seq.startTime == 0) {
                seq.startTime = Math.max(0, strokeStartTime - FADE_IN);
            }
        }

        long strokeEndTime = -1;
        if (end != null) {
            strokeEndTime = (long) (end.getTime() * 1000) - FADE_OUT;
        }
        if (strokeEnd != null) {
            strokeEndTime = (long) (strokeEnd.getTime() * 1000);
        }

        for (EMBRMorphTargetConstraint c : constraints) {
            List<EMBRPose> poses = generateSequence(c.key, c.value, strokeStartTime, strokeEndTime);
            seq.addPoses(poses);
        }
        
        return seq;
    }

    /**
     * Creates three poses of the same target to indicate
     * POSE (weight 1), POSE (weight 1) and NO POSE (weight 0)
     */
    private static List<EMBRPose> generateSequence(EMBRMorphKey target,
            double weight, long strokeStart, long strokeEnd)
    {
        if (target != null) {
            List<EMBRPose> result = new ArrayList<EMBRPose>();

            // start pose
            result.add(createPose(strokeStart, target, weight));

            // end pose
            if (strokeEnd > -1) {
                result.add(createPose(strokeEnd, target, weight));

                // after fade-out time set this pose to zero
                result.add(createPose(strokeEnd + FADE_OUT, target, 0));
            }
            return result;
        } else {
            return null;
        }
    }

    /**
     * Creates single pose with given target, (relative) time and weight.
     */
    private static EMBRPose createPose(long time, EMBRMorphKey target, double weight)
    {
        EMBRPose p = new EMBRPose(time);
        p.constraints.add(new EMBRMorphTargetConstraint(target, weight));

        // taking this out seemed to have helped the timing precision!
        //p.relativeTime = true;
        return p;
    }

    /*
    public static EMBRPoseSequence createEMBRScript(String character,
    BMLFaceBehavior beh)
    {
    if (beh == null)
    return null;

    EMBRFacialExpression target1 = null, target2 = null;

    // Mapping from BML type to EMBR morph target is done here:
    if (BMLFaceType.SMILE.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.SMILE_CLOSED;
    } else if (BMLFaceType.ANGER.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.ANGER;
    } else if (BMLFaceType.DISGUST.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.DISGUST;
    } else if (BMLFaceType.FEAR.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.FEAR;
    } else if (BMLFaceType.SAD.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.SAD;
    } else if (BMLFaceType.SURPRISE.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.SURPRISE;
    } else if (BMLFaceType.FROWN.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.BROW_DOWN_LEFT;
    target2 = EMBRFacialExpression.BROW_DOWN_RIGHT;
    } else if (BMLFaceType.SCEPTICAL.toSymbol().equals(beh.getType())) {
    target1 = EMBRFacialExpression.BROW_UP_RIGHT;
    target2 = EMBRFacialExpression.BROW_DOWN_LEFT;
    }

    // find start+end times
    BMLSyncPoint start = beh.getSyncPoint(BMLSyncLabel.START.toString());
    BMLSyncPoint end = beh.getSyncPoint(BMLSyncLabel.END.toString());
    BMLSyncPoint strokeStart = beh.getSyncPoint(BMLSyncLabel.STROKE_START.toString());
    BMLSyncPoint strokeEnd = beh.getSyncPoint(BMLSyncLabel.STROKE_END.toString());

    // create embrscript
    EMBRPoseSequence seq = new EMBRPoseSequence(character);
    seq.comment = "FACIAL EXPRESSION: " + beh.getType();
    seq.startTime = 0;

    // default start time is zero
    long strokeStartTime = FADE_IN;
    if (start != null) {
    seq.startTime = (long) (start.getTime() * 1000);
    strokeStartTime = seq.startTime + FADE_IN;
    }
    if (strokeStart != null) {
    strokeStartTime = (long) (strokeStart.getTime() * 1000);

    // case: start was not specified
    if (seq.startTime == 0) {
    seq.startTime = Math.max(0, strokeStartTime - FADE_IN);
    }
    }

    long strokeEndTime = -1;
    if (end != null) {
    strokeEndTime = (long) (end.getTime() * 1000) - FADE_OUT;
    }
    if (strokeEnd != null) {
    strokeEndTime = (long) (strokeEnd.getTime() * 1000);
    }
    //System.out.println("#### times start=" + strokeStartTime + " end=" + strokeEndTime);

    seq.addPoses(generateSequence(target1, 1, strokeStartTime, strokeEndTime));
    if (target2 != null) {
    seq.addPoses(generateSequence(target2, 1d, strokeStartTime, strokeEndTime));
    }

    //System.out.println("#### sequence: " + seq.toScript());
    return seq;
    }

     * Creates three poses of the same target to indicate
     * POSE (weight 1), POSE (weight 1) and NO POSE (weight 0)
    private static List<EMBRPose> generateSequence(EMBRFacialExpression target,
    double weight, long strokeStart, long strokeEnd)
    {
    if (target != null) {
    List<EMBRPose> result = new ArrayList<EMBRPose>();
    result.add(createPose(strokeStart, target.toSymbol(), weight));

    if (strokeEnd > -1) {
    result.add(createPose(strokeEnd, target.toSymbol(), weight));

    // after fade-out time set this pose to zero
    result.add(createPose(strokeEnd + FADE_OUT, target.toSymbol(), 0));
    }
    return result;
    } else {
    return null;
    }
    }
     */
}
