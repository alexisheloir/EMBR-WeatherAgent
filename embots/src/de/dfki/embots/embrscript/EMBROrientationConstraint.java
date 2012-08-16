package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBROrientationConstraint extends EMBRArticulatorConstraint
{

    public Triple direction;
    public EMBRNormal normal;

    public EMBROrientationConstraint()
    {
    }

    public EMBROrientationConstraint(EMBRNormal normal, Triple direction)
    {
        this.direction = direction;
        this.normal = normal;
    }

    public EMBROrientationConstraint(EMBRBodyGroup bodyGroup, EMBRJoint articulator, EMBRNormal normal, Triple direction)
    {
        super(bodyGroup, articulator);
        this.direction = direction;
        this.normal = normal;
    }

    //~ bug: direction != normal, double-check
    public EMBROrientationConstraint(EMBRBodyGroup bodyGroup, EMBRJoint articulator, Triple normal)
    {
        super(bodyGroup, articulator);
        this.direction = normal;
    }

    @Override
    public String toScript()
    {
        String result = "  BEGIN ORIENTATION_CONSTRAINT";
        result = result + "\n    BODY_GROUP:" + bodyGroup.toScript();
        result = result + "\n    DIRECTION:" + direction.toString();
        result = result + "\n    JOINT:" + _articulator.toScript();
        if (normal != null) {
            result = result + "\n    NORMAL:" + normal.toScript();
        }
        result = result + "\n  END\n";
        return result;
    }
}
