package warehouseClient.protocolUnit;

import java.util.LinkedHashMap;

public class ReleaseData {
	public String site;
	public int siteId;
	public String name;
	public String time;
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
	
	public void setTime(String newTime) {
		time = newTime;
	}
	
	public void setSize(int newSize) {
		size = newSize;
	}
	
	public void setIsManual(boolean newIsManual) {
		isManual = newIsManual;
	}
}