package org.ssh.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A one-to-one hashmap with a unique key and value
 * 
 * TODO
 * Replace with {@link com.google.common.collect.BiMap}
 *
 * @author http://stackoverflow.com/users/2219808/user2219808
 * @see http://stackoverflow.com/a/31636492
 *     
 * @param <T1>
 *            unique key
 * @param <T2>
 *            unique value
 */
public class BiMap<T1, T2> implements Map<T1, T2> {
    
    private HashMap<T1, T2> mapKeys   = new HashMap<>();
    private HashMap<T2, T1> mapValues = new HashMap<>();
                                      
    public BiMap() {
    }

    public BiMap(final Object object, final Object object2) {
        this.mapKeys = (HashMap<T1, T2>) object;
        this.mapValues = (HashMap<T2, T1>) object2;
    }

    @Override
    public void clear() {
        this.mapKeys.clear();
        this.mapValues.clear();
    }
    
    @Override
    public BiMap<T1, T2> clone() {
        return new BiMap(this.mapKeys.clone(), this.mapValues.clone());
    }
    
    @Override
    public boolean containsKey(final Object key) {
        return this.mapKeys.containsKey(key);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return this.mapKeys.containsValue(value);
    }
    
    @Override
    public Set<Entry<T1, T2>> entrySet() {
        return this.mapKeys.entrySet();
    }
    
    public Set<Entry<T2, T1>> entrySetByValue() {
        return this.mapValues.entrySet();
    }
    
    @Override
    public T2 get(final Object key) {
        return this.mapKeys.get(key);
    }
    
    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no
     * mapping for the key.
     *
     * More formally, if this map contains a mapping from a key k to a value v such that (key==null
     * ? k==null : key.equals(k)), then this method returns v; otherwise it returns null. (There can
     * be at most one such mapping.)
     *
     * If this map permits null values, then a return value of null does not necessarily indicate
     * that the map contains no mapping for the key; it's also possible that the map explicitly maps
     * the key to null. The containsKey operation may be used to distinguish these two cases.
     * 
     * @param value
     *            the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no
     *         mapping for the key
     */
    public T1 getbyValue(final Object value) {
        return this.mapValues.get(value);
    }
    
    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public Collection<T1> keys() {
        return this.mapValues.values();
    }
    
    @Override
    public Set<T1> keySet() {
        return this.mapKeys.keySet();
    }
    
    @Override
    public T2 put(final T1 key, final T2 value) {
        final T2 tmp = this.mapKeys.remove(key);
        this.mapValues.remove(tmp);
        
        this.mapKeys.put(key, value);
        this.mapValues.put(value, key);
        
        return tmp;
    }
    
    @Override
    public void putAll(final Map<? extends T1, ? extends T2> m) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public T2 remove(final Object key) {
        final T2 value = this.mapKeys.remove(key);
        this.mapValues.remove(value);
        return value;
    }
    
    /**
     * Remove the value and his key
     *
     * @param value
     *            to remove
     * @return previous key
     */
    public T1 removeByValue(final T2 value) {
        final T1 key = this.mapValues.remove(value);
        this.mapKeys.remove(key);
        return key;
    }
    
    /**
     * @return number of elements in this map
     */
    @Override
    public int size() {
        return this.mapKeys.size();
    }

    @Override
    public Collection<T2> values() {
        return this.mapKeys.values();
    }

    public Set<T2> valueSet() {
        return this.mapValues.keySet();
    }
}