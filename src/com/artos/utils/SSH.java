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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.artos.framework.infra.TestContext;
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
	 * Class Constructor
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param context
	 *            test context
	 * @param hostIP
	 *            host ip address
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @param portNumber
	 *            SSH port number
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
	 * Class Constructor
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param context
	 *            test context
	 * @param hostIP
	 *            host IP address
	 * @param username
	 *            username
	 * @param password
	 *            password
	 * @param portNumber
	 *            SSH port number
	 * @param privateKeyPath
	 *            private key file path
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
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 */
	public void connect() {
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
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param value
	 *            data to write
	 * @throws Exception
	 *             if can not flush data
	 */
	public void write(String value) throws Exception {
		/*
		 * Do not use out.println(LOG_LEVEL.INFO, value) because it is considered as two
		 * enter in unix environment, then one extra line will be returned which is not
		 * expected
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
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100", "#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param value
	 *            write value
	 * @param pattern
	 *            pattern to look for in received msg
	 * @param longTimeoutMilliseconds
	 *            timeout value
	 * @return String response
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The interrupted
	 *             status of the current thread is cleared when this exception is
	 *             thrown.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public String write(String value, String pattern, long longTimeoutMilliseconds) throws IOException, InterruptedException {
		/*
		 * Do not use out.println(LOG_LEVEL.INFO, value) because it is considered as two
		 * enter in unix environment, then one extra line will be returned which is not
		 * expected
		 */
		out.print(value + "\n");
		out.flush();
		return readUntil(pattern, longTimeoutMilliseconds);
	}

	/**
	 * Reads shell commands until pattern match is received
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123");
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param pattern
	 *            pattern to look for in received msg
	 * @return returns string data read from console up until pattern match is found
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The interrupted
	 *             status of the current thread is cleared when this exception is
	 *             thrown.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public String readUntil(String pattern) throws IOException, InterruptedException {
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
					context.getLogger().debug("exit-status: {}", getChannel().getExitStatus());
					break;
				}
			}
		}
		context.getLogger().debug("*************************************************" + "\nPattern match could not be found"
				+ "\n*************************************************");
		return sb.toString();
	}

	/**
	 * Reads shell commands until pattern match is received or timeout is met
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil("#123", 2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param pattern
	 *            pattern to look for in received msg
	 * @param longTimeoutMilliseconds
	 *            timeout value
	 * @return returns string data which are read from console within provided
	 *         timeout
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The interrupted
	 *             status of the current thread is cleared when this exception is
	 *             thrown
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public String readUntil(String pattern, long longTimeoutMilliseconds) throws IOException, InterruptedException {
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
					} else {
						if ((System.currentTimeMillis() - starttime) >= longTimeoutMilliseconds) {
							context.getLogger().debug(
									"*************************************************\nTimed out before Pattern match could be found\n*************************************************");
							break;
						}
					}
				}
			} else {
				Thread.sleep(10);
			}
			if (channel.isClosed()) {
				if (in.available() > 0) {
					continue;
				} else {
					context.getLogger().debug("exit-status: {}", channel.getExitStatus());
					break;
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Reads shell commands until timeout is met
	 * 
	 * <pre>
	 * Example:
	 * SSH ssh = new SSH(context, "192.168.1.100", "root", "1234", 22);
	 * ssh.connect();
	 * ssh.write("ping 192.168.3.100");
	 * ssh.readUntil(2000);
	 * ssh.disconnect();
	 * </pre>
	 * 
	 * @param longTimeoutMilliseconds
	 *            timeout
	 * @return String formatted received data
	 * @throws Exception
	 *             if anything failed
	 */
	public String readUntil(long longTimeoutMilliseconds) throws Exception {

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
					context.getLogger().debug("exit-status: {}", channel.getExitStatus());
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
	 *             if SSH failed to disconnect
	 */
	public void disconnect() throws Exception {
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
