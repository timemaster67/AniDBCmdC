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

import java.io.File;

import jonelo.sugar.util.GeneralString;
import anidbcmdc.impl.AniDBUdpApiConnection;
import anidbcmdc.impl.AnimeFile;
import anidbcmdc.impl.Options;
import anidbcmdc.util.FileUtils;
import anidbcmdc.util.HashFile;

/**
 * <p>
 * Title: Console
 * </p>
 * <p>
 * Description: The console main class, it will check if enough options were
 * passed and will then start the hash-checkatanidb-addtomylist-rename process
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class Console {

	private Options options;

	private boolean booOptionsNotValid;
	public boolean booErrorOccurred;

	/**
	 * Will check if enough options were passed and if not throw an errormessage
	 * 
	 * @param options -
	 *            The options for this client
	 */
	public Console(Options options) {
		booOptionsNotValid = false;
		booErrorOccurred = false;
		if (options.getLogin() == null) {
			
			System.out
					.println("AniDBCmdC: You need to specify your AniDB - Loginname."
							+ "\n"
							+ "            Use the option 'anidbcmdc.jar [OPTIONS] --login=LOGINNAME'."
							+ "\n");
			booOptionsNotValid = true;
		}
		if (options.getPassword() == null) {
			System.out
					.println("AniDBCmdC: You need to specify your AniDB - Password."
							+ "\n"
							+ "            Use the option 'anidbcmdc.jar [OPTIONS] --password=PASSWORD'."
							+ "\n");
			booOptionsNotValid = true;
		}
		if (!options.hasFiles()) {
			System.out
					.println("AniDBCmdC: You need to specify at least one file"
							+ "\n"
							+ "            Use the option 'anidbcmdc.jar [OPTIONS] --files=DIRECTORY_LIST'."
							+ "\n"
							+ "              or 'anidbcmdc.jar [OPTIONS] --recursive-files=DIRECTORY_LIST'."
							+ "\n"
							+ "              or 'anidbcmdc.jar [OPTIONS] DIRECTORY'."
							+ "\n");
			booOptionsNotValid = true;
		}
		if (options.getMoveFinishedDir() != null && !options.getMoveFinishedDir().isDirectory()) {
			System.out.println("AniDBCmdC: "
					+ options.getMoveFinishedDir().getAbsolutePath()
					+ " is not a valid directory." + "\n");
			booOptionsNotValid = true;
		}
		if (options.isAnimeTree() && options.getMoveFinishedDir() == null) {
			System.out
					.println("AniDBCmdC: You must set the option --move-finished=DIRECTORY if you wanna create an animetree."
							+ "\n");
			booOptionsNotValid = true;
		}
		if (booOptionsNotValid) {
			System.out
					.println("Try 'anidbcmdc.jar --help' for more information.");
			return;
		}
		this.options = options;
	}

	/**
	 * Starts the real client. All operations will be done with the passed
	 * options. There wont be any user questions. If something fails will the
	 * program throw an error message or abort.
	 */
	public void start() {
		if (!booOptionsNotValid) {
			try {
				File[] f = options.getAllFiles();
				
				if (f.length > 0){
					AniDBUdpApiConnection c = new AniDBUdpApiConnection(options
							.getLogin(), options.getPassword(), options
							.getLocalPort(), options.getRemoteAddress(), options
							.getRemotePort());
					c.auth();
						
					if (options.getMoveFinishedDir() != null && options.isRecursive()){
						//Fonction qui enlève les fichiers qui sont déjà dans le Finished Dir
						//Lors d'un scan récursif, pour éviter de re-scanner le dossier terminé involontairement
						f = FileUtils.removeFilesWithStringInPathFromArray(f, options.getMoveFinishedDir().getAbsolutePath());
					}
					
					for (int i = 0; i < f.length; i++) {
						try {
							File realFile = f[i];
							
							HashFile hf = new HashFile(realFile);
							hf.hashEd2k();
							System.out.println("Hashed:\t\t\t" + realFile.getName());
							AnimeFile animeFile = c.getAnimeFileByEd2k(hf
									.getHashEd2k(), hf.getSize());
							if (animeFile == null) {
								System.out.println("File is corrupt or not part of AniDB: "	+ realFile.getName() + "\n");
								booErrorOccurred = true;
							} else {
								System.out.println("Checked at AniDB:\t" + realFile.getName());
								String[] s = options.getMylistFlags();
								if (s != null) {
									if (s.length > 1 && s[1] != null){
										animeFile.setState(Integer.parseInt(s[1]));
									}
									if (s.length > 2 && s[2] != null){
										animeFile.setViewed(s[2].equals("1"));
									}
									if (s.length > 3 && s[3] != null){
										animeFile.setStorage(s[3]);
									}
									if (s.length > 4 && s[4] != null){
										animeFile.setOther(s[4]);
									}
									if (s[0] != null) {
										int x = Integer.parseInt(s[0]);
										switch (x) {
										case 0:
											System.out
													.println("Not added to MyList:\t"
															+ realFile.getName());
											break;
										case 1:
											String reply = c.addFileToMyList(
													animeFile, true);
											if (reply == null)
												System.out
														.println("Added to MyList:\t"
																+ realFile.getName());
											else
												System.out
														.println("Existed in MyList:\t"
																+ realFile.getName());
											break;
										case 2:
											c.addFileToMyList(animeFile, false);
											System.out.println("Added to MyList:\t"
													+ realFile.getName());
										default:
											break;
										}
									}
								} else {
									c.addFileToMyList(animeFile, true);
									System.out.println("Added to MyList:\t"
											+ realFile.getName());
								}

								// Activer le renamer 
								if (options.getRename() == true) {
									File renamed = FileUtils.renameFile(realFile, animeFile
										.createFilename(options.getStyle()),
										options.getReplaceList());
									//System.out.println("Renamed:\t\t" + realFile.getName());
									//System.out.println("To:\t\t\t" + renamed.getName());
									System.out.println("Renamed:\t\t" + renamed.getName());
									realFile = renamed; // Je me demande si je ne cause pas un memory leak ?
									renamed = null;
								}
	
								
								
								
								if (options.getMoveFinishedDir() != null) {
									if (options.isAnimeTree()) {
										String anime = animeFile.getAnimeName();
										
										//Clean the directory name the say way as the file name using the replace list. 
										anime = FileUtils.ReplaceFileName(anime, options.getReplaceList());
										//Also remove any directory separator as we use it to define the full destination path
										anime = GeneralString.replaceAllStrings(anime, File.separator, "");
																				
										String moveDir = options
												.getMoveFinishedDir().getAbsolutePath();
										String animeDir = moveDir + File.separator + anime;
										if(!new File(animeDir).exists())
											new File(animeDir).mkdir();									
										String absPath =  animeDir + File.separator	+ realFile.getName();
										FileUtils.moveFile(realFile, absPath);
										System.out.println("Moved:\t\t\t"
												+ realFile.getName());
										System.out.println("To:\t\t\t" + absPath);
									} else {
										String absPath = options
												.getMoveFinishedDir()
												+ File.separator
												+ realFile.getName();
										FileUtils.moveFile(realFile, absPath);
										System.out.println("Moved:\t\t\t"
												+ realFile.getName());
										System.out.println("To:\t\t\t" + absPath);
									}
								}
							}
							System.out.println();
						} catch (Exception e) {
							System.out.println(e + "\n");
							booErrorOccurred = true;
						}
					}
					c.logout();
				}else{
					System.out.println("Fichier non trouvé\n");
					booErrorOccurred = true;	
				}
				
			} catch (Exception e) {
				System.out.println(e + "\n");
				booErrorOccurred = true;
			}
		}
	}
}
