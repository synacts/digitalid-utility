package net.digitalid.utility.collections.freezable;

import java.util.Set;

import javax.annotation.Nonnull;

import net.digitalid.utility.collections.readonly.ReadOnlySet;
import net.digitalid.utility.freezable.annotations.Frozen;
import net.digitalid.utility.validation.state.Immutable;

/**
 * This interface models a {@link Set set} that can be {@link Freezable frozen}.
 * As a consequence, all modifying methods may fail with an {@link AssertionError}.
 * <p>
 * <em>Important:</em> Only use freezable or immutable types for the elements!
 * (The type is not restricted to {@link Freezable} or {@link Immutable} so that library types can also be used.)
 * 
 * @see FreezableHashSet
 * @see FreezableLinkedHashSet
 * @see BackedFreezableSet
 */
public interface FreezableSet<E> extends ReadOnlySet<E>, Set<E>, FreezableCollection<E> {
    
    /* -------------------------------------------------- Freezable -------------------------------------------------- */
    
    @Override
    public @Nonnull @Frozen ReadOnlySet<E> freeze();
    
}