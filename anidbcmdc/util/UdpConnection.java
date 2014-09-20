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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * <p>
 * Title: UdpConnection
 * </p>
 * <p>
 * Description: Creates a connection with an udp server and sends and receives
 * packets.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * 
 * @author ExElNeT
 * @version 1.0
 */
public class UdpConnection {

	private DatagramSocket sock;

	private int localPort;

	private int remotePort;

	private String host;

	private int floodProtection;

	private static long lastSent = 0;

	/**
	 * Creates an new connections and opens a socket with the given params.
	 * 
	 * @param localPort
	 *            - The port this connection should send and listen on.
	 * @param host
	 *            - The target of the connection. The remote host.
	 * @param remotePort
	 *            - The port of the target of this connection.
	 * @param floodProtection
	 *            - The time between sending messages (globally between all
	 *            connections and threads)
	 * @throws SocketException
	 *             - If the connection to the host failed.
	 */
	public UdpConnection(int localPort, String host, int remotePort,
			int floodProtection) throws SocketException {
		this.localPort = localPort;
		this.host = host;
		this.remotePort = remotePort;
		this.floodProtection = floodProtection;
		if (lastSent == 0)
			lastSent = System.currentTimeMillis();
		sock = new DatagramSocket(localPort);
		sock.setSoTimeout(floodProtection); //Ajoute une valeur de timeout en milliseconde lors de l'attente de réponse du serveur
		// Si le timeout est dépassé par la méthode read, l'exception SocketTimeoutException est envoyé.
		// Je met la valeur de timeout à la même valeur que le floodProtection, car de toute façon, il faudra attendre pour réenvoyer. Ce temps perdu
		// à attendre pour réenvoyer m'enpêche d'attentre la réponse.
	}

	/**
	 * Sends a String with data to the remote host and waits for an answer.
	 * 
	 * @param message
	 *            - The message to send.
	 * @return - The reply from the remote host.
	 * @throws IOException
	 *             - If the connection to the remote host failed.
	 */
	public String sendAndReceive(String message) throws IOException {

		synchronized (getClass()) {

			String returnvalue = "";
			char TryCount = 0;
			long timePassed;
			while ((TryCount < 3) && (returnvalue.equals(""))) {

				timePassed = System.currentTimeMillis() - lastSent;
				if (timePassed > floodProtection)
					sendData(message);
				else {
					try {
						Thread.sleep(floodProtection - timePassed);
						sendData(message);
						lastSent = System.currentTimeMillis();
					} catch (InterruptedException e) {
						System.out.println(e);
					}
				}

				try {
					returnvalue =  receiveData();
				} catch (SocketTimeoutException e) {
					// Le timeout de réception à été dépassé, probablement parce
					// que le packet udp s'est perdu.
					// on incrémente TryCount et on réessaie
					TryCount++;
				} catch (IOException e) {
					// Erreur générale de réseau. On laisse la gestion au parent
					throw e;
				}

			}
	
			if (TryCount >= 3) {
				// Si on a essayé à trois reprise l'envoie et la réception et
				// que ça ne fonctionne toujours pas,
				// On relache une exception générale qui sera géré par le
				// parent.
				throw new IOException();
			}

			return returnvalue;
		}
		

	}

	/**
	 * Sends a String with data to the remote host.
	 * 
	 * @param out
	 *            - The String with data that should be send.
	 * @throws UnknownHostException
	 *             - If the host is unknown.
	 * @throws IOException
	 *             - If the connection to the remote host failed.
	 */
	private void sendData(String out) throws UnknownHostException, IOException {
		InetAddress remoteAddr = InetAddress.getByName(host);
		byte outData[] = out.getBytes();
		DatagramPacket packet = new DatagramPacket(outData, outData.length,
				remoteAddr, remotePort);
		sock.send(packet);
	}

	/**
	 * Waits for string with data from the host.
	 * 
	 * @return - The String with data the host sent.
	 * @throws IOException
	 *             - If the connection to the remote host failed.
	 */
	private String receiveData() throws IOException {
		DatagramPacket packet = new DatagramPacket(new byte[1500], 1500);
		sock.receive(packet);
		byte inData[] = packet.getData();
		return new String(inData, 0, packet.getLength());
	}
}
