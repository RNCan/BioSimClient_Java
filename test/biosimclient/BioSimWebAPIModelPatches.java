/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2026 His Majesty the King in right of Canada
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
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.RCP;

public class BioSimWebAPIModelPatches {

	private static List<String> MODELS_WITH_MISSING_INITIAL_YEAR = Arrays.asList(new String[] { "BudBurst", 
	    "Climate_Mosture_Index_Annual",     // a typo in BioSIM 11
	    "Gypsy_Moth_Seasonality",
	    "HemlockWoollyAdelgid_Annual",
	    "MPB_Cold_Tolerance_Annual",
	    "MPB_SLR",
	    "Spruce_Budworm_Biology_Annual",
	    "SpruceBeetle" });

	private static List<String> MODELS_REQUIRING_MORE_THAN_ONE_YEAR = Arrays.asList(new String[] { "EmeraldAshBorerColdHardiness_Annual",
	    "HemlockWoollyAdelgid_Daily",
	    "MPB_Cold_Tolerance_Daily",
	    "Standardised_Precipitation_Evapotranspiration_Index" });

	@SuppressWarnings("unchecked")
	@Test
	public void test01ModelsWithMissingInitialYear() throws BioSimClientException, BioSimServerException {
		System.out.println("Testing models with missing initial year...");
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot refPlot = BioSimClientNormalsTest.getPlots().get(0); 
		locations.add(refPlot);
		List<String> models = new ArrayList<String>(MODELS_WITH_MISSING_INITIAL_YEAR);
		models.add("DegreeDay_Annual");
		
		int initYear = 2015;
		Map<String, Object> pastClimateGeneration = BioSimClient.generateWeather(initYear, 
				initYear + 5, 
				locations, 
				RCP.CONSTANT_CLIMATE, 
				null, 
				models, 
				null);
		
		for (String model : models) {
			System.out.println("    Testing model " + model + "...");
			if (!model.equals("MPB_SLR")) {
				LinkedHashMap<BioSimPlot, BioSimDataSet> modelOutput = (LinkedHashMap<BioSimPlot, BioSimDataSet>) pastClimateGeneration.get(model);
				BioSimDataSet modelOutputForThisPlot = modelOutput.get(refPlot);
				int yearIndex = modelOutputForThisPlot.getFieldNames().indexOf("Year");
				if (yearIndex == -1) {
					throw new UnsupportedOperationException("Cannot find field year in BioSimDataSet instance!");
				}
				Assert.assertEquals("Testing that first year is " + initYear + " for model " + model, 
						initYear,
						(int) modelOutputForThisPlot.getObservations().get(0).values.get(yearIndex));
			}
		}
		
		System.out.println("Done.");
	}
	

	@SuppressWarnings("unchecked")
	@Test
	public void test02ModelsRequiringMoreThanOneYear() throws BioSimClientException, BioSimServerException {
		System.out.println("Testing models requiring more than one year in their MTS...");
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		BioSimPlot refPlot = BioSimClientNormalsTest.getPlots().get(0); 
		locations.add(refPlot);
		List<String> models = new ArrayList<String>(MODELS_REQUIRING_MORE_THAN_ONE_YEAR);
		models.add("DegreeDay_Annual");
		
		int initYear = 2015;
		Map<String, Object> pastClimateGeneration = BioSimClient.generateWeather(initYear, 
				initYear, 
				locations, 
				RCP.CONSTANT_CLIMATE, 
				null, 
				models, 
				null);
		
		for (String model : models) {
			System.out.println("    Testing model " + model + "...");
				LinkedHashMap<BioSimPlot, BioSimDataSet> modelOutput = (LinkedHashMap<BioSimPlot, BioSimDataSet>) pastClimateGeneration.get(model);
				BioSimDataSet modelOutputForThisPlot = modelOutput.get(refPlot);
				int yearIndex = modelOutputForThisPlot.getFieldNames().indexOf("Year");
				if (yearIndex == -1) {
					throw new UnsupportedOperationException("Cannot find field year in BioSimDataSet instance!");
				}
				Assert.assertEquals("Testing that first year is " + initYear + " for model " + model, 
						initYear,
						(int) modelOutputForThisPlot.getObservations().get(0).values.get(yearIndex));
		}
		
		System.out.println("Done.");
	}

	
}
