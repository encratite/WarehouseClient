package warehouseClient;

import java.util.ArrayList;

public class RemoteProcedureCallResult {
	public int id;
	public String method;
	public ArrayList<Object> params;
	
	public void setId(int newId) {
		id = newId;
	}
	
	public void setMethod(String newMethod) {
		method = newMethod;
	}
	
	public void setParams(ArrayList<Object> newParams) {
		params = newParams;
	}
}