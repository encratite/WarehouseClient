package warehouseClient;

import javax.swing.UIManager;

import ail.Column;
import ail.Table;
import ail.Window;

public class WarehouseClientView {
	private WarehouseClient client;
	
	public WarehouseClientView(WarehouseClient newClient) {
		client = newClient;
	}
	
	public void createAndUseView() {
		useWindowsTheme();
		
		Window mainWindow = new Window("Warehouse client", 640, 240);
		
		Column icon = new Column("Icon");
		int iconWidth = 35;
		icon.minimum.setSize(iconWidth);
		icon.maximum.setSize(iconWidth);
		Column description = new Column("Description");
		description.preferred.setSize(200);
		Column time = new Column("Time");
		time.preferred.setSize(40);
		
		Table notificationTable = new Table(icon, description, time);
		mainWindow.add(notificationTable.getPane());
		mainWindow.visualise();
	}
	
	private void useWindowsTheme() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		}
		catch(Exception exception) {
		}
	}
}
