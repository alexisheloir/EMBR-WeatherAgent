package de.dfki.embots.behaviorbuilder.model;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.embrscript.*;
import java.util.List;
import java.util.Vector;

/**
 * Internal pose representation, an extension of an EMBRPose, to be
 * used in the EMBR-Poser's control panel and pose sequence table.
 * Builds the pose sequence sent to the agent on-the-fly.
 *
 * Class encapsulates (aggregates)
 * a {@link de.dfki.embots.embrscript.EMBRPoseSequence}
 * to enrich it by additional attributes such as "enabled" (used in the
 * later sequence), and an ID.
 * 
 * @author Oliver Schoenleben
 */
public class PoseModel extends EMBRPose implements Comparable<PoseModel>, BBConstants
{

    /** The default pose that can be set by button in the application. */
    public static PoseModel DEFAULT_POSE = new PoseModel(
            -1, false, SimpleSyncTag.ACTUAL_END, 50, 0,
            new EMBRTimeWarpConstraint(DEFAULT_TIME_WARP_KEY, 0), "");

    static {
        DEFAULT_POSE.initToDefaults();
        DEFAULT_POSE.useDefaults();
    }
    /** An empty pose to clone whenever needed. */
    public static PoseModel EMPTY_POSE = new PoseModel(
            -2, false, SimpleSyncTag.UNDEFINED, 50, 0,
            new EMBRTimeWarpConstraint(DEFAULT_TIME_WARP_KEY, 0), "");

    static {
        EMPTY_POSE.initToDefaults();
        EMPTY_POSE.useNone();
    }
    private boolean _modified = false;
    /** A unique identification number, used for add/delete operations. */
    private int _id;
    /** Whether or this pose is played in the sequence */
    protected boolean _isActivated;
    /** The phase type of this pose within the gesture, used for BML synchronization. */
    //protected BMLSyncLabel _phase;
    protected SimpleSyncTag _phase;
    /** ~rong: The argument (value) for the time warp */
    protected EMBRTimeWarpConstraint _warp;
    // Breathing
    public boolean useBreathing;
    public double breathFreq;
    public double breathAmpl;
    // Gaze Target
    public boolean useGazeDir;
    public Triple lookAt;
    public EMBRBodyGroup lookAtBodyGroup;
    // Head orientation NEW
    public boolean useHeadOrientZ = false;
    public boolean useHeadOrientY = false;
    public Triple headOrientZ = new Triple();
    public Triple headOrientY = new Triple();
    // Face Shade
    public boolean useShader;
    public double shade;
    public List<EMBRMorphTargetConstraint> morphV;
    // Hand Shape
    public EMBRHandshape lhShape;
    public EMBRHandshape rhShape;
    // Arm Swivel
    public boolean useLeftSwivel;
    public double lSwivel;
    public boolean useRightSwivel;
    public double rSwivel;
    // Hand Position
    public boolean useLPos;
    public Triple lhand;
    public boolean useRPos;
    public Triple rhand;
    // hand orientation
    public boolean useLhOrientY;
    public boolean useRhOrientY;
    public boolean useLhOrientZ;
    public boolean useRhOrientZ;
    public Triple lhOrientY;
    public Triple rhOrientY;
    public Triple lhOrientZ;
    public Triple rhOrientZ;
    // time warping
    private float timeWarpFactor = 1f;
    private float motionWarpFactor = 1f;
    private float holdWarpFactor = 1f;
    private boolean useTimeWarpFactors = true;
    // torso
    public boolean useTorsoPosition = false;
    public boolean useTorsoOrient = false;
    public Triple torsoPosition = new Triple(0, 0, 0);
    public Triple torsoOrient = new Triple(0, 0, 0);
    // shoulder
    public boolean useShoulder = false;
    public double shoulderValue = 0f;


    /**
     * Copy-constructor.
     *
     * @param p
     */
    public PoseModel(PoseModel p)
    {
        this(
                -1, p._isActivated, p.getSimplePhaseType(),
                p.getTime(), p.getHoldDuration(),
                p.getTimeWarp(),
                p.getComment() + " (copy!)");
        copySettingsFrom(p);
    }

