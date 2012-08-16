package de.dfki.embots.embrscript;

import java.util.HashMap;

/**
 *
 * @author Michael Kipp
 */
public enum EMBRPoseSemantics
{

    START("START"),
    END("END"),
    STROKE_START("STROKE_START"),
    STROKE_END("STROKE_END");
    private String _symbol;
    private static HashMap<String, EMBRPoseSemantics> _symbol2enum = new HashMap<String, EMBRPoseSemantics>();


    static {
        for (EMBRPoseSemantics s : values()) {
            _symbol2enum.put(s.toSymbol(), s);
        }
    }

    private EMBRPoseSemantics(String s)
    {
        _symbol = s;
    }

    public String toSymbol()
    {
        return _symbol;
    }

    public static EMBRPoseSemantics parsePoseSemantics(String line) {
        return _symbol2enum.get(line);
    }
}
