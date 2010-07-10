package warehouseClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	public String
		trustStorePath,
		trustStorePassword,
		
		keyStorePath,
		keyStorePassword;
	
	public String notificationServerAddress;
	public int notificationServerPort;
	
	public Configuration(String path) throws IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(path));
		
		trustStorePath = properties.getProperty("trustStorePath");
		trustStorePassword = properties.getProperty("trustStorePassword");
		
		keyStorePath = properties.getProperty("keyStorePath");
		keyStorePassword = properties.getProperty("keyStorePassword");
		
		notificationServerAddress = properties.getProperty("notificationServerAddress");
		notificationServerPort = Integer.getInteger(properties.getProperty("notificationServerPort"));
	}
}
