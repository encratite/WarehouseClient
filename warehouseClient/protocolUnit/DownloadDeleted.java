package warehouseClient.protocolUnit;

import java.io.Serializable;

public class DownloadDeleted implements Serializable {
	public String release;
	public String reason;
	
	public void setRelease(String release) {
		this.release = release;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
}
