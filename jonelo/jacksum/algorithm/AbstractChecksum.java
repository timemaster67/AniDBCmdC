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

import java.util.zip.Checksum;
import java.io.*;
import java.text.*;
import java.util.*;
import jonelo.sugar.util.*;

abstract public class AbstractChecksum implements Checksum {

	protected long value;

	protected long length;

	protected String separator;

	protected String filename;

	protected boolean hex; // output in hex?

	protected boolean uppercase; // hex in uppercase?

	protected String timestampFormat;

	protected Format timestampFormatter;

	protected long timestamp;

	protected int BUFFERSIZE = 8192;

	public AbstractChecksum() {
		value = 0;
		length = 0;
		separator = "\t";
		filename = null;
		hex = false;
		uppercase = false;
		timestampFormat = null;
		timestampFormatter = null;
		timestamp = 0;
	}

	// from the Checksum interfacce
	public void reset() {
		value = 0;
		length = 0;
	}

	// from the Checksum interface
	public void update(byte[] bytes, int offset, int length) {
		for (int i = offset; i < length; i++) {
			update(bytes[i]);
		}
	}

	public void update(byte b) {
		update((int) (b & 0xFF));
	}

	// from the Checksum interface
	public void update(int b) {
		length++;
	}

	public void update(byte[] bytes) {
		update(bytes, 0, bytes.length);
	}

	public long getValue() {
		return value;
	}

	public long getLength() {
		return length;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	public String toString() {
		return (hex ? getHexValue() : Long.toString(getValue()))
				+ separator
				+ length
				+ separator
				+ (isTimestampWanted() ? getTimestampFormatted() + separator
						: "") + filename;
	}

	public String format(String format) {
		String temp = GeneralString.replaceAllStrings(format, "#FINGERPRINT",
				"#CHECKSUM");
		temp = GeneralString.replaceAllStrings(temp, "#CHECKSUM",
				hex ? getHexValue() : Long.toString(getValue()));
		temp = GeneralString.replaceAllStrings(temp, "#FILESIZE", Long
				.toString(length));
		temp = GeneralString.replaceAllStrings(temp, "#FILENAME", filename);
		if (isTimestampWanted())
			temp = GeneralString.replaceAllStrings(temp, "#TIMESTAMP",
					getTimestampFormatted());
		temp = GeneralString.replaceAllStrings(temp, "#SEPARATOR", separator);
		temp = GeneralString.replaceAllStrings(temp, "#QUOTE", "\"");

		return temp;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setHex(boolean hex) {
		this.hex = hex;
	}

	public void setUpperCase(boolean uppercase) {
		this.uppercase = uppercase;
	}

	public String getHexValue() {
		String s = Long.toHexString(getValue());
		return (uppercase ? s.toUpperCase() : s);
	}

	public void setTimestamp(String filename) {
		File file = new File(filename);
		this.timestamp = file.lastModified();
	}

	public long getTimestamp() {
		return timestamp;
	}

	// set the timestampFormat to force a timestamp output
	public void setTimestampFormat(String timestampFormat) {
		this.timestampFormat = timestampFormat;
	}

	public String getTimestampFormat() {
		return timestampFormat;
	}

	public String getTimestampFormatted() {
		if (timestampFormatter == null)
			timestampFormatter = new SimpleDateFormat(timestampFormat);
		return timestampFormatter.format(new Date(timestamp));
	}

	public boolean isTimestampWanted() {
		return (timestampFormat != null);
	}

	public void readFile(String filename) throws IOException {
		readFile(filename, true);
	}

	public void readFile(String filename, boolean reset) throws IOException {
		this.filename = filename;
		if (isTimestampWanted())
			setTimestamp(filename);

		FileInputStream fis = null;
		BufferedInputStream bis = null;

		// http://java.sun.com/developer/TechTips/1998/tt0915.html#tip2
		try {
			fis = new FileInputStream(filename);
			bis = new BufferedInputStream(fis);
			if (reset)
				reset();
			int len = 0;
			byte[] buffer = new byte[BUFFERSIZE];
			while ((len = bis.read(buffer)) > -1) {
				update(buffer, 0, len);
			}
		} finally {
			if (bis != null)
				bis.close();
			if (fis != null)
				fis.close();
		}
	}
}
