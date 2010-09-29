package warehouseClient;

import java.awt.Rectangle;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import warehouseClient.protocolUnit.NotificationData;
import ail.Column;
import ail.Icon;
import ail.Table;
import ail.Window;

public class WarehouseClientView {
	private WarehouseClient client;
	
	private JScrollPane messageBoxScrollPane;
	
	private Table notificationTable;
	private JTextArea messageBox;
	
	private boolean isFirstMessage;
	
	public WarehouseClientView(WarehouseClient newClient) {
		client = newClient;
		
		//early construction required due to premature calls to print()
		messageBox = new JTextArea();
		isFirstMessage = true;
	}
	
	public void createAndUseView() {	
		final int
			totalWidth = 750,
			totalHeight = 350,
			
			iconWidth = 18,
			timeWidth = 120,
			
			mainIconSize = 32;
		
		final double
			splitRatio = 0.8;
		
		Icon mainIcon = loadIcon("application");
		mainIcon.resize(mainIconSize, mainIconSize);
		
		Window mainWindow = new Window("Warehouse client", totalWidth, totalHeight);
		mainWindow.setIconImage(mainIcon.icon.getImage());
		
		Column iconColumn = new Column("");
		iconColumn.minimum.setSize(iconWidth);
		iconColumn.maximum.setSize(iconWidth);
		
		Column descriptionColumn = new Column("Description");
		descriptionColumn.preferred.setSize(totalWidth - iconWidth - timeWidth);
		descriptionColumn.renderer = new NotificationDescriptionRenderer();
		
		Column timeColumn = new Column("Time");
		timeColumn.preferred.setSize(timeWidth);
		
		notificationTable = new Table(iconColumn, descriptionColumn, timeColumn);
		
		messageBox.setEditable(false);
		
		messageBoxScrollPane = new JScrollPane(messageBox);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, notificationTable.getPane(), messageBoxScrollPane);
		splitPane.setResizeWeight(splitRatio);
		
		mainWindow.add(splitPane);
		mainWindow.visualise();
	}
	
	private Icon loadIcon(String name) {
		String iconPath = "icon/" + name + ".png";
		Icon icon = new Icon(iconPath);
		return icon;
	}
	
	public void addNotification(NotificationData notification, boolean isNew) {
		final int size = 14;
		
		Object[] row = new Object[3];
		Icon icon = loadIcon(notification.icon);
		
		icon.resize(size, size);
		
		row[0] = icon;
		row[1] = new NotificationDescription(notification.description, isNew);
		row[2] = notification.time;
		
		notificationTable.addRow(row);
	}
	
	public void print(String message) {
		if(isFirstMessage)
			isFirstMessage = false;
		else
			messageBox.append("\n");
		messageBox.append(message);
		messageBox.setCaretPosition(messageBox.getText().length());
	}
}
