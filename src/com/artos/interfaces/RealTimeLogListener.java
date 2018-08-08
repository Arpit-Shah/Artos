package com.artos.interfaces;

public interface RealTimeLogListener {

	default void send(String msg, Object... params) {
	}

	default void receive(String msg, Object... params) {
	}
}
