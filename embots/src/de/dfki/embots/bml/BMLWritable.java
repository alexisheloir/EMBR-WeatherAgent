/*
 * BMLWritable.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.05.2009, 11:04:39
 */

package de.dfki.embots.bml;

/**
 * @author Michael Kipp (kipp@dfki.de)
 */
public interface BMLWritable {

    /**
     * Creates BML code.
     */
    public String toXML();

}
