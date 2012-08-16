package de.dfki.embots.behaviorbuilder.model;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.behaviorbuilder.view.PoseSequenceTable;
import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.embrscript.*;
import java.util.*;

/**
 * Wrapper class with similar functionality as EMBRPoseSequence.
 *
 * Keeps sequence of poses that are only assembled to a pose sequence
 * when needed for animation.
 *
 * NOTE: Poses are NOT kept in temporal sorted order. Instead, they
 * are re-indexed. The output is sorted by producing an EMBRPoseSequence
 * which does the sorting automatically (using TreeSet).
 *
 * @author Oliver Schoenleben
 */
public class PoseSequenceModel implements BBConstants
{

    private String _agentName = "";
    private String _lexeme = "";
    private boolean _modified;
    private long _startTime;
    /**
     * The poses to be dealt with.
     * Currently, <strong>code duplication</strong> from
     * {@link de.dfki.embots.embrscript.EMBRPoseSequence}
     */
    private List<PoseModel> _poses = new ArrayList<PoseModel>();
    private EMBRProperties _properties;

    /**
     * Offsets both start time and all poses.
     *
     * @param offset
     */
    public void offset(long offset)
    {
        _startTime += offset;
        for (PoseModel p : _poses) {
            p.offset(offset);
        }
    }

    public void setStartTime(long time)
    {
        _startTime = time;
    }

    /**
     * @return the startTime
     */
    public long getStartTime()
    {
        return _startTime;
    }

    /**
     * Since the poses are not ordered, all poses need to be checked.
     *
     * @return end time of latest pose (time + duration)
     */
    public long getEndTime()
    {
        if (_poses.size() > 0) {
            long endTime = 0;
            for (PoseModel p : _poses) {
                long poseEnd = p.getEndTime();
                if (poseEnd > endTime) {
                    endTime = poseEnd;
                }

//            PoseModel p = _poses.get(_poses.size() - 1);
//            return p.getTime() + p.getHoldDuration();
            }
            return endTime;
        } else {
            return getStartTime();
        }
    }

    public class ImportWarning
    {

        public static final int WARNING = 0, ERROR = 1;
        int level = 0;
        String message;

        public ImportWarning(int level, String message)
        {
            this.level = level;
            this.message = message;
        }

        public ImportWarning(String message)
        {
            this(WARNING, message);
        }
    }

    public void setTimeWarpFactors(float factor, float holdFactor)
    {
        for (PoseModel p : _poses) {
            p.setTimeWarpFactor(factor);
            p.setHoldWarpFactor(holdFactor);
        }
    }

    public void setUseTimeWarpFactors(boolean val)
    {
        for (PoseModel p : _poses) {
            p.setUseTimeWarpFactors(val);
        }
    }

    /**
     * Constructs the pose sequence object.
     *
     * @param agentName name of the character
     */
    public PoseSequenceModel(String agentName)
    {
        _agentName = agentName;
    }

    public void setAgentName(String name)
    {
        _agentName = name;
    }

    public List<PoseModel> getPoses()
    {
        return _poses;
    }

    /**
     * Returns the number of poses in the wrapped pose sequence
     * @return the size of the list of poses
     */
    public int size()
    {
        return _poses.size();
    }

