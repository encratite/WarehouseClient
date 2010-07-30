package warehouseClient;

import java.io.IOException;

import warehouseClient.protocolUnit.DownloadError;
import warehouseClient.protocolUnit.ReleaseData;
import warehouseClient.protocolUnit.ServiceMessage;

public class WarehouseProtocolHandler extends NotificationProtocolClient {
	public WarehouseProtocolHandler(String serverAddress, int serverPort) {
		super(serverAddress, serverPort);
	}
	
	public void connect() throws IOException {
		System.out.println("Connecting...");
		super.connect();
		System.out.println("Connected");
	}
	
	protected void queuedRelease(ReleaseData releaseData) {
		System.out.println("Queued: " + releaseData.name);
	}
	
	protected void downloadedRelease(ReleaseData releaseData) {
		System.out.println("Download finished: " + releaseData.name);
	}
	
	protected void downloadError(DownloadError downloadError) {
		System.out.println("Failed to download " + downloadError.release.name + ": " + downloadError.message);
	}
	
	protected void downloadDeleted(ReleaseData releaseData) {
		System.out.println("Download deleted: " + releaseData.name);
	}
	
	protected void serviceMessage(ServiceMessage serviceMessage) {
		System.out.println("Service message: " + serviceMessage.message + " (" + serviceMessage.severity + ")");
	}
}
