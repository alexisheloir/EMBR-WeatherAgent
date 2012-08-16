/*
 * EMBRTargetPoseConstraint.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 22.06.2009, 16:18:16
 */

package de.dfki.embots.embrscript;

/**
 * Represents a stored key pose (e.g. created in Blender). This is used
 * for facial expression and hand shapes.
 *
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRMorphTargetConstraint extends EMBRConstraint {

    //public String target;
    public EMBRMorphKey key;
    //public double weight;
    public double value;

    @Deprecated
    public EMBRMorphTargetConstraint(String keyAsString, double value) {
        this(EMBRMorphKey.get(keyAsString), value);
    }

    /**
     * Copy constructor.
     * @param template the constraint to be copied
     */
    public EMBRMorphTargetConstraint(EMBRMorphTargetConstraint template) {
        this.key = template.key;
        this.value = template.value;
    }

    public EMBRMorphTargetConstraint(EMBRMorphKey key, double value) {
        if (key != null)
        {
            this.key = key;
            this.value = value;
        }else
        {
            this.key=EMBRMorphKey.get("undefined");
            this.value=value;
        }
    }

    public EMBRMorphTargetConstraint() {
        this(EMBRMorphKey.get("undefined"), 0.0);
        // This constructor intentionally left blank
    }

    @Override
    public String toScript()
    {
        return "  BEGIN MORPH_TARGET" +
                //"\n    MORPH_KEY:" + target +
                "\n    MORPH_KEY:" + key.toScript() +
                //"\n    MORPH_KEY:" + "this is a morphTarget" +
                //"\n    MORPH_VALUE:" + value +
                "\n    MORPH_VALUE:" + value +
                "\n  END\n";
    }
}
