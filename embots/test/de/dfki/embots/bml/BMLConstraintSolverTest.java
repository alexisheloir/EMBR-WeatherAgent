package de.dfki.embots.bml;

import de.dfki.embots.bml.solver.BMLConstraintSolver;
import de.dfki.embots.TestConstants;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.reader.BMLReaderException;
import de.dfki.embots.embrscript.EMBRScriptReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.jdom.JDOMException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.xml.sax.SAXException;

/**
 *
 * @author Michael Kipp
 */
public class BMLConstraintSolverTest extends TestCase
{

    private static final String[] BMLRESOLVE_FILES = {"gesture-test",
        "gesture-test-1", "gesture-test-2", "gesture-test-3", "no-gestures-test",
        "gesture1", "gesture2"};
    private BehaviorLexicon _lexicon;

    public BMLConstraintSolverTest()
    {
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void setUp() throws Exception
    {
        EMBRScriptReader rd1 = new EMBRScriptReader();
        File lexiconFile = new File(TestConstants.LEXICON_NEW);
        System.out.println("lexicon=" + lexiconFile + " " + lexiconFile.exists());
        _lexicon = rd1.readLexicon(lexiconFile);
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of solveTiming method, of class BMLConstraintSolver.
     */
    @org.junit.Test
    public void testSolveTiming()
    {
        System.out.println("testBMLResolution");
        for (int i = 0; i < BMLRESOLVE_FILES.length; i++) {
            File f = new File(TestConstants.TEST_DATA_DIR + BMLRESOLVE_FILES[i] + ".bml");
            System.out.println("############### bml=" + f);
            BMLReader rd2 = new BMLReader(_lexicon);
            BMLBlock bml = null;
            try {
                bml = rd2.getBMLBlock(f);
            } catch (JDOMException ex) {
                Logger.getLogger(BMLConstraintSolverTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(BMLConstraintSolverTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(BMLConstraintSolverTest.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BMLReaderException ex) {
                Logger.getLogger(BMLConstraintSolverTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (bml != null) {
                System.out.println("+++ BML BEFORE +++ \n" + bml.toXML());
            }

            BMLConstraintSolver solver = new BMLConstraintSolver(bml);
            boolean ok = solver.solveTiming();
            System.out.println("+++ RESULT BML [solved="
                    + ok + "] +++ \n" + bml.toXML());
            assertEquals(true, ok);
        }
    }
}
