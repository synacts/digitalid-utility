package net.digitalid.utility.property.nullable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.property.ReadOnlyProperty;
import net.digitalid.utility.property.ValueValidator;
import net.digitalid.utility.validation.state.Pure;
import net.digitalid.utility.validation.state.Validated;

/**
 * This is the read-only abstract class for properties that stores a nullable replaceable value.
 */
public abstract class ReadOnlyNullableProperty<V> extends ReadOnlyProperty<V, NullablePropertyObserver<V>> {
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Creates a new read-only nullable replaceable property with the given validator.
     *
     * @param validator the validator used to validate the value of this property.
     */
    protected ReadOnlyNullableProperty(@Nonnull ValueValidator<? super V> validator) {
        super(validator);
    }
    
    /* -------------------------------------------------- Getter -------------------------------------------------- */
    
    /**
     * Returns the value of this replaceable property.
     * 
     * @return the value of this replaceable property.
     *
     * @ensure getValidator().isValid(return) : "The returned value is valid.";
     */
    @Pure
    public abstract @Nullable @Validated V get();
    
}
