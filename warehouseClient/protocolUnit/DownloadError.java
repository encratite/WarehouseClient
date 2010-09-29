package warehouseClient.protocolUnit;

import java.io.Serializable;


public class DownloadError implements Serializable {
	public ReleaseData release;
	public String message;
	
	public void setRelease(ReleaseData newRelease) {
		release = newRelease;
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
}
