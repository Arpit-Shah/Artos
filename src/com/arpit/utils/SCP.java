package com.arpit.utils;

import java.io.File;
import java.net.Socket;

import com.arpit.infra.TestContext;
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
	 * 
	 * <PRE>
	 * Example:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.Connect();
	 * scp.SCPFileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.Disconnect();
	 * </PRE>
	 * 
	 * @param context
	 * @param server
	 * @param username
	 * @param password
	 * @param port
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
	 * Creates SCP Object
	 * 
	 * <PRE>
	 * Example:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22, "./conf/ssh_key");
	 * scp.Connect();
	 * scp.SCPFileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.Disconnect();
	 * </PRE>
	 * 
	 * @param context
	 * @param server
	 * @param username
	 * @param password
	 * @param port
	 * @param privateKeyPath
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
	 * scp.Connect();
	 * scp.SCPFileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.Disconnect();
	 * </pre>
	 * 
	 * @param server
	 * @param user
	 * @param password
	 * @param port
	 * @return
	 */
	public SSH2SCP1Client Connect() {
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
		return getScpClient();
	}

	/**
	 * File Transfer to remote location or from remote location to local.
	 * 
	 * <PRE>
	 * Example 1:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.Connect();
	 * scp.SCPFileTransfer(SCPTransferDirection.COPY_TO_REMOTE, "/files/temp.txt", "/etc/temp/file");
	 * scp.Disconnect();
	 * Example 2:
	 * SCP scp = new SCP(context, "192.168.1.100", "root", "", 22);
	 * scp.Connect();
	 * scp.SCPFileTransfer(SCPTransferDirection.COPY_TO_LOCAL, "/etc/temp/file/temp.txt", "./files");
	 * scp.Disconnect();
	 * </PRE>
	 * 
	 * @param context
	 * @param direction
	 * @param strSourceFilePath
	 * @param strDestinationFilePath
	 * @param strHost
	 * @throws Exception
	 */
	public void SCPFileTransfer(SCPTransferDirection direction, String srcFilePath, String destFilePath) {
		try {
			setDirection(direction);
			setSrcFilePath(srcFilePath);
			setDestFilePath(destFilePath);
			context.getLogger().println("SCP in Progress. Please Wait ...");
			context.getLogger().println("Source :" + getSrcFilePath());
			context.getLogger().println("Destination :" + getDestFilePath());
			SSHSCP1 scp = getScpClient().scp1();

			if (getDirection().equals(SCPTransferDirection.COPY_TO_REMOTE)) {
				scp.copyToRemote(getSrcFilePath(), getDestFilePath(), false);
			} else if (getDirection().equals(SCPTransferDirection.COPY_TO_LOCAL)) {
				scp.copyToLocal(getSrcFilePath(), getDestFilePath(), false);
			}
			context.getLogger().println("SCP Completed");
		} catch (Exception e) {
			context.getLogger().println("SCP Failed");
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void Disconnect() {
		getTransport().normalDisconnect("User disconnects");
		context.getLogger().println("SCP disconnected");
	}

	/**
	 * Create a random number generator. This implementation uses the system
	 * random device if available to generate good random numbers. Otherwise it
	 * falls back to some low-entropy garbage.
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

	private SSH2SCP1Client getScpClient() {
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
