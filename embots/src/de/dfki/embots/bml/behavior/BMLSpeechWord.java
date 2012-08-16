/*
 * BMLSpeechWord.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 20.05.2009, 16:20:02
 */
package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.sync.BMLSyncPoint;

/**
 * @author Michael Kipp
 */
public class BMLSpeechWord
{

    private String _word;
    private double _startTime;
    private double _endTime;
    private BMLSyncPoint _startSync = null;
    private BMLSyncPoint _endSync = null;

    public BMLSpeechWord(String word)
    {
        _word = word;
    }

    /**
     * @return the _startTime
     */
    public double getStartTime()
    {
        return _startTime;
    }

    /**
     * @param startTime the _startTime to set
     */
    public void setStartTime(double startTime)
    {
        this._startTime = startTime;
    }

    /**
     * @return the _endTime
     */
    public double getEndTime()
    {
        return _endTime;
    }

    /**
     * @param endTime the _endTime to set
     */
    public void setEndTime(double endTime)
    {
        this._endTime = endTime;
    }

    /**
     * @return the _word
     */
    public String getWord()
    {
        return _word;
    }

    /**
     * @return the _startSync
     */
    protected BMLSyncPoint getStartSync()
    {
        return _startSync;
    }

    /**
     * @param startSync the _startSync to set
     */
    protected void setStartSync(BMLSyncPoint startSync)
    {
        this._startSync = startSync;
    }

    /**
     * @return the _endSync
     */
    protected BMLSyncPoint getEndSync()
    {
        return _endSync;
    }

    /**
     * @param endSync the _endSync to set
     */
    protected void setEndSync(BMLSyncPoint endSync)
    {
        this._endSync = endSync;
    }
}
