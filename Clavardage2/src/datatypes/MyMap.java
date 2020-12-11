package datatypes;
import java.util.HashMap;
import java.util.Map;

public class MyMap <ThisUser, Value> {
	private Map<ThisUser,Value> map1;
	private Map<Value,ThisUser> map2;
	
	
/*-----------------------Constructeurs-------------------------*/
	
	
	/**
     * Constructor for the class MyMap
     * <p>This class implements a bijective Map
     * (for example < User,String > to associate users and usernames)
     */
	public MyMap() {
		this.map1 = new HashMap<>();
		this.map2 = new HashMap<>();
	}
	
	
/*-----------------------Méthodes - Utilitaires-------------------------*/
	
	
	/**
	 * This method return true if the MyMap is empty
	 * @return True if empty
	 */
	public boolean isEmpty() {
		return map1.isEmpty();
	}
	
	/**
	 * This method return the value associated to the user
	 * 
	 * @param user The user from who we want to see the value
	 * 
	 * @return the value associated to the user
	 */
	public Value getValue(ThisUser user) {
		return this.map1.get(user);
	}
	
	/**
	 * This method return the user associated to the value
	 * 
	 * @param value The value that we want to find the associated user
	 * 
	 * @return the user associated to the value
	 */
	public ThisUser getUser(Value value) {
		return this.map2.get(value);
	}
	
	/**
	 * This method empties the MyMap
	 */
	public void clear() {
		this.map1.clear();
		this.map2.clear();
	}
	
	/**
	 * This method adds an association of a user and a value
	 */
	public void putUser(ThisUser user, Value value) {
		this.map1.put(user,value);
		this.map2.put(value,user);
	}
	
	/**
	 * This method removes a user (and its associated value)
	 * 
	 * @param user The user that we want to remove
	 */
	public void remove(ThisUser user) {
		Value value = this.getValue(user);
		this.map1.remove(user);
		this.map2.remove(value);
	}
	
	/**
	 * This method replaces the value associated to a user with a new one
	 * 
	 * @param user The user from who we want to replace the value
	 * @param newValue The new value that we want to associate to the user
	 */
	public void replaceValue (ThisUser user, Value newValue) {
		Value oldValue = this.map1.get(user);
		this.map1.replace(user, newValue);
		this.map2.remove(oldValue);
		this.map2.put(newValue, user);
	}
	
	/**
	 * This method return the size of the MyMap
	 * 
	 * @return The size of the MyMap
	 */
	public int size() {
		return this.map1.size();
	}
	
	/**
	 * This method return true if the user is in the MyMap
	 * 
	 * @return True if the user is in the MyMap
	 */
	public boolean containsUser(ThisUser user) {
		return this.map1.containsKey(user);
	}
	
	/**
	 * This method return true if the value is in the MyMap
	 * 
	 * @return True if the value is in the MyMap
	 */
	public boolean containsValue(Value value) {
		return this.map2.containsKey(value);
	}
}
