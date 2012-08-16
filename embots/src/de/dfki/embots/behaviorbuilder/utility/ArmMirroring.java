package de.dfki.embots.behaviorbuilder.utility;

import de.dfki.embots.behaviorbuilder.utility.BBConstants;
import de.dfki.embots.embrscript.Triple;

/**
 * Class comprising "mirror" operations for positions in the EMBRGui.
 *
 * @author Oliver Schoenleben
 */
public class ArmMirroring implements BBConstants
{

    /** All mirror modes known to ArmMirroring */
    public enum MirrorMode
    {

        NONE("none"), MIRROR("mirror"), ALTERNATE("alternate height"), PARALLEL("parallel");
        private String _symbol;

        MirrorMode(String symbol)
        {
            _symbol = symbol;
        }

        @Override
        public String toString()
        {
            return _symbol;

        }
    }
    /** The current mirror mode. */
    MirrorMode _mode = MirrorMode.NONE; //< the default
    /**
     * Determines the parameter for some of the modes, namely:
     *
     * - for ALTERNATE mode: the height of the mirroring point
     * - for PARALLEL mode: the (fixed) distance between the entities
     *
     * (An entity might be the hand position, for hand position mirroring.)
     *
     * DEPENDENCY: the argument's type must be synced with the type
     *             underlying the Triple datastructure.
     *
     * @see Triple
     */
    double _arg; // is set to relevant parameter

    /**
     * Get the currently set mirroring mode.
     *
     * @return the current mode
     */
    public MirrorMode getMode()
    {
//        System.out.println("mode <- NONE");
        return _mode;
    }

    /**
     * Set that there be no mirroring whatsoever.
     * ArmMirroring operations requested while this mode is active will
     * return *null*.
     */
    public void setModeToNone()
    {
        _mode = MirrorMode.NONE;
    }

    /**
     * Set actual mirroring to be the mirroring mode, using a given
     * yz-plane as the mirror.
     *
     * @param mirrorX the mirror plane, by its x-coordinate
     */
    public void setModeToMirror(double mirrorX)
    {
        _mode = MirrorMode.MIRROR;
        _arg = mirrorX;
    }

    /**
     * Set actual mirroring to be the mirroring mode, using a preset default
     * yz-plane as the mirror.
     */
    public void setModeToMirror()
    {
        setModeToMirror(DEFAULT_MIRROR_X_LOC);
    }

    /**
     * Set actual mirroring to be the mirroring mode, preserving the
     * mirror plane, whose x-coordinate is computed as the mean of
     * the given 3d-points' x-values.
     *
     * @param lh the left-hand point to determine the mirror
     * @param rh the right-hand point to determine the mirror
     */
    public void setModeToMirror(Triple lh, Triple rh)
    {
        setModeToMirror((rh.x + lh.x) / 2);
    }

    /**
     * Set /alternate/ mirroring mode, using a given mirror height.
     *
     * @param mirrorHeight the height of the mirror, as z-coordinate
     */
    public void setModeToAlternate(double mirrorHeight)
    {
        _mode = MirrorMode.ALTERNATE;
        _arg = mirrorHeight;
    }

    /**
     * Set /alternate/ mirroring mode, using a default mirror height.
     */
    public void setModeToAlternate()
    {
        setModeToAlternate(DEFAULT_ALTERNATE_Z_LOC);
    }

    /**
     * Set /alternate/ mirroring mode, preserving the mirror height.
     *
     * The mirror xy-axis' height (the z-level) is overtaken from the
     * current given z-values, i.e. the arithmetic mean of the
     * left-hand-z and the right-hand-z
     *
     * @param lh The left-hand point (for EMBRPoser, this is the left hand's position)
     * @param rh The right-hand point
     */
    public void setModeToAlternate(Triple lh, Triple rh)
    {
        setModeToAlternate((lh.z + rh.z) / 2);
    }

    /**
     * Set /parallel/ mirroring mode, using the given fixed hand distance.
     * 
     * @param fixedDistance the distance between the two side points
     */
    public void setModeToParallel(double fixedDistance)
    {
        _mode = MirrorMode.PARALLEL;
        _arg = fixedDistance;
    }

    /**
     * Set /parallel/ mirroring mode, using the default distance.
     * Not longer used by the EMBRPoser
     */
    public void setModeToParallel()
    {
        setModeToParallel(DEFAULT_PARALLEL_DIST);
    }

    /**
     * Set /parallel/ mirroring mode, using the distance between two given
     * points.
     *
     * @param lh left-hand point
     * @param rh right-hand point
     */
    public void setModeToParallel(Triple lh, Triple rh)
    {
        setModeToParallel(lh.x - rh.x);
    }

    /**
     * Get an array of all possible mirroring modes, as defined in this class.
     * @return all modes defined here.
     */
    public static MirrorMode[] getPossibleModes()
    {
        return MirrorMode.values();
    }

    /**
     * Retrienve a {@link Triple} defining the point that mirrors the
     * given point, according to the currently set mirroring mode.
     *
     * @param t the point to be mirrored
     * @return the mirror point
     */
    public Triple correspond(Triple t)
    {
        switch (_mode) {
            case MIRROR:
                return mirror(t);
            case ALTERNATE:
                return alternate(t);
            case PARALLEL:
                return parallel(t);
            default:
                return null; //< mode: NONE
        }
    }

    /**
     * Perform (actual) mirroring of a given point
     *
     * @param t the given point
     * @return the mirrored point
     */
    public Triple mirror(Triple t)
    {
        //return new Triple(_arg - t.x, t.y, t.z);

        // MK: returned to simple mirroring
        return new Triple(-t.x, t.y, t.z);
    }

    /**
     * Perform alternate mirroring of a given point
     *
     * @param t the given point
     * @return the mirrored point
     */
    public Triple alternate(Triple t)
    {
        Triple result = mirror(t);
        result.z = 2 * _arg - result.z;
        return result;
        //return new Triple(t.x, t.y, _arg - t.z);
    }

    /**
     * Perform parallel "mirroring" of a given point
     *
     * @param t the given point
     * @return the mirrored point
     */
    public Triple parallel(Triple t)
    {
        return new Triple(-_arg + t.x, t.y, t.z);
    }
}
