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
package net.digitalid.utility.concurrency.map;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.digitalid.utility.annotations.method.Impure;
import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Capturable;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.ownership.NonCapturable;
import net.digitalid.utility.annotations.ownership.NonCaptured;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.circumfixes.Brackets;
import net.digitalid.utility.functional.iterables.FiniteIterable;
import net.digitalid.utility.generator.annotations.generators.GenerateBuilder;
import net.digitalid.utility.generator.annotations.generators.GenerateSubclass;
import net.digitalid.utility.validation.annotations.generation.Default;
import net.digitalid.utility.validation.annotations.generation.Recover;
import net.digitalid.utility.validation.annotations.math.NonNegative;
import net.digitalid.utility.validation.annotations.math.Positive;
import net.digitalid.utility.validation.annotations.type.Mutable;

/**
 * Extends Java's {@link java.util.concurrent.ConcurrentHashMap ConcurrentHashMap} implementation with more useful methods.
 * 
 * @param <K> the type of the keys of this map.
 * @param <V> the type of the values of this map.
 */
@Mutable
@GenerateBuilder
@GenerateSubclass
public abstract class ConcurrentHashMap<K, V> extends java.util.concurrent.ConcurrentHashMap<K, V> implements ConcurrentMap<K, V> {
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    @Recover
    protected ConcurrentHashMap(@NonNegative @Default("16") int initialCapacity, @Positive @Default("0.75f") float loadFactor, @Positive @Default("1") int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }
    
    protected ConcurrentHashMap(@NonCaptured @Unmodified @Nonnull Map<? extends K, ? extends V> map) {
        super(map);
    }
    
    /**
     * Returns a new concurrent hash map with the mappings of the given map or null if the given map is null.
     */
    @Pure
    public static @Capturable <K, V> ConcurrentHashMap<K, V> withMappingsOf(@NonCaptured @Unmodified Map<? extends K, ? extends V> map) {
        return map == null ? null : new ConcurrentHashMapSubclass<>(map);
    }
    
    /* -------------------------------------------------- ConcurrentMap -------------------------------------------------- */
    
    @Impure
    @Override
    public @NonCapturable @Nonnull V putIfAbsentElseReturnPresent(@Captured @Nonnull K key, @Captured @Nonnull V value) {
        final @Nullable V previous = putIfAbsent(key, value);
        if (previous == null) { return value; }
        else { return previous; }
    }
    
    /* -------------------------------------------------- Cloneable -------------------------------------------------- */
    
    @Pure
    @Override
    @SuppressWarnings("unchecked")
    public @Capturable @Nonnull ConcurrentHashMap<K, V> clone() {
        try {
            return (ConcurrentHashMap<K, V>) super.clone();
        } catch (@Nonnull CloneNotSupportedException exception) {
            throw new RuntimeException("This should never happen because ConcurrentHashMap implements Cloneable.", exception);
        }
    }
    
    /* -------------------------------------------------- Object -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull String toString() {
        return FiniteIterable.of(entrySet()).map(entry -> entry.getKey() + ": " + entry.getValue()).join(Brackets.CURLY);
    }
    
}
