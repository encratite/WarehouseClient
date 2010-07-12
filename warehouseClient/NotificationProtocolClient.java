package warehouseClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

public class NotificationProtocolClient {
	private final int byteBufferSize = 1024;
	private final int bufferLimit = 100 * byteBufferSize;
	
	private String buffer;
	private byte[] byteBuffer;
	
	private SSLSocket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	
	private String address;
	private int port;
	
	private ObjectMapper mapper;
	
	class NotificationError extends Exception {
		public NotificationError() {
			super();
		}
		
		public NotificationError(String message) {
			super(message);
		}
	}
	
	public NotificationProtocolClient(String serverAddress, int serverPort) {
		byteBuffer = new byte[byteBufferSize];
		mapper = new ObjectMapper();
	}
	
	public void connect() throws IOException {
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		socket = (SSLSocket)socketFactory.createSocket(address, port);
		socket.startHandshake();
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		buffer = "";
	}
	
	private String readData() throws NotificationError {
		try {
			while(true) {
				int bytesRead = inputStream.read(byteBuffer);
				String newData = new String(byteBuffer, 0, bytesRead);
				buffer.concat(newData);
				if(buffer.length() >= bufferLimit)
					throw new NotificationError("The buffer has exceeded the limit");
				//check for the separator
				int offset = buffer.indexOf(':');
				if(offset == -1)
					//the length string hasn't been fully received yet, keep on reading
					continue;
				String lengthString = buffer.substring(0, offset);
				//remove the "123:" string from the buffer
				buffer = buffer.substring(offset + 1);
				int unitSize;
				try {
					unitSize = Integer.parseInt(lengthString);
				}
				catch(NumberFormatException exception) {
					//the server is sending invalid data - this is a critical error, terminate connection and throw an exception
					throw criticalError("The server provided an invalid unit length string: " + lengthString);
				}
				if(unitSize >= bufferLimit) {
					//the unit size provided by the server is too large for the client to handle
					throw criticalError("The server provided a unit size which exceeds the internal buffer limit: " + lengthString);
				}
				while(buffer.length() < unitSize) {
					//need to keep on reading until we have enough data
					bytesRead = inputStream.read(byteBuffer);
					newData = new String(byteBuffer, 0, bytesRead);
					buffer.concat(newData);
				}
				String unit = buffer.substring(0, unitSize - 1);
				//remove the unit from the buffer
				buffer = buffer.substring(unitSize);
				return unit;
			}
		}
		catch(IOException exception) {
			throw criticalError("A stream I/O error occured: " + exception.getMessage());
		}
	}
	
	private NotificationProtocolUnit getUnit() throws NotificationError {
		String unitString = readData();
		try {
			NotificationProtocolUnit unit = mapper.readValue(unitString, NotificationProtocolUnit.class);
			return unit;
		}
		catch(Exception exception) {
			throw criticalError("Unable to process JSON data sent by the server: " + exception.getMessage());
		}
	}
	
	private void processUnit() throws NotificationError {
		String unitString = readData();
		try {
			NotificationProtocolUnit unit = mapper.readValue(unitString, NotificationProtocolUnit.class);
			JsonNode root = mapper.readValue(unitString, JsonNode.class);
			JsonNode content = root.path("content");
			JsonParser tokens = mapper.treeAsTokens(content);
			switch(unit.type) {
			case queued:
				break;
			case downloaded:
				break;
			case downloadError:
				break;
			case downloadDeleted:
				break;
			case serviceMessage:
				break;
			}
		}
		catch(Exception exception) {
			throw criticalError("Unable to process JSON data sent by the server: " + exception.getMessage());
		}
	}
	
	private NotificationError criticalError(String message) {
		try {
			socket.close();
		}
		catch(IOException exception) {
		}
		return new NotificationError(message);
	}
}
