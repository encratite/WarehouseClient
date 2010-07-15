package warehouseClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.codehaus.jackson.JsonGenerationException;
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
	
	private int rpcId;
	private Map<Integer, RemoteProcedureCallHandler> rpcHandlers; 
	
	class NotificationError extends Exception {
		public NotificationError() {
			super();
		}
		
		public NotificationError(String message) {
			super(message);
		}
	}
	
	public NotificationProtocolClient() {
	}
	
	public NotificationProtocolClient(String serverAddress, int serverPort) {
		address = serverAddress;
		port = serverPort;
		byteBuffer = new byte[byteBufferSize];
		mapper = new ObjectMapper();
		rpcHandlers = new HashMap<Integer, RemoteProcedureCallHandler>();
	}
	
	public void connect() throws IOException {
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		socket = (SSLSocket)socketFactory.createSocket(address, port);
		socket.startHandshake();
		inputStream = socket.getInputStream();
		outputStream = socket.getOutputStream();
		buffer = "";
		rpcId = 1;
	}
	
	private String readData() throws NotificationError {
		try {
			while(true) {
				System.out.println("Reading...");
				int bytesRead = inputStream.read(byteBuffer);
				System.out.println("Read " + bytesRead + " bytes");
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
	
	public void processUnit() throws NotificationError {
		String unitString = readData();
		try {
			NotificationProtocolUnit unit = mapper.readValue(unitString, NotificationProtocolUnit.class);
			JsonNode root = mapper.readValue(unitString, JsonNode.class);
			JsonNode content = root.path("content");
			JsonParser parser = content.traverse();
			
			ReleaseData releaseData;
			
			switch(unit.type) {
			case queued:
				releaseData = mapper.readValue(parser, ReleaseData.class);
				break;
				
			case downloaded:
				releaseData = mapper.readValue(parser, ReleaseData.class);
				break;
				
			case downloadError:
				DownloadError downloadError = mapper.readValue(parser, DownloadError.class);
				break;
				
			case downloadDeleted:
				releaseData = mapper.readValue(parser, ReleaseData.class);
				break;
				
			case serviceMessage:
				ServiceMessage serviceMessage = mapper.readValue(parser, ServiceMessage.class);
				break;
				
			case rpcResult:
				RemoteProcedureCallResult rpcResult = mapper.readValue(parser, RemoteProcedureCallResult.class);
				int id = rpcResult.id;
				if(!rpcHandlers.containsKey(id))
					throw criticalError("The server provided an invalid RPC result ID: " + Integer.toString(id));
				RemoteProcedureCallHandler handler = rpcHandlers.get(id);
				handler.receiveResult(rpcResult.result);
				break;
			}
		}
		catch(Exception exception) {
			throw criticalError("Unable to process JSON data sent by the server: " + exception.getMessage());
		}
	}
	
	protected void queuedRelease(ReleaseData releaseData) {
	}
	
	protected void downloadedRelease(ReleaseData releaseData) {
	}
	
	protected void downloadError(DownloadError downloadError) {
	}
	
	protected void downloadDeleted(ReleaseData releaseData) {
	}
	
	protected void serviceMessage(ServiceMessage serviceMessage) {
	}
	
	private void sendData(String input) throws IOException {
		String packet = Integer.toString(input.length()) + ":" + input;
		outputStream.write(packet.getBytes());
	}
	
	public void sendRPCData(String function, List<Object> arguments) throws NotificationError, IOException {
		Map<String, Object> content = new HashMap<String, Object>();
		content.put("id", rpcId);
		content.put("method", function);
		content.put("params", arguments);
		
		Map<String, Object> unit = new HashMap<String, Object>();
		unit.put("type", "rpc");
		unit.put("content", content);
		
		try {
			mapper.writeValue(outputStream, unit);
		}
		catch(JsonGenerationException exception) {
			throw criticalError("Unable to serialise RPC arguments");
		}
		
		rpcId++;
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
