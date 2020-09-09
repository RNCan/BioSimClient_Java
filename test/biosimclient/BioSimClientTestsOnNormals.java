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
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.RCP;

public class BioSimClientTestsOnNormals {

	
	private static List<BioSimPlot> Plots;
	
	
	static List<BioSimPlot> getPlots() {
		if (Plots == null) {
			Plots = new ArrayList<BioSimPlot>();
			Plots.add(new BioSimPlotImpl(46.87,-71.25,114));
			Plots.add(new BioSimPlotImpl(46.03,-73.12,15));
		}
		return Plots;
	}
	
	
	@Test
	public void getNormalsFor1981_2010() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals1981_2010, 
				getPlots(), 
				null, 
				null);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", -0.13835616438356102, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 9.331780821917809, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1319.0, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 1.6958904109589048, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 11.37041095890411, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1033.4, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP45() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				ClimateModel.Hadley);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 4.97972602739726, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 13.793424657534247, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1342.6, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 6.007671232876713, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 15.470958904109589, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1166.9, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_Hadley_RCP85() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080,
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.Hadley);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 6.555342465753424, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 15.288219178082194, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1340.7, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 7.374246575342466, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 16.840273972602738, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1135.1, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP45() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				null, 
				null);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 3.6131506849315067, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 12.596712328767124, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1467.3, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 5.4043835616438365, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 14.761917808219177, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1115.0, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_RCM4_RCP85() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				null);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 4.977808219178082, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 13.67780821917808, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1566.5, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 6.734520547945206, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 15.849315068493151, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1215.0, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP45() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP45, 
				ClimateModel.GCM4);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 3.4284931506849325, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 12.595068493150684, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1534.9, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 5.2715068493150685, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 14.583287671232876, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1224.4, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080_GCM4_RCP85() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, 
				getPlots(), 
				RCP.RCP85, 
				ClimateModel.GCM4);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 5.252602739726027, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 14.431232876712327, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1486.8, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tn", 6.871780821917809, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tx", 16.36986301369863, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1178.7, (Double) record[2], 1E-1);
	}
	
	
	@Test
	public void getMonthlyNormalsFor1971_2000() throws BioSimClientException, BioSimServerException {
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getMonthlyNormals(Period.FromNormals1971_2000, 
				getPlots(), 
				null, 
				null);
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing observation size", 4, record.length);
		Assert.assertEquals("Testing Tn", -16.9, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing Tx", -7.6, (Double) record[2], 1E-8);
		Assert.assertEquals("Testing P", 100.4, (Double) record[3], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing observation size", 4, record.length);
		Assert.assertEquals("Testing Tn", -16.2, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing Tx", -6.1, (Double) record[2], 1E-8);
		Assert.assertEquals("Testing P", 76.6, (Double) record[3], 1E-1);
	}
	
}
