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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import repicea.serial.xml.XmlDeserializer;
import repicea.serial.xml.XmlSerializer;


public class BioSimClientModelWithParametersTest {

	
	/*
	 * Testing parameter map conversion to String
	 */
	@Test
	public void testingParametersWithDegreeDays() throws Exception {
		BioSimParameterMap parameterMap = BioSimClient.getModelDefaultParameters("DegreeDay_Annual");
		String paramStr = parameterMap.toString();
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + methodName + "Ref.zml";
		if (!BioSimClientTestSettings.Validation) {
			XmlSerializer serializer = new XmlSerializer(validationFilename);
			serializer.writeObject(paramStr);
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		XmlDeserializer deser = new XmlDeserializer(validationFilename);
		String refString = (String) deser.readObject();
		Assert.assertEquals(refString, paramStr);
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
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + methodName + "Ref.zml";
		if (!BioSimClientTestSettings.Validation) {
			XmlSerializer serializer = new XmlSerializer(validationFilename);
			serializer.writeObject(teleIO);
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		XmlDeserializer deser = new XmlDeserializer(validationFilename);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = (LinkedHashMap) deser.readObject();
		
		Assert.assertTrue("Comparing the two LinkedHashMap instances",
				BioSimClientTestSettings.areTheseInnerMapsEqual(teleIO, teleIORefs));
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
		LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(modelNames), 
				Arrays.asList(new BioSimParameterMap[] {null, parms}));
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + methodName + "Ref.zml";
		if (!BioSimClientTestSettings.Validation) {
			XmlSerializer serializer = new XmlSerializer(validationFilename);
			serializer.writeObject(teleIO);
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		XmlDeserializer deser = new XmlDeserializer(validationFilename);
		LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> teleIORefs = (LinkedHashMap) deser.readObject();
		Assert.assertTrue("Comparing the two LinkedHashMap instances",
				BioSimClientTestSettings.areTheseOuterMapsEqual(teleIO, teleIORefs));
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
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2001, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + methodName + "Ref.zml";
		if (!BioSimClientTestSettings.Validation) {
			XmlSerializer serializer = new XmlSerializer(validationFilename);
			serializer.writeObject(teleIO);
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		XmlDeserializer deser = new XmlDeserializer(validationFilename);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = (LinkedHashMap) deser.readObject();
		
		Assert.assertTrue("Comparing the two LinkedHashMap instances",
				BioSimClientTestSettings.areTheseInnerMapsEqual(teleIO, teleIORefs));
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
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2020, 
				BioSimClientNormalsTest.getPlots(), 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}),
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + methodName + "Ref.zml";
		if (!BioSimClientTestSettings.Validation) {
			XmlSerializer serializer = new XmlSerializer(validationFilename);
			serializer.writeObject(teleIO);
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		XmlDeserializer deser = new XmlDeserializer(validationFilename);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = (LinkedHashMap) deser.readObject();
		
		Assert.assertTrue("Comparing the two LinkedHashMap instances",
				BioSimClientTestSettings.areTheseInnerMapsEqual(teleIO, teleIORefs));

	}
	
	
	
}
