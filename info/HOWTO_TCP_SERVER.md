# How to use TCPServer

* TCPServer class is designed to talk to single client.
* If client connection is established, server will launch separate thread which is responsible for listening to incoming messages from client.
* All received messages are populated in queue which can be polled by driving application.
* TCPServer implements `Connectable` interface so same connector object can be used for `HeartBeat` class.
* TCPServer have following facilities which is discussed later with example:
	* Message Filter
	* Message Parser
	* Real-Time log

## Simple server
Simple server can be launched by specifying minimum required details as shown below.
```
	// launch server
	int port = 1200;
	TCPServer server = new TCPServer(port);
	server.connect();
```

* Above code will start server which is listening on port 1200
* Server will keep listening for incoming client until first client connection is established.

## Simple server with timeout
Simple server can be launched by specifying minimum required details as shown below.
```
	// connect server with soTimeout
    int port = 1200;
    int soTimeout = 5000;
    TCPServer server = new TCPServer(port);
    server.connect(soTimeout);
```

* Above code will launch server which is listening on port 1200
* If client connection is not established within specified timeout (5000 milliseconds in this case), server will timeout and stop listening.

## Server with message filter

Usage:
* User may require filtering some messages during server/client communication (Heartbeat, status messages etc..). Message filter interface allows user to define how to filter messages.
* All messages filtered or non-filtered will be logged in real time logs.
* Message filter is applied after message parse object de-serialise messages, so user should not worry about concatenated messages while writing message filter code.
* User can apply more than one filter object in same TCPServer object.

> WARNING: Implementation inside meetCriteria() method may impact performance of receiver thread so user should keep implementation simple and light.

Message filter simple example is given below:

```
	Transform _transform = new Transform();

	// Create filter object
	ConnectableFilter filter = new ConnectableFilter() {
		@Override
		public boolean meetCriteria(byte[] data) {
			if (Arrays.equals(data, _transform.strHexToByteArray("00 00 00 04 01 02 03 04"))) {
				return true;
			}
			return false;
		}
	};
```
* Above example creates filter object using `ConnectableFilter` interface. User must provide implementation of the method `meetCriteria(byte[] data)` so receiver thread can filter messages which meets those criteria(s).
* In current example any received byte array matches `"00 00 00 04 01 02 03 04"` will be filtered out and will not be added to message queue.

```
	// add filter to filterList
	List<ConnectableFilter> filterList = new ArrayList<>();
    filterList.add(filter);

	// launch server with filter
	int port = 1200;
	TCPServer server = new TCPServer(port, null, filterList);
	server.connect();
    // receive msg with 2 seconds timeout
    byte[] msg = server.getNextMsg(2000, TimeUnit.MILLISECONDS);
    // server disconnect
    server.disconnect();
```

* Above code will launch server which is listening on port 1200 with supplied filter list.
* any message which meets criteria specified in supplied filter(s) will be dropped from the message queue.

## Server with message parser

Usage:
* TCP does not have concept of fixed size packets like UDP. If two or more byte-arrays are sent at the same time, TCP protocol can concatenate them (TCP guarantees to maintain order) and send it to make transfer efficient. Due to this behaviour, at receiver end user may have to implement logic which can de-serialise message according to their specification.
* TCPServer allows user to supply de-serialising logic so prior to populating messages to queue, messages can be separated from concatenated byte arrays.
* If filter object is supplied then filtering will be processed after message de-serialising.
* Real-Time log will log message prior to de-serialisation is applied so performance measurement does not have any impact on time stamp.

> WARNING: Implementation of de-serialisation method may impact performance of receiver thread so user should keep implementation simple and light.

Below Example de-serialises concanated messages with specification:
* First four bytes (Big Endian) as payload length excluding length bytes + data
	* Example Message: `"00 00 00 04 11 22 33 44"`
	* Length: `"00 00 00 04"`
	* Data: `"11 22 33 44"`
* Use can construct similar class which implements `ConnectableMessageParser` to de-serialise concatenated messages.

```
public class MsgParser4ByteLength implements ConnectableMessageParser {
	Transform _transform = new Transform();
	byte[] leftOverBytes = null;
	List<byte[]> msgList = null;

	@Override
	public byte[] getLeftOverBytes() {
		return leftOverBytes;
	}

	@Override
	public List<byte[]> parse(byte[] data) {
		// reset variable before use
		msgList = new ArrayList<>();
		leftOverBytes = null;

		deserializeMsg(data);

		return msgList;
	}

	private void deserializeMsg(byte[] data) {
		// Check if at least length can be worked out
		if (!sufficientDataForLengthCalc(data)) {
			leftOverBytes = data;
			return;
		}

		// Check if message can be constructed
		if (!sufficientDataForMsg(data)) {
			leftOverBytes = data;
			return;
		}

		// Extract one complete message
		byte[] leftOvers = extractMsg(data);

        // process leftOver bytes to see if anymore messages can be extracted
		if (null != leftOvers) {
			deserializeMsg(leftOvers);
		}
	}

	// Extract complete message inclusive of 4 bytes of length
	private byte[] extractMsg(byte[] data) {
		int length = _transform.bytesToInteger(Arrays.copyOfRange(data, 0, 4), ByteOrder.BIG_ENDIAN);

        // if complete message is found then add to message list.
		msgList.add(Arrays.copyOfRange(data, 0, 4 + length));

        // Return leftover bytes after extracting one complete message
		if (data.length > 4 + length) {
			return Arrays.copyOfRange(data, 4 + length, data.length);
		}
		return null;
	}

	// Returns true if atleast 4 bytes are present to calculate length of the data
	private boolean sufficientDataForLengthCalc(byte[] data) {
		if (data.length < 4) {
			return false;
		}
		return true;
	}

	// Returns true if enough bytes are present to construct one complete message
	private boolean sufficientDataForMsg(byte[] data) {
		int length = _transform.bytesToInteger(Arrays.copyOfRange(data, 0, 4), ByteOrder.BIG_ENDIAN);
		if (data.length < 4 + length) {
			return false;
		}
		return true;
	}

}
```

Above example de-serialise concatenated messages and construct list of messages according to specification. If any bytes are left over then those bytes are handed back to receiver thread.

```
    // create msg parser object
    MsgParser4ByteLength msgParser = new MsgParser4ByteLength();

	// launch server with message parser
	int port = 1200;
	TCPServer server = new TCPServer(port, msgParser, null);
	server.connect();
    // receive msg with 2 seconds timeout
    byte[] msg = server.getNextMsg(2000, TimeUnit.MILLISECONDS);
    // server disconnect
    server.disconnect();
```