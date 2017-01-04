package net.digitalid.utility.conversion.recovery;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.contracts.Constraint;
import net.digitalid.utility.conversion.exceptions.RecoveryException;
import net.digitalid.utility.conversion.exceptions.RecoveryExceptionBuilder;
import net.digitalid.utility.string.Strings;
import net.digitalid.utility.validation.annotations.elements.NullableElements;
import net.digitalid.utility.validation.annotations.type.Immutable;

/**
 * This class makes it easier to check recovered data.
 * Usage: {@code Check.that(condition).orThrow(message)}.
 */
@Immutable
public final class Check extends Constraint {
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    private Check(boolean condition) {
        super(condition);
    }
    
    /* -------------------------------------------------- Instances -------------------------------------------------- */
    
    private static final @Nonnull Check FULFILLED = new Check(true);
    
    private static final @Nonnull Check VIOLATED = new Check(false);
    
    /* -------------------------------------------------- Evaluation -------------------------------------------------- */
    
    /**
     * Returns a check with the given condition that needs to be checked with {@link #orThrow(java.lang.String, java.lang.Object...)}.
     */
    @Pure
    public static @Nonnull Check that(boolean condition) {
        return condition ? FULFILLED : VIOLATED;
    }
    
    /**
     * Checks whether the check returned by {@link #that(boolean)} is fulfilled and throws a {@link RecoveryException} with the given message otherwise.
     * Each dollar sign in the message is replaced with the corresponding argument.
     */
    @Pure
    public void orThrow(@Nonnull String message, @Captured @Nonnull @NullableElements Object... arguments) throws RecoveryException {
        if (isViolated()) { throw RecoveryExceptionBuilder.withMessage(Strings.format(message, arguments)).build(); }
    }
    
}
