package net.digitalid.utility.validation.annotations.type.kind;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Modified;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.processing.logging.SourcePosition;
import net.digitalid.utility.processing.utility.ProcessingUtility;
import net.digitalid.utility.processing.utility.TypeImporter;
import net.digitalid.utility.validation.annotations.meta.ValueValidator;
import net.digitalid.utility.validation.annotations.type.Stateless;
import net.digitalid.utility.validation.contract.Contract;
import net.digitalid.utility.validation.processing.ErrorLogger;
import net.digitalid.utility.validation.validator.ValueAnnotationValidator;
import net.digitalid.utility.validation.validators.ElementKindValidator;

import static javax.lang.model.element.ElementKind.*;

/**
 * This annotation indicates that a class or element is of one of the given kinds.
 * 
 * @see ElementKind
 */
@Documented
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@ValueValidator(TypeOf.Validator.class)
public @interface TypeOf {
    
    /* -------------------------------------------------- Value -------------------------------------------------- */
    
    /**
     * Returns the kinds one of which the annotated type or element has to be.
     */
    @Nonnull ElementKind[] value();
    
    /* -------------------------------------------------- Validator -------------------------------------------------- */
    
    /**
     * This class checks the use of and generates the contract for the surrounding annotation.
     */
    @Stateless
    public static class Validator implements ValueAnnotationValidator {
        
        private static final @Nonnull FiniteIterable<@Nonnull Class<?>> targetTypes = FiniteIterable.of(Class.class, Element.class);
        
        @Pure
        @Override
        public @Nonnull FiniteIterable<@Nonnull Class<?>> getTargetTypes() {
            return targetTypes;
        }
        
        private static final @Nonnull FiniteIterable<@Nonnull ElementKind> typeKinds = FiniteIterable.of(CLASS, INTERFACE, ENUM, ANNOTATION_TYPE);
        
        @Pure
        @Override
        public void checkUsage(@Nonnull Element element, @Nonnull AnnotationMirror annotationMirror, @NonCaptured @Modified @Nonnull ErrorLogger errorLogger) {
            ValueAnnotationValidator.super.checkUsage(element, annotationMirror, errorLogger);
            
            if (ProcessingUtility.isRawlyAssignable(element, Class.class) && !typeKinds.containsAll(ProcessingUtility.getEnums(ProcessingUtility.getAnnotationValue(annotationMirror), ElementKind.class))) {
                errorLogger.log("In case of classes, the annotation '@TypeKind' may only be used with CLASS, INTERFACE, ENUM and ANNOTATION_TYPE:", SourcePosition.of(element, annotationMirror));
            }
        }
        
        @Pure
        @Override
        public @Nonnull Contract generateContract(@Nonnull Element element, @Nonnull AnnotationMirror annotationMirror, @NonCaptured @Modified @Nonnull TypeImporter typeImporter) {
            final @Nonnull StringBuilder condition = new StringBuilder("# == null");
            for (@Nonnull ElementKind kind : ProcessingUtility.getEnums(ProcessingUtility.getAnnotationValue(annotationMirror), ElementKind.class)) {
                condition.append(" || ").append(ElementKindValidator.getCondition(element, kind, typeImporter));
            }
            return Contract.with(condition.toString(), "The # has to be null or one of the specified kinds but was $.", element);
        }
        
    }
    
}
