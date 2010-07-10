package warehouseClient;

import java.io.IOException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class NotificationProtocolClient {
	SSLSocket socket;
	
	String address;
	int port;
	
	public NotificationProtocolClient(String serverAddress, int serverPort) {
	}
	
	void connect() throws IOException {
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		socket = (SSLSocket)socketFactory.createSocket(address, port);
		socket.startHandshake();
	}
}
