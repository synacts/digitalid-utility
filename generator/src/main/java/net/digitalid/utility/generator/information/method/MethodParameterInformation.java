package net.digitalid.utility.generator.information.method;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.DeclaredType;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.generator.information.ElementInformationImplementation;
import net.digitalid.utility.generator.information.field.FieldInformation;
import net.digitalid.utility.generator.information.type.TypeInformation;
import net.digitalid.utility.generator.information.variable.VariableElementInformation;
import net.digitalid.utility.processing.logging.ProcessingLog;
import net.digitalid.utility.processing.logging.SourcePosition;
import net.digitalid.utility.processing.utility.StaticProcessingEnvironment;
import net.digitalid.utility.validation.annotations.generation.Default;

import com.sun.tools.javac.code.Type;

/**
 *
 */
public class MethodParameterInformation extends ElementInformationImplementation implements VariableElementInformation {
    
    /* -------------------------------------------------- Matching Field -------------------------------------------------- */
    
    /**
     * The type information object that contains information about the class, interface or enum.
     */
    private final @Nonnull TypeInformation typeInformation;
    
    /**
     * Returns a matching field for the method parameter. This method may only be called once type information is fully initialized. Otherwise, it may accidentally return null even though a field with the same name might exist.
     */
    @Pure
    public @Nullable FieldInformation getMatchingField() {
        Require.that(typeInformation.isInitialized()).orThrow("getMatchingField() called before the type information object was fully initialized.");
        
        return typeInformation.getFieldInformation().findFirst(field -> field.getName().equals(getName()) && StaticProcessingEnvironment.getTypeUtils().isAssignable(field.getType(), getType()));
    }
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Creates a method parameter information object from the given element, containing type and type information.
     */
    protected MethodParameterInformation(@Nonnull Element element, @Nonnull DeclaredType containingType, @Nonnull TypeInformation typeInformation) {
        super(element, element.asType(), containingType);
        
        Require.that(element.getKind() == ElementKind.PARAMETER).orThrow("The element $ has to be a parameter.", SourcePosition.of(element));
        
        this.typeInformation = typeInformation;
    }
    
    /* -------------------------------------------------- toString -------------------------------------------------- */
    
    @Override
    public @Nonnull String toString() {
        return getType() + " " + getElement();
    }
    
    /* -------------------------------------------------- Default Value -------------------------------------------------- */
    
    @Pure
    @Override
    public boolean hasDefaultValue() {
        if (hasAnnotation(Default.class) || getMatchingField() != null && getMatchingField().hasDefaultValue()) {
            return true;
        }
        return false;
    }
    
    @Pure
    @Override
    public @Nonnull String getDefaultValue() {
        @Nullable String defaultValue = null;
        if (hasAnnotation(Default.class)) {
            defaultValue = getAnnotation(Default.class).value();
        } else {
            if (getMatchingField() != null && getMatchingField().hasDefaultValue()) {
                defaultValue = getMatchingField().getDefaultValue();
            }
        }
        if (defaultValue == null) {
            final String typeName;
            if (getType().getKind().isPrimitive()) {
                if (getType() instanceof Type.AnnotatedType) {
                    Type.AnnotatedType annotatedType = (Type.AnnotatedType) getType();
                    final Type type = annotatedType.unannotatedType();
                    typeName = type.toString();
                } else {
                    typeName = getType().toString();
                }
                if (typeName.equals("boolean")) {
                    defaultValue = "false";
                } else {
                    defaultValue = "0";
                }
            } else {
                ProcessingLog.debugging("element: $, type kind: $", getElement(), getType().getKind());
                defaultValue = "null";
            }
        }
        return defaultValue;
    }
    
    @Pure
    @Override
    public boolean isMandatory() {
        return !(hasDefaultValue() || hasAnnotation(Nullable.class));
    }
    
}
