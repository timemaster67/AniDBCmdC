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

package anidbcmdc.impl;

import java.io.*;
import anidbcmdc.util.*;
import jonelo.sugar.util.*;

/**
 * <p>
 * Title: AnimeFile
 * </p>
 * <p>
 * Description: holds all data an AnimeFile can have
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class AnimeFile {
    private int fileId, animeId, epId, groupId, state, animeLength;
    private long size;

    private String ed2kHash, animeName, epName, groupName, fileExtension, md5, sha1, crc, version, censored, dubLang, subLang, quality, ripSource, resolution, vidCodec, audCodec, storage, other,
            epNr;

    private boolean viewed;

    /**
     * The default constructor is not needed here so private
     */
    private AnimeFile() {
    }

    /**
     * Creates a new AnimeFile and parses the data from a String the api sent
     * and trys to get more data out of the AniDB website
     * 
     * @param apiReply -
     *            The data the api sent
     * @throws IOException -
     *             If the connection to the website failed while we tried to get
     *             more data
     */
    public AnimeFile(String apiReply) throws IOException {
        String[] reply = apiReply.split("\\|");      
        //fmask
        //bit 1
        fileId = Integer.parseInt(reply[0]);
        animeId = Integer.parseInt(reply[1]);
        epId = Integer.parseInt(reply[2]);
        groupId = Integer.parseInt(reply[3]);
        state = Integer.parseInt(reply[4]);
        //bit 2
        size = Long.parseLong(reply[5]);
        ed2kHash = reply[6];
        md5 = reply[7];
		sha1 = reply[8];
		crc = reply[9];
		//bit 3
		quality = reply[10];	
		ripSource = reply[11];
		audCodec = reply[12];
		vidCodec = reply[13];
		resolution = reply[14];
		fileExtension = reply[15];
		//bit 4
		dubLang = reply[16];
		subLang = reply[17];
		animeLength = Integer.parseInt(reply[18]);
		
		
		/*
		// try if epNr is an Integer
		String animeLengthAsString = "";
		try {
			Integer.parseInt(epNr);
			if (animeLengthAsString != null) {
				animeLength = Integer.parseInt(animeLengthAsString);
				while (epNr.length() < animeLengthAsString.length()) {
					epNr = 0 + epNr;
				}
			}
		} catch (NumberFormatException e) {
		}
		*/
		
		//amask
		//bit 1
		//bit 2
	    animeName = reply[19]; // mauvais
	    //bit 3
        epNr = reply[20];
        epName = reply[21];
        //bit 4
        groupName = reply[22];
        
		/*
        String[] tempFilename = reply[7].split(" - ");
        animeName = tempFilename[0];
        epNr = tempFilename[1];
        epName = tempFilename[2];
        int epNameIndex = apiReply.indexOf(epName);
        int groupIndex = apiReply.indexOf("[");     
        epName = apiReply.substring(epNameIndex, groupIndex - 3);
        String rest = apiReply.substring(groupIndex + 1, apiReply.length() - 1);
        int groupEndIndex = rest.indexOf("]");
        groupName = rest.substring(0, groupEndIndex);
        */
               
        //Ne sont pas disponible par le upd api, avant ils était extraite de la réponse web      
		version = "";
		censored = "";

		
		
        //parseAniDBWebsite();
        /* Ne marche plus, car la réponse est en xml au lieu d'html
         Mais grâce au udp api amélioré, je devrais être capable de récupérer les valeurs 
         récupéré avant dans la requête http.
         */
    }

    /**
     * Parses the source of the AniDB website of the file for additional data
     * our file could need (deletes spaces out of the website and searches for
     * patterns
     */
    /*
private void parseAniDBWebsite() throws IOException {
		String episodeWebsiteSource = WebsiteParser.websiteToString(
				"http://anidb.info/perl-bin/animedb.pl?show=file&aid="
						+ animeId + "&eid=" + epId + "&fid=" + fileId
						+ "&nonav=1", -1);
		String animeWebsiteSource = WebsiteParser.websiteToString(
				"http://anidb.info/perl-bin/animedb.pl?show=anime&aid="
						+ animeId, 200);
		episodeWebsiteSource = episodeWebsiteSource.replaceAll("\\s", "");
		animeWebsiteSource = animeWebsiteSource.replaceAll("\\s", "");
		String parseStringStart_md5 = "<tr><td>MD5Sum:</td><td>";
		String parseStringEnd_md5 = "</td></tr>";
		String parseStringStart_sha1 = "<tr><td>SHA1Sum:</td><td>";
		String parseStringEnd_sha1 = "</td></tr>";
		String parseStringStart_crc = "<tr><td>CRCSum:</td><td>";
		String parseStringEnd_crc = "</td></tr>";		
		String parseStringStart_version = "<tr><td>FileVersion:</td><td>";
		String parseStringEnd_version = "<br/><i>";
		String parseStringStart_censored = "<tr><td>Censored:</td><td>";
		String parseStringEnd_censored = "</td></tr>";
		String parseStringStart_dubLang = "<tr><td>Lang:</td><td>";
		String parseStringEnd_dubLang = "</td></tr>";
		String parseStringStart_subLang = "<tr><td>SubLang:</td><td>";
		String parseStringEnd_subLang = "</td></tr>";
		String parseStringStart_quality = "<tr><td>Quality:</td><td>";
		String parseStringEnd_quality = "</td></tr>";
		String parseStringStart_ripSource = "<tr><td>Source:</td><td>";
		String parseStringEnd_ripSource = "</td></tr>";
		String parseStringStart_resolution = "<tr><td>Resolution:</td><td>";
		String parseStringEnd_resolution = "</td></tr>";
		String parseStringStart_vidCodec = "<tr><td>VideoCodec:</td><td>";
		String parseStringEnd_vidCodec = "</td></tr>";
		String parseStringStart_audCodec = "<tr><td>AudioCodec:</td><td>";
		String parseStringEnd_audCodec = "</td></tr>";
		String parseStringStart_animeLength = "<tr><td>Episodes:</td><td>";
		String parseStringEnd_animeLength = "</td></tr>";
		md5 = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_md5, parseStringEnd_md5);
		sha1 = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_sha1, parseStringEnd_sha1);
		crc = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_crc, parseStringEnd_crc);
		version = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_version, parseStringEnd_version);
		version = version.replaceAll("-.*", "");
		censored = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_censored, parseStringEnd_censored);
		censored = censored.replaceAll("/doesnotapply", "");
		dubLang = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_dubLang, parseStringEnd_dubLang);
		if (dubLang != null)
			dubLang = dubLang.substring(0, 3);
		subLang = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_subLang, parseStringEnd_subLang);
		if (dubLang != null)
			subLang = subLang.substring(0, 3);
		quality = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_quality, parseStringEnd_quality);
		ripSource = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_ripSource, parseStringEnd_ripSource);
		resolution = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_resolution, parseStringEnd_resolution);
		vidCodec = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_vidCodec, parseStringEnd_vidCodec);
		vidCodec = vidCodec.replaceAll("Bitrate:|/", "");
		audCodec = WebsiteParser.findDataInWebsite(episodeWebsiteSource,
				parseStringStart_audCodec, parseStringEnd_audCodec);
		audCodec = audCodec.replaceAll("Bitrate:|/", "");
		String animeLengthAsString = WebsiteParser.findDataInWebsite(
				animeWebsiteSource, parseStringStart_animeLength,
				parseStringEnd_animeLength);
		// try if epNr is an Integer
		try {
			Integer.parseInt(epNr);
			if (animeLengthAsString != null) {
				animeLength = Integer.parseInt(animeLengthAsString);
				while (epNr.length() < animeLengthAsString.length()) {
					epNr = 0 + epNr;
				}
			}
		} catch (NumberFormatException e) {
		}
	}*/
    /**
     * Creates a new filename out of the style the user has chosen for more info
     * check --help
     * 
     * @param style -
     *            The style the user has created
     * @return - A filename in the form of the supplied style
     */
    public String createFilename(String style) {
        style = style.trim();
        String separatorAnime = style.substring(style.indexOf("%animenamepart1") + 15, style.indexOf("%animenamepart2"));
        String separatorEp = style.substring(style.indexOf("%epnamepart1") + 12, style.indexOf("%epnamepart2"));
        animeName = animeName.replaceAll(" ", separatorAnime);
        epName = epName.replaceAll(" ", separatorEp);
        style = style.replaceFirst("%animenamepart1.*%animenamepart2", animeName);
        style = style.replaceFirst("%epnamepart1.*%epnamepart2", epName);
        style = GeneralString.replaceAllStrings(style, "%epnr", String.valueOf(epNr));
        style = GeneralString.replaceAllStrings(style, "%group", groupName);
        style = GeneralString.replaceAllStrings(style, "%md5", md5);
        style = GeneralString.replaceAllStrings(style, "%sha1", sha1);
        style = GeneralString.replaceAllStrings(style, "%ed2k", ed2kHash);
        style = GeneralString.replaceAllStrings(style, "%crc", crc);
        style = GeneralString.replaceAllStrings(style, "%CRC", crc.toUpperCase());
        style = GeneralString.replaceAllStrings(style, "%version", version);
        style = GeneralString.replaceAllStrings(style, "%censored", censored);
        style = GeneralString.replaceAllStrings(style, "%dublang", dubLang);
        style = GeneralString.replaceAllStrings(style, "%sublang", subLang);
        style = GeneralString.replaceAllStrings(style, "%quality", quality);
        style = GeneralString.replaceAllStrings(style, "%ripsource", ripSource);
        style = GeneralString.replaceAllStrings(style, "%resolution", resolution);
        style = GeneralString.replaceAllStrings(style, "%vidcodec", vidCodec);
        style = GeneralString.replaceAllStrings(style, "%audCodec", audCodec);
        return style += "." + fileExtension;
    }

    /**
     * Returns the AniDB fileId of this file
     * 
     * @return Returns the fileId.
     */
    public int getFileId() {
        return fileId;
    }

    /**
     * Returns the optional episode tag
     * 
     * @return Returns the other.
     */
    public String getOther() {
        return other;
    }

    /**
     * Returns the state of this AniDB file
     * 
     * @return Returns the state.
     */
    public int getState() {
        return state;
    }

    /**
     * Returns the storage this file is currently saved on
     * 
     * @return Returns the storage.
     */
    public String getStorage() {
        return storage;
    }

    /**
     * Return if this AniDB file has been viewed
     * 
     * @return Returns the viewed.
     */
    public boolean isViewed() {
        return viewed;
    }

    /**
     * Sets this AniDB file viewed
     * 
     * @param viewed
     *            The viewed to set.
     */
    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    /**
     * Returns the medium this AniDB was ripped off
     * 
     * @return Returns the ripSource.
     */
    public String getRipSource() {
        return ripSource;
    }

    /**
     * Sets the state this AniDB file is in
     * 
     * @param state
     *            The state to set.
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Set the medium this AniDB file is saved on
     * 
     * @param storage
     *            The storage to set.
     */
    public void setStorage(String storage) {
        this.storage = storage;
    }

    /**
     * @param other
     *            The other to set.
     */
    public void setOther(String other) {
        this.other = other;
    }

    /**
     * @return Returns the animeName.
     */
    public String getAnimeName() {
        return animeName;
    }
}
