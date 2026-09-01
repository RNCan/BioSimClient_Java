/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2024 His Majesty the King in right of Canada
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
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.RCP;

public class BioSimDataSetTest {

	@Test
	public void test01CheckParsingWithScientificNotation() {
		BioSimDataSet dataSet = new BioSimDataSet(Arrays.asList(new String[] {"Field1", "Field2"}));
		dataSet.addObservation(new Object[] {"Value", "-1e-7"});
		dataSet.addObservation(new Object[] {"Value", "1E+7"});
		dataSet.indexFieldType();
		Assert.assertTrue("Checking field type is Double", dataSet.fieldTypes.get(1).getName().equals("java.lang.Double"));
		Assert.assertEquals("Checking first value", -1E-7, (Double) dataSet.getObservations().get(0).values.get(1), 1E-15);
		Assert.assertEquals("Checking second value", 1E7, (Double) dataSet.getObservations().get(1).values.get(1), 1E-8);
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void test02CheckParsingNaN() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> plots = new ArrayList<BioSimPlot>();
		plots.add(new BioSimPlotImpl(59.48759, -134.3845, Double.NaN));
		String model = "Standardised_Precipitation_Evapotranspiration_Index";
		LinkedHashMap<String, Object> results = BioSimClient.generateWeather(2017,
				2021, 
				plots, 
				RCP.RCP45, 
				ClimateModel.GCM4, 
				Arrays.asList(model), 
				null);
		BioSimDataSet ds = (BioSimDataSet) ((LinkedHashMap) results.get(model)).get(plots.get(0)); 
		Class<?> expectedType = ds.fieldTypes.get(ds.getFieldNames().indexOf("SPEI"));
		Assert.assertEquals("Checking SPEI type", expectedType, Double.class);
	}
	
}
