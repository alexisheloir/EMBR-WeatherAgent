package de.dfki.embots.embrscript;

import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores meta-information about pose sequences and poses.
 *
 * @author Michael Kipp
 */
public class EMBRProperties
{

    private EnumMap<EMBRPropertyKey, String> _properties;

    public EMBRProperties()
    {
    }

    /**
     * Copy constructor
     * 
     * @param p object to copy
     */
    public EMBRProperties(EMBRProperties p)
    {
        if (p != null && p._properties != null) {
            _properties = new EnumMap<EMBRPropertyKey, String>(EMBRPropertyKey.class);
            _properties.putAll(p._properties);
        }
    }

    public void put(EMBRPropertyKey key, String value)
    {
        if (_properties == null) {
            _properties = new EnumMap<EMBRPropertyKey, String>(EMBRPropertyKey.class);
        }
        _properties.put(key, value);
    }

    public String get(EMBRPropertyKey key)
    {
        return _properties == null ? null : _properties.get(key);
    }

    public int size()
    {
        return _properties == null ? 0 : _properties.size();
    }

    public String toScript()
    {
        if (_properties == null) {
            return "";
        }
        if (_properties.size() == 0) {
            return "";
        }
        StringBuilder buff = new StringBuilder();
        for (Map.Entry<EMBRPropertyKey, String> entry : _properties.entrySet()) {
            buff.append(" [").append(entry.getKey().toScript()).append(" ").append(entry.getValue()).append("]");
        }
        return buff.toString();
    }

    /**
     * Fills properties with data contained in the string in the form of
     *
     * [MODALITY mouthing] [COMMENT das ist ein Kommentar]
     *
     * @param line
     */
    public static EMBRProperties parse(String line) throws UnknownEMBRPropertyKeyException
    {
        EMBRProperties result = new EMBRProperties();
        // the pattern looks for bracketed things
        Pattern p = Pattern.compile("(\\[[^\\]]*\\]\\s*)");
        Matcher m = p.matcher(line.trim());
        Pattern pairPattern = Pattern.compile("\\[([^\\[^\\s]+)\\s(.*)\\]");
        while (m.find()) {
            String pair = m.group().trim();
//            System.out.println("pari=" + pair);
            Matcher m2 = pairPattern.matcher(pair);
            if (m2.find()) {
                if (m2.groupCount() > 1) {
                    String key = m2.group(1);
                    String value = m2.group(2);
//                    System.out.println(key + "###" + value);
                    EMBRPropertyKey k = EMBRPropertyKey.get(key);
                    if (k == null) {
                        throw new UnknownEMBRPropertyKeyException("Unknown property key: " + key);
                    } else {
                        result.put(k, value);
                    }
                }
            }
        }
        return result.size() > 0 ? result : null;
    }
}
