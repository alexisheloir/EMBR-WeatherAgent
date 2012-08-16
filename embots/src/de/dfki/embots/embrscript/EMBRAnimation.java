/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.dfki.embots.embrscript;

import de.dfki.embots.bml.sync.BMLSyncLabel;

/**
 *
 * @author Oliver Schoenleben
 * @author Michael Kipp
 */
public class EMBRAnimation implements EMBRElement {

    public long time;
    public long holdDuration; //~TODO: needed in animations?
    public boolean relativeTime = false; //~TODO: needed here?
    public String _clip;
    private BMLSyncLabel _semantics;
    public String comment = "";

    public EMBRAnimation() { }

    public EMBRAnimation(long time) {
        setTime(time);
    }

    public EMBRAnimation(long time, String clip) {
        setTime(time);
        setClip(clip);
    }

    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

    public String getClip() { return this._clip; }
    public void setClip(String clip) { this._clip = clip; }

    public BMLSyncLabel getSemantics() { return this._semantics; }
    public void setSemantics(BMLSyncLabel sem) { _semantics = sem; }

    public void offset(long delta) { time += delta; }

    public String toScript() {
        StringBuffer bf = new StringBuffer();
        bf.append(" BEGIN ANIMATION");
        if (comment.length() > 0) {
            bf.append("  # " + comment);
        }
        if (_semantics != null) {
            bf.append((comment.length() == 0 ? "  # " : "") + " --- SYNC:" + _semantics);
        }
        bf.append("\n  TIME_POINT:" + (relativeTime ? "+" : "") + time +
                "\n  HOLD:" + holdDuration + "\n");

        bf.append(_clip);

        bf.append(" END\n");
        return bf.toString();
    }
}
