package warehouseClient;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class WarehouseClient implements Runnable {
	private NotificationTest protocolClient;
	private Thread networkingThread, notificationThread;
	private Configuration configuration;
	
	public WarehouseClient(String configurationPath) throws IOException {
		loadConfiguration(configurationPath);
		protocolClient = new NotificationTest(configuration.notificationServerAddress, configuration.notificationServerPort);
		networkingThread = new Thread(protocolClient, "Networking thread");
		notificationThread = new Thread(this, "Notification thread");
	}
	
	private void setStoreData(String trustStorePath, String trustStorePassword, String keyStorePath, String keyStorePassword) {
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		System.setProperty("javax.net.ssl.keyStore", keyStorePath);
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
	}
	
	private void processOldNotifications(JsonNode input) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		for(JsonNode node : input) {
			NotificationData unit = new NotificationData(node);
			System.out.println(unit.description);
		}
	}
	
	private void runTest() {
		try {
			protocolClient.connect();
		}
		catch(IOException exception) {
			System.out.println("Failed to connect: " + exception.getMessage());
			System.exit(1);
		}
		networkingThread.start();
		try {
			RemoteProcedureCallHandler
				getNotificationCount = new RemoteProcedureCallHandler("getNotificationCount", protocolClient),
				getOldNotifications = new RemoteProcedureCallHandler("getOldNotifications", protocolClient);
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
		runTest();
	}
	
	private void loadConfiguration(String path) throws IOException {
		configuration = new Configuration(path);
		setStoreData(
			configuration.trustStorePath,
			configuration.trustStorePassword,
			configuration.keyStorePath,
			configuration.keyStorePassword
		);
	}
	
	public void runGUI() {
		Display display = new Display();
		Shell shell = new Shell(display);
        shell.setLayout(new FillLayout());
        Composite composite = new Composite(shell, SWT.NONE);
        composite.setLayout(new FillLayout());
        
        createTable(composite);

        shell.setSize(480, 300);
		shell.open();
		while(!shell.isDisposed()) {
			if(!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
		System.exit(0);
	}
	
	private void createTable(Composite parent) {
		final int columnCount = 3;
		
		Table table = new Table(parent, SWT.V_SCROLL);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = columnCount;
		table.setLayoutData(gridData);
		
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		int columnCounter = 0;
		
		TableColumn column = new TableColumn(table, SWT.CENTER, columnCounter++);		
		column.setText("Icon");
		column.setWidth(40);
		
		column = new TableColumn(table, SWT.LEFT, columnCounter++);
		column.setText("Description");
		column.setWidth(300);
		
		column = new TableColumn(table, SWT.LEFT, columnCounter++);
		column.setText("Time");
		column.setWidth(80);
	}
	
	public void runClient() {
		notificationThread.start();
		runGUI();
	}
	
	public static void main(String[] arguments) {
		try {
			WarehouseClient client = new WarehouseClient("warehouseClient.properties");
			client.runClient();
		}
		catch(IOException exception) {
			exception.printStackTrace();
		}
	}
}
