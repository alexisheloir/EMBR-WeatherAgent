package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.reader.BMLReader;

/**
 *
 * @author Michael Kipp
 */
public class BMLGestureBehavior extends BMLNonverbalBehavior
        implements BMLTypedBehavior
{

    private String _type;

    protected BMLGestureBehavior(BMLBlock parent)
    {
        super(parent);
    }

    @Override
    public String toScript()
    {
        return BMLReader.GESTURE + " " + super.toScript();
    }

    @Override
    public void setType(String type)
    {
        _type = type;
    }

    @Override
    public String getType()
    {
        return _type;
    }

    @Override
    public String getBMLTag()
    {
        return BMLReader.GESTURE;
    }
}
