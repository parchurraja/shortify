package com.shortify.util;

import java.security.SecureRandom;

public class Base62Utils {

    private static final String BASE62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = BASE62_CHARACTERS.length();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int DEFAULT_CODE_LENGTH = 6;

    /**
     * Generates a random Base62 short code of default length (6).
     */
    public static String generateShortCode() {
        StringBuilder sb = new StringBuilder(DEFAULT_CODE_LENGTH);
        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
            sb.append(BASE62_CHARACTERS.charAt(RANDOM.nextInt(BASE)));
        }
        return sb.toString();
    }

    /**
     * Encodes a decimal number into a Base62 string representation.
     */
    public static String encode(long number) {
        if (number == 0) {
            return String.valueOf(BASE62_CHARACTERS.charAt(0));
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % BASE);
            sb.append(BASE62_CHARACTERS.charAt(remainder));
            number /= BASE;
        }
        return sb.reverse().toString();
    }

    /**
     * Decodes a Base62 string back into its decimal number representation.
     */
    public static long decode(String base62Str) {
        long result = 0;
        long multiplier = 1;
        for (int i = base62Str.length() - 1; i >= 0; i--) {
            char c = base62Str.charAt(i);
            int value = BASE62_CHARACTERS.indexOf(c);
            if (value == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            result += value * multiplier;
            multiplier *= BASE;
        }
        return result;
    }
}
