package com.ikonicit.resource.tracker.predicates;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * @author Parasuram
 */
public class Predicates {
    public static Predicate<Integer> isIdNotEmpty = num -> num != null && num != 0;
    public static Predicate<Object> isNotNull = object -> object != null;
    public static Predicate<Object> isNull = object -> object == null;
    public static BiPredicate<Integer, Integer> isIdsEqual = (one, two) -> one.equals(two);
    public static Predicate<Long> isNotZero = count -> count > 0;
}
