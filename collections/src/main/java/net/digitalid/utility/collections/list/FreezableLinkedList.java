package net.digitalid.utility.collections.list;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.ownership.NonCapturable;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.freezable.FreezableInterface;
import net.digitalid.utility.freezable.annotations.Freezable;
import net.digitalid.utility.freezable.annotations.Frozen;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.freezable.annotations.NonFrozenRecipient;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.functional.iterators.ReadOnlyIterableIterator;
import net.digitalid.utility.functional.iterators.ReadOnlyIterator;
import net.digitalid.utility.functional.iterators.ReadOnlyListIterator;
import net.digitalid.utility.validation.annotations.index.Index;
import net.digitalid.utility.validation.annotations.index.IndexForInsertion;
import net.digitalid.utility.validation.annotations.type.Immutable;
import net.digitalid.utility.validation.annotations.type.ReadOnly;

/**
 * This class extends the {@link LinkedList} and makes it {@link FreezableInterface freezable}.
 * It is recommended to use only {@link ReadOnly} or {@link Immutable} types for the elements.
 */
@Freezable(ReadOnlyList.class)
public class FreezableLinkedList<E> extends LinkedList<E> implements FreezableList<E> {
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    protected FreezableLinkedList() {}
    
    /**
     * Returns a new freezable linked list with no elements.
     */
    @Pure
    public static @Capturable <E> @Nonnull @NonFrozen FreezableLinkedList<E> withNoElements() {
        return new FreezableLinkedList<>();
    }
    
    /**
     * Returns a new freezable linked list with the given element.
     */
    @Pure
    public static @Capturable <E> @Nonnull @NonFrozen FreezableLinkedList<E> with(@Captured E element) {
        final @Nonnull FreezableLinkedList<E> list = new FreezableLinkedList<>();
        list.add(element);
        return list;
    }
    
    /**
     * Returns a new freezable linked list with the given elements or null if the given array is null.
     */
    @Pure
    @SafeVarargs
    public static @Capturable <E> @NonFrozen FreezableLinkedList<E> with(@Captured E... elements) {
        if (elements == null) { return null; }
        final @Nonnull FreezableLinkedList<E> list = new FreezableLinkedList<>();
        list.addAll(Arrays.asList(elements));
        return list;
    }
    
    protected FreezableLinkedList(@NonCaptured @Unmodified @Nonnull Iterable<? extends E> iterable) {
        for (E element : iterable) {
            super.add(element);
        }
    }
    
    /**
     * Returns a new freezable linked list with the elements of the given collection or null if the given collection is null.
     */
    @Pure
    public static @Capturable <E> @NonFrozen FreezableLinkedList<E> with(@NonCaptured @Unmodified Collection<? extends E> collection) {
        return collection == null ? null : new FreezableLinkedList<>(collection);
    }
    
    /**
     * Returns a new freezable linked list with the elements of the given iterable or null if the given iterable is null.
     */
    @Pure
    public static @Capturable <E> @NonFrozen FreezableLinkedList<E> with(FiniteIterable<? extends E> iterable) {
        return iterable == null ? null : new FreezableLinkedList<>(iterable);
    }
    
    /* -------------------------------------------------- Freezable -------------------------------------------------- */
    
    private boolean frozen = false;
    
    @Pure
    @Override
    public boolean isFrozen() {
        return frozen;
    }
    
    @Impure
    @Override
    public @Nonnull @Frozen ReadOnlyList<E> freeze() {
        this.frozen = true;
        return this;
    }
    
    /* -------------------------------------------------- Cloneable -------------------------------------------------- */
    
    @Pure
    @Override
    public @Capturable @Nonnull @NonFrozen FreezableLinkedList<E> clone() {
        return new FreezableLinkedList<>(this);
    }
    
    /* -------------------------------------------------- List -------------------------------------------------- */
    
    @Pure
    @Override
    public @NonCapturable E get(@Index int index) {
        return super.get(index);
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull ReadOnlyIterator<E> iterator() {
        return ReadOnlyIterableIterator.with(super.iterator());
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull ReadOnlyIterator<E> descendingIterator() {
        return ReadOnlyIterableIterator.with(super.descendingIterator());
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull ReadOnlyListIterator<E> listIterator() {
        return ReadOnlyListIterator.with(super.listIterator());
    }
    
    @Pure
    @Override
    public @Capturable @Nonnull ReadOnlyListIterator<E> listIterator(@IndexForInsertion int index) {
        return ReadOnlyListIterator.with(super.listIterator(index));
    }
    
    @Pure
    @Override
    public @NonCapturable @Nonnull FreezableList<E> subList(@Index int fromIndex, @IndexForInsertion int toIndex) {
        return BackedFreezableList.with(this, super.subList(fromIndex, toIndex));
    }
    
    /* -------------------------------------------------- Add -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean add(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.add(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public void add(@IndexForInsertion int index, @Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.add(index, element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public void addFirst(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.addFirst(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public void addLast(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.addLast(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean addAll(@NonCaptured @Unmodified @Nonnull Collection<? extends E> collection) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.addAll(collection);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean addAll(@IndexForInsertion int index, @NonCaptured @Unmodified @Nonnull Collection<? extends E> collection) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.addAll(index, collection);
    }
    
    /* -------------------------------------------------- Offer -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean offer(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.offer(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean offerFirst(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.offerFirst(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean offerLast(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.offerLast(element);
    }
    
    /* -------------------------------------------------- Remove -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E remove() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.remove();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E removeFirst() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.removeFirst();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E removeLast() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.removeLast();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E remove(@Index int index) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.remove(index);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean remove(@NonCaptured @Unmodified @Nullable Object object) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.remove(object);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean removeFirstOccurrence(@NonCaptured @Unmodified @Nullable Object object) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.removeFirstOccurrence(object);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean removeLastOccurrence(@NonCaptured @Unmodified @Nullable Object object) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.removeLastOccurrence(object);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    protected void removeRange(@Index int fromIndex, @IndexForInsertion int toIndex) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.removeRange(fromIndex, toIndex);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean removeAll(@NonCaptured @Unmodified @Nonnull Collection<?> collection) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.removeAll(collection);
    }
    
    /* -------------------------------------------------- Poll -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E poll() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.poll();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E pollFirst() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.pollFirst();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E pollLast() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.pollLast();
    }
    
    /* -------------------------------------------------- Stack -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public void push(@Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.push(element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Capturable E pop() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.pop();
    }
    
    /* -------------------------------------------------- Other -------------------------------------------------- */
    
    @Impure
    @Override
    @NonFrozenRecipient
    public @Nullable E set(@Index int index, @Captured E element) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.set(index, element);
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public void clear() {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        super.clear();
    }
    
    @Impure
    @Override
    @NonFrozenRecipient
    public boolean retainAll(@NonCaptured @Unmodified @Nonnull Collection<?> collection) {
        Require.that(!isFrozen()).orThrow("This object is not frozen.");
        
        return super.retainAll(collection);
    }
    
}
