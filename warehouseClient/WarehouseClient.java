package warehouseClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class WarehouseClient implements Runnable {
	private NotificationTest test;
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private void runTest(Configuration configuration) {
		test = new NotificationTest(configuration.notificationServerAddress, configuration.notificationServerPort);
		try {
			test.connect();
		}
		catch(IOException exception) {
			System.out.println("Failed to connect: " + exception.getMessage());
			System.exit(1);
		}
		Thread clientThread = new Thread(this, "NotificationClientThread");
		clientThread.start();
		try {
			RemoteProcedureCallHandler<Integer> getNotificationCount = new RemoteProcedureCallHandler<Integer>("getNotificationCount", test, Integer.class);
			RemoteProcedureCallHandler<ArrayList> getOldNotifications = new RemoteProcedureCallHandler<ArrayList>("getOldNotifications", test, ArrayList.class);
			int count = getNotificationCount.call();
			System.out.println("Number of messages: " + count);
			ArrayList<LinkedHashMap> notifications = (ArrayList<LinkedHashMap>)getOldNotifications.call(0, Math.max(count - 1, 0));
			//System.out.println("getOldNotifications: " + notifications.get(0).getClass().toString());
			for(LinkedHashMap<String, Object> map : notifications) {
				try {
					ReleaseData releaseData = new ReleaseData(map);
					System.out.println(releaseData.name);
				}
				catch(NullPointerException exception) {
					for(String key : map.keySet()) {
						System.out.println("Key: " + key);
					}
					System.exit(1);
				}
			}
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
		catch(RemoteProcedureCallException exception) {
			System.out.println("An RPC exception occured: " + exception.getMessage());
		}
		catch(ClassCastException exception) {
			System.out.println("The server returned invalid data which could not be interpreted: " + exception.getMessage());
		}
	}
	
	public void run() {
		try {
			while(true) {
				test.processUnit();
			}
		}
		catch(NotificationProtocolClient.NotificationError exception) {
			System.out.println("A notification client exception occured: " + exception.getMessage());
		}
	}
	
	public static void main(String[] arguments) {
		try {
			Configuration configuration = new Configuration("warehouseClient.properties");
			WarehouseClient client = new WarehouseClient();
			client.setStoreData(
				configuration.trustStorePath,
				configuration.trustStorePassword,
				configuration.keyStorePath,
				configuration.keyStorePassword
			);
			client.runTest(configuration);
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
