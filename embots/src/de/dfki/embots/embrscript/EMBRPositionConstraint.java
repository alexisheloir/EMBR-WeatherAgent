package de.dfki.embots.embrscript;

/**
 * NOTE: time warp should be moved to pose at some point
 * @author Michael Kipp
 */
public class EMBRPositionConstraint extends EMBRArticulatorConstraint
{

    public Triple target;
    public Triple offset;
    //public String timeWarp = null; // e.g. "EXP;5" or "TAN;.5"

    public EMBRPositionConstraint(Triple target, Triple offset)
    {
        this.target = target == null ? new Triple() : target;
        this.offset = offset == null ? new Triple(0, 0, 0) : offset;
        bodyGroup = EMBRBodyGroup.ALL;
        _articulator = EMBRJoint.NONE;
    }
    
    public EMBRPositionConstraint(EMBRBodyGroup bodyGroup, EMBRJoint joint, Triple target, Triple offset) {
        this(target, offset);
        this._articulator = joint;
        this.bodyGroup = bodyGroup;
    }

    public EMBRPositionConstraint() {
        this(new Triple(0,0,0), new Triple(0,0,0));
    }

    @Override
    public String toScript()
    {
//        System.out.println("foo");
        return "  BEGIN POSITION_CONSTRAINT" +
                "\n    BODY_GROUP:" + bodyGroup.toScript() +                
                "\n    TARGET:" + target +
                "\n    JOINT:" + _articulator.toScript() +
                "\n    OFFSET:" + offset +
                "\n  END\n";
    }
}
