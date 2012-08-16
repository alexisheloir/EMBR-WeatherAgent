package de.dfki.carmina.eyeTrackerLogger.dataProcessor;


import java.util.Vector;


import org.apache.commons.codec.binary.Base64;


public class DataProcessor {


	public LogData getLogData(String inputstring)
	{
		String datastring;

		// decode packets
		datastring = new String(Base64.decodeBase64(inputstring.getBytes()));

		//unzip packets
		//datastring = GZip.unzip(inputstring);

		// output
		//System.out.println(datastring);

		// parse XML
		XMLParser parser = new XMLParser(datastring);

		// process EyeTracker data
		LogData eyeData = parser.getEyeData();

		// process AreaField data
		Vector<Polygon2D> polygons = parser.getPolygons();

		Polygon2D polygon;
		boolean hit_lefteye, hit_righteye;

		for(int i=0; i<polygons.size(); i++)
		{
			polygon = polygons.elementAt(i);

			// check if point is contained in polygon
			hit_lefteye = polygon.contains(eyeData.x_gazepos_lefteye,eyeData.y_gazepos_lefteye);
			hit_righteye = polygon.contains(eyeData.x_gazepos_righteye,eyeData.y_gazepos_righteye);

			// show message, if eye gaze lies within the given polygon
			if(hit_lefteye || hit_righteye)
				System.err.println("IN --> "+polygon.name);
		}

		// return eyeData
		return eyeData;
	}

}
