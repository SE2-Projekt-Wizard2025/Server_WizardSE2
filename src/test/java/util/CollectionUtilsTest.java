package util;

import com.aau.wizard.util.CollectionUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CollectionUtilsTest {

    @Test
    void testMapOrEmptyWithNonNullInputReturnsMappedList() {
        List<String> input = List.of("a", "b", "c");
        List<String> result = CollectionUtils.mapOrEmpty(input, s -> s.toUpperCase(Locale.ROOT));

        assertEquals(List.of("A", "B", "C"), result);
    }

    @Test
    void testMapOrEmptyWithEmptyInputReturnsEmptyList() {
        List<String> input = List.of();
        List<Integer> result = CollectionUtils.mapOrEmpty(input, String::length);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testMapOrEmptyWithCustomMapper() {
        List<String> input = List.of("foo", "bar");
        List<Integer> result = CollectionUtils.mapOrEmpty(input, String::length);

        assertEquals(List.of(3, 3), result);
    }
}
