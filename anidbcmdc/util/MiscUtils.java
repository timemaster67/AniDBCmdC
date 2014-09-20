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

import java.util.*;

/**
 * <p>
 * Title: MiscUtils
 * </p>
 * <p>
 * Description: Some misc utils like add an array to an ArrayList or merge two
 * Arrays. This class is just a container for static methods.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class MiscUtils {

	/**
	 * Not needed here. Nobody should create an instance of this class, so
	 * private.
	 */
	private MiscUtils() {
	}

	/**
	 * Adds an Object Array to an ArrayList.
	 * 
	 * @param v -
	 *            The ArrayList.
	 * @param obj -
	 *            The Object array that should be added at the end of the
	 *            ArrayList.
	 * @return - The merged ArrayList. Warning: You must cast every single
	 *         Object inside the ArrayList to your class.
	 */
	public static ArrayList addArrayToArrayList(ArrayList v, Object[] obj) {
		if (obj != null) {
			for (int i = 0; i < obj.length; i++)
				v.add(obj[i]);
		}
		return v;
	}

	/**
	 * Merges two Array to one.
	 * 
	 * @param first -
	 *            The first Array.
	 * @param second -
	 *            The Second Array.
	 * @return - A new Array from thos merged Arrays: Warning: You must cast
	 *         every single Object inside the array to your class.
	 */
	public static Object[] mergeArrays(Object[] first, Object[] second) {
		if (first != null && second == null)
			return first;
		if (first == null && second != null)
			return second;
		if (first != null && second != null) {
			int size = first.length + second.length;
			Object[] newObjectArray = new Object[size];
			for (int i = 0; i < first.length; i++)
				newObjectArray[i] = first[i];
			for (int i = 0; i < second.length; i++)
				newObjectArray[first.length + i] = second[i];
			return newObjectArray;
		}
		return null;
	}
}
