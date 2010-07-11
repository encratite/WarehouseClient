package warehouseClient;

public class ReleaseData {
	public String site;
	public int siteId;
	public String name;
	public int time;
	public int size;
	public boolean isManual;
	
	public void setSite(String newSite) {
		site = newSite;
	}
	
	public void setSiteId(int newSiteId) {
		siteId = newSiteId;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public void setTime(int newTime) {
		time = newTime;
	}
	
	public void setSize(int newSize) {
		size = newSize;
	}
	
	public void setIsManual(boolean newIsManual) {
		isManual = newIsManual;
	}
}
