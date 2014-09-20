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

import java.io.*;

import jonelo.sugar.util.*;

import java.util.ArrayList;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import anidbcmdc.exceptions.UnknownReplyException;

/**
 * <p>
 * Title: FileUtils
 * </p>
 * <p>
 * Description: A File utility class which provides some advanced functions on
 * files. Like get all files recursive or rename a file or read a file into a
 * String. This class is just a container for static mathods.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class FileUtils {

	/**
	 * Default constructor is not needed here so private.
	 */
	private FileUtils() {
	}

	/**
	 * Gets all Files in a directory which match the startFile. Regular
	 * expression are possible.
	 * 
	 * @param f -
	 *            The file / dir where the files resist
	 * @param startFile -
	 *            The first file
	 * @param regex -
	 *            If true regular expressions are enabled
	 * @return An ArrayList of files
	 */
	private static ArrayList getFiles(File f, File startFile, boolean regex) {
		if (f.isDirectory()) {
			// result ArrayList with all files
			ArrayList result = new ArrayList();
			// cast File[] to result ArrayList
			result = MiscUtils.addArrayToArrayList(result, f.listFiles());
			// remove dirs from result ArrayList
			for (int i = 0; i < result.size(); i++) {
				if (((File) result.get(i)).isDirectory())
					result.remove(i);
			}
			return result;
		} else {
			// result ArrayList with all files
			ArrayList result = new ArrayList();
			// filter for listFiles(FileFilter)
			RegexFilter filter = new RegexFilter(startFile.getName(), regex);
			// get all files with which match f.getName()
			result = MiscUtils.addArrayToArrayList(result, f.getParentFile()
					.listFiles(filter));
			// remove dirs from result ArrayList
			for (int i = 0; i < result.size(); i++) {
				if (((File) result.get(i)).isDirectory())
					result.remove(i);
			}
			return result;
		}
	}

	/**
	 * Gets all files recursive from the passed dir. If f is not dir, this
	 * method will return all files in all subdirs that match this file.
	 * 
	 * @param f -
	 *            The base file or dir
	 * @param startFile -
	 *            The startfile (only needed for the recusrive call).
	 * @param regex -
	 *            enables regular expressions
	 * @return - An ArrayList of files
	 */
	private static ArrayList getRecursiveFiles(File f, File startFile,
			boolean regex) {
		ArrayList temp = new ArrayList();
		if (f.isDirectory()) {
			// add all files and dirs under this dir to temp
			temp = MiscUtils.addArrayToArrayList(temp, f.listFiles());
		} else {
			// list files in current dir because the current file can be a regex
			temp = MiscUtils.addArrayToArrayList(temp, f.getParentFile()
					.listFiles());
		}
		// result ArrayList with all files in subdirs
		ArrayList result = new ArrayList();
		for (int i = 0; i < temp.size(); i++) {
			File current = (File) temp.get(i);
			// recursive start of getRecursiveFiles
			if (current.isDirectory()) {
				result.addAll(getRecursiveFiles(current, startFile, regex));
			}
			// current file is not a dir
			else {
				RegexFilter filter = new RegexFilter(startFile.getName(), regex);
				if (filter.accept(current) || startFile.isDirectory())
					result.add(current);
				// result.addAll(getFiles(current, startFile, regex));
			}
		}
		return result;
	}

	/**
	 * Returns all files in all dirs of files recursively if the flag is set. If
	 * files contains files, only matching files will be returned. Regular
	 * expressions are allowed.
	 * 
	 * @param files -
	 *            All files and dirs that should be searched for files.
	 * @param recursive -
	 *            If true, all files and dirs will be searched recursively.
	 * @param regex -
	 *            If true regular expressions are allowed.
	 * @return - A File array of files.
	 */
	public static File[] getFiles(File[] files, boolean recursive, boolean regex) {
		// cast File[] to ArrayList
		ArrayList file = new ArrayList();
		file = MiscUtils.addArrayToArrayList(file, files);
		// ArrayList with all files from subdirs or dirs in files
		ArrayList tmpResult = new ArrayList();
		if (recursive) {
			// get all files in subdirs files
			for (int i = 0; i < file.size(); i++) {
				File current = (File) file.get(i);
				ArrayList arr = getRecursiveFiles(current, current, regex);
				tmpResult.addAll(arr);
			}
		} else {
			// get all files in dirs files
			for (int i = 0; i < file.size(); i++) {
				File current = (File) file.get(i);
				ArrayList arr = getFiles(current, current, regex);
				tmpResult.addAll(arr);
			}
		}
		// cast ArrayList to File[]
		File[] result = new File[tmpResult.size()];
		for (int i = 0; i < tmpResult.size(); i++)
			result[i] = (File) tmpResult.get(i);
		return result;
	}

	/**
	 * Renames a file to passed name and replaces all Strings in it like
	 * described in replaceList.
	 * 
	 * @param file -
	 *            The file that should be renamed.
	 * @param newFilename -
	 *            The new filename.
	 * @param replaceList -
	 *            A list of replacement instructions. Syntax: ab->ba
	 * @return - The renamed file
	 * @throws IOException -
	 *             If the File cant be renamed.
	 */
	public static File renameFile(File file, String newFilename,
			String[] replaceList) throws IOException {
		if (replaceList != null) {
			String[] sourceReplace = new String[replaceList.length];
			String[] destReplace = new String[replaceList.length];
			for (int i = 0; i < replaceList.length; i++) {
				String[] s = replaceList[i].split("->");
				
				// En vérifiant la taille de la chaine de caractère splitté en premier, 
				// Cela nous permet d'éviter un crash si les données entré sont mauvaise
				// et nous permet d'effacer complètement le caractère. 
				if (s.length >  0){
					sourceReplace[i] = s[0];
					
					if (s.length >  1){
						destReplace[i] = s[1];
					}else{
						destReplace[i] = "";
					}
					
				}else{
					// Si on ne peux obtenir le premier caractère à remplacer, on sort de la boucle
					// si les autres caractère à remplacer était valide, ils ne seront pas tenu compte
					i = replaceList.length;
				}
				
				
			}
			for (int i = 0; i < sourceReplace.length; i++) {
				if (!sourceReplace[i].equals("")){ // Vérifier que le tableau ne contiend pas de ""
					newFilename = GeneralString.replaceAllStrings(newFilename, sourceReplace[i], destReplace[i]);
				}
				
			}
		}
		newFilename = GeneralString.replaceAllStrings(newFilename, "/", "");
		newFilename = GeneralString.replaceAllStrings(newFilename, "\\", "");
		File f2 = new File(file.getParent() + File.separator + newFilename);
		//return file.renameTo(f2) ? f2 : null;
		if (file.renameTo(f2)){
			return f2;
		}else{
			throw new IOException("Impossible de renommer le fichier " + file.getName());
		}

	}

	/**
	 * Reads a file into a String
	 * 
	 * @param file -
	 *            The file that should be read into a String.
	 * @return - The interior of the file.
	 * @throws IOException -
	 *             If the file cant be read.
	 */
	public static String readFile(File file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = "";
		while ((line += in.readLine()) != null)
			;
		return line;
	}

	public static File moveFile(File file, String newPath) throws IOException {
		File f2 = new File(newPath);
		return file.renameTo(f2) ? f2 : null;
		//Files.move vas soit faire un RenameTo qui ne fonctionne qu'à l'intérieur du même système de fichier sur unix
		//ou bien un copy/delete vers un autre système de fichier.
		//return Files.move(file.toPath(), Paths.get(newPath)).toFile();
		
		
	}

	public static File[] removeFilesWithStringInPathFromArray(File[] f,
			String path) {
		//Fonction qui enlève les fichiers qui sont déjà dans le Finished Dir
		//Je ne comprend pas à quoi cela peut être utilisé, c'est à l'humain de décider quoi re-scanner et re-déplacer 
		ArrayList removeList = new ArrayList();
		MiscUtils.addArrayToArrayList(removeList, f);
		for (int i = 0; i < removeList.size(); i++) {
			if (((File) removeList.get(i)).getAbsolutePath().indexOf(path) != -1)
				removeList.remove(i);
		}
		File[] result = new File[removeList.size()];
		for (int i = 0; i < removeList.size(); i++)
			result[i] = (File) removeList.get(i);
		return result;
	}
}