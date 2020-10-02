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

import biosimclient.BioSimEnums.RCP;

public class BioSimClientTestsWithReplicates {

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
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		
		int expectedObservationsPerPlot = ((finalDateYr - initialDateYr) + 1) * nbReplicates;
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, RCP.RCP85, null, "DegreeDay_Annual", nbReplicates, null);
		
		for (BioSimPlot plot : oRCP85_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP85_RCM4def.get(plot);
			Assert.assertTrue("The number of observations", firstDataSet.getNumberOfObservations() ==  expectedObservationsPerPlot);
		}
	}

	
	@Test
	public void test2015to2025_2repForcedClimateGeneration() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientTestsOnNormals.getPlots().get(0));
		BioSimClient.setForceClimateGenerationEnabled(true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getModelOutput(2015, 2025, locations, RCP.RCP85, null, "DegreeDay_Annual", 2, null);
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
	}


	
	@Test
	public void test2012to2016_1repForcedClimateGeneration() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientTestsOnNormals.getPlots().get(0));
		BioSimClient.setForceClimateGenerationEnabled(true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getModelOutput(2012, 2016, locations, RCP.RCP85, null, "DegreeDay_Annual", null);
		BioSimDataSet dataset = oRCP85_RCM4def.get(locations.get(0)); 
		Assert.assertEquals("Testing the number of observations", 5, dataset.getNumberOfObservations());
		BioSimClient.resetClientConfiguration();
	}

	
}
