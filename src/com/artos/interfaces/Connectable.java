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
package com.artos.interfaces;

import java.util.concurrent.TimeUnit;

import com.artos.utils.Heartbeat;

/**
 * Interface recommended for classes which are responsible for making
 * connections (socket, serial etc..) class which implements {@code Connectable}
 * can be used along with {@code Heartbeat} class to provide hearbeat/keepalive.
 * 
 * @see Heartbeat
 */
public interface Connectable {

	/**
	 * method to manage connection
	 * 
	 * @throws Exception in case of an error
	 */
	public void connect() throws Exception;

	/**
	 * method to manage disconnection
	 * 
	 * @throws Exception in case of an error
	 */
	public void disconnect() throws Exception;

	/**
	 * method let user confirm if they are connected
	 * 
	 * @return true = connected, false = not connected
	 */
	public boolean isConnected();

	/**
	 * send message to connected end point
	 * 
	 * @param data = byte array of data to be sent
	 * @throws Exception in case of an error
	 */
	public void sendMsg(byte[] data) throws Exception;

	/**
	 * Get next message from queue
	 * 
	 * @return next message from queue
	 * @throws Exception in case of an error
	 */
	public byte[] getNextMsg() throws Exception;

	/**
	 * Get next message from queue within given timeout
	 * 
	 * @param timeout  timeout value
	 * @param timeunit timeout unit
	 * @return next message from queue
	 * @throws Exception in case of an error
	 */
	public byte[] getNextMsg(long timeout, TimeUnit timeunit) throws Exception;

	public boolean hasNextMsg();

}
