package de.dfki.embots.modules.behavior;

import javax.jms.JMSException;
import eu.semaine.components.Component;
import eu.semaine.jms.sender.Sender;
import de.dfki.embots.framework.EMBOTSConstants;
import de.dfki.embots.framework.ui.ModuleErrorDialog;
import eu.semaine.jms.message.SEMAINEMessage;
import eu.semaine.jms.receiver.Receiver;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Receives FML input and plans all behaviors, verbal and nonverbal, to
 * express the message encoded there. The output is BML and sent to the
 * BML Realizer module.
 *
 * @author Michael Kipp
 */
public class BehaviorPlannerModule extends Component
{

    private static final String NAME = "Behavior Planner";
    private Sender _bmlSender;
    private Sender _fmlFeedbackSender;
    private HashMap<String, String> _fml2bmlHashMap = new HashMap<String, String>();

    public BehaviorPlannerModule() throws JMSException
    {
        super(NAME);

        // init senders
        _bmlSender = new Sender(EMBOTSConstants.BML_INPUT_TYPE, "String", NAME);
        senders.add(_bmlSender);
        _fmlFeedbackSender = new Sender(EMBOTSConstants.FML_FEEDBACK_TYPE, "String", NAME);
        senders.add(_fmlFeedbackSender);

        // init receivers
        receivers.add(new Receiver(EMBOTSConstants.FML_TYPE));
        receivers.add(new Receiver(EMBOTSConstants.BML_FEEDBACK_TYPE));
    }

    @Override
    public void react(SEMAINEMessage m) throws Exception
    {
        try {
            if (m.getTopicName().equals(EMBOTSConstants.FML_TYPE)) {
//            log.info("Input FML: " + m.getText());
                try {
                    SimpleFML2BMLTranslator.Result result = SimpleFML2BMLTranslator.translate(m.getText());
                    _fml2bmlHashMap.put(result.bmlID, result.fmlID);
                    _bmlSender.sendTextMessage(result.bml, meta.getTime());
//                log.info("Translated to BML: " + bml);
                } catch (IOException e1) {
                    log.error(e1.getMessage());
                    e1.printStackTrace();
                } catch (JDOMException e2) {
                    log.error(e2.getMessage());
                    e2.printStackTrace();
                }

            } else if (m.getTopicName().equals(EMBOTSConstants.BML_FEEDBACK_TYPE)) {
                String bmlFeedback = m.getText();


                // parse as XML
                SAXBuilder builder = new SAXBuilder();
                Document doc = builder.build(new ByteArrayInputStream(bmlFeedback.getBytes()));

                // process content
                Element root = doc.getRootElement();

                if (root.getName().equals("STATUS")) {
                    String bmlID = root.getChild("BML-ID").getTextTrim();
                    String type = root.getAttributeValue("type");

                    // case: got a "finished" message
                    if ("finished".equals(type)) {
                        log.info("FINSHED BML " + bmlID);
                        String fmlID = _fml2bmlHashMap.get(bmlID);
                        if (fmlID == null) {
                            log.warn("Got feedback from unknown BML block \"" + bmlID + "\".");
                        } else {
                            String msg = createFMLFeedback(fmlID);
                            log.info("FML feedback: " + msg);
                            _fmlFeedbackSender.sendTextMessage(msg, meta.getTime());
                            _fml2bmlHashMap.remove(bmlID);
                        }
                    }


                }
            }
        } catch (RuntimeException e) {
            ModuleErrorDialog.show(NAME, e.getStackTrace());
        }
    }

    private String createFMLFeedback(String fmlID)
    {
        return ("<STATUS type=\"finished\"><FML-ID>" + fmlID
                + "</FML-ID></STATUS>");
    }
}
