package config.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Mahendiran
 *
 */
public class ExperimentConfigXMLParser {

    private final String EXPERIMENT = "Experiment";
    private final String USERID = "User.Id";

    private final String COUNTRY = "Country";
    private final String COUNTRYNAME = "Name";
    private final String PRESENTATIONSTYLE = "Presentation.Style";

    private final String CITY = "City";
    private final String WEATHERCONDITION = "Weather.Condition";
    private final String CLIMATE = "Climate";

    private String xmlFilePath;
    private Document document;
    // to represent root node i.e Experiment node
    private Element docEle;
    // NodeList of 'Country's
    private NodeList countryNodeList;
    private int countryCount;
   
    private ExperimentConfig experimentConfig=null;


    private InputStream xmlFileInputStream;
    private boolean readFromInpuStream = false;

    public ExperimentConfigXMLParser(String xmlFilePath) throws ParserConfigurationException, SAXException, IOException {
        this.xmlFilePath = xmlFilePath;
        init();
    }

    public ExperimentConfigXMLParser(InputStream xmlFileInputStream) throws ParserConfigurationException, SAXException, IOException {
        this.xmlFileInputStream = xmlFileInputStream;
        readFromInpuStream = true;
        init();
    }

    private void init() throws ParserConfigurationException, SAXException, IOException {
        parseXmlFile();
        //get the root element
        docEle = document.getDocumentElement();

        String userId=docEle.getAttribute(USERID);

        experimentConfig=new ExperimentConfig(userId);
        //get a nodelist of 'relation's
        countryNodeList = docEle.getElementsByTagName(COUNTRY);

        if (countryNodeList == null && countryNodeList.getLength() == 0) {
            countryCount = 0;
        } else {
            countryCount = countryNodeList.getLength();
        }

    }

    private void parseXmlFile() throws ParserConfigurationException, SAXException, IOException {

        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        if (readFromInpuStream) {
            document = db.parse(xmlFileInputStream);
        } else {
            document = db.parse(new File(xmlFilePath));
        }
    }

    public ExperimentConfig extractParameters() {
       
        for (int countryindex = 0; countryindex < countryCount; countryindex++) {
            
            Element countryElement = (Element) countryNodeList.item(countryindex);
            String countryName=countryElement.getAttribute(COUNTRYNAME);
            String presentationStyle=countryElement.getAttribute(PRESENTATIONSTYLE);
            
            CountryConfig countryConfig=new CountryConfig(countryName, presentationStyle);
            experimentConfig.addCountry(countryConfig);
            
             NodeList cityNodeList=countryElement.getElementsByTagName(CITY);
             int noOfCities=cityNodeList.getLength();

             if(cityNodeList!=null)
             {
                for(int cityCounter=0;cityCounter<noOfCities;cityCounter++){

                  Element cityElement = (Element) cityNodeList.item(cityCounter);
                  String weatherCondition=cityElement.getAttribute(WEATHERCONDITION);
                  String climate=cityElement.getAttribute(CLIMATE);
                  CityConfig cityConfig=new CityConfig(weatherCondition, climate);
                  countryConfig.addCity(cityConfig);
                }
             }
            

        }

                return experimentConfig;
    }



}
