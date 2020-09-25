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


public class BioSimClientModelNbNearestNeighboursTest {

	/*
	 * Testing ClimaticQc_Annual model
	 */
	@Test
	public void testingWithDefaultFourClimateStations() throws BioSimClientException, BioSimServerException {
		BioSimClient.resetClientConfiguration();
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot plot = BioSimClientTestsOnNormals.getPlots().get(0);
		locations.add(plot);
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "ClimaticQc_Annual", null);
		
		BioSimClient.setNbNearestNeighbours(4);

		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "ClimaticQc_Annual", null);
		
		BioSimDataSet firstDataSet = teleIORefs.get(plot);
		Assert.assertTrue("First dataset has one observation", firstDataSet.getNumberOfObservations() == 1);
		
		BioSimDataSet secondDataSet = teleIORefs2.get(plot);
		Assert.assertTrue("Second dataset has one observation", secondDataSet.getNumberOfObservations() == 1);

		List<Object> firstObs = firstDataSet.getObservations().get(0).values;
		Assert.assertTrue("First observation has some values", firstObs.size() > 0);
		List<Object> secondObs = secondDataSet.getObservations().get(0).values;
		Assert.assertTrue("Second observation has some values", secondObs.size() > 0);

		for (int i = 2; i < firstObs.size(); i++) {
			Object obj1 = firstObs.get(i);
			Object obj2 = secondObs.get(i);
			Assert.assertEquals("Testing if values at location " + i + " are equal", obj1, obj2);
		}
		BioSimClient.resetClientConfiguration();
	}

	@Test
	public void testingWithTwelveClimateStations() throws BioSimClientException, BioSimServerException {
		BioSimClient.resetClientConfiguration();
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot plot = BioSimClientTestsOnNormals.getPlots().get(0);
		locations.add(plot);
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "ClimaticQc_Annual", null);
		
		BioSimClient.setNbNearestNeighbours(20);

		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "ClimaticQc_Annual", null);
		
		BioSimDataSet firstDataSet = teleIORefs.get(plot);
		Assert.assertTrue("First dataset has one observation", firstDataSet.getNumberOfObservations() == 1);
		
		BioSimDataSet secondDataSet = teleIORefs2.get(plot);
		Assert.assertTrue("Second dataset has one observation", secondDataSet.getNumberOfObservations() == 1);

		List<Object> firstObs = firstDataSet.getObservations().get(0).values;
		Assert.assertTrue("First observation has some values", firstObs.size() > 0);
		List<Object> secondObs = secondDataSet.getObservations().get(0).values;
		Assert.assertTrue("Second observation has some values", secondObs.size() > 0);

		for (int i = 2; i < 10; i++) {
			Object obj1 = firstObs.get(i);
			Object obj2 = secondObs.get(i);
			Assert.assertTrue("Testing if values at location " + i + " are different", !obj1.equals(obj2));
		}
		BioSimClient.resetClientConfiguration();
	}


	
	
	
	
	
}
