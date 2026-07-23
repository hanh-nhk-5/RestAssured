package resources.utils;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

public class AssertHelpers {
    public static void assertNonBlank(String value, String message) {
        assertNotNull(value, message);
        assertFalse(value.trim().isEmpty(), message);
    }
}
