package net.digitalid.utility.generator;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.functional.string.IterableConverter;
import net.digitalid.utility.generator.information.field.FieldInformation;
import net.digitalid.utility.generator.information.type.TypeInformation;
import net.digitalid.utility.logging.processing.AnnotationLog;
import net.digitalid.utility.logging.processing.AnnotationProcessing;
import net.digitalid.utility.processor.generator.JavaFileGenerator;
import net.digitalid.utility.string.StringCase;
import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.reference.Chainable;
import net.digitalid.utility.validation.annotations.type.Utiliy;

/**
 * The Builder Generator generates a builder class for a given type (through the type information). 
 * For each required field, an interface is generated which holds the setter for the required field. 
 * The setter method is the field name prefixed with "with".
 * An inner class, which represents the builder, implements all interfaces and therefore also implements
 * all setters of the required fields. Optional fields are added to the inner class only. 
 * A static method of one of the required fields, or an optional field if no required fields exist, is created
 * to provide an entrance method to the builder and sets the appropriate field in the builder. If neither required not optional fields exist, a static 
 * get() method is generated, which returns the builder without calling any further methods.
 */
@Utiliy
public class BuilderGenerator extends JavaFileGenerator {
    
    /* -------------------------------------------------- Type Information -------------------------------------------------- */
    
    /**
     * Holds information about the type.
     */
    private final @Nonnull TypeInformation typeInformation;
    
    /* -------------------------------------------------- Required Fields Builder -------------------------------------------------- */
    
    /**
     * Returns the name of the field builder interface for a given field.
     */
    private @Nonnull String getNameOfFieldBuilder(@Nonnull FieldInformation field) {
        return StringCase.capitalizeFirstLetters(field.name) + typeInformation.getSimpleNameOfGeneratedBuilder();
    }
    
    /**
     * Creates an interface for a given required field. The only declared method is a setter
     * for the field. The name is generated through the fieldName prefixed with "with".
     * 
     * @param nextInterface The field setter returns an object with this type, which is the interface
     *                      of the next required field. If no further required field must be set, the 
     *                      actual builder is returned. Thus, unless the caller casts the returned type
     *                      away, this method allows to traverse through all required fields before being
     *                      able to call "build()" on the builder.
     */
    private @Nonnull String createInterfaceForField(@Nonnull FieldInformation field, @Nullable String nextInterface) {
        final @Nonnull String interfaceName = getNameOfFieldBuilder(field);
        final @Nonnull String methodName = "with" + StringCase.capitalizeFirstLetters(field.name);
        beginInterface("interface " + interfaceName + importingTypeVisitor.mapTypeVariablesWithBoundsToStrings(typeInformation.type.getTypeArguments()));
        addAnnotation("@" + importIfPossible(Chainable.class));
        addMethodDeclaration("public @" + importIfPossible(Nonnull.class) + " " + nextInterface + " " + methodName + "(" + field.type + " " + field.name + ")");
        endInterface();
        return interfaceName;
    }
    
    /**
     * Returns true if the field is required, false otherwise.
     */
    private boolean isFieldRequired(@Nonnull FieldInformation fieldInformation) {
        // TODO: nullable fields are not required. Non-final fields are also not required (and probably not representing).
        return fieldInformation.defaultValue == null;
    }
    
    /**
     * Returns a list of field information objects for fields that are required.
     */
    private @Nonnull @NonNullableElements List<FieldInformation> getRequiredFields() {
        final @Nonnull @NonNullableElements List<FieldInformation> requiredFields = new ArrayList<>();
        for (@Nonnull FieldInformation fieldInformation : typeInformation.representingFields) {
            if (isFieldRequired(fieldInformation)) {
                requiredFields.add(fieldInformation);
            }
        }
        return requiredFields;
    }
    
    /**
     * Declares and implements the setter for the given field with the given return type and the given returned instance.
     */
    private void addSetterForField(@Nonnull FieldInformation field, @Nonnull String returnType, @Nonnull String returnedInstance) {
        final @Nonnull String methodName = "with" + StringCase.capitalizeFirstLetters(field.name);
        beginMethod("public static " + importingTypeVisitor.mapTypeVariablesWithBoundsToStrings(typeInformation.type.getTypeArguments()) + returnType + " " + methodName + "(" + field.type + " " + field.name + ")");
        addStatement("return new " + returnedInstance + "()." + methodName + "(" + field.name + ")");
        endMethod();
    }
    
    protected final @Nonnull @NonNullableElements List<String> createInterfacesForRequiredFields(@Nonnull @NonNullableElements List<FieldInformation> requiredFields, @Nonnull String nameOfInnerClass) {
        final @Nonnull List<String> listOfInterfaces = new ArrayList<>();
        
        for (int i = 0; i < requiredFields.size(); i++) {
            final @Nonnull FieldInformation requiredField = requiredFields.get(i);
            @Nonnull String nextInterface = nameOfInnerClass;
            if ((i + 1) < requiredFields.size()) {
                final @Nonnull FieldInformation nextField = requiredFields.get(i + 1);
                nextInterface = getNameOfFieldBuilder(nextField);
            }
            listOfInterfaces.add(createInterfaceForField(requiredField, nextInterface));
        }
        return listOfInterfaces;
    }
    
