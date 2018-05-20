package com.arpitos.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.arpitos.interfaces.Connectable;

public class UDPServer implements Connectable {
	int port;
	DatagramSocket serverSocket;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	Queue<byte[]> queue = new LinkedList<byte[]>();
	Thread serverThread;

	public UDPServer(int Port) {
		System.out.println("Creating UDP server port with : " + Port);
		this.port = Port;
	}

	public void connect() {
		try {
			// Start Reading task in parallel thread
			System.out.println("Listening on Port : " + port);
			serverSocket = new DatagramSocket(port);
			readFromSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		// Only true if client is bound
		return serverSocket.isBound();
	}

	public void disconnect() {
		try {
			serverThread.interrupt();
			serverSocket.close();
			System.out.println("Connection Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean hasNextMsg() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	public byte[] getNextMSG(long Timeout, TimeUnit timeunit) {
		boolean isTimeout = false;
		long startTime = System.nanoTime();
		long finishTime;
		long maxAllowedTime = TimeUnit.NANOSECONDS.convert(Timeout, timeunit);

		while (!isTimeout) {
			if (hasNextMsg()) {
				return queue.poll();
			}
			finishTime = System.nanoTime();
			if ((finishTime - startTime) > maxAllowedTime) {
				return null;
			}
			// Give system some time to do other things
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public byte[] getNextMsg() {
		if (hasNextMsg()) {
			return queue.poll();
		}
		return null;
	}

	public void cleanQueue() {
		queue.clear();
	}

	private void readFromSocket() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new UDPClientTask(serverSocket, queue));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

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

	@Override
	public void sendMsg(byte[] msg) throws Exception {
		// TODO Need to implement

	}
}

class UDPClientTask implements Runnable {
	private final DatagramSocket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;

	UDPClientTask(DatagramSocket connector, Queue<byte[]> queue) {
		this.connector = connector;
		this.queue = queue;
	}

	@Override
	public void run() {
		try {

			byte[] receiveData = new byte[1024 * 4];
			while (true) {
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				connector.receive(receivePacket);
				readData = receivePacket.getData();

				// This to avoid non printable char in the buffer
				String string = new String(receiveData, 0, receivePacket.getLength());
				string = string + "\n";
				if (readData.length > 0) {
					queue.add(string.getBytes());
				}
			}
		} catch (Exception e) {
			System.out.println("caught exception");
			System.out.println(e.getMessage().toString());
		}
		System.out.println("Terminating thread");
	}
}
