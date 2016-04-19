package net.digitalid.utility.generator;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.type.Mutable;
import net.digitalid.utility.contracts.Require;
import net.digitalid.utility.exceptions.UnexpectedFailureException;
import net.digitalid.utility.functional.fixes.Brackets;
import net.digitalid.utility.functional.fixes.Quotes;
import net.digitalid.utility.functional.interfaces.UnaryFunction;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.generator.information.ElementInformation;
import net.digitalid.utility.generator.information.ElementInformationImplementation;
import net.digitalid.utility.generator.information.field.DirectlyAccessibleFieldInformation;
import net.digitalid.utility.generator.information.field.FieldInformation;
import net.digitalid.utility.generator.information.field.GeneratedFieldInformation;
import net.digitalid.utility.generator.information.field.RepresentingFieldInformation;
import net.digitalid.utility.generator.information.method.ConstructorInformation;
import net.digitalid.utility.generator.information.method.MethodInformation;
import net.digitalid.utility.generator.information.type.ClassInformation;
import net.digitalid.utility.generator.information.type.InterfaceInformation;
import net.digitalid.utility.generator.information.type.TypeInformation;
import net.digitalid.utility.generator.information.type.exceptions.UnsupportedTypeException;
import net.digitalid.utility.generator.interceptor.MethodInterceptor;
import net.digitalid.utility.generator.interceptor.MethodUtility;
import net.digitalid.utility.generator.typevisitors.GenerateComparisonTypeVisitor;
import net.digitalid.utility.generator.typevisitors.GenerateHashCodeTypeVisitor;
import net.digitalid.utility.generator.typevisitors.GenerateToStringTypeVisitor;
import net.digitalid.utility.processing.logging.ProcessingLog;
import net.digitalid.utility.processor.generator.JavaFileGenerator;
import net.digitalid.utility.string.Strings;
import net.digitalid.utility.tuples.Pair;
import net.digitalid.utility.tuples.Quartet;
import net.digitalid.utility.tuples.Triplet;
import net.digitalid.utility.validation.processing.ValidatorProcessingUtility;
import net.digitalid.utility.validation.validator.MethodAnnotationValidator;
import net.digitalid.utility.validation.validator.ValueAnnotationValidator;

/**
 * This class generates a subclass with the provided type information.
 * 
 * @see GeneratorProcessor
 * @see TypeInformation
 */
@Mutable
public class SubclassGenerator extends JavaFileGenerator {
    
    /* -------------------------------------------------- Type Information -------------------------------------------------- */
    
    protected final @Nonnull TypeInformation typeInformation;
    
    /* -------------------------------------------------- Generating Methods -------------------------------------------------- */
    
    protected void generateFields() {
        for (@Nonnull GeneratedFieldInformation field : typeInformation.generatedFieldInformation) {
            ProcessingLog.verbose("Generating the field $.", field.getName());
            addSection(Strings.capitalizeFirstLetters(Strings.decamelize(field.getName())));
            addField("private " + (field.isMutable() ? "" : "final ") + importIfPossible(field.getType()) + " " + field.getName());
            
            {
                final @Nonnull MethodInformation getter = field.getGetter();
                ProcessingLog.verbose("Implementing the getter $.", getter.getName());
                final @Nonnull String statement = "result = this." + field.getName();
                generateMethodWithStatement(getter, statement, "result");
            }
            
            if (field.hasSetter()) {
                final @Nonnull MethodInformation setter = field.getNonNullSetter();
                ProcessingLog.verbose("Implementing the setter " + Quotes.inSingle(setter.getName()));
                final @Nonnull List<? extends VariableElement> parameters = setter.getElement().getParameters();
                Require.that(parameters.size() == 1).orThrow("Found a setter with " + (parameters.size() == 0 ? "zero " : "more than one ") + "parameters.");
                final @Nonnull VariableElement parameter = parameters.get(0);
                final @Nonnull String statement = ("this." + field.getName() + " = " + parameter.getSimpleName());
                generateMethodWithStatement(setter, statement);
            }
        }
    }
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    // TODO: This function is nowhere used!
    private static final UnaryFunction<@Nonnull Pair<@Nonnull RepresentingFieldInformation, @Nonnull SubclassGenerator>, @Nonnull String> elementInformationToDeclarationFunction = pair -> pair.get1().importIfPossible(pair.get0().getType()) + " " + pair.get0().getName();
    
