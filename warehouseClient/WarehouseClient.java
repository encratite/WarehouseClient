package warehouseClient;

import java.io.IOException;

public class WarehouseClient {
	private static void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private static void runTest(Configuration configuration) {
		NotificationTest test = new NotificationTest(configuration.notificationServerAddress, configuration.notificationServerPort);
		try {
			test.connect();
		}
		catch(IOException exception) {
			System.out.println("Failed to connect: " + exception.getMessage());
			System.exit(1);
		}
		while(true) {
			try {
				test.processUnit();
			}
			catch(NotificationProtocolClient.NotificationError exception) {
				System.out.println("A notification client exception occured: " + exception.getMessage());
				System.exit(2);
			}
		}
	}
	
	public static void main(String[] arguments) {
		try {
			Configuration configuration = new Configuration("warehouseClient.properties");
			setStoreData(
				configuration.trustStorePath,
				configuration.trustStorePassword,
				configuration.keyStorePath,
				configuration.keyStorePassword
			);
			runTest(configuration);
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
