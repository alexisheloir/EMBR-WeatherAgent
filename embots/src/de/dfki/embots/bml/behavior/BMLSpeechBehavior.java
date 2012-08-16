/*
 * BMLSpeechBehavior.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 20.05.2009, 16:14:24
 */
package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.BMLElement;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.sync.BMLSpeechSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a speech block by a single speaker (agent). Currently,
 * this is tailored to synthesized speech with the TTS software
 * OpenMARY.
 *
 * @author Michael Kipp
 */
public class BMLSpeechBehavior extends BMLBehavior
{

    private List<BMLSpeechWord> _words = new ArrayList<BMLSpeechWord>();
    private String _voice = null; // voice of the TTS (currently OpenMARY)
    private long  _wait = 0; // in milliseconds

    public String getVoice() {
        return _voice;
    }

    public void setVoice(String _voice) {
        this._voice = _voice;
    }

    public long getWait() {
        return _wait;
    }

    public void setWait(long _wait) {
        this._wait = _wait;
    }
    

    protected BMLSpeechBehavior(BMLBlock parent)
    {
        super(parent);
    }

    public String getText()
    {
        StringBuilder buf = new StringBuilder();
        for (BMLSpeechWord w : _words) {
            buf.append(w.getWord() + " ");
        }
        return buf.toString();
    }

    public void clearWords()
    {
        _words.clear();
    }

    public void addWord(String word)
    {
        _words.add(new BMLSpeechWord(word));
    }

    public void addWord(String word, boolean startSync, boolean endSync)
    {
        BMLSpeechWord w = new BMLSpeechWord(word);
        _words.add(w);
        if (startSync) {
            BMLSyncPoint sp = new BMLSyncPoint(this, BMLElement.generateID());
            w.setStartSync(sp);
            _syncPoints.put(sp.getID(), sp);
        }
        if (endSync) {
            BMLSyncPoint sp = new BMLSyncPoint(this, BMLElement.generateID());
            w.setEndSync(sp);
            _syncPoints.put(sp.getID(), sp);
        }
    }

    public BMLSyncPoint addSyncPointBefore(int index)
    {
        if (index < _words.size() && index >= 0) {
            BMLSyncPoint sp = new BMLSyncPoint(this, BMLElement.generateID());
            _words.get(index).setStartSync(sp);
            _syncPoints.put(sp.getID(), sp);
            return sp;
        } else {
            return null;
        }
    }

    public BMLSyncPoint addSyncPointAfter(int index)
    {
        if (index < _words.size() && index >= 0) {
            BMLSyncPoint sp = new BMLSyncPoint(this, BMLElement.generateID());
            _words.get(index).setEndSync(sp);
            _syncPoints.put(sp.getID(), sp);
            return sp;
        } else {
            return null;
        }
    }

    public String toXML()
    {
        StringBuilder sb = new StringBuilder("  <" + getBMLTag() + " id=\"" + getID() + "\"");
        if (getAgent() != null) {
            sb.append(" agent=\"" + getAgent() + "\"");
        }
        if (getVoice() != null) {
            sb.append(" voice=\"" + getVoice() + "\"");
        }
        sb.append(">\n");
        if (_words.size() > 0) {
            sb.append("    <text>");
            int count = 0;
            for (BMLSpeechWord w : _words) {
                if (w.getStartSync() != null) {
                    sb.append(w.getStartSync().toXML() + " ");
                }
                sb.append(w.getWord() + " ");
                for (BMLSyncPoint sy : _syncPoints.values()) {
                    if (((BMLSpeechSyncPoint) sy).getPosition() == count + 1) {
                        sb.append(sy.toXML() + " ");
                    }
                }
                if (w.getEndSync() != null) {
                    sb.append(w.getEndSync().toXML() + " ");
                }
                count++;
            }
            sb.append("</text>\n");
        } else {
            for (BMLSyncPoint sy : _syncPoints.values()) {
                if (sy instanceof BMLSpeechSyncPoint) {
                    sb.append(((BMLSpeechSyncPoint) sy).toXML() + " ");
                }
            }
        }
        sb.append("  </" + getBMLTag() + ">\n");
        return sb.toString();
    }

    @Override
    public String getBMLTag()
    {
        return BMLReader.SPEECH;
    }
}