    private void generateConstructor(@Nonnull String constructorDeclaration, @Nullable String superStatement) {
        beginConstructor(constructorDeclaration);
        if (superStatement != null) {
            addStatement(superStatement);
            addEmptyLine();
        }
        for (@Nonnull FieldInformation field : typeInformation.generatedFieldInformation) {
            addStatement("this." + field.getName() + " = " + field.getName());
        }
        endConstructor();
    }
    
    private void generateConstructor(@Nonnull ConstructorInformation constructorInformation) throws UnsupportedTypeException {
        @Nullable List<? extends TypeMirror> throwTypes = constructorInformation.getElement().getThrownTypes();
        final @Nonnull FiniteIterable<ElementInformation> constructorParameter = constructorInformation.getParameters().map(parameter -> (ElementInformation) parameter).combine(typeInformation.generatedFieldInformation);
        ProcessingLog.debugging("Constructor parameter: $", constructorParameter.join());
        ProcessingLog.debugging("Constructor throw types: $", throwTypes);
        
        final @Nonnull String superStatement = "super" + constructorInformation.getParameters().map(ElementInformationImplementation::getName).join(Brackets.ROUND);
        
        generateConstructor("protected " + typeInformation.getSimpleNameOfGeneratedSubclass() + constructorParameter.map(element -> this.importIfPossible(element.getType()) + " " + element.getName()).join(Brackets.ROUND) + (throwTypes == null || throwTypes.isEmpty() ? "" : " throws " + FiniteIterable.of(throwTypes).map(this::importIfPossible).join()), superStatement);
    }
    
    protected void generateConstructors() throws UnsupportedTypeException {
        addSection("Constructors");
        if (typeInformation instanceof ClassInformation) {
            for (@Nonnull ConstructorInformation constructor : typeInformation.getConstructors()) {
                generateConstructor(constructor);
            }
        } else if (typeInformation instanceof InterfaceInformation) {
            generateConstructor("protected " + typeInformation.getSimpleNameOfGeneratedSubclass() + typeInformation.getRepresentingFieldInformation().map(element -> this.importIfPossible(element.getType()) + " " + element.getName()).join(Brackets.ROUND), null);
        }
    }
    
    protected @Nonnull String implementCallToMethodInterceptors(@Nonnull MethodInformation method, @Nonnull String lastStatement, @Nullable String returnedValue) {
        for (Map.@Nonnull Entry<AnnotationMirror, MethodInterceptor> annotationMirrorMethodInterceptorEntry : method.getInterceptors().entrySet()) {
            final @Nonnull MethodInterceptor methodInterceptor = annotationMirrorMethodInterceptorEntry.getValue();
            lastStatement = methodInterceptor.generateInterceptorMethod(this, method, lastStatement, returnedValue);
        }
        return lastStatement;
    }
    /* -------------------------------------------------- Overridden Methods -------------------------------------------------- */
    
    protected void overrideMethods() {
        // TODO: I had the problem that the generator also tried to override private methods.
        if (!typeInformation.getOverriddenMethods().isEmpty()) { addSection("Overridden Methods"); }
        for (final @Nonnull MethodInformation method : typeInformation.getOverriddenMethods()) {
            ProcessingLog.verbose("Overriding the method $.", method.getName());
            
            // TODO: Please generate the pre- and post-conditions again.
            // TODO: Why is the result first declared and only then assigned (with the real value and not just null)?
            final @Nonnull String callToSuperMethod = MethodUtility.createSuperCall(method, "result");
            final @Nonnull String firstMethodCall = implementCallToMethodInterceptors(method, callToSuperMethod, "result"); 
            addAnnotation(Override.class);
            MethodUtility.generateBeginMethod(this, method, null, "result");
            addStatement(firstMethodCall);
            if (method.hasReturnType()) {
                addStatement("return result");
            }
            endMethod();
        }
    }
    
    private void generateMethodWithStatement(@Nonnull MethodInformation method, @Nonnull String statement) {
        generateMethodWithStatement(method, statement, null);
    }
    
