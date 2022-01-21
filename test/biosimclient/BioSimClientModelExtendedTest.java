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


public class BioSimClientModelExtendedTest {

	/*
	 * Testing ClimaticQc_Annual model
	 */
	@Test
	public void testingWithClimaticQc_Annual() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		String modelName = "ClimaticQc_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				null).get(modelName);
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
	 * Testing Climatic_Annual model
	 */
	@Test
	public void testingWithClimatic_Annual() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		String modelName = "Climatic_Annual";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}),
				null).get(modelName);
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
	 * Testing Climatic_Monthly model
	 */
	@Test
	public void testingWithClimatic_Monthly() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		String modelName = "Climatic_Monthly";
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.generateWeather(initialDateYr, 
				2000, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}),
				null).get(modelName);
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
