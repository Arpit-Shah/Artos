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

public class UDPClientSeperateSendReceive implements Connectable {

	String sendIP;
	int sendPort;
	String receiveIP;
	int receivePort;
	UDPClient sendUDPClient;
	UDPClient receiveUDPClient;

	public UDPClientSeperateSendReceive(String sendIP, int sendPort, String receiveIP, int receivePort) {
		this.sendIP = sendIP;
		this.sendPort = sendPort;
		this.receiveIP = receiveIP;
		this.receivePort = receivePort;
		sendUDPClient = new UDPClient(sendIP, sendPort);
		receiveUDPClient = new UDPClient(receiveIP, receivePort);
	}

	public void connect() {
		sendUDPClient.connect();
		receiveUDPClient.connect();
	}

	public boolean isConnected() {
		return (sendUDPClient.isConnected() && receiveUDPClient.isConnected());
	}

	public void disconnect() {
		sendUDPClient.disconnect();
		receiveUDPClient.disconnect();
	}

	public void sendMsg(String hexString) throws Exception {
		sendUDPClient.sendMsg(hexString);
	}
	
	@Override
	public void sendMsg(byte[] data) throws Exception {
		sendUDPClient.sendMsg(data);
	}

	public void cleanQueue() {
		sendUDPClient.cleanQueue();
		receiveUDPClient.cleanQueue();
	}

	@Override
	public boolean hasNextMsg() {
		return receiveUDPClient.hasNextMsg();
	}

	@Override
	public byte[] getNextMsg() throws Exception {
		return receiveUDPClient.getNextMsg();
	}

	public byte[] getNextMsg(long Timeout, TimeUnit timeunit) {
		return receiveUDPClient.getNextMSG(Timeout, timeunit);
	}

	public String getSendIP() {
		return sendIP;
	}

	public void setSendIP(String sendIP) {
		this.sendIP = sendIP;
	}

	public int getSendPort() {
		return sendPort;
	}

	public void setSendPort(int sendPort) {
		this.sendPort = sendPort;
	}

	public String getReceiveIP() {
		return receiveIP;
	}

	public void setReceiveIP(String receiveIP) {
		this.receiveIP = receiveIP;
	}

	public int getReceivePort() {
		return receivePort;
	}

	public void setReceivePort(int receivePort) {
		this.receivePort = receivePort;
	}

	public UDPClient getSendUDPClient() {
		return sendUDPClient;
	}

	public void setSendUDPClient(UDPClient sendUDPClient) {
		this.sendUDPClient = sendUDPClient;
	}

	public UDPClient getReceiveUDPClient() {
		return receiveUDPClient;
	}

	public void setReceiveUDPClient(UDPClient receiveUDPClient) {
		this.receiveUDPClient = receiveUDPClient;
	}

}