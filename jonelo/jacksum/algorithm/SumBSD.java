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

public class SumBSD extends AbstractChecksum {

	public SumBSD() {
		super();
		separator = " ";
	}

	/** implemented from original GNU C source */
	public void update(byte b) {
		value = (value >> 1) + ((value & 1) << 15);
		value += b & 0xFF;
		value &= 0xffff;
		length++;
	}

	public String toString() {
		long kb = (length + 1023) / 1024;
		return ((hex ? getHexValue() : Service.format(getValue(), "00000"))
				+ separator
				+ Service.right(kb, 5)
				+ separator
				+ (isTimestampWanted() ? getTimestampFormatted() + separator
						: "") + getFilename());
	}

	public String getHexValue() {
		String s = Service.hexformat(getValue(), 4);
		return (uppercase ? s.toUpperCase() : s);
	}

}
