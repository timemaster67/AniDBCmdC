/******************************************************************************
 *
 * Jacksum version 1.5.1 - checksum utility in Java
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
import jonelo.sugar.util.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.security.NoSuchAlgorithmException;

/** This is Jacksum */
public class Jacksum {
	/*
	 * exit codes 0 ok 1 input error (parameter) 2 input error (checkfile) 3
	 * input error (file system)
	 */

	private final static String VERSION = "1.5.1";

	private final static String JACKSUM = "Jacksum";

	private final static String METAINFO = JACKSUM + ": Meta-Info: ";

	private final static String COMMENT = JACKSUM + ": Comment: ";

	private final static String CSEP = "\t"; // separator for the checkfile

	private final static String DEFAULT = "default";

	private final static String TIMESTAMPFORMAT_DEFAULT = "yyyyMMddHHmmss";

	private AbstractChecksum checksum = null;

	private String checksumArg = null;

	private String expected = null;

	private String format = null;

	private char fileseparatorChar = '/';

	private boolean _f = false, _x = false, _X = false, _r = false, _t = false,
			_m = false, _p = false, _y = false, _l = false, _d = false,
			_S = false, _e = false, _F = false, _alternate = false, _P = false;

	/**
	 * Jacksum's main method
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String args[]) {
		new Jacksum(args);
	}

	/**
	 * get Jacksum's program version
	 * 
	 * @return version number
	 */
	public final static String getVersion() {
		return JACKSUM + " " + VERSION;
	}

	/** print the GPL information an OSI certified note to stdout */
	private void printGPL() {
		System.out
				.println("\n "
						+ JACKSUM
						+ " v"
						+ VERSION
						+ ", Copyright (C) 2002-2004, Dipl.-Inf. (FH) Johann N. Loefflmann\n");
		System.out
				.println(" "
						+ JACKSUM
						+ " comes with ABSOLUTELY NO WARRANTY; for details see 'license.txt'.");
		System.out
				.println(" This is free software, and you are welcome to redistribute it under certain");
		System.out.println(" conditions; see 'license.txt' for details.");
		System.out
				.println(" This software is OSI Certified Open Source Software.");
		System.out
				.println(" OSI Certified is a certification mark of the Open Source Initiative.\n");
		System.out
				.println(" Go to http://www.jonelo.de/java/jacksum/index.html to get the latest version.\n");
	}

	/** print GPL info and a short help */
	public void printHelpShort() {
		printGPL();
		System.out.println(" For more information please type:");
		System.out.println(" java " + JACKSUM + " -h en");
		System.out.println("\n Fuer weitere Informationen bitte eingeben:");
		System.out.println(" java " + JACKSUM + " -h de\n");
		System.exit(0);
	}

