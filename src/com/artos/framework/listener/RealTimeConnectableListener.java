package com.artos.framework.listener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.RealTimeLogListener;
import com.artos.utils.Transform;

public class RealTimeConnectableListener implements RealTimeLogListener {

	static final String FQCN = LogWrapper.class.getName();
	Transform _tfm = new Transform();
	TestContext context;
	Logger logger;

	public RealTimeConnectableListener(TestContext context) {
		this.context = context;
		this.logger = context.getLogger().getRealTimeLogger();
	}

	@Override
	public void send(byte[] data) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, "Req: {}", _tfm.bytesToHexString(data));
	}

	@Override
	public void receive(byte[] data) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, "Res: {}", _tfm.bytesToHexString(data));
	}

	@Override
	public void connected() {
		// Do not do anything
	}

	@Override
	public void disConnected() {
		// Do not do anything
	}
}
