package de.dfki.carmina.eyeTrackerLogger.dataProcessor;

import java.io.*;
import java.util.zip.*;

public class GZip {

    public static String zip(String unzipped)
    {
    	byte[] zipped = null;

		try {
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));

			bufos.write(unzipped.getBytes());
		    bufos.close();
		    zipped = bos.toByteArray();
		    bos.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return new String(zipped);
	}

	public static String unzip(String zipped)
	{
        BufferedReader stream = null;
        String unzipped = "";
        String line;

		try {
			stream = new BufferedReader(new InputStreamReader(new GZIPInputStream(
					new ByteArrayInputStream(zipped.getBytes()))));

			while ((line = stream.readLine()) != null)
			      unzipped += line+"\n";

		} catch (IOException e) {
			e.printStackTrace();
		}

		return unzipped;
	}
}