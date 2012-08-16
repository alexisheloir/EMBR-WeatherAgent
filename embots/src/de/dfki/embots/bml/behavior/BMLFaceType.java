package de.dfki.embots.bml.behavior;

import de.dfki.embots.embrscript.EMBRFacialExpression;
import java.util.HashMap;
import java.util.Map;

/**
 * BML facial expressions, e.g.
 *
 * <face id="f1" type="happy" intensity=".8" />
 *
 * We follow the categorical terms as defined in EmotionML
 *
 * http://www.w3.org/TR/2010/WD-emotionml-20100729/#s2.2.1
 *
 * (except for sceptical)
 *
 * We tried to be consistent by taking a term that describes an emotional
 * state (adjective).
 *
 * @author Michael Kipp
 */
public enum BMLFaceType
{

    NEUTRAL("neutral"),
    HAPPY("happy"),
    ANGRY("angry"),
    DISGUSTED("disgusted"),
    AFRAID("afraid"),
    SAD("sad"),
    SURPRISED("surprised"),
    SCEPTICAL("sceptical"),
    EYEBROWS("eyebrows");

    private String _symbol;

    private static Map<String, BMLFaceType> _symbol2enum =
            new HashMap<String, BMLFaceType>();

    static {
        for (BMLFaceType f : values()) {
            _symbol2enum.put(f.toSymbol(), f);
        }
    }

    private BMLFaceType(String symbol)
    {
        _symbol = symbol;

        // the below is now defined in a config file:
        /*
        switch (this) {
            case SMILE: break;
            // smile closed 1.0
            // smile open 0.3
            // brow up left/right 0.7
            case ANGER: break;
            // anger 0.4
            // brow down left/right 1.0
            // squint left/right 0.3
            // sad 0.6
            case DISGUST: break;
            // disgust 0.75
            // brow up L 1.0
            // brow down R 0.5
            case FEAR: break;
            // fear 1.0
            case SAD: break;
            // sad 1.0
            // PHON_B_M_P 1.0
            case SURPRISE: break;
            // surprise 1.0
            case FROWN: break;
            // sad 1.0
            // disgust 0.25
            // brow in R/L 1.0
            // PHON_CH_J_SH 0.3
            case SCEPTICAL: break;
            // disgust 1.0
            // brow up L 1.0
            // brow down R 1.0
            // squint R 1.0
            // PHON_W 0.5
        }
         */
    }

    public String toSymbol()
    {
        return _symbol;
    }

    public static BMLFaceType parseFaceType(String symbol)
    {
        return _symbol2enum.get(symbol);
    }
}
