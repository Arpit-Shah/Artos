package com.artos.framework.listener;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Logger;

import com.artos.framework.FWStatic_Store;
import com.artos.framework.infra.LogWrapper;
import com.artos.framework.infra.TestContext;
import com.artos.interfaces.RealTimeLogListener;

public class RealTimeConnectableListener implements RealTimeLogListener {

	static final String FQCN = LogWrapper.class.getName();
	TestContext context;
	Logger logger;

	public RealTimeConnectableListener() {
		this.context = FWStatic_Store.context;
		this.logger = context.getLogger().getRealTimeLogger();
	}

	public void send(String msg, Object... params) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, msg, params);
	}

	public void receive(String msg, Object... params) {
		logger.logIfEnabled(FQCN, Level.TRACE, null, msg, params);
	}
}
