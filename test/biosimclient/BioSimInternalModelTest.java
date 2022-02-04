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


public class BioSimInternalModelTest {

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
	public void testingEachModel() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(new BioSimFakeLocation(45, -74, 300));

		List<String> modelList = BioSimClient.getModelList();
		LinkedHashMap<String, Object> overallOutput = BioSimClient.generateWeather(2018, 
				2019, 
				locations, 
				null, 
				null, 
				modelList, 
				1, 
				1, 
				null);
		LinkedHashMap<String, Boolean> resultMap = new LinkedHashMap<String, Boolean>();
		for (String modelName : modelList) {
			Object output = overallOutput.get(modelName);
			if (output instanceof LinkedHashMap) 
				resultMap.put(modelName, true);
			else if (output instanceof BioSimClientException) 
				resultMap.put(modelName, false);
			else 
				throw new Exception("The value of the map should be either a LinkedHashMap or a BioSimClient Exception!");
		}
		String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
		String validationFilename = BioSimClientTestSettings.getValidationFilename(methodName);
		String observedString = this.getJSONObject(resultMap, validationFilename);
		String referenceString = BioSimClientTestSettings.getReferenceString(validationFilename);
		Assert.assertEquals("Comparing strings", referenceString, observedString);
	}

	private String getJSONObject(LinkedHashMap<String, Boolean> oMap, String validationFilename) throws IOException {
		ObjectMapper om = new ObjectMapper();
		String outputString = om.writeValueAsString(oMap);
		if (!BioSimClientTestSettings.Validation) {
			FileWriter out = new FileWriter(validationFilename);
			out.write(outputString);
			out.close();
		}
		Assert.assertTrue("Should be in validation mode.", BioSimClientTestSettings.Validation);
		return outputString;
	}
}


// 9 models with variable results

// On development server as of 2021-01-18
//HemlockLooper tested - Number of unsuccessful check = 2282 / 16790
//LaricobiusNigrinus tested - Number of unsuccessful check = 1271 / 16790
//ObliqueBandedLeafroller tested - Number of unsuccessful check = 2979 / 14600
//Spruce_Budworm_Biology_Annual tested - Number of unsuccessful check = 1 / 4
//Spruce_Budworm_Biology tested - Number of unsuccessful check = 1216 / 21170
//Tranosema_OBL_SBW_daily tested - Number of unsuccessful check = 10686 / 51100
//Western_Spruce_Budworm_annual tested - Number of unsuccessful check = 13 / 30
//Western_Spruce_Budworm tested - Number of unsuccessful check = 2818 / 27740
//WhitemarkedTussockMoth tested - Number of unsuccessful check = 1258 / 14600




// On production server
//HemlockLooper tested - Number of unsuccessful check = 2203 / 16790
//LaricobiusNigrinus tested - Number of unsuccessful check = 1131 / 16790
//ObliqueBandedLeafroller tested - Number of unsuccessful check = 2968 / 14600
//Spruce_Budworm_Biology_Annual tested - Number of unsuccessful check = 1 / 4
//Spruce_Budworm_Biology tested - Number of unsuccessful check = 1190 / 21170
//Tranosema_OBL_SBW_daily tested - Number of unsuccessful check = 10911 / 51100
//Western_Spruce_Budworm_annual tested - Number of unsuccessful check = 14 / 30
//Western_Spruce_Budworm tested - Number of unsuccessful check = 2793 / 27740
//WhitemarkedTussockMoth tested - Number of unsuccessful check = 938 / 14600