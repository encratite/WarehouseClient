package warehouseClient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import warehouseClient.protocolUnit.NotificationData;

public class NotificationStorage implements Serializable {
	public Vector<NotificationData> notifications;
	public int lastNotificationCount;
	public String path;
	
	public NotificationStorage(String newPath) {
		notifications = new Vector<NotificationData>();
		lastNotificationCount = 0;
		path = newPath;
	}
	
	public static NotificationStorage load(String path) throws FileNotFoundException, ClassNotFoundException, IOException {
		FileInputStream fileStream = new FileInputStream(path);
		ObjectInputStream objectStream = new ObjectInputStream(fileStream);
		NotificationStorage output = (NotificationStorage)objectStream.readObject();
		output.path = path;
		return output;
	}
	
	public void store() throws FileNotFoundException, IOException {
		FileOutputStream fileStream = new FileOutputStream(path);
		ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);
		objectStream.writeObject(this);
	}
	
	public void increaseCount() {
		lastNotificationCount += 1;
	}
}
