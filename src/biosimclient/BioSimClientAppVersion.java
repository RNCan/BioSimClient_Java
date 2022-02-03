/*
 * This file is part of the biosimclient library
 *
 * Author Mathieu Fortin - Canadian Forest Service
 * Copyright (C) 2020-2022 Her Majesty the Queen in right of Canada
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
import java.net.URL;
import java.security.InvalidParameterException;


public final class BioSimClientAppVersion {

	private static final String JAR_FILE_PREFIX = "jar:file:";

	private static BioSimClientAppVersion SINGLETON;

	private final String version;
	private final String revision;
	
	protected BioSimClientAppVersion() {
		String filePath = getJarFileImInIfAny(getClass());
		if (filePath != null) {
			try {
				String filename = getRelativePackagePath(getClass()) + "revision";
				InputStream in = getClass().getResourceAsStream("/" + filename);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				revision = br.readLine().split("=")[1];
				version = br.readLine().split("=")[1];
			} catch (IOException e) {
				throw new InvalidParameterException("Cannot retrieve manifest from jar file: " + filePath);
			}
		} else {
			version = "Unknown";
			revision = "Unknown";
		}
	}

	public static BioSimClientAppVersion getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new BioSimClientAppVersion();
		}
		return SINGLETON;
	}
	
	/**
	 * This method returns the revision number, i.e. the label "revision" + the build number.
	 * @return a String
	 */
	public final String getRevision() {return "Revision " + revision.trim();}

	/**
	 * This method returns the build. It is just the number without any other string.
	 * @return the build number as a string.
	 */
	public final String getBuild() {return revision.trim();}

	/**
	 * Return the version number. Typically, 1.1.819. The last number represents the revision.
	 * @return
	 */
	public final String getVersion() {return version;}
	
	
	public String getJarFileImInIfAny(Class<?> clazz) {
		String className = clazz.getSimpleName();
		URL resourceURL = clazz.getResource(className + ".class");
		String resourcePath = resourceURL.toString();
		if (resourcePath.startsWith(JAR_FILE_PREFIX)) {
			int indexMark = resourcePath.indexOf("!");
			return resourcePath.substring(JAR_FILE_PREFIX.length(), indexMark);	// we remove the jar: prefix here
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public String getRelativePackagePath(Class anyClass) {
		return anyClass.getPackage().getName().replace(".", "/") + "/";
	}


}

