package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public abstract class EMBRConstraint implements EMBRElement
{

    public EMBRBodyGroup bodyGroup;

    public EMBRConstraint() {}

    public EMBRConstraint(EMBRBodyGroup bodyGroup) {
        this.bodyGroup = bodyGroup;
    }

    @Override
    public void offset(long d)
    {
    }
}
