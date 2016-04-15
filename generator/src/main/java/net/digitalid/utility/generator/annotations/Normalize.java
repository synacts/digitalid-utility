package net.digitalid.utility.generator.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;

import net.digitalid.utility.generator.BuilderGenerator;
import net.digitalid.utility.validation.annotations.string.JavaExpression;

/**
 * This annotation indicates the expression with which a value is normalized.
 * 
 * @see BuilderGenerator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface Normalize {
    
    /**
     * Returns the expression with which the value is normalized.
     */
    @Nonnull @JavaExpression String value();
    
}
