package de.dfki.embots.bml.exception;

import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.sync.BMLSyncLabel;

/**
 * Exception for the case that reference behavior B2 referred to a sync point P in
 * behavior B.
 *
 * @author Michael Kipp
 */
public class BMLUnderspecifiedLexemeException extends BMLException
{

    public BMLBehavior behavior;
    public BMLBehavior referencedFromBehavior;
    public String missingSyncPoint;

    public BMLUnderspecifiedLexemeException(BMLBehavior beh, BMLBehavior refFrom,
            String missingSync)
    {
        super("");
        behavior = beh;
        referencedFromBehavior = refFrom;
        missingSyncPoint = missingSync;
    }

    @Override
    public String getMessage()
    {
        return "Behavior \""
                + behavior.getID() + "\" does not have sync point \""
                + missingSyncPoint
                + "\" specified in lexeme \"" + behavior.getLexemeName() + 
                "\" " + (referencedFromBehavior != null ? (" (referenced by behavior \"" +
                referencedFromBehavior.getID() + "\"") : "");
    }
}
