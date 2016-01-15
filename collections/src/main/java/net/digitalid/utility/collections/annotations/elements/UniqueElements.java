package net.digitalid.utility.collections.annotations.elements;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import net.digitalid.utility.collections.readonly.ReadOnlyCollection;
import net.digitalid.utility.validation.meta.TargetType;

/**
 * This annotation indicates that a {@link Collection collection} does not contain {@link ReadOnlyCollection#containsDuplicates() duplicates}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@TargetType({Collection.class, ReadOnlyCollection.class, Object[].class})
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface UniqueElements {}