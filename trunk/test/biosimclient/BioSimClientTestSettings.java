package biosimclient;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class BioSimClientTestSettings {

	static boolean Validation = true;

	static String ProjectRootPath;
	static {
		try {
			File file = new File(BioSimClientTestSettings.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
			while (!file.getAbsolutePath().endsWith("biosimclient")) {
				file = file.getParentFile();
			}
			ProjectRootPath = file.getAbsolutePath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	static boolean areTheseInnerMapsEqual(LinkedHashMap<BioSimPlot, BioSimDataSet> map1, LinkedHashMap<BioSimPlot, BioSimDataSet> map2) {
		if (map1.size() == map2.size()) {
			List<BioSimPlot> plotList1 = new ArrayList<BioSimPlot>();
			plotList1.addAll(map1.keySet());
			List<BioSimPlot> plotList2 = new ArrayList<BioSimPlot>();
			plotList2.addAll(map2.keySet());
			for (int i = 0; i < plotList1.size(); i++) {
				if (!((BioSimPlotImpl) plotList1.get(i)).areEqual((BioSimPlotImpl) plotList2.get(i))) {
					return false;
				} else if (!map1.get(plotList1.get(i)).areEqual(map2.get(plotList2.get(i)))) {
						return false;
				}
			}
			return true;
		}
		return false;
	}
	
	static boolean areTheseOuterMapsEqual(LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> map1, LinkedHashMap<String, LinkedHashMap<BioSimPlot, BioSimDataSet>> map2) {
		if (map1.size() == map2.size()) {
			List<String> plotList1 = new ArrayList<String>();
			plotList1.addAll(map1.keySet());
			List<String> plotList2 = new ArrayList<String>();
			plotList2.addAll(map2.keySet());
			if (plotList1.equals(plotList2)) {
				for (int i = 0; i < plotList1.size(); i++) {
					if (!areTheseInnerMapsEqual(map1.get(plotList1.get(i)), map2.get(plotList2.get(i)))) {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}

}
