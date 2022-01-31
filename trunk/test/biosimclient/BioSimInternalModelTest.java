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
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class BioSimInternalModelTest {

	@BeforeClass
	public static void initializeTest() {
		BioSimClient.isLocal = true;
	}
	
	@AfterClass
	public static void finalizeTest() {
		BioSimClient.isLocal = false;
	}


	@Test
	public void testingEachModelExceptPlanHardiness() throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(new BioSimFakeLocation(45, -74, 300));

		List<String> blackList = new ArrayList<String>();
		blackList.add("PlantHardinessCanada"); // because it needs 30 years of weather
		blackList.add("PlantHardinessUSA"); // because it needs 30 years of weather
		int nbFailures = 0;
		int nbSuccesses = 0;
		List<String> modelList = BioSimClient.getModelList();
		modelList.removeAll(blackList);
		LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> overallOutput = BioSimClient.generateWeather(2018, 
				2019, 
				locations, 
				null, 
				null, 
				modelList, 
				1, 
				1, 
				null);
		for (String modelName : modelList) {
			LinkedHashMap<BioSimPlot, BioSimDataSet> output = overallOutput.get(modelName);
			Assert.assertTrue("There is only one dataset in the output", output.size() == 1);
			BioSimDataSet obsDataset = output.values().iterator().next();
			List<Observation> observations = obsDataset.getObservations();
			String validationFilename = BioSimClientTestSettings.ProjectRootPath + File.separator + "testData" + File.separator + modelName + "ref.ser";;


			//					UNCOMMENT THESE TWO LINES TO UPDATE THE TEST RESULTS
			//					FileOutputStream fos = new FileOutputStream(filename);
			//					ObjectOutputStream oos = new ObjectOutputStream(fos);
			//					oos.writeObject(observations);
			//					oos.close();

			FileInputStream fis = new FileInputStream(validationFilename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			List<Observation> references = (List) ois.readObject();
			ois.close();	
			Assert.assertEquals("Testing dataset have equal size", 
					observations.size(), 
					references.size());

			int nbSuccessful = 0;
			int nbUnsuccessful = 0;
			for (int i = 0; i < references.size(); i++) {
				List<Object> refValues = references.get(i).values;
				List<Object> obsValues = observations.get(i).values;
				Assert.assertEquals("Testing observations have equal number of fields", 
						refValues.size(), 
						obsValues.size());
				for (int j = 0; j < refValues.size(); j++) {
					Object refValue = refValues.get(j);
					Object obsValue = obsValues.get(j);
					if (refValue instanceof Number && obsValue instanceof Number) {
						if (Math.abs(((Number) refValue).doubleValue() - ((Number) obsValue).doubleValue()) < 1E-8) {
							nbSuccessful++;
						} else {
							nbUnsuccessful++;
						}
					} else {
						if (refValue.equals(obsValue)) {
							nbSuccessful++;
						} else {
							nbUnsuccessful++;
						}
					}
				}
			}
			nbSuccesses++;
			int total = nbUnsuccessful + nbSuccessful;
			if (nbUnsuccessful > 0) {
				System.out.println(modelName + " tested - Number of unsuccessful check = " + nbUnsuccessful + " / " + total);
			} else {
				System.out.println(modelName + " successfully tested");
			}
		}
		System.out.println("Nb of failures = " + nbFailures + "; Nb of successes = " + nbSuccesses);
		Assert.assertTrue("No exception thrown", nbFailures == 0);
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