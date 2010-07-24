package warehouseClient;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;

import warehouseClient.NotificationProtocolClient.NotificationError;

public class RemoteProcedureCallHandler<ReturnType> {
	private String method;
	private NotificationProtocolClient client;
	private Object result;
	private JsonNode resultNode;
	private Object[] arguments;
	private Class<ReturnType> returnTypeClass;
	
	public RemoteProcedureCallHandler(String newMethod, NotificationProtocolClient newClient, Class<ReturnType> newReturnTypeClass) {
		method = newMethod;
		client = newClient;
		returnTypeClass = newReturnTypeClass;
	}
	
	public synchronized ReturnType call(Object... newArguments) throws IOException, NotificationError, InterruptedException, RemoteProcedureCallException {
		client.performRPC(this, newArguments);
		//wait for the notification in receiveResult which the object receives from its NotificationProtocolClient
		wait();
		Class resultClass = result.getClass();
		if(!resultClass.equals(returnTypeClass))
			throw new RemoteProcedureCallException("The server returned an invalid class: " + resultClass.toString() + " (expected " + returnTypeClass.toString() + ")");
		return (ReturnType)result;
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
