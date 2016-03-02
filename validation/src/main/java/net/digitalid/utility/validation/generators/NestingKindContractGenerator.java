package net.digitalid.utility.validation.generators;

import javax.annotation.Nonnull;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.NestingKind;

import net.digitalid.utility.validation.annotations.method.Pure;
import net.digitalid.utility.validation.annotations.type.Stateless;
import net.digitalid.utility.validation.contract.Contract;
import net.digitalid.utility.validation.generator.ContractGenerator;
import net.digitalid.utility.validation.processing.ProcessingUtility;
import net.digitalid.utility.validation.processing.TypeImporter;

/**
 * This class generates the contracts for the kinds of nesting.
 * 
 * @see net.digitalid.utility.validation.annotations.type.nesting
 */
@Stateless
public abstract class NestingKindContractGenerator extends ContractGenerator {
    
    /* -------------------------------------------------- Kind -------------------------------------------------- */
    
    /**
     * Returns the nesting kind which the type has to have.
     */
    @Pure
    public abstract @Nonnull NestingKind getKind();
    
    /* -------------------------------------------------- Condition -------------------------------------------------- */
    
    /**
     * Returns the condition for the given element depending on the type of the element.
     */
    @Pure
    public static @Nonnull String getCondition(@Nonnull Element element, @Nonnull NestingKind kind, @Nonnull TypeImporter typeImporter) {
        if (ProcessingUtility.isAssignable(element, Class.class)) {
            switch (kind) {
                case ANONYMOUS: return "#.isAnonymousClass()";
                case LOCAL: return "#.isLocalClass()";
                case MEMBER: return "#.isMemberClass()";
                case TOP_LEVEL: return "!#.isAnonymousClass() && !#.isLocalClass() && !#.isMemberClass()";
                default: return "false";
            }
        } else {
            return "#.getNestingKind() == " + typeImporter.importIfPossible(NestingKind.class) + "." + kind.name();
        }
    }
    
    /* -------------------------------------------------- Contract Generation -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Contract generateContract(@Nonnull Element element, @Nonnull AnnotationMirror annotationMirror, @Nonnull TypeImporter typeImporter) {
        return Contract.with("# == null || " + getCondition(element, getKind(), typeImporter), "The # has to be null or have the nesting kind '" + getKind().name().toLowerCase().replace("_", "-") + "' but was $.", element);
    }
    
}
