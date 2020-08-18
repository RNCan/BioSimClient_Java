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

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.RCP;


public class BioSimClientPastAndFutureDailyClimateTest {

	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDailyOverlappingPastAndFuture() throws BioSimClientException, BioSimServerException {
//		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2040, locations, null, null, "DegreeDay_Annual", true, null);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getModelOutput(initialDateYr, 2040, locations, null, null, "DegreeDay_Annual", true, null);
		
		for (BioSimPlot plot : teleIORefs.keySet()) {
			BioSimDataSet firstDataSet = teleIORefs.get(plot);
			BioSimDataSet secondDataSet = teleIORefs2.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");

			int dateFieldIndex = firstDataSet.getFieldNames().indexOf("Year");
			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				double d1 = (Double) firstDataSet.getValueAt(i, ddFieldIndex);
				double d2 = (Double) secondDataSet.getValueAt(i, ddFieldIndex);;
				int dateYr = (Integer) firstDataSet.getValueAt(i, dateFieldIndex);
				if (dateYr >= initialDateYr && dateYr < 2019) {		// From observation
					Assert.assertEquals("Testing if the degree-days are the same before 2019", 
							d1,	d2, 1E-8);
				} else {											// generated from normals
					Assert.assertTrue("Testing that the degree-days are different for 2019 and after",
							Math.abs(d1 - d2) > 1E-8);
				}
			}
			System.out.println("Degree-days before 2019 are the same and those after vary.");
		}
		
	}

	
	/*
	 * Tests future climate with default climate model and RCP.
	 */
	@Test
	public void testingFutureDegreeDaysWithDefaultValuesOfRCPsandClimateModels() throws BioSimClientException, BioSimServerException {
//		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2090;
		int finalDateYr = 2091;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45def_RCM4def = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, null, null, "DegreeDay_Annual", true, null);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45_RCM4def = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, RCP.RCP45, null, "DegreeDay_Annual", true, null);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45def_RCM4 = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, null, ClimateModel.RCM4, "DegreeDay_Annual", true, null);
		
		for (BioSimPlot plot : oRCP45def_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP45def_RCM4def.get(plot);
			BioSimDataSet secondDataSet = oRCP45_RCM4def.get(plot);
			BioSimDataSet thirdDataSet = oRCP45def_RCM4.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations between first and second dataset", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			Assert.assertEquals("Testing the number of observations between second and third dataset", 
					secondDataSet.getNumberOfObservations(), 
					thirdDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");
			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				double d1 = ((Number) firstDataSet.getValueAt(i, ddFieldIndex)).doubleValue();
				double d2 = ((Number) secondDataSet.getValueAt(i, ddFieldIndex)).doubleValue();
				double d3 = ((Number) thirdDataSet.getValueAt(i, ddFieldIndex)).doubleValue();
				Assert.assertEquals("Testing if the degree-days are equal between first and second datasets", d1, d2, 420);
				Assert.assertEquals("Testing if the degree-days are equal between second and third datasets", d2, d3, 420);
			}
			System.out.println("Degree-days tested for default values.");
		}
		
	}

	
	
	/*
	 * Tests future climate with RCP 8.5 and default climate model.
	 */
	@Test
	public void testingFutureDegreeDaysWithRCP85andClimateModels() throws BioSimClientException, BioSimServerException {
//		BioSimClient.setMultithreadingEnabled(false);
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2090;
		int finalDateYr = 2091;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, RCP.RCP85, null, "DegreeDay_Annual", true, null);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4 = BioSimClient.getModelOutput(initialDateYr, finalDateYr, locations, RCP.RCP85, ClimateModel.RCM4, "DegreeDay_Annual", true, null);
		
		for (BioSimPlot plot : oRCP85_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP85_RCM4def.get(plot);
			BioSimDataSet secondDataSet = oRCP85_RCM4.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations between first and second dataset", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");
			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				double d1 = ((Number) firstDataSet.getValueAt(i, ddFieldIndex)).doubleValue();
				double d2 = ((Number) secondDataSet.getValueAt(i, ddFieldIndex)).doubleValue();
				Assert.assertEquals("Testing if the degree-days are equal between first and second datasets", d1, d2, 400);
			}
			System.out.println("Degree-days tested for default values.");
		}
		
	}

	
	
	
}