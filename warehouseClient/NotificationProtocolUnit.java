package warehouseClient;

public class NotificationProtocolUnit {
	public enum UnitType {
		queued,
		downloaded,
		downloadError,
		downloadDeleted,
		serviceMessage,
		rpcResult
	};
	
	public UnitType type;
	
	public void setType(UnitType newType) {
		type = newType;
	}
}
