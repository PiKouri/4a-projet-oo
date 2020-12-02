import java.util.HashMap;
import java.util.Map;

public class MyMap <ThisUser, Value> {
	private Map<ThisUser,Value> map1;
	private Map<Value,ThisUser> map2;
	
	// Constructor
	
	public MyMap() {
		this.map1 = new HashMap<>();
		this.map2 = new HashMap<>();
	}
	
	// isEmpty
	
	public boolean isEmpty() {
		return map1.isEmpty();
	}
	
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
	
	public void putUser(ThisUser v1, Value v2) {
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
	
	public void replaceValue (ThisUser v1, Value newV2) {
		Value oldV2 = this.map1.get(v1);
		this.map1.replace(v1, newV2);
		this.map2.remove(oldV2);
		this.map2.put(newV2, v1);
	}
	
	// Size
	
	public int size() {
		return this.map1.size();
	}
	
	// Contains
	
	public boolean containsUser(ThisUser v1) {
		return this.map1.containsKey(v1);
	}
	
	public boolean containsValue(Value v2) {
		return this.map1.containsValue(v2);
	}
}
