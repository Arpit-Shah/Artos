package com.arpitos.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import com.arpitos.interfaces.Connectable;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

public class SimpleHttpsServer implements Connectable {
	private int Port;
	private HttpsServer server;
	private File keystoreFile;
	private static String protocol = "TLS";
	HashMap<String, HttpHandler> httpHandlerHashMap = null;
	boolean bServerStarted = false;

	public SimpleHttpsServer(int Port, File keystoreFile) {
		this.Port = Port;
		this.keystoreFile = keystoreFile;
		this.httpHandlerHashMap = null;
	}

	public SimpleHttpsServer(int Port, File keystoreFile, HashMap<String, HttpHandler> httpHandlerHashMap) {
		this.Port = Port;
		this.keystoreFile = keystoreFile;
		this.httpHandlerHashMap = httpHandlerHashMap;
	}

	public void connect() {
		try {
			setPort(Port);
			setKeystoreFile(keystoreFile);
			// load certificate
			// String keystoreFilename = getKeyStorePath() + "mycert.keystore";
			char[] storepass = "mypassword".toCharArray();
			char[] keypass = "mypassword".toCharArray();
			String alias = "alias";
			FileInputStream fIn = new FileInputStream(getKeystoreFile());
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(fIn, storepass);
			// display certificate
			Certificate cert = keystore.getCertificate(alias);
			System.out.println(cert);

			// setup the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, keypass);

			// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keystore);

			// create https server
			server = HttpsServer.create(new InetSocketAddress(getPort()), 0);
			// create ssl context
			SSLContext sslContext = SSLContext.getInstance(protocol);
			// setup the HTTPS context and parameters
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
				public void configure(HttpsParameters params) {
					try {
						// Initialize the SSL context
						SSLContext c = SSLContext.getDefault();
						SSLEngine engine = c.createSSLEngine();
						params.setNeedClientAuth(false);
						params.setCipherSuites(engine.getEnabledCipherSuites());
						params.setProtocols(engine.getEnabledProtocols());

						// get the default parameters
						// SSLParameters defaultSSLParameters =
						// c.getDefaultSSLParameters();
						// params.setSSLParameters(defaultSSLParameters);
					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("Failed to create HTTPS server");
					}
				}
			});

			System.out.println("server started at port " + getPort());

			// If user does not specify handlers than add default handlers
			if (null != httpHandlerHashMap) {
				Iterator<?> entries = httpHandlerHashMap.entrySet().iterator();
				while (entries.hasNext()) {
					@SuppressWarnings("unchecked")
					Entry<String, HttpHandler> thisEntry = (Entry<String, HttpHandler>) entries.next();
					server.createContext(thisEntry.getKey(), thisEntry.getValue());
				}
			} else {
				// add default handler which is applied based on URI
				server.createContext("/arpit/unknown", new RootHandler());
				server.createContext("/arpit/testGet", new EchoGetHandler());
				server.createContext("/arpit/testPost", new EchoPostHandler());
				server.createContext("/arpit/testHeader", new EchoHeaderHandler());
			}

			server.setExecutor(null);
			server.start();
			bServerStarted = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return bServerStarted;
	}

	public void disconnect() {
		server.stop(0);
		bServerStarted = false;
		System.out.println("server stopped");
	}

	@Override
	public boolean hasNextMsg() {
		return false;
	}
	
	@Override
	public byte[] getNextMsg() {
		return null;
	}

	@Override
	public void sendMsg(byte[] msg) {
		// TODO write method to send msg
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int Port) {
		this.Port = Port;
	}

	public File getKeystoreFile() {
		return keystoreFile;
	}

	public void setKeystoreFile(File keystoreFile) {
		this.keystoreFile = keystoreFile;
	}

	public static class RootHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println(he.getRequestMethod().toString());
			String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + "" + "</h1>";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	public static class EchoHeaderHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoHeader handler...");
			Headers headers = he.getRequestHeaders();
			Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
			String response = "";
			for (Map.Entry<String, List<String>> entry : entries)
				response += entry.toString() + "\n";
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}
	}

	public static class EchoGetHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoGet handler...");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = he.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet()) {
				response += key + " = " + parameters.get(key) + "\n";
			}
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();
		}

	}

	public static class EchoPostHandler implements HttpHandler {

		@Override
		public void handle(HttpExchange he) throws IOException {
			System.out.println("Served by /echoPost handler...");
			// parse request
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);
			String query = br.readLine();
			parseQuery(query, parameters);
			// send response
			String response = "";
			for (String key : parameters.keySet()) {
				response += key + " = " + parameters.get(key) + "\n";
			}
			he.sendResponseHeaders(200, response.length());
			OutputStream os = he.getResponseBody();
			os.write(response.toString().getBytes());
			os.close();

		}
	}

	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

		if (query != null) {
			String pairs[] = query.split("[&]");

			for (String pair : pairs) {
				String param[] = pair.split("[=]");

				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}

				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}

				if (parameters.containsKey(key)) {
					Object obj = parameters.get(key);
					if (obj instanceof List<?>) {
						@SuppressWarnings("unchecked")
						List<String> values = (List<String>) obj;
						values.add(value);
					} else if (obj instanceof String) {
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, values);
					}
				} else {
					parameters.put(key, value);
				}
			}
		}
	}
}
