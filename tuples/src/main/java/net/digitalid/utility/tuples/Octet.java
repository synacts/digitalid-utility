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
package net.digitalid.utility.tuples;

import java.util.Objects;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.ownership.NonCapturable;
import net.digitalid.utility.circumfixes.Quotes;
import net.digitalid.utility.validation.annotations.index.Index;
import net.digitalid.utility.validation.annotations.math.NonNegative;
import net.digitalid.utility.validation.annotations.type.Immutable;

/**
 * This class implements an immutable octet.
 */
@Immutable
@SuppressWarnings("EqualsAndHashcode")
public class Octet<E0, E1, E2, E3, E4, E5, E6, E7> extends Septet<E0, E1, E2, E3, E4, E5, E6> {
    
    /* -------------------------------------------------- Element 0 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set0(@Captured E0 element0) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 1 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set1(@Captured E1 element1) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 2 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set2(@Captured E2 element2) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 3 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set3(@Captured E3 element3) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 4 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set4(@Captured E4 element4) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 5 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set5(@Captured E5 element5) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 6 -------------------------------------------------- */
    
    @Pure
    @Override
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set6(@Captured E6 element6) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Element 7 -------------------------------------------------- */
    
    protected final E7 element7;
    
    /**
     * Returns the eighth element of this tuple.
     */
    @Pure
    public @NonCapturable E7 get7() {
        return element7;
    }
    
    /**
     * Returns a new tuple with the eighth element set to the given object.
     */
    @Pure
    public @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> set7(@Captured E7 element7) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    protected Octet(@Captured E0 element0, @Captured E1 element1, @Captured E2 element2, @Captured E3 element3, @Captured E4 element4, @Captured E5 element5, @Captured E6 element6, @Captured E7 element7) {
        super(element0, element1, element2, element3, element4, element5, element6);
        
        this.element7 = element7;
    }
    
    /**
     * Returns a new octet with the given elements.
     */
    @Pure
    public static <E0, E1, E2, E3, E4, E5, E6, E7> @Nonnull Octet<E0, E1, E2, E3, E4, E5, E6, E7> of(@Captured E0 element0, @Captured E1 element1, @Captured E2 element2, @Captured E3 element3, @Captured E4 element4, @Captured E5 element5, @Captured E6 element6, @Captured E7 element7) {
        return new Octet<>(element0, element1, element2, element3, element4, element5, element6, element7);
    }
    
    /* -------------------------------------------------- Tuple -------------------------------------------------- */
    
    @Pure
    @Override
    public @NonNegative int size() {
        return 8;
    }
    
    @Pure
    @Override
    public @NonCapturable Object get(@Index int index) {
        if (index == 7) { return element7; }
        else { return super.get(index); }
    }
    
    /* -------------------------------------------------- Object -------------------------------------------------- */
    
    @Pure
    @Override
    protected boolean elementEquals(@Nonnull Pair<?, ?> tuple) {
        return super.elementEquals(tuple) && Objects.equals(this.element7, ((Octet) tuple).element7);
    }
    
    @Pure
    @Override
    public int hashCode() {
        return 83 * super.hashCode() + Objects.hashCode(element7);
    }
    
    @Pure
    @Override
    public @Nonnull String toStringWithoutParentheses() {
        return super.toStringWithoutParentheses() + ", " + Quotes.inCode(element7);
    }
    
}
