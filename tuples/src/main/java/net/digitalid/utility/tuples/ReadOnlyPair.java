package net.digitalid.utility.tuples;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.readonly.ReadOnly;
import net.digitalid.utility.validation.reference.Capturable;
import net.digitalid.utility.validation.state.Pure;

/**
 * This interface models a {@link ReadOnly read-only} pair.
 * 
 * @see FreezablePair
 * @see ReadOnlyTriplet
 */
public interface ReadOnlyPair<E0, E1> extends ReadOnly {
    
    /* -------------------------------------------------- Getters -------------------------------------------------- */
    
    /**
     * Returns the first element of this tuple.
     * 
     * @return the first element of this tuple.
     */
    @Pure
    public @Nullable E0 getNullableElement0();
    
    /**
     * Returns the first element of this tuple.
     * 
     * @return the first element of this tuple.
     * 
     * @require getNullableElement0() != null : "The element is not null.";
     */
    @Pure
    public @Nonnull E0 getNonNullableElement0();
    
    /**
     * Returns the second element of this tuple.
     * 
     * @return the second element of this tuple.
     */
    @Pure
    public @Nullable E1 getNullableElement1();
    
    /**
     * Returns the second element of this tuple.
     * 
     * @return the second element of this tuple.
     * 
     * @require getNullableElement1() != null : "The element is not null.";
     */
    @Pure
    public @Nonnull E1 getNonNullableElement1();
    
    /* -------------------------------------------------- Cloneable -------------------------------------------------- */
    
    @Pure
    @Override
    public @Capturable @Nonnull @NonFrozen FreezablePair<E0, E1> clone();
    
}