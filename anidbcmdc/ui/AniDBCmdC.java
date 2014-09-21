/******************************************************************************
 *
 * AniDBCmdC version 1.0 - AniDB client in Java
 * Copyright (C) 2005 ExElNeT
 * Copyright (C) 2014 Timemaster
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

package anidbcmdc.ui;

import gnu.getopt.*;
import anidbcmdc.impl.*;
import java.io.*;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * <p>
 * Title: AniDBCmdC
 * </p>
 * <p>
 * Description: The mainp rogram, parses options and starts help console or maybe
 * later a gui
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT, Timemaster67
 * @version 1.0
 */
public class AniDBCmdC {
	
	public ResourceBundle messages;

	/**
	 * Creates an instance of this class and starts the parseOptions method
	 * 
	 * @param args -
	 *            Options passed by the user
	 */
	public static void main(String[] args) {
		AniDBCmdC opt = new AniDBCmdC();
		opt.parseOptions(args);
	}

	public AniDBCmdC(){
		messages = ResourceBundle.getBundle("AniDBCmdC",Locale.getDefault());	
	}
	
	/**
	 * Parses all options given by the user with the help of getopt and writes
	 * those options to clients Options object
	 * 
	 * @param args -
	 *            Options passed by the user
	 */
	private void parseOptions(String[] args) {
		Options options = new Options();
		LongOpt[] longopts = new LongOpt[14];
		longopts[0] = new LongOpt("files", LongOpt.REQUIRED_ARGUMENT, null, 'f');
		longopts[1] = new LongOpt("gui", LongOpt.NO_ARGUMENT, null, 'g');
		longopts[2] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
		longopts[3] = new LongOpt("ip-address", LongOpt.REQUIRED_ARGUMENT,
				null, 'i');
		longopts[4] = new LongOpt("login", LongOpt.REQUIRED_ARGUMENT, null, 'l');
		longopts[5] = new LongOpt("password", LongOpt.REQUIRED_ARGUMENT, null,
				'p');
		longopts[6] = new LongOpt("clientport", LongOpt.REQUIRED_ARGUMENT,
				null, 'c');
		longopts[7] = new LongOpt("recursive-files", LongOpt.REQUIRED_ARGUMENT,
				null, 'r');
		longopts[8] = new LongOpt("name-replace", LongOpt.REQUIRED_ARGUMENT,
				null, 'n');

		// Pouvoir désactiver le renommage des animes
		longopts[9] = new LongOpt("rename", LongOpt.NO_ARGUMENT,
				null, 'k');
		
		longopts[10] = new LongOpt("style", LongOpt.REQUIRED_ARGUMENT, null, 's');
		longopts[11] = new LongOpt("move-finished", LongOpt.REQUIRED_ARGUMENT,
				null, 'm');
		longopts[12] = new LongOpt("anidb-mylist-flags",
				LongOpt.REQUIRED_ARGUMENT, null, 'a');
		longopts[13] = new LongOpt("enable-animetree", LongOpt.NO_ARGUMENT,
				null, 0);
		
	

		Getopt g = new Getopt("AniDBCmdC", args, "Rx", longopts, true);

		int c;
		while ((c = g.getopt()) != -1) {
			switch (c) {
			case 0:
				options.setAnimeTree(true);
				break;
			case 'a':
				options.setMylistFlags(parseParams(g.getOptarg()));
				break;
			case 'f':
				// Ne pas parser les fichiers, car les fichiers qui contienne des virgule
				// sont mal handeled
				//String[] s = parseParams(g.getOptarg());
				String[] s = new String[] {g.getOptarg() };
				File[] f = new File[s.length];
				for (int i = 0; i < s.length; i++)
					f[i] = new File(s[i]);
				options.addFileArray(f);
				break;
			case 'g':
				options.setGui(true);
				break;
			case 'h':
				printHelp();
				return;
			case 'i':
				String[] i = g.getOptarg().split(":");
				options.setRemoteAddress(i[0]);
				options.setLocalPort(Integer.parseInt(i[1]));
				break;
			case 'l':
				options.setLogin(g.getOptarg());
				break;
			case 'm':
				options.setMoveFinishedDir(new File(g.getOptarg()));
				break;
			case 'p':
				options.setPassword(g.getOptarg());
				break;
			case 'c':
				options.setLocalPort(Integer.parseInt(g.getOptarg()));
				break;
			case 'r':
				String[] d = parseParams(g.getOptarg());
				File[] r = new File[d.length];
				for (int j = 0; j < d.length; j++)
					r[j] = new File(d[j]);
				options.addRfileArray(r);
				break;
			case 'n':
				options.addReplaceList(parseParams(g.getOptarg()));
				break;
			case 'R':
				options.setRecursive(true);
				break;
			case 's':
				options.setStyle(g.getOptarg());
				break;
			case 'x':
				options.setRegex(true);
				break;
			case 'k':
				//Option pour activer le renommage
				options.setRename(true);
				break;
			default:
				break;
			}
		}
		//Ceci ajoute les fichiers passé en paramêters supplémentaires, séparé par des espaces
		//sans aucune switch en tenant compte de l'option -R récursive
		int count = args.length - g.getOptind();
		File[] files = new File[count];
		int j = 0;
		for (int i = g.getOptind(); i < args.length; i++) {
			files[j] = new File(args[i]);
			j++;
		}
		if (options.isRecursive())
			options.addRfileArray(files);
		else
			options.addFileArray(files);

		// at this point every options was parsed
		// we decide here if we should start the gui or stay at consolemode

		if (options.isGui()) {
			// start the gui here
			/*System.out.println("AniDBCmdC: Sorry, but no gui available yet."
					+ "\n" + "\n"
					+ "Try 'anidbcmdc.jar --help' for more information.");*/
			System.out.println(messages.getString("gui"));
			System.out.println();
			System.out.println();
			System.out.println(messages.getString("help"));		

		} else {
			Console console = new Console(options);
			console.start();
			if (console.booErrorOccurred){
				System.exit(1); // Exit avec un code de sortie 1, signifiant qu'il y a eu une erreur
			}
		}
	}

