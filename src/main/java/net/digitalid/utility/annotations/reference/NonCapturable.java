package net.digitalid.utility.annotations.reference;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that a parameter may not be captured by the callee.
 * (The callee must not keep or leak a reference to the passed parameter.)
 *
 * @see Capturable
 * @see Captured
 */
@Documented
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface NonCapturable {}
