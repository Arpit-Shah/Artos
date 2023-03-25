/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.utils;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.artos.framework.infra.TestContext;
import com.mindbright.jca.security.SecureRandom;
import com.mindbright.ssh2.SSH2SCP1Client;
import com.mindbright.ssh2.SSH2SimpleClient;
import com.mindbright.ssh2.SSH2Transport;
import com.mindbright.sshcommon.SSHSCP1;
import com.mindbright.util.RandomSeed;
import com.mindbright.util.SecureRandomAndPad;

public class SCP {

	private TestContext context;
	private SCPTransferDirection direction;
	private String srcFilePath;
	private String destFilePath;
	private SSH2SCP1Client scpClient;
	private String privateKeyPath;
	private SSH2Transport transport;
	private String server;
	private String username;
	private String password;
	private int port = 22;

	public enum SCPTransferDirection {
		COPY_TO_REMOTE, COPY_TO_LOCAL
	};

	/**
	 * Class Constructor
	 * 
	 * <PRE>
	 * Example:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.disconnect();
	 * </PRE>
	 * 
	 * @param context
	 *            test context
	 * @param server
	 *            server ip
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @param port
	 *            scp port number
	 */
	public SCP(TestContext context, String server, String username, String password, int port) {
		this.context = context;
		this.server = server;
		this.username = username;
		this.password = password;
		this.port = port;
		setPrivateKeyPath(null);
	}

	/**
	 * Class Constructor
	 * 
	 * <PRE>
	 * Example:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22, "./conf/ssh_key");
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.disconnect();
	 * </PRE>
	 * 
	 * @param context
	 *            test context
	 * @param server
	 *            server IP
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @param port
	 *            SCP port number
	 * @param privateKeyPath
	 *            private key location
	 */
	public SCP(TestContext context, String server, String username, String password, int port, String privateKeyPath) {
		this.context = context;
		this.server = server;
		this.username = username;
		this.password = password;
		this.port = port;
		setPrivateKeyPath(privateKeyPath);
	}

	/**
	 * Logs in to remote server.
	 * 
	 * <pre>
	 * It can use following methods :
	 * 1) User + empty password
	 * 2) User + password
	 * 3) User + PrivateKeyFromFile
	 * Example:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.disconnect();
	 * </pre>
	 * 
	 */
	public void connect() {
		SSH2SCP1Client scpClient = null;
		try {
			Socket serverSocket = new Socket(getServer(), getPort());
			setTransport(new SSH2Transport(serverSocket, createSecureRandom()));
			SSH2SimpleClient client = null;

			// If user has provided privateKeyPath, use private key
			if (null != getPrivateKeyPath()) {
				client = new SSH2SimpleClient(getTransport(), getUsername(), getPrivateKeyPath(), getPassword());
			} else {
				client = new SSH2SimpleClient(getTransport(), getUsername(), getPassword());
			}

			scpClient = new SSH2SCP1Client(new File(System.getProperty("user.dir")), client.getConnection(), System.err, false);

		} catch (Exception jSchException) {
			jSchException.printStackTrace();
		}
		setScpClient(scpClient);
	}

	/**
	 * File Transfer to remote location or from remote location to local.
	 * 
	 * <PRE>
	 * Example 1:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.disconnect();
	 * Example 2:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_LOCAL, "/etc/temp/file/temp.txt", "./files");
	 * scp.disconnect();
	 * </PRE>
	 * 
	 * @param direction
	 *            scp direction
	 * @param srcFilePath
	 *            source file path
	 * @param destFilePath
	 *            destination file path
	 * @throws IOException
	 *             when IO exception occurs
	 */
	public void fileTransfer(SCPTransferDirection direction, String srcFilePath, String destFilePath) throws IOException {
		fileTransfer(direction, srcFilePath, destFilePath, false);
	}

	/**
	 * File Transfer to remote location or from remote location to local.
	 * 
	 * <PRE>
	 * Example 1:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.disconnect();
	 * Example 2:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.connect();
	 * scp.fileTransfer(SCPTransferDirection.COPY_TO_LOCAL, "/etc/temp/file/temp.txt", "./files");
	 * scp.disconnect();
	 * </PRE>
	 * 
	 * @param direction
	 *            scp direction
	 * @param srcFilePath
	 *            source file path
	 * @param recursiveFileTransfer
	 *            recursively transfer file
	 * @param destFilePath
	 *            destination file path
	 * @throws IOException
	 *             when IO exception occurs
	 */
	public void fileTransfer(SCPTransferDirection direction, String srcFilePath, String destFilePath, boolean recursiveFileTransfer)
			throws IOException {
		setDirection(direction);
		setSrcFilePath(srcFilePath);
		setDestFilePath(destFilePath);
		context.getLogger().debug("SCP in Progress. Please Wait ...");
		context.getLogger().debug("Source : {}", getSrcFilePath());
		context.getLogger().debug("Destination : {}", getDestFilePath());
		SSHSCP1 scp = getScpClient().scp1();

		if (getDirection().equals(SCPTransferDirection.COPY_TO_REMOTE)) {
			scp.copyToRemote(getSrcFilePath(), getDestFilePath(), recursiveFileTransfer);
		} else if (getDirection().equals(SCPTransferDirection.COPY_TO_LOCAL)) {
			scp.copyToLocal(getDestFilePath(), getSrcFilePath(), recursiveFileTransfer);
		}
		context.getLogger().debug("SCP Completed");
	}

	/**
	 * 
	 */
	public void disconnect() {
		getTransport().normalDisconnect("User disconnects");
		context.getLogger().debug("SCP disconnected");
	}

	/**
	 * Create a random number generator. This implementation uses the system random
	 * device if available to generate good random numbers. Otherwise it falls back
	 * to some low-entropy garbage.
	 */
	private static SecureRandomAndPad createSecureRandom() {
		byte[] seed;
		File devRandom = new File("/dev/urandom");
		if (devRandom.exists()) {
			RandomSeed rs = new RandomSeed("/dev/urandom", "/dev/urandom");
			seed = rs.getBytesBlocking(20);
		} else {
			seed = RandomSeed.getSystemStateHash();
		}
		return new SecureRandomAndPad(new SecureRandom(seed));
	}

	private SCPTransferDirection getDirection() {
		return direction;
	}

	private void setDirection(SCPTransferDirection direction) {
		this.direction = direction;
	}

	private String getSrcFilePath() {
		return srcFilePath;
	}

	private void setSrcFilePath(String srcFilePath) {
		this.srcFilePath = srcFilePath;
	}

	private String getDestFilePath() {
		return destFilePath;
	}

	private void setDestFilePath(String destFilePath) {
		this.destFilePath = destFilePath;
	}

	public SSH2SCP1Client getScpClient() {
		return scpClient;
	}

	private void setScpClient(SSH2SCP1Client scpClient) {
		this.scpClient = scpClient;
	}

	private String getPrivateKeyPath() {
		return privateKeyPath;
	}

	private void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	private SSH2Transport getTransport() {
		return transport;
	}

	private void setTransport(SSH2Transport transport) {
		this.transport = transport;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
