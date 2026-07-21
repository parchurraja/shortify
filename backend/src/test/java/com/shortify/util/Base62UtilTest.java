package com.shortify.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62UtilTest {

    @Test
    @DisplayName("Should generate random Base62 short code of length 6")
    void testGenerateShortCode() {
        String code = Base62Utils.generateShortCode();
        assertNotNull(code);
        assertEquals(6, code.length());
    }

    @Test
    @DisplayName("Should correctly encode long values to Base62 string")
    void testEncode() {
        assertEquals("a", Base62Utils.encode(0));
        assertNotNull(Base62Utils.encode(12345));
    }

    @Test
    @DisplayName("Should correctly decode Base62 string to long and reverse encode")
    void testEncodeDecodeRoundTrip() {
        long original = 987654321L;
        String encoded = Base62Utils.encode(original);
        long decoded = Base62Utils.decode(encoded);

        assertEquals(original, decoded);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid Base62 characters")
    void testInvalidCharacterDecode() {
        assertThrows(IllegalArgumentException.class, () -> {
            Base62Utils.decode("invalid-char!");
        });
    }
}
