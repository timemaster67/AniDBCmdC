/******************************************************************************
 *
 * AniDBCmdC version 1.0 - AniDB client in Java
 * Copyright (C) 2005 ExElNeT
 * All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * E-mail: exelnet@web.de
 *
 *******************************************************************************/

package anidbcmdc.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * <p>
 * Title: WebsiteParser
 * </p>
 * <p>
 * Description: Downloads a website and searches for data surrounded by two
 * Strings. This class is just a container for static methods.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class WebsiteParser {

	/**
	 * Not needed here. This class ist just a static method container, so
	 * private.
	 */
	private WebsiteParser() {
	}

	/**
	 * Downloads the source of a website into a String.
	 * 
	 * @param url -
	 *            The url to the websiste.
	 * @param -
	 *            upToLine to line, if -1 to the end of the site
	 * @return - The source of the website.
	 * @throws IOException -
	 *             If the connection to the website failed.
	 */
	public static String websiteToString(String url, int upToLine)
			throws IOException {
		BufferedReader in;
		String website = "";
		URL u = new URL(url);
		URLConnection uc = u.openConnection();
		in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		String line = "";
		int i = 0;
		while ((line = in.readLine()) != null
				&& (upToLine == -1 || i < upToLine + 1)) {
			website += line;
			i++;
		}
		return website;
	}

	/**
	 * Searches for data in a website surrounded by two Strings.
	 * 
	 * @param websiteSource -
	 *            The source of the website.
	 * @param start -
	 *            The String in front of the data.
	 * @param end -
	 *            THe String right after the data.
	 * @return - The data between the start and end Strings.
	 */
	public static String findDataInWebsite(String websiteSource, String start,
			String end) {
		int startIndex = websiteSource.indexOf(start);
		if (startIndex > -1) {
			startIndex += start.length();
			int endIndex = websiteSource.indexOf(end, startIndex);
			if (endIndex > -1) {
				String result = websiteSource.substring(startIndex, endIndex);
				return result;
			}
		}
		return null;
	}
}
