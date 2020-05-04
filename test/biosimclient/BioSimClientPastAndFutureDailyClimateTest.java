package biosimclient;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimClient.ClimateModel;
import biosimclient.BioSimClient.RCP;


public class BioSimClientPastAndFutureDailyClimateTest {

	/*
	 * Tests if the weather generation over several contexts. It uses the memorization. First run should be longer
	 * than the others.
	 */
	@Test
	public void testingWithDailyOverlappingPastAndFuture() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2000;
		
//		long startTime = System.currentTimeMillis();
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getClimateVariables(initialDateYr, 2040, locations, null, null, "DegreeDay_Annual", true);
//		double et1 = (System.currentTimeMillis() - startTime) *.001;
//		startTime = System.currentTimeMillis();
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getClimateVariables(initialDateYr, 2040, locations, null, null, "DegreeDay_Annual", true);
//		double et2 = (System.currentTimeMillis() - startTime) *.001;
//		double timeRatio = et1/et2;
		
//		Assert.assertTrue("Testing that it was ephemeral", Math.abs(timeRatio - 1) < .2);
//		System.out.println("Ephemeral enabled test passed");
		
		for (BioSimPlot plot : teleIORefs.keySet()) {
			BioSimDataSet firstDataSet = teleIORefs.get(plot);
			BioSimDataSet secondDataSet = teleIORefs2.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				Object[] obs1 = firstDataSet.getObservations().get(i).toArray();
				Object[] obs2 = secondDataSet.getObservations().get(i).toArray();
				double d1 = (Double) obs1[1];
				double d2 = (Double) obs2[1];
				int dateYr = (Integer) obs1[0];
				if (dateYr >= initialDateYr && dateYr < 2019) {		// From observation
					Assert.assertEquals("Testing if the degree-days are the same before 2019", 
							d1,	d2, 1E-8);
				} else {											// generated from normals
					Assert.assertTrue("Testing that the degree-days are different for 2019 and after",
							Math.abs(d1 - d2) > 1E-8);
				}
			}
			System.out.println("Degree-days before 2019 are the same and those after vary.");
		}
		
	}

	
	/*
	 * Tests if the weather generation over several contexts. It uses the memorization. First run should be longer
	 * than the others.
	 */
	@Test
	public void testingFutureDegreeDaysWithDefaultValuesOfRCPsandClimateModels() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2090;
		int finalDateYr = 2091;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45def_RCM4def = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, null, null, "DegreeDay_Annual", true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45_RCM4def = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, RCP.RCP45, null, "DegreeDay_Annual", true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP45def_RCM4 = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, null, ClimateModel.RCM4, "DegreeDay_Annual", true);
		
		for (BioSimPlot plot : oRCP45def_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP45def_RCM4def.get(plot);
			BioSimDataSet secondDataSet = oRCP45_RCM4def.get(plot);
			BioSimDataSet thirdDataSet = oRCP45def_RCM4.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations between first and second dataset", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			Assert.assertEquals("Testing the number of observations between second and third dataset", 
					secondDataSet.getNumberOfObservations(), 
					thirdDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				Object[] obs1 = firstDataSet.getObservations().get(i).toArray();
				Object[] obs2 = secondDataSet.getObservations().get(i).toArray();
				Object[] obs3 = thirdDataSet.getObservations().get(i).toArray();
				double d1 = ((Number) obs1[1]).doubleValue();
				double d2 = ((Number) obs2[1]).doubleValue();
				double d3 = ((Number) obs3[1]).doubleValue();
				Assert.assertEquals("Testing if the degree-days are equal between first and second datasets", d1, d2, 320);
				Assert.assertEquals("Testing if the degree-days are equal between second and third datasets", d2, d3, 320);
			}
			System.out.println("Degree-days tested for default values.");
		}
		
	}

	
	
	/*
	 * Tests if the weather generation over several contexts. It uses the memorization. First run should be longer
	 * than the others.
	 */
	@Test
	public void testingFutureDegreeDaysWithRCP85andClimateModels() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		int initialDateYr = 2090;
		int finalDateYr = 2091;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, RCP.RCP85, null, "DegreeDay_Annual", true);
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4 = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, RCP.RCP85, ClimateModel.RCM4, "DegreeDay_Annual", true);
		
		for (BioSimPlot plot : oRCP85_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP85_RCM4def.get(plot);
			BioSimDataSet secondDataSet = oRCP85_RCM4.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");
			Assert.assertEquals("Testing the number of observations between first and second dataset", 
					firstDataSet.getNumberOfObservations(), 
					secondDataSet.getNumberOfObservations());
			System.out.println("Same number of observations in each dataset");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				Object[] obs1 = firstDataSet.getObservations().get(i).toArray();
				Object[] obs2 = secondDataSet.getObservations().get(i).toArray();
				double d1 = ((Number) obs1[1]).doubleValue();
				double d2 = ((Number) obs2[1]).doubleValue();
				Assert.assertEquals("Testing if the degree-days are equal between first and second datasets", d1, d2, 400);
			}
			System.out.println("Degree-days tested for default values.");
		}
		
	}

	
	
	
}
