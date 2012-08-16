package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.reader.BMLReader;

/**
 *
 * @author Michael Kipp
 */
public class BMLHeadBehavior extends BMLNonverbalBehavior implements BMLTypedBehavior
{

    private BMLHeadType _type;

    protected BMLHeadBehavior(BMLBlock parent)
    {
        super(parent);
    }

    @Override
    public String toScript()
    {
        return BMLReader.HEAD + " " + super.toScript();
    }

    @Override
    public void setType(String type)
    {
        _type = BMLHeadType.parseHeadType(type);
    }

    @Override
    public String getType()
    {
        return _type.toSymbol();
    }

    @Override
    public String getBMLTag()
    {
        return BMLReader.HEAD;
    }
}
