package de.dfki.embots.behaviorbuilder.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * List of behaviors. Each behavior is a pose sequence. The corresponding
 * EMBRScript class is EMBRScript.
 *
 * @author Michael Kipp
 */
public class BehaviorSequenceModel extends AbstractListModel
{

    public static final long DEFAULT_START_TIME = 300;
    private static final int DEFAULT_INBETWEEN_PAUSE = 30;
    private String name;
    private ArrayList<PoseSequenceModel> _behaviorModels = new ArrayList<PoseSequenceModel>();

    public BehaviorSequenceModel()
    {
    }

    public ArrayList<PoseSequenceModel> getBehaviorModels()
    {
        return _behaviorModels;
    }

    public void clear()
    {
        _behaviorModels.clear();
    }

    public void add(PoseSequenceModel model)
    {
        _behaviorModels.add(model);
        fireIntervalAdded(this, _behaviorModels.size() - 1, _behaviorModels.size() - 1);
    }

    /**
     * Puts all behaviors into sequential timing, i.e.
     * every behavior starts the moment the previous one ends. Note
     * that the start time of the first behavior is always set to
     * a default (e.g. zero).
     */
    public void adjustTiming()
    {
        PoseSequenceModel prev = null;
        for (PoseSequenceModel seq : _behaviorModels) {
            if (prev == null) {
                // case: first behavior => set start time to default
                seq.offset(DEFAULT_START_TIME - seq.getStartTime());
            } else {
                // case: all other behaviors start when previous ends
                long start = seq.getStartTime();
                long end = prev.getEndTime();
                long offset = end - start + DEFAULT_INBETWEEN_PAUSE;
                seq.offset(offset);
            }
            prev = seq;
        }
    }

    /**
     * Moves single behavior (by index) to another position. Does nothing if
     * indeces are out of legal range.
     *
     * @param from specifies behavior to move
     * @param to target location
     * @return true if move was successful
     */
    public boolean move(int from, int to)
    {
        System.out.println("move " + from + " " + to);
        if (from >= 0 && to >= 0 && from < _behaviorModels.size() && to <= _behaviorModels.size() && to != from) {
            System.out.println("=> MOVE");
            PoseSequenceModel model = _behaviorModels.remove(from);
            _behaviorModels.add(to, model);
            fireContentsChanged(this, from, to);
            return true;
        } else {
            return false;
        }
    }

    public void delete(int pos) {
        if (pos >= 0 && pos < _behaviorModels.size()) {
            System.out.println("DEL");
            _behaviorModels.remove(pos);
            fireContentsChanged(this, pos, pos);
        }
    }

    public PoseSequenceModel get(int i)
    {
        return _behaviorModels.get(i);
    }

    @Override
    public int getSize()
    {
        return _behaviorModels.size();
    }

    /**
     * Here is where the display of elements is controlled.
     * @param i index number
     * @return object to display in list
     */
    @Override
    public Object getElementAt(int i)
    {
        PoseSequenceModel ps = _behaviorModels.get(i);
        return "[" + i + "] " + ps.getLexeme() + "   " + ps.getStartTime() + "-->" + ps.getEndTime();
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
