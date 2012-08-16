package de.dfki.embots.translate;

import de.dfki.embots.TestConstants;
import de.dfki.embots.framework.translate.BMLMARYFusion;
import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.embrscript.EMBRScriptReader;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import junit.framework.TestCase;
import org.w3c.dom.Document;

/**
 * @author Michael Kipp
 */
public class BMLMARYFusionTest extends TestCase
{

    
    
    private static final String[] BMLMARY_FILES = {"gesture-simple", "gesture-simple2"};
    private static final double[] FIRST_SYNC = { 1.072875d, 0d };
    Document[] mary;
    BMLBlock[] bml;
    private BehaviorLexicon _lexiconOld;

    public BMLMARYFusionTest()
    {
    }

    @org.junit.BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @org.junit.AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @org.junit.Before
    @Override
    public void setUp() throws Exception
    {
        mary = new Document[BMLMARY_FILES.length];
        bml = new BMLBlock[BMLMARY_FILES.length];

        EMBRScriptReader rd1 = new EMBRScriptReader();
        File lexiconFile = new File(TestConstants.LEXICON_OLD);
        System.out.println("lexicon=" + lexiconFile + " " + lexiconFile.exists());
        _lexiconOld = rd1.readLexicon(lexiconFile);

        for (int i = 0; i < BMLMARY_FILES.length; i++) {
            File f = new File(TestConstants.TEST_DATA_DIR + BMLMARY_FILES[i] + ".mary");
            System.out.println("test mary=" +f);
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            mary[i] = docBuilder.parse(f);
            mary[i].getDocumentElement().normalize();

            f = new File(TestConstants.TEST_DATA_DIR + BMLMARY_FILES[i] + ".bml");
            System.out.println("test bml=" + f);
            BMLReader rd2 = new BMLReader(_lexiconOld);
            bml[i] = rd2.getBMLBlock(f);
        }
    }

    @org.junit.After
    @Override
    public void tearDown() throws Exception
    {
    }

    /**
     * Test of completeSpeechSyncPoints method, of class BMLMARYFusion.
     */
    @org.junit.Test
    public void testCompleteSpeechSyncPoints()
    {
        System.out.println("completeSpeechSyncPoints");
        for (int i = 0; i < BMLMARY_FILES.length; i++) {
            BMLMARYFusion.completeSpeechSyncPoints(bml[i], mary[i]);
            double syncTime1 = bml[i].findElement("s1").getSyncPoint("1").getTime();
            System.out.println("Check " + BMLMARY_FILES[i]);
            assertEquals(FIRST_SYNC[i], syncTime1);
        }
    }

}
