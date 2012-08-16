package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.bml.*;
import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.sync.BMLRelativeSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import java.util.Collection;
import java.util.HashMap;

/**
 * Abstract class for behaviors like speech, gesture, head etc.
 *
 * Every behavior can have a number of sync points that are identified by ID.
 * Standard sync points are start, ready, stroke_start, ...
 *
 * However, like in the speech tag, sync points can also have arbitrary IDs.
 *
 * @author Michael Kipp
 */
public abstract class BMLBehavior extends BMLElement implements
        BMLWritable, BMLLexicalizedBehavior
{

    protected BMLBlock _parent;
    private String _gestureLexeme;
    private String _agent;
    protected BehaviorLexeme _lexeme;
    protected HashMap<String, BMLSyncPoint> _syncPoints = new HashMap<String, BMLSyncPoint>();

    public BMLBehavior(BMLBlock parent) {
        _parent = parent;
    }

    /**
     * Factory method.
     * 
     * @param type
     * @return behavior object
     */
    public static BMLBehavior getInstance(BMLBlock parent, String type)
    {
        if (type.toLowerCase().equals(BMLReader.GAZE)) {
            return new BMLGazeBehavior(parent);
        } else if (type.toLowerCase().equals(BMLReader.SPEECH)) {
            return new BMLSpeechBehavior(parent);
        } else if (type.toLowerCase().equals(BMLReader.GESTURE)) {
            return new BMLGestureBehavior(parent);
        } else if (type.toLowerCase().equals(BMLReader.HEAD)) {
            return new BMLHeadBehavior(parent);
        } else if (type.toLowerCase().equals(BMLReader.FACE)) {
            return new BMLFaceBehavior(parent);
        }
        return null;
    }

    /**
     * Compute pose time offsets according to the first sync point constraint
     * encountered.
     *
     * TODO: generalize offset computation to multiple sync points
     *
     * @return offset in seconds, or -1 if no sync was found
     */
//    public double computeOffset()
//    {
//        System.out.println("### compute for " + this);
//        if (_lexeme != null && _syncPoints.size() > 0) {
//
//            // iterate through sync points (take first one with corresponding pose)
//            for (BMLSyncPoint sy : _syncPoints.values()) {
//                if (sy.getSyncLabel() != null) {
//
//                    // find corresponding pose
//                    for (EMBRPose p : _lexeme.getEMBRScript().getPoses()) {
//                        if (p.getSemantics() != null) {
//                            System.out.println(" ## sem: " + p.getSemantics().toString());
//                            if (p.getSemantics().toString().equals(sy.getID())) {
//
//                                long holdtime = sy.getSyncLabel().includesPoseHold() ? p.getHoldDuration() : 0;
//                                double offset = sy.getTime() - (p.getTime() - holdtime) / 1000d;
//                                System.out.println("### found sync: " + sy + " offset=" + offset);
//                                return offset;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return -1d;
//    }

    public BMLSyncPoint getSyncPoint(String id)
    {
        return _syncPoints.get(id);
    }

    public BMLSyncPoint getSyncPoint(BMLSyncLabel label)
    {
        return getSyncPoint(label.toString());
    }

    public void addSyncPoint(BMLSyncPoint c)
    {
        _syncPoints.put(c.getID(), c);
    }

    public void removeSyncPoint(BMLSyncPoint p)
    {
        _syncPoints.remove(p.getID());
    }

    public Collection<BMLSyncPoint> getSyncPoints()
    {
        return _syncPoints.values();
    }

    public boolean hasSyncPoint(BMLSyncLabel label)
    {
        for (BMLSyncPoint sp : _syncPoints.values()) {
            if (sp.getSyncLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    public boolean changeSyncLabel(BMLSyncLabel oldLabel, BMLSyncLabel newLabel) {
//        System.out.println("CHANGE " + oldLabel + " to " + newLabel);
        BMLSyncPoint oldSync = getSyncPoint(oldLabel);
        if (oldSync == null)
            return false;
        else
            removeSyncPoint(oldSync);
        oldSync.setSyncLabel(newLabel);
        addSyncPoint(oldSync);

//        System.out.println("CHANGED BEHAVIOR: " + toScript());
        return true;
    }

    public abstract String getBMLTag();

    public String toScript()
    {
        StringBuffer b = new StringBuffer();
        b.append("id=" + getID());
        for (BMLSyncPoint sp : _syncPoints.values()) {
            b.append(" " + sp.toScript());
        }
        return b.toString();
    }

    @Override
    public void setLexemeName(String name)
    {
        _gestureLexeme = name;
    }

    @Override
    public String getLexemeName()
    {
        return _gestureLexeme;
    }

    @Override
    public BehaviorLexeme getLexeme()
    {
        return _lexeme;
    }

    @Override
    public void setLexeme(BehaviorLexeme lex)
    {
        _lexeme = lex;
    }

    @Override
    public String toString()
    {
        return "<BMLElement id=" + getID() + "/>";
    }

    public String getAgentTrans(String defaultAgent) {
        String ag = _agent != null ? _agent : _parent.getAgent();
        return ag == null ? defaultAgent : ag;
    }

    /**
     * @return the agent
     */
    public String getAgent()
    {
        return _agent;
    }

    /**
     * @param agent agent to set
     */
    public void setAgent(String agent)
    {
        _agent = agent;
    }
}
