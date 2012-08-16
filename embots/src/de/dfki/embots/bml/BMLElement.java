/*
 * BMLElement.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.05.2009, 10:57:13
 */
package de.dfki.embots.bml;

import java.util.ArrayList;
import java.util.List;

/**
 * Every BML element must have an ID which is stored in this abstract class.
 *
 * @author Michael Kipp
 */
public abstract class BMLElement
{

    private static int _idCount = 0;
    private static List<String> _usedIDs = new ArrayList<String>();
    private String _id;

    public BMLElement() {
        setID(generateID());
    }
    
    /**
     * @return the _id
     */
    public String getID()
    {
        return _id;
    }

    /**
     * @param id the _id to set
     */
    public void setID(String id)
    {
        this._id = id;
        _usedIDs.add(id);
    }

    /**
     * Autogenerates an ID.
     */
    public static String generateID() {
        while (_usedIDs.contains("" + _idCount)) {
            _idCount++;
        }
        return "" + _idCount++;
    }
}
