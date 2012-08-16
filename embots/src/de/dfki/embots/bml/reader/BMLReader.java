package de.dfki.embots.bml.reader;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.sync.BMLSyncPoint;
import de.dfki.embots.bml.behavior.BMLBehavior;
import de.dfki.embots.bml.behavior.BMLLexicalizedBehavior;
import de.dfki.embots.bml.behavior.BMLSpeechBehavior;
import de.dfki.embots.bml.sync.BMLRelativeSyncPoint;
import de.dfki.embots.bml.sync.BMLSyncLabel;
import de.dfki.embots.bml.behavior.BMLTypedBehavior;
import de.dfki.embots.bml.exception.BMLBehaviorException;
import de.dfki.embots.bml.exception.BMLUnderspecifiedLexemeException;
import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.sync.BMLSpeechSyncPoint;
import de.dfki.embots.bml.sync.BMLVirtualSyncPoint;
import de.dfki.embots.embrscript.EMBRScriptReader;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.ui.eyetracking.Gaze;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parses BML file (XML).
 *
 * Currently uses a combination of in-built XML and JDOM. TODO: All in JDOM!
 * 
 * @author Michael Kipp
 */
public class BMLReader
{

    public static final String BML = "bml";
    public static final String SPEECH = "speech";
    public static final String GAZE = "gaze";
    public static final String FACE = "face";
    public static final String GESTURE = "gesture";
    public static final String HEAD = "head";    // missing: offset outside the bracket (e.g. "before(x:stroke)+3.2" )
    private static final String SYNC = "sync";
    private static final String TEXT = "text";
    private static final String AUTO_ID_PREFIX = "auto";
    private static final Pattern BEFORE_AFTER_PATTERN = Pattern.compile("(before|after)\\(([^)]*)\\)");    // missing: reference to multiple beats (i.e. second ":" in the ID)
    private static final Pattern ID_OFFSET_PATTERN = Pattern.compile("([^:]*):([^:+-]*)\\s*(.*)");
    private int _idCount = 0;
    private BMLBlock _bml;
    private List<SyncPointData> _constraintsToResolve = new ArrayList<SyncPointData>();
    private BehaviorLexicon _embrLexicon;
    private org.jdom.Document _jdoc = null;

    class SyncPointData
    {

        String constraint;
        BMLSyncPoint bmlConstraint;  // used during processing
        BMLRelativeSyncPoint relSyncPoint;

        SyncPointData(String c, BMLRelativeSyncPoint sp)
        {
            constraint = c;
            relSyncPoint = sp;
        }
    }

    public BMLReader()
    {
    }

    public BMLReader(BehaviorLexicon embrLexicon)
    {
        _embrLexicon = embrLexicon;
    }

    private org.jdom.Document readJDocument(File file)
            throws JDOMException, IOException
    {
        SAXBuilder builder = new SAXBuilder();
        return builder.build(file);
    }

