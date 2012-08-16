/*
 * BMLLexicalizedBehavior.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 31.08.2009, 23:22:50
 */
package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.embrscript.EMBRPoseSequence;

/**
 * @author Michael Kipp (kipp@dfki.de)
 */
public interface BMLLexicalizedBehavior
{

    public void setLexemeName(String name);

    public String getLexemeName();

    public BehaviorLexeme getLexeme();

    public void setLexeme(BehaviorLexeme seq);
}