    private void generateMethodWithStatement(@Nonnull MethodInformation method, @Nonnull String statement, @Nullable String returnedValue) {
        final @Nonnull String firstMethodCall = implementCallToMethodInterceptors(method, statement, returnedValue);
        addAnnotation(Override.class);
        MethodUtility.generateBeginMethod(this, method, null, returnedValue);
        for (@Nonnull VariableElement parameter : method.getElement().getParameters()) {
            for (Map.@Nonnull Entry<AnnotationMirror, ValueAnnotationValidator> entry : ValidatorProcessingUtility.getValueValidators(parameter).entrySet()) {
                addPrecondition(entry.getValue().generateContract(parameter, entry.getKey(), this));
            }
        }
        addStatement(firstMethodCall);
        ProcessingLog.debugging("generateMethodWithStatement - postcondition");
        for (Map.@Nonnull Entry<AnnotationMirror, MethodAnnotationValidator> entry : method.getMethodValidators().entrySet()) {
            ProcessingLog.debugging("for annotation: " + entry.getKey());
            addPostcondition(entry.getValue().generateContract(method.getElement(), entry.getKey(), this));
        }
        if (returnedValue != null) {
            addStatement("return " + returnedValue);
        }
        endMethod();
    }
    
    private static @Nonnull GenerateComparisonTypeVisitor generateComparisonTypeVisitor = new GenerateComparisonTypeVisitor();
    
    private static @Nonnull GenerateToStringTypeVisitor generateToStringTypeVisitor = new GenerateToStringTypeVisitor();
    
    /**
     * Generates a hashCode method that generate a hashCode from all representing fields.
     */
    protected void generateToStringMethod() {
        if (typeInformation instanceof ClassInformation && ((ClassInformation) typeInformation).toStringMethod != null) {
            return;
        }
        addAnnotation(Pure.class);
        addAnnotation(Override.class);
        beginMethod("public String toString()");
        importIfPossible(Objects.class);
        // TODO: Rather use String.valueOf(object) instead of Objects.toString(object) because the latter forwards to the former anyway and primitive types need not to be boxed.
        addStatement("return \"" + typeInformation.getName() + typeInformation.getRepresentingFieldInformation().map(field -> field.getName() + ": \" + " + generateToStringTypeVisitor.visit(field.getType(), field.getAccessCode()) + " + \"").join(Brackets.ROUND) + "\"");
        endMethod();
    }
    
    private void generateValidateMethod() {
        ProcessingLog.debugging("generateValidateMethod()");
        addAnnotation(Pure.class);
        addAnnotation(Override.class);
        beginMethod("public void validate()");
        // TODO: The following cast does not work for interfaces!
        final @Nonnull ClassInformation classInformation = (ClassInformation) typeInformation; 
        for (@Nonnull DirectlyAccessibleFieldInformation field : classInformation.writableAccessibleFields) {
            for (Map.@Nonnull Entry<AnnotationMirror, ValueAnnotationValidator> entry : ValidatorProcessingUtility.getValueValidators(field.getElement()).entrySet()) {
                addInvariant(entry.getValue().generateContract(field.getElement(), entry.getKey(), this));
            }
        }
        endMethod();
    }
    
    private final @Nonnull GenerateHashCodeTypeVisitor generateHashCodeTypeVisitor = new GenerateHashCodeTypeVisitor();
    
    /**
     * Generates a hashCode method that generate a hashCode from all representing fields.
     */
    protected void generateHashCodeMethod() {
        if (typeInformation instanceof ClassInformation && ((ClassInformation) typeInformation).hashCodeMethod != null) {
            return;
        }
        beginJavadoc();
        addJavadoc("Computes and returns the hash code of this object, using the fields of the class which are handed to the class via the recovery method or the constructor.");
        endJavadoc();
        addAnnotation(Pure.class);
        addAnnotation(Override.class);
        beginMethod("public int hashCode()");
        addStatement("int prime = 92821");
        addStatement("int result = 46411");
        // TODO: Primitive types are auto-boxed right now. Is the hash of an auto-boxed value always the same?
        final @Nonnull FiniteIterable<RepresentingFieldInformation> representingFieldInformation = typeInformation.getRepresentingFieldInformation();
        for (@Nonnull RepresentingFieldInformation field : representingFieldInformation) {
            generateHashCodeTypeVisitor.visit(field.getType(), Triplet.of(field.getAccessCode(), this, "result"));
        }
        addStatement("return result");
        endMethod();
    }
    
