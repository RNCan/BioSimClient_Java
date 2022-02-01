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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class BioSimClientModelNbNearestNeighboursTest {

	@BeforeClass
	public static void initializeTest() {
		BioSimClient.isLocal = true;
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClient.isLocal = false;
	}

	/*
	 * Testing ClimaticQc_Annual model and ensuring that the default nb of nearest neighbour is 4.
	 */
	@Test
	public void testingWithDefaultFourClimateStations() throws Exception {
		BioSimClient.resetClientConfiguration();
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot plot = BioSimClientNormalsTest.getPlots().get(0);
		locations.add(plot);
		int initialDateYr = 2000;
		String modelName = "ClimaticQc_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[] {modelName}), 
				null).get(modelName);
		BioSimDataSet bsds1 = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIORefs);
		String referenceString = BioSimClientTestSettings.getJSONObject(bsds1, null);

		BioSimClient.setNbNearestNeighbours(4);

		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[] {modelName}), 
				null).get(modelName);
		BioSimDataSet bsds2 = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIORefs2);
		String observedString = BioSimClientTestSettings.getJSONObject(bsds2, null);

		Assert.assertEquals("Comparing the two LinkedHasMap instances", referenceString, observedString);
		BioSimClient.resetClientConfiguration();
	}

	@Test
	public void testingWithTwentyClimateStations() throws Exception {
		BioSimClient.resetClientConfiguration();
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot plot = BioSimClientNormalsTest.getPlots().get(0);
		locations.add(plot);
		int initialDateYr = 2000;
		String modelName = "ClimaticQc_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				null).get(modelName);
		BioSimDataSet bsds1 = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIORefs);
		String referenceString = BioSimClientTestSettings.getJSONObject(bsds1, null);

		BioSimClient.setNbNearestNeighbours(20);

		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}),
				null).get(modelName);
		BioSimDataSet bsds2 = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIORefs2);
		String observedString = BioSimClientTestSettings.getJSONObject(bsds2, null);

		Assert.assertTrue("Comparing the two LinkedHasMap instances. Expecting them to be different", !referenceString.equals(observedString));
		BioSimClient.resetClientConfiguration();
	}


	
	
	
	
	
}
