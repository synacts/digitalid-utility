package net.digitalid.utility.functional.iterable;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;

import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.method.Pure;
import net.digitalid.utility.validation.annotations.type.Immutable;

/**
 *
 */
@Immutable
class WrappingNonNullIterable<T> extends NonNullIterable<T> {
    
    private final @Nonnull @NonNullableElements Iterable<T> iterable;
    
    /**
     * The size of the iterable.
     */
    private final int size;
    
    WrappingNonNullIterable(@Nonnull NonNullIterable<T> iterable) {
        this.iterable = iterable;
        this.size = iterable.size();
    }
    
    /**
     * Creates a wrapper around the original collection.
     */
    WrappingNonNullIterable(@Nonnull @NonNullableElements Collection<T> collection) {
        this.iterable = collection;
        this.size = collection.size();
    }
    
    @Pure
    @Override
    public @Nonnull @NonNullableElements Iterator<T> iterator() {
        return iterable.iterator();
    }
    
    /* -------------------------------------------------- Size -------------------------------------------------- */
    
    @Override
    public int size() {
        return 0;
    }
}
