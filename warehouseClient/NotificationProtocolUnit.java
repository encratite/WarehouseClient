package warehouseClient;

//this is the outer most data type of the protocol in which all the other data is wrapped 
public class NotificationProtocolUnit {
	public enum UnitType {
		notification,
		rpcResult,
		error,
	};
	
	public UnitType type;
	//the field "data" cannot be used meaningfully in this context
	
	public void setType(UnitType newType) {
		type = newType;
	}
	
	public void setData(Object newData) {
	}
}
