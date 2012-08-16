package de.dfki.embots.bml.lex;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * The behavior lexicon stores templates for a given lexeme.
 *
 * The main idea for having this in a separate class is to store variants
 * of the same "lexeme" under the same token.
 *
 * @author Michael Kipp
 */
public class BehaviorLexicon
{

    public static final int LEXEME_ERROR = 1, LEXEME_WARNING = 0;
    HashMap<String, BehaviorLexeme> _entries = new HashMap<String, BehaviorLexeme>();
    private List<CorruptedLexeme> _corruptedLexemes = new ArrayList<CorruptedLexeme>();

    /**
     * A class for describing the problem with this lexeme.
     */
    public class CorruptedLexeme
    {

        BehaviorLexeme lexeme;
        String problemDescription;
        int problemLevel = LEXEME_WARNING;

        public CorruptedLexeme(BehaviorLexeme lex, String prob, int level)
        {
            lexeme = lex;
            problemDescription = prob;
            problemLevel = level;
        }

        @Override
        public String toString() {
            return "[Corrupt lexeme " + lexeme.getName() +
                    (problemLevel == LEXEME_ERROR ? " ERROR] " : "WARNING] ")
                    + problemDescription;
        }
    }

    public BehaviorLexicon()
    {
    }

    /**
     * Checks lexeme for correctness (sync points). If corrupted, the lexeme
     * will not be added but stored in the corrupted lexemes list.
     *
     * Minimal condition is that a lexeme contains:
     * - START
     * - READY
     * - END
     *
     * Also, labels must be in correct order.
     *
     * If one of the above is violated it results in a LEXEME_ERROR, the lexeme
     * is not added.
     *
     * TODO: Duplicate labels are fixed automatically and results in a LEXEME_WARNING
     * but the (fixed) lexeme will be added.
     */
    private boolean checkLexeme(BehaviorLexeme lexeme)
    {
        List<BMLSyncLabel> syncList = lexeme.getOrderedSyncLabels();

        // check minimal set
        BMLSyncLabel[] minimalSet = {BMLSyncLabel.START, BMLSyncLabel.READY, BMLSyncLabel.END};
        boolean containsMinimalSet = true;
        for (BMLSyncLabel label : minimalSet) {
            if (!syncList.contains(label)) {
                containsMinimalSet = false;
            }
        }

        // check order
        boolean correctOrder = true;
        int previous = -1;
        for (BMLSyncLabel label : syncList) {
            if (label != BMLSyncLabel.UNDEFINED) {
                if (label.getIndex() <= previous) {
                    correctOrder = false;
                    break;
                }
                previous = label.getIndex();
            }
        }

        if ((!containsMinimalSet) || (!correctOrder)) {
            String msg = "";
            if (!containsMinimalSet) {
                msg += "Does not contain all of {START, READY, END}. ";
            }
            if (!correctOrder) {
                msg += "Does not respect sync point order.";
            }
            _corruptedLexemes.add(new CorruptedLexeme(lexeme, msg, LEXEME_ERROR));
            return false;
        }
        return true;
    }

    /**
     * To make access case-insensitive, all lexeme names are made uppercase.
     *
     * @param name Behavior name
     * @param beh Lexeme object
     */
    public void putLexeme(String name, BehaviorLexeme beh)
    {
        if (checkLexeme(beh)) {
            _entries.put(name.toUpperCase(), beh);
        }
    }

    public BehaviorLexeme getLexeme(String name)
    {
        return _entries.get(name.toUpperCase());
    }

    public Set<String> getLexemeNames()
    {
        return _entries.keySet();
    }

    public String getLexemeNamesString()
    {
        StringBuffer b = new StringBuffer();
        for (String n : _entries.keySet()) {
            b.append(n + " ");
        }
        return b.toString();
    }

    public Collection<BehaviorLexeme> getLexemes()
    {
        return _entries.values();
    }

    public int size()
    {
        return _entries.size();
    }

    /**
     * @return the corrupted lexemes
     */
    public List<CorruptedLexeme> getCorruptedLexemes()
    {
        return _corruptedLexemes;
    }

    @Override
    public String toString()
    {
        return "[BehaviorLexicon size=" + _entries.size() + "]";
    }
}
