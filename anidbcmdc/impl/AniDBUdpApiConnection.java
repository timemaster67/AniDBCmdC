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

import java.net.*;
import java.io.*;
import anidbcmdc.exceptions.*;
import anidbcmdc.util.*;

/**
 * <p>
 * Title: AniDBUdpApiConnection
 * </p>
 * <p>
 * Description: Creates a connection to the AniDBUdpApi and handles available
 * functions e.g auth, logout, addfile, ...
 * http://www.anidb.net/client/udp-api.html
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class AniDBUdpApiConnection {

	private String username, password, sessionId, client, tag;

	private int localPort, remotePort, clientVersion, protoVersion;

	private UdpConnection connection;
	
	/**
	 * default constructor is not needed here so private
	 */
	private AniDBUdpApiConnection() {
	}

	/**
	 * Constructor for the connection to the Api Sets default values if not
	 * enough were given
	 * 
	 * @param username -
	 *            The username of the AniDB user
	 * @param password -
	 *            The password of the AniDB user
	 * @param localPort -
	 *            The port on which the client should operate *
	 * @param host -
	 *            The address of the AniDBUdpApi
	 * @param remotePort -
	 *            The port of the AniDBUdpApi
	 * @throws SocketException -
	 *             Thrown if the connection to the Api failed
	 */
	public AniDBUdpApiConnection(String username, String password,
			int localPort, String host, int remotePort) throws SocketException {
		this.username = username;
		this.password = password;
		this.localPort = localPort;
		this.remotePort = remotePort;
		tag = String.valueOf(this.hashCode());
		client = "anidbcmdc";
		clientVersion = 3; // Version augmenté pour les modifications apporté à cette application
		protoVersion = 3;
		connection = new UdpConnection(localPort, host, remotePort, 2000);
	}

	/**
	 * Parses messages of the api into data, warning and checks if the api sent
	 * an error
	 * 
	 * @param replyMessage -
	 *            The String the api returned as reply on an answer
	 * @return - A String array with data at 0 and warnings at 1
	 * @throws UnknownReplyException -
	 *             Thrown if the answer of the api is unknown
	 * @throws AniDBUdpApiException -
	 *             Thrown if the api sends an errormessage
	 */
	private String[] evaluateReplyMessage(String replyMessage)
			throws UnknownReplyException, AniDBUdpApiException {
		String[] result = new String[2];
		int errorCode = 0;
		String[] s = replyMessage.split(" ");
		if (s[0].equals(tag)) { // Le tag n'est présent que si l'utilisateut est connecté, on ne peut utiliser cette méthode avant la connection
			errorCode = Integer.parseInt(s[1]);
			switch (errorCode) {
			case 200:
				result[0] = s[2];
				return result;
			case 201:
				result[0] = s[2];
				result[1] = "New version available.";
				return result;
			case 203:
				return null;
			case 210:
				return null;
			case 220:
				result[0] = replyMessage.replaceAll(tag + " 220 FILE\n", "").replaceAll("\n", ""); // La fin de ligne se termine par un newline
				return result;
			case 310:
				result[1] = replyMessage.replaceAll(tag, "");
				return result;
			case 311:
				result[1] = replyMessage.replaceAll(tag, "");
				return result;
			case 320:
				result[1] = replyMessage.replaceAll(tag, "");
				return result;
			case 403:
				return null;
			default:
				throw new AniDBUdpApiException(replyMessage.replaceAll(tag, ""));
			}
		} else
			throw new UnknownReplyException("Response from server is unknown : " + replyMessage);
			
	}

	/**
	 * Auths to the Api and returns a warning if one occured, else null
	 * 
	 * @return - Warning if the api sent one else null
	 * @throws IOException -
	 *             Thrown if the sending of the auth message failed
	 * @throws UnknownReplyException -
	 *             Thrown if the answer of the api is unknown
	 * @throws AniDBUdpApiException -
	 *             Thrown if the api sends an errormessage
	 */
	public String auth() throws IOException, UnknownReplyException,
			AniDBUdpApiException {
		String out = "AUTH user=" + username + "&pass=" + password
				+ "&protover=" + protoVersion + "&client=" + client
				+ "&clientver=" + clientVersion + "&tag=" + tag;
		String replyMessages[] = evaluateReplyMessage(connection
				.sendAndReceive(out));
		sessionId = replyMessages[0];
		return replyMessages[1];
	}

	/**
	 * Logs out of the api
	 * 
	 * @throws IOException -
	 *             Thrown if the sending of the logout message failed
	 * @throws UnknownReplyException -
	 *             Thrown if the answer of the api is unknown
	 * @throws AniDBUdpApiException -
	 *             Thrown if the api sends an errormessage
	 */
	public void logout() throws IOException, UnknownReplyException,
			AniDBUdpApiException {
		String out = "LOGOUT &s=" + sessionId + "&tag=" + tag;
		evaluateReplyMessage(connection.sendAndReceive(out));
	}

	/**
	 * Checks if file with hash and size is in the AniDB and asks for data about
	 * this file
	 * 
	 * @param ed2kHash -
	 *            Hash of the file in the AniDB
	 * @param fileSize -
	 *            Filesize of the file in the AniDB
	 * @return - New AnimeFile with AniDB data about this file
	 * @throws IOException -
	 *             If the connection to the api failed
	 * @throws UnknownReplyException -
	 *             If the answer of the api was unknown
	 * @throws AniDBUdpApiException -
	 *             If the api send an error
	 */
	public AnimeFile getAnimeFileByEd2k(String ed2kHash, long fileSize)
			throws IOException, UnknownReplyException, AniDBUdpApiException {	
		//String out = "FILE size=" + fileSize + "&ed2k=" + ed2kHash + "&s="
		//		+ sessionId + "&tag=" + tag;
		
		// http://wiki.anidb.net/w/UDP_API_Definition#FILE:_Retrieve_File_Data
		String out = "FILE size=" + fileSize + "&ed2k=" + ed2kHash + "&s="
				+ sessionId + "&tag=" + tag + "&fmask=71F8EBE0&amask=0080C040";
		
		String[] replyMessages = evaluateReplyMessage(connection
				.sendAndReceive(out));
		return replyMessages[1] == null ? new AnimeFile(replyMessages[0])
				: null;
	}

	/**
	 * Adds the AnimeFile to the users MyList Sets all available flags if
	 * possible
	 * 
	 * @param file -
	 *            The AnimeFile
	 * @return - A warning if the api sent one
	 * @throws IOException -
	 *             If the connection to the api failed
	 * @throws UnknownReplyException -
	 *             If the answer of the api was unknown
	 * @throws AniDBUdpApiException -
	 *             If the api send an error
	 */
	public String addFileToMyList(AnimeFile file, boolean onlyNew)
			throws IOException, UnknownReplyException, AniDBUdpApiException {
		String out = "MYLISTADD fid=" + file.getFileId() + "&state="
				+ file.getState() + "&source=" + file.getRipSource() + "&s="
				+ sessionId + "&tag=" + tag;
		if (file.isViewed() == true)
			out += "&viewed=1";
		if (file.getStorage() != null)
			out += "&storage=" + file.getStorage();
		if (file.getOther() != null)
			out += "&other=" + file.getOther();
		if (!onlyNew)
			out += "&edit=1";
		String[] result = evaluateReplyMessage(connection.sendAndReceive(out));
		return result != null ? result[1] : null;
	}
}
