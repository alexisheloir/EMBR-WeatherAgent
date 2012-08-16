/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.embrscript;

import java.util.HashMap;

/**
 * Represent Joints, mostly used as tagets in kinematic constraints
 *
 * @author Alexis Heloir
 */
public enum EMBRJoint
{
    NONE("none"),
    HEAD("head"),
    HEADDIR("headDir"),
    LEFT_WRIST("lhand"),
    RIGHT_WRIST("rhand"),
    LEFT_HUMERUS("rhumerus"),
    RIGHT_HUMERUS("lhumerus"),
    SPINE4("spine4"),
    NECK("neck");

    private String _symbol;
    private static HashMap<String, EMBRJoint> _symbol2joint = new HashMap<String, EMBRJoint>();

    static {
        for (EMBRJoint g: values()) {
            _symbol2joint.put(g.toScript().toLowerCase(), g);
        }
    }

    EMBRJoint(String s)
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
    public static EMBRJoint parseJoint(String str) {
        return _symbol2joint.get(str.trim().toLowerCase());
    }
}
