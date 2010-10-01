package warehouseClient;

import java.io.IOException;

import warehouseClient.protocolUnit.DownloadError;
import warehouseClient.protocolUnit.NotificationData;
import warehouseClient.protocolUnit.ReleaseData;
import warehouseClient.protocolUnit.ServiceMessage;

public class WarehouseProtocolHandler extends NotificationProtocolClient {
	private WarehouseClient client;
	
	public WarehouseProtocolHandler(WarehouseClient client, String address, int port) {
		super(address, port, client);
		this.client = client;
	}
	
	public void connect() throws IOException {
		print("Connecting...");
		super.connect();
		print("Connected");
	}
	
	private void print(String message) {
		client.print(message);
	}
	
	protected void processNotification(NotificationData notification) {
		client.processNotification(notification);
	}
}
