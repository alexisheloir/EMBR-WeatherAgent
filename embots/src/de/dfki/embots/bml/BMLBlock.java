package de.dfki.embots.bml;

import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.exception.BMLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a BML block. A BML block contains a number of single behaviors.
 * 
 * @author Michael Kipp
 */
public class BMLBlock extends BMLElement implements BMLWritable
{

    private String _agent;
    private HashMap<String, BMLBehavior> _idToElement = new HashMap<String, BMLBehavior>();
    private List<BMLBehavior> _orderedElements = new ArrayList<BMLBehavior>();
    private List<BMLException> _exceptions = new ArrayList<BMLException>();

    public void addException(BMLException e)
    {
        _exceptions.add(e);
    }

    /**
     * @return Exceptions that occurred during e.g. parsing the BML block.
     */
    public List<BMLException> getExceptions()
    {
        return _exceptions;
    }

    public Collection<BMLBehavior> getBehaviors()
    {
        return _orderedElements;
    }

    public void addBehavior(BMLBehavior el)
    {
        _idToElement.put(el.getID(), el);
        _orderedElements.add(el);
    }

    public void addBehaviors(List<BMLBehavior> list)
    {
        for (BMLBehavior b : list) {
            addBehavior(b);
        }
    }

    public BMLBehavior findElement(String id)
    {
        return _idToElement.get(id);
    }

    public BMLSyncPoint[] collectSyncPoints()
    {
        List<BMLSyncPoint> ls = new ArrayList<BMLSyncPoint>();
        for (BMLBehavior beh : getBehaviors()) {
            ls.addAll(beh.getSyncPoints());
        }
        BMLSyncPoint[] result = new BMLSyncPoint[ls.size()];
        return (BMLSyncPoint[]) ls.toArray(result);
    }

    @Override
    public String toXML()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<BML id=\"" + getID() + "\"");
        if (_agent != null) {
            sb.append(" agent=\"" + _agent + "\"");
        }

        sb.append(">\n");
        for (BMLBehavior b : getBehaviors()) {
            sb.append(b.toXML() + "\n");
        }

        // print exceptions
        if (!_exceptions.isEmpty()) {
            sb.append("\n<!-- EXCEPTIONS (" + _exceptions.size() + ")");
            for (BMLException ex : _exceptions) {
                sb.append("\n     * " + ex.toString());
            }
            sb.append("\n-->\n");
        }

        sb.append("</BML>");
        return sb.toString();
    }

    public String toScript()
    {
        StringBuffer buf = new StringBuffer();
        for (BMLBehavior e : getBehaviors()) {
            buf.append(e.toScript() + "\n");
        }
        return buf.toString();
    }

    @Override
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        for (BMLBehavior el : getBehaviors()) {
            b.append(el + "\n");
        }
        return b.toString();
    }

    public String getLexemeID()
    {
        return "bml";
    }

    public String getDefaultAgent()
    {
        return _agent;
    }

    /**
     * @param defaultAgent Name of default agent
     */
    public void setAgent(String agent)
    {
        _agent = agent;
    }

    public String getAgent()
    {
        return _agent;
    }
}
