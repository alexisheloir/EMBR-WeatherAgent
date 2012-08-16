/*
 * EMBRTargetPoseConstraint.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.06.2009, 16:18:16
 */
package de.dfki.embots.embrscript;

/**
 * Represents a stored key pose (e.g. created in Blender). This is used
 * for hand shapes. Note that morph targets have a different representation.
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRTimeWarpConstraint extends EMBRConstraint
{

    public EMBRTimeWarpKey key;
    public double sigma;

    public EMBRTimeWarpConstraint(EMBRTimeWarpKey key)
    {
        this(key, 0.0);
    }

    public EMBRTimeWarpConstraint(EMBRTimeWarpKey key, double sigma)
    {
        this.key = key;
        this.sigma = sigma;
    }

    @Override
    public String toScript()
    {
        return "  BEGIN MORPH_TARGET" +
                "\n    MORPH_KEY:" + key.toScript() +
                "\n    MORPH_VALUE:" + sigma
                + "\n  END\n";
    }
}
