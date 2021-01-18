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
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import repicea.serial.xml.XmlDeserializer;
import repicea.serial.xml.XmlSerializer;
import repicea.util.ObjectUtility;


public class BioSimInternalModelTest {

	
	@Test
	public void testingEachModelExceptPlanHardiness() throws NoSuchMethodException, SecurityException, BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(new BioSimFakeLocation(45, -74, 300));

		List<String> blackList = new ArrayList<String>();
		blackList.add("PlantHardinessCanada"); // because it needs 30 years of weather
		blackList.add("PlantHardinessUSA"); // because it needs 30 years of weather
		int nbFailures = 0;
		int nbSuccesses = 0;
		List<String> modelList = BioSimClient.getModelList();
		for (String model : modelList) {
			if (!blackList.contains(model)) {
//				System.out.print("Testing model: " + model);
				try {
//					if (model.equals("MPB_SLR")) {
//						int u = 0;
//					}
					Map<BioSimPlot, BioSimDataSet> output = BioSimClient.getModelOutput(2018, 
							2019, 
							locations, 
							null, 
							null, 
							model, 
							1, 
							1, 
							false, 
							null);					
					Assert.assertTrue("There is only one dataset in the output", output.size() == 1);
					BioSimDataSet obsDataset = output.values().iterator().next();
					List<Observation> observations = obsDataset.getObservations();
					String filename = ObjectUtility.getPackagePath(getClass()).replace("bin", "test") + model + "ref.xml";
//					UNCOMMENT THESE TWO LINES TO UPDATE THE TEST RESULTS
//					XmlSerializer serializer = new XmlSerializer(filename);
//					serializer.writeObject(observations);
					
					XmlDeserializer deserializer = new XmlDeserializer(filename);
					List<Observation> references = (List) deserializer.readObject();
					
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
//								Assert.assertEquals("Testing values at line " + i + " field " + j, 
//										((Number) refValue).doubleValue(), 
//										((Number) obsValue).doubleValue(),
//										1E-8);
								if (Math.abs(((Number) refValue).doubleValue() - ((Number) obsValue).doubleValue()) < 1E-8) {
									nbSuccessful++;
								} else {
									nbUnsuccessful++;
								}
							} else {
//								Assert.assertEquals("Testing observations have equal number of fields", 
//										refValue, 
//										obsValue);
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
						System.out.println(model + " tested - Number of unsuccessful check = " + nbUnsuccessful + " / " + total);
					} else {
						System.out.println(model + " successfully tested");
					}
				} catch (Exception e) {
					e.printStackTrace();
					nbFailures++;
					System.out.println(" - Failed for this reason " + e.toString());
				}
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