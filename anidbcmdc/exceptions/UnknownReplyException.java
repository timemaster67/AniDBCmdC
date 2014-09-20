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

package anidbcmdc.exceptions;

/**
 * <p>
 * Title: UnknownReplyException
 * </p>
 * <p>
 * Description: Thrown if the AniDBUdpApi gets an unknown reply from the server
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class UnknownReplyException extends Exception {

	/**
	 * @param s -
	 *            The Exception error message
	 */
	public UnknownReplyException(String s) {
		super(s);
	}
}
