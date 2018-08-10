// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
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

/**
 * This class creates TCP Client
 * 
 * 
 *
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
	Transform _transform = new Transform();

	/**
	 * Constructor
	 * 
	 * @param ip
	 *            Server IP
	 * @param nPort
	 *            Server Port
	 */
	public TCPClient(String ip, int nPort) {
		this.ip = ip;
		this.nPort = nPort;
		this.filterList = null;
	}

	/**
	 * Constructor. Every filter adds overheads in processing received messages
	 * which may have impact on performance
	 * 
	 * @param ip
	 *            Server IP
	 * @param nPort
	 *            Server Port
	 * @param filterList
	 *            list of filters
	 */
	public TCPClient(String ip, int nPort, List<ConnectableFilter> filterList) {
		this.ip = ip;
		this.nPort = nPort;
		this.filterList = null;
	}

	/**
	 * Creates a stream socket and connects it to the specified port number on the
	 * named host.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs when creating the socket.
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
	 * Returns the connection state of the socket. true is returned if socket is
	 * successfully connected and has not been closed
	 */
	public boolean isConnected() {
		if (clientSocket.isConnected() && clientSocket.isBound() && !clientSocket.isClosed()) {
			return true;
		}
		return false;
	}

	/**
	 * Closes this socket. Once a socket has been closed, it is not available for
	 * further networking use (i.e. can't be reconnected or rebound). A new socket
	 * needs to be created.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs when closing this socket.
	 */
	public void disconnect() throws IOException {
		clientSocket.close();
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
	 * Polls the queue for msg, Function will block until msg is polled from the
	 * queue or timeout has occurred. null is returned if no message received within
	 * timeout period
	 * 
	 * @param timeout
	 *            msg timeout
	 * @param timeunit
	 *            timeunit
	 * @return byte[] from queue, null is returned if timeout has occurred
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread. The interrupted
	 *             status of the current thread is cleared when this exception is
	 *             thrown.
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
			Thread.sleep(20);
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
	 * Send data to server in String format
	 * 
	 * @param data
	 *            data to be sent in String format
	 * @throws IOException
	 *             if an I/O error occurs when creating the output stream or if the
	 *             socket is not connected.
	 */
	public void sendMsg(String data) throws IOException {
		sendMsg(data.getBytes());
	}

	/**
	 * Send byte array to server
	 * 
	 * @throws IOException
	 *             if an I/O error occurs when creating the output stream or if the
	 *             socket is not connected.
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
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable clientTask = new Runnable() {
			@Override
			public void run() {
				try {
					serverTask = new ServerTask(clientSocket, queue, realTimeListener, filterList);
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
 * Inner Class which acts as receiver thread for incoming data. All Data will be
 * added to the Queue
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

	ServerTask(Socket connector, Queue<byte[]> queue, RealTimeLogEventListener realTimeListener) {
		this.connector = connector;
		this.queue = queue;
		this.realTimeListener = realTimeListener;
		this.filterList = null;
	}

	ServerTask(Socket connector, Queue<byte[]> queue, RealTimeLogEventListener realTimeListener, List<ConnectableFilter> filterList) {
		this.connector = connector;
		this.queue = queue;
		this.realTimeListener = realTimeListener;
		this.filterList = filterList;
	}

	@Override
	public void run() {
		try {
			while ((read = connector.getInputStream().read(buffer)) > -1) {
				readData = new byte[read];
				System.arraycopy(buffer, 0, readData, 0, read);
				if (readData.length > 0) {
					notifyReceive(readData);
					applyFilter(readData);
				}
			}
		} catch (Exception e) {
			if (connector.isClosed() && e.getMessage().contains("Socket closed")) {
				// Do nothing because if connector was closed then this
				// exception is as expected
			} else {
				e.printStackTrace();
			}
		}
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
