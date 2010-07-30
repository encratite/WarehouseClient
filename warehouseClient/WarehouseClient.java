package warehouseClient;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import warehouseClient.protocolUnit.NotificationData;

public class WarehouseClient implements Runnable {
	private WarehouseProtocolHandler protocolClient;
	private Thread networkingThread, notificationThread;
	private Configuration configuration;
	private WarehouseClientView view;
	
	public WarehouseClient(String configurationPath) throws IOException {
		loadConfiguration(configurationPath);
		protocolClient = new WarehouseProtocolHandler(configuration.notificationServerAddress, configuration.notificationServerPort);
		networkingThread = new Thread(protocolClient, "Networking thread");
		notificationThread = new Thread(this, "Notification thread");
		view = new WarehouseClientView();
	}
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private void processOldNotifications(JsonNode input) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		for(JsonNode node : input) {
			NotificationData unit = new NotificationData(node);
			System.out.println(unit.description);
		}
	}
	
	private void runTest() {
		try {
			protocolClient.connect();
		}
		catch(IOException exception) {
			System.out.println("Failed to connect: " + exception.getMessage());
			System.exit(1);
		}
		networkingThread.start();
		try {
			RemoteProcedureCallHandler
				getNotificationCount = new RemoteProcedureCallHandler("getNotificationCount", protocolClient),
				getOldNotifications = new RemoteProcedureCallHandler("getOldNotifications", protocolClient);
			int count = (Integer)getNotificationCount.call();
			System.out.println("Number of messages: " + count);
			getOldNotifications.call(0, Math.max(count - 1, 0));
			JsonNode oldNotificationsNode = getOldNotifications.node();
			processOldNotifications(oldNotificationsNode);
		}
		catch(InterruptedException exception) {
			System.out.println("Interrupted");
		}
		catch(IOException exception) {
			System.out.println("An IO exception occured: " + exception.getMessage());
		}
		catch(NotificationProtocolClient.NotificationError exception) {
			System.out.println("An notification error occured: " + exception.getMessage());
		}
		catch(ClassCastException exception) {
			System.out.println("The server returned invalid data which could not be interpreted: " + exception.getMessage());
		}
	}
	
	public void run() {
		runTest();
		view.createAndUseView();
	}
	
	private void loadConfiguration(String path) throws IOException {
		configuration = new Configuration(path);
		setStoreData(
			configuration.trustStorePath,
			configuration.trustStorePassword,
			configuration.keyStorePath,
			configuration.keyStorePassword
		);
	}
	
	public void runGUI() {
	}
	
	public void runClient() {
		notificationThread.start();
		runGUI();
	}
}
