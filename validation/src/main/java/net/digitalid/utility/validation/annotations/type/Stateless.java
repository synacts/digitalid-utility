package net.digitalid.utility.validation.annotations.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import net.digitalid.utility.logging.processing.ProcessingLog;
import net.digitalid.utility.logging.processing.StaticProcessingEnvironment;
import net.digitalid.utility.logging.processing.SourcePosition;
import net.digitalid.utility.validation.annotations.meta.Validator;
import net.digitalid.utility.validation.annotations.method.Pure;
import net.digitalid.utility.validation.generator.TypeValidator;

/**
 * This annotation indicates that the objects of the annotated class are stateless (have no non-static fields).
 * 
 * @see Utiliy
 * @see Immutable
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Validator(Stateless.Validator.class)
public @interface Stateless {
    
    @Stateless
    public static class Validator extends TypeValidator {
        
        @Pure
        @Override
        public void checkUsage(@Nonnull Element element, @Nonnull AnnotationMirror annotationMirror) {
            for (@Nonnull VariableElement field : ElementFilter.fieldsIn(StaticProcessingEnvironment.getElementUtils().getAllMembers((TypeElement) element))) {
                if (!field.getModifiers().contains(Modifier.STATIC)) {
                    ProcessingLog.error("The stateless type $ may not have non-static fields.", SourcePosition.of(field), element);
                }
            }
        }
        
    }
    
}
