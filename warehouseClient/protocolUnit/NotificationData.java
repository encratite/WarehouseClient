package warehouseClient.protocolUnit;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import ail.Time;

//this is the actual notification data found inside a NotficationProtocolUnit whose type is equal to "notification"
public class NotificationData implements Serializable {
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
	public DownloadDeleted downloadDeleted;
	
	transient public String description;
	transient public String icon;
	
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
			break;
			
		case downloaded:
			releaseData = mapper.readValue(parser, ReleaseData.class);
			break;
			
		case downloadError:
			downloadError = mapper.readValue(parser, DownloadError.class);
			break;
			
		case downloadDeleted:
			downloadDeleted = mapper.readValue(parser, DownloadDeleted.class);
			break;
			
		case serviceMessage:
			serviceMessage = mapper.readValue(parser, ServiceMessage.class);
			break;
		}
		
		initialiseTransientMembers();
	}
	
	public boolean isNegative() {
		switch(type) {
		case downloadError:
		case serviceMessage:
			return true;
		default:
			return false;
		}
	}
	
	public void initialiseTransientMembers() {
		switch(type) {
		case queued:
			description = "Release queued: " + releaseData.name;
			icon = "release-queued";
			break;
			
		case downloaded:
			description = "Release downloaded: " + releaseData.name;
			icon = "release-downloaded";
			break;
			
		case downloadError:
			description = "Download error in release " + downloadError.release + ": " + downloadError.message;
			icon = "download-error";
			break;
			
		case downloadDeleted:
			description = "Download " + downloadDeleted.release + " was deleted: " + downloadDeleted.reason;
			icon = "download-deleted";
			break;
			
		case serviceMessage:
			description = "Service message of level \"" + serviceMessage.severity + "\": " + serviceMessage.message;
			icon = "service-message";
			break;
		}
	}
	
	public void setTime(long timestamp) {
		time = Time.convertTimestamp(timestamp);
	}
	
	public SimpleDateFormat getDateForm() {
		SimpleTimeZone utc = new SimpleTimeZone(0, "UTC");
		SimpleDateFormat dateForm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateForm.setTimeZone(utc);
		return dateForm;
	}
	
	public void setType(NotificationType newType) {
		type = newType;
	}
	
	public void setContent(Object newContent) {
	}
}
