package net.digitalid.utility.conversion;

import java.lang.annotation.Annotation;
import java.util.HashMap;

import javax.annotation.Nonnull;

import net.digitalid.utility.collections.freezable.FreezableHashMap;
import net.digitalid.utility.freezable.annotations.NonFrozen;
import net.digitalid.utility.validation.math.NonNegative;
import net.digitalid.utility.validation.math.Positive;
import net.digitalid.utility.validation.reference.Capturable;
import net.digitalid.utility.validation.state.Pure;

/**
 * The annotations of convertible fields or types, which are used during the converting and recovering of an object.
 */
public class ConverterAnnotations extends FreezableHashMap<Class<? extends Annotation>, Annotation> {
    
    /**
     * Creates a new converter annotations map.
     *
     * @param initialCapacity the initial capacity of the map, see {@link HashMap#HashMap(int, float)}.
     * @param loadFactor the loadFactor of the map, see {@link HashMap#HashMap(int, float)}.
     */
    protected ConverterAnnotations(@NonNegative int initialCapacity, @Positive float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    /**
     * Creates and returns a converter annotations map.
     * 
     * @param initialCapacity the initial capacity of the map, see {@link HashMap#HashMap(int, float)}.
     * 
     * @return a converter annotations map.
     */
    @Pure
    public static @Capturable @Nonnull @NonFrozen ConverterAnnotations get(@NonNegative int initialCapacity) {
        return new ConverterAnnotations(initialCapacity, 0.75f);
    }
    
    /**
     * Creates and returns a converter annotations map.
     * 
     * @return a converter annotations map.
     */
    @Pure
    public static @Capturable @Nonnull @NonFrozen ConverterAnnotations get() {
        return get(16);
    }
    
}
