package de.dfki.embots.embrscript;

import java.util.HashMap;

/**
 * Represent kinematic chains.
 * 
 * @author Michael Kipp
 */
public enum EMBRBodyGroup
{
    ALL("all"),
    EYES("eyes"),
    UPPER_BODY_NO_ARMS("upperBodyNoArms"),
    SHOULDERS("shoulders"),
    HEAD_NECK("headNeck"),
    HEAD_THORAX("headThorax"),
    HEAD_ABDOMEN("headAbdomen"),
    LEFT_HAND("lhand"),
    RIGHT_HAND("rhand"),
    LEFT_ARM("larm"),
    RIGHT_ARM("rarm"),
    SPINE("spine");





    private String _symbol;
    private static HashMap<String, EMBRBodyGroup> _symbol2bodygroup = new HashMap<String, EMBRBodyGroup>();

    static {
        for (EMBRBodyGroup g: values()) {
            _symbol2bodygroup.put(g.toScript().toLowerCase(), g);
        }
    }

    EMBRBodyGroup(String s)
    {
        _symbol = s;
    }

    public String toScript()
    {
        return _symbol;
    }

    /**
     * Not case-sensitive!
     */
    public static EMBRBodyGroup parseBodyGroup(String str) {
        return _symbol2bodygroup.get(str.trim().toLowerCase());
    }
}