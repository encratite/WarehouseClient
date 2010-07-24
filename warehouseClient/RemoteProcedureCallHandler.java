package warehouseClient;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;

import warehouseClient.NotificationProtocolClient.NotificationError;

public class RemoteProcedureCallHandler {
	private String method;
	private NotificationProtocolClient client;
	private Object result;
	private JsonNode resultNode;
	private Object[] arguments;
	
	public RemoteProcedureCallHandler(String newMethod, NotificationProtocolClient newClient) {
		method = newMethod;
		client = newClient;
	}
	
	public synchronized Object call(Object... newArguments) throws IOException, NotificationError, InterruptedException {
		client.performRPC(this, newArguments);
		//wait for the notification in receiveResult which the object receives from its NotificationProtocolClient
		wait();
		return result;
	}
	
	public synchronized void receiveResult(Object newRpcResult, JsonNode newResultNode) {
		result = newRpcResult;
		resultNode = newResultNode;
		notify();
	}
	
	public String getMethod() {
		return method;
	}
	
	public JsonNode node() {
		return resultNode;
	}
}
