package warehouseClient;

import java.io.IOException;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class WarehouseClient implements Runnable {
	private NotificationTest test;
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private void processOldNotifications(JsonNode input) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		for(JsonNode node : input) {
			/*
			for(Iterator<String> i = node.getFieldNames(); i.hasNext();) {
				String field = i.next();
				System.out.println("Field: " + field + ": " + node.get(field).toString());
			}
			System.out.println(node.get("content").toString());
			*/
			NotificationData unit = mapper.readValue(node.traverse(), NotificationData.class);
			unit.initialise(node);
			System.out.println(unit.description);
		}
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
			RemoteProcedureCallHandler
				getNotificationCount = new RemoteProcedureCallHandler("getNotificationCount", test),
				getOldNotifications = new RemoteProcedureCallHandler("getOldNotifications", test);
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
