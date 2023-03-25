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
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.artos.framework.listener.RealTimeLogEventListener;
import com.artos.interfaces.Connectable;
import com.artos.interfaces.ConnectableFilter;
import com.artos.interfaces.ConnectableMessageParser;

/**
 * This class creates TCP Client
 */
public class TCPClient implements Connectable {
	String ip;
	int nPort;
	Socket clientSocket;
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	Queue<byte[]> queue = new LinkedList<byte[]>();
	ServerTask serverTask = null;
	List<ConnectableFilter> filterList = null;
	RealTimeLogEventListener realTimeListener = null;
	ConnectableMessageParser msgParser = null;
	Transform _transform = new Transform();
	ExecutorService clientProcessingPool;

	/**
	 * Constructor
	 * 
	 * @param ip Server IP
	 * @param nPort Server Port
	 */
	public TCPClient(String ip, int nPort) {
		this.ip = ip;
		this.nPort = nPort;
		this.filterList = null;
	}

	/**
	 * Constructor. Every filter adds overheads in processing received messages which may have impact on performance
	 * 
	 * @param ip Server IP
	 * @param nPort Server Port
	 * @param msgParser {@code ConnectableMessageParser} object which contains logic to separate concatenated messages
	 * @param filterList list of filters
	 */
	public TCPClient(String ip, int nPort, ConnectableMessageParser msgParser, List<ConnectableFilter> filterList) {
		this.ip = ip;
		this.nPort = nPort;
		this.msgParser = msgParser;
		this.filterList = filterList;
	}

	/**
	 * Creates a stream socket and connects it to the specified port number on the named host.
	 * 
	 * @throws IOException if an I/O error occurs when creating the socket.
	 */
	public void connect() throws UnknownHostException, IOException {

		System.out.println("Connecting on Port : " + nPort);

		clientSocket = new Socket(ip, nPort);
		if (clientSocket.isConnected()) {
			System.out.println("Connected to " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());
		}

		// Start Reading task in parallel thread
		readFromSocket();
		notifyConnected();
	}

	/**
	 * Returns the connection state of the socket. true is returned if socket is successfully connected and has not been closed
	 */
	public boolean isConnected() {
		if (clientSocket.isConnected() && clientSocket.isBound() && !clientSocket.isClosed()) {
			return true;
		}
		return false;
	}

	/**
	 * Closes this socket. Once a socket has been closed, it is not available for further networking use (i.e. can't be reconnected or rebound). A new
	 * socket needs to be created.
	 * 
	 * @throws IOException if an I/O error occurs when closing this socket.
	 */
	public void disconnect() throws IOException {
		clientSocket.close();
		clientProcessingPool.shutdownNow();
		notifyDisconnected();
		System.out.println("Connection Closed");
	}

