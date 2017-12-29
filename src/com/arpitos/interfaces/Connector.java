package com.arpitos.interfaces;

public interface Connector {

	public void connect() throws Exception;

	public void disconnect() throws Exception;

	public boolean isConnected();

	public void sendData(byte[] data) throws Exception;

	public byte[] recieveData() throws Exception;

}
