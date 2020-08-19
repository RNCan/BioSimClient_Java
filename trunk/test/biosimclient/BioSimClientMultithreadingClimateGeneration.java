/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2020 Her Majesty the Queen in right of Canada
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package biosimclient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import biosimclient.BioSimClientTest.FakeLocation;


public class BioSimClientMultithreadingClimateGeneration {

	final int id;
	final String filename;
	final boolean isCheckEnabled;
	
	BioSimClientMultithreadingClimateGeneration(int id, boolean isCheckEnabled) {
		this.id = id;
		String tmpDirectory = System.getProperty("java.io.tmpdir");
		this.filename = tmpDirectory + File.separator + "refClimGenMap" + id + ".xml";
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
		this.isCheckEnabled = isCheckEnabled;
	}
	
	/*
	 * A reference test for multithreading on the server side
	 * With 4 processes, 100 locations from 2000 to 2010 on the server side this takes 27.5 sec.
	 */
	public void testingWithDegreeDaysAbove5C(int initialDateYr, int finalDateYr) throws Exception {
//		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 50; i++) {
			FakeLocation loc = new FakeLocation(45 + i * .1,
					-74 + i * .1,
					300 + 5 * i);
			locations.add(loc);
		}
 
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		long initial = System.currentTimeMillis();
		Map<BioSimPlot, BioSimDataSet> outputMap = BioSimClient.getModelOutput(initialDateYr, 
				finalDateYr, 
				locations, 
				null, 
				null, 
				"DegreeDay_Annual", 
				true, 
				parms);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time = " + elapsedTime);
		
		if (isCheckEnabled) {
			File file = new File(filename);
			if (!file.exists()) {
				FileOutputStream fos = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(outputMap);
				oos.close();
				fos.close();
			} else {
				FileInputStream fis = new FileInputStream(filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				Map refMap = (Map) ois.readObject();
				Iterator<BioSimDataSet> iterObs = outputMap.values().iterator();
				Iterator<BioSimDataSet> iterRef = refMap.values().iterator();
				while(iterObs.hasNext()) {
					BioSimDataSet obsDataSet = iterObs.next();
					BioSimDataSet refDataSet = iterRef.next();
					List<Observation> observations = obsDataSet.getObservations();
					List<Observation> references = refDataSet.getObservations();
					if (observations.size() != references.size()) {
						throw new Exception("The number of observations is inconsistent!");
					} else {
						for (int i = 0; i < observations.size(); i++) {
							Object[] o = observations.get(i).toArray();
							Object[] r = references.get(i).toArray();
							if (o.length != r.length) {
								throw new Exception("The number of values in records " + i + " is inconsistent!");
							}
							for (int j = 0; j < o.length; j++) {
								if (o[j] instanceof String) {
									if (!o[j].equals(r[j])) {
										throw new Exception("Value " + o[j].toString() + " is different from value " + r[j].toString());
									}
								} else if (o[j] instanceof Number) {
									double oDouble = ((Number) o[j]).doubleValue();
									double rDouble = ((Number) r[j]).doubleValue();
									if (Math.abs(oDouble - rDouble) > 1E-8) {
										throw new Exception("Value " + o[j].toString() + " is different from value " + r[j].toString());
									}
								} else {
									throw new Exception("The value " +o[j].toString() + " is not a number nor a String!");
								}
							}
						}
					}
				}
				System.out.println("Successfully compared!");
			}
		}
	}


	public static void main(String[] args) throws Exception {
		int id = Integer.parseInt(args[0]);
		boolean isLocal = Boolean.parseBoolean(args[1]);
		int initialDateYr = Integer.parseInt(args[2]);
		int finalDateYr = Integer.parseInt(args[3]);
		boolean isCheckEnabled = Boolean.parseBoolean(args[4]);
		BioSimClient.isLocal = isLocal;
		BioSimClientMultithreadingClimateGeneration bioSim = new BioSimClientMultithreadingClimateGeneration(id, isCheckEnabled);
		for (int i = 0; i < 20; i++) {
			bioSim.testingWithDegreeDaysAbove5C(initialDateYr, finalDateYr);
		}
		if (isCheckEnabled) {
			System.out.println("All the runs are consistent!");
		}
	}
	
	
}