    public PoseModel(int id, boolean invoked, SimpleSyncTag phase,
            long start, long hold, EMBRTimeWarpConstraint warp,
            String comment)
    {
        super();//DEFAULT_POSE_REL_TIME);
        relativeTime = true;
        _id = -1;
        //morphV = new Vector<Morph>(0,4); // hard coded guess ;)
        morphV = new Vector<EMBRMorphTargetConstraint>(0, 4); // hard coded guess ;)
        setInvoked(invoked);
        setSimplePhaseType(phase);
        setTime(start); //~ hard coded, but overwritten in relevant cases
        setHoldDuration(hold);
        setTimeWarp(warp);
        setComment(comment); //~
    }

    public void copySettingsFrom(PoseModel p)
    {

        // OLD:
        //morphKey = p.morphKey;
        //morphValue = p.morphValue;
        // INTERIM:
        //Collections.copy(_morphs, p._morphs);
        // triggers a JAVA-bug, so NEW:
        morphV.clear(); //~ actually, currently not neccessary, I reckon
        //for ( Morph m : p.morphV ) morphV.add(m);
        for (EMBRMorphTargetConstraint m : p.morphV) {
            morphV.add(new EMBRMorphTargetConstraint(m));
        }

        // Breathing
        useBreathing = p.useBreathing;
        breathFreq = p.breathFreq;
        breathAmpl = p.breathAmpl;

        // Gaze Target
        useGazeDir = p.useGazeDir;
        lookAt = p.lookAt;
        lookAtBodyGroup = p.lookAtBodyGroup;

        // Head orient
//        useHeadOrientation = p.useHeadOrientation;
//        headOrientation = p.headOrientation;
//        headOrientBodyGroup = p.headOrientBodyGroup;
        useHeadOrientZ = p.useHeadOrientZ;
        useHeadOrientY = p.useHeadOrientY;
//        headOrientAxis1 = p.headOrientAxis1;
        headOrientZ = p.headOrientZ;
//        headOrientAxis2 = p.headOrientAxis2;
        headOrientY = p.headOrientY;

        // Face Shade
        useShader = p.useShader;
        shade = p.shade;

        // Hand Shape
        lhShape = p.lhShape;
        rhShape = p.rhShape;

        // Arm Swivel
        useLeftSwivel = p.useLeftSwivel;
        lSwivel = p.lSwivel;
        useRightSwivel = p.useRightSwivel;
        rSwivel = p.rSwivel;

        // Hand Position
        useLPos = p.useLPos;
        lhand = p.lhand;
        useRPos = p.useRPos;
        rhand = p.rhand;

        // Hand Orientation
        useLhOrientY = p.useLhOrientY;
        useRhOrientY = p.useRhOrientY;
        useLhOrientZ = p.useLhOrientZ;
        useRhOrientZ = p.useRhOrientZ;
        lhOrientY = p.lhOrientY;
        rhOrientY = p.rhOrientY;
        lhOrientZ = p.lhOrientZ;
        rhOrientZ = p.rhOrientZ;

        // Torso
        useTorsoOrient = p.useTorsoOrient;
        useTorsoPosition = p.useTorsoPosition;
        torsoOrient = p.torsoOrient;
        torsoPosition = p.torsoPosition;

        // shoulder
        useShoulder = p.useShoulder;
        shoulderValue = p.shoulderValue;
    }

