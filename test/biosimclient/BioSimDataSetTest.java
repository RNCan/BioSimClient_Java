/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2024 His Majesty the King in right of Canada
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

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class BioSimDataSetTest {

	@Test
	public void checkParsingWithScientificNotation() {
		BioSimDataSet dataSet = new BioSimDataSet(Arrays.asList(new String[] {"Field1", "Field2"}));
		dataSet.addObservation(new Object[] {"Value", "-1e-7"});
		dataSet.addObservation(new Object[] {"Value", "1E+7"});
		dataSet.indexFieldType();
		Assert.assertTrue("Checking field type is Double", dataSet.fieldTypes.get(1).getName().equals("java.lang.Double"));
	}
}
