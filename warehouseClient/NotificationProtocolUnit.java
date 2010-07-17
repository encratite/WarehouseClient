package warehouseClient;

public class NotificationProtocolUnit {
	public enum UnitType {
		queued,
		downloaded,
		downloadError,
		downloadDeleted,
		serviceMessage,
		rpcResult,
		error
	};
	
	public UnitType type;
	public Object data;
	
	public void setType(UnitType newType) {
		type = newType;
	}
	
	public void setData(Object newData) {
		data = newData;
	}
}
