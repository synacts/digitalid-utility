package net.digitalid.utility.functional.iterators;

import net.digitalid.utility.tuples.annotations.Pure;

/**
 * This interface models an iterator that returns an infinite number of elements.
 */
public interface InfiniteIterator<E> extends ReadOnlyIterator<E> {
    
    /* -------------------------------------------------- Overrided Operations -------------------------------------------------- */
    
    @Pure
    @Override
    public default boolean hasNext() {
        return true;
    }
    
}