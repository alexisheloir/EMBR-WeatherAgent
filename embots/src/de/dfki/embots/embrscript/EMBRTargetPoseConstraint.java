/*
 * EMBRTargetPoseConstraint.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.06.2009, 16:18:16
 */

package de.dfki.embots.embrscript;

/**
 * Represents a stored key pose (e.g. created in Blender). This is used
 * for hand shapes. Note that morph targets have a different representation.
 *
 * @author Michael Kipp
 */
public class EMBRTargetPoseConstraint extends EMBRConstraint {

    public String target;
    public double influence = -10;

    public EMBRTargetPoseConstraint() {
        super();
    }
    
    public EMBRTargetPoseConstraint(EMBRBodyGroup bodygroup, String target, double influence) {
        super(bodygroup);
        this.target = target;
        this.influence = influence;
    }
    
    @Override
    public String toScript()
    {
        return "  BEGIN POSE_TARGET" +
                "\n    BODY_GROUP:" + bodyGroup.toScript() +
                "\n    POSE_KEY:" + target +
                (influence >= -1 ? "\n    INFLUENCE:" + influence : "") +
                "\n  END\n";
    }



}
