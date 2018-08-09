package com.artos.interfaces;

public interface RealTimeLogListener {

	public void connected();
	
	public void disConnected();

	public void send(byte[] data);

	public void receive(byte[] data);

}
