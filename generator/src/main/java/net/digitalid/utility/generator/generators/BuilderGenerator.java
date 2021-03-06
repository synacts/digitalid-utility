/*
 * Copyright (C) 2017 Synacts GmbH, Switzerland (info@synacts.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.digitalid.utility.generator.generators;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;

import net.digitalid.utility.circumfixes.Brackets;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.generator.exceptions.FailedClassGenerationException;
import net.digitalid.utility.generator.information.ElementInformation;
import net.digitalid.utility.generator.information.filter.MethodSignatureMatcher;
import net.digitalid.utility.generator.information.method.ExecutableInformation;
import net.digitalid.utility.generator.information.method.MethodInformation;
import net.digitalid.utility.generator.information.type.TypeInformation;
import net.digitalid.utility.generator.information.variable.VariableElementInformation;
import net.digitalid.utility.processing.logging.ProcessingLog;
import net.digitalid.utility.processor.generator.JavaFileGenerator;
import net.digitalid.utility.string.Strings;
import net.digitalid.utility.validation.annotations.elements.NonNullableElements;
import net.digitalid.utility.validation.annotations.method.Chainable;
import net.digitalid.utility.validation.annotations.type.Mutable;

/**
 * The Builder Generator generates a builder class for a given type (through the type information). 
 * For each required field, an interface is generated which holds the setter for the required field. 
 * The setter method is the field's name prefixed with "with".
 * An inner class, which represents the builder, implements all interfaces and therefore also implements
 * all setters of the required fields. Optional fields are added to the inner class only. 
 * A static method of one of the required fields, or an optional field if no required fields exist, is created
 * to provide an entrance method to the builder and sets the appropriate field in the builder. If neither required not optional fields exist, a static 
 * get() method is generated, which returns the builder without calling any further methods.
 * 
 * TODO: Clean up the following legacy remarks (the conversion point is no longer true and one of the biggest advantage seems to be that the constructor can be generated and accessed through the builder without exposing the subclass).
 * 
 * Advantages of builder pattern:
 * - Better handling of optional and default values.
 * - Callers fail if the order of getters with same return type is changed in the source code.
 * - The builder class serves as a meta-information-object about the fields (and their names) of the source class. (This can be used to generate @GenericType(Student.class) annotations.)
 * 
 * Questions:
 * - Enforce certain parameters with intermediary classes (e.g. StudentWithoutID) that miss the create/build-method? Disadvantage of such an approach: The order of parameters would be enforced.
 *   (Either create a new object for each provided parameter or generate an interface for each intermediary step and return the same object as an instance of the particular interface.)
 * - What if the type of the getter and the field are not exactly the same (for example, expose only ReadOnlyType but store a FreezableType for internal operations).
 */
