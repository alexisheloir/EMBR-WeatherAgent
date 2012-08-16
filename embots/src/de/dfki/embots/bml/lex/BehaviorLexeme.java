package de.dfki.embots.bml.lex;

import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.embrscript.EMBRPoseSequence;
import java.util.List;

/**
 * Stores a single lexeme.
 *
 * @author Michael Kipp
 */
public interface BehaviorLexeme
{

    public String getName();

    public EMBRPoseSequence getEMBRScript();

    public PoseSemantics getPoseSemantics(BMLSyncLabel label);

    /**
     * @return Sync labels in the order they occur in the pose
     * sequence.
     */
    public List<BMLSyncLabel> getOrderedSyncLabels();

    /**
     * Hold behaviors require special handling.
     * stroke_start is mapped to ready, stroke_end/relax to stroke_start.
     *
     * @return
     */
    public boolean isHoldBehavior();
}
