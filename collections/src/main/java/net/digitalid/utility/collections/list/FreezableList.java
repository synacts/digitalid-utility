package net.digitalid.utility.collections.list;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.annotations.ownership.NonCapturable;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Modified;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.collections.collection.FreezableCollection;
import net.digitalid.utility.freezable.FreezableInterface;
import net.digitalid.utility.freezable.annotations.Freezable;
import net.digitalid.utility.freezable.annotations.Frozen;
import net.digitalid.utility.freezable.annotations.NonFrozenRecipient;
import net.digitalid.utility.validation.annotations.index.Index;
import net.digitalid.utility.validation.annotations.index.IndexForInsertion;
import net.digitalid.utility.validation.annotations.math.relative.GreaterThanOrEqualTo;
import net.digitalid.utility.validation.annotations.method.Chainable;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.type.ReadOnly;

/**
 * This interface models a {@link List list} that can be {@link FreezableInterface frozen}.
 * It is recommended to use only {@link ReadOnly} or {@link Immutable} types for the elements.
 * 
 * @see BackedFreezableList
 * @see FreezableArrayList
 * @see FreezableLinkedList
 */
@Freezable(ReadOnlyList.class)
public interface FreezableList<E> extends ReadOnlyList<E>, List<E>, FreezableCollection<E> {
    
    /* -------------------------------------------------- Freezable -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Chainable @Nonnull @Frozen ReadOnlyList<E> freeze();
    
    /* -------------------------------------------------- List -------------------------------------------------- */
    
    @Pure
    @Override
    public @NonCapturable @Nonnull FreezableList<E> subList(@Index int fromIndex, @IndexForInsertion int toIndex);
    
    /* -------------------------------------------------- Conflicts -------------------------------------------------- */
    
    @Pure
    @Override
    public default boolean isEmpty() {
        return FreezableCollection.super.isEmpty();
    }
    
    @Pure
    @Override
    public default @NonCapturable E get(@Index int index) {
        return FreezableCollection.super.get(index);
    }
    
    @Pure
    @Override
    public default @GreaterThanOrEqualTo(-1) int indexOf(@NonCaptured @Unmodified @Nullable Object object) {
        return FreezableCollection.super.indexOf(object);
    }
    
    @Pure
    @Override
    public default @GreaterThanOrEqualTo(-1) int lastIndexOf(@NonCaptured @Unmodified @Nullable Object object) {
        return FreezableCollection.super.lastIndexOf(object);
    }
    
    @Pure
    @Override
    public default boolean contains(@NonCaptured @Unmodified @Nullable Object object) {
        return FreezableCollection.super.contains(object);
    }
    
    @Pure
    @Override
    public default boolean containsAll(@NonCaptured @Unmodified @Nonnull Collection<?> collection) {
        return FreezableCollection.super.containsAll(collection);
    }
    
    @Pure
    @Override
    public default @Capturable @Nonnull Object[] toArray() {
        return FreezableCollection.super.toArray();
    }
    
    @Pure
    @Override
    @SuppressWarnings("SuspiciousToArrayCall")
    public default @Capturable <T> @Nonnull T[] toArray(@NonCaptured @Modified @Nonnull T[] array) {
        return FreezableCollection.super.toArray(array);
    }
    
}
