package com.arpit.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.arpit.interfaces.Connector;

public class TCPClient implements Connector {

	String ip;
	int nPort;
	Socket clientSocket;
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	Queue<byte[]> queue = new LinkedList<byte[]>();

	public TCPClient(String ip, int nPort) {
		this.ip = ip;
		this.nPort = nPort;
	}

	public void connect() throws Exception {

		System.out.println("Connecting on Port : " + nPort);

		clientSocket = new Socket(ip, nPort);
		if (clientSocket.isConnected()) {
			System.out.println("Connected to " + ip + ":" + nPort);
		}

		// Start Reading task in parallel thread
		readFromSocket();
	}

	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	public void disconnect() throws Exception {
		clientSocket.close();
		System.out.println("Connection Closed");
	}

	public void sendData(String data) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(data);
	}

	public void sendData(byte[] data) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.write(data);
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
			Thread.sleep(100);
		}
		return null;
	}

	private void readFromSocket() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable clientTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new ServerTask(clientSocket, queue));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread clientThread = new Thread(clientTask);
		clientThread.start();
	}

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

	public void cleanQueue() {
		queue.clear();
	}

}

class ServerTask implements Runnable {
	private final Socket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;

	ServerTask(Socket connector, Queue<byte[]> queue) {
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
