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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLHandshakeException;

import com.fasterxml.jackson.databind.ObjectMapper;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Month;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;
import biosimclient.BioSimEnums.Variable;

/**
 * This class enables a client for the Biosim server at repicea.dyndns.org.
 * 
 * @author Mathieu Fortin - October 2019
 */
public final class BioSimClient {

	private static int MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION = -1; // not set yet
	private static int MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS = -1; // not set yet
	private static int MAXIMUM_NB_LOCATIONS_IN_A_SINGLE_REQUEST = 1000; // not set yet
	private static Boolean IS_CLIENT_SUPPORTED = null;
	private static String CLIENT_MESSAGE;
	
	static final String FieldSeparator = ",";
	
	private static final InetSocketAddress REpiceaAddress = new InetSocketAddress("repicea.dynu.net", 80);
	private static final InetSocketAddress LocalAddress = new InetSocketAddress("192.168.0.194", 88);	
	
	private static final String SPACE_IN_REQUEST = "%20";

	static final List<Month> AllMonths = Arrays.asList(Month.values());

	private static final String NORMAL_API = "BioSimNormals";
	private static final String MODEL_LIST_API = "BioSimModelList";
//	private static final String BIOSIMMAXCOORDINATES = "BioSimMaxCoordinatesPerRequest";
	private static final String BIOSIMSTATUS = "BioSimStatus";
	private static final String BIOSIMMODELHELP = "BioSimModelHelp";
	private static final String BIOSIMMODELDEFAULTPARAMETERS = "BioSimModelDefaultParameters";
	private static final String BIOSIMWEATHER = "BioSimWeather";
	
	private static List<String> ReferenceModelList;

	private static double totalServerRequestDuration = 0.0;

	public static boolean isLocal = false;		// set to true to connect on 5000 locally 

	static boolean ForceClimateGenerationEnabled = false;  // default value
	
	static Integer NbNearestNeighbours = null;
	
	
	private static String addQueryIfAny(String urlString, String query) {
		if (query != null && !query.isEmpty()) {
			return urlString.trim() + "?" + query;
		} else {
			return urlString;
		}
	}
	
	
	private static synchronized BioSimStringList getStringFromConnection(String api, String query) throws BioSimClientException, BioSimServerException {
//		long initTime = System.currentTimeMillis();
		InetSocketAddress address = isLocal ? BioSimClient.LocalAddress : BioSimClient.REpiceaAddress;
		String urlString = "http://" + address.getHostName() + ":" + address.getPort() + "/" + api;
		urlString = addQueryIfAny(urlString, query);
		try {
			URL bioSimURL = new URL(urlString);
			long requestInitTime = System.currentTimeMillis();
			HttpURLConnection connection = (HttpURLConnection) bioSimURL.openConnection();
			int code = connection.getResponseCode();
			long requestEndTime = System.currentTimeMillis();
			totalServerRequestDuration += (requestEndTime - requestInitTime) * 0.001;
			
			if (code >= 400 && code < 500) { // client error
				String msg = getCompleteString(connection, true).toString();
				throw new BioSimClientException("Code " + code + ": " + msg);
			}
			if (code >= 500 && code < 600) { // server error
				String msg = getCompleteString(connection, true).toString();
				throw new BioSimServerException("Code " + code + ": " + msg);
			}
			// TODO MF2022-01-18 Handle other codes here
//			System.out.println("Time for server to process request: " + (System.currentTimeMillis() - initTime) + " ms");
			return getCompleteString(connection, false);
		} catch (MalformedURLException e) {
			throw new BioSimClientException("Malformed URL: " + e.getMessage());
		} catch (UnknownHostException e) {
			throw new BioSimClientException("Unknown host: " + e.getMessage());
		} catch (SSLHandshakeException e) {
			throw new BioSimClientException("Unable to confirm certificate for secure connection!");
		} catch (IOException e) {
			throw new BioSimClientException("Unable to connect to the server!");
		} 
	}


