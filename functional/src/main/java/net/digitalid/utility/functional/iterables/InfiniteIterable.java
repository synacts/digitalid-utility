package net.digitalid.utility.functional.iterables;

import javax.annotation.Nonnull;

import net.digitalid.utility.annotations.method.Pure;
import net.digitalid.utility.annotations.ownership.Captured;
import net.digitalid.utility.annotations.parameter.Referenced;
import net.digitalid.utility.annotations.parameter.Unmodified;
import net.digitalid.utility.functional.interfaces.Predicate;
import net.digitalid.utility.functional.interfaces.Producer;
import net.digitalid.utility.functional.interfaces.UnaryFunction;
import net.digitalid.utility.functional.interfaces.UnaryOperator;
import net.digitalid.utility.functional.iterators.FilteringIterator;
import net.digitalid.utility.functional.iterators.FlatteningIterator;
import net.digitalid.utility.functional.iterators.GeneratingIterator;
import net.digitalid.utility.functional.iterators.IteratingIterator;
import net.digitalid.utility.functional.iterators.MappingIterator;
import net.digitalid.utility.functional.iterators.PruningIterator;
import net.digitalid.utility.functional.iterators.RepeatingIterator;
import net.digitalid.utility.functional.iterators.ZippingIterator;
import net.digitalid.utility.tuples.Pair;
import net.digitalid.utility.validation.annotations.math.NonNegative;
import net.digitalid.utility.validation.annotations.math.Positive;
import net.digitalid.utility.validation.annotations.type.Functional;
import net.digitalid.utility.validation.annotations.type.ReadOnly;

/**
 * This interface extends the functional iterable interface to model infinite iterables.
 */
@ReadOnly
@Functional
public interface InfiniteIterable<E> extends FunctionalIterable<E> {
    
    /* -------------------------------------------------- Constructors -------------------------------------------------- */
    
    /**
     * Returns a new infinite iterable that repeats the given object infinitely.
     */
    @Pure
    public static <E> @Nonnull InfiniteIterable<E> repeat(@Captured E object) {
        return () -> RepeatingIterator.with(object);
    }
    
    /**
     * Returns a new infinite iterable that generates an infinite number of elements with the given producer.
     */
    @Pure
    public static <E> @Nonnull InfiniteIterable<E> generate(@Referenced @Unmodified @Nonnull Producer<? extends E> producer) {
        return () -> GeneratingIterator.with(producer);
    }
    
    /**
     * Returns a new infinite iterable that iterates over the sequence produced by the given operator from the given seed.
     */
    @Pure
    public static <E> @Nonnull InfiniteIterable<E> iterate(@Nonnull UnaryOperator<E> operator, @Captured E seed) {
        return () -> IteratingIterator.with(operator, seed);
    }
    
    /* -------------------------------------------------- Size -------------------------------------------------- */
    
    @Pure
    @Override
    public default boolean isEmpty() {
        return false;
    }
    
    @Pure
    @Override
    public default @NonNegative int size(@Positive int limit) {
        if (limit <= 0) { throw new IndexOutOfBoundsException("The limit has to be positive but was " + limit + "."); }
        
        return limit;
    }
    
    /* -------------------------------------------------- Filtering -------------------------------------------------- */
    
    @Pure
    @Override
    public default @Nonnull InfiniteIterable<E> filter(@Nonnull Predicate<? super E> predicate) {
        return () -> FilteringIterator.with(iterator(), predicate);
    }
    
    @Pure
    @Override
    public default @Nonnull FunctionalIterable<E> filterNulls() {
        return filter(element -> element != null);
    }
    
    /* -------------------------------------------------- Mapping -------------------------------------------------- */
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<F> map(@Nonnull UnaryFunction<? super E, ? extends F> function) {
        return () -> MappingIterator.with(iterator(), function);
    }
    
    /* -------------------------------------------------- Instance -------------------------------------------------- */
    
    @Pure
    @Override
    public default <T> @Nonnull InfiniteIterable<T> instanceOf(@Nonnull Class<T> type) {
        return filter(type::isInstance).map(type::cast);
    }
    
    /* -------------------------------------------------- Pruning -------------------------------------------------- */
    
    @Pure
    @Override
    public default @Nonnull InfiniteIterable<E> skip(@Positive int number) {
        return () -> PruningIterator.with(iterator(), number, Integer.MAX_VALUE);
    }
    
    /* -------------------------------------------------- Zipping -------------------------------------------------- */
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<Pair<E, F>> zipShortest(@Nonnull InfiniteIterable<? extends F> iterable) {
        return () -> ZippingIterator.with(iterator(), iterable.iterator(), true);
    }
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<Pair<E, F>> zipLongest(@Nonnull FiniteIterable<? extends F> iterable) {
        return () -> ZippingIterator.with(iterator(), iterable.iterator(), false);
    }
    
    /* -------------------------------------------------- Flattening -------------------------------------------------- */
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<F> flatten(@Positive int level) {
        return () -> FlatteningIterator.with(iterator(), level);
    }
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<F> flattenOne() {
        return flatten(1);
    }
    
    @Pure
    @Override
    public default <F> @Nonnull InfiniteIterable<F> flattenAll() {
        return flatten(Integer.MAX_VALUE);
    }
    
}
