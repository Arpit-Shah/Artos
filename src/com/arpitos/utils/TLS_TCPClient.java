package com.arpitos.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.arpitos.interfaces.Connectable;

//http://blog.trifork.com/2009/11/10/securing-connections-with-tls/
/**
 * @author ArpitS
 * 
 *         <PRE>
 * Use "Keystore explorer" third party tool
 * Create Three JKS key stores
 * 1) serverKeystore - in this example (keystore.jks)
 * 2) clientKeystore - in this example (keystore.jks)
 * 3) trustStore - in this example (cacerts.jks)
 * Generate CA root key pair in trustStore (self signed)
 * Generate Server Key pair in serverKeystore (signed with CA root key)
 * Generate Client Key pair in clientKeystore (signed with CA root key)
 * Export Server/client certificates and keys in .pem format or .cert.pem format as needed
 * if Bladerunner is server then use serverKeystore and trustStore for authenticating client
 * if Bladerunner is client then use clientKeystore and trustStore for authenticating server
 *         </PRE>
 */

public class TLS_TCPClient implements Connectable {
	String IP;
	int Port;
	Socket clientSocket;
	BufferedReader inFromServer;
	DataOutputStream outToServer;
	Queue<byte[]> queue = new LinkedList<byte[]>();
	Thread serverThread;

	public TLS_TCPClient(String IP, int Port) {
		this.IP = IP;
		this.Port = Port;
	}

	public void connect() {
		try {
			System.out.println("Listening on Port : " + Port);
			clientSocket = new Socket(IP, Port);
			if (clientSocket.isConnected()) {
				System.out.println("Connected to " + IP + ":" + Port);
			}

			/*
			 * Create SSLSocketFactory
			 */
			KeyManagerFactory kmf = createKeyManagerFactory();
			TrustManagerFactory tmf = createTrustStore();
			/*
			 * Create SSL context
			 */
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			/*
			 * Later, when both parties agree that TLS should be started, a
			 * SSLSocket is needed. The SSLSocket will wrap the normal socket
			 * that was created by the ServerSocket.
			 */
			clientSocket = startSSLSocket(sslContext, clientSocket);
			System.out.println(sslContext.getProtocol());

			// Start Reading task in parallel thread
			readFromSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private SSLSocket startSSLSocket(SSLContext sslContext, Socket clientSocket) throws Exception {
		// Get the default SSLSocketFactory
		// SSLSocketFactory sf = ((SSLSocketFactory)
		// SSLSocketFactory.getDefault());
		SSLSocketFactory sf = sslContext.getSocketFactory();
		// Wrap 'socket' from above in a SSL socket
		InetSocketAddress remoteAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
		SSLSocket s = (SSLSocket) (sf.createSocket(clientSocket, remoteAddress.getHostName(), clientSocket.getPort(), true));

		// we are a client
		s.setUseClientMode(true);

		boolean allowallCipherSuites = true;
		if (allowallCipherSuites) {
			// Risky
			// allow all supported protocols and cipher suites
			s.setEnabledProtocols(s.getSupportedProtocols());
			s.setEnabledCipherSuites(s.getSupportedCipherSuites());
		} else {
			// Less Risky
			// select supported protocols and cipher suites
			s.setEnabledProtocols(StrongTls.intersection(s.getSupportedProtocols(), StrongTls.ENABLED_PROTOCOLS));
			s.setEnabledCipherSuites(StrongTls.intersection(s.getSupportedCipherSuites(), StrongTls.ENABLED_CIPHER_SUITES));
		}

		// and go!
		s.startHandshake();

		// continue communication on 'socket'
		return s;
	}

	private KeyManagerFactory createKeyManagerFactory() throws Exception {
		// Key store for your own private key and signing certificates
		InputStream keyStoreResource = new FileInputStream("./tls_tcpclient_cert/keystore.jks");
		char[] keyStorePassphrase = "password".toCharArray();
		KeyStore ksKeys = KeyStore.getInstance("JKS");
		ksKeys.load(keyStoreResource, keyStorePassphrase);

		/*
		 * As a key store potentially contains many keys, you need a KeyManager
		 * to determine which key to use. Under the assumption that there is
		 * only 1 key in the key store, the default KeyManager is fine. Of
		 * course, there is an extra indirection through a KeyManagerFactory:
		 */
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ksKeys, keyStorePassphrase);

		return kmf;
	}

	private TrustManagerFactory createTrustStore() throws Exception {
		// Trust store contains certificates of trusted certificate authorities.
		// Needed for client certificate validation.
		InputStream trustStoreIS = new FileInputStream("./tls_tcpclient_cert/cacerts.jks");
		char[] trustStorePassphrase = "password".toCharArray();
		KeyStore ksTrust = KeyStore.getInstance("JKS");
		ksTrust.load(trustStoreIS, trustStorePassphrase);

		/*
		 * And again a TrustManager (the default one accepts all certificate
		 * authorities in the trust store):
		 */
		// TrustManager decides which certificate authorities to use.
		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ksTrust);

		return tmf;
	}

