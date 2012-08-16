/*
 * BMLNonverbalBehavior.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.05.2009, 14:09:23
 */
package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.sync.BMLSyncPoint;

/**
 * Nonverbal behaviors have other sync point outputs than SpeechBehavior.
 *
 * @author Michael Kipp
 */
public abstract class BMLNonverbalBehavior extends BMLBehavior
{

    protected BMLNonverbalBehavior(BMLBlock parent) {
        super(parent);
    }

    @Override
    public String toXML()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("  <" + getBMLTag().toUpperCase() + " id=\"" + getID() + "\"");
        if (getAgent() != null) {
            sb.append(" agent=\"" + getAgent() + "\"");
        }
        if (this instanceof BMLTypedBehavior) {
            sb.append(" type=\"" + ((BMLTypedBehavior) this).getType() + "\"");
        }
        if (this instanceof BMLLexicalizedBehavior) {
            if (((BMLLexicalizedBehavior) this).getLexemeName() != null) {
                sb.append(" lexeme=\"" + ((BMLLexicalizedBehavior) this).getLexemeName() + "\"");
            }
        }
        // ensure order
        for (BMLSyncLabel sp : BMLSyncLabel.getAllLabels()) {
            BMLSyncPoint sy = _syncPoints.get(sp.toString());
            if (sy != null) {
                sb.append(" " + sy.getID() + "=\"" + sy.toXML() + "\"");
            }
        }
//        for (BMLSyncPoint sy : _syncPoints.values()) {
//            sb.append(" " + sy.getID() + "=\"" + sy.toXML() + "\"");
//        }
        sb.append(">\n");
        sb.append("  </" + getBMLTag().toUpperCase() + ">");
        return sb.toString();
    }

    /**
     * Ensures canonical order of sync points.
     */
    @Override
    public String toScript()
    {
        StringBuffer b = new StringBuffer();
        b.append("id=" + getID());

        for (BMLSyncLabel label : BMLSyncLabel.getAllLabels()) {
            BMLSyncPoint sp = _syncPoints.get(label.toString());
            if (sp != null) {
                b.append(" " + sp.toScript());
            }
        }
        return b.toString();
    }
}
