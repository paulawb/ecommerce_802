package com.ecommerce.crud.infraestructure.entry_points.dto;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;

public final class FlexibleNumberParser {
    private static final Map<String, Integer> NUMEROS_TEXTO = Map.ofEntries(
            Map.entry("cero", 0),
            Map.entry("un", 1),
            Map.entry("uno", 1),
            Map.entry("una", 1),
            Map.entry("dos", 2),
            Map.entry("tres", 3),
            Map.entry("cuatro", 4),
            Map.entry("cinco", 5),
            Map.entry("seis", 6),
            Map.entry("siete", 7),
            Map.entry("ocho", 8),
            Map.entry("nueve", 9),
            Map.entry("diez", 10),
            Map.entry("once", 11),
            Map.entry("doce", 12),
            Map.entry("trece", 13),
            Map.entry("catorce", 14),
            Map.entry("quince", 15),
            Map.entry("dieciseis", 16),
            Map.entry("diecisiete", 17),
            Map.entry("dieciocho", 18),
            Map.entry("diecinueve", 19),
            Map.entry("veinte", 20),
            Map.entry("veintiuno", 21),
            Map.entry("veintidos", 22),
            Map.entry("veintitres", 23),
            Map.entry("veinticuatro", 24),
            Map.entry("veinticinco", 25),
            Map.entry("veintiseis", 26),
            Map.entry("veintisiete", 27),
            Map.entry("veintiocho", 28),
            Map.entry("veintinueve", 29)
    );

    private static final Map<String, Integer> DECENAS = Map.ofEntries(
            Map.entry("treinta", 30),
            Map.entry("cuarenta", 40),
            Map.entry("cincuenta", 50),
            Map.entry("sesenta", 60),
            Map.entry("setenta", 70),
            Map.entry("ochenta", 80),
            Map.entry("noventa", 90)
    );

    private static final Map<String, Integer> CENTENAS = Map.ofEntries(
            Map.entry("cien", 100),
            Map.entry("ciento", 100),
            Map.entry("doscientos", 200),
            Map.entry("trescientos", 300),
            Map.entry("cuatrocientos", 400),
            Map.entry("quinientos", 500),
            Map.entry("seiscientos", 600),
            Map.entry("setecientos", 700),
            Map.entry("ochocientos", 800),
            Map.entry("novecientos", 900)
    );

    private FlexibleNumberParser() {
    }

    public static Double parseFlexibleDouble(Object value, String fieldName) {
        if (value == null || normalizeFreeText(String.valueOf(value)).isEmpty()) {
            return null;
        }

        if (value instanceof Number number) {
            double parsed = number.doubleValue();
            validateFinite(parsed, fieldName);
            validateNonNegative(parsed, fieldName);
            return parsed;
        }

        String original = String.valueOf(value);
        Double wordsValue = parseWordsAsDouble(original);
        if (wordsValue != null) {
            validateNonNegative(wordsValue, fieldName);
            return wordsValue;
        }

        try {
            double parsed = Double.parseDouble(normalizeDecimalNumber(original, fieldName));
            validateFinite(parsed, fieldName);
            validateNonNegative(parsed, fieldName);
            return parsed;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    fieldName + " debe aceptar formatos como 40000, 40000.0, 40.000, $40.000 o \"cuarenta mil\"."
            );
        }
    }

    public static Integer parseFlexibleInteger(Object value, String fieldName) {
        Double parsed = parseFlexibleDouble(value, fieldName);
        if (parsed == null) {
            return null;
        }
        if (parsed % 1 != 0) {
            throw new IllegalArgumentException(
                    fieldName + " debe representar un numero entero (ejemplo valido: 6, 6.0, 6.000 o \"seis\")."
            );
        }
        if (parsed > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(fieldName + " excede el maximo permitido.");
        }
        return parsed.intValue();
    }

    public static String normalizeFreeText(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
        return normalized;
    }

    public static String normalizeSearchToken(String value) {
        String normalized = normalizeFreeText(value);
        return normalized.replaceAll("[\\$]", "")
                .replace("cop", "")
                .replace("pesos colombianos", "")
                .replace("peso colombiano", "")
                .replace("pesos", "")
                .replace("peso", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String normalizeNumericForContains(Number number) {
        if (number == null) {
            return "";
        }
        String text = String.valueOf(number).toLowerCase(Locale.ROOT);
        if (text.endsWith(".0")) {
            text = text.substring(0, text.length() - 2);
        }
        return text;
    }

    private static String normalizeDecimalNumber(String original, String fieldName) {
        String text = normalizeSearchToken(original)
                .replace("_", "")
                .replace("'", "")
                .replace("`", "")
                .replace(" ", "");

        if (text.startsWith("+")) {
            text = text.substring(1);
        }
        if (text.startsWith("-")) {
            throw new IllegalArgumentException(fieldName + " no puede ser negativo.");
        }

        if (!text.matches("[0-9.,]+")) {
            throw new NumberFormatException("Formato numerico invalido");
        }

        if (text.contains(",") && text.contains(".")) {
            return text.replace(".", "").replace(",", ".");
        }
        if (text.contains(",") && !text.contains(".")) {
            return text.replace(",", ".");
        }
        if (text.contains(".")) {
            int dots = countChar(text, '.');
            if (dots > 1 || looksLikeThousandsSeparator(text)) {
                return text.replace(".", "");
            }
        }
        return text;
    }

    private static int countChar(String text, char ch) {
        int total = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ch) {
                total++;
            }
        }
        return total;
    }

    private static boolean looksLikeThousandsSeparator(String text) {
        String[] parts = text.split("\\.");
        if (parts.length < 2) {
            return false;
        }
        for (int i = 1; i < parts.length; i++) {
            if (parts[i].length() != 3 || !parts[i].chars().allMatch(Character::isDigit)) {
                return false;
            }
        }
        return parts[0].chars().allMatch(Character::isDigit);
    }

    private static Double parseWordsAsDouble(String original) {
        String normalized = normalizeSearchToken(original)
                .replace("-", " ")
                .replace(" y ", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.isEmpty()) {
            return null;
        }

        if (!normalized.chars().allMatch(ch -> Character.isLetter(ch) || Character.isWhitespace(ch))) {
            return null;
        }

        Long parsed = parseWords(normalized);
        return parsed == null ? null : parsed.doubleValue();
    }

    private static Long parseWords(String text) {
        long total = 0;
        long current = 0;

        for (String token : text.split(" ")) {
            if (token.isBlank()) {
                continue;
            }
            if (NUMEROS_TEXTO.containsKey(token)) {
                current += NUMEROS_TEXTO.get(token);
                continue;
            }
            if (DECENAS.containsKey(token)) {
                current += DECENAS.get(token);
                continue;
            }
            if (CENTENAS.containsKey(token)) {
                current += CENTENAS.get(token);
                continue;
            }
            if ("mil".equals(token)) {
                if (current == 0) {
                    current = 1;
                }
                total += current * 1000;
                current = 0;
                continue;
            }
            if ("millon".equals(token) || "millones".equals(token)) {
                if (current == 0) {
                    current = 1;
                }
                total += current * 1_000_000L;
                current = 0;
                continue;
            }
            return null;
        }

        return total + current;
    }

    private static void validateFinite(double value, String fieldName) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException(fieldName + " no es valido.");
        }
    }

    private static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " no puede ser negativo.");
        }
    }
}
