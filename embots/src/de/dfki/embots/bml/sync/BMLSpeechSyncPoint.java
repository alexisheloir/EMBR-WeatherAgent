/*
 * BMLSpeechSyncPoint.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.05.2009, 14:54:35
 */
package de.dfki.embots.bml.sync;

import de.dfki.embots.bml.behavior.BMLBehavior;

/**
 * @author Michael Kipp
 */
public class BMLSpeechSyncPoint extends BMLSyncPoint
{

    private int _position;

    public BMLSpeechSyncPoint(BMLBehavior owner, String id, int position)
    {
        super(owner, id);
        _position = position;
    }

    /**
     * Position in the word stream:
     * 0 = before first word
     * 1 = after first word
     * 2 = after second word
     * ...
     */
    public int getPosition()
    {
        return _position;
    }

    @Override
    public String toXML()
    {
        return "<sync id=\"" + getID() + "\""
                + (_time > -1 ? " time=\"" + _time + "\" " : "") + "/>";
    }
}
