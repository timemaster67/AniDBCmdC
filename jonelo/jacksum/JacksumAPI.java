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

package jonelo.jacksum;

import jonelo.jacksum.algorithm.*;
import jonelo.sugar.util.GeneralProgram;
import java.security.NoSuchAlgorithmException;

public class JacksumAPI {

	/**
	 * get an object of a checksum algoritm, always try to use fast
	 * implementations from the Java API
	 * 
	 * @param algorithm
	 *            code for the checksum algorithm
	 * @return a checksum algorithm object, returns null if algorithm is unknown
	 */
	public static AbstractChecksum getChecksumInstance(String algorithm)
			throws NoSuchAlgorithmException {
		return getChecksumInstance(algorithm, false);
	}

	/**
	 * get an object of a checksum algoritm
	 * 
	 * @param algorithm
	 *            code for the checksum algorithm
	 * @param alternate
	 *            use a pure Java implementation (only if available)
	 * @return a checksum algorithm object, returns null if algorithm is unknown
	 */
	public static AbstractChecksum getChecksumInstance(String algorithm,
			boolean alternate) throws NoSuchAlgorithmException {
		AbstractChecksum checksum = null;

		// most popular algorithms first
		if (algorithm.equals("sha") || algorithm.equals("sha1")
				|| algorithm.equals("sha-1") || algorithm.equals("sha160")
				|| algorithm.equals("sha-160")) {
			if (alternate)
				checksum = new MDgnu(gnu.crypto.Registry.SHA160_HASH);
			else
				checksum = new MD("SHA-1");
		} else if (algorithm.equals("crc32") || algorithm.equals("crc-32")
				|| algorithm.equals("fcs32") || algorithm.equals("fcs-32")) {
			if (alternate)
				checksum = new FCS32();
			else
				checksum = new Crc32();
		} else if (algorithm.equals("md5") || algorithm.equals("md5sum")) {
			if (alternate)
				checksum = new MDgnu(gnu.crypto.Registry.MD5_HASH);
			else
				checksum = new MD("MD5");
		} else if (algorithm.equals("cksum")) {
			checksum = new Cksum();
		} else if (algorithm.equals("bsd") || algorithm.equals("bsdsum")) {
			checksum = new SumBSD();
		} else if (algorithm.equals("sysv") || algorithm.equals("sysvsum")) {
			checksum = new SumSysV();
		} else if (algorithm.equals("adler32") || algorithm.equals("adler-32")) {
			checksum = new Adler32();
		} else if (algorithm.equals("crc32_mpeg2")
				|| algorithm.equals("crc-32_mpeg-2")) {
			checksum = new Crc32Mpeg2();
		}

		/*
		 * we use faster versions (supported since 1.4.2) if possible see
		 * http://java.sun.com/j2se/1.4.2/changes.html#security and
		 * http://java.sun.com/j2se/1.4.2/docs/guide/security/CryptoSpec.html#AppA
		 */
		else if (algorithm.equals("sha256") || algorithm.equals("sha-256")) {
			if (alternate)
				checksum = new MDgnu(gnu.crypto.Registry.SHA256_HASH);
			else if (GeneralProgram.isSupportFor("1.4.2"))
				checksum = new MD("SHA-256");
			else
				checksum = new MDgnu(gnu.crypto.Registry.SHA256_HASH);
		} else if (algorithm.equals("sha384") || algorithm.equals("sha-384")) {
			if (alternate)
				checksum = new MDgnu(gnu.crypto.Registry.SHA384_HASH);
			else if (GeneralProgram.isSupportFor("1.4.2"))
				checksum = new MD("SHA-384");
			else
				checksum = new MDgnu(gnu.crypto.Registry.SHA384_HASH);
		} else if (algorithm.equals("sha512") || algorithm.equals("sha-512")) {
			if (alternate)
				checksum = new MDgnu(gnu.crypto.Registry.SHA512_HASH);
			else if (GeneralProgram.isSupportFor("1.4.2"))
				checksum = new MD("SHA-512");
			else
				checksum = new MDgnu(gnu.crypto.Registry.SHA512_HASH);
		} else if (algorithm.equals("tiger")) {
			checksum = new MDgnu(gnu.crypto.Registry.TIGER_HASH);
		} else if (algorithm.startsWith("haval")) {
			checksum = new MDgnu(algorithm);
		} else if (algorithm.equals("crc16") || algorithm.equals("crc-16")) {
			checksum = new Crc16();
		} else if (algorithm.equals("ripemd160")
				|| algorithm.equals("ripemd-160")
				|| algorithm.equals("ripe-md160") || algorithm.equals("rmd160")
				|| algorithm.equals("rmd-160")) {
			checksum = new MDgnu(gnu.crypto.Registry.RIPEMD160_HASH);
		} else if (algorithm.equals("ripemd128")
				|| algorithm.equals("ripemd-128")
				|| algorithm.equals("ripe-md128") || algorithm.equals("rmd128")
				|| algorithm.equals("rmd-128")) {
			checksum = new MDgnu(gnu.crypto.Registry.RIPEMD128_HASH);
		} else if (algorithm.equals("whirlpool")) {
			checksum = new MDgnu(gnu.crypto.Registry.WHIRLPOOL_HASH);
		} else if (algorithm.equals("crc64") || algorithm.equals("crc-64")) {
			checksum = new Crc64();
		} else if (algorithm.equals("ed2k") || algorithm.equals("emule")
				|| algorithm.equals("edonkey")) {
			checksum = new Edonkey();
		} else if (algorithm.equals("md4") || algorithm.equals("md4sum")) {
			checksum = new MDgnu(gnu.crypto.Registry.MD4_HASH);
		} else if (algorithm.equals("md2") || algorithm.equals("md2sum")) {
			checksum = new MDgnu(gnu.crypto.Registry.MD2_HASH);
		} else if (algorithm.equals("elf") || algorithm.equals("elf32")
				|| algorithm.equals("elf-32")) {
			checksum = new Elf();
		} else if (algorithm.equals("fcs16") || algorithm.equals("fcs-16")) {
			checksum = new FCS16();
		} else if (algorithm.equals("sum8") || algorithm.equals("sum-8")) {
			checksum = new Sum8();
		} else if (algorithm.equals("sum16") || algorithm.equals("sum-16")) {
			checksum = new Sum16();
		} else if (algorithm.equals("sum24") || algorithm.equals("sum-24")) {
			checksum = new Sum24();
		} else if (algorithm.equals("sum32") || algorithm.equals("sum-32")) {
			checksum = new Sum32();
		} else if (algorithm.equals("xor8") || algorithm.equals("xor-8")) {
			checksum = new Xor8();
		} else { // unknown
			throw new NoSuchAlgorithmException(algorithm
					+ " is an unknown algorithm.");
		}
		return checksum;
	}

}