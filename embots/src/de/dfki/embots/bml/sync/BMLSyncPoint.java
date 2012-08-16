package de.dfki.embots.bml.sync;

import de.dfki.embots.bml.*;
import de.dfki.embots.bml.behavior.BMLBehavior;

/**
 * Sync points can be one of the standard BML sync points (start, ready,
 * stroke_start etc.) or a specifically defined time point in the speech
 * stream (with <sync id="X"/>). For the standard sync points the ID
 * corresponds to the sync label ("start", "ready" etc.).
 *
 * @author Michael Kipp
 */
public class BMLSyncPoint extends BMLElement
{

    protected BMLBehavior _owner;
    protected BMLSyncLabel _syncLabel;
    protected double _time = -1d; // resolved time in seconds

    public BMLSyncPoint(BMLBehavior owner, String id)
    {
        _owner = owner;
        setID(id);
        _syncLabel = BMLSyncLabel.getSyncLabel(id);
    }

    public BMLSyncPoint(BMLBehavior owner, String id, double time)
    {
        this(owner, id);
        _time = time;
    }

    public BMLBehavior getOwner() {
        return _owner;
    }
    
    /**
     * Time in seconds.
     */
    public double getTime()
    {
        return _time;
    }

    /**
     * Time in seconds.
     */
    public void setTime(double time)
    {
        _time = time;
    }

    public String toScript()
    {
        return getID() + "=" + _time;
    }

    public BMLBehavior getBehavior() {
        return _owner;
    }

    public BMLSyncLabel getSyncLabel() {
        return _syncLabel;
    }

    public void setSyncLabel(BMLSyncLabel label) {
        _syncLabel = label;
        setID(label.toString());
    }

    @Override
    public String toString()
    {
        return "[SyncPoint owner=" + _owner.getID() + " id=" + getID() + " time=" + _time + " ]";
    }

    public String toXML() {
        return "" + _time;
    }
}
