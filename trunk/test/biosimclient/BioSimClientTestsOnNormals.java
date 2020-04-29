package biosimclient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import biosimclient.BioSimEnums.Period;
import biosimclient.BioSimEnums.Variable;

public class BioSimClientTestsOnNormals {

	
	private static List<BioSimPlot> Plots;
	
	
	private static List<BioSimPlot> getPlots() {
		if (Plots == null) {
			Plots = new ArrayList<BioSimPlot>();
			Plots.add(new BioSimPlotImpl(46.87,-71.25,114));
			Plots.add(new BioSimPlotImpl(46.03,-73.12,15));
		}
		return Plots;
	}
	
	
	@Test
	public void getNormalsFor1981_2010() throws IOException {
		List<Variable> variables = new ArrayList<Variable>();
		variables.add(Variable.TX);
		variables.add(Variable.TN);
		variables.add(Variable.P);
		
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals1981_2010, variables, getPlots());
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tx", 9.331780821917809, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tn", -0.13835616438356102, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1319.0, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tx", 11.37041095890411, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tn", 1.6958904109589048, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1033.4, (Double) record[2], 1E-1);
	}

	@Test
	public void getNormalsFor2051_2080() throws IOException {
		List<Variable> variables = new ArrayList<Variable>();
		variables.add(Variable.TX);
		variables.add(Variable.TN);
		variables.add(Variable.P);
		
		Map<BioSimPlot, BioSimDataSet> resultingMap = BioSimClient.getAnnualNormals(Period.FromNormals2051_2080, variables, getPlots());
		BioSimDataSet firstPlotDataSet = resultingMap.get(getPlots().get(0));
		Object[] record = firstPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tx", 12.424657534246576, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tn", 3.647123287671233, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1328.5, (Double) record[2], 1E-1);
		
		BioSimDataSet secondPlotDataSet = resultingMap.get(getPlots().get(1));
		record = secondPlotDataSet.getObservations().get(0).toArray();
		Assert.assertEquals("Testing Tx", 13.975616438356166, (Double) record[0], 1E-8);
		Assert.assertEquals("Testing Tn", 4.53972602739726, (Double) record[1], 1E-8);
		Assert.assertEquals("Testing P", 1161.0, (Double) record[2], 1E-1);
	}
	
}
