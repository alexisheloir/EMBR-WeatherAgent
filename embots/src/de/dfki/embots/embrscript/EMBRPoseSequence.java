package de.dfki.embots.embrscript;

import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;

/**
 * A pose sequence corrensponds to a single animation, defined by successive
 * poses.
 * 
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRPoseSequence implements EMBRElement
{

    private static final String TAG = "K_POSE_SEQUENCE";
    public String character;
    public long startTime = 0;
    public long fadeIn = 0;
    public long fadeOut = 0;
    private TreeSet<EMBRPose> _poses;
    public String comment = "";
    private String _lexeme;
    public String _timeWarp = "";
    public boolean _isASAP = false;
    private EMBRProperties _properties;

    public EMBRPoseSequence()
    {
        _poses = new TreeSet<EMBRPose>(new Comparator<EMBRPose>()
        {

            @Override
            public int compare(EMBRPose o1, EMBRPose o2)
            {
                return o1.getTime() < o2.getTime() ? -1 : 1;
                //(o1.time == o2.time ? 0 : 1);
            }
        });
    }

    /**
     * Copy constructor
     */
    public EMBRPoseSequence(EMBRPoseSequence seq)
    {
        this();
        character = seq.character;
        startTime = seq.startTime;
        fadeIn = seq.fadeIn;
        fadeOut = seq.fadeOut;
        comment = seq.comment;
        _lexeme = seq._lexeme;
        _timeWarp = seq._timeWarp;
        for (EMBRPose p : seq._poses) {
            _poses.add(new EMBRPose(p));
        }
    }

    public EMBRPoseSequence(String character)
    {
        this();
        this.character = character;
    }

    public TreeSet<EMBRPose> getPoses()
    {
        return _poses;
    }

    public void addPose(EMBRPose p)
    {
        _poses.add(p);
    }

    public void addPoses(Collection<EMBRPose> poses)
    {
        //_poses.addAll(poses);
        for (EMBRPose p : poses) {
            addPose(p);
        }
    }

    public void setRelativeTimes(boolean val)
    {
        for (EMBRPose p : _poses) {
            p.relativeTime = val;
        }
    }

    public void setASAP(boolean val)
    {
        _isASAP = val;
    }

    /**
     * Moves all pose times by given offset.
     * Also moves the start time!
     *
     * @param delta Time to add.
     */
    @Override
    public void offset(long delta)
    {
        startTime += delta;
        for (EMBRPose p : _poses) {
            p.offset(delta);
        }
    }

    @Override
    public String toScript()
    {
        if (_poses.isEmpty()) {
            return "";
        }
        StringBuilder bf = new StringBuilder();
        bf.append("\nBEGIN " + TAG);
        if (comment.length() > 0) {
            bf.append("  # ").append(comment);
        }
        if (_lexeme != null) {
            bf.append(comment.length() > 0 ? "" : "  # ---").append(" LEXEME:").append(_lexeme);
        }
        if (_properties != null && _properties.size() > 0) {
            bf.append(_properties.toScript());
        }
        bf.append("\n CHARACTER:" + character
                + "\n START:" + (_isASAP ? "asap" : startTime) + //G. seq. start should be defined relative to the last start time
                "\n");
        if (fadeIn > 0) {
            bf.append(" FADE_IN:" + fadeIn + "\n");
        }
        if (fadeOut > 0) {
            bf.append(" FADE_OUT:" + fadeOut + "\n");
        }
        if (_timeWarp.length() > 0) {
            bf.append(" TIME_WARP:" + _timeWarp + "\n");
        }
        for (EMBRPose p : _poses) {
            bf.append(p.toScript());
        }
        bf.append("END\n");
        return bf.toString();
    }

    public String getLexeme()
    {
        return _lexeme;
    }

    public void setLexeme(String lexeme)
    {
        this._lexeme = lexeme;
    }

    /**
     * @return the _properties
     */
    public EMBRProperties getProperties()
    {
        if (_properties == null) {
            setProperties(new EMBRProperties());
        }
        return _properties;
    }

    /**
     * @param properties the _properties to set
     */
    public void setProperties(EMBRProperties properties)
    {
        this._properties = properties;
    }
}
