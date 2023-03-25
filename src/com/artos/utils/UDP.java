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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.artos.framework.listener.RealTimeLogEventListener;
import com.artos.interfaces.Connectable;
import com.artos.interfaces.ConnectableFilter;

public class UDP implements Connectable {
	int localPort;
	int remotePort;
	private final InetSocketAddress localSocketAddress;
	private final InetSocketAddress remoteSocketAddress;
	DatagramSocket serverSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	Queue<byte[]> queue = new LinkedList<byte[]>();
	Thread serverThread;
	List<ConnectableFilter> filterList = null;
	RealTimeLogEventListener realTimeListener = null;
	Transform _transform = new Transform();
	ExecutorService clientProcessingPool;

	/**
	 * Allows user to use different send and receive ports
	 * 
	 * @param localAddress Host IP
	 * @param localPort Host Port
	 * @param remoteAddress Remote IP
	 * @param remotePort Remote Port
	 */
	public UDP(String localAddress, int localPort, String remoteAddress, int remotePort) {
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.localSocketAddress = new InetSocketAddress(localAddress, localPort);
		this.remoteSocketAddress = new InetSocketAddress(remoteAddress, remotePort);
		this.filterList = null;
	}

	/**
	 * Allows user to use different send and receive ports. Every filter adds overheads in processing received messages which may have impact on
	 * performance. If filter logic takes too much time to make decision then UDP message may be dropped.
	 * 
	 * @param localAddress Host IP
	 * @param localPort Host Port
	 * @param remoteAddress Remote IP
	 * @param remotePort Remote Port
	 * @param filterList list of filters
	 */
	public UDP(String localAddress, int localPort, String remoteAddress, int remotePort, List<ConnectableFilter> filterList) {
		this.localPort = localPort;
		this.remotePort = remotePort;
		this.localSocketAddress = new InetSocketAddress(localAddress, localPort);
		this.remoteSocketAddress = new InetSocketAddress(remoteAddress, remotePort);
		this.filterList = filterList;
	}

	/**
	 * Creates a datagram socket, bound to the specified local socket address. If, if the address is null, creates an unbound socket. With Infinite
	 * socket timeout
	 * 
	 */
	public void connect() throws Exception {
		// set infinite timeout by default
		connect(0);
	}

	/**
	 * Creates a datagram socket, bound to the specified local socket address. If, if the address is null, creates an unbound socket.
	 * 
	 * With timeout option set to a non-zero, a call to receive() for this DatagramSocket will block for only this amount of time. If the timeout
	 * expires, a java.net.SocketTimeoutException is raised, though the DatagramSocket is still valid.
	 * 
	 * @param soTimeout the specified timeout in milliseconds.
	 * @throws Exception if failed to connect or create a socket
	 */
	public void connect(int soTimeout) throws Exception {
		// Start Reading task in parallel thread
		System.out.println("Listening on local port : " + localPort);
		serverSocket = new DatagramSocket(localSocketAddress);
		// infinite timeout by default
		serverSocket.setSoTimeout(soTimeout);
		readFromSocket();
		System.out.println("Remote port : " + remotePort);
		notifyConnected();
	}

	public boolean isConnected() {
		if (serverSocket.isConnected() && !serverSocket.isClosed()) {
			return true;
		}
		return false;
	}

