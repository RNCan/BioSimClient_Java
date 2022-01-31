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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;

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
	
	
	@BeforeClass
	public static void initializeTest() {
		BioSimClient.isLocal = true;
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClient.isLocal = false;
	}

	
	
	@Test
	public void getNormalsFor1981_2010() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals1981_2010, 
				getPlots(), 
				null, 
				null);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				ClimateModel.Hadley);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080,
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.Hadley);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				null);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				null);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP45() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP45, 
				ClimateModel.GCM4);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP85() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.GCM4);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}
	
	
	@Test
	public void getMonthlyNormalsFor1971_2000() throws Exception {
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIO = BioSimClient.getMonthlyNormals(Period.FromNormals1971_2000, 
				getPlots(), 
				null, 
				null);
		
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		BioSimDataSet dataSet = BioSimDataSet.convertLinkedHashMapToBioSimDataSet(teleIO);
		String observedString = BioSimClientTestSettings.getJSONObject(dataSet, validationFilename);
		
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);

		Assert.assertEquals("Comparing the two LinkedHashMap instances", referenceString, observedString);
	}
	
}
