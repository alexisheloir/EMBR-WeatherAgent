package de.dfki.embots.bml.exception;

/**
 * Stores exceptions that occurred during BML reading.
 *
 * @author Michael Kipp
 */
public class BMLException
{

    protected String _message;

    /**
     * @param msg Description of what went wrong.
     */
    public BMLException(String msg) {
        _message = msg;
    }

    public String getMessage() {
        return _message;
    }

    @Override
    public String toString() {
        return "BMLException: " + _message;
    }
}
