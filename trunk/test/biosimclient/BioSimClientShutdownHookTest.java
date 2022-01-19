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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

@Deprecated
public class BioSimClientShutdownHookTest {

	
	private static void testingWithDegreeDaysAbove5C(int initialDateYr, int finalDateYr) throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 5; i++) {
			BioSimFakeLocation loc = new BioSimFakeLocation(45 + i * .1,
					-74 + i * .1,
					300 + 5 * i);
			locations.add(loc);
		}
 
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		long initial = System.currentTimeMillis();
		String modelName = "DegreeDay_Annual";
		@SuppressWarnings("unused")
		Map<BioSimPlot, BioSimDataSet> outputMap = BioSimClient.getModelOutput(initialDateYr, 
				finalDateYr, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{modelName}), 
				1,
				1,
				Arrays.asList(new BioSimParameterMap[]{parms})).get(modelName);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time = " + elapsedTime);
	}


	@Ignore
	@Test
	public void testClearCacheAndShutdownHook() throws Exception {
		BioSimClientShutdownHookTest.testingWithDegreeDaysAbove5C(2000, 2005);
		BioSimClient.clearCache();
		Map copy = BioSimClient.GeneratedClimateMap;
		System.out.println("Size of GeneratedClimateMap after clearing = " + copy.size());
		Assert.assertEquals("Testing if the cache has been cleared", copy.size(), 0);
	}
	
	
}
