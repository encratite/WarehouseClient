package warehouseClient.protocolUnit;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


//this is the actual notification data found inside a NotficationProtocolUnit whose type is equal to "notification"
public class NotificationData {
	public enum NotificationType {
		queued,
		downloaded,
		downloadError,
		downloadDeleted,
		serviceMessage,
	};
	
	public Date time;
	public NotificationType type;
	//the field "content" can't be used meaningfully in this context
	
	//extended variables deduced from parsing content:
	public ReleaseData releaseData;
	public DownloadError downloadError;
	public ServiceMessage serviceMessage;
	
	public String description;
	
	public NotificationData() {
	}

	public NotificationData(JsonNode notificationDataNode) throws IOException, JsonMappingException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		NotificationData data = mapper.readValue(notificationDataNode.traverse(), NotificationData.class);
		type = data.type;
		time = data.time;
		initialise(notificationDataNode);
	}
	
	//this function processes the data stored in the field "content" which is a string containing JSON data
	private void initialise(JsonNode notificationDataNode) throws IOException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonParser parser = notificationDataNode.get("content").traverse();
		switch(type) {
		case queued:
			releaseData = mapper.readValue(parser, ReleaseData.class);
			description = "Release queued: " + releaseData.name;
			break;
			
		case downloaded:
			releaseData = mapper.readValue(parser, ReleaseData.class);
			description = "Release downloaded: " + releaseData.name;
			break;
			
		case downloadError:
			downloadError = mapper.readValue(parser, DownloadError.class);
			description = "Download error in release + " + downloadError.release.name + ": " + downloadError.message;
			break;
			
		case downloadDeleted:
			releaseData = mapper.readValue(parser, ReleaseData.class);
			description = "Download deleted: " + releaseData.name;
			break;
			
		case serviceMessage:
			serviceMessage = mapper.readValue(parser, ServiceMessage.class);
			description = "Service message of level " + serviceMessage.severity + ": " + serviceMessage.message;
			break;
		}
	}
	
	public void setTime(String timeString) throws ParseException {
		SimpleTimeZone utc = new SimpleTimeZone(0, "UTC");
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateForm.setTimeZone(utc);
		time = dateForm.parse(timeString);
	}
	
	public void setType(NotificationType newType) {
		type = newType;
	}
	
	public void setContent(Object newContent) {
	}
}
