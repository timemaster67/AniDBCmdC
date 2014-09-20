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
 * MDgnu is a wrapper class for accessing MessageDigests from the
 * GNU crypto project http://www.gnu.org/software/classpathx/crypto
 *
 *****************************************************************************/
package jonelo.jacksum.algorithm;

import java.security.NoSuchAlgorithmException;
import gnu.crypto.hash.*;

public class MDgnu extends AbstractChecksum {

	private IMessageDigest md = null;

	/** Creates new MDgnu */
	public MDgnu(String arg) throws NoSuchAlgorithmException {
		// value0; we don't use value, we use md
		length = 0;
		filename = null;
		separator = " ";
		hex = true;
		md = HashFactory.getInstance(arg);
		if (md == null)
			throw new NoSuchAlgorithmException(arg
					+ " is an unknown algorithm.");
	}

	public void reset() {
		md.reset();
		length = 0;
	}

	public void update(byte[] buffer, int offset, int len) {
		md.update(buffer, offset, len);
		length += len;
	}

	public void update(byte b) {
		md.update(b);
		length++;
	}

	public String toString() {
		return getHexValue()
				+ separator
				+ (isTimestampWanted() ? getTimestampFormatted() + separator
						: "") + getFilename();
	}

	public String getHexValue() {
		return Service.format(md.digest(), uppercase);
	}

}
