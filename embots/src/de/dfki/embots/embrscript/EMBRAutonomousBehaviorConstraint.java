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
 * @author Oliver Schoenleben
 */
public class EMBRAutonomousBehaviorConstraint extends EMBRConstraint {

    //public String target;
    public EMBRAutonomousBehaviorKey key;
    //public double weight;
    public double value;

    public EMBRAutonomousBehaviorConstraint(EMBRAutonomousBehaviorKey key, double value) {
        this.key = key;
        this.value = value;
    }

    public EMBRAutonomousBehaviorConstraint() {
        // This constructor intentionally left blank
    }

    public String toScript()
    {
        return "  BEGIN AUTONOMOUS_BEHAVIOR" +
                "\n    BEHAVIOR_KEY:" + key.toScript() +
                "\n    BEHAVIOR_VALUE:" + value +
                "\n  END\n";
    }



}
