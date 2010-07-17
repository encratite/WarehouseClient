package warehouseClient;

import java.io.IOException;
import java.util.List;

import warehouseClient.NotificationProtocolClient.NotificationError;

public class RemoteProcedureCallHandler {
	private String method;
	private NotificationProtocolClient client;
	public Object rpcResult;
	private List<Object> arguments;
	
	public class RemoteProcedureCallException extends Exception {
		public RemoteProcedureCallException() {
			super();
		}
		
		public RemoteProcedureCallException(String message) {
			super(message);
		}
	}
	
	public RemoteProcedureCallHandler(String newMethod, NotificationProtocolClient newClient) {
		method = newMethod;
		client = newClient;
	}
	
	public synchronized Object call(List<Object> newArguments) throws IOException, NotificationError, InterruptedException {
		client.performRPC(this, newArguments);
		//wait for the notification in receiveResult which the object receives from its NotificationProtocolClient
		wait();
		return rpcResult;
	}
	
	public synchronized void receiveResult(Object newRpcResult) {
		rpcResult = newRpcResult;
		notify();
	}
	
	public String getMethod() {
		return method;
	}
}
