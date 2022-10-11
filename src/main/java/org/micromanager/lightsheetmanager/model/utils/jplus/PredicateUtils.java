package org.micromanager.lightsheetmanager.model.utils.jplus;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * JDK11 added a static Predicate.not() method to the Predicate interface.
 * This can be removed and replaced if Micro-Manager adopts JDK11.
 */
public interface PredicateUtils {

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> not(Predicate<? super T> target) {
        Objects.requireNonNull(target);
        return (Predicate<T>)target.negate();
    }

}
