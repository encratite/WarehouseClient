package warehouseClient;

public class RemoteProcedureCallResult {
	public int id;
	public String error;
	public Object result;
	
	public void setId(int newId) {
		id = newId;
	}
	
	public void setError(String newError) {
		error = newError;
	}
	
	public void setResult(Object newResult) {
		result = newResult;
	}
}