    /**
     * Retrieves the pose with a given id
     * @param id the id of the seeked pose
     * @return the pose carrying the unique id
     */
    public PoseModel getPoseById(int id)
    {
        for (PoseModel p : _poses) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public void push(PoseModel currentPose, int numPoses, long duration)
    {
        int i = _poses.indexOf(currentPose);
        boolean countPoses = (numPoses > 0);
        if (i > -1) {
            int count = 0;
            for (PoseModel pm : _poses) {
                if (count++ >= i) {
                    pm.setTime(pm.getTime() + duration);
                    if (countPoses) {
                        numPoses--;
                        if (numPoses == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Getter for this class' {@link #_lexeme} member.
     *
     * @return the lexeme name of the gesture (i.e. pose sequence)
     */
    public String getLexeme()
    {
        return _lexeme;
    }

    public void setLexeme(String lexeme)
    {
        _lexeme = lexeme;
    }

    /**
     * Create a new pose, based on the given one.
     * The new pose will be set up using some reasonable initial timing
     * settings (start time, phase, ...)
     *
     * @param template pose to base the new pose on
     * @return created pose
     */
    public PoseModel createPoseModel(PoseModel template, long dur)
    {
        if (null == template) {
            template = PoseModel.DEFAULT_POSE;
        }

        PoseModel p = new PoseModel(
                size(), DEFAULT_NEW_POSES_ARE_ACTIVE,
                //template._phase.subsequent(), DEFAULT_TIME_WARP
                //template.getBMLPhaseType().subsequent(),
                template.getSimplePhaseType().subsequent(),
                //template.getStart() + template.getHold() * DEFAULT_START_FROM_HOLD_MULTIPLIER,
                template.getTime() + template.getHoldDuration() + dur,
                DEFAULT_NEW_POSE_HOLD_DURATION,
                new EMBRTimeWarpConstraint(DEFAULT_TIME_WARP_KEY, DEFAULT_TIME_WARP_SIGMA), "");
        p.copySettingsFrom(template);
        _poses.add(p);
        return p;
    }

    /**
     * Re-index the poses of the array. Useful after pose insertion/deletion.
     */
    public void reindexSequence()
    {
        int newId = 0;
        PoseModel poses[] = _poses.toArray(new PoseModel[0]);
        Arrays.sort(poses);
        for (PoseModel p : poses) {
            p.setId(newId++);
        }
    }

    /**
     * Cleanly remove a pose out of the sequence.
     * The remaining pose sequence will get {@link #reindexSequence()}ured.
     *
     * @param pose the pose to delete
     * @return whether the list was actually ridded of a pose
     */
    public boolean removePose(PoseModel pose)
    {
        if (_poses.size() <= 1) {
            createPoseModel(null, 0);
            System.out.println("WARNING! Removed single pose.");
        }
        boolean r = _poses.remove(pose);
        reindexSequence();
        return r;
    }

    public void removeAllPoses()
    {
        _poses.clear();
        removePose(null);
    }

    private EMBRPoseSequence createEmptyPoseSequence()
    {
        EMBRPoseSequence sequence = new EMBRPoseSequence(_agentName);
//        sequence.setASAP(true);
        sequence.startTime = _startTime;
        sequence.setLexeme(_lexeme);
        sequence.setProperties(_properties);
        return sequence;
    }

    /**
     * Generates the actual {@link EMBRPoseSequence} to generate the
     * code for the agent.
     *
     * @param fromPose start pose for the subsequence
     * @param toPose last pose (set to -1 to take till last existing one)
     *
     * @return the EMBR pose sequence comprising the here stored poses
     */
    public EMBRPoseSequence createPoseSequence(int fromPose, int toPose)
    {
        EMBRPoseSequence r = createEmptyPoseSequence();
        int count = 0;
        for (PoseModel p : _poses) {
            if (p._isActivated) {
                if (count >= fromPose && (toPose < 0 || count <= toPose)) {
                    r.addPose(p);
                }
            }
            count++;
        }
        return r;
    }

    /**
     * For continuously following poses.
     */
    public EMBRPoseSequence assembleQuickSequence(PoseModel cp)
    {
        EMBRPoseSequence r = createEmptyPoseSequence();
        PoseModel[] poses = _poses.toArray(new PoseModel[0]);
        Arrays.sort(poses);
        long start = 0;
        for (PoseModel template : poses) {
            PoseModel p = new PoseModel(template);
            start += 10;
            p.setTime(start);
            p.setHoldDuration(5);
            p.setSemantics(p.getBMLPhaseType()); // egal?
            p.extractCurrentPoseConstraints();
            r.addPose(p);

            if (template == cp) {
                break;
            }
        }
        return r;
    }

    /**
     * Build pose sequence from given {@link EMBRPoseSequence}. This is
     * called when a new .embr file is read.
     *
     * @param poseSequence the sequence to be read in
     * @return whether the sequence was successfully read and built
     */
    public List<ImportWarning> importSequence(EMBRPoseSequence poseSequence)
    {
        List<ImportWarning> warnings = new ArrayList<ImportWarning>();
        _lexeme = poseSequence.getLexeme();
        _properties = new EMBRProperties(poseSequence.getProperties());
        _startTime = poseSequence.startTime;
        _poses.clear();
        int id = 0;

        // create pose model for every pose
        for (EMBRPose pose : poseSequence.getPoses()) {
            PoseModel poseModel = importPose(id, pose, warnings);
            poseModel.extractCurrentPoseConstraints();
        }
        return warnings;
    }

    /**
     * @return whether modified
     */
    public boolean isModified()
    {
        return _modified;
    }

    /**
     * @param modified whether modified
     */
    public void setModified(boolean modified)
    {
        this._modified = modified;

        // if modified flag is cleared => do so for all poses
        if (!modified) {
            for (PoseModel p : _poses) {
                p.setModified(false);
            }
        }
    }

    /**
     * Creates the wrapper class instance for a given pose.
     */
    private PoseModel importPose(int id, EMBRPose pose, List<ImportWarning> warnings)
    {
        boolean success;
        PoseModel poseModel = createPoseModel(PoseModel.EMPTY_POSE, 0);
        poseModel.setId(id++);
        poseModel._isActivated = true; // non-invoked do not get saved
        BMLSyncLabel semantics = pose.getSemantics();
        if (semantics != null) {
            poseModel.setSimplePhaseType(SimpleSyncTag.createFromSyncLabel(semantics));
        }
        poseModel.setTime(pose.getTime());
        poseModel.setHoldDuration(pose.getHoldDuration());
        poseModel.comment = pose.comment;
        poseModel.setProperties(new EMBRProperties(pose.getProperties()));

        // process constraints
        for (EMBRConstraint constraint : pose.constraints) {
            if (constraint instanceof EMBRTargetPoseConstraint) {
                EMBRTargetPoseConstraint c = (EMBRTargetPoseConstraint) constraint;
                if (c.bodyGroup == EMBRBodyGroup.SHOULDERS) {
                    poseModel.useShoulder = true;
                    poseModel.shoulderValue = c.influence;
                } else {
                    System.err.println("Unknown body group for shoulders EMBRTargetPoseConstraint: " + c.bodyGroup);
                    success = false;


                    // THIS MUST BE CHANGED: CURRENTLY THE HANDSHAPE CONSTRAINT
                    // IS READ AS TARGET POSE
                    /*
                    EMBRHandshapeConstraint hs = new EMBRHandshapeConstraint(constraint.bodyGroup, EMBRHandshape.get(((EMBRTargetPoseConstraint) constraint).target));
                    if (hs.bodyGroup == EMBRBodyGroup.LEFT_HAND) {
                    poseModel.lhShape = hs._handshape;
                    } else if (c.bodyGroup == EMBRBodyGroup.RIGHT_HAND) {
                    poseModel.rhShape = hs._handshape;
                    } else {
                    System.err.println("Unknown body group for handshape specified.");
                    success = false;
                    }
                     *
                     */
                }
            } else if (constraint instanceof EMBRHandshapeConstraint) {
                EMBRHandshapeConstraint hs = (EMBRHandshapeConstraint) constraint;
                if (hs.bodyGroup == EMBRBodyGroup.LEFT_HAND) {
                    poseModel.lhShape = hs._handshape;
                } else if (hs.bodyGroup == EMBRBodyGroup.RIGHT_HAND) {
                    poseModel.rhShape = hs._handshape;
                } else {
                    System.err.println("Unknown body group for handshape specified.");
                    success = false;
                }
            } else if (constraint instanceof EMBRAutonomousBehaviorConstraint) {
                EMBRAutonomousBehaviorConstraint c = (EMBRAutonomousBehaviorConstraint) constraint;
                poseModel.useBreathing = true;
                if (c.key == EMBRAutonomousBehaviorKey.BREATHING_AMPLITUDE) {
                    poseModel.breathAmpl = c.value;
                } else if (c.key == EMBRAutonomousBehaviorKey.BREATHING_FREQUENCY) {
                    poseModel.breathFreq = c.value;
                } else {
                    System.err.println("Unknown autonomous behaviour specified: " + c.key);
                    success = false;
                }
            } else if (constraint instanceof EMBRLookAtConstraint) {
                EMBRLookAtConstraint c = (EMBRLookAtConstraint) constraint;
                poseModel.useGazeDir = true;
                poseModel.lookAtBodyGroup = c.bodyGroup;
                poseModel.lookAt = c.target;
            } else if (constraint instanceof EMBRShaderConstraint) {
                EMBRShaderConstraint c = (EMBRShaderConstraint) constraint;
                if (c.key == EMBRShaderKey.BLUSHING) {
                    poseModel.useShader = true;
                    poseModel.shade = c.shade;
                } else {
                    System.err.println("Unknown shader key specified.");
                    success = false;
                }
            } else if (constraint instanceof EMBRMorphTargetConstraint) {
                EMBRMorphTargetConstraint c = (EMBRMorphTargetConstraint) constraint;
                poseModel.addMorph(c.key, c.value);
            } else if (constraint instanceof EMBRPositionConstraint) {
                // POSITION CONSTRAINT
                EMBRPositionConstraint c = (EMBRPositionConstraint) constraint;
                if (c.bodyGroup == EMBRBodyGroup.LEFT_ARM) {
                    poseModel.useLPos = true;
                    poseModel.lhand = c.target;
                } else if (c.bodyGroup == EMBRBodyGroup.RIGHT_ARM) {
                    poseModel.useRPos = true;
                    poseModel.rhand = c.target;
                } else if (c.bodyGroup == EMBRBodyGroup.SPINE) {
                    poseModel.useTorsoPosition = true;
                    poseModel.torsoPosition = c.target;
                } else {
                    System.err.println("Unknown body group for position specified: " + c.bodyGroup);
                    success = false;
                }
            } else if (constraint instanceof EMBROrientationConstraint) {
                // ORIENTATION CONSTRAINT
                EMBROrientationConstraint c = (EMBROrientationConstraint) constraint;
                // Torso
                if (c.bodyGroup == EMBRBodyGroup.SPINE) {
                    if (c.normal == EMBRNormal.Y_AXIS) {
                        poseModel.useTorsoOrient = true;
                        poseModel.torsoOrient = c.direction;
                    } else {
                        warnings.add(new ImportWarning("Pose " + poseModel.getId() + ": Axis " + c.normal + " not imported."));
                    }
                } else if (c.bodyGroup == EMBRBodyGroup.LEFT_ARM) {
                    // Hand orientation
                    if (c.normal == EMBRNormal.Y_AXIS) {
                        poseModel.useLhOrientY = true;
                        poseModel.lhOrientY = c.direction;
                    } else if (c.normal == EMBRNormal.Z_AXIS) {
                        poseModel.useLhOrientZ = true;
                        poseModel.lhOrientZ = c.direction;
                    } else {
                        warnings.add(new ImportWarning("Pose " + poseModel.getId() + ": Axis " + c.normal + " not imported."));
                    }
                } else if (c.bodyGroup == EMBRBodyGroup.RIGHT_ARM) {
                    if (c.normal == EMBRNormal.Y_AXIS) {
                        poseModel.useRhOrientY = true;
                        poseModel.rhOrientY = c.direction;
                    } else if (c.normal == EMBRNormal.Z_AXIS) {
                        poseModel.useRhOrientZ = true;
                        poseModel.rhOrientZ = c.direction;
                    } else {
                        warnings.add(new ImportWarning("Pose " + poseModel.getId() + ": Axis " + c.normal + " not imported."));
                    }
                } else if (c.bodyGroup == EMBRBodyGroup.HEAD_NECK) {
                    // Head orientation
                    if (c.normal == EMBRNormal.Z_AXIS) {
                        poseModel.useHeadOrientZ = true;
                        poseModel.headOrientZ = c.direction;
                    } else if (c.normal == EMBRNormal.Y_AXIS) {
                        poseModel.useHeadOrientY = true;
                        poseModel.headOrientY = c.direction;
                    } else {
                        warnings.add(new ImportWarning("Pose " + poseModel.getId() + ": Axis " + c.normal + " not imported."));
                    }
                } else {
                    System.err.println("Unknown body group for orientation constraint specified: " + c.bodyGroup);
                    success = false;
                }
            } else if (constraint instanceof EMBRSwivelConstraint) {
                EMBRSwivelConstraint c = (EMBRSwivelConstraint) constraint;
                if (c.bodyGroup == EMBRBodyGroup.LEFT_ARM) {
                    poseModel.useLeftSwivel = true;
                    poseModel.lSwivel = c.angle;
                } else if (c.bodyGroup == EMBRBodyGroup.RIGHT_ARM) {
                    poseModel.useRightSwivel = true;
                    poseModel.rSwivel = c.angle;
                } else {
                    System.err.println("Unknown body group for swivel specified.");
                    success = false;
                }
            } else {
                System.err.println("Unknown constraint specified: " + constraint.getClass());
                System.err.println("Details:");
                System.err.println(constraint.toScript());
                System.err.println("============");
                success = false;
            }
        }
        return poseModel;
    }
}
