package de.dfki.embots.bml.sync;

import de.dfki.embots.bml.behavior.BMLBehavior;

/**
 * Virtual sync points are inserted because they are pointed to from
 * some other sync point or are used to express behavior-internal constraints.
 *
 * @author Michael Kipp
 */
public class BMLVirtualSyncPoint extends BMLSyncPoint
{

    public BMLVirtualSyncPoint(BMLBehavior owner, String id)
    {
        super(owner, id);
    }
}
