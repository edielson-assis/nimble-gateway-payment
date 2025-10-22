package br.com.nimble.gateway.payment.util;

import java.util.regex.Pattern;

public final class LoginUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
        Pattern.CASE_INSENSITIVE
    );

    private LoginUtils() { }

    public static boolean isEmail(String value) {
        if (value == null) return false;
        return EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    public static boolean isCpf(String value) {
        if (value == null) return false;
        String digits = onlyDigits(value);
        return digits.length() == 11;
    }

    public static String normalizeCpf(String value) {
        if (value == null) return null;
        return onlyDigits(value);
    }

    public static String normalizeLogin(String value) {
        if (value == null) return null;
        return isCpf(value) ? normalizeCpf(value) : value.trim();
    }

    private static String onlyDigits(String value) {
        return value.replaceAll("\\D", "");
    }
}