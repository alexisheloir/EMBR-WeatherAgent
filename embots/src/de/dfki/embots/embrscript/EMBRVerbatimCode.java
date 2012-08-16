/*
 * EMBRVerbatimCode.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 09.09.2009, 20:35:23
 */
package de.dfki.embots.embrscript;

/**
 * DO NOT USE THIS
 *
 * @deprecated 
 * @author Michael Kipp
 */
public class EMBRVerbatimCode implements EMBRElement
{

    private String _text;

    public EMBRVerbatimCode(String text)
    {
        _text = text;
    }

    @Override
    public String toScript()
    {
        return _text;
    }

    @Override
    public void offset(long delta)
    {
    }
}
