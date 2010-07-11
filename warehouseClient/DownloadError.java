package warehouseClient;

public class DownloadError {
	public ReleaseData release;
	public String message;
	
	public void setRelease(ReleaseData newRelease) {
		release = newRelease;
	}
	
	public void setMessage(String newMessage) {
		message = newMessage;
	}
}
