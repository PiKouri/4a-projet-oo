import java.util.Map;

public class MyMap <ThisUser, Value> {
	private Map<ThisUser,Value> map1;
	private Map<Value,ThisUser> map2;
	
	// Get
	
	public Value getValue(ThisUser key) {
		return this.map1.get(key);
	}
	
	public ThisUser getUser(Value key) {
		return this.map2.get(key);
	}
	
	// Clear
	
	public void clear() {
		this.map1.clear();
		this.map2.clear();
	}
	
	// Put
	
	public void putV1(ThisUser v1, Value v2) {
		this.map1.put(v1,v2);
		this.map2.put(v2,v1);
	}
	
	// Remove
	
	public void remove(ThisUser v1) {
		Value v2 = this.getValue(v1);
		this.map1.remove(v1);
		this.map2.remove(v2);
	}
	
	// Replace
	
	public void replace (ThisUser v1, Value v2) {
		this.map1.replace(v1, v2);
		this.map2.replace(v2, v1);
	}
	
	// Size
	
	public int size() {
		return this.map1.size();
	}
}
