package de.dfki.embots.embrscript;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kipp
 */
public class EMBRPropertiesTest
{

    public EMBRPropertiesTest()
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
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of put method, of class EMBRProperties.
     */
    @Test
    public void testPut()
    {
        System.out.println("put");
        EMBRPropertyKey key = EMBRPropertyKey.COMMENT;
        String value = "das ist ein comment";
        EMBRProperties instance = new EMBRProperties();
        instance.put(key, value);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class EMBRProperties.
     */
    @Test
    public void testGet()
    {
        System.out.println("get");
        EMBRPropertyKey key = EMBRPropertyKey.COMMENT;
        EMBRProperties instance = new EMBRProperties();
        String expResult = "foo";
        instance.put(EMBRPropertyKey.COMMENT, expResult);
        String result = instance.get(key);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of toScript method, of class EMBRProperties.
     */
    @Test
    public void testToScript()
    {
        System.out.println("toScript");
        EMBRProperties instance = new EMBRProperties();
        String expResult = "";
        String result = instance.toScript();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of parse method, of class EMBRProperties.
     */
    @Test
    public void testParse()
    {
        System.out.println("parse");
        String line = "sdfweop dsfäöe [MODALITY mouthing]   [COMMENT das ist ein Kommentar]";
        EMBRProperties instance;
        try {
            instance = EMBRProperties.parse(line);
            System.out.println(">>>" + instance.toScript());
        } catch (UnknownEMBRPropertyKeyException ex) {
            Logger.getLogger(EMBRPropertiesTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
}
