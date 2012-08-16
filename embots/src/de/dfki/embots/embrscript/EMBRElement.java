package de.dfki.embots.embrscript;

/**
 *
 * @author Michael Kipp
 */
public interface EMBRElement
{
    //~getTag()?

    public String toScript();

    /**
     * Move whole behavior by the given time.
     *
     * @param delta Offset.
     */
    public void offset(long delta);
}
