package biosimclient;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.RCP;

public class BioSimClientTestsWithReplicates {

	@Test
	public void test2085to2090_2rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2085,2090,2);
	}

	@Test
	public void test2085to2090_10rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2085,2090,10);
	}

	@Test
	public void test2065to2090_2rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2065,2090,2);
	}

	@Test
	public void test2015to2030_3rep() throws BioSimClientException, BioSimServerException {
		testingFutureDegreeDaysWithRCP85andClimateModels(2015,2030,3);
	}

	
	private static void testingFutureDegreeDaysWithRCP85andClimateModels(int initialDateYr, int finalDateYr, int nbReplicates) throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientTestsOnNormals.getPlots();
		
		int expectedObservationsPerPlot = ((finalDateYr - initialDateYr) + 1) * nbReplicates;
		LinkedHashMap<BioSimPlot, BioSimDataSet> oRCP85_RCM4def = BioSimClient.getClimateVariables(initialDateYr, finalDateYr, locations, RCP.RCP85, null, "DegreeDay_Annual", nbReplicates, true);
		
		for (BioSimPlot plot : oRCP85_RCM4def.keySet()) {
			BioSimDataSet firstDataSet = oRCP85_RCM4def.get(plot);
			Assert.assertTrue("The number of observations", firstDataSet.getNumberOfObservations() ==  expectedObservationsPerPlot);
		}
	}
	
}
