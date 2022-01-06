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
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


public class BioSimClientModelExtendedTest {

	/*
	 * Testing ClimaticQc_Annual model
	 */
	@Test
	public void testingWithClimaticQc_Annual() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "ClimaticQc_Annual", null);
		BioSimDataSet ds = teleIORefs.values().iterator().next();
		Observation obs = ds.getObservations().get(0);
		int index = ds.getFieldNames().indexOf("DegreeDay");
		if (obs.values.get(index) instanceof Integer) {
			Assert.assertEquals("Testing DegreeDay", 1575, (Integer) obs.values.get(index), 1E-8);
		} else {
			Assert.assertEquals("Testing DegreeDay", 1575, (Double) obs.values.get(index), 1E-8);		// with new setup failed at 1586.45 MF2022-01-06
		}

		index = ds.getFieldNames().indexOf("TMean");
		Assert.assertEquals("Testing TMean", 4.1776, (Double) obs.values.get(index), 1E-8);
		
		index = ds.getFieldNames().indexOf("GrowingSeasonTmean");
		Assert.assertEquals("Testing GrowingSeasonTmean", 13.9292, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("JulyTmean");
		Assert.assertEquals("Testing JulyTmean", 18.1387, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("SnowfallProportion");
		Assert.assertEquals("Testing SnowfallProportion", 30.7134, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("TotalSnowfall");
		Assert.assertEquals("Testing TotalSnowfall", 363.8, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("TotalRadiation");
		Assert.assertEquals("Testing TotalRadiation", 4654.31, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("GrowingSeasonRadiation");
		Assert.assertEquals("Testing GrowingSeasonRadiation", 2820.74, (Double) obs.values.get(index), 1E-8);
	}


	
	
	
	
	/*
	 * Testing Climatic_Annual model
	 */
	@Test
	public void testingWithClimatic_Annual() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "Climatic_Annual", null);
		BioSimDataSet ds = teleIORefs.values().iterator().next();
		Observation obs = ds.getObservations().get(0);
		int index = ds.getFieldNames().indexOf("TotalRadiation");
		Assert.assertEquals("Testing TotalRadiation", 4654.3, (Double) obs.values.get(index), 1E-8);	// with new setup failed at 4621.3 MF2022-01-06
		
		index = ds.getFieldNames().indexOf("MeanRelH");
		Assert.assertEquals("Testing MeanRelH", 73.4, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("MeanTdew");
		Assert.assertEquals("Testing MeanTdew", 0.1, (Double) obs.values.get(index), 1E-8);
		
		index = ds.getFieldNames().indexOf("MeanTair");
		Assert.assertEquals("Testing MeanTair", 4.2, (Double) obs.values.get(index), 1E-8);
	}

	/*
	 * Testing Climatic_Monthly model
	 */
	@Test
	public void testingWithClimatic_Monthly() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2000, locations, null, null, "Climatic_Monthly", null);
		BioSimDataSet ds = teleIORefs.values().iterator().next();
		Observation obs = ds.getObservations().get(0);
		int index = ds.getFieldNames().indexOf("TotalRadiation");
		Assert.assertEquals("Testing TotalRadiation", 219.2, (Double) obs.values.get(index), 1E-8);		// with new setup failed at 216.2 MF20220506
		
		index = ds.getFieldNames().indexOf("MeanRelH");
		Assert.assertEquals("Testing MeanRelH", 73.9, (Double) obs.values.get(index), 1E-8);

		index = ds.getFieldNames().indexOf("MeanTdew");
		Assert.assertEquals("Testing MeanTdew", -15.8, (Double) obs.values.get(index), 1E-8);
		
		index = ds.getFieldNames().indexOf("MeanTair");
		Assert.assertEquals("Testing MeanTair", -12.7, (Double) obs.values.get(index), 1E-8);
	}
	
	
	
}
