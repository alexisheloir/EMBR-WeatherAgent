package de.dfki.carmina.eyeTrackerLogger.dataProcessor;


import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import java.util.Vector;


public class XMLParser {

	private Document doc;
	private String datastring;

	public XMLParser(String xmlstring) {

		this.datastring = xmlstring;
		try {
	        InputSource xmlsource = new InputSource();
	        xmlsource.setCharacterStream(new StringReader(xmlstring));

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			doc = db.parse(xmlsource);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public LogData getEyeData()
	{
		LogData eyeData = new LogData();
		eyeData.datastring = datastring;
		eyeData.timestamp = getnode("timestamp");
		eyeData.x_gazepos_lefteye = Float.parseFloat(getnode("x_gazepos_lefteye").replace(",","."));
		eyeData.y_gazepos_lefteye = Float.parseFloat(getnode("y_gazepos_lefteye").replace(",","."));
		eyeData.x_gazepos_righteye = Float.parseFloat(getnode("x_gazepos_righteye").replace(",","."));
		eyeData.y_gazepos_righteye = Float.parseFloat(getnode("y_gazepos_righteye").replace(",","."));
		eyeData.x_eyepos_lefteye = Float.parseFloat(getnode("x_eyepos_lefteye").replace(",","."));
		eyeData.y_eyepos_lefteye = Float.parseFloat(getnode("y_eyepos_lefteye").replace(",","."));
		eyeData.x_eyepos_righteye = Float.parseFloat(getnode("x_eyepos_righteye").replace(",","."));
		eyeData.y_eyepos_righteye = Float.parseFloat(getnode("y_eyepos_righteye").replace(",","."));
		eyeData.diameter_pupil_lefteye = Float.parseFloat(getnode("diameter_pupil_lefteye").replace(",", "."));
		eyeData.diameter_pupil_righteye = Float.parseFloat(getnode("diameter_pupil_righteye").replace(",", "."));
		eyeData.distance_lefteye = Float.parseFloat(getnode("distance_lefteye").replace(",", "."));
		eyeData.distance_righteye = Float.parseFloat(getnode("distance_righteye").replace(",", "."));
		eyeData.validity_lefteye = Long.parseLong(getnode("validity_lefteye"));
		eyeData.validity_righteye = Long.parseLong(getnode("validity_righteye"));

		return eyeData;
	}

	public Vector<Polygon2D> getPolygons()
	{
		NodeList nodeLst = doc.getElementsByTagName("eyeCoordinates");
        Element element = (Element) nodeLst.item(0);
        NodeList polygons = element.getElementsByTagName("polygon");
        int nrOfpolygons = polygons.getLength();

        Vector<Polygon2D> returnvector = new Vector<Polygon2D>(nrOfpolygons);

        for(int i = 0;i<nrOfpolygons;i++)
        {
        	Element polygonsElement = (Element) polygons.item(i);

        	NodeList polygonsPointsNode = polygonsElement.getElementsByTagName("point");
        	int nrOfPoints = polygonsPointsNode.getLength();

        	Polygon2D polygon = new Polygon2D(nrOfPoints);

        	// add name of polygon
        	NodeList polygonsNameNode = polygonsElement.getElementsByTagName("name");
        	Element polygonsNameElement = (Element) polygonsNameNode.item(0);
        	polygon.name = getCharacterDataFromElement(polygonsNameElement);

        	// add points of polygon
            for(int j = 0;j<nrOfPoints;j++)
            {
            	Element polygonsPointsElement = (Element) polygonsPointsNode.item(j);

            	NodeList polygonsPointXNode =  polygonsPointsElement.getElementsByTagName("x");
            	Element polygonsPointXElement = (Element) polygonsPointXNode.item(0);
            	float x = Float.parseFloat(getCharacterDataFromElement(polygonsPointXElement).replace(",","."));

            	NodeList polygonsPointYNode =  polygonsPointsElement.getElementsByTagName("y");
            	Element polygonsPointYElement = (Element) polygonsPointYNode.item(0);
            	float y = Float.parseFloat(getCharacterDataFromElement(polygonsPointYElement).replace(",","."));

            	polygon.addPoint(x,y);
            }

        	returnvector.add(polygon);
        }
        return returnvector;
	}


	private String getnode(String nodeid)
	{
		NodeList nodeLst = doc.getElementsByTagName("eyeCoordinates");
        Element element = (Element) nodeLst.item(0);
        NodeList name = element.getElementsByTagName(nodeid);

        if(name.getLength()>0)
        {
        	Element line = (Element) name.item(0);
        	return getCharacterDataFromElement(line);
        }
        else
        	return "-1";
	}

	private static String getCharacterDataFromElement(Element elem)
	{
		Node child = elem.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "-2";
	}


}