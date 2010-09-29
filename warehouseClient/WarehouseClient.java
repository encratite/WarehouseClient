package warehouseClient;

import java.io.FileNotFoundException;
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
	private NotificationStorage storage;
	
	private static String storagePath = "notifications.ser";
	
	public WarehouseClient(String configurationPath) throws IOException {
		loadConfiguration(configurationPath);
		protocolClient = new WarehouseProtocolHandler(this, configuration.notificationServerAddress, configuration.notificationServerPort);
		networkingThread = new Thread(protocolClient, "Networking thread");
		notificationThread = new Thread(this, "Notification thread");
		view = new WarehouseClientView(this);
	}
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private void processNotifications(JsonNode input) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		for(JsonNode node : input) {
			try {
				NotificationData notification = new NotificationData(node);
				print(notification.time.toString() + ": " + notification.description);
				addNotification(notification);
			}
			catch(IOException exception) {
				//ignore the ones which cannot be converted due to their invalid notification types from generateNotification and such
			}
		}
		writeStorage();
	}
	
	private void addNotification(NotificationData notification) {
		storage.notifications.add(notification);
		view.addNotification(notification);
	}
	
	private void writeStorage() {
		try {
			storage.store();
		}
		catch(FileNotFoundException exception) {
			print("Unable to open the notification storage file for writing");
		}
		catch(IOException exception) {
			print("Failed to write the serialised storage data to the notification storage file");
		}
	}
	
	private void loadNotificationData() throws InterruptedException, IOException, NotificationProtocolClient.NotificationError, ClassNotFoundException, ClassCastException {
		try {
			storage = NotificationStorage.load(storagePath);
		}
		catch(FileNotFoundException exception) {
			//the configuration file didn't exist yet - no problem
			storage = new NotificationStorage(storagePath);
		}
		
		//add old notifications to the GUI
		for(NotificationData i : storage.notifications) {
			view.addNotification(i);
		}
		
		RemoteProcedureCallHandler
			getNotificationCount = new RemoteProcedureCallHandler("getNotificationCount", protocolClient),
			getNotifications = new RemoteProcedureCallHandler("getNotifications", protocolClient);
		int count = (Integer)getNotificationCount.call();
		int lastCount = storage.lastNotificationCount;
		int newNotificationCount = count - lastCount;
		if(count > lastCount) {
			print("Number of new notifications: " + newNotificationCount);
			getNotifications.call(lastCount, newNotificationCount);
			JsonNode newNotificationsNode = getNotifications.node();
			processNotifications(newNotificationsNode);
		}
	}
	
	public void processNotification(NotificationData notification) {
		addNotification(notification);
		writeStorage();
	}
	
	//this function will be changed to print the data to a text window inside the GUI instead of the console
	public void print(String input) {
		System.out.println(input);
	}
	
	public void run() {
		try {
			protocolClient.connect();
		}
		catch(IOException exception) {
			print("Failed to connect: " + exception.getMessage());
		}
		networkingThread.start();
		try {
			loadNotificationData();
		}
		catch(InterruptedException exception) {
			print("Interrupted");
		}
		catch(IOException exception) {
			print("An IO exception occured: " + exception.getMessage());
		}
		catch(NotificationProtocolClient.NotificationError exception) {
			print("A notification error occured: " + exception.getMessage());
		}
		catch(ClassCastException exception) {
			print("The server returned invalid data which could not be interpreted: " + exception.getMessage());
		}
		catch(ClassNotFoundException exception) {
			print("Invalid configuration file!");
		}
		catch(Exception exception) {
			print("An exception occured: " + exception.getMessage());
		}
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
	
	public void runView() {
		view.createAndUseView();
	}
	
	public void runClient() {
		notificationThread.start();
		runView();
	}
}
