/******************************************************************************
 *
 * Sugar for Java 1.1.0
 * Copyright (C) 2001-2003  Dipl.-Inf. (FH) Johann Nepomuk Loefflmann, 
 * All Rights Reserved, http://www.jonelo.de
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author jonelo@jonelo.de
 *
 * 01-May-2002: initial release
 *
 * 06-Jul-2002: bug fixed (replaceString and replaceAllString do not work if 
 *              oldString starts at pos 0)
 *
 * 09-Mar-2003: bug fixed (endless loop in replaceAllStrings, if oldString is
 *              part of newString), testcases: 
 *              replaceAllStrings("aaa","a","abc") => abcabcabc 
 *              replaceAllStrings("abbbabbbabbbb","bb","") => ababa
 *              replaceAllStrings("aaa","","b") => bababa
 *              new method: removeAllStrings()
 *
 *****************************************************************************/
package jonelo.sugar.util;

import java.text.MessageFormat;

public class GeneralString {

	/** Creates new GeneralString */
	public GeneralString() {
	}

	/**
	 * Replaces none or only one String oldString to newString in String source
	 */
	public static String replaceString(String source, String oldString,
			String newString) {
		int pos = source.indexOf(oldString);
		if (pos > -1) {
			StringBuffer sb = new StringBuffer();
			sb.append(source.substring(0, pos));
			sb.append(newString);
			sb.append(source.substring(pos + oldString.length()));

			return sb.toString();
		} else
			return source;
	}

	/**
	 * Replaces all oldStrings found within source by newString
	 */
	public static String replaceAllStrings(String source, String oldString,
			String newString) {
		StringBuffer buffer = new StringBuffer(source);
		int idx = source.length();
		int offset = oldString.length();

		while ((idx = buffer.toString().lastIndexOf(oldString, idx - 1)) > -1) {
			buffer.replace(idx, idx + offset, newString);
		}
		return buffer.toString();
	}

	public static String removeAllStrings(String source, String oldString) {
		return replaceAllStrings(source, oldString, "");
	}

	/**
	 * Overwrites a string s at a given position with newString
	 */
	public static String replaceString(String s, int pos, String newString) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < newString.length(); i++) {
			sb.setCharAt(pos + i, newString.charAt(i));
		}
		return sb.toString();
	}

	/**
	 * @since 1.0.1
	 */
	public static String translateEscapeSequences(String s) {
		String temp = s;
		temp = replaceAllStrings(temp, "\\t", "\t"); // \t
		temp = replaceAllStrings(temp, "\\n", "\n"); // \n
		temp = replaceAllStrings(temp, "\\r", "\r"); // \r
		temp = replaceAllStrings(temp, "\\\"", "\""); // \"
		temp = replaceAllStrings(temp, "\\\'", "\'"); // \'
		temp = replaceAllStrings(temp, "\\\\", "\\"); // \\
		return temp;
	}

	/*
	 * Removes all chars c in String s
	 */
	public static String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c)
				r += s.charAt(i);
		}
		return r;
	}

	/*
	 * remove one char at a given position
	 */
	public static String removeChar(String s, int pos) {
		StringBuffer buf = new StringBuffer(s.length() - 1);
		buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
		return buf.toString();
	}

	/**
	 * replaces all characters oldC in a String s with character newC
	 */
	public static String replaceChar(String s, char oldC, char newC) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == oldC)
				sb.setCharAt(i, newC);
		}
		return sb.toString();
	}

	/*
	 * replace one char c in String s at a given position pos
	 */
	public static String replaceChar(String s, int pos, char c) {
		StringBuffer sb = new StringBuffer(s);
		sb.setCharAt(pos, c);
		return sb.toString();
	}

	public static int countChar(String s, char c) {
		int count = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c)
				count++;
		}
		return count;
	}

	public static String message(String s, char c) {
		Character character = new Character(c);
		Object aobj[] = { character.toString() };
		String s1 = MessageFormat.format(s, aobj);
		return s1;
	}

	public static String message(String s, int i) {
		Integer integer = new Integer(i);
		Object aobj[] = { integer.toString() };
		String s1 = MessageFormat.format(s, aobj);
		return s1;
	}

	public static String message(String s, int i1, int i2) {
		Integer integer = new Integer(i1);
		Integer integer2 = new Integer(i2);
		Object aobj[] = { integer.toString(), integer2.toString() };
		String s1 = MessageFormat.format(s, aobj);
		return s1;
	}

	public static String message(String s, String s1) {
		Object aobj[] = { s1 };
		String s2 = MessageFormat.format(s, aobj);
		return s2;
	}

}
