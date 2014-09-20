/******************************************************************************
 *
 * Jacksum version 1.5.0 - checksum utility in Java
 * Copyright (C) 2001-2004 Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
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

public class Xor8 extends AbstractChecksum {

	public Xor8() {
		super();
		value = 0;
	}

	public void reset() {
		value = 0;
		length = 0;
	}

	public void update(byte b) {
		value ^= b & 0xFF;
		length++;
	}

	public long getValue() {
		return value;
	}

	public String getHexValue() {
		String s = Service.hexformat(getValue(), 2);
		return (uppercase ? s.toUpperCase() : s);
	}

}

/*
 * Testvector from Motorola's GPS:
 * (http://www.motorola.com/ies/GPS/docs_pdf/checksum.pdf)
 * 
 * hex: 45 61 01 => 25
 */