	/**
	 * Returns true if receive queue is not empty
	 */
	@Override
	public boolean hasNextMsg() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Get the message from the queue. With non zero timeout, function blocks until message is received or timeout has occurred. If timeout value is
	 * zero then function will block until next message is received with infinite timeout.
	 * 
	 * @param timeout timeout value
	 * @param timeunit timeunit
	 * @return byte[] from queue, null is returned if timeout has occurred
	 * @throws InterruptedException if any thread has interrupted the current thread. The interrupted status of the current thread is cleared when
	 *             this exception is thrown.
	 */
	public byte[] getNextMsg(long timeout, TimeUnit timeunit) throws InterruptedException {
		long maxAllowedTime = TimeUnit.MILLISECONDS.convert(timeout, timeunit);
		// If queue has message then return it
		if (hasNextMsg()) {
			return queue.poll();
		}
		// If queue did not have message then wait for message until timeout
		synchronized (queue) {
			queue.wait(maxAllowedTime);
		}
		// If queue has message then return it or return null
		if (hasNextMsg()) {
			return queue.poll();
		} else {
			return null;
		}
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
	 * Send data to server in String format
	 * 
	 * @param data data to be sent in String format
	 * @throws IOException if an I/O error occurs when creating the output stream or if the socket is not connected.
	 */
	public void sendMsg(String data) throws IOException {
		sendMsg(data.getBytes());
	}

	/**
	 * Send byte array to server
	 * 
	 * @throws IOException if an I/O error occurs when creating the output stream or if the socket is not connected.
	 */
	@Override
	public void sendMsg(byte[] data) throws IOException {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.write(data);
		notifySend(data);
	}

	/**
	 * Clean receive queue
	 */
	public void cleanQueue() {
		queue.clear();
	}

	private void readFromSocket() {
		clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable clientTask = new Runnable() {
			@Override
			public void run() {
				try {
					serverTask = new ServerTask(clientSocket, queue, realTimeListener, filterList, msgParser);
					clientProcessingPool.submit(serverTask);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread clientThread = new Thread(clientTask, "Artos_TCPClient_Receiver_Thread");
		clientThread.start();
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

	public Socket getConnector() {
		return clientSocket;
	}

	public BufferedReader getInFromClient() {
		return inFromServer;
	}

	public DataOutputStream getOutToClient() {
		return outToServer;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public RealTimeLogEventListener getRealTimeListener() {
		return realTimeListener;
	}

	public void setRealTimeListener(RealTimeLogEventListener realTimeListener) {
		this.realTimeListener = realTimeListener;
	}

}

/**
 * Inner Class which acts as receiver thread for incoming data. All Data will be added to the Queue
 * 
 * 
 *
 */
class ServerTask implements Runnable {

	private final Socket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;
	volatile RealTimeLogEventListener realTimeListener;
	Transform _transform = new Transform();
	volatile List<ConnectableFilter> filterList = null;
	byte[] leftOverBytes = null;
	volatile ConnectableMessageParser msgParser = null;

	ServerTask(Socket connector, Queue<byte[]> queue, RealTimeLogEventListener realTimeListener, List<ConnectableFilter> filterList,
			ConnectableMessageParser msgParser) {
		this.connector = connector;
		this.queue = queue;
		this.realTimeListener = realTimeListener;
		this.filterList = filterList;
		this.msgParser = msgParser;
	}

	@Override
	public void run() {
		try {
			while ((read = connector.getInputStream().read(buffer)) > -1) {
				readData = new byte[read];
				System.arraycopy(buffer, 0, readData, 0, read);
				if (readData.length > 0) {
					notifyReceive(readData);

					/*
					 * If user has not provided logic for msg parsing then do simple filtering
					 */
					if (null == msgParser) {
						applyFilter(readData);
					} else {
						/*
						 * If user has provided message parsing logic then assemble any left over data from previous byte[] to readData and then put
						 * it through parsing logic to separate each messages.
						 */
						if (null != leftOverBytes) {
							readData = _transform.concat(leftOverBytes, readData);
							leftOverBytes = null;
						}
						parseIncomingData(readData);
					}
				}
			}
		} catch (SocketException se) {
			// Do nothing because if connector was closed then this
			// exception is as expected
			System.out.println(se.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseIncomingData(byte[] readData) throws Exception {
		List<byte[]> msgList = msgParser.parse(readData);
		if (null != msgParser.getLeftOverBytes() && msgParser.getLeftOverBytes().length != 0) {
			leftOverBytes = msgParser.getLeftOverBytes();
		}

		for (byte[] msg : msgList) {
			applyFilter(msg);
		}
	}

	private void applyFilter(byte[] readData) throws Exception {
		if (null != filterList && !filterList.isEmpty()) {
			for (ConnectableFilter filter : filterList) {
				if (filter.meetCriteria(readData)) {
					// Do not add to queue if filter match is found
					return;
				}
			}
			queue.add(readData);

			synchronized (queue) {
				queue.notifyAll();
			}
		} else {
			queue.add(readData);
			synchronized (queue) {
				queue.notifyAll();
			}
		}
	}

	private void notifyReceive(byte[] data) {
		if (null != realTimeListener) {
			realTimeListener.receive(data);
		}
	}
}
