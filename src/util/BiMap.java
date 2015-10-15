package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A one-to-one hashmap with a unique key and value
 * 
 * @author  http://stackoverflow.com/users/2219808/user2219808
 * @see 	http://stackoverflow.com/a/31636492
 *
 * @param <T1>	unique key
 * @param <T2>	unique value
 */
public class BiMap<T1, T2> implements Map<T1, T2>
{

    private final HashMap< T1, T2 > mapKeys = new HashMap<>();
    private final HashMap< T2, T1 > mapValues = new HashMap<>();
  
    /**
     * @return number of elements in this map
     */
    @Override
	public int size(){
    	return mapKeys.size();
    }

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return mapKeys.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return mapKeys.containsValue(value);
	}

	@Override
	public T2 get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T2 put(T1 key, T2 value) {
		T2 tmp = mapKeys.remove(key);
		mapValues.remove(tmp);

		mapKeys.put(key, value);
		mapValues.put(value, key);
		
		return tmp;
	}

	@Override
	public T2 remove(Object key) {
    	T2 value = mapKeys.remove(key);
    	mapValues.remove(value);
    	return value;
	}

    /**
     * Remove the value and his key
     * 
     * @param value to remove
     * @return previous key
     */
    public T1 removeByValue(T2 value){
    	T1 key = mapValues.remove(value);
    	mapKeys.remove(key);
    	return key;
    }

	@Override
	public void putAll(Map<? extends T1, ? extends T2> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		mapKeys.clear();
		mapValues.clear();
	}

	@Override
	public Set<T1> keySet() {
		return mapKeys.keySet();
	}

	@Override
	public Collection<T2> values() {
		return mapKeys.values();
	}

	public Set<T2> valueSet() {
		return mapValues.keySet();
	}

	public Collection<T1> keys() {
		return mapValues.values();
	}

	@Override
	public Set<Entry<T1, T2>> entrySet() {
    	return mapKeys.entrySet();
	}
    
    public  Set<Entry<T2, T1>> entrySetByValue(){
    	return mapValues.entrySet();
    }
}