@Mutable
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
    private @Nonnull String getNameOfFieldBuilder(@Nonnull ElementInformation field) {
        return Strings.capitalizeFirstLetters(field.getName()) + typeInformation.getSimpleNameOfGeneratedBuilder();
    }
    
    /**
     * Creates an interface for a given required field. The only declared method is a setter
     * for the field. The name is generated through the field name prefixed with "with".
     * 
     * @param nextInterface The field setter returns an object with this type, which is the interface
     *                      of the next required field. If no further required field must be set, the 
     *                      actual builder is returned. Thus, unless the caller casts the returned type
     *                      away, this method allows to traverse through all required fields before being
     *                      able to call "build()" on the builder.
     */
    private @Nonnull String createInterfaceForField(@Nonnull ElementInformation field, @Nullable String nextInterface) {
        final @Nonnull String interfaceName = getNameOfFieldBuilder(field);
        final @Nonnull String methodName = "with" + Strings.capitalizeFirstLetters(field.getName());
        beginInterface("public interface " + interfaceName + importWithBounds(typeInformation.getTypeArguments()));
        addAnnotation(Chainable.class);
        addMethodDeclaration("public @" + importIfPossible(Nonnull.class) + " " + nextInterface + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + " " + methodName + "(" + importIfPossible(field.getType()) + " " + field.getName() + ")");
        addEmptyLine();
        endInterface();
        return interfaceName;
    }
    
    /**
     * Returns a list of field information objects for fields that are required.
     */
    // TODO: improve exception handling
    private @Nonnull @NonNullableElements FiniteIterable<VariableElementInformation> getRequiredFields() {
        return typeInformation.getConstructorParameters().filter(VariableElementInformation::isMandatory);
    }
    
    private String getSetterForFieldStatementString(@Nonnull ElementInformation field, @Nonnull String returnType, @Nonnull String methodName) {
        return "public static " + importWithBounds(typeInformation.getTypeArguments()) + (typeInformation.getTypeArguments().isEmpty() ? "" : " ") + returnType + " " + methodName + "(" + importIfPossible(field.getType()) + " " + field.getName() + ")";
    }
    
    /**
     * Declares and implements the setter for the given field with the given return type and the given returned instance.
     */
    private void addSetterForField(@Nonnull ElementInformation field, @Nonnull String returnType, @Nonnull String returnedInstance) {
        final @Nonnull String methodName = "with" + Strings.capitalizeFirstLetters(field.getName());
        beginMethod(getSetterForFieldStatementString(field, returnType, methodName));
        addStatement("return new " + returnedInstance + "()." + methodName + "(" + field.getName() + ")");
        endMethod();
    }
    
    /**
     * Creates interfaces for every required field that restricts the creation of the object such that the caller is required to initialize the mandatory fields before creating the object.
     * This method returns a list of interfaces that need to be implemented by the builder.
     */
    private @Nonnull @NonNullableElements List<String> createInterfacesForRequiredFields(@Nonnull @NonNullableElements FiniteIterable<VariableElementInformation> requiredFields, @Nonnull String nameOfInnerClass) {
        final @Nonnull List<String> listOfInterfaces = new ArrayList<>();
        
        for (int i = 1; i < requiredFields.size(); i++) {
            final @Nonnull ElementInformation requiredField = requiredFields.get(i);
            @Nonnull String nextInterface = nameOfInnerClass;
            if ((i + 1) < requiredFields.size()) {
                final @Nonnull ElementInformation nextField = requiredFields.get(i + 1);
                nextInterface = getNameOfFieldBuilder(nextField);
            }
            listOfInterfaces.add(createInterfaceForField(requiredField, nextInterface) + typeInformation.getTypeArguments().join(Brackets.POINTY, ""));
        }
        return listOfInterfaces;
    }
    
    /**
     * Returns the types that are thrown by the initialize method.
     */
    private @Nonnull FiniteIterable<@Nonnull TypeMirror> getThrownTypesOfInitializeMethod() {
        final @Nullable MethodInformation initializeMethod = typeInformation.getOverriddenMethods().findFirst(MethodSignatureMatcher.of("initialize"));
        if (initializeMethod != null) {
            return initializeMethod.getThrownTypes();
        } else {
            return FiniteIterable.of();
        }
    }
    
    /**
     * Returns the types that are thrown by the recover constructor or method.
     */
    private @Nonnull @NonNullableElements FiniteIterable<? extends TypeMirror> getThrownTypes() {
        final @Nullable ExecutableInformation recoverConstructorOrMethod = typeInformation.getRecoverConstructorOrMethod();
        final @Nonnull FiniteIterable<@Nonnull ? extends TypeMirror> thrownTypes;
        if (recoverConstructorOrMethod != null) {
            final List<@Nonnull TypeMirror> typeMirrorList = getThrownTypesOfInitializeMethod().toList();
            typeMirrorList.addAll(recoverConstructorOrMethod.getElement().getThrownTypes());
            thrownTypes = FiniteIterable.of(typeMirrorList);
        } else {
            thrownTypes = getThrownTypesOfInitializeMethod();
        }
        return thrownTypes;
    }
    
    /**
     * Creates a builder that collects all fields and provides a build() method, which returns an instance of the type that the builder builds.
     */
    protected void createInnerClassForFields(@Nonnull String nameOfBuilder, @Nonnull @NonNullableElements List<String> interfacesForRequiredFields) throws FailedClassGenerationException {
        ProcessingLog.debugging("createInnerClassForFields()");
        
        final @Nonnull FiniteIterable<@Nonnull TypeVariable> typeArguments = typeInformation.getTypeArguments();
        beginClass("public static class " + nameOfBuilder + importWithBounds(typeArguments) + (interfacesForRequiredFields.isEmpty() ? "" : " implements " + FiniteIterable.of(interfacesForRequiredFields).join()));
        
        beginConstructor("private " + nameOfBuilder + "()");
        endConstructor();
        
        boolean first = true;
        for (@Nonnull VariableElementInformation field : typeInformation.getConstructorParameters()) {
            field.getAnnotations();
            addSection(Strings.capitalizeFirstLetters(Strings.decamelize(field.getName())));
            addField("private " + importIfPossible(field.getType()) + " " + field.getName() + " = " + field.getDefaultValue());
            final @Nonnull String methodName = "with" + Strings.capitalizeFirstLetters(field.getName());
            if (field.isMandatory() && !first) {
                addAnnotation(Override.class);
                first = false;
            }
            addAnnotation(Chainable.class);
            beginMethod("public @" + importIfPossible(Nonnull.class) + " " + nameOfBuilder + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + " " + methodName + "(" + importIfPossible(field.getType()) + " " + field.getName() + ")");
            addStatement("this." + field.getName() + " = " + field.getName());
            addStatement("return this");
            endMethod();
        }
        
        addSection("Build");
        
        beginMethod("public " + typeInformation.getName() + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + " build()" + getThrownTypes().map(this::importIfPossible).join(" throws ", "", ""));
        
        addStatement(typeInformation.getInstantiationCode(false, true, false /* This was true until 2017-07-21. */, null));
        
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
    protected void createStaticEntryMethod(@Nonnull String nameOfBuilder, @Nonnull FiniteIterable<@Nonnull VariableElementInformation> requiredFields, @Nonnull List<@Nonnull String> interfacesForRequiredFields) {
        if (requiredFields.size() > 0) {
            final @Nonnull ElementInformation entryField = requiredFields.get(0);
            final @Nonnull String secondInterface;
            if (interfacesForRequiredFields.size() > 0) {
                secondInterface = interfacesForRequiredFields.get(0);
            } else {
                secondInterface = nameOfBuilder + typeInformation.getTypeArguments().join(Brackets.POINTY, "");
            }
            addSetterForField(entryField, secondInterface, nameOfBuilder);
        } else {
            for (@Nonnull ElementInformation optionalField : typeInformation.getConstructorParameters()) {
                addSetterForField(optionalField, nameOfBuilder + typeInformation.getTypeArguments().join(Brackets.POINTY, ""), nameOfBuilder);
            }
            beginMethod("public static " + importWithBounds(typeInformation.getTypeArguments()) + " " + typeInformation.getName() + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + " build()" + getThrownTypes().map(this::importIfPossible).join(" throws ", "", ""));
            addStatement("return new " + nameOfBuilder + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + "().build()");
            endMethod();
    
            for (@Nonnull ElementInformation optionalField : typeInformation.getConstructorParameters()) {
                final @Nonnull String methodName = Strings.capitalizeFirstLetters(optionalField.getName());
                beginMethod(getSetterForFieldStatementString(optionalField, typeInformation.getName() + typeInformation.getTypeArguments().join(Brackets.POINTY, ""), "buildWith" + methodName) + getThrownTypes().map(this::importIfPossible).join(" throws ", "", ""));
                addStatement("return new " + nameOfBuilder + typeInformation.getTypeArguments().join(Brackets.POINTY, "") + "().with" + methodName + Brackets.inRound(optionalField.getName()) + ".build()");
                endMethod();
            }
        }
    }
    
    /* -------------------------------------------------- Constructor -------------------------------------------------- */
    
    /**
     * Constructs a new builder generator for the given type information. The builder generator prepares the Java source file that 
     * is generated.
     */
    protected BuilderGenerator(@Nonnull TypeInformation typeInformation) {
        super(typeInformation.getQualifiedNameOfGeneratedBuilder(), typeInformation.getElement());
        ProcessingLog.debugging("BuilderGenerator(" + typeInformation + ")");
        
        this.typeInformation = typeInformation;
        
        beginClass("public class " + typeInformation.getSimpleNameOfGeneratedBuilder() + importWithBounds(typeInformation.getTypeArguments()));
        
        final @Nonnull @NonNullableElements FiniteIterable<VariableElementInformation> requiredFields = getRequiredFields();
        final @Nonnull String nameOfBuilder = "Inner" + typeInformation.getSimpleNameOfGeneratedBuilder();

        final @Nonnull @NonNullableElements List<String> interfacesForRequiredFields = createInterfacesForRequiredFields(requiredFields, nameOfBuilder);
        createInnerClassForFields(nameOfBuilder, interfacesForRequiredFields);
        createStaticEntryMethod(nameOfBuilder, requiredFields, interfacesForRequiredFields);
        
        endClass();
    }
    
    /**
     * Generates a new builder class for the given type information.
     */
    public static void generateBuilderFor(@Nonnull TypeInformation typeInformation) {
        ProcessingLog.debugging("generateBuilderFor(" + typeInformation + ")");
        new BuilderGenerator(typeInformation).write();
    }
    
}
