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

package anidbcmdc.impl;

import java.io.*;
import anidbcmdc.util.*;

/**
 * <p>
 * Title: Options
 * </p>
 * <p>
 * Description: Holds all options this client can take
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class Options {

	private String[] params, replaceList, mylistFlags;

	private File[] fileArray, rfileArray;

	private File moveFinishedDir;

	private String style, login, password, remoteAddress;
	
	private boolean rename;

	private int localPort, remotePort;

	private boolean regex, recursive, gui, animeTree;

	/**
	 * Sets some defaults for the basic options
	 */
	public Options() {
		//style = "%animenamepart1_%animenamepart2_-_%epnr_-_%epnamepart1_%epnamepart2_[%group](%crc)[AniDB]";
		style = "%animenamepart1 %animenamepart2 - %epnr - %epnamepart1 %epnamepart2 [%group][%CRC]";
		localPort = 44444;
		remoteAddress = "api.anidb.info";
		remotePort = 9000;
		regex = false;
	}

	/**
	 * Adds non recursive files and dirs to the global non recursive File array
	 * 
	 * @param fileArray -
	 *            The File array that should be added to the files and dirs that
	 *            will be evaluated
	 */
	public void addFileArray(File[] fileArray) {
		Object[] o = MiscUtils.mergeArrays(this.fileArray, fileArray);
		File[] f = new File[o.length];
		for (int i = 0; i < o.length; i++)
			f[i] = (File) o[i];
		this.fileArray = f;
	}

	/**
	 * Adds recursive files and dirs to the global recursive File array
	 * 
	 * @param rfileArray -
	 *            The File array that should be added to the files and dirs that
	 *            will be evaluated
	 */
	public void addRfileArray(File[] rfileArray) {
		Object[] o = MiscUtils.mergeArrays(this.rfileArray, rfileArray);
		File[] f = new File[o.length];
		for (int i = 0; i < o.length; i++)
			f[i] = (File) o[i];
		this.rfileArray = f;
	}

	/**
	 * Adds filename replacements to the global replacement array each part of
	 * this array should have the syntax: SOURCE->DESTINATION example: ab->bc
	 * 
	 * @param replaceList -
	 *            The list of replacements
	 */
	public void addReplaceList(String[] replaceList) {
		Object[] o = MiscUtils.mergeArrays(this.replaceList, replaceList);
		String[] f = new String[o.length];
		for (int i = 0; i < o.length; i++)
			f[i] = (String) o[i];
		this.replaceList = f;
	}

	/**
	 * Returns all files that should be evaluated This method will combine
	 * recursive and non recursive files and dirs
	 * 
	 * @return - Returns all files that should be evaluated
	 */
	public File[] getAllFiles() {
		File[] a = FileUtils.getFiles(fileArray, false, regex);
		File[] b = FileUtils.getFiles(rfileArray, true, regex);
		Object[] o = MiscUtils.mergeArrays(a, b);
		File[] result = new File[o.length];
		for (int i = 0; i < o.length; i++)
			result[i] = (File) o[i];
		return result;
	}

	/**
	 * Checks if all the client has some files to evaluate
	 * 
	 * @return - true if |files| > 0 else false
	 */
	public boolean hasFiles() {
		if (fileArray == null) {
			if (rfileArray == null)
				return false;
			if (rfileArray.length == 0)
				return false;
			return true;
		}
		if (rfileArray == null) {
			if (fileArray == null)
				return false;
			if (fileArray.length == 0)
				return false;
			return true;
		}
		if (fileArray.length == 0 && rfileArray.length == 0)
			return false;
		return true;
	}

	/**
	 * Returns the AniDB username
	 * 
	 * @return Returns the login.
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Sets the AniDB username
	 * 
	 * @param login
	 *            The login to set.
	 */
	public void setLogin(String login) {
		this.login = login;
	}

	/**
	 * Returns the users password
	 * 
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the users password
	 * 
	 * @param password
	 *            The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Returns the style how the AnimeFile should be renamed
	 * 
	 * @return Returns the style.
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Sets the AnimeFile filename style
	 * 
	 * @param style
	 *            The style to set.
	 */
	public void setStyle(String style) {
		this.style = style;
	}

	/**
	 * Returns the local port this client is running on
	 * 
	 * @return Returns the localPort.
	 */
	public int getLocalPort() {
		return localPort;
	}

	/**
	 * Sets the local port this client should run on
	 * 
	 * @param localPort
	 *            The localPort to set.
	 */
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/**
	 * Returns the address of the AniDBUdpApi
	 * 
	 * @return Returns the remoteAddress.
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * Sets the address of the AniDBUdpApi
	 * 
	 * @param remoteAddress
	 *            The remoteAddress to set.
	 */
	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	/**
	 * Returns the port of the AniDBUdpApi
	 * 
	 * @return Returns the remotePort.
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * Sets the port of the AniDBUdpApi
	 * 
	 * @param remotePort
	 *            The remotePort to set.
	 */
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * Returns all files and dirs that should be evaluated non recursive
	 * 
	 * @return Returns the file.
	 */
	public File[] getFileArray() {
		return fileArray;
	}

	/**
	 * Sets all files and dirs that should be evaluated non recursive
	 * 
	 * @param file
	 *            The file to set.
	 */
	public void setFileArray(File[] file) {
		this.fileArray = file;
	}

	/**
	 * Returns all files and dirs that should be evaluated recursive
	 * 
	 * @return Returns the rfile.
	 */
	public File[] getRfileArray() {
		return rfileArray;
	}

	/**
	 * Sets all files and dirs that should be evaluated recursive
	 * 
	 * @param rfile
	 *            The rfile to set.
	 */
	public void setRfileArray(File[] rfile) {
		this.rfileArray = rfile;
	}

	/**
	 * Returns true if all passed files should evaluated recursive
	 * 
	 * @return Returns the recursive.
	 */
	public boolean isRecursive() {
		return recursive;
	}

	/**
	 * Sets if all files should be evaluated recursive
	 * 
	 * @param recursive
	 *            The recursive to set.
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}

	/**
	 * Returns true if regular expressions should be allowed in filenames
	 * 
	 * @return Returns the regex.
	 */
	public boolean isRegex() {
		return regex;
	}

	/**
	 * Sets if regular expressions should be allowed in filenames
	 * 
	 * @param regex
	 *            The regex to set.
	 */
	public void setRegex(boolean regex) {
		this.regex = regex;
	}

	/**
	 * Returns the list of the Strings that should be replaced in the filename
	 * 
	 * @return Returns the replaceList.
	 */
	public String[] getReplaceList() {
		return replaceList;
	}

	/**
	 * Sets the list of the Strings that should be replaced in the filename
	 * 
	 * @param replaceList
	 *            The replaceList to set.
	 */
	public void setReplaceList(String[] replaceList) {
		this.replaceList = replaceList;
	}

	/**
	 * Returns true if the gui should be started
	 * 
	 * @return Returns the gui.
	 */
	public boolean isGui() {
		return gui;
	}

	/**
	 * Sets if the gui should be started
	 * 
	 * @param gui
	 *            The gui to set.
	 */
	public void setGui(boolean gui) {
		this.gui = gui;
	}

	/**
	 * @param createAnimeFileStructure
	 *            The createAnimeFileStructure to set.
	 */
	public void setAnimeTree(boolean createAnimeFileStructure) {
		this.animeTree = createAnimeFileStructure;
	}

	/**
	 * @param moveFinishedDir
	 *            The moveFinishedDir to set.
	 */
	public void setMoveFinishedDir(File moveFinishedDir) {
		this.moveFinishedDir = moveFinishedDir;
	}

	/**
	 * @param mylistFlags
	 *            The mylistFlags to set.
	 */
	public void setMylistFlags(String[] mylistFlags) {
		this.mylistFlags = mylistFlags;
	}

	/**
	 * @return Returns the animeTree.
	 */
	public boolean isAnimeTree() {
		return animeTree;
	}

	/**
	 * @return Returns the mylistFlags.
	 */
	public String[] getMylistFlags() {
		return mylistFlags;
	}

	/**
	 * @return Returns the moveFinishedDir.
	 */
	public File getMoveFinishedDir() {
		return moveFinishedDir;
	}	
	
	/**
	 * @return Returns bool rename.
	 */
	public boolean getRename() {
		return rename;
	}	
	
	/**
	 * @return Returns bool rename.
	 */
	public void setRename(boolean newrename) {
		rename = newrename;
	}	
}
