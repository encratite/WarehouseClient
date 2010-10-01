package warehouseClient;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import warehouseClient.NotificationProtocolClient.NotificationError;
import warehouseClient.protocolUnit.NotificationData;
import warehouseClient.protocolUnit.NotificationData.NotificationType;
import ail.SoundPlayer;

public class WarehouseClient implements Runnable, NotificationProtocolClient.ExceptionHandler {
	private WarehouseProtocolHandler protocolClient;
	private Thread networkingThread, notificationThread;
	private Configuration configuration;
	private WarehouseClientView view;
	private NotificationStorage storage;
	
	private static String storagePath = "notifications.ser";
	
	private SoundPlayer
		notificationSound,
		errorSound;
	
	enum NewNotificationsResult {
		noNewNotifications,
		gotNewNotifications,
		anErrorOccured,
	}
	
	public WarehouseClient(String configurationPath) throws IOException {
		loadConfiguration(configurationPath);
		protocolClient = new WarehouseProtocolHandler(this, configuration.notificationServerAddress, configuration.notificationServerPort);
		networkingThread = new Thread(protocolClient, "Networking thread");
		notificationThread = new Thread(this, "Notification thread");
		view = new WarehouseClientView(this);
		
		notificationSound = getSound("notification");
		errorSound = getSound("error");
	}
	
	private SoundPlayer getSound(String base) {
		return new SoundPlayer("sound/" + base + ".wav");
	}
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	//returns if any new notifications were added
	private NewNotificationsResult processNotifications(JsonNode input) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		NewNotificationsResult output = NewNotificationsResult.noNewNotifications;
		for(JsonNode node : input) {
			synchronized(storage) {
				try {
					NotificationData notification = new NotificationData(node);
					//print(notification.time.toString() + ": " + notification.description);
					addNotification(notification, true);

					if(notification.isNegative())
						output = NewNotificationsResult.anErrorOccured;
					else if(output != NewNotificationsResult.anErrorOccured)
						output = NewNotificationsResult.gotNewNotifications;
				}
				catch(IOException exception) {
					//ignore the ones which cannot be converted due to their invalid notification types from generateNotification and such
					storage.increaseCount();
				}
			}
		}
		writeStorage();
		return output;
	}
	
	private void addNotification(NotificationData notification, boolean isNew) {
		synchronized(storage) {
			storage.notifications.add(notification);
			storage.increaseCount();
		}
		view.addNotification(notification, isNew);
	}
	
	private void writeStorage() {
		try {
			storage.store();
		}
		catch(FileNotFoundException exception) {
			print("Unable to open the notification storage file for writing: " + exception.getMessage());
		}
		catch(IOException exception) {
			print("Failed to write the serialised storage data to the notification storage file: " + exception.getMessage());
		}
	}
	
	private void loadNotificationData() throws InterruptedException, IOException, NotificationProtocolClient.NotificationError, ClassNotFoundException, ClassCastException {
		try {
			storage = NotificationStorage.load(storagePath);
			print("Loaded " + storage.notifications.size() + " notifications from the notification storage file");
		}
		catch(FileNotFoundException exception) {
			//the configuration file didn't exist yet - no problem
			storage = new NotificationStorage(storagePath);
		}
		
		//add old notifications to the GUI
		for(NotificationData i : storage.notifications)
			view.addNotification(i, false);
		
		RemoteProcedureCallHandler
			getNotificationCount = new RemoteProcedureCallHandler("getNotificationCount", protocolClient),
			getNotifications = new RemoteProcedureCallHandler("getNotifications", protocolClient);
		
		int count = (Integer)getNotificationCount.call();
		int lastCount = storage.lastNotificationCount;
		int newNotificationCount = count - lastCount;
		if(count > lastCount) {
			print("Number of new notifications: " + newNotificationCount);
			getNotifications.call(0, newNotificationCount);
			JsonNode newNotificationsNode = getNotifications.node();
			switch(processNotifications(newNotificationsNode)) {
			case gotNewNotifications:
				notificationSound.play();
				break;
			case anErrorOccured:
				errorSound.play();
				break;
			}
		}
		print("Last notification counts: " + lastCount + " in the storage file, " + count + " on the server");
	}
	
	//this function is called by the protocol client when a new notification arrives
	public void processNotification(NotificationData notification) {
		addNotification(notification, true);
		writeStorage();
		if(notification.isNegative())
			errorSound.play();
		else
			notificationSound.play();
	}
	
	public void print(String input) {
		view.print(input);
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
			exception.printStackTrace();
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
			print("An exception of type " + exception.getClass().toString() + " occured: " + exception.getMessage());
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
	
	public void handleNotificationServerDisconnect() {
		print("Disconnected");
		errorSound.play();
	}
	
	public void handleCriticalNotificationError(NotificationError error) {
		print("A critical error occured: " + error.getMessage());
		errorSound.play();
	}
}
