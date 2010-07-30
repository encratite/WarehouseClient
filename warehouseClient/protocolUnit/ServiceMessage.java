package warehouseClient.protocolUnit;

public class ServiceMessage {
	public String severity;
	public String message;
	
	public void setSeverity(String newSeverity) {
		severity = newSeverity;
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
}
