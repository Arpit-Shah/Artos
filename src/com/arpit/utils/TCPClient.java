package com.arpit.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TCPClient {
	Socket clientSocket;
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	Queue<byte[]> queue = new LinkedList<byte[]>();

	public TCPClient() {
	}

	public void connect(String ip, int Port) throws Exception {
		System.out.println("Listening on Port : " + Port);
		clientSocket = new Socket(ip, Port);
		if (clientSocket.isConnected()) {
			System.out.println("Connected to " + ip + ":" + Port);
		}
		// Start Reading task in parallel thread
		readFromSocket();
	}

	public void Disconnect() throws Exception {
		clientSocket.close();
		System.out.println("Connection Closed");
	}

	public void writeToSocket(String MSGToWrite) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(MSGToWrite);
	}

	public void writeToSocket(byte[] bytes) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.write(bytes);
	}

	public void cleanQueue() {
		queue.clear();
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
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new ServerTask(clientSocket, queue));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		Thread serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public Socket getConnector() {
		return clientSocket;
	}

	public void setConnector(Socket connector) {
		this.clientSocket = connector;
	}

	public BufferedReader getInFromClient() {
		return inFromServer;
	}

	public void setInFromClient(BufferedReader inFromClient) {
		this.inFromServer = inFromClient;
	}

	public DataOutputStream getOutToClient() {
		return outToServer;
	}

	public void setOutToClient(DataOutputStream outToClient) {
		this.outToServer = outToClient;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public void setQueue(Queue<byte[]> queue) {
		this.queue = queue;
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
