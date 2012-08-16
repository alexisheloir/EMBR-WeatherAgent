package de.dfki.embots.bml.behavior;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.reader.BMLReader;

/**
 *
 * @author Michael Kipp
 */
public class BMLFaceBehavior extends BMLNonverbalBehavior
        implements BMLTypedBehavior
{

    private BMLFaceType _type;

    protected BMLFaceBehavior(BMLBlock parent) {
        super(parent);
    }
    
    @Override
    public String toScript()
    {
        return BMLReader.FACE + " " + super.toScript();
    }

    @Override
    public void setType(String type)
    {
        _type = BMLFaceType.parseFaceType(type);
        if (_type == null)
            System.out.println("################### NO FACE TYPE: " + type);
    }

    public BMLFaceType getFaceType() {
        return _type;
    }

    @Override
    public String getType()
    {
        return _type.toSymbol();
    }

    @Override
    public String getBMLTag()
    {
        return BMLReader.FACE;
    }
}
