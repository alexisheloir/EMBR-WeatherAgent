/*
 * EMBRScriptReader.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 31.08.2009, 08:42:32
 */
package de.dfki.embots.embrscript;

import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.lex.BehaviorLexemeImpl;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael Kipp
 * @author Oliver Schoenleben
 */
public class EMBRScriptReader
{

    private String getValue(String line)
    {
        int pos = line.indexOf(":");
        int posEnd = line.indexOf(" ", pos);
        return pos < 0 ? "" : (posEnd > -1 ? line.substring(pos + 1, posEnd).trim() : line.substring(pos + 1).trim());
    }

    private long getLongValue(String line)
    {
        String v = getValue(line);
        v = v.startsWith("+") ? v.substring(1) : v;
        return v.length() > 0 ? Long.parseLong(v) : -1;
    }

    private Triple getTripleValue(String line)
    {
        String v = getValue(line);
        String[] num = v.split(";");
        if (num.length == 3) {
            Triple result = new Triple();
            result.x = Float.parseFloat(num[0]);
            result.y = Float.parseFloat(num[1]);
            result.z = Float.parseFloat(num[2]);
            return result;
        }
        return null;
    }

    private EMBRConstraint readLookAtConstraint(BufferedReader rd) throws IOException
    {
        EMBRLookAtConstraint c = new EMBRLookAtConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                c.bodyGroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.TARGET)) {
                c.target = getTripleValue(line);
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    /**
     * Has to distinguish between a handshape constraint which looks like this:
     *
     *   BEGIN POSE_TARGET
     *     BODY_GROUP:rhand
     *     POSE_KEY:hands_open-relaxed
     *   END
     *
     * And a shoulders constraint which looks like this:
     *
     * BEGIN POSE_TARGET
     *   BODY_GROUP:shoulders
     *   POSE_KEY:shrug
     *   INFLUENCE:0.0
     * END
     */
    private EMBRConstraint readPoseTarget(BufferedReader rd) throws IOException
    {
        String line;
        EMBRBodyGroup bodygroup = null;
        String target = null;
        double influence = 0;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                bodygroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.POSE_KEY)) {
                target = getValue(line);
            } else if (line.startsWith(EMBRScript.INFLUENCE)) {
                influence = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        if (target != null) {
            EMBRTargetPose targetPose = EMBRTargetPose.get(target);
            if (targetPose != null) {
                // is shoulders (or other) constraint
                EMBRTargetPoseConstraint c = new EMBRTargetPoseConstraint();
                c.target = target;
                c.influence = influence;
                return c;
            } else {
                // must be hand shape
                EMBRHandshapeConstraint c = new EMBRHandshapeConstraint(bodygroup, EMBRHandshape.get(target));
                return c;
            }
        } else {
            return null;
        }


        /*
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                c.bodyGroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.POSE_KEY)) {
                c.target = getValue(line);
            } else if (line.startsWith(EMBRScript.INFLUENCE)) {
                c.influence = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
         *
         */
    }

    private EMBRConstraint readSwivelConstraint(BufferedReader rd) throws IOException
    {
        EMBRSwivelConstraint c = new EMBRSwivelConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                c.bodyGroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.SWIVEL_ANGLE)) {
                c.angle = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private EMBRConstraint readAutonomousBehaviourConstraint(BufferedReader rd) throws IOException
    {
        EMBRAutonomousBehaviorConstraint c = new EMBRAutonomousBehaviorConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BEHAVIOR_KEY)) {
                c.key = EMBRAutonomousBehaviorKey.get(getValue(line));
            } else if (line.startsWith(EMBRScript.BEHAVIOR_VALUE)) {
                c.value = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private EMBRConstraint readMorphTargetConstraint(BufferedReader rd) throws IOException
    {
        EMBRMorphTargetConstraint c = new EMBRMorphTargetConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.MORPH_KEY)) {
                c.key = EMBRMorphKey.get(getValue(line));
            } else if (line.startsWith(EMBRScript.MORPH_VALUE)) {
                c.value = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private EMBRConstraint readShaderConstraint(BufferedReader rd) throws IOException
    {
        EMBRShaderConstraint c = new EMBRShaderConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.SHADER_KEY)) {
                c.key = EMBRShaderKey.get(getValue(line));
            } else if (line.startsWith(EMBRScript.SHADER_VALUE)) {
                c.shade = Double.parseDouble(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private EMBRConstraint readPositionConstraint(BufferedReader rd) throws IOException
    {
        EMBRPositionConstraint c = new EMBRPositionConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                c.bodyGroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.JOINT)) {
                c._articulator = EMBRJoint.parseJoint(getValue(line));
            } else if (line.startsWith(EMBRScript.TARGET)) {
                c.target = getTripleValue(line);
            } else if (line.startsWith(EMBRScript.OFFSET)) {
                c.offset = getTripleValue(line);
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private EMBRConstraint readOrientationConstraint(BufferedReader rd) throws IOException
    {
        EMBROrientationConstraint c = new EMBROrientationConstraint();
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.BODY_GROUP)) {
                c.bodyGroup = EMBRBodyGroup.parseBodyGroup(getValue(line));
            } else if (line.startsWith(EMBRScript.JOINT)) {
                c._articulator = EMBRJoint.parseJoint(getValue(line));
            } else if (line.startsWith(EMBRScript.DIRECTION)) {
                c.direction = getTripleValue(line);
            } else if (line.startsWith(EMBRScript.NORMAL)) {
                c.normal = EMBRNormal.parseNormal(getValue(line));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return c;
    }

    private void readPoseMetadata(EMBRPose p, String line)
    {
        int pos = line.indexOf("SYNC:");
        if (pos > -1) {
            String sem = getValue(line.substring(pos));
            p.setSemantics(BMLSyncLabel.getSyncLabel(sem));
        }
        try {
            EMBRProperties prop = EMBRProperties.parse(line);
            if (prop != null) {
                p.setProperties(prop);
            }
        } catch (UnknownEMBRPropertyKeyException ex) {
            Logger.getLogger(EMBRScriptReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private EMBRPose readPose(String header, BufferedReader rd) throws IOException
    {
        EMBRPose p = new EMBRPose();
        readPoseMetadata(p, header);
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith(EMBRScript.TIME_POINT)) {
                p.setTime(getLongValue(line));
                p.relativeTime = getValue(line).startsWith("+");
            } else if (line.startsWith(EMBRScript.HOLD)) {
                p.setHoldDuration(getLongValue(line));
            } else if (line.indexOf(EMBRScript.POSITION_CONSTRAINT) > -1) {
                p.constraints.add(readPositionConstraint(rd));
            } else if (line.indexOf(EMBRScript.ORIENTATION_CONSTRAINT) > -1) {
                p.constraints.add(readOrientationConstraint(rd));
            } else if (line.indexOf(EMBRScript.POSE_TARGET) > -1) {
                p.constraints.add(readPoseTarget(rd));
            } else if (line.indexOf(EMBRScript.LOOK_AT_CONSTRAINT) > -1) {
                p.constraints.add(readLookAtConstraint(rd));
            } else if (line.indexOf(EMBRScript.SWIVEL_CONSTRAINT) > -1) {
                p.constraints.add(readSwivelConstraint(rd));
            } else if (line.indexOf(EMBRScript.AUTONOMOUS_BEHAVIOR) > -1) {
                p.constraints.add(readAutonomousBehaviourConstraint(rd));
            } else if (line.indexOf(EMBRScript.MORPH_TARGET) > -1) {
                p.constraints.add(readMorphTargetConstraint(rd));
            } else if (line.indexOf(EMBRScript.SHADER) > -1) {
                p.constraints.add(readShaderConstraint(rd));
            } else if (line.startsWith(EMBRScript.END)) {
                break;
            }
        }
        return p;
    }

    private void readPoseSequenceMetadata(EMBRPoseSequence s, String str)
    {
        int pos = str.indexOf("LEXEME:");
        if (pos > -1) {
            s.setLexeme(getValue(str.substring(pos)));
        }
        try {
            EMBRProperties prop = EMBRProperties.parse(str);
            if (prop != null) {
                s.setProperties(prop);
            }
        } catch (UnknownEMBRPropertyKeyException ex) {
            Logger.getLogger(EMBRScriptReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private EMBRPoseSequence readPoseSequence(String header, BufferedReader rd) throws
            IOException
    {
        EMBRPoseSequence seq = new EMBRPoseSequence();
        readPoseSequenceMetadata(seq, header);
        String line;
        while ((line = rd.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("CHARACTER")) {
                seq.character = getValue(line);
            } else if (line.startsWith(EMBRScript.START)) {
                String time = getValue(line);
                if (time.toLowerCase().equals("asap")) {
                    seq.setASAP(true);
                } else {
                    if (time.startsWith("+")) {
                        time = time.substring(1);
                    }
                    seq.startTime = Long.parseLong(time);
                }
            } else if (line.startsWith("FADE_IN")) {
                String time = getValue(line);
                seq.fadeIn = Long.parseLong(time);
            } else if (line.startsWith("FADE_OUT")) {
                String time = getValue(line);
                seq.fadeOut = Long.parseLong(time);
            } else if (line.indexOf("K_POSE") > -1) {
                seq.addPose(readPose(line, rd));
            } else if (line.startsWith("TIME_WARP")) {
                seq._timeWarp = getValue(line);
            } else if (line.startsWith("END")) {
                break;
            }
        }
        return seq;
    }

    /**
     * Adds start and end sync point semantics if not already present.
     */
    private void addStartEndSyncPoints(EMBRPoseSequence seq)
    {
        boolean hasStart = false, hasEnd = false;
        EMBRPose firstPose = null, lastPose = null;
        for (EMBRPose p : seq.getPoses()) {
            if (firstPose == null) {
                firstPose = p;
            }
            lastPose = p;
            if (p.getSemantics() == BMLSyncLabel.START) {
                hasStart = true;
            } else if (p.getSemantics() == BMLSyncLabel.END) {
                hasEnd = true;
            }
        }
        if (!hasStart) {
            firstPose.setSemantics(BMLSyncLabel.START);
        }
        if (!hasEnd) {
            lastPose.setSemantics(BMLSyncLabel.END);
        }
    }

    /**
     * Reads all pose sequences contained in a file.
     * 
     * @param file File to read from
     * @return list of pose sequences
     * @throws IOException
     */
    public List<EMBRPoseSequence> readLexemesToList(File file) throws
            IOException
    {
        List<EMBRPoseSequence> result = new ArrayList<EMBRPoseSequence>();
        BufferedReader rd = new BufferedReader(new FileReader(file));
        String line;
        while ((line = rd.readLine()) != null) {
            if (line.indexOf("K_POSE_SEQUENCE") > -1) {
                EMBRPoseSequence ps = readPoseSequence(line, rd);
                addStartEndSyncPoints(ps);
                if (ps.getLexeme() != null) {
                    result.add(ps);
                }
            }
        }
        rd.close();
        return result;
    }

    /**
     * Reads all pose sequences contained in a file.
     *
     * @param file File to read from
     * @return hash map from lexeme name to pose sequence
     * @throws IOException
     */
    private HashMap<String, EMBRPoseSequence> readLexemes(File file) throws
            IOException
    {
        HashMap<String, EMBRPoseSequence> result =
                new HashMap<String, EMBRPoseSequence>();
        List<EMBRPoseSequence> list = readLexemesToList(file);
        for (EMBRPoseSequence ps : list) {
            if (ps.getLexeme() != null) {
                result.put(ps.getLexeme(), ps);
            }
        }
        return result;
    }

    /**
     * This should be used for reading the lexicon.
     *
     * @param dir Directory which contains all lexemes as EMBRScript
     * @return lexicon
     * @throws IOException
     */
    public BehaviorLexicon readLexicon(File dir) throws
            IOException
    {
        BehaviorLexicon lex = new BehaviorLexicon();
        if (dir.isDirectory()) {
            for (File f : dir.listFiles(new FilenameFilter()
            {

                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("embr");
                }
            })) {
                EMBRScriptReader rd = new EMBRScriptReader();
                HashMap<String, EMBRPoseSequence> scripts = rd.readLexemes(f);
                for (Entry<String, EMBRPoseSequence> entry : scripts.entrySet()) {
                    BehaviorLexeme lexeme = new BehaviorLexemeImpl(entry.getKey(), entry.getValue());
                    lex.putLexeme(entry.getKey(), lexeme);
                }
            }
        }
        return lex;
    }

    public static void main(String[] args)
    {
        File dir = new File("data/embrscript/");
        EMBRScriptReader rd = new EMBRScriptReader();
        try {
            System.out.println("+++ Reading " + dir);
            BehaviorLexicon result = rd.readLexicon(dir);
            System.out.println("Found " + result.size() + " lexemes...");
            for (String lex : result.getLexemeNames()) {
                System.out.println("  - " + lex);
            }
            System.out.println("+++ Finished reading");
            for (BehaviorLexeme s : result.getLexemes()) {
                System.out.println("\n" + s.getEMBRScript().toScript());
            }
        } catch (IOException ex) {
            Logger.getLogger(EMBRScriptReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
