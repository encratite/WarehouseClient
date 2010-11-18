package warehouseClient.protocolUnit;

import java.io.Serializable;

public class DownloadError implements Serializable {
	public String release;
	public String message;
	
	public void setRelease(String release) {
		this.release = release;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
