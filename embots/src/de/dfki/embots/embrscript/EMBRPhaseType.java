package de.dfki.embots.embrscript;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import java.util.HashMap;

/**
 * ~TODO (( This class is meant to replace EMBRPoseSemantics ))
 *
 * @deprecated this is done by {@link BMLSyncLabel}, actually
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public enum EMBRPhaseType
{

    START("START"),
    READY("READY"),
    STROKE_START("STROKE_START"),
    STROKE_APEX("STROKE_APEX"),
    STROKE_END("STROKE_END"),
    RELAX("RELAX"),
    END("END");
    private String _symbol;
    private static HashMap<String, EMBRPhaseType> _symbol2enum = new HashMap<String, EMBRPhaseType>();


    static {
        for (EMBRPhaseType s : values()) {
            _symbol2enum.put(s.toSymbol(), s);
        }
    }

    private EMBRPhaseType(String s)
    {
        _symbol = s;
    }

    public String toSymbol()
    {
        return _symbol;
    }

    public static EMBRPhaseType parsePoseSemantics(String line) {
        return _symbol2enum.get(line);
    }
}
