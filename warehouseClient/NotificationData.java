package warehouseClient;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
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
	
	public String time;
	public NotificationType type;
	//the field "content" can't be used meaningfully in this context
	
	//extended variables deduced from parsing content:
	public ReleaseData releaseData;
	public DownloadError downloadError;
	public ServiceMessage serviceMessage;
	
	public String description;
	
	//this function processes the data stored in the field "content" which is a string containing JSON data
	public void initialise(JsonNode notificationDataNode) throws IOException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		JsonParser parser = notificationDataNode.get("content").traverse();
		switch(type) {
		case queued:
			description = "Release queued";
			releaseData = mapper.readValue(parser, ReleaseData.class);
			break;
			
		case downloaded:
			description = "Release downloaded";
			releaseData = mapper.readValue(parser, ReleaseData.class);
			break;
			
		case downloadError:
			description = "Download error";
			downloadError = mapper.readValue(parser, DownloadError.class);
			break;
			
		case downloadDeleted:
			description = "Download deleted";
			releaseData = mapper.readValue(parser, ReleaseData.class);
			break;
			
		case serviceMessage:
			description = "Service message";
			serviceMessage = mapper.readValue(parser, ServiceMessage.class);
			break;
		}
	}
	
	public void setTime(String newTime) {
		time = newTime;
	}
	
	public void setType(NotificationType newType) {
		type = newType;
	}
	
	public void setContent(Object newContent) {
	}
}
