package de.dfki.embots.bml.translate;

import de.dfki.embots.bml.behavior.BMLFaceType;
import de.dfki.embots.embrscript.EMBRConstraint;
import de.dfki.embots.embrscript.EMBRMorphKey;
import de.dfki.embots.embrscript.EMBRMorphTargetConstraint;
import de.dfki.embots.embrscript.EMBRPose;
import de.dfki.embots.framework.EMBOTSConstants;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Creates mapping from BML tag to EMBR morph targets. The mapping is defined
 * in a config file (config directory).
 *
 * @author Michael Kipp
 */
public class FaceMapping
{

    private static final String PREFIX = "face.default.";
    private HashMap<BMLFaceType, EMBRPose> _bmlToEmbrMap = new HashMap<BMLFaceType, EMBRPose>();

    public FaceMapping() throws IOException
    {
        File file = new File(EMBOTSConstants.CONFIG_DIR, EMBOTSConstants.FACE_MAPPING);
        Properties prop = new Properties();
        prop.load(new FileReader(file));
        fillMap(prop);
    }

    /**
     * Read constraints from properties.
     */
    private void fillMap(Properties prop)
    {
        for (BMLFaceType bml : BMLFaceType.values()) {
            EMBRPose pose = new EMBRPose();
            int i = 1;
            while (i > 0) {
                String target = prop.getProperty(PREFIX + bml.toSymbol() + ".target." + i);
                String weight = prop.getProperty(PREFIX + bml.toSymbol() + ".weight." + i);
                if (target != null && weight != null) {
                    EMBRMorphTargetConstraint morphTarget = new EMBRMorphTargetConstraint();
                    morphTarget.key = EMBRMorphKey.get(target);
                    morphTarget.value = Float.parseFloat(weight);
                    pose.constraints.add(morphTarget);
                } else {
                    i = -1; // terminate loop
                }
                i++;
            }
            if (pose.constraints.size() > 0) {
                _bmlToEmbrMap.put(bml, pose);
            }
        }
    }

    public int size()
    {
        return _bmlToEmbrMap.size();
    }

    public List<EMBRMorphTargetConstraint> getMorphConstraints(BMLFaceType bml)
    {
        List<EMBRMorphTargetConstraint> result = new ArrayList<EMBRMorphTargetConstraint>();
        EMBRPose pose = _bmlToEmbrMap.get(bml);
        if (pose != null) {
            for (EMBRConstraint c : pose.constraints) {
                EMBRMorphTargetConstraint mt = new EMBRMorphTargetConstraint();
                mt.key = ((EMBRMorphTargetConstraint) c).key;
                mt.value = ((EMBRMorphTargetConstraint) c).value;
                result.add(mt);
            }
        }
        return result;
    }
}
