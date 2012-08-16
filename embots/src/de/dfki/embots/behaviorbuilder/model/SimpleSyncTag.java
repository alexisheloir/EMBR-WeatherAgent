package de.dfki.embots.behaviorbuilder.model;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping between simplified tags and real BML tags.
 *
 * @author Olier Schoenleben
 * @author Michael Kipp
 */
public enum SimpleSyncTag
{

    UNDEFINED("(undefined)"),
    ACTUAL_START("Start"),
    STROKE_STARTS("Stroke begins"),
    STROKE_ENDS("Stroke finished"),
    ACTUAL_END("End");
    private String _symbol;
    private static final Map<String, SimpleSyncTag> lookup = new HashMap<String, SimpleSyncTag>();

    static {
        for (SimpleSyncTag s : EnumSet.allOf(SimpleSyncTag.class)) {
            lookup.put(s.getSymbol(), s);
        }
    }

    SimpleSyncTag(String s)
    { // ~ make public?
        _symbol = s;
    }

    public String getSymbol()
    {
        return _symbol;
    }

    @Override
    public String toString()
    {
        return getSymbol(); // also used for the JComboBox (no place for fancy stuff here)
    }

    public SimpleSyncTag subsequent()
    {
        switch (this) {
            case ACTUAL_START:
                return STROKE_STARTS;
            case STROKE_STARTS:
                return STROKE_ENDS;
            case STROKE_ENDS:
                return ACTUAL_END;
            case ACTUAL_END:
                return ACTUAL_START;
            default:
                return UNDEFINED;
        }
    }

    public static SimpleSyncTag createFromSyncLabel(BMLSyncLabel bmlSyncLabel)
    {
        switch (bmlSyncLabel) {
            case START:
                return ACTUAL_START;
            case READY:
                return STROKE_STARTS;
            case STROKE_END:
                return STROKE_ENDS;
            case END:
                return ACTUAL_END;
            default:
                return UNDEFINED;
        }
    }

    public BMLSyncLabel toCorrespondingSyncLabel()
    {
        switch (this) {
            case ACTUAL_START:
                return BMLSyncLabel.START;
            case STROKE_STARTS:
                return BMLSyncLabel.READY;
            case STROKE_ENDS:
                return BMLSyncLabel.STROKE_END;
            case ACTUAL_END:
                return BMLSyncLabel.END;
            default:
                return BMLSyncLabel.UNDEFINED;
        }
    }
}
