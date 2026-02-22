/*
 * This file is part of the biosimclient library
 *
 * Copyright (C) 2026 His Majesty the King in right of Canada
 * Author Mathieu Fortin - Canadian Forest Service
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

import java.time.Year;

import org.junit.Assert;
import org.junit.Test;


public class BioSimClientSettingTest {

	@Test
	public void test01GetLatestDailyDateYr() throws BioSimClientException, BioSimServerException {
		int lastDailyDateYr = BioSimClient.getLastDailyDateYr();
		int currentYear = Year.now().getValue();
		Assert.assertEquals("Testing if last daily date is equal to current date minus 1", currentYear, lastDailyDateYr);
	}
}
