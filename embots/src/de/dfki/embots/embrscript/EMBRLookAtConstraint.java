package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRLookAtConstraint extends EMBRConstraint {

    public Triple target;

    public EMBRLookAtConstraint() { }
    
    public EMBRLookAtConstraint(Triple target) {
        this.target = target;
    }

    public EMBRLookAtConstraint(EMBRBodyGroup bodyGroup, Triple target) {
        this.bodyGroup = bodyGroup; //~TODO: restrict here?
        this.target = target;
    }

    public String toScript() {
        return "  BEGIN LOOK_AT_CONSTRAINT" +
                "\n    BODY_GROUP:" + bodyGroup.toScript() +
                "\n    TARGET:" + target +
                "\n  END\n";
    }
}
