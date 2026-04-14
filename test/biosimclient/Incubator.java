package biosimclient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.RCP;

public class Incubator {

	
	
	private static LinkedHashMap<String, Object> getClimateVariables(int startYr, int endYr, int nbRealizations) throws Exception {
		BioSimPlot p = new BioSimPlotImpl(48.3,-90.2, Double.NaN);
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(p);
		List<String> models = Arrays.asList(new String[] {"Climatic_Monthly",
						"Climatic_Annual", 
						"Climate_Mosture_Index_Annual", 
						"VaporPressureDeficit_Monthly", 
						"Soil_Moisture_Index_Annual", 
						"DegreeDay_Annual"});
		List<BioSimParameterMap> parms = new ArrayList<BioSimParameterMap>();
		parms.add(null);
		parms.add(null);
		parms.add(null);
		parms.add(null);
		parms.add(null);
		BioSimParameterMap parm = new BioSimParameterMap();
		parm.addParameter("LowerThreshold", 5);
		parms.add(parm);
		return BioSimClient.generateWeather(startYr, 
				endYr, 
				plots, 
				RCP.RCP45, 
				ClimateModel.RCM4,
				models,
				nbRealizations,
				parms);
	}
	
	@Ignore
	@Test
	public void test01() throws Exception {
		int nbRep = 100;
		long initTime = System.currentTimeMillis();
		getClimateVariables(2031,2035,nbRep);
		System.out.println("Time to process whole request = " + (System.currentTimeMillis() - initTime) + " ms.");
		System.out.println("Time to process on server side = " + BioSimClient.getLastServerRequestDuration() * 1000 + " ms.");
		
	}
	
	

}
