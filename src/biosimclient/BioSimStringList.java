/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2021 Her Majesty the Queen in right of Canada
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

final class BioSimStringList extends ArrayList<String> {

	@Override
	public String toString() {
		if (size() == 0) {
			return "";
		} else if (size() == 1) {
			return get(0);
		} else {
			StringBuilder completeString = new StringBuilder();
			int i = 0;
			for (String s : this) {
				if (i == 0) {
					completeString.append(s);
				} else {
					completeString.append(System.lineSeparator() + s);
				}
				i++;
			}
			return completeString.toString();
		}
		
	}
 	
	
	
}
