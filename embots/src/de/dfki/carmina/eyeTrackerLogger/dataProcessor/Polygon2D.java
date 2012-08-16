package de.dfki.carmina.eyeTrackerLogger.dataProcessor;

import java.util.Vector;

public class Polygon2D {
	public String name;
	private Vector<Float> xpoints;
	private Vector<Float> ypoints;

	public Polygon2D(int nrOfPoints) {
		xpoints = new Vector<Float>(nrOfPoints);
		ypoints = new Vector<Float>(nrOfPoints);
	}

	public void addPoint(float x, float y) {

		xpoints.add(x);
		ypoints.add(y);
	}

	public boolean contains(float testx, float testy) {
		int nrOfPoints = xpoints.size();
		int i, j;
		boolean c = false;

		for (i = 0, j = nrOfPoints - 1; i < nrOfPoints; j = i++) {
			if (((ypoints.elementAt(i) > testy) != (ypoints.elementAt(j) > testy))
					&& (testx < (xpoints.elementAt(j) - xpoints.elementAt(i))
							* (testy - ypoints.elementAt(i))
							/ (ypoints.elementAt(j) - ypoints.elementAt(i))
							+ xpoints.elementAt(i)))
				c = !c;
		}
		return c;
	}

}