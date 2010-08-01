package warehouseClient;

import warehouseClient.protocolUnit.NotificationData;
import ail.Column;
import ail.Table;
import ail.Window;

public class WarehouseClientView {
	private WarehouseClient client;
	
	private Table notificationTable;
	
	public WarehouseClientView(WarehouseClient newClient) {
		client = newClient;
	}
	
	public void createAndUseView() {	
		final int
			totalWidth = 750,
			iconWidth = 35,
			timeWidth = 200;
		
		Window mainWindow = new Window("Warehouse client", totalWidth, 250);
		
		Column icon = new Column("Icon");
		icon.minimum.setSize(iconWidth);
		icon.maximum.setSize(iconWidth);
		Column description = new Column("Description");
		description.preferred.setSize(totalWidth - iconWidth - timeWidth);
		Column time = new Column("Time");
		time.preferred.setSize(timeWidth);
		
		notificationTable = new Table(icon, description, time);
		mainWindow.add(notificationTable.getPane());
		mainWindow.visualise();
	}
	
	public void addNotification(NotificationData notification) {
		Object[] row = new Object[3];
		row[0] = "";
		row[1] = notification.description;
		System.out.println("Test: " + notification.time);
		row[2] = notification.time.toString();
		
		notificationTable.addRow(row);
	}
}
