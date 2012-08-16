package de.dfki.embots.bml.sync;

import de.dfki.embots.bml.behavior.BMLBehavior;

/**
 * Constraint that points to another constraint, e.g.
 * 
 *    <gesture stroke="g1:end"/>
 * 
 * or
 *  
 *    <gesture stroke="before(g1:end-1.1)" />
 * 
 * @author Michael Kipp
 */
public class BMLRelativeSyncPoint extends BMLSyncPoint
{

    public enum Relation
    {

        EQUAL("equal"), BEFORE("before"), AFTER("after");
        private String _symbol;

        Relation(String s)
        {
            _symbol = s;
        }

        @Override
        public String toString()
        {
            return _symbol;
        }
    }
    private Relation _relation = Relation.EQUAL;
    private double _offset = 0d;
    private BMLSyncPoint _refConstraint;

    public BMLRelativeSyncPoint(BMLBehavior owner, String id)
    {
        super(owner, id);
    }

    public void setRelation(Relation r)
    {
        _relation = r;
    }

    public Relation getRelation()
    {
        return _relation;
    }

    public void setOffset(double offset)
    {
        _offset = offset;
    }

    public double getOffset()
    {
        return _offset;
    }

    public void setRefConstraint(BMLSyncPoint con)
    {
        _refConstraint = con;
    }

    public BMLSyncPoint getRefConstraint()
    {
        return _refConstraint;
    }

    /**
     * Returns what would go inside the constraint in the XML, e.g.
     * for <gesture stroke="g1:end"/> would return "g1:end"
     */
    @Override
    public String toXML()
    {
        String refBehID = _refConstraint == null ? "UNDEF" : _refConstraint.getBehavior().getID();
        String refID = _refConstraint == null ? "UNDEF" : _refConstraint.getID();
        if (_time > -1)
            return _time + "";
        if (_relation.equals(Relation.EQUAL)) {
            return refBehID + ":" + refID;
        } else {
            String offset = _offset > 0d ? "+" + _offset : (_offset < 0d ? "" + _offset : "");
            return _relation.toString() + "(" + refBehID + ":" + refID +
                    offset + ")";
        }

    }

    @Override
    public String toString()
    {
        return "[RelSyncPoint owner=" + _owner.getID() + " id=" + getID() +
                " rel=" + _relation.toString() + " offset=" + _offset + "]";
    }
}
