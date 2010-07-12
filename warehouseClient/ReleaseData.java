package warehouseClient;

import org.codehaus.jackson.JsonNode;

public class ReleaseData {
	public String site;
	public int siteId;
	public String name;
	public int time;
	public int size;
	public boolean isManual;
	
	public ReleaseData(JsonNode node) {
		site = node.path("site").getTextValue();
		siteId = node.path("id").getIntValue();
		name = node.path("name").getTextValue();
		time = node.path("time").getIntValue();
		size = node.path("size").getIntValue();
		isManual = node.path("isManual").getBooleanValue();
	}
}
