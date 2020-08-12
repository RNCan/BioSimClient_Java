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
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimClientTest.FakeLocation;


public class BioSimClientModelWithMultithreading {

	
	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5C() throws BioSimClientException, BioSimServerException {
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
		LinkedHashMap<BioSimPlot, BioSimDataSet>  teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2001, locations, null, null, "DegreeDay_Annual", true, parms);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time single threading = " + elapsedTime);
		
		initial = System.currentTimeMillis();
		BioSimClient.setMultithreadingEnabled(true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getModelOutput(initialDateYr, 2001, locations, null, null, "DegreeDay_Annual", true, parms);
		elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time multi threading = " + elapsedTime);
		
		for (BioSimPlot plot : teleIORefs.keySet()) {
			BioSimDataSet firstDataSet = teleIORefs.get(plot);
			BioSimDataSet secondDataSet = teleIORefs2.get(plot);
			
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
//			System.out.println("There is at least one observation");

			Assert.assertTrue("Same number of observations", firstDataSet.getNumberOfObservations() == secondDataSet.getNumberOfObservations());
//			System.out.println("There is at least one observation");

			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				double actualDD = (Double) secondDataSet.getValueAt(i, ddFieldIndex);
				double expectedDD = (Double) firstDataSet.getValueAt(i, ddFieldIndex);
				Assert.assertEquals("Testing degree-days above 5C",	expectedDD,	actualDD, 1E-8);
			}
		}
		System.out.println("Degree-days above 5C successfully tested with multithreading!");
		
	}


	
	
	
}
