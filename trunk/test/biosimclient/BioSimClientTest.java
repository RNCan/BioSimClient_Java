/*
 * This file is part of the biosimclient library
 *
 * Copyright (C) 2019-20 Mathieu Fortin - Canadian Wood Fibre Centre
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
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;


public class BioSimClientTest {

	protected final static Random RANDOM = new Random();
	
	
	private static int nbObjectsBefore;
	
	static {
		try {
			nbObjectsBefore = BioSimClient.getNbWgoutObjectsOnServer();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	static class FakeLocation implements BioSimPlot {

		private final double elevationM;
		private final double latitude;
		private final double longitude;

		FakeLocation(double latitudeDeg, double longitudeDeg, double elevationM) {
			this.latitude = latitudeDeg;
			this.longitude = longitudeDeg;
			this.elevationM = elevationM;
		}



		@Override
		public double getElevationM() {return elevationM;}

		@Override
		public double getLatitudeDeg() {return latitude;}

		@Override
		public double getLongitudeDeg() {return longitude;}


		@Override
		public String toString() {return latitude + "_" + longitude + "_" + elevationM;}

	}
	
	
	/*
	 * Tests if the wgout id is kept in memory for further use instead of generating the wgouts over and
	 * over again.
	 */
	@Test
	public void testingMemorizer() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 100; i++) {
			FakeLocation loc = new FakeLocation(45 + RANDOM.nextDouble() * 7,
					-74 + RANDOM.nextDouble() * 8,
					300 + RANDOM.nextDouble() * 400);
			locations.add(loc);
		}
		
		long initialTime;
		double nbSecs1, nbSecs2;

		initialTime = System.currentTimeMillis();
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getClimateVariables(2018, 2019, locations, "DegreeDay_Annual");
		nbSecs1 = (System.currentTimeMillis() - initialTime) * .001;
		System.out.println("Elapsed time = " + nbSecs1 + " size = " + teleIORefs.size());

		for (int i = 0; i < 10; i++) {
			initialTime = System.currentTimeMillis();
			LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getClimateVariables(2018, 2019, locations, "DegreeDay_Annual");
			nbSecs2 = (System.currentTimeMillis() - initialTime) * .001;
			
			Assert.assertTrue(nbSecs1 > (nbSecs2 * 5));
			
			for (BioSimPlot location : locations) {
				BioSimDataSet expectedMap = teleIORefs.get(location);
				BioSimDataSet actualMap = teleIORefs2.get(location);
				Assert.assertTrue(expectedMap.getNumberOfObservations() > 0);
				Assert.assertEquals("Testing map size", 
						expectedMap.getNumberOfObservations(), 
						actualMap.getNumberOfObservations());
				for (int j = 0; j < expectedMap.getNumberOfObservations(); j++) {
					Observation expected = expectedMap.getObservations().get(j);
					Observation actual = actualMap.getObservations().get(j);
					Assert.assertTrue("Testing if observations are equal",  
							expected.isEqualToThisObservation(actual));
				}
			}
		}
	}

	
	/*
	 * Tests if the weather generation over several contexts. It uses the memorization. First run should be longer
	 * than the others.
	 */
	@Test
	public void testingWeatherGenerationOverSeveralContexts() throws BioSimClientException, BioSimServerException {
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 10; i++) {
			FakeLocation loc = new FakeLocation(45 + RANDOM.nextDouble() * 7,
					-74 + RANDOM.nextDouble() * 8,
					300 + RANDOM.nextDouble() * 400);
			locations.add(loc);
		}
		
		long initialTime;
		double nbSecs1, nbSecs2;

		initialTime = System.currentTimeMillis();
		LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs = BioSimClient.getClimateVariables(2000, 2019, locations, "DegreeDay_Annual");
		nbSecs1 = (System.currentTimeMillis() - initialTime) * .001;
		System.out.println("Elapsed time = " + nbSecs1 + " size = " + teleIORefs.size());

		for (int i = 0; i < 10; i++) {
			initialTime = System.currentTimeMillis();
			LinkedHashMap<BioSimPlot, BioSimDataSet> teleIORefs2 = BioSimClient.getClimateVariables(2000, 2019, locations, "DegreeDay_Annual");
			nbSecs2 = (System.currentTimeMillis() - initialTime) * .001;
			
			Assert.assertTrue(nbSecs1 > (nbSecs2 * 5));	// testing that the computational time is five times faster when the memorization is used.
			
			for (BioSimPlot location : locations) {
				BioSimDataSet expectedMap = teleIORefs.get(location);
				BioSimDataSet actualMap = teleIORefs2.get(location);
				Assert.assertTrue(expectedMap.getNumberOfObservations() > 0);
				Assert.assertEquals("Testing map size", 
						expectedMap.getNumberOfObservations(),
						actualMap.getNumberOfObservations());
				for (int j = 0; j < expectedMap.getNumberOfObservations(); j++) {
					Observation expected = expectedMap.getObservations().get(j);
					Observation actual = actualMap.getObservations().get(j);
					Assert.assertTrue("Testing observations",  
								expected.isEqualToThisObservation(actual));
					
				}
			}
		}
	}
	
	@Test
	public void testingMemoryManagementOnServerAfterEphemeralOptionSetToTrue() throws Exception {
		for (int nbRuns = 0; nbRuns < 5; nbRuns++) {
			int nbObjectsBefore = BioSimClient.getNbWgoutObjectsOnServer();
			System.out.println("Nb objects before this function call = " + nbObjectsBefore);
			List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
			for (int i = 0; i < 5; i++) {
				FakeLocation loc = new FakeLocation(45 + RANDOM.nextDouble() * 7,
						-74 + RANDOM.nextDouble() * 8,
						300 + RANDOM.nextDouble() * 400);
				locations.add(loc);
			}
			
			BioSimClient.getClimateVariables(2018, 2019, locations, "DegreeDay_Annual", true);	// is ephemeral: wgout instances are not stored on the server
			
			int nbObjectsAfter = BioSimClient.getNbWgoutObjectsOnServer();
			Assert.assertEquals("Testing if the number of objects before and after is consistent", nbObjectsBefore, nbObjectsAfter);
		}
	}

	@Test
	public void testingMemoryManagementOnServerThroughEventualShutdownHook() throws Exception {
		System.out.println("Nb objects before starting test on shutdown hook = " + nbObjectsBefore);
		List<BioSimPlot> locations = new ArrayList<BioSimPlot>();
		for (int i = 0; i < 10; i++) {
			FakeLocation loc = new FakeLocation(45 + RANDOM.nextDouble() * 7,
					-74 + RANDOM.nextDouble() * 8,
					300 + RANDOM.nextDouble() * 400);
			locations.add(loc);
		}
		
		BioSimClient.getClimateVariables(2018, 2019, locations, "DegreeDay_Annual");
		
		System.out.println("Nb objects immediately before eventual shutdown hook = " + BioSimClient.getNbWgoutObjectsOnServer());
		System.out.println("Calling eventual shutdown hook...");
		BioSimClient.removeWgoutObjectsFromServer(BioSimClient.GeneratedClimateMap.values());
		int nbObjectsAfter = BioSimClient.getNbWgoutObjectsOnServer();
		System.out.println("Nb objects after testing eventual shutdown hook = " + nbObjectsAfter);
		Assert.assertEquals("Testing if the number of objects before and after is consistent", nbObjectsBefore, nbObjectsAfter);
	}

}
