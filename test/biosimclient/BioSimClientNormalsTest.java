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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;
import repicea.serial.xml.XmlDeserializer;
import repicea.serial.xml.XmlSerializer;

public class BioSimClientNormalsTest {

	
	private static List<BioSimPlot> Plots;
	
	
	static List<BioSimPlot> getPlots() {
		if (Plots == null) {
			Plots = new ArrayList<BioSimPlot>();
			Plots.add(new BioSimPlotImpl(46.87,-71.25,114));
			Plots.add(new BioSimPlotImpl(46.03,-73.12,15));
		}
		return Plots;
	}
	
	
	@Test
	public void getNormalsFor1981_2010() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals1981_2010, 
				getPlots(), 
				null, 
				null);
		
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

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				ClimateModel.Hadley);
		
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

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080,
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.Hadley);
		
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

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				null);
		
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

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				null);
		
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

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP45, 
				ClimateModel.GCM4);
		
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

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.GCM4);
		
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
	
	
	@Test
	public void getMonthlyNormalsFor1971_2000() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getMonthlyNormals(Period.FromNormals1971_2000, 
				getPlots(), 
				null, 
				null);
		
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
