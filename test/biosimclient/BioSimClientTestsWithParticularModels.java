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

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;


public class BioSimClientTestsWithParticularModels {

	/*
	 * Tests if the weather generation over past and future time intervals.
	 */
	@Ignore
	@Test
	public void testingWithMPB_SLR() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = BioSimClientNormalsTest.getPlots();
		int initialDateYr = 1998;
		
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getModelOutput(initialDateYr, 2020, locations, null, null, "MPB_SLR", null);
		
		// TODO set this test properly. To be checked with Remi
		
		int u = 0;
//		for (BioSimPlot plot : teleIORefs.keySet()) {
//			BioSimDataSet firstDataSet = teleIORefs.get(plot);
//			BioSimDataSet secondDataSet = teleIORefs2.get(plot);
//			Assert.assertTrue("Is there at least one observation", firstDataSet.getNumberOfObservations() > 0);
//			System.out.println("There is at least one observation");
//			Assert.assertEquals("Testing the number of observations", 
//					firstDataSet.getNumberOfObservations(), 
//					secondDataSet.getNumberOfObservations());
//			System.out.println("Same number of observations in each dataset");
//
//			int dateFieldIndex = firstDataSet.getFieldNames().indexOf("Year");
//			int ddFieldIndex = firstDataSet.getFieldNames().indexOf("DD");
//			int dataTypeIndex = firstDataSet.getFieldNames().indexOf("DataType");
//			for (int i = 0; i < firstDataSet.getNumberOfObservations(); i++) {
//				double d1 = (Double) firstDataSet.getValueAt(i, ddFieldIndex);
//				double d2 = (Double) secondDataSet.getValueAt(i, ddFieldIndex);
//				String dataType1 = (String) firstDataSet.getValueAt(i, dataTypeIndex);
//				String dataType2 = (String) secondDataSet.getValueAt(i, dataTypeIndex);
//				@SuppressWarnings("unused")
//				int dateYr = (Integer) firstDataSet.getValueAt(i, dateFieldIndex);
//				if (dataType1.equals("Real_Data")) {
//					Assert.assertTrue("Testing if was taken from observation", dataType2.equals("Real_Data"));
//					Assert.assertEquals("Testing if the degree-days are the same before 2020", 
//							d1,	d2, 1E-8);
//				} else if (dataType1.equals("Simulated")) {
//					Assert.assertTrue("Testing if was simulated", dataType2.equals("Simulated"));
//					Assert.assertTrue("Testing that the degree-days are different for 2020 and after",
//							Math.abs(d1 - d2) > 1E-8);
//				}
//			}
//			System.out.println("Degree-days before 2020 are the same and those after vary.");
//		}
		
	}

	
}
