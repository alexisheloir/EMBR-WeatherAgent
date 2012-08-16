package de.dfki.embots.bml.translate;

import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import de.dfki.embots.framework.EMBOTSConstants;

/**
 * Instanciates solved BML objects.
 * 
 * @author Michael Kipp
 */
public class BMLToEMBRScript
{

    /**
     * Takes a piece of solved BML and translates it to EMBRScript.
     *
     * Note: the solution of the resolution step is in the sync points.
     *
     * @param beh BML block in solved state, i.e. only absolute times.
     * @return EMBRScript
     */
    public static EMBRPoseSequence toEMBRScript(BMLBehavior beh)
    {
        if (beh.getLexeme() != null) {

            // create copy of lexeme template
            EMBRPoseSequence result = new EMBRPoseSequence(beh.getLexeme().getEMBRScript());

            // assign agent
            result.character = beh.getAgentTrans(EMBOTSConstants.EMBR_CHARACTER);
            
            // create pose-hold list
            long[] poseHold = new long[2 * result.getPoses().size()];

            // create target time list (stores absolute times)
            long[] targetTimes = new long[poseHold.length];

            // fill arrays for all sync points in the BML
            int i = 0;
            EMBRPose firstPose = null;
            beh.getSyncPoints();
            for (EMBRPose p : result.getPoses()) {
                poseHold[i] = p.getTime();
                // store first time
                if (firstPose == null)
                    firstPose = p;

                // determine target time if there is a sync point
                targetTimes[i] = -1;
                if (p.getSemantics() != null) {
                    BMLSyncPoint sy = beh.getSyncPoint(p.getSemantics().toString());
                    if (sy != null) {
                        targetTimes[i] = (long) (sy.getTime() * 1000);
                    }
                }
                i++;

                // now the hold
                poseHold[i] = p.getHoldDuration();

                // determine target time if there is a sync point
                targetTimes[i] = -1;
                if (p.getSemantics() != null && p.getSemantics().holdFollows()) {
                    BMLSyncLabel lab = p.getSemantics().subsequent();
                    if (lab != null) {
                        BMLSyncPoint sy = beh.getSyncPoint(p.getSemantics().subsequent().toString());
                        if (sy != null) {
                            targetTimes[i] = (long) (sy.getTime() * 1000);
                        }
                    }
                }
                i++;
            }

            long[] poseList = poseHoldToPoseList(poseHold);
            adjustPoseList(poseList, targetTimes);
            long[] resultPoseHold = poseToPoseHoldList(poseList);

            // write back results to EMBRScript
            int j = 0;
            for (EMBRPose p : result.getPoses()) {
                p.setTime(resultPoseHold[j]);
                p.setHoldDuration(resultPoseHold[j + 1]);
                j += 2;

                // FIX: make all times ABSOLUTE
                p.relativeTime = false;
            }

            // move start pose (first pose) into header of sequence
            result.startTime = firstPose.getTime();
            result.getPoses().remove(firstPose);

            return result;
        } else {
            return null;
        }
    }


    /**
     * Performs warping of intermediate poses if constrained by target times.
     *
     * @param poseList Modified pose times (result is written here)
     * @param targetList Target times
     */
    private static void adjustPoseList(long[] poseList, long[] targetList)
    {
        long prevTime = 0;
        int prevIndex = -1;
        for (int i = 0; i < poseList.length; i++) {

            // case: there is a target time
            if (targetList[i] > -1) {

                // case: this is the first target value
                if (prevIndex == -1) {
                    long offset = targetList[i] - poseList[i];
                    for (int j = 0; j < i; j++) {
                        poseList[j] += offset;
                    }

                } else {

                    // case: not first target value
                    double ratio = ((double) (targetList[i] - targetList[prevIndex])) / (poseList[i] - prevTime);
                    for (int j = prevIndex + 1; j < i; j++) {
                        poseList[j] = poseList[prevIndex] + (long) ((poseList[j] - prevTime) * ratio);
                    }
                }
                prevTime = poseList[i];
                poseList[i] = targetList[i];
                prevIndex = i;
            }
        }
        if (prevIndex < poseList.length - 1 && prevIndex >= 0) {
            long offset = poseList[prevIndex] - prevTime;
            for (int i = prevIndex + 1; i < poseList.length; i++) {
                poseList[i] += offset;
            }
        }
    }

    /**
     * Translates pose-hold list of times to list of absolute poses.
     *
     * @param poseHoldList
     */
    public static long[] poseHoldToPoseList(long[] poseHoldList)
    {
        long[] time = new long[poseHoldList.length];
        for (int i = 0; i < time.length; i++) {
            time[i] = i % 2 == 0 ? poseHoldList[i] : poseHoldList[i - 1] + poseHoldList[i];
        }
        return time;
    }

    /**
     * Translates list of abs. poses to pose-hold format.
     *
     * @param poseList
     */
    public static long[] poseToPoseHoldList(long[] poseList)
    {
        long[] time = new long[poseList.length];
        for (int i = 0; i < time.length; i++) {
            time[i] = i % 2 == 0 ? poseList[i] : poseList[i] - poseList[i - 1];
        }
        return time;
    }

    public static void main(String[] args)
    {
        long[] ph = {1000, 50, 1500, 200, 3000, 178};
        long[] p = {50, 100, 200, 300};
        long[] p1 = poseHoldToPoseList(ph);
        long[] ph1 = poseToPoseHoldList(p);
        System.out.println("# FINISHED");
    }
}
