// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.framework.listener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.RealTimeLogListener;
import com.artos.utils.Transform;

/**
 * Responsible for logging byte array provided by registering classes. Generally
 * used in cases where real time logging is required for all send/receive type
 * of communication. Example: {@code TCPClient}. Logs will be stored in the log
 * file with name *-realtime.*
 *
 */
public class RealTimeLogEventListener implements RealTimeLogListener {

	static final String FQCN = LogWrapper.class.getName();
	Transform _tfm = new Transform();
	TestContext context;
	Logger logger;

	/**
	 * Constructor of type 'Real Time Log Listener' storing the context and the
	 * log4j logger object
	 * 
	 * @param context
	 *            Test context
	 */
	public RealTimeLogEventListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger().getRealTimeLogger();
	}

	/**
	 * Logs the byte array
	 */
	@Override
	public void send(byte[] data) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, "Req: {}", _tfm.bytesToHexString(data));
	}

	/**
	 * Logs the byte array
	 */
	@Override
	public void receive(byte[] data) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, "Res: {}", _tfm.bytesToHexString(data));
	}

	/**
	 * Logs if connect event is triggered
	 */
	@Override
	public void connected() {
		// Do not do anything
	}

	/**
	 * Logs if disconnect event is triggered
	 */
	@Override
	public void disConnected() {
		// Do not do anything
	}
}
