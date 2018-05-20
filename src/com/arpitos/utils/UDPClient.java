package com.arpitos.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.arpitos.interfaces.Connectable;
import com.arpitos.utils.Convert;

public class UDPClient implements Connectable {

	String IP;
	int Port;
	DatagramSocket clientSocket;
	// DatagramPacket packetFromServer;
	DatagramPacket packetToServer;
	Queue<byte[]> queue = new LinkedList<byte[]>();
	Thread serverThread;

	public UDPClient(String IP, int Port) {
		this.IP = IP;
		this.Port = Port;
	}

	public void connect() {
		// create udp socket connection
		try {
			System.out.println("Listening on Port : " + Port);
			clientSocket = new DatagramSocket();
			if (clientSocket.isConnected()) {
				System.out.println("Connected to " + IP + ":" + Port);
			}
			// Start Reading task in parallel thread
			readFromSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	public void disconnect() {
		try {
			serverThread.interrupt();
			clientSocket.close();
			System.out.println("Connection Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(String hexString) throws Exception {
		byte[] data = new Convert().stringHexToByteArray(hexString);
		packetToServer = new DatagramPacket(data, data.length, InetAddress.getByName(IP), Port);
		clientSocket.send(packetToServer);
	}
	
	@Override
	public void sendMsg(byte[] data) throws Exception {
		packetToServer = new DatagramPacket(data, data.length, InetAddress.getByName(IP), Port);
		clientSocket.send(packetToServer);
	}

	public void cleanQueue() {
		queue.clear();
	}

	@Override
	public boolean hasNextMsg() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getNextMsg() throws Exception {
		if (hasNextMsg()) {
			return queue.poll();
		}
		return null;
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

	private void readFromSocket() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new UDPServerTask(clientSocket, queue, IP, Port));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public DatagramSocket getConnector() {
		return clientSocket;
	}

	public void setConnector(DatagramSocket connector) {
		this.clientSocket = connector;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public void setQueue(Queue<byte[]> queue) {
		this.queue = queue;
	}

	public String getIP() {
		return IP;
	}
}

class UDPServerTask implements Runnable {
	private final DatagramSocket connector;
	DatagramPacket packetFromServer;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;
	String IP;
	int Port;

	UDPServerTask(DatagramSocket connector, Queue<byte[]> queue, String IP, int Port) {
		this.connector = connector;
		this.queue = queue;
		this.IP = IP;
		this.Port = Port;
	}

	@Override
	public void run() {
		try {
			while ((read = connector.getReceiveBufferSize()) > 0) {
				readData = new byte[read];
				packetFromServer = new DatagramPacket(readData, readData.length, InetAddress.getByName(IP), Port);
				connector.receive(packetFromServer);
				if (null != packetFromServer.getData()) {
					System.arraycopy(packetFromServer.getData(), 0, readData, 0, read);
					if (readData.length > 0) {
						queue.add(readData);
					}
				} else {
					Thread.sleep(20);
				}
			}
		} catch (Exception e) {
			if (!connector.isClosed()) {
				// only print if the read method generated an exception while
				// the socket is open, otherwise ignore it
				e.printStackTrace();
			}
		}
	}
}
