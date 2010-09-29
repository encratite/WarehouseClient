package warehouseClient;

import ail.DefaultCellRenderer;

public class NotificationDescription {
	public String description;
	public boolean isNew;
	
	public NotificationDescription(String newDescription, boolean newIsNew) {
		description = newDescription;
		isNew = newIsNew;
	}
}
