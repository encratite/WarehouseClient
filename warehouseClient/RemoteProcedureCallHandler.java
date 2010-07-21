package warehouseClient;

import java.io.IOException;
import java.util.List;

import warehouseClient.NotificationProtocolClient.NotificationError;

public class RemoteProcedureCallHandler<ReturnType> {
	private String method;
	private NotificationProtocolClient client;
	public Object rpcResult;
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
		Class resultClass = rpcResult.getClass();
		if(!resultClass.equals(returnTypeClass))
			throw new RemoteProcedureCallException("The server returned an invalid class: " + resultClass.toString() + " (expected " + returnTypeClass.toString() + ")");
		return (ReturnType)rpcResult;
	}
	
	public synchronized void receiveResult(Object newRpcResult) {
		rpcResult = newRpcResult;
		notify();
	}
	
	public String getMethod() {
		return method;
	}
}
