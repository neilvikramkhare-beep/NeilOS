package com.neilos.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * StringUtils - Comprehensive string utility class for NeilOS
 * Provides extensive string manipulation, formatting, validation,
 * and conversion utilities.
 * 
 * Features:
 * - String validation (empty, null, blank)
 * - String manipulation (trim, pad, truncate, reverse)
 * - Case conversion (camelCase, snake_case, kebab-case, PascalCase)
 * - Substring operations
 * - String comparison
 * - Pattern matching and regex utilities
 * - String tokenization
 * - Character utilities
 * - String formatting
 * - HTML/XML escaping
 * - Base64 encoding/decoding
 * - String similarity and distance (Levenshtein, Jaro-Winkler)
 * - UUID generation
 * - Slug generation
 * - Word manipulation (capitalize, pluralize, singularize)
 * - Random string generation
 * - String splitting and joining
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class StringUtils {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Empty string */
    public static final String EMPTY = "";
    
    /** New line characters */
    public static final String NEWLINE = System.lineSeparator();
    public static final String NEWLINE_UNIX = "\n";
    public static final String NEWLINE_WINDOWS = "\r\n";
    public static final String NEWLINE_MAC = "\r";
    
    /** Common strings */
    public static final String SPACE = " ";
    public static final String TAB = "\t";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String COLON = ":";
    public static final String PERIOD = ".";
    public static final String UNDERSCORE = "_";
    public static final String DASH = "-";
    public static final String SLASH = "/";
    public static final String BACKSLASH = "\\";
    
    /** Character sets */
    public static final String ALPHABET_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String ALPHABET_LOWER = "abcdefghijklmnopqrstuvwxyz";
    public static final String ALPHABET = ALPHABET_UPPER + ALPHABET_LOWER;
    public static final String DIGITS = "0123456789";
    public static final String ALPHANUMERIC = ALPHABET + DIGITS;
    public static final String HEX_CHARS = "0123456789ABCDEFabcdef";
    public static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    public static final String WHITESPACE = " \t\n\r\f\v";
    
    /** HTML entities */
    public static final Map<String, String> HTML_ENTITIES = new HashMap<>();
    static {
        HTML_ENTITIES.put("&", "&amp;");
        HTML_ENTITIES.put("<", "&lt;");
        HTML_ENTITIES.put(">", "&gt;");
        HTML_ENTITIES.put("\"", "&quot;");
        HTML_ENTITIES.put("'", "&apos;");
        HTML_ENTITIES.put("©", "&copy;");
        HTML_ENTITIES.put("®", "&reg;");
        HTML_ENTITIES.put("™", "&trade;");
        HTML_ENTITIES.put("€", "&euro;");
        HTML_ENTITIES.put("£", "&pound;");
        HTML_ENTITIES.put("¥", "&yen;");
        HTML_ENTITIES.put("¢", "&cent;");
    }
    
    /** HTML entity reverse mapping */
    private static final Map<String, String> HTML_ENTITIES_REVERSE = new HashMap<>();
    static {
        for (Map.Entry<String, String> entry : HTML_ENTITIES.entrySet()) {
            HTML_ENTITIES_REVERSE.put(entry.getValue(), entry.getKey());
        }
    }
    
    // ============================================================
    // NULL/EMPTY CHECKS
    // ============================================================
    
    /**
     * Checks if a string is null or empty
     * 
     * @param str The string to check
     * @return true if null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }
    
    /**
     * Checks if a string is not null and not empty
     * 
     * @param str The string to check
     * @return true if not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * Checks if a string is null, empty, or whitespace only
     * 
     * @param str The string to check
     * @return true if null, empty, or whitespace only
     */
    public static boolean isBlank(String str) {
        if (str == null) return true;
        return str.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not null, not empty, and not whitespace only
     * 
     * @param str The string to check
     * @return true if not blank
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
    
    /**
     * Returns the string if not null, otherwise an empty string
     * 
     * @param str The string
     * @return The string or empty string if null
     */
    public static String nullToEmpty(String str) {
        return str == null ? EMPTY : str;
    }
    
    /**
     * Returns the string if not null, otherwise a default string
     * 
     * @param str The string
     * @param defaultStr The default string
     * @return The string or default if null
     */
    public static String nullToDefault(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }
    
    // ============================================================
    // STRING MANIPULATION
    // ============================================================
    
    /**
     * Trims whitespace from both ends of a string
     * 
     * @param str The string to trim
     * @return The trimmed string, or null if input is null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }
    
    /**
     * Trims whitespace from the left of a string
     * 
     * @param str The string to trim
     * @return The trimmed string
     */
    public static String trimLeft(String str) {
        if (str == null) return null;
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }
    
    /**
     * Trims whitespace from the right of a string
     * 
     * @param str The string to trim
     * @return The trimmed string
     */
    public static String trimRight(String str) {
        if (str == null) return null;
        int i = str.length() - 1;
        while (i >= 0 && Character.isWhitespace(str.charAt(i))) {
            i--;
        }
        return str.substring(0, i + 1);
    }
    
    /**
     * Pads a string to the left with spaces
     * 
     * @param str The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String padLeft(String str, int length) {
        return padLeft(str, length, ' ');
    }
    
    /**
     * Pads a string to the left with a character
     * 
     * @param str The string to pad
     * @param length The desired length
     * @param padChar The pad character
     * @return The padded string
     */
    public static String padLeft(String str, int length, char padChar) {
        if (str == null) return null;
        if (str.length() >= length) return str;
        StringBuilder sb = new StringBuilder(length);
        for (int i = str.length(); i < length; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }
    
    /**
     * Pads a string to the right with spaces
     * 
     * @param str The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String padRight(String str, int length) {
        return padRight(str, length, ' ');
    }
    
    /**
     * Pads a string to the right with a character
     * 
     * @param str The string to pad
     * @param length The desired length
     * @param padChar The pad character
     * @return The padded string
     */
    public static String padRight(String str, int length, char padChar) {
        if (str == null) return null;
        if (str.length() >= length) return str;
        StringBuilder sb = new StringBuilder(str);
        for (int i = str.length(); i < length; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }
    
    /**
     * Pads a string to center with spaces
     * 
     * @param str The string to pad
     * @param length The desired length
     * @return The padded string
     */
    public static String padCenter(String str, int length) {
        return padCenter(str, length, ' ');
    }
    
    /**
     * Pads a string to center with a character
     * 
     * @param str The string to pad
     * @param length The desired length
     * @param padChar The pad character
     * @return The padded string
     */
    public static String padCenter(String str, int length, char padChar) {
        if (str == null) return null;
        if (str.length() >= length) return str;
        int leftPad = (length - str.length()) / 2;
        int rightPad = length - str.length() - leftPad;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < leftPad; i++) sb.append(padChar);
        sb.append(str);
        for (int i = 0; i < rightPad; i++) sb.append(padChar);
        return sb.toString();
    }
    
    /**
     * Truncates a string to a maximum length, adding "..." if truncated
     * 
     * @param str The string to truncate
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String str, int maxLength) {
        return truncate(str, maxLength, "...");
    }
    
    /**
     * Truncates a string to a maximum length with a custom suffix
     * 
     * @param str The string to truncate
     * @param maxLength The maximum length
     * @param suffix The suffix to add if truncated
     * @return The truncated string
     */
    public static String truncate(String str, int maxLength, String suffix) {
        if (str == null) return null;
        if (str.length() <= maxLength) return str;
        int suffixLen = suffix != null ? suffix.length() : 0;
        if (maxLength <= suffixLen) return str.substring(0, maxLength);
        return str.substring(0, maxLength - suffixLen) + suffix;
    }
    
    /**
     * Reverses a string
     * 
     * @param str The string to reverse
     * @return The reversed string
     */
    public static String reverse(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }
    
    /**
     * Removes all whitespace from a string
     * 
     * @param str The string
     * @return The string without whitespace
     */
    public static String removeWhitespace(String str) {
        if (str == null) return null;
        return str.replaceAll("\\s+", "");
    }
    
    /**
     * Removes all occurrences of a character from a string
     * 
     * @param str The string
     * @param ch The character to remove
     * @return The string without the character
     */
    public static String removeChar(String str, char ch) {
        if (str == null) return null;
        return str.replace(String.valueOf(ch), "");
    }
    
    /**
     * Removes all occurrences of a substring from a string
     * 
     * @param str The string
     * @param substring The substring to remove
     * @return The string without the substring
     */
    public static String removeSubstring(String str, String substring) {
        if (str == null || substring == null) return str;
        return str.replace(substring, "");
    }
    
    /**
     * Removes duplicate whitespace (replaces multiple spaces with one)
     * 
     * @param str The string
     * @return The string with normalized whitespace
     */
    public static String normalizeWhitespace(String str) {
        if (str == null) return null;
        return str.trim().replaceAll("\\s+", " ");
    }
    
    // ============================================================
    // CASE CONVERSION
    // ============================================================
    
    /**
     * Converts a string to camelCase
     * 
     * @param str The string to convert
     * @return The camelCase string
     */
    public static String toCamelCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        String[] parts = str.split("[\\s_-]+");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                      .append(part.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    /**
     * Converts a string to PascalCase
     * 
     * @param str The string to convert
     * @return The PascalCase string
     */
    public static String toPascalCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        String camel = toCamelCase(str);
        return Character.toUpperCase(camel.charAt(0)) + camel.substring(1);
    }
    
    /**
     * Converts a string to snake_case
     * 
     * @param str The string to convert
     * @return The snake_case string
     */
    public static String toSnakeCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        // Convert camelCase to snake_case
        String result = str.replaceAll("([a-z])([A-Z])", "$1_$2");
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2");
        result = result.replaceAll("[\\s-]+", "_");
        return result.toLowerCase();
    }
    
    /**
     * Converts a string to kebab-case
     * 
     * @param str The string to convert
     * @return The kebab-case string
     */
    public static String toKebabCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        String result = str.replaceAll("([a-z])([A-Z])", "$1-$2");
        result = result.replaceAll("([A-Z]+)([A-Z][a-z])", "$1-$2");
        result = result.replaceAll("[\\s_]+", "-");
        return result.toLowerCase();
    }
    
    /**
     * Converts a string to Title Case
     * 
     * @param str The string to convert
     * @return The Title Case string
     */
    public static String toTitleCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                if (result.length() > 0) result.append(" ");
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    /**
     * Converts a string to Sentence case
     * 
     * @param str The string to convert
     * @return The Sentence case string
     */
    public static String toSentenceCase(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return str;
        
        String trimmed = str.trim();
        if (trimmed.isEmpty()) return str;
        
        return Character.toUpperCase(trimmed.charAt(0)) + 
               trimmed.substring(1).toLowerCase();
    }
    
    /**
     * Converts a string to uppercase with underscores
     * 
     * @param str The string to convert
     * @return The UPPER_CASE string
     */
    public static String toUpperUnderscore(String str) {
        return toSnakeCase(str).toUpperCase();
    }
    
    /**
     * Converts a string to lowercase with dashes
     * 
     * @param str The string to convert
     * @return The lower-dash string
     */
    public static String toLowerDash(String str) {
        return toKebabCase(str);
    }
    
    /**
     * Capitalizes the first letter of a string
     * 
     * @param str The string
     * @return The string with first letter capitalized
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    /**
     * Uncapitalizes the first letter of a string
     * 
     * @param str The string
     * @return The string with first letter lowercased
     */
    public static String uncapitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
    
    // ============================================================
    // SUBSTRING OPERATIONS
    // ============================================================
    
    /**
     * Gets a substring before the first occurrence of a separator
     * 
     * @param str The string
     * @param separator The separator
     * @return The substring before the separator
     */
    public static String substringBefore(String str, String separator) {
        if (str == null || separator == null || str.isEmpty()) return str;
        int index = str.indexOf(separator);
        return index < 0 ? str : str.substring(0, index);
    }
    
    /**
     * Gets a substring after the first occurrence of a separator
     * 
     * @param str The string
     * @param separator The separator
     * @return The substring after the separator
     */
    public static String substringAfter(String str, String separator) {
        if (str == null || separator == null || str.isEmpty()) return str;
        int index = str.indexOf(separator);
        return index < 0 ? str : str.substring(index + separator.length());
    }
    
    /**
     * Gets a substring before the last occurrence of a separator
     * 
     * @param str The string
     * @param separator The separator
     * @return The substring before the last separator
     */
    public static String substringBeforeLast(String str, String separator) {
        if (str == null || separator == null || str.isEmpty()) return str;
        int index = str.lastIndexOf(separator);
        return index < 0 ? str : str.substring(0, index);
    }
    
    /**
     * Gets a substring after the last occurrence of a separator
     * 
     * @param str The string
     * @param separator The separator
     * @return The substring after the last separator
     */
    public static String substringAfterLast(String str, String separator) {
        if (str == null || separator == null || str.isEmpty()) return str;
        int index = str.lastIndexOf(separator);
        return index < 0 ? str : str.substring(index + separator.length());
    }
    
    /**
     * Gets the leftmost n characters of a string
     * 
     * @param str The string
     * @param n The number of characters
     * @return The leftmost n characters
     */
    public static String left(String str, int n) {
        if (str == null) return null;
        if (n < 0) return "";
        if (str.length() <= n) return str;
        return str.substring(0, n);
    }
    
    /**
     * Gets the rightmost n characters of a string
     * 
     * @param str The string
     * @param n The number of characters
     * @return The rightmost n characters
     */
    public static String right(String str, int n) {
        if (str == null) return null;
        if (n < 0) return "";
        if (str.length() <= n) return str;
        return str.substring(str.length() - n);
    }
    
    /**
     * Gets the middle substring of a string
     * 
     * @param str The string
     * @param start The start index
     * @param length The length
     * @return The middle substring
     */
    public static String mid(String str, int start, int length) {
        if (str == null) return null;
        if (start < 0 || length < 0) return "";
        if (start >= str.length()) return "";
        int end = Math.min(start + length, str.length());
        return str.substring(start, end);
    }
    
    // ============================================================
    // STRING COMPARISON
    // ============================================================
    
    /**
     * Compares two strings ignoring case
     * 
     * @param str1 First string
     * @param str2 Second string
     * @return true if equal ignoring case
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) return str2 == null;
        return str1.equalsIgnoreCase(str2);
    }
    
    /**
     * Compares two strings ignoring null and case
     * 
     * @param str1 First string
     * @param str2 Second string
     * @return true if equal ignoring null and case
     */
    public static boolean equalsIgnoreNull(String str1, String str2) {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equalsIgnoreCase(str2);
    }
    
    /**
     * Checks if a string contains a substring ignoring case
     * 
     * @param str The string
     * @param search The substring to search
     * @return true if contains ignoring case
     */
    public static boolean containsIgnoreCase(String str, String search) {
        if (str == null || search == null) return false;
        return str.toLowerCase().contains(search.toLowerCase());
    }
    
    /**
     * Checks if a string starts with a prefix ignoring case
     * 
     * @param str The string
     * @param prefix The prefix
     * @return true if starts with ignoring case
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) return false;
        return str.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    /**
     * Checks if a string ends with a suffix ignoring case
     * 
     * @param str The string
     * @param suffix The suffix
     * @return true if ends with ignoring case
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) return false;
        return str.toLowerCase().endsWith(suffix.toLowerCase());
    }
    
    /**
     * Calculates the Levenshtein distance between two strings
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The Levenshtein distance
     */
    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null) return s2 == null ? 0 : s2.length();
        if (s2 == null) return s1.length();
        
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = s1.charAt(i - 1) == s2.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Calculates the similarity between two strings (0.0 - 1.0)
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The similarity score
     */
    public static double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        if (s1.equals(s2)) return 1;
        if (s1.isEmpty() || s2.isEmpty()) return 0;
        
        int distance = levenshteinDistance(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        return 1.0 - ((double) distance / maxLen);
    }
    
    /**
     * Calculates the Jaro-Winkler distance between two strings
     * 
     * @param s1 First string
     * @param s2 Second string
     * @return The Jaro-Winkler distance
     */
    public static double jaroWinklerDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        if (s1.equals(s2)) return 1;
        
        int len1 = s1.length();
        int len2 = s2.length();
        if (len1 == 0 || len2 == 0) return 0;
        
        int searchRange = Math.max(0, Math.max(len1, len2) / 2 - 1);
        boolean[] matched1 = new boolean[len1];
        boolean[] matched2 = new boolean[len2];
        
        int matches = 0;
        for (int i = 0; i < len1; i++) {
            int start = Math.max(0, i - searchRange);
            int end = Math.min(len2, i + searchRange + 1);
            for (int j = start; j < end; j++) {
                if (matched2[j]) continue;
                if (s1.charAt(i) != s2.charAt(j)) continue;
                matched1[i] = true;
                matched2[j] = true;
                matches++;
                break;
            }
        }
        
        if (matches == 0) return 0;
        
        int transpositions = 0;
        int k = 0;
        for (int i = 0; i < len1; i++) {
            if (!matched1[i]) continue;
            while (!matched2[k]) k++;
            if (s1.charAt(i) != s2.charAt(k)) transpositions++;
            k++;
        }
        
        double jaro = ((double) matches / len1 + 
                      (double) matches / len2 + 
                      (double) (matches - transpositions / 2) / matches) / 3;
        
        // Jaro-Winkler boost for common prefix
        int prefixLength = 0;
        int maxPrefix = Math.min(4, Math.min(len1, len2));
        while (prefixLength < maxPrefix && 
               s1.charAt(prefixLength) == s2.charAt(prefixLength)) {
            prefixLength++;
        }
        
        return jaro + prefixLength * 0.1 * (1 - jaro);
    }
    
    // ============================================================
    // PATTERN MATCHING
    // ============================================================
    
    /**
     * Checks if a string matches a regex pattern
     * 
     * @param str The string
     * @param regex The regex pattern
     * @return true if matches
     */
    public static boolean matches(String str, String regex) {
        if (str == null || regex == null) return false;
        return str.matches(regex);
    }
    
    /**
     * Finds all matches of a regex pattern in a string
     * 
     * @param str The string
     * @param regex The regex pattern
     * @return List of matches
     */
    public static List<String> findAllMatches(String str, String regex) {
        List<String> matches = new ArrayList<>();
        if (str == null || regex == null) return matches;
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }
    
    /**
     * Extracts groups from a regex pattern match
     * 
     * @param str The string
     * @param regex The regex pattern
     * @param group The group index
     * @return The extracted group, or null if not found
     */
    public static String extractGroup(String str, String regex, int group) {
        if (str == null || regex == null) return null;
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return matcher.group(group);
        }
        return null;
    }
    
    /**
     * Replaces all occurrences of a regex pattern with a replacement
     * 
     * @param str The string
     * @param regex The regex pattern
     * @param replacement The replacement
     * @return The replaced string
     */
    public static String replacePattern(String str, String regex, String replacement) {
        if (str == null || regex == null) return str;
        return str.replaceAll(regex, replacement);
    }
    
    /**
     * Checks if a string is a valid email address
     * 
     * @param email The email to validate
     * @return true if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                           "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Checks if a string is a valid URL
     * 
     * @param url The URL to validate
     * @return true if valid
     */
    public static boolean isValidUrl(String url) {
        if (url == null) return false;
        String urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        return url.matches(urlRegex);
    }
    
    /**
     * Checks if a string is a valid phone number (US format)
     * 
     * @param phone The phone number
     * @return true if valid
     */
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        String phoneRegex = "^\\(?([0-9]{3})\\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})$";
        return phone.matches(phoneRegex);
    }
    
    /**
     * Checks if a string is a valid UUID
     * 
     * @param uuid The UUID
     * @return true if valid
     */
    public static boolean isValidUUID(String uuid) {
        if (uuid == null) return false;
        String uuidRegex = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
        return uuid.matches(uuidRegex);
    }
    
    // ============================================================
    // STRING TOKENIZATION
    // ============================================================
    
    /**
     * Splits a string by a delimiter
     * 
     * @param str The string
     * @param delimiter The delimiter
     * @return Array of parts
     */
    public static String[] split(String str, String delimiter) {
        if (str == null) return null;
        if (delimiter == null) return new String[]{str};
        return str.split(Pattern.quote(delimiter));
    }
    
    /**
     * Splits a string by a delimiter, preserving empty parts
     * 
     * @param str The string
     * @param delimiter The delimiter
     * @return Array of parts
     */
    public static String[] splitPreserveEmpty(String str, String delimiter) {
        if (str == null) return null;
        if (delimiter == null) return new String[]{str};
        return str.split(Pattern.quote(delimiter), -1);
    }
    
    /**
     * Splits a string by a delimiter and returns a list
     * 
     * @param str The string
     * @param delimiter The delimiter
     * @return List of parts
     */
    public static List<String> splitToList(String str, String delimiter) {
        String[] parts = split(str, delimiter);
        return parts != null ? Arrays.asList(parts) : new ArrayList<>();
    }
    
    /**
     * Joins strings with a delimiter
     * 
     * @param delimiter The delimiter
     * @param parts The strings to join
     * @return The joined string
     */
    public static String join(String delimiter, String... parts) {
        if (parts == null || parts.length == 0) return "";
        StringBuilder sb = new StringBuilder(parts[0] != null ? parts[0] : "");
        for (int i = 1; i < parts.length; i++) {
            if (delimiter != null) sb.append(delimiter);
            sb.append(parts[i] != null ? parts[i] : "");
        }
        return sb.toString();
    }
    
    /**
     * Joins strings with a delimiter
     * 
     * @param delimiter The delimiter
     * @param parts The strings to join
     * @return The joined string
     */
    public static String join(String delimiter, Collection<String> parts) {
        if (parts == null || parts.isEmpty()) return "";
        return String.join(delimiter, parts);
    }
    
    /**
     * Tokenizes a string, respecting quotes
     * 
     * @param str The string to tokenize
     * @return List of tokens
     */
    public static List<String> tokenize(String str) {
        List<String> tokens = new ArrayList<>();
        if (str == null) return tokens;
        
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean escaped = false;
        
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            
            if (escaped) {
                current.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                continue;
            }
            
            if (c == '\'' && !inDouble) {
                inSingle = !inSingle;
                continue;
            }
            
            if (c == '"' && !inSingle) {
                inDouble = !inDouble;
                continue;
            }
            
            if (!inSingle && !inDouble && Character.isWhitespace(c)) {
                if (current.length() > 0) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
                continue;
            }
            
            current.append(c);
        }
        
        if (current.length() > 0) {
            tokens.add(current.toString());
        }
        
        return tokens;
    }
    
    // ============================================================
    // CHARACTER UTILITIES
    // ============================================================
    
    /**
     * Counts the occurrences of a character in a string
     * 
     * @param str The string
     * @param ch The character
     * @return The count
     */
    public static int countChar(String str, char ch) {
        if (str == null) return 0;
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) count++;
        }
        return count;
    }
    
    /**
     * Counts the occurrences of a substring in a string
     * 
     * @param str The string
     * @param substring The substring
     * @return The count
     */
    public static int countSubstring(String str, String substring) {
        if (str == null || substring == null || substring.isEmpty()) return 0;
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
    
    /**
     * Checks if a string contains only digits
     * 
     * @param str The string
     * @return true if only digits
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
    
    /**
     * Checks if a string contains only letters
     * 
     * @param str The string
     * @return true if only letters
     */
    public static boolean isAlpha(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isLetter(c)) return false;
        }
        return true;
    }
    
    /**
     * Checks if a string contains only letters and digits
     * 
     * @param str The string
     * @return true if only alphanumeric
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }
    
    /**
     * Checks if a string contains only ASCII characters
     * 
     * @param str The string
     * @return true if ASCII only
     */
    public static boolean isAscii(String str) {
        if (str == null || str.isEmpty()) return true;
        for (char c : str.toCharArray()) {
            if (c > 127) return false;
        }
        return true;
    }
    
    // ============================================================
    // STRING FORMATTING
    // ============================================================
    
    /**
     * Formats a string with arguments (like printf)
     * 
     * @param format The format string
     * @param args The arguments
     * @return The formatted string
     */
    public static String format(String format, Object... args) {
        if (format == null) return null;
        return String.format(format, args);
    }
    
    /**
     * Formats a string with line breaks and indentation
     * 
     * @param str The string
     * @param indent The indentation level
     * @return The formatted string
     */
    public static String indent(String str, int indent) {
        if (str == null) return null;
        String spaces = " ".repeat(indent);
        String[] lines = str.split("\n");
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(spaces).append(line).append("\n");
        }
        return result.toString();
    }
    
    /**
     * Wraps text to a specified width
     * 
     * @param text The text to wrap
     * @param width The maximum width
     * @return The wrapped text
     */
    public static String wrapText(String text, int width) {
        if (text == null) return null;
        if (width <= 0) return text;
        
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        int lineLength = 0;
        
        for (String word : words) {
            if (lineLength + word.length() + 1 > width) {
                result.append("\n");
                lineLength = 0;
            }
            if (lineLength > 0) {
                result.append(" ");
                lineLength++;
            }
            result.append(word);
            lineLength += word.length();
        }
        
        return result.toString();
    }
    
    // ============================================================
    // HTML/XML ESCAPING
    // ============================================================
    
    /**
     * Escapes HTML special characters
     * 
     * @param str The string
     * @return The escaped string
     */
    public static String escapeHtml(String str) {
        if (str == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            String entity = HTML_ENTITIES.get(String.valueOf(c));
            sb.append(entity != null ? entity : c);
        }
        return sb.toString();
    }
    
    /**
     * Unescapes HTML entities
     * 
     * @param str The string
     * @return The unescaped string
     */
    public static String unescapeHtml(String str) {
        if (str == null) return null;
        String result = str;
        for (Map.Entry<String, String> entry : HTML_ENTITIES_REVERSE.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
    
    /**
     * Escapes XML special characters
     * 
     * @param str The string
     * @return The escaped string
     */
    public static String escapeXml(String str) {
        if (str == null) return null;
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
    
    /**
     * Escapes JSON special characters
     * 
     * @param str The string
     * @return The escaped string
     */
    public static String escapeJson(String str) {
        if (str == null) return null;
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f");
    }
    
    // ============================================================
    // BASE64 ENCODING/DECODING
    // ============================================================
    
    /**
     * Encodes a string to Base64
     * 
     * @param str The string to encode
     * @return The Base64 encoded string
     */
    public static String encodeBase64(String str) {
        if (str == null) return null;
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Decodes a Base64 string
     * 
     * @param str The Base64 string
     * @return The decoded string
     */
    public static String decodeBase64(String str) {
        if (str == null) return null;
        return new String(Base64.getDecoder().decode(str), StandardCharsets.UTF_8);
    }
    
    /**
     * URL-safe Base64 encoding
     * 
     * @param str The string to encode
     * @return The URL-safe Base64 encoded string
     */
    public static String encodeBase64Url(String str) {
        if (str == null) return null;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(
            str.getBytes(StandardCharsets.UTF_8)
        );
    }
    
    /**
     * URL-safe Base64 decoding
     * 
     * @param str The URL-safe Base64 string
     * @return The decoded string
     */
    public static String decodeBase64Url(String str) {
        if (str == null) return null;
        return new String(Base64.getUrlDecoder().decode(str), StandardCharsets.UTF_8);
    }
    
    // ============================================================
    // UUID GENERATION
    // ============================================================
    
    /**
     * Generates a random UUID
     * 
     * @return A UUID string
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a short UUID (without dashes)
     * 
     * @return A short UUID string
     */
    public static String generateShortUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * Generates a compact UUID (timestamp-based)
     * 
     * @return A compact UUID
     */
    public static String generateCompactUUID() {
        return String.format("%d%06d", System.currentTimeMillis(), 
                           new Random().nextInt(1000000));
    }
    
    // ============================================================
    // SLUG GENERATION
    // ============================================================
    
    /**
     * Generates a URL-friendly slug from a string
     * 
     * @param str The string
     * @return The slug
     */
    public static String slugify(String str) {
        if (str == null) return null;
        if (str.isEmpty()) return "";
        
        String slug = str.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .trim()
            .replaceAll("[\\s-]+", "-");
        
        return slug;
    }
    
    /**
     * Generates a slug with a random suffix
     * 
     * @param str The string
     * @param suffixLength The length of the random suffix
     * @return The slug with suffix
     */
    public static String slugifyWithSuffix(String str, int suffixLength) {
        String slug = slugify(str);
        if (slug.isEmpty()) {
            slug = "untitled";
        }
        return slug + "-" + randomAlphanumeric(suffixLength).toLowerCase();
    }
    
    // ============================================================
    // WORD MANIPULATION
    // ============================================================
    
    /**
     * Pluralizes a word (basic English rules)
     * 
     * @param word The word
     * @return The plural form
     */
    public static String pluralize(String word) {
        if (word == null || word.isEmpty()) return word;
        
        // Special cases
        if (word.endsWith("y") && !word.endsWith("ay") && 
            !word.endsWith("ey") && !word.endsWith("iy") &&
            !word.endsWith("oy") && !word.endsWith("uy")) {
            return word.substring(0, word.length() - 1) + "ies";
        }
        if (word.endsWith("s") || word.endsWith("x") || 
            word.endsWith("z") || word.endsWith("ch") || word.endsWith("sh")) {
            return word + "es";
        }
        return word + "s";
    }
    
    /**
     * Singularizes a word (basic English rules)
     * 
     * @param word The word
     * @return The singular form
     */
    public static String singularize(String word) {
        if (word == null || word.isEmpty()) return word;
        
        if (word.endsWith("ies") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("ses") || word.endsWith("xes") || 
            word.endsWith("zes") || word.endsWith("ches") || word.endsWith("shes")) {
            return word.substring(0, word.length() - 1);
        }
        if (word.endsWith("s") && !word.endsWith("ss")) {
            return word.substring(0, word.length() - 1);
        }
        return word;
    }
    
    /**
     * Reverses words in a string
     * 
     * @param str The string
     * @return The string with words reversed
     */
    public static String reverseWords(String str) {
        if (str == null) return null;
        String[] words = str.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = words.length - 1; i >= 0; i--) {
            if (result.length() > 0) result.append(" ");
            result.append(words[i]);
        }
        return result.toString();
    }
    
    // ============================================================
    // RANDOM STRING GENERATION
    // ============================================================
    
    /**
     * Generates a random alphanumeric string
     * 
     * @param length The length
     * @return The random string
     */
    public static String randomAlphanumeric(int length) {
        return randomString(length, ALPHANUMERIC);
    }
    
    /**
     * Generates a random alphabetic string
     * 
     * @param length The length
     * @return The random string
     */
    public static String randomAlphabetic(int length) {
        return randomString(length, ALPHABET);
    }
    
    /**
     * Generates a random numeric string
     * 
     * @param length The length
     * @return The random string
     */
    public static String randomNumeric(int length) {
        return randomString(length, DIGITS);
    }
    
    /**
     * Generates a random string from a character set
     * 
     * @param length The length
     * @param chars The character set
     * @return The random string
     */
    public static String randomString(int length, String chars) {
        if (length <= 0 || chars == null || chars.isEmpty()) return "";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    // ============================================================
    // DIACRITIC REMOVAL
    // ============================================================
    
    /**
     * Removes diacritics (accents) from a string
     * 
     * @param str The string
     * @return The string without diacritics
     */
    public static String removeDiacritics(String str) {
        if (str == null) return null;
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }
    
    // ============================================================
    // UTILITY METHODS
    // ============================================================
    
    /**
     * Repeats a string n times
     * 
     * @param str The string to repeat
     * @param count The number of times
     * @return The repeated string
     */
    public static String repeat(String str, int count) {
        if (str == null) return null;
        if (count <= 0) return "";
        return str.repeat(count);
    }
    
    /**
     * Repeats a character n times
     * 
     * @param ch The character
     * @param count The number of times
     * @return The repeated string
     */
    public static String repeat(char ch, int count) {
        if (count <= 0) return "";
        return String.valueOf(ch).repeat(count);
    }
    
    /**
     * Returns the default string if the input is empty
     * 
     * @param str The string
     * @param defaultStr The default string
     * @return The string or default if empty
     */
    public static String defaultIfEmpty(String str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str;
    }
    
    /**
     * Returns the default string if the input is blank
     * 
     * @param str The string
     * @param defaultStr The default string
     * @return The string or default if blank
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }
    
    /**
     * Removes diacritics (accents) from a string
     * 
     * @param str The string
     * @return The string without diacritics
     */
    public static String removeAccents(String str) {
        return removeDiacritics(str);
    }
    
    /**
     * Swaps the case of a string
     * 
     * @param str The string
     * @return The case-swapped string
     */
    public static String swapCase(String str) {
        if (str == null) return null;
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isUpperCase(chars[i])) {
                chars[i] = Character.toLowerCase(chars[i]);
            } else if (Character.isLowerCase(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }
        return new String(chars);
    }
    
    /**
     * Generates a string of n spaces
     * 
     * @param n The number of spaces
     * @return The spaces string
     */
    public static String spaces(int n) {
        return repeat(' ', n);
    }
    
    /**
     * Generates a string of n tabs
     * 
     * @param n The number of tabs
     * @return The tabs string
     */
    public static String tabs(int n) {
        return repeat('\t', n);
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of StringUtils
     */
    public static void main(String[] args) {
        System.out.println("📝 StringUtils Demo");
        System.out.println("═".repeat(60));
        
        // Empty checks
        System.out.println("\n📌 Empty/Blank Checks:");
        System.out.println("  isEmpty(null): " + isEmpty(null));
        System.out.println("  isEmpty(''): " + isEmpty(""));
        System.out.println("  isBlank('   '): " + isBlank("   "));
        System.out.println("  isNotBlank('hello'): " + isNotBlank("hello"));
        
        // Manipulation
        System.out.println("\n📌 Manipulation:");
        String test = "  Hello World  ";
        System.out.println("  trim('" + test + "'): '" + trim(test) + "'");
        System.out.println("  padLeft('hello', 10): '" + padLeft("hello", 10) + "'");
        System.out.println("  padRight('hello', 10): '" + padRight("hello", 10) + "'");
        System.out.println("  padCenter('hello', 10): '" + padCenter("hello", 10) + "'");
        System.out.println("  truncate('Hello World', 8): '" + truncate("Hello World", 8) + "'");
        System.out.println("  reverse('hello'): '" + reverse("hello") + "'");
        
        // Case conversion
        System.out.println("\n📌 Case Conversion:");
        String mixed = "hello_world_test";
        System.out.println("  toCamelCase('" + mixed + "'): '" + toCamelCase(mixed) + "'");
        System.out.println("  toPascalCase('" + mixed + "'): '" + toPascalCase(mixed) + "'");
        System.out.println("  toSnakeCase('HelloWorld'): '" + toSnakeCase("HelloWorld") + "'");
        System.out.println("  toKebabCase('HelloWorld'): '" + toKebabCase("HelloWorld") + "'");
        System.out.println("  toTitleCase('hello world'): '" + toTitleCase("hello world") + "'");
        System.out.println("  capitalize('hello'): '" + capitalize("hello") + "'");
        
        // Substring operations
        System.out.println("\n📌 Substring Operations:");
        String data = "file.txt";
        System.out.println("  substringBefore('" + data + "', '.'): '" + substringBefore(data, ".") + "'");
        System.out.println("  substringAfter('" + data + "', '.'): '" + substringAfter(data, ".") + "'");
        System.out.println("  left('hello', 3): '" + left("hello", 3) + "'");
        System.out.println("  right('hello', 3): '" + right("hello", 3) + "'");
        System.out.println("  mid('hello', 1, 3): '" + mid("hello", 1, 3) + "'");
        
        // Comparison
        System.out.println("\n📌 Comparison:");
        System.out.println("  equalsIgnoreCase('Hello', 'hello'): " + equalsIgnoreCase("Hello", "hello"));
        System.out.println("  containsIgnoreCase('Hello World', 'world'): " + containsIgnoreCase("Hello World", "world"));
        System.out.println("  Levenshtein distance('kitten', 'sitting'): " + levenshteinDistance("kitten", "sitting"));
        System.out.println("  Similarity('kitten', 'sitting'): " + similarity("kitten", "sitting"));
        System.out.println("  Jaro-Winkler('kitten', 'sitting'): " + jaroWinklerDistance("kitten", "sitting"));
        
        // Pattern matching
        System.out.println("\n📌 Pattern Matching:");
        System.out.println("  isValidEmail('test@example.com'): " + isValidEmail("test@example.com"));
        System.out.println("  isValidUrl('https://example.com'): " + isValidUrl("https://example.com"));
        System.out.println("  isValidPhoneNumber('(123) 456-7890'): " + isValidPhoneNumber("(123) 456-7890"));
        System.out.println("  isValidUUID('123e4567-e89b-12d3-a456-426614174000'): " + 
                          isValidUUID("123e4567-e89b-12d3-a456-426614174000"));
        
        // Tokenization
        System.out.println("\n📌 Tokenization:");
        String toTokenize = "hello world 'quoted string' \"double quoted\"";
        System.out.println("  tokenize('" + toTokenize + "'): " + tokenize(toTokenize));
        
        // Random generation
        System.out.println("\n📌 Random Generation:");
        System.out.println("  randomAlphanumeric(10): " + randomAlphanumeric(10));
        System.out.println("  randomAlphabetic(10): " + randomAlphabetic(10));
        System.out.println("  randomNumeric(10): " + randomNumeric(10));
        System.out.println("  generateUUID(): " + generateUUID());
        System.out.println("  generateShortUUID(): " + generateShortUUID());
        
        // Slug generation
        System.out.println("\n📌 Slug Generation:");
        String slugInput = "Hello World! This is a test.";
        System.out.println("  slugify('" + slugInput + "'): '" + slugify(slugInput) + "'");
        
        // HTML escaping
        System.out.println("\n📌 HTML Escaping:");
        String htmlInput = "<script>alert('Hello')</script>";
        System.out.println("  escapeHtml('" + htmlInput + "'): " + escapeHtml(htmlInput));
        
        // Base64
        System.out.println("\n📌 Base64:");
        String base64Input = "Hello World";
        String encoded = encodeBase64(base64Input);
        System.out.println("  encodeBase64('" + base64Input + "'): " + encoded);
        System.out.println("  decodeBase64('" + encoded + "'): " + decodeBase64(encoded));
        
        // Word manipulation
        System.out.println("\n📌 Word Manipulation:");
        System.out.println("  pluralize('cat'): " + pluralize("cat"));
        System.out.println("  pluralize('city'): " + pluralize("city"));
        System.out.println("  singularize('cats'): " + singularize("cats"));
        System.out.println("  singularize('cities'): " + singularize("cities"));
        System.out.println("  reverseWords('Hello World'): " + reverseWords("Hello World"));
        
        // Utility
        System.out.println("\n📌 Utility:");
        System.out.println("  repeat('*', 10): " + repeat('*', 10));
        System.out.println("  swapCase('Hello World'): " + swapCase("Hello World"));
        System.out.println("  removeAccents('café'): " + removeAccents("café"));
        System.out.println("  defaultIfEmpty('', 'default'): " + defaultIfEmpty("", "default"));
        
        System.out.println("\n✅ Demo completed!");
    }
}