package de.dfki.embots.embrscript;

import java.util.HashMap;
import java.util.Map;

/**
 * DEPRECATED !!!
 *
 * These are the basic morph targets available in EMBRScript. They were
 * taken from FaceGen.
 *
 * This has been replace by EMBRMorphKey which includes all available
 * morph targets, including visemes.
 *
 * @author Michael Kipp
 */
public enum EMBRFacialExpression {
    BASIS ("Basis"),
    SMILE_CLOSED ("ExpSmileClosed"),
    ANGER ("ExpAnger"),
    DISGUST ("ExpDisgust"),
    FEAR ("ExpFear"),
    SAD ("ExpSad"),
    SURPRISE ("ExpSurprise"),
    SMILE_OPEN ("ExpSmileOpen"), // looks evil
    BLINK_LEFT ("ModBlinkLeft"),
    BLINK_RIGHT ("ModBlinkRight"),
    BROW_DOWN_LEFT ("ModBrowDownLeft"),
    BROW_DOWN_RIGHT ("ModBrowDownRight"),
    BROW_UP_LEFT ("ModBrowUpLeft"),
    BROW_UP_RIGHT ("ModBrowUpRight"),
    BROW_IN_LEFT ("ModBrowInLeft"),
    BROW_IN_RIGHT ("ModBrowInRight"),
    EYE_SQUINT_LEFT ("ModEyeSquintLeft"),
    EYE_SQUINT_RIGHT ("ModEyeSquintRight"),
    LOWER_EYELIDS_RAISE ("ModLookUp"),
    EYELASHES_DOWN ("ModLookDown");

    private String _symbol;
    private final static Map<String, EMBRFacialExpression> _symbol2enum = new HashMap<String, EMBRFacialExpression>();

    static {
        for (EMBRFacialExpression f: values())
            _symbol2enum.put(f.toSymbol(), f);
    }

    private EMBRFacialExpression(String symbol)
    {
        _symbol = symbol;
    }

    public String toSymbol() {
        return _symbol;
    }

    public static EMBRFacialExpression parseFacialExpression(String symbol) {
        return _symbol2enum.get(symbol);
    }


}
