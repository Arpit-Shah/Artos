package com.arpitos.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import com.arpitos.interfaces.Connectable;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class SimpleHttpServer implements Connectable {
	private int Port;
	private HttpServer server;
	HashMap<String, HttpHandler> httpHandlerHashMap = null;
	boolean bServerStarted = false;

	public SimpleHttpServer(int Port) {
		this.Port = Port;
		this.httpHandlerHashMap = null;
	}

	public SimpleHttpServer(int Port, HashMap<String, HttpHandler> httpHandlerHashMap) {
		this.Port = Port;
		this.httpHandlerHashMap = httpHandlerHashMap;
	}

	public void connect() {
		try {
			setPort(Port);
			server = HttpServer.create(new InetSocketAddress(getPort()), 0);
			System.out.println("server started at port " + getPort());

			// If user does not specify handlers than add default handlers
			if (null != httpHandlerHashMap) {
				Iterator<Entry<String, HttpHandler>> entries = httpHandlerHashMap.entrySet().iterator();
				while (entries.hasNext()) {
					Entry<String, HttpHandler> thisEntry = entries.next();
					System.out.println(thisEntry.getKey());
					server.createContext(thisEntry.getKey(), thisEntry.getValue());
				}
			} else {
				// add default handler which is applied based on URI
				// server.createContext("/arpit/unknown", new RootHandler());
				// server.createContext("/arpit/testGet", new EchoGetHandler());
				// server.createContext("/arpit/testPost", new
				// EchoPostHandler());
				// server.createContext("/arpit/testHeader", new
				// EchoHeaderHandler());
			}

			// This is crazy, we are launching no limit thread executor, ideally
			// we should use limited thread executor, if does not work than
			// comment out this line
			// http://stackoverflow.com/questions/14729475/can-i-make-a-java-httpserver-threaded-process-requests-in-parallel
			server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
			// server.setExecutor(null);
			server.start();
			bServerStarted = true;
		} catch (IOException e) {
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

	public byte[] getNextMSG(long Timeout, TimeUnit timeunit) {
		return null;
	}

	@Override
	public void sendMsg(byte[] msg) {
		// TODO write method to send msg
	}

	@Override
	public byte[] getNextMsg() {
		// TODO write method to receive msg
		return null;
	}

	public int getPort() {
		return Port;
	}

	public void setPort(int port) {
		this.Port = port;
	}

	// public static class RootHandler implements HttpHandler {
	//
	// @Override
	// public void handle(HttpExchange he) throws IOException {
	// System.out.println(he.getRequestMethod().toString());
	// String response = "<h1>Server start success if you see this message</h1>"
	// + "<h1>Port: " + "" + "</h1>";
	// he.sendResponseHeaders(200, response.length());
	// OutputStream os = he.getResponseBody();
	// os.write(response.getBytes());
	// os.close();
	// }
	// }
	//
	// public static class EchoHeaderHandler implements HttpHandler {
	//
	// @Override
	// public void handle(HttpExchange he) throws IOException {
	// System.out.println("Served by /echoHeader handler...");
	// Headers headers = he.getRequestHeaders();
	// Set<Map.Entry<String, List<String>>> entries = headers.entrySet();
	// String response = "";
	// for (Map.Entry<String, List<String>> entry : entries)
	// response += entry.toString() + "\n";
	// he.sendResponseHeaders(200, response.length());
	// OutputStream os = he.getResponseBody();
	// os.write(response.toString().getBytes());
	// os.close();
	// }
	// }
	//
	// public static class EchoGetHandler implements HttpHandler {
	//
	// @Override
	// public void handle(HttpExchange he) throws IOException {
	// System.out.println("Served by /echoGet handler...");
	// // parse request
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// URI requestedUri = he.getRequestURI();
	// String query = requestedUri.getRawQuery();
	// parseQuery(query, parameters);
	// // send response
	// String response = "";
	// for (String key : parameters.keySet()) {
	// response += key + " = " + parameters.get(key) + "\n";
	// }
	// he.sendResponseHeaders(200, response.length());
	// OutputStream os = he.getResponseBody();
	// os.write(response.toString().getBytes());
	// os.close();
	// }
	//
	// }
	//
	// public static class EchoPostHandler implements HttpHandler {
	//
	// @Override
	// public void handle(HttpExchange he) throws IOException {
	// System.out.println("Served by /echoPost handler...");
	// // parse request
	// Map<String, Object> parameters = new HashMap<String, Object>();
	// InputStreamReader isr = new InputStreamReader(he.getRequestBody(),
	// "utf-8");
	// BufferedReader br = new BufferedReader(isr);
	// String query = br.readLine();
	// parseQuery(query, parameters);
	// // send response
	// String response = "";
	// for (String key : parameters.keySet()) {
	// response += key + " = " + parameters.get(key) + "\n";
	// }
	// he.sendResponseHeaders(200, response.length());
	// OutputStream os = he.getResponseBody();
	// os.write(response.toString().getBytes());
	// os.close();
	//
	// }
	// }

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
