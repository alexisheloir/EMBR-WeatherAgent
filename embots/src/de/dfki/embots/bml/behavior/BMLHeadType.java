package de.dfki.embots.bml.behavior;

import java.util.HashMap;
import java.util.Map;

/**
 * Head types: nod and shake.
 *
 * @author Michael Kipp
 */
public enum BMLHeadType
{

    NOD("nod"),
    SHAKE("shake");

    private String _symbol;
    private static Map<String, BMLHeadType> _symbol2enum =
            new HashMap<String, BMLHeadType>();

    static {
        for (BMLHeadType f : values()) {
            _symbol2enum.put(f.toSymbol(), f);
        }
    }

    private BMLHeadType(String symbol)
    {
        _symbol = symbol;
    }

    public String toSymbol()
    {
        return _symbol;
    }

    public static BMLHeadType parseHeadType(String symbol)
    {
        return _symbol2enum.get(symbol.toLowerCase());
    }
}
