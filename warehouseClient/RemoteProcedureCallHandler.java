package warehouseClient;

import java.io.IOException;
import java.util.List;

import warehouseClient.NotificationProtocolClient.NotificationError;

public class RemoteProcedureCallHandler {
	private String method;
	private NotificationProtocolClient client;
	private Object rpcResult;
	
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
	
	public Object call(List<Object> arguments) throws IOException, NotificationError, InterruptedException {
		client.sendRPCData(method, arguments);
		//wait for the notification in receiveResult which the object receives from its NotificationProtocolClient
		wait();
		return rpcResult;
	}
	
	public void receiveResult(Object result) {
		rpcResult = result;
		notify();
	}
}
