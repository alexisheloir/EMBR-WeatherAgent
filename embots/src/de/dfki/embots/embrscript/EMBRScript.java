package de.dfki.embots.embrscript;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a sequence of EMBRPoseSequence elements.
 *
 * For the moment the only addition to a pure pose sequence is the addition
 * of the time reset if you use createScript.
 * 
 * @author Michael Kipp
 */
public class EMBRScript implements EMBRElement
{

    public final static String BODY_GROUP = "BODY_GROUP";
    public final static String TARGET = "TARGET";
    public final static String END = "END";
    public final static String START = "START";
    public final static String POSE_KEY = "POSE_KEY";
    public final static String SWIVEL_ANGLE = "SWIVEL_ANGLE";
    public final static String BEHAVIOR_KEY = "BEHAVIOR_KEY";
    public final static String BEHAVIOR_VALUE = "BEHAVIOR_VALUE";
    public final static String MORPH_KEY = "MORPH_KEY";
    public final static String MORPH_VALUE = "MORPH_VALUE";
    public final static String SHADER_KEY = "SHADER_KEY";
    public final static String SHADER_VALUE = "SHADER_VALUE";
    public final static String JOINT = "JOINT";
    public final static String OFFSET = "OFFSET";
    public final static String DIRECTION = "DIRECTION";
    public final static String NORMAL = "NORMAL";
    public final static String TIME_POINT = "TIME_POINT";
    public final static String HOLD = "HOLD";
    public final static String POSITION_CONSTRAINT = "POSITION_CONSTRAINT";
    public final static String LOOK_AT_CONSTRAINT = "LOOK_AT_CONSTRAINT";
    public final static String SWIVEL_CONSTRAINT = "SWIVEL_CONSTRAINT";
    public final static String AUTONOMOUS_BEHAVIOR = "AUTONOMOUS_BEHAVIOR";
    public final static String MORPH_TARGET = "MORPH_TARGET";
    public final static String SHADER = "SHADER";
    public final static String CHARACTER = "CHARACTER";
    public final static String FADE_IN = "FADE_IN";
    public final static String FADE_OUT = "FADE_OUT";
    public final static String K_POSE = "K_POSE";
    public final static String TIME_WARP = "TIME_WARP";
    public final static String K_POSE_SEQUENCE = "K_POSE_SEQUENCE";
    public final static String ORIENTATION_CONSTRAINT = "ORIENTATION_CONSTRAINT";
    public final static String POSE_TARGET = "POSE_TARGET";
    public final static String INFLUENCE = "INFLUENCE";
    private List<EMBRPoseSequence> _elements = new ArrayList<EMBRPoseSequence>();

    public EMBRScript() {
    }

    public EMBRScript(EMBRPoseSequence ps) {
        _elements.add(ps);
    }

    @Override
    public void offset(long delta)
    {
        for (EMBRElement el : getElements()) {
            el.offset(delta);
        }
    }

    /**
     * Produces EMBRScript string without time reset.
     *
     * @return EMBRScript as a string
     */
    @Override
    public String toScript()
    {
        StringBuilder bf = new StringBuilder();
        for (EMBRElement el : getElements()) {
            bf.append(el.toScript());
        }
        return bf.toString();
    }

    /**
     * Allows the addition of a time reset, otherwise uses the toScript()
     * output.
     *
     * @param resetTime whether to signify a time reset at the beginning
     *
     * @return EMBRScript as a string
     */
    public String createScript(boolean resetTime)
    {
        StringBuilder bf = new StringBuilder();
        if (resetTime) {
            bf.append("TIME_RESET\n");
        }
        bf.append(toScript());
        return bf.toString();
    }

    /**
     * @return All EMBRPoseSequence elements
     */
    public List<EMBRPoseSequence> getElements()
    {
        return _elements;
    }

    /**
     * @param element EMBRPoseSequence element to add
     */
    public void addElement(EMBRPoseSequence element)
    {
        _elements.add(element);
    }

    /**
     * @param elements List of EMBRPoseSequence elements to add
     */
    public void addAllElements(List<EMBRPoseSequence> elements)
    {
        _elements.addAll(elements);
    }

    /**
     * Remove all EMBRPoseSequence elements.
     */
    public void clear()
    {
        _elements.clear();
    }
}
