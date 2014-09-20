/******************************************************************************
 *
 * Jacksum version 1.5.0 - checksum utility in Java
 * Copyright (C) 2001-2004  Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
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
 * E-mail: jonelo@jonelo.de
 *
 *****************************************************************************/

package jonelo.jacksum.algorithm;

import java.text.*;
import java.io.*;
import jonelo.sugar.util.*;

public class Service {

	private final static char[] HEX = "0123456789abcdef".toCharArray();

	public static String right(long number, int blanks) {
		StringBuffer sb = new StringBuffer(number + "");
		while (sb.length() < blanks)
			sb.insert(0, ' ');
		return sb.toString();
	}

	public static String format(long number, String mask) {
		DecimalFormat df = new DecimalFormat(mask);
		return df.format(number);
	}

	public static String hexformat(long value, int nibbles) {
		StringBuffer sb = new StringBuffer(Long.toHexString(value));
		while (sb.length() < nibbles)
			sb.insert(0, '0');
		return sb.toString();
	}

	public static String format(byte[] bytes) {
		return format(bytes, false);
	}

	public static String format(byte[] bytes, boolean uppercase) {
		if (bytes == null)
			return "";
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		int b;
		for (int i = 0; i < bytes.length; i++) {
			b = bytes[i] & 0xFF;
			sb.append(HEX[b >>> 4]);
			sb.append(HEX[b & 0x0F]);
		}
		return (uppercase ? sb.toString().toUpperCase() : sb.toString());
	}

	public static boolean isSymbolicLink(File file) {
		// there are no symbolic links on Windows
		// on Windows a link is always a file
		if (System.getProperty("os.name").toLowerCase().startsWith("windows"))
			return false;

		try {
			String cnnpath = file.getCanonicalPath();
			String abspath = file.getAbsolutePath();
			abspath = GeneralString.replaceString(abspath, "/./", "/");
			if (abspath.endsWith("/."))
				return false;
			return !abspath.equals(cnnpath);
		} catch (IOException ex) {
			System.err.println(ex);
			return true;
		}
	}

}
