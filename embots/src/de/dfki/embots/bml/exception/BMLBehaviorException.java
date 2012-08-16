package de.dfki.embots.bml.exception;

/**
 * Exception that refers to an erroneous single behavior.
 *
 * @author Michael Kipp
 */
public class BMLBehaviorException extends BMLException
{
    private String _behaviorID;

    public BMLBehaviorException(String behID, String msg) {
        super(msg);
        _behaviorID = behID;
    }

    public String getBehaviorID() {
        return _behaviorID;
    }

    @Override
    public String toString() {
        return "BMLBehaviorException in \"" + _behaviorID + "\": " +
                _message;
    }

}
