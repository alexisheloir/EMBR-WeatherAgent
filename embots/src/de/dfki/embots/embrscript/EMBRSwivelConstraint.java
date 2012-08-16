package de.dfki.embots.embrscript;

/**
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public class EMBRSwivelConstraint extends EMBRArticulatorConstraint {

    public double angle;

    public EMBRSwivelConstraint() {
    }

    public EMBRSwivelConstraint(double angle) {
        this.angle = angle;
    }

    public EMBRSwivelConstraint(EMBRBodyGroup bodyGroup, double angle) {
        this(angle);
        this.bodyGroup = bodyGroup;
    }

    public EMBRSwivelConstraint(EMBRBodyGroup bodyGroup, EMBRJoint joint, double angle) {
        this(bodyGroup, angle);
        this._articulator = joint;
    }

    public String toScript() {
        return "  BEGIN SWIVEL_CONSTRAINT" +
                "\n    BODY_GROUP:" + bodyGroup.toScript() +
                "\n    SWIVEL_ANGLE:" + angle +
                (null != _articulator
                    ? "\n    JOINT:" + _articulator.toScript()
                    : ""
                ) +
                "\n  END\n";
    }
}
