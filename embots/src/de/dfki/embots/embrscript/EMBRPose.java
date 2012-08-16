package de.dfki.embots.embrscript;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single pose, specified with a list of constraints.
 * 
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRPose implements EMBRElement
{

    public final static String TAG = "K_POSE";
    protected long time;
    protected long holdDuration = 0;
    public List<EMBRConstraint> constraints = new ArrayList<EMBRConstraint>();
    public boolean relativeTime = false;
    private BMLSyncLabel _semantics;
    public String comment = "";
    private EMBRProperties _properties;

    public EMBRPose()
    {
    }

    /**
     * Copy constructor. However, constraints are not copied deeply.
     */
    public EMBRPose(EMBRPose p)
    {
        time = p.time;
        holdDuration = p.holdDuration;
        relativeTime = p.relativeTime;
        comment = p.comment;
        constraints.addAll(p.constraints); // NOT DEEP !!!
        _semantics = p._semantics;
        _properties = new EMBRProperties(p._properties);
    }

    public EMBRPose(long time)
    {
        this.time = time;
    }

    public long getTime()
    {
        return time;
    }

    public long getHoldDuration()
    {
        return holdDuration;
    }

    public long getEndTime()
    {
        return time + holdDuration;

    }

    public void setSemantics(BMLSyncLabel sem)
    {
        _semantics = sem;
    }

    public BMLSyncLabel getSemantics()
    {
        return _semantics;
    }

    @Override
    public void offset(long delta)
    {
        setTime(getTime() + delta);
    }

    @Override
    public String toScript()
    {
        StringBuilder bf = new StringBuilder();
        bf.append(" BEGIN " + TAG);
        if (comment.length() > 0) {
            bf.append("  # ").append(comment);
        }
        if (_semantics != null && !BMLSyncLabel.UNDEFINED.equals(_semantics)) {
            bf.append((comment.length() == 0 ? "  # " : "") + " --- SYNC:" + _semantics);
        }
        if (_properties != null && _properties.size() > 0) {
            bf.append(_properties.toScript());
        }
        bf.append("\n  TIME_POINT:" + (relativeTime ? "+" : "") + (getTime() > 200 ? (getTime()) : 200)
                + "\n  HOLD:" + getHoldDuration() + "\n");
        for (EMBRConstraint c : constraints) {
            bf.append(c.toScript());
        }
        bf.append(" END\n");
        return bf.toString();
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time)
    {
        this.time = time;
    }

    /**
     * @param holdDuration the holdDuration to set
     */
    public void setHoldDuration(long holdDuration)
    {
        this.holdDuration = holdDuration;
    }

    /**
     * @return the _properties
     */
    public EMBRProperties getProperties()
    {
        if (_properties == null) {
            _properties = new EMBRProperties();
        }
        return _properties;
    }

    /**
     * @param properties the _properties to set
     */
    public void setProperties(EMBRProperties properties)
    {
        this._properties = properties;
    }
}