	/**
	 * Splits the argument of an option into a String array
	 * 
	 * @param paramString -
	 *            The argument of an option
	 * @return - A String array with all arguments for this option
	 */
	private String[] parseParams(String paramString) {
		return paramString.indexOf(",") != -1 ? paramString.split("\\s*,\\s*")
		: new String[] { paramString };
		// Désactiver le split par virgule car il ne tiend pas compte des parantèses, 
		// ce qui brise les fichiers avec des parantèses à l'intérieur
		//return new String[] { paramString };	
	}

	/**
	 * Prints a help for the user (-h or --help or --h). This help is written in
	 * a standard unix manner with the help of gnu.getopt for java
	 */
	private void printHelp() {
		
		System.out.println(messages.getString("Usage_1")); 
		System.out.println(messages.getString("Usage_2")); 
		System.out.println(messages.getString("Usage_3")); System.out.println();
		System.out.println(messages.getString("Usage_4")); System.out.println(); 
		System.out.println(messages.getString("Usage_5")); 
		System.out.println(messages.getString("Usage_6"));
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_7")); 
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_8")); 
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_9")); 
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_10"));
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_11")); 
		System.out.println(messages.getString("Usage_12")); 
		System.out.println(messages.getString("Usage_13")); 
		System.out.println(messages.getString("Usage_14")); 
		System.out.println(messages.getString("Usage_15")); 
		System.out.println(messages.getString("Usage_16")); 
		System.out.println(messages.getString("Usage_17")); 
		System.out.println(messages.getString("Usage_18")); 
		System.out.println(messages.getString("Usage_19")); 
		System.out.println(messages.getString("Usage_20")); 
		System.out.println(messages.getString("Usage_21")); 
		System.out.println(messages.getString("Usage_22")); 
		System.out.println(messages.getString("Usage_23")); 
		System.out.println(messages.getString("Usage_24")); 
		System.out.println(messages.getString("Usage_25")); 
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_26"));
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_27")); 
		System.out.println(messages.getString("Usage_28")); 
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_29"));
		System.out.println("\t\t\t\t\t  " + messages.getString("Usage_30")); 
		System.out.println(messages.getString("Usage_31")); 

		/*
		System.out
				.println("Usage: anidbcmdc.jar [OPTION]... login=LOGIN pass=PASSWORD DIRECTORY"
				
						+ "  or:  anidbcmdc.jar [OPTION]... login=LOGIN pass=PASSWORD --files=DIRECTORY_LIST"
						+ "\n"
						+ "  or:  anidbcmdc.jar [OPTION]... login=LOGIN pass=PASSWORD --recursive-files=DIRECTORY_LIST"
						+ "\n"
						+ "Hash files in DIRECTORY(s), add them to AniDB - MyList and rename them."
						+ "\n"
						+ "\n"
						+ "Mandatory arguments to long options are mandatory for short options too."
						+ "\n"
						+ "  -a, --anidb-mylist-flags=FLAG_LIST      describes using which flags a file should be added"
						+ "\n"
						+ "                                            syntax: 'addit,state,viewed,storage,other'"
						+ "\n"
						+ "                                            addit syntax: 0 do not add to mylist, 1 for add new, 2 add all"
						+ "\n"
						+ "                                            state sytax: 0 - unknown, 1 - hdd, 2 - cd, 3 - deleted"
						+ "\n"
						+ "                                            4 - shared, 5 - release  viewed: 0 or 1, storage, other strings"
						+ "\n"
						+ "                                            default: '1,1,0,,'"
						+ "\n"
						+ "      --enable-animetree                  will create a subdir structure in --move-finished dir"
						+ "\n"
						+ "                                            e.g if --move-finished dir 'anime'"
						+ "\n"
						+ "                                            all bleach eps will end in 'anime/bleach/bleach...avi'"
						+ "\n"
						+ "  -f, --files=DIRECTORY_LIST              processes files in the listed DIRECTORY(s)"
						+ "\n"
						+ "  -g, --gui                               will start the gui-mode (no gui available yet)"
						+ "\n"
						+ "  -h, --help                              print this help"
						+ "\n"
						+ "  -i, --ip-address=IP-ADDRESS:PORT        specify the IP-ADDRESS and PORT of AniDB"
						+ "\n"
						+ "                                            default: 'api.anidb.info:9000'"
						+ "\n"
						+ "  -l, --login=LOGINNAME                   specifiy AniDB LOGINNAME"
						+ "\n"
						+ "  -m, --move-finished=DIRECTORY           moves finished files to DIRECTORY"
						+ "\n"
						+ "  -p, --password=PASSWORD                 specifiy AniDB PASSWORD"
						+ "\n"
						+ "  -c  --clientport=PORT                   the localport on which anidbcmdc is working"
						+ "\n"
						+ "                                            default: '44444'"
						+ "\n"
						+ "  -r, --recursive-files=DIRECTORY_LIST    processes files recursive in the listed DIRECTORY(s) (incl subdirs)"
						+ "\n"
						+ "  -n, --name-replace=REPLACE_LIST         replaces SOURCE string with DEST string of the anime filename"
						+ "\n"
						+ "  -R                                      processes files recursive in DIRECTORY(s) (incl subdirs)"
						+ "\n"
						+ "                                            syntax: 'SOURCE->DEST,SOURCE->DEST,...'"
						+ "\n"
						+ "  -k  --rename                            renames files"
						+ "  -s, --style=STYLE                       renames files after STYLE"
						+ "\n"
						+ "                                            available templates: %animenamepart1,%animenamepart2,"
						+ "\n"
						+ "                                            %epnamepart1,%epnamepart2,%epnr,%group,%dublang,"
						+ "\n"
						+ "                                            %crc,%resolution,%quality,%vidcodec,%audcodec,%md5,"
						+ "\n"
						+ "                                            %censored,%version,%ed2k,%sha1,%sublang,%ripsource"
						+ "\n"
						+ "                                            default: '%animenamepart1_%animenamepart2_-_%epnr_-_%epnamepart1_"
						+ "\n"
						+ "                                            %epnamepart2_[%group](%crc)[AniDB]'"
						+ "\n"
						+ "  -x                                      enables regular expressions in filenames"
						+ "\n"
						+ "                                            example: anidbcmdc.jar -R -l=name -p=pass "
						+ "\n"
						+ "                                            '/path/.*\\.avi|.*\\.ogm|.*\\.mpg' will add all"
						+ "\n"
						+ "                                            .avi,.ogm,.mpg files in all subdirs of /path"
						+ "\n"
						+ "                                            more information: http://java.sun.com/j2se/1.4.2/docs/"
						+ "\n"
						+ "                                            api/java/util/regex/Pattern.html"
						+ "\n" + "\n" + "Report bugs to <exelnet@web.de>.");*/

	}
}
