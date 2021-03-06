/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2020-22 Her Majesty the Queen in right of Canada
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
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;

public class BioSimServerExceptionTest {

	@BeforeClass
	public static void initializeTest() {
		BioSimClientTestSettings.setForTest(true);
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClientTestSettings.setForTest(false);
	}


	@Test
	public void incorrectNormalsRequestWithNaN() {
		BioSimFakeLocation fakeLocation = new BioSimFakeLocation(Double.NaN, Double.NaN, Double.NaN);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(fakeLocation);
		try {
			BioSimClient.getMonthlyNormals(Period.FromNormals1951_1980, locations, RCP.RCP45, ClimateModel.RCM4);
			Assert.fail("Should have thrown a BioSimClientException instance");
		} catch (BioSimClientException e) {
			String errMsg = e.getMessage();
			Assert.assertTrue("Testing exception", 
					errMsg.contains("the lat parameter cannot be parsed") || errMsg.contains("argument lat could not be parsed to a NaN"));
			Assert.assertTrue("Testing exception", 
					errMsg.contains("the long parameter cannot be parsed") || errMsg.contains("argument long could not be parsed to a NaN"));
			
		} catch (Exception e) {
			Assert.fail("Should have thrown a BioSimClientException instance");
		}
	}
	
	@Test
	public void incorrectNormalsRequestWithInconsistentLatitudeAndLongitudeValues() {
		BioSimFakeLocation fakeLocation = new BioSimFakeLocation(-2000, 2000, Double.NaN);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(fakeLocation);
		try {
			BioSimClient.getMonthlyNormals(Period.FromNormals1951_1980, locations, RCP.RCP45, ClimateModel.RCM4);
			Assert.fail("Should have thrown a BioSimClientException instance");
		} catch (BioSimClientException e) {
			String errMsg = e.getMessage();
			Assert.assertTrue("Testing exception", 
					errMsg.contains("lat is out of range") || errMsg.contains("the latitude must range"));
			Assert.assertTrue("Testing exception", 
					errMsg.contains("long is out of range") || errMsg.contains("the longitude must range"));
		} catch (Exception e) {
			Assert.fail("Should have thrown a BioSimClientException instance");
		}
	}

	
	@Test
	public void incorrectWeatherGenerationRequestWithInconsistentLatitudeAndLongitudeValues() {
		BioSimFakeLocation fakeLocation = new BioSimFakeLocation(-2000, 2000, Double.NaN);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(fakeLocation);
		try {
			BioSimClient.generateWeather(2000, 2001, locations, RCP.RCP45, ClimateModel.RCM4, Arrays.asList(new String[]{"DegreeDay_Annual"}), null);
			Assert.fail("Should have thrown a BioSimClientException instance");
		} catch (BioSimClientException e) {
			String errMsg = e.getMessage();
			Assert.assertTrue("Testing exception", 
					errMsg.contains("lat is out of range") || errMsg.contains("the latitude must range"));
			Assert.assertTrue("Testing exception", 
					errMsg.contains("long is out of range") || errMsg.contains("the longitude must range"));
		} catch (Exception e) {
			Assert.fail("Should have thrown a BioSimClientException instance");
		}
	}

	@Test
	public void incorrectModelHelpRequest() {
		try {
			BioSimClient.getModelHelp("Blabla");
			Assert.fail("Should have thrown a BioSimClientException instance");
		} catch (BioSimClientException e) {
			String errMsg = e.getMessage();
			Assert.assertTrue("Testing exception", errMsg.contains("Error: Model Blabla does not exist"));
		} catch (Exception e) {
			Assert.fail("Should have thrown a BioSimClientException instance");
		}
	}

	@Test
	public void incorrectModelDefaultParametersRequest() {
		try {
			BioSimClient.getModelDefaultParameters("Blabla");
			Assert.fail("Should have thrown a BioSimClientException instance");
		} catch (BioSimClientException e) {
			String errMsg = e.getMessage();
			Assert.assertTrue("Testing exception", errMsg.contains("Error: Model Blabla does not exist"));
		} catch (Exception e) {
			Assert.fail("Should have thrown a BioSimClientException instance");
		}
	}

}