	/**
	 * Closes this datagram socket.
	 * 
	 * Any thread currently blocked in receive upon this socket will throw a SocketException.
	 * 
	 */
	public void disconnect() {
		try {
			serverThread.interrupt();
			serverSocket.close();
			clientProcessingPool.shutdownNow();
			notifyDisconnected();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if queue is not empty
	 */
	@Override
	public boolean hasNextMsg() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Polls the queue for msg, Function will block until msg is polled from the queue or timeout has occurred. null is returned if no message
	 * received within timeout period
	 * 
	 * @param timeout msg timeout
	 * @param timeunit timeunit
	 * @return byte[] from queue, null is returned if timeout has occurred
	 * @throws InterruptedException if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when
	 *             this exception is thrown.
	 */
	public byte[] getNextMsg(long timeout, TimeUnit timeunit) throws InterruptedException {
		boolean isTimeout = false;
		long startTime = System.nanoTime();
		long finishTime;
		long maxAllowedTime = TimeUnit.NANOSECONDS.convert(timeout, timeunit);

		while (!isTimeout) {
			if (hasNextMsg()) {
				return queue.poll();
			}
			finishTime = System.nanoTime();
			if ((finishTime - startTime) > maxAllowedTime) {
				return null;
			}
			// Give system some time to do other things
			Thread.sleep(2);
		}
		return null;
	}

	/**
	 * Returns byte array from the queue, null is returned if queue is empty
	 */
	@Override
	public byte[] getNextMsg() {
		if (hasNextMsg()) {
			return queue.poll();
		}
		return null;
	}

	/**
	 * Constructs and sends datagram packet to the specified port number on the specified host. The length argument must be less than or equal to
	 * buf.length. The DatagramPacket includes information indicating the data to be sent, its length, the IP address of the remote host, and the port
	 * number on the remote host.
	 * 
	 * @param stringMsg String data
	 * @throws IOException if an I/O error occurs.
	 */
	public void sendMsg(String stringMsg) throws IOException {
		byte[] data = stringMsg.getBytes();
		sendMsg(data);
	}

	/**
	 * Constructs and sends datagram packet to the specified port number on the specified host. The length argument must be less than or equal to
	 * buf.length. The DatagramPacket includes information indicating the data to be sent, its length, the IP address of the remote host, and the port
	 * number on the remote host.
	 * 
	 * @throws IOException if an I/O error occurs.
	 */
	@Override
	public void sendMsg(byte[] data) throws IOException {
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, remoteSocketAddress);
		serverSocket.send(sendPacket);
		notifySend(data);
	}

	/**
	 * Cleans all message from the queue
	 */
	public void cleanQueue() {
		queue.clear();
	}

	private void readFromSocket() {
		clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new UDPClientTask(serverSocket, queue, realTimeListener, filterList));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

	// =================================================================================================
	// Listener Notify
	// =================================================================================================
	private void notifySend(byte[] data) {
		if (null != realTimeListener) {
			realTimeListener.send(data);
		}
	}

	private void notifyConnected() {
		if (null != realTimeListener) {
			realTimeListener.connected();
		}
	}

	private void notifyDisconnected() {
		if (null != realTimeListener) {
			realTimeListener.disConnected();
		}
	}

	// =================================================================================================
	// Getter Setter
	// =================================================================================================
	public DatagramSocket getUdpSocket() {
		return serverSocket;
	}

	public void setUdpSocket(DatagramSocket UdpSocket) {
		this.serverSocket = UdpSocket;
	}

	public BufferedReader getInFromClient() {
		return inFromClient;
	}

	public void setInFromClient(BufferedReader inFromClient) {
		this.inFromClient = inFromClient;
	}

	public DataOutputStream getOutToClient() {
		return outToClient;
	}

	public void setOutToClient(DataOutputStream outToClient) {
		this.outToClient = outToClient;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public void setQueue(Queue<byte[]> queue) {
		this.queue = queue;
	}

	public int getLocalPort() {
		return localPort;
	}

	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	public InetSocketAddress getLocalSocketAddress() {
		return localSocketAddress;
	}

	public InetSocketAddress getRemoteSocketAddress() {
		return remoteSocketAddress;
	}

	public RealTimeLogEventListener getRealTimeListener() {
		return realTimeListener;
	}

	public void setRealTimeListener(RealTimeLogEventListener realTimeListener) {
		this.realTimeListener = realTimeListener;
	}
}

class UDPClientTask implements Runnable {
	private final DatagramSocket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;
	volatile List<ConnectableFilter> filterList = null;
	volatile RealTimeLogEventListener realTimeListener;
	Transform _transform = new Transform();

	UDPClientTask(DatagramSocket connector, Queue<byte[]> queue, RealTimeLogEventListener realTimeListener) {
		this.connector = connector;
		this.queue = queue;
		this.realTimeListener = realTimeListener;
		this.filterList = null;
	}

	UDPClientTask(DatagramSocket connector, Queue<byte[]> queue, RealTimeLogEventListener realTimeListener, List<ConnectableFilter> filterList) {
		this.connector = connector;
		this.queue = queue;
		this.realTimeListener = realTimeListener;
		this.filterList = filterList;
	}

	@Override
	public void run() {
		try {

			byte[] receiveData = new byte[1024 * 4];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				connector.receive(receivePacket);
				// readData = receivePacket.getData();
				byte[] readData = new byte[receivePacket.getLength()];
				readData = Arrays.copyOfRange(receiveData, 0, receivePacket.getLength());

				if (readData.length > 0) {
					notifyReceive(readData);
					applyFilter(readData);
				}
			}
		} catch (SocketException se) {
			System.out.println("UDP socket closed");
		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println("Connection Terminated");
		}
		System.out.println("Terminating thread");
	}

	private void applyFilter(byte[] readData) {
		if (null != filterList && !filterList.isEmpty()) {
			for (ConnectableFilter filter : filterList) {
				if (filter.meetCriteria(readData)) {
					// Do not add to queue if filter match is found
					return;
				}
			}
			queue.add(readData);
		} else {
			queue.add(readData);
		}
	}

	private void notifyReceive(byte[] data) {
		if (null != realTimeListener) {
			realTimeListener.receive(data);
		}
	}
}
