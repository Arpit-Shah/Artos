package com.arpitos.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.arpitos.interfaces.Connector;

public class TCPServer implements Connector {

	int nPort;
	ServerSocket tcpSocket;
	Socket serverSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	Queue<byte[]> queue = new LinkedList<byte[]>();

	public TCPServer(int nPort) {
		this.nPort = nPort;
	}

	public void connect() throws Exception {
		System.out.println("Listening on Port : " + nPort);

		tcpSocket = new ServerSocket(nPort);
		serverSocket = tcpSocket.accept();
		if (serverSocket.isConnected()) {
			System.out.println("Connected to " + serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getLocalPort());
		}

		// Start Reading task in parallel thread
		readFromSocket();
	}

	public boolean isConnected() {
		return serverSocket.isConnected();
	}

	public void disconnect() throws Exception {
		serverSocket.close();
		tcpSocket.close();
		System.out.println("Connection Closed");
	}

	public void sendData(String data) throws Exception {
		outToClient = new DataOutputStream(serverSocket.getOutputStream());
		outToClient.writeBytes(data);
	}

	public void sendData(byte[] data) throws Exception {
		outToClient = new DataOutputStream(serverSocket.getOutputStream());
		outToClient.write(data);
	}

	public byte[] recieveData() {
		return getNextMSG();
	}

	public boolean hasNextMSG() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	public byte[] getNextMSG() {
		if (hasNextMSG()) {
			return queue.poll();
		}
		return null;
	}

	public byte[] getNextMSG(long Timeout, TimeUnit timeunit) throws Exception {
		boolean isTimeout = false;
		long startTime = System.nanoTime();
		long finishTime;
		long maxAllowedTime = TimeUnit.NANOSECONDS.convert(Timeout, timeunit);

		while (!isTimeout) {
			if (hasNextMSG()) {
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

	private void readFromSocket() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new ClientTask(serverSocket, queue));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public ServerSocket getTcpSocket() {
		return tcpSocket;
	}

	public Socket getConnector() {
		return serverSocket;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public void cleanQueue() {
		queue.clear();
	}

}

/**
 * Inner Class which acts as receiver thread for incoming data. All Data will be
 * added to the Queue
 * 
 * @author arpit
 *
 */
class ClientTask implements Runnable {

	private final Socket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;

	ClientTask(Socket connector, Queue<byte[]> queue) {
		this.connector = connector;
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			while ((read = connector.getInputStream().read(buffer)) > -1) {
				readData = new byte[read];
				System.arraycopy(buffer, 0, readData, 0, read);
				if (readData.length > 0) {
					queue.add(readData);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
