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

	/**
	 * Constructor
	 * 
	 * @see Connectable
	 * 
	 * @param context
	 *            TestContext object
	 * @param connector
	 *            Any Connector which implements {@code Connectable}
	 * @param heartbeatData
	 *            fix byte array which should be used in heart bit
	 * @param intervalInMillis
	 *            Interval value between each heartbeats
	 */
	public Heartbeat(TestContext context, Connectable connector, final byte[] heartbeatData, long intervalInMillis) {
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
					UtilsFramework.writePrintStackTrace(context, e);
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
