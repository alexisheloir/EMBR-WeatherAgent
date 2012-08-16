package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public abstract class EMBRArticulatorConstraint extends EMBRConstraint
{
    public EMBRJoint _articulator;

    public EMBRArticulatorConstraint() {}

    public EMBRArticulatorConstraint(EMBRJoint articulator) {
        this._articulator = articulator;
    }

    public EMBRArticulatorConstraint(EMBRBodyGroup bodyGroup, EMBRJoint articulator) {
        super(bodyGroup);
        this._articulator = articulator;
    }
}
