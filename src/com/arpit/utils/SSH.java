package com.arpit.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.PrintStream;

import com.arpit.infra.OrganisedLog.LOG_LEVEL;
import com.arpit.infra.TestContext;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSH {

	TestContext context;
	Session ssh = null;
	Channel channel = null;
	private InputStream in;
	private PrintStream out;
	private int portNumber = 22;
	private String privateKeyPath;
	private String hostIP;
	private String username;
	private String password;

	/**
	 * SSH object
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param context
	 * @param hostIP
	 * @param username
	 * @param password
	 * @param portNumber
	 */
	public SSH(TestContext context, String hostIP, String username, String password, int portNumber) {
		this.context = context;
		this.hostIP = hostIP;
		this.username = username;
		this.password = password;
		this.portNumber = portNumber;
		this.privateKeyPath = null;
	}

	/**
	 * SSH object
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param context
	 * @param hostIP
	 * @param username
	 * @param password
	 * @param portNumber
	 * @param privateKeyPath
	 */
	public SSH(TestContext context, String hostIP, String username, String password, int portNumber, String privateKeyPath) {
		this.context = context;
		this.hostIP = hostIP;
		this.username = username;
		this.password = password;
		this.portNumber = portNumber;
		this.privateKeyPath = privateKeyPath;
	}

	/**
	 * SSH connection
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 */
	public void Connect() {
		try {

			if (getSsh() != null && getSsh().isConnected()) {
				getSsh().disconnect();
			}
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}

			JSch jsch = new JSch();
			if (null != getPrivateKeyPath()) {
				jsch.addIdentity(getPrivateKeyPath());
			}
			setSsh(jsch.getSession(getUsername(), getHostIP(), getPortNumber()));
			getSsh().setConfig("StrictHostKeyChecking", "no");
			getSsh().setPassword(getPassword());
			getSsh().connect();
			// channel = ssh.openChannel("exec");
			setChannel(getSsh().openChannel("shell"));

			// Get input and output stream references
			getChannel().setInputStream(null);
			in = new BufferedInputStream(getChannel().getInputStream());
			out = new PrintStream(getChannel().getOutputStream());
			// ((ChannelExec) getChannel()).setErrStream(System.err);

			// Filter out all escape char
			// ((ChannelShell) getChannel()).setPty(false);
			getChannel().connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return getSsh().isConnected();
	}

	/**
	 * Write shell commands
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param value
	 * @throws Exception
	 */
	public void Write(String value) throws Exception {
		/*
		 * Do not use out.println(LOG_LEVEL.INFO, value) because it is
		 * considered as two enter in unix environment, then one extra line will
		 * be returned which is not expected
		 */
		out.print(value + "\n");
		out.flush();
	}

	/**
	 * Writes and read pattern in same command
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100", "#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param value
	 * @param pattern
	 * @param longTimeoutMilliseconds
	 * @return
	 * @throws Exception
	 */
	public String Write(String value, String pattern, long longTimeoutMilliseconds) throws Exception {
		/*
		 * Do not use out.println(LOG_LEVEL.INFO, value) because it is
		 * considered as two enter in unix environment, then one extra line will
		 * be returned which is not expected
		 */
		out.print(value + "\n");
		out.flush();
		return ReadUntil(pattern, longTimeoutMilliseconds);
	}

	/**
	 * Reads shell commands until pattern match is received
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123");
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	public String ReadUntil(String pattern) throws Exception {
		char lastChar = pattern.charAt(pattern.length() - 1);
		StringBuffer sb = new StringBuffer();

		// Run Until Pattern match
		while (true) {
			if (in.available() >= 1) {
				char ch = (char) in.read();
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
			} else {
				Thread.sleep(10);
			}
			if (getChannel().isClosed()) {
				if (in.available() > 0) {
					continue;
				} else {
					context.getLogger().println(LOG_LEVEL.INFO, "exit-status: " + getChannel().getExitStatus());
					break;
				}
			}
		}
		context.getLogger().println(LOG_LEVEL.INFO, "*************************************************");
		context.getLogger().println(LOG_LEVEL.INFO, "Pattern match could not be found");
		context.getLogger().println(LOG_LEVEL.INFO, "*************************************************");
		return sb.toString();
	}

	/**
	 * Reads shell commands until pattern match is received or timeout is met
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil("#123", 2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param pattern
	 * @param longTimeoutMilliseconds
	 * @return
	 * @throws Exception
	 */
	public String ReadUntil(String pattern, long longTimeoutMilliseconds) throws Exception {
		char lastChar = pattern.charAt(pattern.length() - 1);
		StringBuffer sb = new StringBuffer();
		long starttime = System.currentTimeMillis();
		char ch;

		sb.append("\n---------------------------\n");
		while ((System.currentTimeMillis() - starttime) < longTimeoutMilliseconds) {
			if (in.available() >= 1) {
				ch = (char) in.read();
				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						if ((System.currentTimeMillis() - starttime) >= longTimeoutMilliseconds) {
							break;
						}
						// add end
						sb.append("\n---------------------------\n");
						return sb.toString();
					}
				}
			} else {
				Thread.sleep(10);
			}
			if (channel.isClosed()) {
				if (in.available() > 0) {
					continue;
				} else {
					context.getLogger().println(LOG_LEVEL.INFO, "exit-status: " + channel.getExitStatus());
					break;
				}
			}
		}
		context.getLogger().println(LOG_LEVEL.INFO, "*************************************************");
		context.getLogger().println(LOG_LEVEL.INFO, "Timed out before Pattern match could not be found");
		context.getLogger().println(LOG_LEVEL.INFO, "*************************************************");
		return sb.toString();
	}

	/**
	 * Reads shell commands until timeout is met
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.Connect();
	 * ssh.Write("ping 192.168.3.100");
	 * ssh.ReadUntil(2000);
	 * ssh.Disconnect();
	 * </pre>
	 * 
	 * @param pattern
	 * @param longTimeoutMilliseconds
	 * @return
	 * @throws Exception
	 */
	public String ReadUntil(long longTimeoutMilliseconds) throws Exception {

		StringBuffer sb = new StringBuffer();
		int readcount = 0;
		long starttime = System.currentTimeMillis();
		char ch;
		// add start
		sb.append("\n---------------------------\n");
		// Run Until Timeout occurs
		while ((System.currentTimeMillis() - starttime) < longTimeoutMilliseconds) {
			if (in.available() >= 1) {
				ch = (char) in.read();
				sb.append(ch);
				readcount++;
			} else {
				Thread.sleep(10);
			}
			if (channel.isClosed()) {
				if (in.available() > 0) {
					continue;
				} else {
					context.getLogger().println(LOG_LEVEL.INFO, "exit-status: " + channel.getExitStatus());
					break;
				}
			}
		}
		if (readcount > 0) {
			// add end
			sb.append("\n---------------------------\n");
			return sb.toString();
		} else {
			return null;
		}
	}

	/**
	 * Disconnects SSH session
	 * 
	 * @throws Exception
	 */
	public void Disconnect() throws Exception {
		getChannel().disconnect();
		getSsh().disconnect();
	}

	public Session getSsh() {
		return ssh;
	}

	private void setSsh(Session ssh) {
		this.ssh = ssh;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getHostIP() {
		return hostIP;
	}

	public void setHostIP(String hostIP) {
		this.hostIP = hostIP;
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
}