    /**
     * Creates a builder that collects all fields and provides a build() method, which returns an instance of the type that the builder builds.
     * @param nameOfBuilder
     * @param interfacesForRequiredFields
     */
    protected void createInnerClassForFields(@Nonnull String nameOfBuilder, @Nonnull @NonNullableElements List<String> interfacesForRequiredFields) {
        
        AnnotationLog.debugging("createInnerClassForFields()");
        
        final List<? extends TypeMirror> typeArguments = typeInformation.type.getTypeArguments();
        
        beginClass("static class " + nameOfBuilder + importingTypeVisitor.mapTypeVariablesWithBoundsToStrings(typeArguments) + (interfacesForRequiredFields.size() == 0 ? "" : " implements " + IterableConverter.toString(interfacesForRequiredFields) + importingTypeVisitor.getTypeVariablesWithoutBounds(typeArguments, false)));
        
        for (@Nonnull FieldInformation field : typeInformation.representingFields) {
            field.getAnnotations();
            addSection(StringCase.capitalizeFirstLetters(StringCase.decamelize(field.name)));
            // TODO: Add annotations.
            addField("private " + field.type + " " + field.name);
            final @Nonnull String methodName = "with" + StringCase.capitalizeFirstLetters(field.name);
            if (isFieldRequired(field)) {
                addAnnotation("@" + importIfPossible(Override.class));
            }
            addAnnotation("@" + importIfPossible(Chainable.class));
            beginMethod("public @" + importIfPossible(Nonnull.class) + " " + nameOfBuilder + " " + methodName + "(" + field.type + " " + field.name + ")");
            addStatement("this." + field.name + " = " + field.name);
            addStatement("return this");
            endMethod();
        }
        
        addSection("Build");
        beginMethod("public " + typeInformation.name + " build()");
        
        final @Nonnull @NonNullableElements List<ExecutableElement> constructors = ElementFilter.constructorsIn(typeInformation.element.getEnclosedElements());
        if (constructors.size() != 1) {
            AnnotationLog.error("Expected one constructor in generated type:");
        }
        final @Nonnull ExecutableElement constructor = constructors.get(0);
        
        final @Nonnull ExecutableType type = (ExecutableType) AnnotationProcessing.getTypeUtils().asMemberOf(typeInformation.type, constructor);
        addStatement("return new " + typeInformation.getQualifiedNameOfGeneratedSubclass() + importingTypeVisitor.reduceParametersDeclarationToString(type, constructor));
        
        endMethod();
        
        endClass();
    }
    
    /**
     * Creates a static method that serves as an entry to the builder. If required fields exist,
     * the static method returns the builder and sets the required field. It returns the interface
     * of the next required field, if another one exists. If it is the only required field, the 
     * return type is the builder, which makes calling "build()" possible.
     * If no required fields exits, static entry methods for all optional fields are created. Additionally,
     * a static "get()" method is created, which returns the new builder instance without calling any additional builder setters.
     */
    protected void createStaticEntryMethod(@Nonnull FieldInformation entryField, @Nonnull String nameOfBuilder, @Nonnull @NonNullableElements List<String> interfacesForRequiredFields) {
        final List<? extends TypeMirror> typeArguments = typeInformation.type.getTypeArguments();
        
        if (interfacesForRequiredFields.size() > 0) {
            final @Nonnull String secondInterface;
            if (interfacesForRequiredFields.size() > 1) {
                secondInterface = interfacesForRequiredFields.get(1);
            } else {
                secondInterface = nameOfBuilder;
            }
            addSetterForField(entryField, secondInterface, nameOfBuilder);
        } else {
            for (@Nonnull FieldInformation optionalField : typeInformation.representingFields) {
                addSetterForField(optionalField, nameOfBuilder, nameOfBuilder);
            }
            beginMethod("public static " + importingTypeVisitor.mapTypeVariablesWithBoundsToStrings(typeArguments) + nameOfBuilder + " get()");
            addStatement("return new " + nameOfBuilder + "()");
            endMethod();
        }
 
    }
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Constructs a new builder generator for the given type information. The builder generator prepares the Java source file that 
     * is generated 
     */
    protected BuilderGenerator(@Nonnull TypeInformation typeInformation) {
        super(typeInformation.getQualifiedNameOfGeneratedBuilder(), typeInformation.element);
        AnnotationLog.debugging("BuilderGenerator(" + typeInformation + ")");
    
        this.typeInformation = typeInformation;
    
        beginClass("public class " + typeInformation.getSimpleNameOfGeneratedBuilder() + importingTypeVisitor.reduceTypeVariablesWithBoundsToString(typeInformation.type.getTypeArguments()));
    
        final @Nonnull @NonNullableElements List<FieldInformation> requiredFields = getRequiredFields();
        final @Nonnull String nameOfBuilder = "Inner" + typeInformation.getSimpleNameOfGeneratedBuilder();
        
        final @Nonnull @NonNullableElements List<String> interfacesForRequiredFields = createInterfacesForRequiredFields(requiredFields, nameOfBuilder);
        createInnerClassForFields(nameOfBuilder, interfacesForRequiredFields);
        final @Nonnull FieldInformation entryField = requiredFields.get(0);
        createStaticEntryMethod(entryField, nameOfBuilder, interfacesForRequiredFields);
        
        endClass();
        AnnotationLog.debugging("endClass()");
    }
    
    /**
     * Generates a new builder class for the given type information.
     */
    public static void generateBuilderFor(@Nonnull TypeInformation typeInformation) {
        Require.that(typeInformation.generatable).orThrow("No subclass can be generated for " + typeInformation);
        
        AnnotationLog.debugging("generateBuilderFor(" + typeInformation + ")");
        new BuilderGenerator(typeInformation).write();
    }
    
}