	public boolean isConnected() {
		return clientSocket.isConnected();
	}

	public void disconnect() {
		try {
			serverThread.interrupt();
			clientSocket.close();
			System.out.println("Connection Closed");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMsg(String MSGToWrite) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.writeBytes(MSGToWrite);
	}

	@Override
	public void sendMsg(byte[] bytes) throws Exception {
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		outToServer.write(bytes);
	}

	public void cleanQueue() {
		queue.clear();
	}

	@Override
	public boolean hasNextMsg() {
		if (queue.isEmpty()) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getNextMsg() {
		if (hasNextMsg()) {
			return queue.poll();
		}
		return null;
	}

	public byte[] getNextMSG(long Timeout, TimeUnit timeunit) {
		boolean isTimeout = false;
		long startTime = System.nanoTime();
		long finishTime;
		long maxAllowedTime = TimeUnit.NANOSECONDS.convert(Timeout, timeunit);

		while (!isTimeout) {
			if (hasNextMsg()) {
				return queue.poll();
			}
			finishTime = System.nanoTime();
			if ((finishTime - startTime) > maxAllowedTime) {
				return null;
			}
			// Give system some time to do other things
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void readFromSocket() {
		final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
		final Runnable serverTask = new Runnable() {
			@Override
			public void run() {
				try {
					clientProcessingPool.submit(new TLS_ServerTask(clientSocket, queue));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		serverThread = new Thread(serverTask);
		serverThread.start();
	}

	public Socket getConnector() {
		return clientSocket;
	}

	public void setConnector(Socket connector) {
		this.clientSocket = connector;
	}

	public BufferedReader getInFromClient() {
		return inFromServer;
	}

	public void setInFromClient(BufferedReader inFromClient) {
		this.inFromServer = inFromClient;
	}

	public DataOutputStream getOutToClient() {
		return outToServer;
	}

	public void setOutToClient(DataOutputStream outToClient) {
		this.outToServer = outToClient;
	}

	public Queue<byte[]> getQueue() {
		return queue;
	}

	public void setQueue(Queue<byte[]> queue) {
		this.queue = queue;
	}

}

class TLS_ServerTask implements Runnable {
	private final Socket connector;
	int read = -1;
	byte[] buffer = new byte[4 * 1024]; // a read buffer of 4KiB
	byte[] readData;
	String redDataText;
	Queue<byte[]> queue;

	TLS_ServerTask(Socket connector, Queue<byte[]> queue) {
		this.connector = connector;
		this.queue = queue;
	}

	@Override
	public void run() {
		try {
			// If socket is active, user should get 0 byte or above, if -1
			// returned that means socket is closed. throw exception in that
			// case
			while ((read = connector.getInputStream().read(buffer)) > -1) {
				readData = new byte[read];
				System.arraycopy(buffer, 0, readData, 0, read);
				if (readData.length > 0) {
					queue.add(readData);
				}
			}

			throw new Exception("Socket closed");
		} catch (Exception e) {
			if (!e.getMessage().contains("Socket closed")) {
				e.printStackTrace();
			} else {
				System.out.println(e.getMessage().toString());
			}
		}
	}
}
