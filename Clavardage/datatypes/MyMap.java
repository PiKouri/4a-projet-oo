import java.util.Map;
public class MyMap <V1, V2> {
	private Map<V1,V2> map1;
	private Map<V2,V1> map2;
	
	public MyMap();
	
	// Get
	
	public V2 get(V1 key) {
		return this.map1.get(key);
	}
	
	public V1 get(V2 key) {
		return this.map2.get(key);
	}
	
	// Clear
	
	public void clear() {
		this.map1.clear();
		this.map2.clear();
	}
	
	// Put
	
	public void put(V1 v1, V2 v2) {
		this.map1.put(v1,v2);
		this.map2.put(v2,v1);
	}
	
	public void put (V2 v2, V1 v1) {
		this.put(v1,v2);
	}
	
	// Remove
	
	public void remove(V1 v1) {
		V2 v2 = this.get(v1);
		this.map1.remove(v1);
		this.map2.remove(v2);
	}
	
	public void remove(V2 v2) {
		V1 v1 = this.get(v2);
		this.map1.remove(v1);
		this.map2.remove(v2);
	}
	
	// Replace
	
	public void replace (V1 v1, V2 v2) {
		this.map1.replace(v1, v2);
		this.map2.replace(v2, v1);
	}
	
	public void replace (V2 v2, V1 v1) {
		this.replace(v1,v2);
	}
}
