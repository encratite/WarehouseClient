package warehouseClient.protocolUnit;

import java.io.Serializable;

public class ServiceMessage implements Serializable {
	public String severity;
	public String message;
	
	public void setSeverity(String newSeverity) {
		severity = newSeverity;
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
}
