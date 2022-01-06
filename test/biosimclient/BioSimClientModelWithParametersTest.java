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
import org.junit.Ignore;
import org.junit.Test;


public class BioSimClientModelWithParametersTest {

	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5C() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		locations.add(BioSimClientNormalsTest.getPlots().get(0));
		int initialDateYr = 2000;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2001, locations, null, null, "DegreeDay_Annual", parms);
		
		for (BioSimPlot plot : teleIORefs.keySet()) {
			BioSimDataSet firstDataSet = teleIORefs.get(plot);
			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
			System.out.println("There is at least one observation");

			int dateFieldIndex = firstDataSet.getFieldNames().indexOf("Year");
			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
				int dateYr = (Integer) firstDataSet.getValueAt(i, dateFieldIndex);
				double actualDD = (Double) firstDataSet.getValueAt(i, ddFieldIndex);
				double expectedDD;
				if (dateYr == 2000) {
					expectedDD = 1575d;
				} else {
					expectedDD = 1847.15;
				}
				Assert.assertEquals("Testing degree-days above 5C",	expectedDD,	actualDD, 1E-8);	// with new setup failed at 1586.45 MF2022-01-06
			}
			System.out.println("Degree-days above 5C successfully tested!");
		}
		
	}


	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Test
	public void testingWithDegreeDaysAbove5CLong() throws BioSimClientException, BioSimServerException {
		int initialDateYr = 1980;
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2020, BioSimClientNormalsTest.getPlots(), null, null, "DegreeDay_Daily", parms);
	}
	
	
	
}
