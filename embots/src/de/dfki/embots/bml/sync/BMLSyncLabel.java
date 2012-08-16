package de.dfki.embots.bml.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * Labels of basic BML sync points.
 * 
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public enum BMLSyncLabel
{

    UNDEFINED(-1, "(undefined)"),
    START(0, "start"),
    READY(1, "ready"),
    STROKE_START(2, "stroke_start", true),
    STROKE(3, "stroke"),
    STROKE_END(4, "stroke_end"),
    RELAX(5, "relax", true),
    END(6, "end");
    private String _symbol;
    private boolean _includesPoseHold = false; // indicates whether point is equivalent to time point after hold
    private static Map<String, BMLSyncLabel> _toLabel = new HashMap<String, BMLSyncLabel>();

    /**
     * Defines an ordering to determine which sync-label preceeds and succeeds
     * which other.
     * EMBRPoser trusts that this cycles, which holds as long as UNDEFINED
     * is not part of this order.
     */
    private static BMLSyncLabel[] _order = {START, READY, STROKE_START, STROKE, STROKE_END, RELAX, END};
    private int _index;

    static
    {
        for (BMLSyncLabel v : values()) {
            _toLabel.put(v.toString(), v);
        }
    }
    
    public int getIndex()
    {
        return _index;
    }

    public BMLSyncLabel subsequent()
    {
        return _index < _order.length - 1 ? _order[_index + 1] : _order[0];
    }

    public BMLSyncLabel previous()
    {
        return _index > 0 ? _order[_index - 1] : null;
    }

    private BMLSyncLabel(int index, String symbol)
    {
        _index = index;
        _symbol = symbol;
    }

    private BMLSyncLabel(int index, String symbol, boolean includesPoseHold)
    {
        this(index, symbol);
        _includesPoseHold = includesPoseHold;
    }

    public static BMLSyncLabel getSyncLabel(String syncname)
    {
        return _toLabel.get(syncname);
    }

    /**
     * @return All sync point labels in canonical order.
     */

    public static BMLSyncLabel[] getAllLabels() {
        return _order;
    }

    public boolean holdFollows() {
        return this == READY || this == STROKE_END;
    }

    /**
     * Indicates whether point is equivalent to time point after hold
     */
    public boolean includesPoseHold()
    {
        return _includesPoseHold;
    }

    @Override
    public String toString()
    {
        return _symbol;
    }
}
