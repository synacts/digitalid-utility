package net.digitalid.utility.validation.validators;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import net.digitalid.utility.immutable.collections.ImmutableSet;
import net.digitalid.utility.tuples.annotations.Pure;
import net.digitalid.utility.validation.annotations.type.Stateless;
import net.digitalid.utility.validation.contract.Contract;
import net.digitalid.utility.validation.processing.ProcessingUtility;
import net.digitalid.utility.validation.processing.TypeImporter;
import net.digitalid.utility.validation.validator.ValueAnnotationValidator;

/**
 * This class generates the contracts for the kinds of types.
 * 
 * @see net.digitalid.utility.validation.annotations.type.kind
 */
@Stateless
public abstract class ElementKindValidator extends ValueAnnotationValidator {
    
    /* -------------------------------------------------- Target Types -------------------------------------------------- */
    
    private static final @Nonnull ImmutableSet<Class<?>> targetTypes = ImmutableSet.with(Class.class, Element.class);
    
    @Pure
    @Override
    public @Nonnull ImmutableSet<Class<?>> getTargetTypes() {
        return targetTypes;
    }
    
    /* -------------------------------------------------- Kind -------------------------------------------------- */
    
    /**
     * Returns the kind which the type has to be.
     */
    @Pure
    public abstract @Nonnull ElementKind getKind();
    
    /* -------------------------------------------------- Condition -------------------------------------------------- */
    
    /**
     * Returns the condition for the given element depending on the type of the element.
     */
    @Pure
    public static @Nonnull String getCondition(@Nonnull Element element, @Nonnull ElementKind kind, @Nonnull TypeImporter typeImporter) {
        if (ProcessingUtility.isAssignable(element, Class.class)) {
            switch (kind) {
                case CLASS: return "!#.isInterface() && !#.isEnum() && !#.isAnnotation()";
                case INTERFACE: return "#.isInterface()";
                case ENUM: return "#.isEnum()";
                case ANNOTATION_TYPE: return "#.isAnnotation()";
                default: return "false";
            }
        } else {
            return "#.getKind() == " + typeImporter.importIfPossible(ElementKind.class) + "." + kind.name();
        }
    }
    
    /* -------------------------------------------------- Contract Generation -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Contract generateContract(@Nonnull Element element, @Nonnull AnnotationMirror annotationMirror, @Nonnull TypeImporter typeImporter) {
        return Contract.with("# == null || " + getCondition(element, getKind(), typeImporter), "The # has to be null or of the kind '" + getKind().name().toLowerCase().replace("_", " ") + "' but was $.", element);
    }
    
}
