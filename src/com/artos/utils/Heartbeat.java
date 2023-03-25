/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.utils;

import com.artos.framework.infra.TestContext;
import com.artos.interfaces.Connectable;

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
