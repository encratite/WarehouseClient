package main;

import java.io.IOException;

import warehouseClient.WarehouseClient;

public class Main {
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
