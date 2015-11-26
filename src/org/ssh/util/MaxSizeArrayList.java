package org.ssh.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that can be used for purposes where a maximum size an {@link ArrayList} is wenselijk. The
 * class extends {@link ArrayList} and overrides its functions that can be used to store elements in
 * the list.
 * 
 * @author Joost Overeem
 *         
 * @param <E>
 */
public class MaxSizeArrayList<E> extends ArrayList<E> {
    
    /**
     * The maximum size of the list.
     */
    private int maxSize;
    /**
     * The current position to add the next component. Starts at 0 again when {@link #maxSize} is
     * reached.
     */
    private int head;
                
    /**
     * Constructor with {@link #maxSize} default on 1500.
     */
    public MaxSizeArrayList() {
        super();
        maxSize = 1500;
        head = 0;
    }
    
    /**
     * Constructor where you can give the maxSize of the list.
     * 
     * @param maxSize
     *            The maximum size of the list (should be > 0, otherwise it will be set default on
     *            1500).
     */
    public MaxSizeArrayList(int maxSize) {
        super();
        // Set the maxSize if it bigger than 0
        if (maxSize > 0)
            this.maxSize = maxSize;
        // Otherwise we set it on 500
        else
            this.maxSize = 1500;
        // Set the head at the starting position 0
        head = 0;
    }
    
    /**
     * Adds an element by calling the super, but first checks if the {@link #size()} is already at
     * {@link #maxSize} (if so, the {@link #head} is set 0 again.
     */
    @Override
    public boolean add(E element) {
        if (this.size() < maxSize) head = 0;
        super.add(head, element);
        return true;
    }
    
    /**
     * Functions that calls {@link #add(Object)} and says f*ckyou to the index you give.
     */
    @Override
    public void add(int index, E element) {
        this.add(element);
    }
    
    /**
     * Function that calls the super function only if the collection fits, otherwise it returns
     * false.
     */
    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c.size() > maxSize) {
            return false;
        }
        else {
            E[] array = (E[]) c.toArray();
            for (int i = 0; i < array.length; i++)
                this.add(array[i]);
            return true;
        }
    }
    
    /**
     * Functions that calls {@link #addAll(Collection)} and says f*ckyou to the index you give.
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return addAll(c);
    }
    
    /**
     * Calls the super if index is smaller than {@link #maxSize}, otherwise returns the parameter
     * element.
     * 
     * @return if index > {@link #maxSize} it returns the super call (previous stored element at the
     *         index), otherwise it returns element.
     */
    @Override
    public E set(int index, E element) {
        if (index > maxSize)
            return super.set(index, element);
        else
            return element;
    }
}
