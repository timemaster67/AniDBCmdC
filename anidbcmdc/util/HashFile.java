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

import jonelo.jacksum.algorithm.*;
import java.io.*;
import jonelo.jacksum.*;

/**
 * <p>
 * Title: HashFile
 * </p>
 * <p>
 * Description: Offers all kinds of hash algorithms for file. (JackSum)
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class HashFile {

	private File file;

	private AbstractChecksum ed2k, md5, sha1, crc;

	private long size;

	/**
	 * Creates a new HashFile object and computes the size of the file.
	 * 
	 * @param file -
	 *            The file.
	 */
	public HashFile(File file) {
		this.file = file;
		size = (long) file.length();
	}

	/**
	 * Generates a ed2k hash of the file.
	 * 
	 * @throws IOException -
	 *             If the file cant be hashed.
	 * @throws Exception -
	 *             If the algorithm is unknown or something else failed.
	 */
	public void hashEd2k() throws IOException, Exception {
		ed2k = JacksumAPI.getChecksumInstance("ed2k");
		ed2k.readFile(file.getAbsolutePath());
	}

	/**
	 * Generates a md5 hash of the file.
	 * 
	 * @throws IOException -
	 *             If the file cant be hashed.
	 * @throws Exception -
	 *             If the algorithm is unknown or something else failed.
	 */
	public void hashMd5() throws IOException, Exception {
		md5 = JacksumAPI.getChecksumInstance("md5");
		md5.readFile(file.getAbsolutePath());
	}

	/**
	 * Generates a crc hash of the file.
	 * 
	 * @throws IOException -
	 *             If the file cant be hashed.
	 * @throws Exception -
	 *             If the algorithm is unknown or something else failed.
	 */
	public void hashCrc() throws IOException, Exception {
		crc = JacksumAPI.getChecksumInstance("crc-32");
		crc.readFile(file.getAbsolutePath());
	}

	/**
	 * Generates a sha1 hash of the file.
	 * 
	 * @throws IOException -
	 *             If the file cant be hashed.
	 * @throws Exception -
	 *             If the algorithm is unknown or something else failed.
	 */
	public void hashSha1() throws IOException, Exception {
		sha1 = JacksumAPI.getChecksumInstance("sha-1");
		sha1.readFile(file.getAbsolutePath());
	}

	/**
	 * Returns the ed2k hash of this file. Warning: this method does not hash
	 * the file.
	 * 
	 * @return - Returns the ed2k of the file if available, else null.
	 */
	public String getHashEd2k() {
		return ed2k != null ? ed2k.toString().split(" ")[0] : null;
	}

	/**
	 * Returns the md5 hash of this file. Warning: this method does not hash the
	 * file.
	 * 
	 * @return - Returns the md5 of the file if available, else null.
	 */
	public String getHashMd5() {
		return md5 != null ? md5.toString().split(" ")[0] : null;
	}

	/**
	 * Returns the crc hash of this file. Warning: this method does not hash the
	 * file.
	 * 
	 * @return - Returns the crc of the file if available, else null.
	 */
	public String getHashCrc() {
		return crc != null ? crc.toString().split(" ")[0] : null;
	}

	/**
	 * Returns the sha1 hash of this file. Warning: this method does not hash
	 * the file.
	 * 
	 * @return - Returns the sha1 of the file if available, else null.
	 */
	public String getHashSha1() {
		return sha1 != null ? sha1.toString().split(" ")[0] : null;
	}

	/**
	 * Returns the size of this file.
	 * 
	 * @return - Returns the size of this file.
	 */
	public long getSize() {
		return size;
	}
}