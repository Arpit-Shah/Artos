package com.arpitos.utils;

import com.arpitos.framework.infra.TestContext;
import com.arpitos.interfaces.Connectable;

public class Heartbeat {
	boolean isEnabled = false;
	Connectable connector;
	byte[] heartbeatData;
	volatile Thread thread;
	long intervalInMillis;
	TestContext context;
	volatile boolean running = true;

	public Heartbeat(TestContext context, Connectable connector, byte[] heartbeatData, long intervalInMillis) {
		this.context = context;
		this.connector = connector;
		this.heartbeatData = heartbeatData;
		this.intervalInMillis = intervalInMillis;
	}

	public void stop() {
		running = false;
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(new MyRunnable());
			thread.start();
		}
	}

	public boolean isRunning() {
		return thread != null && thread.isAlive();
	}

	public class MyRunnable implements Runnable {

		public void run() {
			while (running) {
				try {
					connector.sendMsg(heartbeatData);
					synchronized (connector) {
						connector.wait(intervalInMillis);
					}
				} catch (Exception e) {
					FWUtils.writePrintStackTrace(context, e);
					try {
						Thread.sleep(intervalInMillis);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			thread = null;
		}
	}
}
