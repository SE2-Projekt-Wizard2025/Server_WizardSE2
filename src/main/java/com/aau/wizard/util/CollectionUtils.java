package com.aau.wizard.util;

import java.util.List;
import java.util.function.Function;

public class CollectionUtils {
    private CollectionUtils() {} // prevent instantiation

    /**
     * Maps a list of elements using the given mapper function, returning an empty list if the input is null.
     *
     * @param input  the input list (may be null)
     * @param mapper the mapping function
     * @return a list of mapped elements or an empty list if input is null
     */
    public static <T, R> List<R> mapOrEmpty(List<T> input, Function<T, R> mapper) {
        if (input == null) return List.of();
        return input.stream().map(mapper).toList();
    }
}
