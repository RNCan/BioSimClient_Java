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


public class BioSimClientModelWithParametersTest {

	@BeforeClass
	public static void initializeTest() {
		BioSimClientTestSettings.setForTest(true);
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClientTestSettings.setForTest(false);
	}


	
	/*
	 * Testing parameter map conversion to String
	 */
	@Test
	public void testingParametersWithDegreeDays() throws Exception {
		BioSimParameterMap parameterMap = BioSimClient.getModelDefaultParameters("DegreeDay_Annual");
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		String observedString = BioSimClientTestSettings.getJSONObject(parameterMap, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	

	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5CAnEmptyParameters() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		BioSimParameterMap parms = new BioSimParameterMap();
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}


	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAndGrowingSeason() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);

		String[] modelNames = new String[]{"GrowingSeason", "DegreeDay_Annual"};
		LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> teleIO = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(modelNames), 
				Arrays.asList(new BioSimParameterMap[] {null, parms}));
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		LinkedHashMap<String, BioSimDataSet> finalMap = new LinkedHashMap<String, BioSimDataSet>();
		for (String modelName : teleIO.keySet()) {
			finalMap.put(modelName, BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO.get(modelName)));
		}
		String observedString = BioSimClientTestSettings.getJSONObject(finalMap, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5C() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}


	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5CLong() throws Exception {
		int initialDateYr = 1980;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		String modelName = "DegreeDay_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = (LinkedHashMap) BioSimClient.generateWeather(initialDateYr, 
				2020, 
				BioSimClientNormalsTest.getPlots(), 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}),
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);

	}
	
	
	
}
