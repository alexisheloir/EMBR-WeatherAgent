package de.dfki.embots.modules.behavior;

import de.dfki.embots.framework.EMBOTSConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;

/**
 * Maps FML to BML in the simplest way, ignoring all tags.
 * 
 * @author Michael Kipp
 */
public class SimpleFML2BMLTranslator
{

    private static final String DEFAULT_AGENT = EMBOTSConstants.EMBR_CHARACTER;

    public static class Result
    {

        public String bml;
        public String bmlID;
        public String fmlID = "foo";
    }

    public static Result translate(String fml) throws JDOMException, IOException
    {
        Result result = new Result();
        StringBuilder sb = new StringBuilder();

        // parse as XML
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new ByteArrayInputStream(fml.getBytes()));

        // process content
        Element root = doc.getRootElement();

        // this must be <FML>
        if (root.getName().toLowerCase().equals("fml")) {
            String id = root.getAttributeValue("id");
            if (id != null) {
                result.fmlID = id;
            }
            int count = 1;
            Iterator it = root.getChildren().iterator();
            while (it.hasNext()) {
                Element el = (Element) it.next();
                if (el.getName().toLowerCase().equals("turn")) {
                    sb.append(createTurn(count++, el, result));
                }

            }
        }

        result.bml = sb.toString();
        return result;
    }

    private static String createTurn(int id, Element element, Result result)
    {
        StringBuilder sb = new StringBuilder();
        String bmlID = "bml_" + id;
        result.bmlID = bmlID;
        String agent = element.getAttributeValue("agent", DEFAULT_AGENT);
        sb.append("<BML id=\"" + bmlID + "\" agent=\"" + agent + "\">\n");
        sb.append("  <SPEECH>\n");
        sb.append("    <TEXT>\n");
        sb.append("      " + extractText(element) + "\n");
        sb.append("    </TEXT>\n");
        sb.append("  </SPEECH>\n");
        sb.append("</BML>\n");
        return sb.toString();
    }

    /**
     * Collects text from mixed XML content.
     *
     * @param element the turn element
     * @return Text as string
     */
    private static String extractText(Element element)
    {
        StringBuilder sb = new StringBuilder();
        Iterator it = element.getContent().iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (item instanceof Text) {
                sb.append(((Text) item).getTextTrim() + " ");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args)
    {
        try {
            String fml = "<FML><TURN>Hallo Welt <foo/> yes </TURN></FML>";
            System.out.println("Output:\n" + translate(fml));
        } catch (JDOMException ex) {
            Logger.getLogger(SimpleFML2BMLTranslator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SimpleFML2BMLTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
