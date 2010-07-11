package warehouseClient;

import java.io.IOException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class WarehouseClient {
	private static void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
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
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
