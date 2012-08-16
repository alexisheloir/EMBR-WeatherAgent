/*
 * EMBRHandshapeConstraint.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.06.2009, 16:20:23
 */
package de.dfki.embots.embrscript;

/**
 * @author Michael Kipp
 */
public class EMBRHandshapeConstraint extends EMBRArticulatorConstraint
{

    public EMBRHandshape _handshape;

    public EMBRHandshapeConstraint(EMBRBodyGroup bodygroup, EMBRHandshape handshape)
    {
        bodyGroup = bodygroup;
        _handshape = handshape;
    }

    @Override
    public String toScript()
    {
        return "  BEGIN POSE_TARGET" +
                "\n    BODY_GROUP:" + bodyGroup.toScript() +
                "\n    POSE_KEY:" + _handshape.toScript() +
                "\n  END\n";
    }
}
