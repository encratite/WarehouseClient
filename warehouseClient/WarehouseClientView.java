package warehouseClient;

import warehouseClient.protocolUnit.NotificationData;
import ail.Column;
import ail.Table;
import ail.Window;
import ail.Icon;

public class WarehouseClientView {
	private WarehouseClient client;
	
	private Table notificationTable;
	
	public WarehouseClientView(WarehouseClient newClient) {
		client = newClient;
	}
	
	public void createAndUseView() {	
		final int
			totalWidth = 750,
			iconWidth = 18,
			timeWidth = 120,
			
			mainIconSize = 32;
		
		Icon mainIcon = loadIcon("application");
		mainIcon.resize(mainIconSize, mainIconSize);
		
		Window mainWindow = new Window("Warehouse client", totalWidth, 250);
		mainWindow.setIconImage(mainIcon.icon.getImage());
		
		Column iconColumn = new Column("");
		iconColumn.minimum.setSize(iconWidth);
		iconColumn.maximum.setSize(iconWidth);
		
		Column descriptionColumn = new Column("Description");
		descriptionColumn.preferred.setSize(totalWidth - iconWidth - timeWidth);
		
		Column timeColumn = new Column("Time");
		timeColumn.preferred.setSize(timeWidth);
		
		notificationTable = new Table(iconColumn, descriptionColumn, timeColumn);
		mainWindow.add(notificationTable.getPane());
		mainWindow.visualise();
	}
	
	private Icon loadIcon(String name) {
		String iconPath = "icon/" + name + ".png";
		Icon icon = new Icon(iconPath);
		return icon;
	}
	
	public void addNotification(NotificationData notification) {
		Object[] row = new Object[3];
		Icon icon = loadIcon(notification.icon);
		final int size = 14;
		icon.resize(size, size);
		row[0] = icon;
		row[1] = notification.description;
		row[2] = notification.time;
		
		notificationTable.addRow(row);
	}
}