    private Document readDocument(File file) throws SAXException, IOException
    {
        FileReader fr = new FileReader(file);
        Document doc = null;
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = db.parse(new InputSource(fr));
            fr.close();
            return doc;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getAttribute(Node node, String attribute)
    {
        try {
            return node.getAttributes().getNamedItem(attribute).getNodeValue();
        } catch (Exception e) {
            return null;
        }
    }

    private void parseSyncPoints(BMLBehavior beh, Node n)
    {
        for (BMLSyncLabel sy : BMLSyncLabel.values()) {
            String con = getAttribute(n, sy.toString());

            // check whether constraint is present
            if (con != null) {
                // check whether absolute time constraint
                try {
                    double time = Double.parseDouble(con);
                    beh.addSyncPoint(new BMLSyncPoint(beh, sy.toString(), time));
                } catch (NumberFormatException e) {
                    // case: not an absolute constraint
                    //       -> push it to the list
                    BMLRelativeSyncPoint sp = new BMLRelativeSyncPoint(beh, sy.toString());
                    beh.addSyncPoint(sp);
                    _constraintsToResolve.add(new SyncPointData(con, sp));
                }
            }
        }
    }

    /**
     * Extracts both <text> and <sync> points from the speech block.
     */
    private void extractText(Element n, BMLSpeechBehavior beh)
    {
        StringBuilder buf = new StringBuilder();
        int wordcount = 0;
        Iterator it = n.getContent().iterator();
        while (it.hasNext()) {
//        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
//            Node node = n.getChildNodes().item(i);
            Object node = it.next();
            if (node instanceof Text) {
                if (buf.length() > 0) {
                    buf.append(" ");
                }
                String text = ((Text) node).getTextTrim();

                // remove linebreaks
                text = text.replaceAll("\n", "");

                // remove double spaces
                while (text.indexOf("  ") > -1) {
                    text = text.replace("  ", " ");
                }

                // count words
                if (text.length() > 0) {
                    wordcount += text.split(" ").length;
                    buf.append(text);
//                    System.out.println("wordcount=" + wordcount + ": " + text);
                }
            } else if (node instanceof Element) {
                Element enode = (Element) node;
                if (enode.getName().equals(SYNC)) {

                    // here, only speech sync points (i.e. without absolute time) can occur
                    BMLSyncPoint sy =
                            new BMLSpeechSyncPoint(beh, enode.getAttributeValue("id"), wordcount);
                    String time = enode.getAttributeValue("time");
                    if (time != null) {
                        sy.setTime(Double.parseDouble(time));
                    }
                    beh.addSyncPoint(sy);
                }
            }
        }
        for (String w : buf.toString().split(" ")) {
            beh.addWord(w);
        }
    }

    private void parseSpeechBlocks()
    {
        Element root = _jdoc.getRootElement();
        Iterator it = root.getChildren().iterator();
        while (it.hasNext()) {
            Element el = (Element) it.next();
//            System.out.println("speech> found id " + el.getAttributeValue("id"));
            if (el.getName().toLowerCase().equals(SPEECH)) {
                BMLSpeechBehavior beh =
                        (BMLSpeechBehavior) BMLBehavior.getInstance(_bml, SPEECH);
                parseSpeechBlock(el, beh);
                _bml.addBehavior(beh);
            }
        }
    }

    /**
     * Parses speech block.
     */
    private void parseSpeechBlock(Element el, BMLSpeechBehavior beh)
    {
        String id = el.getAttributeValue("id");
        beh.setID(createID(id));
        String wait = el.getAttributeValue("wait");
        String voice = el.getAttributeValue("voice");
        String agent = el.getAttributeValue("agent");
        if (wait != null) {
            beh.setWait((long) (Float.parseFloat(wait) * 1000));
        }
        if (voice != null) {
            beh.setVoice(voice);
        }
        if (agent != null) {
            beh.setAgent(agent);
        }
        Iterator it = el.getChildren().iterator();
        while (it.hasNext()) {
            Element node = (Element) it.next();
            if (node.getName().toLowerCase().equals(TEXT)) {
                extractText(node, beh);
            } else if (node.getName().toLowerCase().equals(SYNC)) {

                // here, only sync points with absolute time can occur
                BMLSpeechSyncPoint sy =
                        new BMLSpeechSyncPoint(beh, node.getAttributeValue("id"), 0);
                String time = node.getAttributeValue("time");
                if (time != null) {
                    sy.setTime(Double.parseDouble(time));
                }
                beh.addSyncPoint(sy);
            }
        }
    }

    private String createID(String id)
    {
        //String id = getAttribute(n, "id");

        // case: no ID specified
        if (id == null) {
            String newID = AUTO_ID_PREFIX + _idCount++;
            _bml.addException(new BMLBehaviorException(newID, "No ID specified: Autogenerated \"" + newID + "\""));
            return newID;
        } else if (_bml.findElement(id) != null) {
            // case: ID already exists
            int count = 1;
            while (_bml.findElement(id + "_" + count) != null) {
                count++;
            }
            String newID = id + "_" + count;
            _bml.addException(new BMLBehaviorException(newID, "Double ID \"" + id + "\": Using \"" + newID + "\" instead"));
            return newID;
        }
        return id;
    }

    private BMLBehavior parseBehavior(Node n, String type)
    {
        BMLBehavior el = BMLBehavior.getInstance(_bml, type);
        if (el != null) {
            el.setID(createID(getAttribute(n, "id")));

            String agent = getAttribute(n, "agent");

            String strategy = getAttribute(n,"strategy");
            if (agent != null) {
                el.setAgent(agent);
            }
//            if (el instanceof BMLSpeechBehavior) {
//                parseSpeechBlock(n, (BMLSpeechBehavior) el);
//            }
            if (el instanceof BMLTypedBehavior) {
                String t = getAttribute(n, "type");
                if (t == null) {
                    //throw new BMLReaderException("Missing type in " + n.getNodeName() + " tag.");
                    _bml.addException(new BMLBehaviorException(el.getID(),
                            "Missing type in " + n.getNodeName() + " tag."));
                }
                ((BMLTypedBehavior) el).setType(t);
            }

            // retrieve lexeme from lexicon
            if (el instanceof BMLLexicalizedBehavior) {
                String lex = getAttribute(n, "lexeme");
                if (lex != null) {
                    ((BMLLexicalizedBehavior) el).setLexemeName(lex);
                    if (_embrLexicon != null) {
                        BehaviorLexeme lexeme = _embrLexicon.getLexeme(lex);
                        if (lexeme != null) {
                            ((BMLLexicalizedBehavior) el).setLexeme(lexeme);
                            //System.out.println("Lexeme retrieved: Found EMBRScript for \"" + lex + "\"");
                        } else {
                            String msg = "Lexeme not found (id=\"" + el.getID() + "\") for \"" + lex + "\"";
                            _bml.addException(new BMLBehaviorException(el.getID(), msg));
                        }
                    }
                }
            }
            parseSyncPoints(el, n);
        }
        return el;
    }

    /**
     * Create sync point so that it can be referenced.
     *
     * Note that this sync point may not be resolvable by the lexeme (if
     * corresponding semantics not specified). This is checked in the solver.
     */
    private BMLSyncPoint createRefSyncPoint(BMLBehavior owner,
            BMLBehavior refBehavior, BMLSyncLabel refSyncLabel)
    {
        if (refBehavior.getLexeme() == null) {
            _bml.addException(new BMLBehaviorException(owner.getID(),
                    "Sync point resolution needs EMBRScript for id=\""
                    + refBehavior.getID() + "\""));
            return null;
        }
        // check whether sync point is specified in the lexeme at all
        if (refBehavior.getLexeme().getPoseSemantics(refSyncLabel) == null) {
            _bml.addException(new BMLUnderspecifiedLexemeException(refBehavior, null, refSyncLabel.toString()));
            return null;
        }

        BMLSyncPoint ref = new BMLVirtualSyncPoint(refBehavior, refSyncLabel.toString());
        refBehavior.addSyncPoint(ref);
        return ref;
    }

    private void parseConstraintString(String str, BMLRelativeSyncPoint c) throws BMLReaderException
    {
        String idOffsetString = str;
        Matcher m = BEFORE_AFTER_PATTERN.matcher(str);
        if (m.matches()) {
            String beforeafter = m.group(1);
            c.setRelation(beforeafter.equals("before") ? BMLRelativeSyncPoint.Relation.BEFORE : BMLRelativeSyncPoint.Relation.AFTER);
            idOffsetString = m.group(2);
        }
        m = ID_OFFSET_PATTERN.matcher(idOffsetString);
        m.matches();
        String bmlID = m.group(1);
        String syncLabel = m.group(2);

        // find BML element
        BMLBehavior refBehavior = _bml.findElement(bmlID);

        // BML element does not exist => wrong ID
        if (refBehavior == null) {
            throw new BMLReaderException("Illegal ID \"" + bmlID + "\" does not point to any BML element.");
        }

        // find referenced sync point
        BMLSyncPoint refSync = refBehavior.getSyncPoint(syncLabel);

        // create new if doesn't exist
        if (refSync == null) {
            refSync = createRefSyncPoint(c.getOwner(), refBehavior, BMLSyncLabel.getSyncLabel(syncLabel));
            if (refSync == null) {
                c.getBehavior().removeSyncPoint(c);
                return;
            }
        }

        // note: sync point is ignored in case of error
        if (refSync
                != null) {
            c.setRefConstraint(refSync);

            if (m.groupCount() > 2) {
                String offset = m.group(3).trim();
                if (offset.length() > 0) {
                    try {
                        c.setOffset(Double.parseDouble(offset));
                    } catch (NumberFormatException e) {
                        throw new BMLReaderException("Illegal number in \"" + str + "\".");
                    }
                }
            }
        }
    }

    private void resolveRelativeSyncPoints()
    {
        while (_constraintsToResolve.size() > 0) {
            SyncPointData cd = _constraintsToResolve.get(0);
            try {
                parseConstraintString(cd.constraint, cd.relSyncPoint);
            } catch (BMLReaderException ex) {
                System.out.println(ex.getMessage());
                _bml.addException(new BMLBehaviorException(cd.relSyncPoint.getBehavior().getID(), ex.getMessage()));
            }
            _constraintsToResolve.remove(cd);
        }
    }

    /**
     * Parses one BML block.
     *
     * @param n Top BML block node.
     * @return BML block object
     */
    private BMLBlock parseBMLBlock(Node n)
    {
        _bml = new BMLBlock();
        _bml.setID(createID(getAttribute(n, "id")));

        // get default agent
        String agent = getAttribute(n, "agent");
        if (agent != null) {
            _bml.setAgent(agent);
        }

        for (int i = 0; i < n.getChildNodes().getLength(); i++) {
            Node node = n.getChildNodes().item(i);
            BMLBehavior el;

            if (!node.getNodeName().toLowerCase().equals(SPEECH)) {
                el = parseBehavior(node, node.getNodeName());
                if (el != null) {
                    _bml.addBehavior(el);
                }
            }
        }

        parseSpeechBlocks();

        resolveRelativeSyncPoints();
        return _bml;
    }

    /**
     * Entry point for parsing. Note that from here on the exceptions of the
     * BML block object get collected in the exception list of the block.
     */
    private BMLBlock getBMLBlock(Document d)
    {
        for (int i = 0; i
                < d.getChildNodes().getLength(); i++) {
            Node node = d.getChildNodes().item(i);


            if (node.getNodeName().toLowerCase().equals(BML)) {
                return parseBMLBlock(node);
            }
        }
        return null;
    }

    /**
     * Returns the first BML block encountered. Exceptions are stored in the
     * block.
     * 
     * @param file
     * @return first BML block encountered.
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public BMLBlock getBMLBlock(File file)
            throws SAXException, IOException, BMLReaderException, JDOMException
    {
        _constraintsToResolve.clear();
        _jdoc = readJDocument(file);
        return getBMLBlock(readDocument(file));
    }

    public BMLBlock getBMLBlock(String string)
            throws SAXException, IOException, BMLReaderException, JDOMException
    {
        try {
            _constraintsToResolve.clear();
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(string)));
            SAXBuilder builder = new SAXBuilder();
            _jdoc = builder.build(new InputSource(new StringReader(string)));
            return getBMLBlock(doc);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void main(String[] args)
    {
        try {
            // read lexicon
            EMBRScriptReader embrReader = new EMBRScriptReader();
            BehaviorLexicon lexicon = embrReader.readLexicon(new File(EMBOTSConstants.EMBRSCRIPT_LEXICON_DIR));
            System.out.println("==> Lexicon: " + lexicon);
            BMLReader reader = new BMLReader(lexicon);


            File f = new File(EMBOTSConstants.BML_DIR);
            for (File file : f.listFiles()) {
                if (file.getName().endsWith(".bml")) {
                    System.out.println("\nINPUT +++ INPUT +++ INPUT\nBML FILE: " + file);
                    BufferedReader read = new BufferedReader(new FileReader(file));
                    String line;
                    while ((line = read.readLine()) != null) {
                        System.out.println("org> " + line);
                    }
                    BMLBlock bml = reader.getBMLBlock(file);
                    System.out.println("\nOUTPUT +++ OUTPUT +++ OUTPUT\n" + bml.toXML());
                }
            }
        } catch (JDOMException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BMLReaderException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);

        /*
        try {
        long startTime = System.currentTimeMillis();
        String[] options = {"BML Test suite", "Select BML file", "Exit"};


        int val = JOptionPane.showOptionDialog(null, "JBML: Select your test run...", "JBML Tester",
        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);


        if (val == 0) {
        File[] files = new File("data").listFiles(new FilenameFilter()
        {

        public boolean accept(File dir, String name)
        {
        return name.toLowerCase().trim().endsWith("bml");


        }
        });


        int fails = 0;


        for (File f : files) {
        if (f.isFile()) {
        System.out.println("\n\n====================================================");
        System.out.println("FILE: " + f);
        System.out.println("====================================================");
        BMLBlock bml = reader.getBMLBlock(f);
        // set times of abs. sync points
        BMLSyncPoint[] sp = bml.collectSyncPoints();


        int v = 1;

        // show original BML
        System.out.println(bml.toXML());
        // solve


        long startSolve = System.currentTimeMillis();
        BMLConstraintSolver solver = new BMLConstraintSolver(bml);


        boolean success = solver.solveTiming();


        if (!success) {
        System.out.println("\n\n# # #   FAILURE    # # #\n");
        fails++;

        }


        long endTime = System.currentTimeMillis();
        System.out.println("-> TIME total: " + (endTime - startTime) + "   solve: " + (endTime - startSolve));
        System.out.println("\n*** Solved BML ***  \n" + bml.toXML());


        }
        }
        System.out.println("\n>>> PROCESSED " + files.length + " FILES: " + fails + " FAILURES.");


        } else if (val == 1) {
        String BML_DIR = "data/bml";
        File[] files = new File(BML_DIR).listFiles(new FilenameFilter()
        {

        public boolean accept(File dir, String name)
        {
        return name.toLowerCase().trim().endsWith("bml");


        }
        });
        String[] filenames = new String[files.length];


        for (int i = 0; i
        < filenames.length; i++) {
        filenames[i] = files[i].getName();


        }
        OptionDialog.initialize(null, filenames,
        "Select BML",
        "Choose BML file:");
        String selectedName = OptionDialog.showDialog(null,
        filenames[0]);

        if (selectedName != null) {
        File f = new File(BML_DIR, selectedName);
        System.out.println("*** PROCESSING: " + f + " " + f.exists() + " ***");
        BMLBlock bml = reader.getBMLBlock(f);
        System.out.println("================ INPUT BML =================");
        System.out.println(bml.toXML());
        System.out.println("============================================");

        // set times of abs. sync points
        BMLSyncPoint[] sp = bml.collectSyncPoints();
        for (int i = 0; i
        < sp.length; i++) {
        if (sp[i] instanceof BMLSpeechSyncPoint) {
        if (sp[i].getTime() < 0) {
        ((BMLSpeechSyncPoint) sp[i]).setTime((sp.length - i));
        }
        }
        }
        long startSolve = System.currentTimeMillis();
        BMLConstraintSolver solver = new BMLConstraintSolver(bml);
        solver.solveTiming();


        long endTime = System.currentTimeMillis();
        System.out.println("-> total: " + (endTime - startTime) + "   solve: " + (endTime - startSolve));
        System.out.println("\n================ RESULT BMLS =================  \n" + bml.toXML());

        // translate to EMBR


        for (BMLBehavior beh : bml.getBehaviors()) {

        // only if EMBRscript exists
        if (beh.getLexeme() != null) {
        EMBRPoseSequence embr = BMLToEMBRScript.toEMBRScript(beh);
        System.out.println("\n********** EMBR for " + beh.getID());
        System.out.println("" + embr.toScript());


        }
        }

        System.out.println("\n================ EXCEPTIONS ================\n");


        for (BMLException ex : bml.getExceptions()) {
        System.out.println("EXCEPTION! " + ex.getMessage() + "\n");
        }

        }
        }
        } catch (BMLReaderException ex) {
        Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
        Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
        Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }


         */
    }


}