	/**
	 * print the documentation
	 * 
	 * @param filename
	 *            the flat file containing the documentation
	 */
	public void printHelpLong(String filename) throws FileNotFoundException,
			IOException {
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			is = getClass().getResourceAsStream(filename);
			if (is == null)
				throw new FileNotFoundException(filename);
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (is != null)
				is.close();
		}
	}

	public void help(String code) {
		String filename = "/help/help_" + code + ".txt";
		int exitcode = 0;
		try {
			printHelpLong(filename);
		} catch (FileNotFoundException fnfe) {
			System.err.println("Helpfile " + filename + " not found.");
			exitcode = 1;
		} catch (IOException ioe) {
			System.err.println("Problem while reading helpfile " + filename);
			exitcode = 1;
		}
		System.exit(exitcode);
	}

	/**
	 * recursive method to traverse folders
	 * 
	 * @param dirItem
	 *            visit this folder
	 */
	public void recursDir(String dirItem) {
		String list[];
		File file = new File(dirItem);
		if (file.isDirectory()) {

			if (!_d || (_d && !Service.isSymbolicLink(file))) {
				list = file.list();
				if (list == null) {
					// isDirectory() returns true, but list() returns null
					// this strange behaviour was only detected on Windows with
					// a folder called
					// "C:\System Volume Information"
					System.err
							.println("Jacksum: Can't access file system folder \""
									+ file + "\"");
				} else {
					if (!_p && !_S) {
						if (_P)
							System.out.println("\n"
									+ file.toString().replace(
											File.separatorChar,
											fileseparatorChar) + ":");
						else
							System.out.println("\n" + file + ":");
					}
					ArrayList vd = new ArrayList();
					ArrayList vf = new ArrayList();

					Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);

					for (int i = 0; i < list.length; i++) {
						// fill both vector files and vector dirs
						File f = new File(dirItem + File.separatorChar
								+ list[i]);
						if (f.isDirectory())
							vd.add(list[i]);
						else
							vf.add(list[i]);
					}

					// don't print header if options -m or -p are used
					if (!_m && !_p) {
						// little header
						String tmp = vf.size() + " file";
						if (vf.size() != 1)
							tmp = tmp + "s";
						if (!_f) {
							tmp = tmp + ", " + vd.size() + " director";
							if (vd.size() != 1)
								tmp = tmp + "ies";
							else
								tmp = tmp + "y";
						}
						if (!_S)
							System.out.println(tmp);
					}

					// files...
					for (int a = 0; a < vf.size(); a++) {
						recursDir(dirItem + File.separatorChar + vf.get(a));
					}

					// dirs...
					if (!_f) {
						for (int c = 0; c < vd.size(); c++)
							System.err.println("Jacksum: " + vd.get(c)
									+ ": Is a directory");
					}
					for (int d = 0; d < vd.size(); d++) {
						String tmp = dirItem + File.separatorChar + vd.get(d);
						recursDir(tmp);
					}
				}
			}
		} else
			processItem(dirItem);
	}

	/**
	 * process one folder
	 * 
	 * @param dirItem
	 *            visit this folder
	 */
	public void oneDir(String dirItem) {
		String list[];
		File file = new File(dirItem);

		if (file.isDirectory()) {
			list = file.list();
			if (list == null) {
				System.err
						.println("Jacksum: Can't access file system folder \""
								+ file + "\"");
			} else {
				Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);

				for (int i = 0; i < list.length; i++) {
					String tmp = dirItem
							+ (dirItem.endsWith(File.separator) ? ""
									: File.separator) + list[i];
					File f = new File(tmp);
					if (f.isDirectory()) {
						if (!_f)
							System.err.println("Jacksum: " + list[i]
									+ ": Is a directory");
					} else {
						processItem(tmp);
					}
				}
			}
		} else
			processItem(dirItem);
	}

	/**
	 * print a formatted checksum line
	 * 
	 * @param filename
	 *            process this file
	 */
	public void processItem(String filename) {
		File f = new File(filename);
		if (f.isFile()) {
			try {
				if (_S) {
					checksum.readFile(filename, false);

					if (checksum.isTimestampWanted())
						checksum.update(checksum.getTimestampFormatted()
								.getBytes("ISO-8859-1"));

					// let's provide platform independency with -S
					// as it is a summary which incorporates filenames into the
					// checksum,
					// but we don't print them
					if (File.separatorChar != '/')
						filename = filename.replace(File.separatorChar, '/');

					checksum.update(filename.getBytes("ISO-8859-1"));
				} else {
					String ret = getChecksumOutput(filename);
					if (ret != null) {
						if (_P && File.separatorChar != fileseparatorChar)
							ret = ret.replace(File.separatorChar,
									fileseparatorChar);

						System.out.println(ret);
					}
				}
			} catch (Exception e) {
				System.err.println(e);
			}
		} else {
			// a fifo for example (mkfifo myfifo)
			if (!_f)
				System.err.println("Jacksum: " + filename
						+ ": Is not a regular file");
		}
	}

	/**
	 * get a formatted checksum line
	 * 
	 * @return a full formatted checksum line
	 * @param filename
	 *            process this file
	 */
	public String getChecksumOutput(String filename) throws IOException {
		checksum.readFile(filename, true);
		File f = new File(filename);
		if (_r && !_p)
			checksum.setFilename(f.getName());
		else
			checksum.setFilename(filename);
		return (_F ? checksum.format(format) : checksum.toString());
	}

	/**
	 * get Meta information
	 * 
	 * @return a String containing meta-information
	 */
	public String getMetainfo() {
		String flags = _r ? "r" : "";
		flags = flags + (_x ? "x" : "");
		flags = flags + (_X ? "X" : "");
		flags = flags + (_p ? "p" : "");
		flags = flags + (_alternate ? "A" : "");
		String timeformat = _t ? checksum.getTimestampFormat() : "";
		char fsep = _P ? fileseparatorChar : File.separatorChar;

		return METAINFO + "version=" + VERSION + ";" + "algorithm="
				+ checksumArg + ";" + "filesep=" + fsep + ";" + "flags="
				+ flags + ";" + "tformat=" + timeformat + ";";
	}

	public String getComment() {
		// avoid the string concatenation operator +
		StringBuffer sb = new StringBuffer(240);

		sb.append(COMMENT);
		sb.append("created with ");
		sb.append(JACKSUM);
		sb.append(" ");
		sb.append(VERSION);
		sb.append(", http://jacksum.sourceforge.net\n");

		sb.append(COMMENT);
		sb.append("created on ");
		sb.append(new Date());
		sb.append("\n");

		sb.append(COMMENT);
		sb.append("os name=");
		sb.append(System.getProperty("os.name"));
		sb.append(";os version=");
		sb.append(System.getProperty("os.version"));
		sb.append(";os arch=");
		sb.append(System.getProperty("os.arch"));
		sb.append("\n");

		sb.append(COMMENT);
		sb.append("jvm vendor=");
		sb.append(System.getProperty("java.vm.vendor"));
		sb.append(";jvm version=");
		sb.append(System.getProperty("java.vm.version"));
		sb.append("\n");

		sb.append(COMMENT);
		sb.append("user dir=");
		sb.append(System.getProperty("user.dir"));

		return sb.toString();
	}

	/**
	 * read a file containing checksums and print out info about modifications
	 * 
	 * @param checkFile
	 *            the filename containing checksums
	 * @throws FileNotFoundException
	 *             if file is not there
	 * @throws IOException
	 *             during an IO error
	 */
	public void checkFile(String checkFile) throws FileNotFoundException,
			IOException {
		// read the checkFile line by line

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			fis = new FileInputStream(checkFile);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);

			String thisLine = null;
			StringTokenizer st = null, stsub = null;
			int ignoretokens = 2;
			String filename = null;

			// read the properties
			Properties props = new Properties();
			if ((thisLine = br.readLine()) != null) {
				if (thisLine.startsWith(METAINFO)) {
					st = new StringTokenizer(thisLine.substring(METAINFO
							.length(), thisLine.length()), ";");
					while (st.hasMoreTokens()) {
						stsub = new StringTokenizer(st.nextToken(), "=");
						props.setProperty(stsub.nextToken(), (stsub
								.hasMoreTokens() ? stsub.nextToken() : ""));
					}
					// version check
					if (new Version(props.getProperty("version"))
							.compareTo(new Version(VERSION)) > 0) {
						System.err
								.println("The file called "
										+ checkFile
										+ "\nwas generated by a newer version of Jacksum.");
						System.err
								.println("Upgrade to the latest Jacksum release, at least to version "
										+ props.getProperty("version"));
						System.exit(2);
					}
				} else {
					System.err
							.println("File does not contain meta information. Create meta info with option -m.\nExit.");
					System.exit(2);
				}
			} else {
				System.err.println("File is empty.\nExit.");
				System.exit(2);
			}

			_alternate = false;
			// read important flags first
			String flags = props.getProperty("flags", "");
			for (int i = 0; i < flags.length(); i++) {
				if (flags.charAt(i) == 'A') {
					_alternate = true;
				}
			}

			try {
				checksum = JacksumAPI.getChecksumInstance(props.getProperty(
						"algorithm", "sha1"), _alternate);
			} catch (NoSuchAlgorithmException nsae) {
				System.err.println(nsae.getMessage());
				System.exit(2);
			}

			if (checksum instanceof MD || checksum instanceof MDgnu
					|| checksum instanceof Edonkey) {
				ignoretokens--; // no size value
			}
			// ignore any custom separators
			checksum.setSeparator(CSEP);

			// is there a timeformat?
			if (props.getProperty("tformat").equals(""))
				checksum.setTimestampFormat(null);
			else { // yes, a timeformat has been set
				ignoretokens++;

				if (props.getProperty("tformat").equals("null")) { // bugfix
					// for
					// sourceforge-bug
					// #948070
					// (problem
					// with
					// files
					// generated
					// with
					// 1.4.0)
					System.err
							.println("Jacksum: Can't determine timeformat (tformat=null), trying default (tformat="
									+ TIMESTAMPFORMAT_DEFAULT + ")");
					checksum.setTimestampFormat(TIMESTAMPFORMAT_DEFAULT);
				} else {
					checksum.setTimestampFormat(props.getProperty("tformat"));
					// if CSEP (\t) is part of tformat, increase ignoretokens
					ignoretokens += GeneralString.countChar(props
							.getProperty("tformat"), '\t');
				}
			}

			fileseparatorChar = File.separatorChar;
			if (props.getProperty("filesep") == null
					|| props.getProperty("filesep").equals(""))
				fileseparatorChar = File.separatorChar;
			else
				fileseparatorChar = props.getProperty("filesep").charAt(0);

			// ignore any command line options, we read them from the file
			_r = false;
			_p = false;

			for (int i = 0; i < flags.length(); i++) {
				if (flags.charAt(i) == 'x') {
					checksum.setHex(true);
				} else if (flags.charAt(i) == 'X') {
					checksum.setUpperCase(true);
					checksum.setHex(true);
				} else if (flags.charAt(i) == 'r') {
					_r = true;
				} else if (flags.charAt(i) == 'p') {
					_p = true;
				}
			}

			// process the check file
			String folder = "";
			while ((thisLine = br.readLine()) != null) {
				if ((!thisLine.equals("")) && // ignore empty lines
						(!thisLine.startsWith(COMMENT)) // ignore Jacksum
				// comment lines
				) {
					if (thisLine.startsWith(JACKSUM)) {
						System.err.println(JACKSUM
								+ ": Ignoring unknown directive.");
					} else {

						if ((_r && !_p) && thisLine.endsWith(":")) { // it is
							// a
							// folder
							folder = thisLine.substring(0,
									thisLine.length() - 1);

							if (File.separatorChar != fileseparatorChar)
								folder = folder.replace(fileseparatorChar,
										File.separatorChar);

							if (!_l) {
								System.out.println("\n" + folder + ":");
							}
							folder += File.separator;
						} else {
							try {
								filename = parseFilename(thisLine, ignoretokens);
								int skip = filename.length();

								if (File.separatorChar != fileseparatorChar)
									filename = filename.replace(
											fileseparatorChar,
											File.separatorChar);

								if (_l) {
									skipOkFiles(folder + filename, thisLine,
											skip);
								} else {
									System.out.print(whatChanged(folder
											+ filename, thisLine, skip));
									System.out.println(filename);
								}
							} catch (NoSuchElementException e) {
								System.err.println(JACKSUM
										+ ": Invalid entry: " + thisLine);
							} catch (IOException ioe) {
								System.err.println(ioe);
							}
						}
					}
				} // if

			} // while

			// release file descriptors
		} finally {
			if (br != null)
				br.close();
			if (isr != null)
				isr.close();
			if (fis != null)
				fis.close();
		}
	}

	private void skipOkFiles(String filename, String thisLine, int skip)
			throws IOException {
		boolean out = false;
		if (!(new File(filename).exists())) {
			out = true;
		} else {
			String output = getChecksumOutput(filename);
			if (!output.regionMatches(0, thisLine, 0, output.length() - skip)) {
				out = true;
			}
		}
		if (out)
			System.out.println(filename);
	}

	private String whatChanged(String filename, String thisLine, int skip)
			throws IOException {
		if (!(new File(filename).exists())) {
			return ("[REMOVED] ");
		} else {
			String output = getChecksumOutput(filename);
			if (!output.regionMatches(0, thisLine, 0, output.length() - skip)) {
				return ("[FAILED]  ");
			} else
				return ("[OK]      ");
		}
	}

	private void expectation(AbstractChecksum checksum, String expected) {
		String value = ((_x || _X) ? checksum.getHexValue() : String
				.valueOf(checksum.getValue()));
		if (value.equalsIgnoreCase(expected)) {
			System.out.println("[OK]");
			System.exit(0);
		} else {
			System.out.println("[MISMATCH]");
			System.exit(1);
		}
	}

	private String parseFilename(String thisLine, int ignoretokens)
			throws NoSuchElementException {
		// get the filename
		String filename = "";
		StringTokenizer st = new StringTokenizer(thisLine, CSEP);
		for (int i = 0; i < ignoretokens; i++) {
			st.nextToken();
		}
		// if a filename has a CSEP in it's name
		filename = st.nextToken();
		while (st.hasMoreTokens()) {
			filename = filename + CSEP + st.nextToken();
		}
		return filename;
	}

	/**
	 * Constructor
	 * 
	 * @param args
	 *            program arguments
	 */
	public Jacksum(String args[]) {
		jonelo.sugar.util.GeneralProgram.requiresMinimumJavaVersion("1.3.1");
		boolean stdin = false, _s = false, _D = false, _q = false, _c = false, _b = false;

		String arg = null;
		String separator = null;
		String timestampFormat = null;
		String sequence = null;
		String checkfile = null;
		int firstfile = 0;

		if (args.length == 0)
			printHelpShort();
		else if (args.length > 0) {
			while (firstfile < args.length && args[firstfile].startsWith("-")) {
				arg = args[firstfile++];
				if (arg.equals("-a")) {
					if (firstfile < args.length) {
						arg = args[firstfile++].toLowerCase();
						checksumArg = arg;
					} else {
						System.err
								.println("Option -a requires an algorithm. Use -h for help. Exit.");
						System.exit(1);
					}
				} else if (arg.equals("-s")) {
					if (firstfile < args.length) {
						_s = true;
						arg = args[firstfile++];
						separator = jonelo.sugar.util.GeneralString
								.translateEscapeSequences(arg);
					} else {
						System.err
								.println("Option -s requires a separator string. Use -h for help. Exit.");
						System.exit(1);
					}
				} else if (arg.equals("-f")) {
					_f = true;
				} else if (arg.equals("-")) {
					stdin = true;
				} else if (arg.equals("-r")) {
					_r = true;
				} else if (arg.equals("-x")) {
					_x = true;
				} else if (arg.equals("-X")) {
					_X = true;
				} else if (arg.equals("-m")) {
					_m = true;
				} else if (arg.equals("-p")) {
					_p = true;
				} else if (arg.equals("-l")) {
					_l = true;
				} else if (arg.equals("-d")) {
					_d = true;
				} else if (arg.equals("-A")) {
					_alternate = true;
				} else if (arg.equals("-S")) {
					_S = true;
				} else if (arg.equals("-q")) {
					if (firstfile < args.length) {
						_q = true;
						arg = args[firstfile++];
						sequence = arg;
					} else {
						System.err
								.println("Option -q requires a hex sequence argument");
						System.exit(1);
					}
				} else if (arg.equals("-P")) {
					if (firstfile < args.length) {
						_P = true;
						arg = args[firstfile++];
						if (arg.length() != 1) {
							System.out
									.println("Option -P requires exactly one character");
							System.exit(1);
						} else {
							if (arg.charAt(0) == '/' || arg.charAt(0) == '\\')
								fileseparatorChar = arg.charAt(0);
							else {
								System.err
										.println("Option -P requires / or \\");
								System.exit(1);
							}
						}
					} else {
						System.err.println("Option -P requires an argument");
						System.exit(1);
					}
				} else if (arg.equals("-F")) {
					if (firstfile < args.length) {
						_F = true;
						arg = args[firstfile++];
						format = arg;
					} else {
						System.err.println("Option -F requires an argument");
						System.exit(1);
					}
				} else if (arg.equals("-c")) {
					if (firstfile < args.length) {
						_c = true;
						arg = args[firstfile++];
						checkfile = arg;
					} else {
						System.err
								.println("Option -c requires a filename parameter");
						System.exit(1);
					}
				} else if (arg.equals("-e")) {
					if (firstfile < args.length) {
						_e = true;
						arg = args[firstfile++];
						expected = arg;
					} else {
						System.err.println("Option -e requires an argument");
						System.exit(1);
					}
				} else if (arg.equals("-h")) {
					String code = null;
					if (firstfile < args.length) {
						code = args[firstfile++].toLowerCase();
					} else {
						code = "en";
					}
					help(code);
				} else if (arg.equals("-t")) {
					_t = true;

					if (firstfile < args.length) {
						timestampFormat = args[firstfile++];

						if (timestampFormat.equals(DEFAULT))
							timestampFormat = TIMESTAMPFORMAT_DEFAULT;

					} else {
						System.err
								.println("Option -t requires a format string. Use -h for help. Exit.");
						System.exit(1);
					}

				} else if (arg.equals("-y")) {
					_y = true;
				} else if (arg.equals("-v")) {
					System.out.println(getVersion());
					System.exit(0);
				} else {
					System.err
							.println("Unknown argument. Use -h for help. Exit.");
					System.exit(1);
				}
			} // end while
		}
		// end parsing arguments

		if (checksumArg == null) { // take the default
			checksumArg = "sha1";
		}
		// get the checksum implementation
		try {
			checksum = JacksumAPI.getChecksumInstance(checksumArg, _alternate);
		} catch (NoSuchAlgorithmException nsae) {
			System.err
					.println(nsae.getMessage()
							+ "\nUse -a <code> to specify a valid one.\nFor help and a list of all supported algorithms use -h.\nExit.");
			System.exit(1);
		}

		if (_s)
			checksum.setSeparator(separator);
		if (_x) {
			checksum.setHex(true);
			checksum.setUpperCase(false);
		}
		if (_X) {
			checksum.setHex(true);
			checksum.setUpperCase(true);
		}

		// timestamp
		if (_t && !_q) { // if -q is used, the -t will be ignored anyway
			try {
				// check if timestampformat is valid
				Format timestampFormatter = new SimpleDateFormat(
						timestampFormat);
				timestampFormatter.format(new Date());

				checksum.setTimestampFormat(timestampFormat);
			} catch (IllegalArgumentException iae) {
				System.err.println("Option -t is wrong (" + iae.getMessage()
						+ ")");
				System.exit(1);
			}
		}

		if (_S && _m) {
			System.out
					.println("Jacksum: -S and -m can't go together, it is not supported");
			System.exit(1);
		}

		// meta-info
		if (_m) {
			// check if tformat contains a semicolon
			// if this is the case, we will have trouble while parsing
			// the meta-info, because semicolon is the meta-separator
			if (_t) {
				if (timestampFormat.indexOf(";") > -1) {
					System.err
							.println("Option -t contains a semicolon. This is not supported with -m.");
					System.exit(1);
				}
			}
			_F = false;
			checksum.setSeparator(CSEP);
			System.out.println(getMetainfo());
			System.out.println(getComment());
		}

		String ret = null;
		String filename = null;

		// no file parameter
		if (_q) { // quick sequence and quit

			// ignore unsuitable parameters
			if (_t) {
				System.err
						.println("Jacksum: Option -t will be ignored, because option -q is used.");
				_t = false;
				checksum.setTimestampFormat(null);
			}

			byte[] bytearr = null;
			checksum.setFilename("");
			String seqlower = sequence.toLowerCase();

			if (seqlower.startsWith("txt:")) {
				sequence = sequence.substring(4);
				bytearr = sequence.getBytes();
			} else if (seqlower.startsWith("dec:")) {
				sequence = sequence.substring(4);

				int count = GeneralString.countChar(sequence, ',');
				bytearr = new byte[count + 1];

				StringTokenizer st = new StringTokenizer(sequence, ",");
				int x = 0;
				while (st.hasMoreTokens()) {
					int temp = 0;
					String stemp = null;
					try {
						stemp = st.nextToken();
						temp = Integer.parseInt(stemp);
					} catch (NumberFormatException nfe) {
						System.err.println(stemp + " is not a decimal number.");
						System.exit(1);
					}
					if (temp < 0 || temp > 255) {
						System.out.println("The number " + temp
								+ " is out of range.");
						System.exit(1);
					}
					bytearr[x++] = (byte) temp;
				}
			} else {
				if (seqlower.startsWith("hex:"))
					sequence = sequence.substring(4);

				// default, a hex sequence is expected
				if ((sequence.length() % 2) == 1) {
					System.err
							.println("An even number of nibbles was expected.\nExit.");
					System.exit(1);
				}
				try {
					bytearr = new byte[sequence.length() / 2];
					int x = 0;
					for (int i = 0; i < sequence.length();) {
						String str = sequence.substring(i, i += 2);
						bytearr[x++] = (byte) Integer.parseInt(str, 16);
					}
				} catch (NumberFormatException nfe) {
					System.err.println("Not a hex number. " + nfe.getMessage());
					System.exit(1);
				}
			}

			checksum.update(bytearr);

			if (_e)
				expectation(checksum, expected);
			else
				System.out.println(_F ? checksum.format(format) : checksum
						.toString());

			System.exit(0);
		} else {

			// checking files in the checkfile
			if (_c) {
				// ignoring flags
				_F = false;
				File f = new File(checkfile);
				if (!f.exists()) {
					System.err.println("Jacksum: " + checkfile
							+ ": No such file or directory. Exit.");
					System.exit(3);
				} else {
					if (f.isDirectory()) {
						System.err
								.println("Parameter is a directory, but a filename was expected. Exit.");
						System.exit(1);
					} else {
						try {
							checkFile(checkfile);
						} catch (Exception e) {
							System.err.println(e);
						}
					}
					System.exit(0);
				}

			} else {

				// only one file parameter
				if (args.length - firstfile == 1) {
					String dir = args[firstfile];
					// check if it is a directory
					File f = new File(dir);
					if (!f.exists()) {
						System.err.println("Jacksum: " + dir
								+ ": No such file or directory. Exit.");
						System.exit(3);
					} else {
						if (f.isDirectory())
							_D = true;
						else {
							if (f.isFile()) {
								if (_e) {
									try {
										checksum.readFile(dir);
										expectation(checksum, expected);
									} catch (IOException ioe) {
										System.err.println(ioe.getMessage());
										System.exit(3);
									}
								}
							} else {
								System.err.println("Jacksum: \"" + dir
										+ "\" is not a normal file");
								System.exit(3);
							}
						}
					}
				}
			}
		}

		// processing a directory
		if (_r || _D) {

			String dir = null;
			if (args.length - firstfile == 1)
				dir = args[firstfile];
			else if (args.length == firstfile)
				dir = ".";
			else {
				System.err
						.println("Too many parameters. One directory was expeced. Exit.");
				System.exit(1);
			}
			File f = new File(dir);
			if (!f.exists()) {
				System.err.println("Jacksum: " + dir
						+ ": No such file or directory. Exit.");
				System.exit(3);
			} else {
				if (f.isDirectory()) {
					if (_m) {
						// sourceforge-feature request #968487
						System.out.println(COMMENT + "param dir=" + dir);
					}

					if (_r)
						recursDir(dir);
					else
						oneDir(dir);
					if (_S)
						printSummary();
				} else {
					System.err
							.println("Parameter is a file, but a directory was expected. Exit.");
					System.exit(1);
				}
			}
		} else {

			// processing standard input
			if (stdin || (firstfile == args.length)) { // no file parameter

				if (_t) {
					System.err
							.println("Jacksum: Option -t will be ignored, because standard input is used.");
					_t = false;
					checksum.setTimestampFormat(null);
				}
				checksum.setFilename("");
				String s = null;
				BufferedReader in = new BufferedReader(new InputStreamReader(
						System.in));
				try {
					do {
						s = in.readLine();
						if (s != null) {
							// better than s=s+"\n";
							StringBuffer sb = new StringBuffer(s.length() + 1);
							sb.insert(0, s);
							sb.insert(s.length(), '\n');
							checksum.update(sb.toString().getBytes());
						}
					} while (s != null);

					if (_e)
						expectation(checksum, expected);
					else
						System.out.println(checksum.toString());

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				// processing arguments file list
				for (int i = firstfile; i < args.length; i++) {
					filename = args[i];
					try {
						File file = new File(filename);
						ret = null;
						if (!file.exists()) {
							ret = "Jacksum: " + filename
									+ ": No such file or directory";
						} else {
							if (file.isDirectory()) { // directory
								if (!_f)
									ret = "Jacksum: " + filename
											+ ": Is a directory";
							} else { // file
								processItem(filename);
							}
						}
						if (ret != null)
							System.err.println(ret);

					} catch (Exception e) {
						System.err.println(e);
					}
				} // end processing arguments file list
				if (_S)
					printSummary();
			}
		} //
	} // end constructor

	public void printSummary() {
		checksum.setFilename("");
		checksum.setTimestampFormat("");
		checksum.setSeparator("");
		if (_e)
			expectation(checksum, expected);
		else {
			System.out.println(_F ? checksum.format(format) : checksum
					.format("#CHECKSUM"));
		}
	}

}