    public void initToDefaults()
    {

        // Facial Expression
        // Old:
        //morphKey = DEFAULT_MORPH_KEY;
        //morphValue = DEFAULT_MORPH_VALUE;
        // New:
        morphV.clear();

        // Breathing
        breathFreq = DEFAULT_BREATHING_FREQUENCY;
        breathAmpl = DEFAULT_BREATHING_AMPLITUDE;

        // Head orientation
//        headOrientAxis1 = EMBRNormal.UNDEFINED;
        useHeadOrientZ = false;
        useHeadOrientY = false;
        headOrientZ = new Triple();
//        headOrientAxis2 = EMBRNormal.UNDEFINED;
        headOrientY = new Triple();

        // Gaze Target
        lookAt = DEFAULT_LOOK_AT;
        lookAtBodyGroup = DEFAULT_LOOK_AT_BODY_GROUP;

        // Face Shade
        shade = DEFAULT_SHADE;

        // Hand Shape
        lhShape = DEFAULT_LH_HANDSHAPE;
        rhShape = DEFAULT_RH_HANDSHAPE;

        // Arm Swivel
        lSwivel = DEFAULT_LEFT_SWIVEL;
        rSwivel = DEFAULT_RIGHT_SWIVEL;

        // Hand Position
        lhand = DEFAULT_LEFT_HAND_POSITION;
        rhand = DEFAULT_RIGHT_HAND_POSITION;

        // Hand Orientation
        useLhOrientY = false;
        useRhOrientY = false;
        useLhOrientZ = false;
        useRhOrientZ = false;

//        lhOrientAxis1 = DEFAULT_LEFT_ORIENTATION_AXIS;
        lhOrientY = DEFAULT_LEFT_HAND_ORIENTATION;
//        rhOrientAxis1 = DEFAULT_RIGHT_ORIENTATION_AXIS;
        rhOrientY = DEFAULT_RIGHT_HAND_ORIENTATION;

        // Hand Orientation
//        lhOrientAxis2 = DEFAULT_LEFT_ORIENTATION_AXIS;
        lhOrientZ = DEFAULT_LEFT_HAND_ORIENTATION;
//        rhOrientAxis2 = DEFAULT_RIGHT_ORIENTATION_AXIS;
        rhOrientZ = DEFAULT_RIGHT_HAND_ORIENTATION;

        useTorsoOrient = false;
        useTorsoPosition = false;
        useShoulder = false;

    }

    public void useConstraints(boolean all, boolean defaults)
    {
        useBreathing = all || defaults ? DEFAULT_USE_BREATHING : false;
        useGazeDir = all || defaults ? DEFAULT_USE_LOOK_AT : false;
        useShader = all || defaults ? DEFAULT_USE_SHADER : false;
        useLeftSwivel = all || defaults ? DEFAULT_USE_LSW : false;
        useRightSwivel = all || defaults ? DEFAULT_USE_RSW : false;
        useLPos = all || defaults ? DEFAULT_USE_LPOS : false;
        useRPos = all || defaults ? DEFAULT_USE_RPOS : false;
        extractCurrentPoseConstraints();


    }

    public void useAll()
    {
        useConstraints(true, true);


    }

    public void useDefaults()
    {
        useConstraints(false, true);


    }

    public void useNone()
    {
        useConstraints(false, false);


    }

    public EMBRMorphTargetConstraint addMorph(EMBRMorphKey key, double value)
    {
        EMBRMorphTargetConstraint m = new EMBRMorphTargetConstraint(key, value);
        morphV.add(m);


        return m;


    }

    public boolean delMorph(EMBRMorphTargetConstraint m)
    {
        return morphV.remove(m);


    }

