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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class BioSimClientPerformanceTest {

	final String testRunFilename;
	final String testResultsFilename;

	BioSimClientPerformanceTest() {

		String tmpDirectory = System.getProperty("java.io.tmpdir");
		try {
			tmpDirectory = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String path = tmpDirectory + File.separator + ".." + File.separator + ".." + File.separator + ".." + File.separator + "testData";
		this.testRunFilename = path + File.separator + "performanceTestRun" + ".txt";
		this.testResultsFilename = path + File.separator + "performanceTestResults" + ".txt";

		BioSimClient.isLocal = true;
	}

	void generateTestRun(int numberOfTests, int initialDateYr, int finalDateYr, int maxNumberOfLocations, BioSimPlot locationMin, BioSimPlot locationMax) {
		File file = new File(testRunFilename);

		try {
			FileOutputStream fos = new FileOutputStream(file);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			ArrayList<String> modelList = new ArrayList<String>();
			modelList.add("DegreeDay_Annual");
			modelList.add("Climatic_Annual");

			Random rn = new Random();

			// creating the output data structure is not needed, but it was added because of json / xml serialization
			// plans, but that was abandoned for a CSV simple file because of dependency requirements.
			// It is kept only for debugging purposes.
			ArrayList<HashMap<String, Object>> testRun = new ArrayList<HashMap<String, Object>>();

			for (int i = 0; i < numberOfTests; i++) {
				HashMap<String, Object> entry = new HashMap<String, Object>();

				int testID = 0;
				entry.put("testID", testID);
				bw.write(String.valueOf(testID) + ";");

				int randomInitialDateYr = rn.nextInt(finalDateYr - initialDateYr) + initialDateYr;
				entry.put("randomInitialDateYr", randomInitialDateYr);
				bw.write(String.valueOf(randomInitialDateYr) + ";");

				int randomfinalDateYr = rn.nextInt(finalDateYr - randomInitialDateYr) + randomInitialDateYr;
				entry.put("randomfinalDateYr", randomfinalDateYr);
				bw.write(String.valueOf(randomfinalDateYr) + ";");

				int modelIndex = rn.nextInt(modelList.size());
				entry.put("modelName", modelList.get(modelIndex));
				bw.write(modelList.get(modelIndex) + ";");

				int numberOfLocations = rn.nextInt(maxNumberOfLocations - 1) + 1;
				bw.write(String.valueOf(numberOfLocations) + ";");

				ArrayList<Object> locations = new ArrayList<Object>();

				for (int j = 0; j < numberOfLocations; j++) {
					ArrayList<Double> location = new ArrayList<Double>();

					double latitude = rn.nextDouble() * (locationMax.getLatitudeDeg() - locationMin.getLatitudeDeg()) + locationMin.getLatitudeDeg();
					location.add(latitude);
					double longitude = rn.nextDouble() * (locationMax.getLongitudeDeg() - locationMin.getLongitudeDeg()) + locationMin.getLongitudeDeg();
					location.add(longitude);
					double elevation = rn.nextDouble() * (locationMax.getElevationM() - locationMin.getElevationM()) + locationMin.getElevationM();
					elevation = elevation > 750.0 ? Double.NaN : elevation;
					location.add(elevation);

					locations.add(location);
					bw.write(String.valueOf(latitude) + ";" + String.valueOf(longitude) + ";" + String.valueOf(elevation) + ";");
				}

				bw.newLine();

				entry.put("locations", locations);
			}

			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void executeTestRun(int numberOfTests, int numberOfWarmupTests) throws IOException, BioSimClientException, BioSimServerException {
		File file = new File(testRunFilename);
		File fileResults = new File(testResultsFilename);
		FileOutputStream fos = new FileOutputStream(fileResults);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int testNumber = 0;

		long msStart = 0;

		while ((line = br.readLine()) != null && (numberOfTests <= 0 || testNumber < numberOfTests)) {
			// process the line.
			if (testNumber == numberOfWarmupTests)
				msStart = System.currentTimeMillis();

			String[] lineSplit = line.split(";");

			int testID = Integer.parseInt(lineSplit[0]);
			int randomInitialDateYr = Integer.parseInt(lineSplit[1]);
			int randomfinalDateYr = Integer.parseInt(lineSplit[2]);
			String modelName = lineSplit[3];
			int numberOfLocations = Integer.parseInt(lineSplit[4]);

			ArrayList<BioSimPlot> locations = new ArrayList<BioSimPlot>();

			for (int i = 0; i < numberOfLocations; i++) {
				double latitude = Double.parseDouble(lineSplit[5 + i * 3]);
				double longitude = Double.parseDouble(lineSplit[6 + i * 3]);
				double elevation = Double.parseDouble(lineSplit[7 + i * 3]);

				locations.add(new BioSimPlotImpl(latitude, longitude, elevation));
			}

			// the line data is complete.  Launch test.
			if (testID == 0) {
				System.out.println("Performing test # " + Integer.toString(testNumber + 1) + " using model " + modelName + " with " + Integer.toString(locations.size()) + " locations");
				double elapsed[] = BioSimModelEphemeral(modelName, randomInitialDateYr, randomfinalDateYr, locations);
				bw.write(Double.toString(elapsed[0]) + ";" + Double.toString(elapsed[1]));
				bw.newLine();
			}
			else {
				throw new BioSimClientException("Unsupported test request ID encountered in " + testRunFilename);
			}

			testNumber++;
		}

		if (msStart != 0) {
			long msEnd = System.currentTimeMillis();
			double totalTimeAfterWarmup = (msEnd - msStart) * 0.001;
			bw.write(Double.toString(totalTimeAfterWarmup));
			System.out.println("Total test duration after warmup : " + Double.toString(totalTimeAfterWarmup));
		}
		else
		{
			System.out.println("Could not measure total test time because warmup was not complete by end of tests");
		}

		bw.close();
	}

	double[] BioSimModelEphemeral(String modelName, int initialDateYr, int finalDateYr, List<BioSimPlot> locations) throws BioSimClientException, BioSimServerException {
		BioSimParameterMap parms = new BioSimParameterMap();
		//parms.addParameter("LowerThreshold", 5);

		long initial = System.currentTimeMillis();
		LinkedHashMap<BioSimPlot, BioSimDataSet> outputMap = BioSimClient.generateWeather(initialDateYr,
				finalDateYr,
				locations,
				null,
				null,
				Arrays.asList(new String[] {modelName}),
				Arrays.asList(new BioSimParameterMap[] {parms})).get(modelName);
		double elapsedTime = (System.currentTimeMillis() - initial) * .001;
		double serverTime = BioSimClient.getLastServerRequestDuration();
		System.out.println("Elapsed time = " + elapsedTime + " , server time = " + serverTime);
		return new double[]{elapsedTime, serverTime};
	}

	//This tool offers two modes : test run production mode, and test run execution mode.
	//launch the tool with argv[0] = true to enable test run production mode.  default mode is test run execution mode.
	//test run production mode creates a test run file that is to be added to the repo to always test with the same random requests.
	//test run execution mode reads the test run file, executes the requests using the client and write the request times to a text file.
	public static void main(String[] args) throws Exception {

		boolean generateTestRun = false;
		if (args.length > 0) {
			generateTestRun = Boolean.parseBoolean(args[0]);
		}

		BioSimClientPerformanceTest bioSim = new BioSimClientPerformanceTest();

		if (generateTestRun) {
			bioSim.generateTestRun(110, 1950, 2050, 100, new BioSimPlotImpl(46.0, -74.0, 300.0), new BioSimPlotImpl(51.0, -65.0, 800.0));
		}
		else
		{
			bioSim.executeTestRun(0, 10);
		}
	}
}