	private static BioSimStringList getCompleteString(HttpURLConnection connection, boolean isError) {
//		long initTime = System.currentTimeMillis();
		BioSimStringList stringList = new BioSimStringList();
		try {
			InputStream is;
			if (isError) { 
				is = connection.getErrorStream();
			} else {
				is = connection.getInputStream();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String lineStr;
			while ((lineStr = br.readLine()) != null) {
				stringList.add(lineStr);
			}
			br.close();
//			System.out.println("Time to make the complete string: " + (System.currentTimeMillis() - initTime) + " ms.");
		} catch (IOException e) {
			stringList.add(e.getMessage());
		}
		return stringList;
	}
	
	
	private static LinkedHashMap<BioSimPlot, BioSimDataSet> internalCalculationForNormals(Period period,
			List<BioSimPlot> locations,
			RCP rcp,
			ClimateModel climModel,
			List<Month> averageOverTheseMonths) throws BioSimClientException, BioSimServerException {
		LinkedHashMap<BioSimPlot, BioSimDataSet> outputMap = new LinkedHashMap<BioSimPlot, BioSimDataSet>();

		StringBuilder query = constructCoordinatesQuery(locations);
		query.append("&" + period.parsedQuery);

		if (rcp != null) {
			query.append("&rcp=" + rcp.getURLString());
		}
		
		if(climModel != null) {
			query.append("&climMod=" + climModel.name());
		}
		
		BioSimStringList serverReply = BioSimClient.getStringFromConnection(NORMAL_API, query.toString());
		
		readLines(serverReply, "month", locations, outputMap);

		if (averageOverTheseMonths == null || averageOverTheseMonths.isEmpty()) {
			List<Integer> fieldsToBeRemoved = null;
			for (BioSimDataSet bioSimDataSet : outputMap.values()) {
				if (fieldsToBeRemoved == null) {
					fieldsToBeRemoved = new ArrayList<Integer>();
					for (int i = bioSimDataSet.getFieldNames().size() - 1; i > 0; i--) {	// reverse order
						if (!Variable.getFieldNamesForNormals().contains(bioSimDataSet.getFieldNames().get(i))) {
							fieldsToBeRemoved.add(i);
						}
					}
				}
				for (Integer fieldId : fieldsToBeRemoved) {
					bioSimDataSet.removeField(fieldId);
				}
			}
			return outputMap;
		} else {
			LinkedHashMap<BioSimPlot, BioSimDataSet> formattedOutputMap = new LinkedHashMap<BioSimPlot, BioSimDataSet>();
			for (BioSimPlot location : outputMap.keySet()) {
				BioSimDataSet ds = outputMap.get(location);
				BioSimMonthMap bsmm = new BioSimMonthMap(ds);
				formattedOutputMap.put(location, bsmm.getMeanForTheseMonths(averageOverTheseMonths));
			}
			return formattedOutputMap;
		}
	}

	
	/**
	 * Retrieves the normals and compiles the mean or sum over some months.
	 * @param period a Period enum variable
	 * @param locations a List of BioSimPlot instances
	 * @param rcp an RCP enum variable (if null the server takes the RCP 4.5 by default 
	 * @param climModel a ClimateModel enum variable (if null the server takes the RCM4 climate model
	 * @param averageOverTheseMonths the months over which the mean or sum is to be
	 *                               calculated. If empty or null the method returns
	 *                               the monthly averages.
	 * @return a Map with the BioSimPlot instances as keys and BioSimDataSet instances as values.
	 * @throws BioSimClientException if the client fails or a BioSimServerException if the server fails 
	 */
	public static LinkedHashMap<BioSimPlot, BioSimDataSet> getNormals(
			Period period,
			List<BioSimPlot> locations,
			RCP rcp,
			ClimateModel climModel,
			List<Month> averageOverTheseMonths) throws BioSimClientException, BioSimServerException {
		isClientSupported();
		if (locations.size() > BioSimClient.MAXIMUM_NB_LOCATIONS_IN_A_SINGLE_REQUEST) {
			throw new BioSimClientException("The maximum number of locations for a single request is " + MAXIMUM_NB_LOCATIONS_IN_A_SINGLE_REQUEST);
		}
		if (locations.size() > BioSimClient.getMaximumNbLocationsPerBatchNormals()) {
			LinkedHashMap<BioSimPlot, BioSimDataSet> resultingMap = new LinkedHashMap<BioSimPlot, BioSimDataSet>();
			List<BioSimPlot> copyList = new ArrayList<BioSimPlot>();
			copyList.addAll(locations);
			List<BioSimPlot> subList = new ArrayList<BioSimPlot>();
			while (!copyList.isEmpty()) {
				while (!copyList.isEmpty() && subList.size() < BioSimClient.getMaximumNbLocationsPerBatchNormals()) {
					subList.add(copyList.remove(0));
				}
				resultingMap.putAll(internalCalculationForNormals(period, subList, rcp, climModel, averageOverTheseMonths));
				subList.clear();
			}
			return resultingMap;
		} else {
			return internalCalculationForNormals(period, locations, rcp, climModel, averageOverTheseMonths);
		}
	}
	
	
	/**
	 * Retrieves the monthly normals.
	 * @param period a Period enum variable
	 * @param locations a List of BioSimPlot instances
	 * @param rcp an RCP enum variable (if null the server takes the RCP 4.5 by default 
	 * @param climModel a ClimateModel enum variable (if null the server takes the RCM4 climate model
	 * @return a Map with the BioSimPlot instances as keys and BioSimDataSet instances as values.
	 * @throws BioSimClientException if the client fails or a BioSimServerException if the server fails 
	 */
	public static LinkedHashMap<BioSimPlot, BioSimDataSet> getMonthlyNormals(
			Period period, 
			List<BioSimPlot> locations,
			RCP rcp,
			ClimateModel climModel) throws BioSimClientException, BioSimServerException {
		return getNormals(period, locations, rcp, climModel, null);
	}

	
	/**
	 * Retrieves the yearly normals.
	 * @param period a Period enum variable
	 * @param locations a List of BioSimPlot instances
	 * @param rcp an RCP enum variable (if null the server takes the RCP 4.5 by default 
	 * @param climModel a ClimateModel enum variable (if null the server takes the RCM4 climate model
	 * @return a Map with the BioSimPlot instances as keys and BioSimDataSet instances as values.
	 * @throws BioSimClientException if the client fails or a BioSimServerException if the server fails 
	 */
	public static LinkedHashMap<BioSimPlot, BioSimDataSet> getAnnualNormals(
			Period period, 
			List<BioSimPlot> locations,
			RCP rcp,
			ClimateModel climModel) throws BioSimClientException, BioSimServerException {
		return getNormals(period, locations, rcp, climModel, AllMonths);
	}


	private static StringBuilder constructCoordinatesQuery(List<BioSimPlot> locations) {
		StringBuilder latStr = new StringBuilder();
		StringBuilder longStr = new StringBuilder();
		StringBuilder elevStr = new StringBuilder();
		String latStrThisLoc, longStrThisLoc, elevStrThisLoc;
		for (BioSimPlot location : locations) {
			latStrThisLoc = latStr.length() == 0 ? "" + location.getLatitudeDeg() : SPACE_IN_REQUEST + location.getLatitudeDeg();
			latStr.append(latStrThisLoc);
			longStrThisLoc = longStr.length() == 0 ? "" + location.getLongitudeDeg() : SPACE_IN_REQUEST + location.getLongitudeDeg();
			longStr.append(longStrThisLoc);
			elevStrThisLoc = elevStr.length() == 0 ? "" + processElevationM(location) : SPACE_IN_REQUEST + processElevationM(location);
			elevStr.append(elevStrThisLoc);
		}

		StringBuilder query = new StringBuilder();
		query.append("lat=" + latStr.toString());
		query.append("&long=" + longStr.toString());
		if (elevStr.length() != 0) {
			query.append("&elev=" + elevStr.toString());
		}
		return query;
	}

	private static String processElevationM(BioSimPlot location) {
		if (Double.isNaN(location.getElevationM())) {
			return "NaN";
		} else {
			return "" + location.getElevationM();
		}
	}
	

	/**
	 * Returns the names of the available models. This is a clone of the
	 * true list to avoid any intended changes in the model list.
	 * 
	 * @return a List of String instances
	 */
	public static List<String> getModelList() throws BioSimClientException, BioSimServerException {
		isClientSupported();
		List<String> copy = new ArrayList<String>();
		copy.addAll(getReferenceModelList());
		return copy;
	}
	
	public static String getModelHelp(String modelName) throws BioSimClientException, BioSimServerException {
		isClientSupported();
		if (modelName == null) {
			throw new InvalidParameterException("THe modelName parameter cannot be set to null!");
		}
		String serverReply = getStringFromConnection(BIOSIMMODELHELP, "model=" + modelName).toString();
		return serverReply;
	}

	
	public static BioSimParameterMap getModelDefaultParameters(String modelName) throws BioSimClientException, BioSimServerException {
		isClientSupported();
		if (modelName == null) {
			throw new InvalidParameterException("THe modelName parameter cannot be set to null!");
		}
		String serverReply = getStringFromConnection(BIOSIMMODELDEFAULTPARAMETERS, "model=" + modelName).toString();
		String[] parms = serverReply.split("\\*");
		BioSimParameterMap parmMap = new BioSimParameterMap();
		for (String parm : parms) {
			String[] keyValue = parm.split(":");
			if (keyValue.length > 1) {
				parmMap.addParameter(keyValue[0], keyValue[1]);
			} else {
				parmMap.addParameter(keyValue[0], null); // TODO MF2022-01-27 Possibility of bug here. Check that.
			}
		}
		return parmMap;
	}
	

	private static List<String> getReferenceModelList() throws BioSimClientException, BioSimServerException {
		if (ReferenceModelList == null) {
			List<String> myList = new ArrayList<String>();
			BioSimStringList modelList = BioSimClient.getStringFromConnection(BioSimClient.MODEL_LIST_API, null);
			for (String model : modelList) {
				myList.add(model);
			}
			ReferenceModelList = new ArrayList<String>();
			ReferenceModelList.addAll(myList);
		}
		return ReferenceModelList;
	}
 	
		
	private static void readLines(BioSimStringList serverReply,
			String fieldLineStarter,
			List<BioSimPlot> refListForLocations,
			LinkedHashMap outputMap) throws BioSimClientException, BioSimServerException {
//		long initTime;
//		long totalTime = 0;
		BioSimDataSet dataSet = null;
		int locationId = 0;
		BioSimPlot location = null;
		boolean isDataSetProperlyInitialized = false;
		String modName = null;
		LinkedHashMap<BioSimPlot, BioSimDataSet> resultMap = new LinkedHashMap<BioSimPlot, BioSimDataSet>();
		for (String line : serverReply) {
			if (line.toLowerCase().startsWith("error")) {
				throw new BioSimServerException(line);
			} else if (BioSimClient.getModelList().contains(line.trim())) {
				resultMap = new LinkedHashMap<BioSimPlot, BioSimDataSet>();
				modName = line.trim();
				outputMap.put(modName, resultMap);
				locationId = 0;
				isDataSetProperlyInitialized = false;		// reset to false until we get the header
			} else if (line.toLowerCase().startsWith(fieldLineStarter)) { // means it is a new location
				if (dataSet != null) {	// must be indexed before instantiating a new DataSet
//					initTime = System.currentTimeMillis();
					dataSet.indexFieldType();
//					totalTime += System.currentTimeMillis() - initTime;
				}
				location = refListForLocations.get(locationId);
				String[] fields = line.split(FieldSeparator);
				List<String> fieldNames = Arrays.asList(fields);
				dataSet = new BioSimDataSet(fieldNames);
				resultMap.put(location, dataSet);
				locationId++;
				isDataSetProperlyInitialized = true;
			} else {
				if (!isDataSetProperlyInitialized) {
					if (modName != null) {
						outputMap.put(modName, new BioSimClientException(line)); // this happens if we are using the Weather route
					} else {
						throw new BioSimClientException(serverReply.toString());	// this happens with normals or in case of severe exception with the weather route
					}
				} else {
					Object[] fields = Arrays.asList(line.split(FieldSeparator)).toArray(new Object[]{});
//					initTime = System.currentTimeMillis();
					dataSet.addObservation(fields);
//					totalTime += System.currentTimeMillis() - initTime;
				}
			}
		}
		if (dataSet != null) {
//			initTime = System.currentTimeMillis();
			dataSet.indexFieldType();	// last DataSet has not been instantiated so it needs to be here.
//			totalTime += System.currentTimeMillis() - initTime;
		}
		if (outputMap.isEmpty()) {
			outputMap.putAll(resultMap);
		}
//		System.out.println("Time to create observations: " + totalTime + " ms");
	}
	
	private static LinkedHashMap<String, Object> internalCalculationForClimateVariables(int fromYr, 
			int toYr, 
			List<BioSimPlot> locations,
			RCP rcp,
			ClimateModel climMod,
			List<String> modelNames, 
			int rep,
			int repModel,
			List<BioSimParameterMap> additionalParms) throws BioSimClientException, BioSimServerException {
		StringBuilder query = constructCoordinatesQuery(locations);
		query.append("&from=" + fromYr);
		query.append("&to=" + toYr);
		if (rcp != null) {
			query.append("&rcp=" + rcp.getURLString());
		}
		if(climMod != null) {
			query.append("&climMod=" + climMod.name());
		}
		if (ForceClimateGenerationEnabled) {
			System.out.println("Warning: past climate is generated instead of being compiled from observations!");
			query.append("&source=FromNormals");
		}
		if (NbNearestNeighbours != null) {
			query.append("&nb_nearest_neighbor=" + NbNearestNeighbours.toString());
		}
		if (rep > 1) {
			query.append("&rep=" + rep);
		}
		for (int i = 0; i < modelNames.size(); i++) {
			if (i == 0)
				query.append("&model=" + modelNames.get(i));
			else 
				query.append(BioSimClient.SPACE_IN_REQUEST + modelNames.get(i));
		}
		if (repModel >  1) {
			query.append("&repmodel=" + repModel);
		}
		if (additionalParms != null) {
			StringBuilder sbParms = new StringBuilder();
			int i = 0;
			for (BioSimParameterMap oMap : additionalParms) {
				String strForThisMap = oMap == null || oMap.isEmpty() ? "null" : oMap.toString();
				if (sbParms.length() == 0)
					sbParms.append(strForThisMap);
				else 
					sbParms.append(SPACE_IN_REQUEST + strForThisMap);
			}
			query.append("&Parameters=" + sbParms.toString());
		}
//		System.out.println("Constructing request: " + (System.currentTimeMillis() - initTime) + " ms");
		BioSimStringList serverReply = getStringFromConnection(BIOSIMWEATHER, query.toString());
		LinkedHashMap<String, Object> outputMap = new LinkedHashMap<String, Object>();
//		long initTime = System.currentTimeMillis();
		readLines(serverReply, "rep", locations, outputMap);
//		System.out.println("Total time to convert string into biosim dataset: " + (System.currentTimeMillis() - initTime) + " ms.");
		return outputMap;
	}

	/**
	 * Returns a model output for a particular time interval. 
	 * 
	 * The "modelnames" argument sets the models to be applied on
	 * the generated meteorological time series. It should be among the strings returned by the 
	 * getModelList static method. Generating the climate is time consuming. The 
	 * generated climate is stored on the server and it can be re used with some 
	 * other models. 
	 * 
	 * @param fromYr starting date (yr) of the period (inclusive)
	 * @param toYr ending date (yr) of the period (inclusive)
	 * @param locations the locations of the plots (BioSimPlot instances)
	 * @param modelNames a list of strings representing the model names
	 * @param rcp an RCP enum variable (by default RCP 4.5)
	 * @param climMod a ClimateModel enum variable (by default RCM 4)
	 * @param rep the number of replicates in climate generation if needed. Should be equal to or greater than 1. 
	 * @param additionalParms a list of BioSimParameterMap instances that contains the eventual additional parameters for the models
	 * @return a LinkedHashMap of BioSimPlot instances (keys) and climate variables (values)
	 * @throws BioSimClientException if the client fails or BioSimServerException if the server fails
	 */
	public static LinkedHashMap<String, Object> generateWeather(int fromYr, 
			int toYr,
			List<BioSimPlot> locations, 
			RCP rcp,
			ClimateModel climMod,
			List<String> modelNames,
			int rep,
			List<BioSimParameterMap> additionalParms)	throws BioSimClientException, BioSimServerException {
		return BioSimClient.generateWeather(fromYr, toYr, locations, rcp, climMod, modelNames, rep, 1, additionalParms); 
	}

	
	/**
	 * Returns a model output for a particular time interval. 
	 * 
	 * The "modelnames" parameter sets the models to be applied on
	 * the generated climate. It should be one of the strings returned by the 
	 * getModelList static method. Generating the climate is time consuming. The 
	 * generated climate is stored on the server and it can be re used with some 
	 * other models. The number of replicate is set to 1.
	 * 
	 * @param fromYr starting date (yr) of the period (inclusive)
	 * @param toYr ending date (yr) of the period (inclusive)
	 * @param locations the locations of the plots (BioSimPlot instances)
	 * @param modelNames a list of strings representing the model names
	 * @param rcp an RCP enum variable (by default RCP 4.5)
	 * @param climMod a ClimateModel enum variable (by default RCM 4)
	 * @param additionalParms a list of BioSimParameterMap instances that contains the eventual additional parameters for the models
	 * @return a LinkedHashMap of BioSimPlot instances (keys) and climate variables (values)
	 * @throws BioSimClientException if the client fails or BioSimServerException if the server fails
	 */
	public static LinkedHashMap<String, Object> generateWeather(int fromYr, 
			int toYr,
			List<BioSimPlot> locations, 
			RCP rcp,
			ClimateModel climMod,
			List<String> modelNames,
			List<BioSimParameterMap> additionalParms)
			throws BioSimClientException, BioSimServerException {
		return BioSimClient.generateWeather(fromYr, toYr, locations, rcp, climMod, modelNames, 1, 1, additionalParms);
	}

	
	

	/**
	 * Returns a model output for a particular period. In this method, the ephemeral 
	 * mode can be disabled by setting the argument isEphemeral to false. In such 
	 * a case, the generated climate is cached in memory. This involves
	 * two request to the server. The first aims at generating the climate, whereas
	 * the second applies a model on the generated climate in order to obtain the 
	 * desired variables. The ephemeral model should be preferred when a single model
	 * is to be applied to some locations. If several models are to be applied to the
	 * some locations, then the ephemeral mode should be disabled. The climate is then
	 * generated only once for all the models. This implies several calls to this method
	 * with exactly the same signature except for the argument "modelName". This "modelName"
	 * argument sets the model to be applied on the generated climate. It should be one 
	 * of the strings returned by the getModelList static method. 
	 * 
	 * @param fromYr starting date (yr) of the period (inclusive)
	 * @param toYr ending date (yr) of the period (inclusive)
	 * @param locations the locations of the plots (BioSimPlot instances)
	 * @param rcp an RCP enum variable (by default RCP 4.5)
	 * @param climMod a ClimateModel enum variable (by default RCM 4)
	 * @param modelNames a list of strings representing the model names
	 * @param rep the number of replicates in climate generation if needed. Should be equal to or greater than 1. 
	 * @param repModel the number of replicates in the model if needed. Should be equal to or greater than 1. 
	 * @param additionalParms a list of BioSimParameterMap instances that contains the eventual additional parameters for the models
	 * @return a LinkedHashMap of BioSimPlot instances (keys) and climate variables (values)
	 * @throws BioSimClientException if the client fails or BioSimServerException if the server fails
	 */
	public static LinkedHashMap<String, Object> generateWeather(int fromYr, 
			int toYr,
			List<BioSimPlot> locations, 
			RCP rcp,
			ClimateModel climMod,
			List<String> modelNames,
			int rep,
			int repModel,
			List<BioSimParameterMap> additionalParms) throws BioSimClientException, BioSimServerException {
		isClientSupported();
		if (rep < 1 || repModel < 1) {
			throw new InvalidParameterException("The rep and repModel parameters should be equal to or greater than 1!");
		} 
		if (locations.size() > MAXIMUM_NB_LOCATIONS_IN_A_SINGLE_REQUEST) {
			throw new BioSimClientException("The maximum number of locations for a single request is " + MAXIMUM_NB_LOCATIONS_IN_A_SINGLE_REQUEST);
		}

		totalServerRequestDuration = 0.0;

		if (locations.size() > BioSimClient.getMaximumNbLocationsPerBatchWeatherGeneration()) {
			LinkedHashMap<String, Object> resultingMap = null;
			List<BioSimPlot> copyList = new ArrayList<BioSimPlot>();
			copyList.addAll(locations);
			List<BioSimPlot> subList = new ArrayList<BioSimPlot>();
			while (!copyList.isEmpty()) {
				while (!copyList.isEmpty() && subList.size() < BioSimClient.getMaximumNbLocationsPerBatchWeatherGeneration()) {
					subList.add(copyList.remove(0));
				}
				LinkedHashMap<String, Object> intermediateMap = internalCalculationForClimateVariables(fromYr, toYr, subList, rcp, climMod, modelNames, rep, repModel, additionalParms);
				if (resultingMap == null) 
					resultingMap = intermediateMap;
				else {
					Set<String> keys = resultingMap.keySet();
					for (String key : keys) {
						Object there = resultingMap.get(key);
						Object incomingResult = intermediateMap.get(key);
						if (incomingResult instanceof LinkedHashMap && there instanceof LinkedHashMap) {
							((LinkedHashMap) there).putAll((LinkedHashMap) incomingResult);
						} else if (incomingResult instanceof Exception) {
							resultingMap.put(key, incomingResult);
						}
					}
				}
				subList.clear();
			}
			return resultingMap;
		} else {
			return internalCalculationForClimateVariables(fromYr, toYr, locations, rcp, climMod, modelNames, rep, repModel, additionalParms);
		}
	}
	
	
	private static int getMaximumNbLocationsPerBatchWeatherGeneration() throws BioSimClientException, BioSimServerException {
		setMaxCapacities();
		return MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION;
	}

	private static void setMaxCapacities() throws BioSimClientException, BioSimServerException {
		if (MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION == -1 || MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS == -1) {
			String query = "crev=" + BioSimClientAppVersion.getInstance().getBuild();
			String serverReply = getStringFromConnection(BIOSIMSTATUS, query).toString();	// is returned in JSON format
			ObjectMapper mp = new ObjectMapper();
			Map statusMap = null;
			try {
				statusMap = mp.readValue(serverReply, Map.class);
			} catch (Exception e) {
				throw new BioSimClientException("Something wrong happened while retrieving the server status: " + e.getMessage());
			}
			
			if (!(Boolean)statusMap.get("IsInitCompleted")) {
				throw new BioSimClientException("The server initialization is not completed!");
			}
			if (!statusMap.containsKey("settings")) {
				throw new BioSimClientException("The status map does not contain the entry settings!");
			}
			
			
			try {
				Map settingsMap = (Map) statusMap.get("settings");
				MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS = (Integer) settingsMap.get("NbMaxCoordinatesNormals");
				MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION = (Integer) settingsMap.get("NbMaxCoordinatesWG");
//				IS_CLIENT_SUPPORTED = settingsMap.containsKey("IsClientSupported") ? (Boolean) settingsMap.get("IsClientSupported") : false;
//				CLIENT_MESSAGE = settingsMap.containsKey("ClientMessage") ? (String) settingsMap.get("ClientMessage") : "This client is not supported";
//				String[] maxCapacities = serverReply.split(FieldSeparator);
//				MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION = Integer.parseInt(maxCapacities[0]);
//				MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS = Integer.parseInt(maxCapacities[1]);
			} catch (NumberFormatException e) {
				throw new BioSimClientException("The server reply could not be parsed: " + e.getMessage());
			}
		}
	}
	
	/**
	 * Check if the status of the server has been retrieved and set the IS_CLIENT_SUPPORTED and CLIENT_MESSAGE static
	 * members. Then check if the client is supported. 
	 * @throws BioSimClientException
	 */
	private static synchronized void isClientSupported() throws BioSimClientException, BioSimServerException {
		if (IS_CLIENT_SUPPORTED == null) {
			String query = "crev=" + BioSimClientAppVersion.getInstance().getBuild();
			String serverReply = getStringFromConnection(BIOSIMSTATUS, query).toString();	// is returned in JSON format
			ObjectMapper mp = new ObjectMapper();
			Map statusMap = null;
			try {
				statusMap = mp.readValue(serverReply, Map.class);
			} catch (Exception e) {
				throw new BioSimClientException("Something wrong happened while retrieving the server status: " + e.getMessage());
			}
			
			if (!(Boolean)statusMap.get("IsInitCompleted")) {
				throw new BioSimClientException("The server initialization is not completed!");
			}
			if (!statusMap.containsKey("settings")) {
				throw new BioSimClientException("The status map does not contain the entry settings!");
			}
			
			try {
				Map settingsMap = (Map) statusMap.get("settings");
				MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS = (Integer) settingsMap.get("NbMaxCoordinatesNormals");
				MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION = (Integer) settingsMap.get("NbMaxCoordinatesWG");
				IS_CLIENT_SUPPORTED = settingsMap.containsKey("IsClientSupported") ? (Boolean) settingsMap.get("IsClientSupported") : true;
				CLIENT_MESSAGE = settingsMap.containsKey("ClientMessage") ? (String) settingsMap.get("ClientMessage") : "";
//				String[] maxCapacities = serverReply.split(FieldSeparator);
//				MAXIMUM_NB_LOCATIONS_PER_BATCH_WEATHER_GENERATION = Integer.parseInt(maxCapacities[0]);
//				MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS = Integer.parseInt(maxCapacities[1]);
			} catch (NumberFormatException e) {
				throw new BioSimClientException("The server reply could not be parsed: " + e.getMessage());
			}
			if (IS_CLIENT_SUPPORTED && !CLIENT_MESSAGE.isEmpty()) {
				System.out.println("WARNING: " + CLIENT_MESSAGE);
			}
		}
		if (!IS_CLIENT_SUPPORTED) 
			throw new BioSimClientException(CLIENT_MESSAGE);
	}
	
	private static int getMaximumNbLocationsPerBatchNormals() throws BioSimClientException, BioSimServerException {
		setMaxCapacities();
		return MAXIMUM_NB_LOCATIONS_PER_BATCH_NORMALS;
	}

	/**
	 * Reset the configuration to its initial values.
	 */
	public static void resetClientConfiguration() {
		NbNearestNeighbours = null;
		ForceClimateGenerationEnabled = false;
	}
	
	
	/**
	 * By default the climate generation retrieves the observations for the
	 * dates prior to the current date. If this option is set to true, then 
	 * the climate is generated from the normals even for dates prior to
	 * the current date.
	 * 
	 * @param bool a boolean
	 */
	public static void setForceClimateGenerationEnabled(boolean bool) {
		ForceClimateGenerationEnabled = bool;
	}

	/**
	 * This option forces the client to generate weather for past dates instead
	 * of using the observations. By default, it is disabled
	 * @return a boolean
	 */
	public static boolean isForceClimateGenerationEnabled() {
		return BioSimClient.ForceClimateGenerationEnabled;
	}

	/**
	 * This option set the number of stations in the imputation of the climate variables
	 * @param nbNearestNeighbours an integer between 1 and 35. The default is 4 stations.
	 */
	public static void setNbNearestNeighbours(int nbNearestNeighbours) {
		if (nbNearestNeighbours < 1 || nbNearestNeighbours > 35) {
			throw new InvalidParameterException("The number of nearest neighbours must be an integer between 1 and 35!");
		}
		NbNearestNeighbours = nbNearestNeighbours;
	}

	/**
	 * Returns the number of climate station used in the imputation of the climate variables.
	 * @return an integer
	 */
	public static int getNbNearestNeighbours() {
		if (NbNearestNeighbours == null) {
			return 4; // default value
		} else {
			return NbNearestNeighbours;
		}
	}

	public static double getLastServerRequestDuration() {
		return totalServerRequestDuration;
	}
	
}