    /**
     * Patches the PoseSequence-Object and extract its EMBRScript code,
     * that can then be sent to the agent.
     * This will usually be called before the actual send.
     */
    public void extractCurrentPoseConstraints()
    {
        // ~ always executed twice
        clearConstraints();

        // Constraints adjusted in the control panel:


        if (useBreathing) {
            constraints.add(new EMBRAutonomousBehaviorConstraint(EMBRAutonomousBehaviorKey.BREATHING_FREQUENCY, breathFreq));
            constraints.add(new EMBRAutonomousBehaviorConstraint(EMBRAutonomousBehaviorKey.BREATHING_AMPLITUDE, breathAmpl));


        }

        if (useGazeDir) {
            constraints.add(new EMBRLookAtConstraint(lookAtBodyGroup, lookAt)); //~ frmt


        }

        if (useHeadOrientZ) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK,
                    EMBRJoint.HEAD, EMBRNormal.Z_AXIS, headOrientZ));


        }
        if (useHeadOrientY) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.HEAD_NECK,
                    EMBRJoint.HEAD, EMBRNormal.Y_AXIS, headOrientY));


        }
        if (useShader) {
            constraints.add(new EMBRShaderConstraint(EMBRShaderKey.BLUSHING, shade)); //~ frmt


        } // Old:
        //if (morphKey != EMBRMorphKey.UNDEFINED) constraints.add(new EMBRMorphTargetConstraint(morphKey, morphValue)); //~ frmt
        // New:
        //for(Morph morph : morphV) constraints.add(new EMBRMorphTargetConstraint(morph.key, morph.sigma));
        for (EMBRMorphTargetConstraint m : morphV) {
            constraints.add(new EMBRMorphTargetConstraint(m.key, m.value));


        }

        if (lhShape != EMBRHandshape.UNDEFINED) {
            constraints.add(new EMBRHandshapeConstraint(EMBRBodyGroup.LEFT_HAND, lhShape));


        }
        if (useLPos) {
            constraints.add(new EMBRPositionConstraint(EMBRBodyGroup.LEFT_ARM,
                    EMBRJoint.LEFT_WRIST, lhand, Triple.ORIGIN));


        }
        if (useLhOrientY) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.LEFT_ARM,
                    EMBRJoint.LEFT_WRIST, EMBRNormal.Y_AXIS, lhOrientY));


        }

        if (useLhOrientZ) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.LEFT_ARM,
                    EMBRJoint.LEFT_WRIST, EMBRNormal.Z_AXIS, lhOrientZ));


        }

        // Torso
        if (useTorsoPosition) {
            constraints.add(new EMBRPositionConstraint(EMBRBodyGroup.SPINE, EMBRJoint.SPINE4, torsoPosition, new Triple(0, 0, 0)));
        }
        if (useTorsoOrient) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.SPINE, EMBRJoint.SPINE4, EMBRNormal.Z_AXIS, torsoOrient));
        }

        // Shoulder
        if (useShoulder) {
            constraints.add(new EMBRTargetPoseConstraint(EMBRBodyGroup.SHOULDERS, EMBRTargetPose.SHRUG.toScript(), shoulderValue));
        }

        if (useLeftSwivel) {
            constraints.add(new EMBRSwivelConstraint(EMBRBodyGroup.LEFT_ARM, lSwivel)); //~ frmt


        }

        if (rhShape != EMBRHandshape.UNDEFINED) {
            constraints.add(new EMBRHandshapeConstraint(EMBRBodyGroup.RIGHT_HAND, rhShape));


        }
        if (useRPos) {
            constraints.add(new EMBRPositionConstraint(EMBRBodyGroup.RIGHT_ARM,
                    EMBRJoint.RIGHT_WRIST, rhand, Triple.ORIGIN));


        }
        if (useRhOrientY) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.RIGHT_ARM,
                    EMBRJoint.RIGHT_WRIST, EMBRNormal.Y_AXIS, rhOrientY));


        }
        if (useRhOrientZ) {
            constraints.add(new EMBROrientationConstraint(EMBRBodyGroup.RIGHT_ARM,
                    EMBRJoint.RIGHT_WRIST, EMBRNormal.Z_AXIS, rhOrientZ));


        }

        if (useRightSwivel) {
            constraints.add(new EMBRSwivelConstraint(EMBRBodyGroup.RIGHT_ARM, rSwivel)); //~ frmt


        }
    }

    /**
     * Create an EMBRScript in string form that contains only this pose.
     * Used e.g. by the "assume pose" facility.
     *
     * This method is intended for packaging a pose to be sent to the
     * agent. There is probably no way to recycle the sendable script
     * otherwise (note the tweaked timings).
     *
     * @param agentCharacter to whom it shall concern
     * @return the generated one-pose-script
     */
    public String toSendableSequenceScript(String agentCharacter)
    {
        EMBRPoseSequence seq = new EMBRPoseSequence(agentCharacter);
        seq.setASAP(true);

        PoseModel p = new PoseModel(this);
        seq.addPose(p);

        p.setTime(100);
        p.setHoldDuration(100);

        p.extractCurrentPoseConstraints();

        String r = seq.toScript();


        return r;


    }

    public void clearConstraints()
    {
        super.constraints.clear();


    }

    /**
     * Give a human-readable string representation of this pose.
     */
    @Override
    public String toString()
    {
        return "\nPose {"
                + "\nId:        " + getId()
                + "\nInvoked:   " + _isActivated
                + "\nPhaseType: " + getSimplePhaseType() + " (mine)"
                + "\nPhaseType: " + getBMLPhaseType() + " (EMBR)"
                + "\nStartTime: " + super.getTime()
                + "\n HoldTime: " + super.getHoldDuration()
                + "\n TimeWarp: " + _warp.key + " (sigma = " + _warp.sigma + ")"
                + "\nComment:   " + comment
                + "\n"
                + "\nBrth-Freq: " + breathFreq
                + "\n}\n";


    }

    public boolean isActive() {
        return _isActivated;
    }

    public void setInvoked(boolean invoked)
    {
        _isActivated = invoked;


    }

    /**
     * Retrieve the pose's "simple" sync label (these are STROKE_STARTS etc.)
     * @return a {@link SimpleSyncTag} for the tag
     */
    public SimpleSyncTag getSimplePhaseType()
    {
        return _phase;


    }

    public void setSimplePhaseType(SimpleSyncTag phase)
    {
        _phase = phase;
        setBMLPhaseType(
                phase.toCorrespondingSyncLabel());


    }

    /**
     * Retrieve the pose's BML sync label (these are READY, RELAX, ...).
     * @return sync label
     */
    public BMLSyncLabel getBMLPhaseType()
    {
        return super.getSemantics();


    }

    /**
     * Set a BML sync label; the corresponding "simple" sync tag will
     * be chosen and set.
     *
     * @param phase the BML sync label.
     */
    public void setBMLPhaseType(BMLSyncLabel phase)
    {
        _phase = SimpleSyncTag.createFromSyncLabel(phase);


        super.setSemantics(phase);


    }

    public int getId()
    {
        return _id;


    }

    public EMBRTimeWarpConstraint getTimeWarp()
    {
        return _warp;


    }

    public void setTimeWarp(EMBRTimeWarpConstraint warp)
    {
        _warp = warp;


    }

    public void setTimeWarp(EMBRTimeWarpKey key, double sigma)
    {
        _warp = new EMBRTimeWarpConstraint(key, sigma);


    }

    public String getComment()
    {
        return super.comment;


    }

    /**
     * Set the comment (written after "#" and before sync statement) of
     * the underlying {@link EMBRPose}
     * @param comment
     */
    public void setComment(String comment)
    {
        super.comment = comment;


    }

    /**
     * Compare this pose to another one.
     * Poses are ordered by start time.
     *
     * However, two poses are considered equal only if they share
     * the same id (in which case they also are references to the
     * same PoseModel objects.
     *
     * Used for sorting in the sequence table, and for retrieving
     * or deleting poses in the PoseSequence pose array.
     *
     * @param other
     * @return order integer
     */
    @Override
    public int compareTo(PoseModel other)
    {
        if (this.getId() == other.getId()) {
            return 0;


        }
        return this.getTime() < other.getTime() ? -1 : 1;


    }

    /**
     * @return the _modified
     */
    public boolean isModified()
    {
        return _modified;


    }

    /**
     * @param modified the _modified to set
     */
    public void setModified(boolean modified)
    {
        this._modified = modified;


    }

    /**
     * @param id the _id to set
     */
    public void setId(int id)
    {
        this._id = id;
        setComment(
                "--- Pose " + id);

    }

    @Override
    public long getTime()
    {
        return useTimeWarpFactors ? (long) (time * timeWarpFactor) : time;
    }

    @Override
    public long getHoldDuration()
    {
        return useTimeWarpFactors ? (long) (holdDuration * holdWarpFactor) : holdDuration;
    }

    /**
     * @param motionWarpFactor the motionWarpFactor to set
     */
    public void setMotionWarpFactor(float motionWarpFactor)
    {
        this.motionWarpFactor = motionWarpFactor;
    }

    /**
     * @param holdWarpFactor the holdWarpFactor to set
     */
    public void setHoldWarpFactor(float holdWarpFactor)
    {
        this.holdWarpFactor = holdWarpFactor;
    }

    /**
     * @return the timeWarpFactor
     */
    public float getTimeWarpFactor()
    {
        return timeWarpFactor;
    }

    /**
     * @param timeWarpFactor the timeWarpFactor to set
     */
    public void setTimeWarpFactor(float timeWarpFactor)
    {
        this.timeWarpFactor = timeWarpFactor;
    }

    /**
     * @return the motionWarpFactor
     */
    public float getMotionWarpFactor()
    {
        return motionWarpFactor;
    }

    /**
     * @return the holdWarpFactor
     */
    public float getHoldWarpFactor()
    {
        return holdWarpFactor;
    }

    /**
     * @param useTimeWarpFactors the useTimeWarpFactors to set
     */
    public void setUseTimeWarpFactors(boolean useTimeWarpFactors)
    {
        this.useTimeWarpFactors = useTimeWarpFactors;
    }
}
