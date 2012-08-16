package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.reader.BMLReader;

/**
 * @author Michael Kipp
 */
public class BMLGazeBehavior extends BMLNonverbalBehavior
{
    protected BMLGazeBehavior(BMLBlock parent) {
        super(parent);
    }

    @Override
    public String toScript()
    {
        return BMLReader.GAZE + " " + super.toScript();
    }

    @Override
    public String getBMLTag()
    {
        return BMLReader.GAZE;
    }

}
