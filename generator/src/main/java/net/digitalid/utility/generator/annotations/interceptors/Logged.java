package net.digitalid.utility.generator.annotations.interceptors;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.generator.annotations.meta.Interceptor;
import net.digitalid.utility.generator.information.method.MethodInformation;
import net.digitalid.utility.generator.interceptor.MethodInterceptor;
import net.digitalid.utility.logging.Level;
import net.digitalid.utility.logging.Log;
import net.digitalid.utility.processor.generator.JavaFileGenerator;
import net.digitalid.utility.validation.annotations.type.Stateless;

/**
 * This annotation indicates that every call to the annotated method is logged with the given level.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Interceptor(Logged.Interceptor.class)
public @interface Logged {
    
    /* -------------------------------------------------- Value -------------------------------------------------- */
    
    /**
     * Returns the level at which the method call is logged.
     */
    @Nonnull Level value() default Level.VERBOSE;
    
    /* -------------------------------------------------- Interceptor -------------------------------------------------- */
    
    /**
     * This class generates content for the annotated method.
     */
    @Stateless
    public static class Interceptor extends MethodInterceptor {
        
        @Pure
        @Override
        protected @Nonnull String getPrefix() {
            return "logged";
        }
        
        @Pure
        @Override
        protected void implementInterceptorMethod(@Nonnull JavaFileGenerator javaFileGenerator, @Nonnull MethodInformation method, @Nonnull String statement, @Nullable String resultVariable, @Nullable String defaultValue) {
            // TODO: The following statement does not work for methods without a return type!
            javaFileGenerator.addStatement(method.getReturnType(javaFileGenerator) + " " + resultVariable + " = " + defaultValue);
            javaFileGenerator.beginTry();
            javaFileGenerator.importIfPossible(Log.class); // TODO: importIfPossible only imports the argument *if possible*! You should replace any "Log." string with 'javaFileGenerator.importIfPossible(Log.class) + "."'.
            javaFileGenerator.addStatement("Log.verbose(\"" + method.getName() + "() {'\")");
            javaFileGenerator.addStatement(resultVariable + " = " + statement);
            javaFileGenerator.endTryOrCatchBeginFinally();
            if (resultVariable != null) {
                javaFileGenerator.addStatement("Log.verbose(\"} = (\" + " + resultVariable + " + \")\")");
            } else {
                javaFileGenerator.addStatement("Log.verbose(\"}\")");
            }
            javaFileGenerator.endFinally();
            if (resultVariable != null && method.hasReturnType()) {
                javaFileGenerator.addStatement("return " + resultVariable);
            }
        }
    
    }
    
}