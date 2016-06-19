package net.digitalid.utility.generator.generators;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.testing.CustomTest;
import net.digitalid.utility.validation.annotations.type.Immutable;

import org.junit.Test;

@Immutable
abstract class AbstractFields {
    
    private final String first;
    
    @Pure
    public String getFirst() {
        return first;
    }
    
    private final String second;
    
    @Pure
    public String getSecond() {
        return second;
    }
    
    protected AbstractFields(String first, String second) {
        this.first = first;
        this.second = second;
    }
    
}

@Immutable
@GenerateBuilder
class MandatoryFields extends AbstractFields {
    
    protected MandatoryFields(@Nonnull String first, @Nonnull String second) {
        super(first, second);
    }
    
}

@Immutable
@GenerateBuilder
class OptionalFields extends AbstractFields {
    
    protected OptionalFields(@Nullable String first, @Nullable String second) {
        super(first, second);
    }
    
}

@Immutable
@GenerateBuilder
class MixedFields extends AbstractFields {
    
    protected MixedFields(@Nullable String first, @Nonnull String second) {
        super(first, second);
    }
    
}

public class BuilderTest extends CustomTest {
    
    @Pure
    private void testFields(@Nonnull AbstractFields fields, @Nullable String first, @Nullable String second) {
        assertEquals(first, fields.getFirst());
        assertEquals(second, fields.getSecond());
    }
    
    @Test
    public void testMandatoryFields() {
        testFields(MandatoryFieldsBuilder.withFirst("alpha").withSecond("beta").build(), "alpha", "beta");
    }
    
    @Test
    public void testOptionalFields() {
        // TODO: Eliminate the get and support a direct build call!
        testFields(OptionalFieldsBuilder.get().build(), null, null);
        testFields(OptionalFieldsBuilder.withFirst("alpha").build(), "alpha", null);
        testFields(OptionalFieldsBuilder.withSecond("beta").build(), null, "beta");
        testFields(OptionalFieldsBuilder.withSecond("beta").withFirst("alpha").build(), "alpha", "beta");
    }
    
    @Test
    public void testMixedFields() {
        testFields(MixedFieldsBuilder.withSecond("beta").build(), null, "beta");
        testFields(MixedFieldsBuilder.withSecond("beta").withFirst("alpha").build(), "alpha", "beta");
    }
    
}