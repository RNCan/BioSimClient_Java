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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import biosimclient.BioSimClientTest.FakeLocation;


public class BioSimClientShutdownHookTest {

	
	BioSimClientShutdownHookTest() {}
	
	/*
	 * A reference test for multithreading on the server side
	 * With 4 processes, 100 locations from 2000 to 2010 on the server side this takes 27.5 sec.
	 */
	public void testingWithDegreeDaysAbove5C(int initialDateYr, int finalDateYr) throws Exception {
//		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 5; i++) {
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
				false, 
				parms);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time = " + elapsedTime);
	}


	public static void main(String[] args) throws Exception {
		BioSimClientShutdownHookTest bioSim = new BioSimClientShutdownHookTest();
		try {
			bioSim.testingWithDegreeDaysAbove5C(2000, 2005);
		} catch(Exception e) {}
//		BioSimClient.clearCache();
		Map copy = BioSimClient.GeneratedClimateMap;
		int u = 0;
		System.exit(0);
	}
	
	
}
