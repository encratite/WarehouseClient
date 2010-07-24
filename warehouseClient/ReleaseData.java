package warehouseClient;

import java.util.LinkedHashMap;

public class ReleaseData {
	public String site;
	public int siteId;
	public String name;
	public int time;
	public int size;
	public boolean isManual;
	
	//the constructor is required for the construction from the LinkedHashMap output from the TLS stream
	public ReleaseData(LinkedHashMap<String, Object> input) {
		site = (String)input.get("site");
		siteId = (Integer)input.get("siteId");
		name = (String)input.get("name");
		time = (Integer)input.get("time");
		size = (Integer)input.get("size");
		isManual = (Boolean)input.get("isManual");
	}
	
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