/*
 * TestConstraintSolving.java
 *
 * (c) 2009 Michael Kipp, DFKI, Germany, kipp@dfki.de
 * Created on 31.08.2009, 11:38:19
 */
package de.dfki.embots.bml.solver;

import de.dfki.embots.bml.BMLBlock;
import de.dfki.embots.bml.lex.BehaviorLexeme;
import de.dfki.embots.bml.lex.BehaviorLexicon;
import de.dfki.embots.bml.reader.BMLReader;
import de.dfki.embots.bml.reader.BMLReaderException;
import de.dfki.embots.embrscript.EMBRScriptReader;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;

/**
 * Test for constraint resolution.
 *
 * @author Michael Kipp
 */
public class TestConstraintSolving
{

    private static final boolean DEBUG_LEXEMES = true; // print all lexemes
    private static final boolean USE_FILE = true;
    //private static final String FILE = "no-gestures-test.bml"; // file to use
   private static final String FILE = "gesture4-test.bml"; // file to use

    public static void main(String[] args)
    {
        File dir = new File("data/lexicon/");
        EMBRScriptReader rd = new EMBRScriptReader();
        try {
            System.out.println("+++ Reading EMBRScript templates" + dir);
            BehaviorLexicon result = rd.readLexicon(dir);
            System.out.println("Found " + result.size() + " lexemes (" + result.getCorruptedLexemes().size()
                    + " corrupted).");
            if (DEBUG_LEXEMES) {
                for (BehaviorLexeme lex : result.getLexemes()) {
                    System.out.println("  - " + lex);
                }
                if (result.getCorruptedLexemes().size() > 0) {
                    System.out.println("### CORRUPTED (" + result.getCorruptedLexemes().size()
                            + ") ###");
                    for (BehaviorLexicon.CorruptedLexeme corrupt: result.getCorruptedLexemes()) {
                        System.out.println("  :( " + corrupt);
                    }
                }
            }
            System.out.println("+++ Finished reading");
            BMLReader r = new BMLReader(result);

            File f = null;
            long startTime = System.currentTimeMillis();
            if (USE_FILE) {
                f = new File("data/unit-test-data/" + FILE);
            } else {
                JFileChooser fc = new JFileChooser("data/unit-test-data/");
                int choose = fc.showOpenDialog(null);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    startTime = System.currentTimeMillis();
                    f = fc.getSelectedFile();
                } else {
                    f = null;
                }
            }
            if (f != null) {
                System.out.println("\n*** INPUT FILE: " + f + " " + f.exists() + " ***");
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }

                BMLBlock bml = r.getBMLBlock(f);
                System.out.println("\n*** PARSED INPUT BML ***\n");
                System.out.println(bml.toXML() + "\n");

                long startSolve = System.currentTimeMillis();
                BMLConstraintSolver solver = new BMLConstraintSolver(bml);
                boolean success = solver.solveTiming();
//                if (debug) {
//                    System.out.println("\n*** SOLUTION: " + (status == RevisedSimplex.Optimal ? "OPTIMAL ***" : "UNDEF ***"));
//                }
                if (!success) {
                    System.out.println("  # # # FAILURE # # #");
                }
                long endTime = System.currentTimeMillis();
                System.out.println("-> total: " + (endTime - startTime) + "   solve: "
                        + (endTime - startSolve));
                System.out.println("\n================ SCRIPT =================  \n"
                        + bml.toScript());
            }
        } catch (JDOMException ex) {
            Logger.getLogger(TestConstraintSolving.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BMLReaderException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BMLReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
