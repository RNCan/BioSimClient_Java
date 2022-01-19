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


public class BioSimClientMultithreadingClimateGenerationForRegenerationIris2020 {

	
	BioSimClientMultithreadingClimateGenerationForRegenerationIris2020() {}
	
	/*
	 * With 3 processes, 50 locations from 2040 to 2050 with 100 replicates: 165 sec.
	 * With 5 processes in WG and 5 in models, 50 locations from 2040 to 2050 with 100 replicates: 125 sec.
	 * With 5 processes in WG and 5 in models, 50 locations from 2040 to 2050 with 100 replicates - max of 10 locations per batch : 65 sec.
	 * With 3 processes in WG and 3 in models, 50 locations from 2040 to 2050 with 100 replicates - max of 10 locations per batch : 113 sec.
	 */
	public void testing(int initialDateYr, int finalDateYr) throws Exception {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 50; i++) {
			BioSimFakeLocation loc = new BioSimFakeLocation(45 + i * .1,
					-74 + i * .1,
					300 + 5 * i);
			locations.add(loc);
		}
 
		BioSimParameterMap parms = new BioSimParameterMap();
		parms.addParameter("LowerThreshold", 5);
		long initial = System.currentTimeMillis();
		BioSimClient.getModelOutput(initialDateYr, 
				finalDateYr, 
				locations, 
				null, 
				null, 
				Arrays.asList(new String[]{"DegreeDay_Annual"}),
				100, // nb rep 
				Arrays.asList(new BioSimParameterMap[] {parms}));
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		System.out.println("Elapsed time = " + elapsedTime);
		
	}


	public static void main(String[] args) throws Exception {
		int initialDateYr = 2040;
		int finalDateYr = 2050;
		BioSimClientMultithreadingClimateGenerationForRegenerationIris2020 bioSim = new BioSimClientMultithreadingClimateGenerationForRegenerationIris2020();
		bioSim.testing(initialDateYr, finalDateYr);
		bioSim.testing(initialDateYr, finalDateYr);
	}
	
	
}
