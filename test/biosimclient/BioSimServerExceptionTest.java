package biosimclient;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;

public class BioSimServerExceptionTest {

	
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
			BioSimClient.getModelOutput(2000, 2001, locations, RCP.RCP45, ClimateModel.RCM4, "DegreeDay_Annual", null);
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
