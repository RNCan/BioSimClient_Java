package biosimclient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimClientTest.FakeLocation;


public class BioSimInternalModelTest {

	
	@Test
	public void testingIfReturnDataSetHasAtLeastOneObservation() throws NoSuchMethodException, SecurityException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 1; i++) {
			FakeLocation loc = new FakeLocation(45 + BioSimClientTest.RANDOM.nextDouble() * 7,
					-74 + BioSimClientTest.RANDOM.nextDouble() * 8,
					300 + BioSimClientTest.RANDOM.nextDouble() * 400);
			locations.add(loc);
		}
		
		int nbFailures = 0;
		int nbSuccesses = 0;
		List<String> modelList = BioSimClient.getModelList();
		for (String model : modelList) {
//			if (!model.equals("ForestTentCaterpillar") // can cause an Exception on the server side
//					&& !model.equals("HWA_Phenology") // can cause an Exception on the server side
//					&& !model.equals("Insect_Development_Database_II")  // can cause an Exception on the server side
//					&& !model.equals("Insect_Development_Database_III")  // can cause an Exception on the server side
//					&& !model.equals("PlantHardiness")	// bad number of parameters 
//					&& !model.equals("Climdex_Annual")  // the base period is not inside the simulation period
//					&& !model.equals("Climdex_Monthly") // the base period is not inside the simulation period
//					&& !model.equals("Spruce_Budworm_Dispersal") // encoding cause an exception from Python to C++ 
//					) { 
				System.out.print("Testing model: " + model);
				try {
//					if (model.equals("Spruce_Budworm_Dispersal")) {
//						int u = 0;
//					}
					Map<BioSimPlot, BioSimDataSet> output = BioSimClient.getClimateVariables(2015, 2019, locations, model);					
					for (BioSimDataSet ds : output.values()) {
						Assert.assertTrue("Testing if DataSet instance has at least one observation", ds.getNumberOfObservations() > 0);
						System.out.println(" - Ok ");
						nbSuccesses++;
					}
				} catch (Exception e) {
					e.printStackTrace();
					nbFailures++;
					System.out.println(" - Failed for this reason " + e.toString());
				}
//			}
		}
		System.out.println("Nb of failures = " + nbFailures + "; Nb of successes = " + nbSuccesses);
		Assert.assertTrue("No exception thrown", nbFailures == 0);
	}
	
}
