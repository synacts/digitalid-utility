package net.digitalid.utility.collections.freezable;

import java.util.Arrays;
import java.util.HashSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.digitalid.utility.annotations.math.NonNegative;
import net.digitalid.utility.annotations.reference.Capturable;
import net.digitalid.utility.annotations.reference.Captured;
import net.digitalid.utility.annotations.state.Immutable;
import net.digitalid.utility.annotations.state.Pure;
import net.digitalid.utility.collections.annotations.freezable.Frozen;
import net.digitalid.utility.collections.annotations.freezable.NonFrozen;
import net.digitalid.utility.collections.annotations.freezable.NonFrozenRecipient;
import net.digitalid.utility.collections.annotations.index.ValidIndex;
import net.digitalid.utility.collections.converter.Brackets;
import net.digitalid.utility.collections.converter.IterableConverter;
import net.digitalid.utility.collections.readonly.ReadOnlyArray;

/**
 * This class models {@link Freezable freezable} arrays.
 * <p>
 * <em>Important:</em> Only use freezable or immutable types for the elements!
 * (The type is not restricted to {@link Freezable} or {@link Immutable} so that library types can also be used.)
 * 
 * @author Kaspar Etter (kaspar.etter@digitalid.net)
 * @version 1.0.0
 */
public class FreezableArray<E> extends FreezableObject implements ReadOnlyArray<E>, FreezableIterable<E> {
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Field –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Stores the elements in an array.
     */
    private final @Nonnull E[] array;
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Constructors –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Creates a new freezable array with the given size.
     * 
     * @param size the size of the newly created array.
     */
    @SuppressWarnings("unchecked")
    protected FreezableArray(@NonNegative int size) {
        array = (E[]) new Object[size];
    }
    
    /**
     * Creates a new freezable array with the given size.
     * 
     * @param size the size of the newly created array.
     * 
     * @return a new freezable array with the given size.
     */
    @Pure
    public static @Capturable @Nonnull @NonFrozen <E> FreezableArray<E> get(@NonNegative int size) {
        return new FreezableArray<>(size);
    }
    
    /**
     * Creates a new freezable array from the given array.
     * 
     * @param array the elements of the new array.
     */
    @SafeVarargs
    protected FreezableArray(@Captured @Nonnull E... array) {
        this.array = array;
    }
    
    /**
     * Creates a new freezable array from the given array.
     * 
     * @param array the elements of the new array.
     * 
     * @return a new freezable array from the given array.
     */
    @Pure
    @SafeVarargs
    public static @Capturable @Nonnull @NonFrozen <E> FreezableArray<E> getNonNullable(@Captured @Nonnull E... array) {
        return new FreezableArray<>(array);
    }
    
    /**
     * Creates a new freezable array from the given array.
     * 
     * @param array the elements of the new array.
     * 
     * @return a new freezable array from the given array.
     */
    @Pure
    public static @Capturable @Nullable @NonFrozen <E> FreezableArray<E> getNullable(@Captured @Nullable E[] array) {
        return array == null ? null : getNonNullable(array);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Freezable –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Override
    public @Nonnull @Frozen ReadOnlyArray<E> freeze() {
        if (!isFrozen()) {
            super.freeze();
            for (final @Nullable E element : array) {
                if (element instanceof Freezable) {
                    ((Freezable) element).freeze();
                } else {
                    break;
                }
            }
        }
        return this;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Retrieval –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public int size() {
        return array.length;
    }
    
    @Pure
    @Override
    public @Nullable E getNullable(@ValidIndex int index) {
        assert index >= 0 && index < size() : "The index is valid.";
        
        return array[index];
    }
    
    @Pure
    @Override
    public boolean isNull(@ValidIndex int index) {
        return getNullable(index) == null;
    }
    
    @Pure
    @Override
    public @Nonnull E getNonNullable(@ValidIndex int index) {
        @Nullable E element = getNullable(index);
        assert element != null : "The element at the given index is not null.";
        
        return element;
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull FreezableArrayIterator<E> iterator() {
        return FreezableArrayIterator.get(this);
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Operation –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    /**
     * Sets the element at the given index to the new value.
     * 
     * @param index the index of the element to be set.
     * @param element the new value to replace the element with.
     */
    @NonFrozenRecipient
    public void set(@ValidIndex int index, @Nullable E element) {
        assert !isFrozen() : "This object is not frozen.";
        assert index >= 0 && index < size() : "The index is valid.";
        
        array[index] = element;
    }
    
    /**
     * Sets each element to the given value.
     * 
     * @param element the value to replace all elements with.
     * 
     * @return this freezable array.
     */
    @NonFrozenRecipient
    public @Nonnull @NonFrozen FreezableArray<E> setAll(@Nullable E element) {
        assert !isFrozen() : "This object is not frozen.";
        
        for (int i = 0; i < array.length; i++) array[i] = element;
        return this;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Conditions –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public boolean containsNull() {
        for (final @Nullable E element : this) {
            if (element == null) return true;
        }
        return false;
    }
    
    @Pure
    @Override
    public boolean containsDuplicates() {
        final @Nonnull HashSet<E> set = new HashSet<>(size());
        for (final @Nullable E element : this) {
            if (set.contains(element)) return true;
            else set.add(element);
        }
        return false;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Conversions –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public @Capturable @Nonnull E[] toArray() {
        return array.clone();
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull @NonFrozen FreezableArray<E> clone() {
        return new FreezableArray<>(array.clone());
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull @NonFrozen FreezableList<E> toFreezableList() {
        final @Nonnull FreezableList<E> freezableList = FreezableArrayList.getWithCapacity(array.length);
        for (final @Nullable E element : array) freezableList.add(element);
        return freezableList;
    }
    
    /* –––––––––––––––––––––––––––––––––––––––––––––––––– Object –––––––––––––––––––––––––––––––––––––––––––––––––– */
    
    @Pure
    @Override
    public boolean equals(@Nullable Object object) {
        if (object == this) return true;
        if (object == null) return false;
        if (object instanceof FreezableArray) return Arrays.equals(array, ((FreezableArray) object).array);
        if (object instanceof Object[]) return Arrays.equals(array, (Object[]) object);
        return false;
    }
    
    @Pure
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(array);
    }
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return IterableConverter.toString(this, Brackets.SQUARE);
    }
    
}