    /**
     * Generates an equal method that compares all representing fields.
     */
    protected void generateEqualsMethod() {
        if (typeInformation instanceof ClassInformation && ((ClassInformation) typeInformation).equalsMethod != null) {
            return;
        }
        beginJavadoc();
        addJavadoc("Returns false if the given object is not equal to the current object, using the heuristics that fields of a class, which are handed to the class via the recovery method or the constructor, must be equal to each other, so that the objects are considered equal.");
        endJavadoc();
        addAnnotation(Pure.class);
        addAnnotation(Override.class);
        beginMethod("public boolean equals(@" + importIfPossible(Nullable.class) + " Object object)");
        // TODO: if (object == this) { return true; } first might be valuable for performance reasons.
        // TODO: Kaspar is not sure whether the equals on the classes is preferable to an instanceof check.
        beginIf("object == null || !object.getClass().equals(this.getClass())");
        addStatement("return false");
        endIf();
        addStatement("final @" + importIfPossible(Nonnull.class)+ " " + typeInformation.getName() + " that = (" + typeInformation.getName() + ") object");
        addStatement("boolean result = true");
        final @Nonnull FiniteIterable<RepresentingFieldInformation> representingFieldInformation = typeInformation.getRepresentingFieldInformation();
        ProcessingLog.debugging("generating equals method...");
        for (@Nonnull RepresentingFieldInformation field : representingFieldInformation) {
            ProcessingLog.debugging("...with representing field/method $", field.getAccessCode());
            final @Nonnull String accessCode = field.getAccessCode();
            // TODO: Maybe we should compare primitive values directly instead of boxing them.
            generateComparisonTypeVisitor.visit(field.getType(), Quartet.of("this." + accessCode, "that." + accessCode, this, "result"));
        }
        addStatement("return result");
        endMethod();
    }
    
    /**
     * Generates a compare to method. This method should be implemented by the super class if the
     * comparison algorithm is more complex and if the fields of the type do not implement Comparable.
     */
    protected void generateCompareToMethod() {
        if (typeInformation instanceof ClassInformation && ((ClassInformation) typeInformation).compareToMethod != null) {
            // only generated if the compareTo method is defined somewhere, but not yet implemented
            addAnnotation(Pure.class);
            addAnnotation(Override.class);
            beginMethod("public int compareTo(@" + importIfPossible(Nonnull.class) + " " + typeInformation.getName() + " other)");
            final @Nonnull FiniteIterable<RepresentingFieldInformation> representingFieldInformation = typeInformation.getRepresentingFieldInformation();
            addStatement("int result = 0");
            for (@Nonnull RepresentingFieldInformation field : representingFieldInformation) {
                // TODO: Doesn't work for primitive values! And shouldn't the next field only be used if the comparison of the previous field was 0?
                addStatement("result += " + field.getAccessCode() + ".compareTo(other." + field.getAccessCode() + ")");
            }
            addStatement("return result");
            endMethod();
        } else {
            ProcessingLog.debugging("compareTo method not found for class $", typeInformation.getName());
        }
    }
    
    protected void generateMethods() {
        addSection("Generated Methods");
        generateEqualsMethod();
        generateHashCodeMethod();
        generateToStringMethod();
        // TODO: There is a bug in the validate method (see above).
        // generateValidateMethod();
        generateCompareToMethod();
    }
    
    /* Copied from the former root class. */
    
//    /**
//     * 
//     */
//    
//    /**
//     * Computes and returns the hash code of this object, using the fields of the class which are handed to the class via the recovery method or the constructor.
//     */

    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    protected SubclassGenerator(@Nonnull TypeInformation typeInformation) {
        super(typeInformation.getQualifiedNameOfGeneratedSubclass(), typeInformation.getElement());
        
        this.typeInformation = typeInformation;
        try {
        
            beginClass("class " + typeInformation.getSimpleNameOfGeneratedSubclass() + importWithBounds(typeInformation.getTypeArguments()) + (typeInformation.getElement().getKind() == ElementKind.CLASS ? " extends " : " implements ") + importIfPossible(typeInformation.getType()));
            
            generateFields();
            generateConstructors();
            overrideMethods();
            generateMethods();
            
            endClass();
        } catch (Exception e) {
            throw UnexpectedFailureException.with(e.getMessage(), e);
        }
    }
    
    /**
     * Generates a subclass (the target) of the given type (the source).
     */
    @Pure
    public static void generateSubclassOf(@Nonnull TypeInformation typeInformation) {
        new SubclassGenerator(typeInformation).write();
    }
    
}
