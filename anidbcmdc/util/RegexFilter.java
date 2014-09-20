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

import java.io.FileFilter;
import java.io.File;

/**
 * <p>
 * Title: WildcardFilter
 * </p>
 * <p>
 * Description: Checks if a filename is matched by a String. Needed by
 * File.listFiles(FileFilter)
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class RegexFilter implements FileFilter {

	private String s;

	private boolean regex;

	/**
	 * Not needed here, so private.
	 */
	private RegexFilter() {
	}

	/**
	 * Set the String the filename should match and activate regular expressions
	 * if needed.
	 * 
	 * @param s -
	 *            The String that should match the filename.
	 * @param regex -
	 *            true, regular expressions are enabled.
	 */
	public RegexFilter(String s, boolean regex) {
		this.s = s;
		this.regex = regex;
	}

	/*
	 * (non-Javadoc) Checks if a filename is matched by a String. Regular
	 * expressions can be enabled if needed. Wildcards are always enabled.
	 * 
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File f) {
		String r = f.getName();
		if (regex) {			
			return r.matches(s);
		}
		return r.equals(s);
	}
}
