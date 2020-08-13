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

import biosimclient.BioSimClientTest.FakeLocation;


public class BioSimClientModelWithMultithreading {

	/*
	 * A reference test for multithreading on the server side
	 * With 4 processes on the server side this takes 27.5 sec.
	 */
	public static void testingWithDegreeDaysAbove5C() throws BioSimClientException, BioSimServerException {
		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 100; i++) {
			FakeLocation loc = new FakeLocation(45 + i * .05,
					-74 + i * .05,
					300 + 1 * i);
			locations.add(loc);
		}
 
		int initialDateYr = 2000;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		long initial = System.currentTimeMillis();
		BioSimClient.getModelOutput(initialDateYr, 2010, locations, null, null, "DegreeDay_Annual", true, parms);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time = " + elapsedTime);
	}


	public static void main(String[] args) throws BioSimClientException, BioSimServerException {
		BioSimClientModelWithMultithreading.testingWithDegreeDaysAbove5C();
	}
	
	
}
