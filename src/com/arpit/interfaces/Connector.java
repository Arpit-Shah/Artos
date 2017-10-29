package com.arpit.interfaces;

public interface Connector {

	public void Connect();

	public void Disconnect();

	public boolean isConnected();

	public void SendMsg(byte[] data);

	public byte[] RecieveMsg();

}
