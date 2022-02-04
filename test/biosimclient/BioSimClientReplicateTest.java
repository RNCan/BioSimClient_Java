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

import biosimclient.BioSimEnums.RCP;

public class BioSimClientReplicateTest {

	@BeforeClass
	public static void initializeTest() {
		BioSimClient.setLocalConnectionEnabled(true);
		BioSimClient.setTestModeEnabled(true);
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClient.setLocalConnectionEnabled(false);
		BioSimClient.setTestModeEnabled(false);
	}

	@Test
	public void test2085to2090_2rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2085,2090,2);
	}

	@Test
	public void test2085to2090_10rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2085,2090,10);
	}

	@Test
	public void test2065to2090_2rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2065,2090,2);
	}

	@Test
	public void test2015to2030_3rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2015,2030,3);
	}


	private static void testingFutureDegreeDaysWithRCP85andClimateModels(int initialDateYr, int finalDateYr, int nbReplicates) throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientNormalsTest.getPlots();
		
		int expectedObservationsPerPlot = ((finalDateYr - initialDateYr) + 1) * nbReplicates;
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				finalDateYr, 
				locations, 
				RCP.RCP85, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				nbReplicates, 
				null).get(modelName);
		
		for (BioSimPlot plot : oRCP85_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP85_RCM4def.get(plot);
			Assert.assertTrue("The number of observations", firstDataSet.getNumberOfObservations() ==  expectedObservationsPerPlot);
		}
	}

	@Test
	public void test2015to2025_2repForcedClimateGeneration() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		BioSimClient.setForceClimateGenerationEnabled(true);
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = (LinkedHashMap) BioSimClient.generateWeather(2015, 
				2025, 
				locations, 
				RCP.RCP85, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				2, 
				null).get(modelName);
		BioSimDataSet dataset = oRCP85_RCM4def.get(locations.get(0)); 
		int repIndex = dataset.getFieldNames().indexOf("Rep");
		int yearIndex = dataset.getFieldNames().indexOf("Year");
		int refRep = -1;
		int refYear = -1;
		for (Observation obs : dataset.getObservations()) {
			int rep = (Integer) obs.values.get(repIndex);
			int year = (Integer) obs.values.get(yearIndex);
			if (rep < refRep) {
				Assert.fail("The ascending order was not repected in the Rep field");
			} else  if (rep == refRep) {
				if (year <= refYear) {
					Assert.fail("The ascending order was not repected in the Year field");
				} else {
					refYear = year;
				}
			} else {
				refRep = rep;
				refYear = year;  
			}
		}
		System.out.println("Ascending order tested in replicated generated climate!");
		BioSimClient.resetClientConfiguration();
		BioSimClientTestSettings.setForTest(true);
	}

	@Test
	public void test2012to2016_1repForcedClimateGeneration() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		BioSimClient.setForceClimateGenerationEnabled(true);
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = (LinkedHashMap) BioSimClient.generateWeather(2012, 
				2016, 
				locations, 
				RCP.RCP85, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				null).get(modelName);
		BioSimDataSet dataset = oRCP85_RCM4def.get(locations.get(0)); 
		Assert.assertEquals("Testing the number of observations", 5, dataset.getNumberOfObservations());
		BioSimClient.resetClientConfiguration();
		BioSimClientTestSettings.setForTest(true);
	}


	@Test
	public void test1981to2010_2repForcedClimateGeneration() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		BioSimClient.setForceClimateGenerationEnabled(true);
		String modelName = "ClimaticQc_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = (LinkedHashMap) BioSimClient.generateWeather(1981, 
				2010, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				2, 
				null).get(modelName);
		BioSimDataSet dataset = oRCP85_RCM4def.get(locations.get(0)); 
		Assert.assertEquals("Testing the number of observations", 30 * 2, dataset.getNumberOfObservations());
		BioSimClient.resetClientConfiguration();
		BioSimClientTestSettings.setForTest(true);
	}


	@Test
	public void test1981to2010_2repOnTheModelEnd() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientNormalsTest.getPlots();
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> climateOutput = (LinkedHashMap) BioSimClient.generateWeather(1981, 
				2010, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				1, 
				2, 
				null).get(modelName);
		for (BioSimPlot l : locations) {
			BioSimDataSet dataset = climateOutput.get(l); 
			Assert.assertEquals("Testing the number of observations", 30 * 2, dataset.getNumberOfObservations());
		}
	}
	
	@Test
	public void test1981to2010_2repOnWGPlus2repTheModelEnd() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientNormalsTest.getPlots();
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> climateOutput = (LinkedHashMap) BioSimClient.generateWeather(1981, 
				2010, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				2, 
				2, 
				null).get(modelName);
		for (BioSimPlot l : locations) {
			BioSimDataSet dataset = climateOutput.get(l); 
			Assert.assertEquals("Testing the number of observations", 30 * 4, dataset.getNumberOfObservations());
		}
	}
	
}
