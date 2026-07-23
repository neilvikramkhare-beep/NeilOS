package com.neilos.utils;

import java.text.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DateUtils - Comprehensive date and time utility class for NeilOS
 * Provides extensive date/time manipulation, formatting, parsing,
 * and calculation utilities.
 * 
 * Features:
 * - Date formatting and parsing (multiple formats)
 * - Time calculations (difference, addition, subtraction)
 * - Date comparisons
 * - Timezone handling
 * - Relative time display (e.g., "2 hours ago")
 * - Date ranges and intervals
 * - Business day calculations
 * - Age calculation
 * - ISO 8601 support
 * - Date validation
 * - Calendar operations
 * - Natural language date parsing
 * 
 * @author NeilOS Team
 * @version 1.0.0
 */
public class DateUtils {
    
    // ============================================================
    // CONSTANTS
    // ============================================================
    
    /** Standard date format patterns */
    public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DISPLAY = "MMM dd, yyyy";
    public static final String DATE_FORMAT_LONG = "EEEE, MMMM dd, yyyy";
    public static final String DATE_FORMAT_SHORT = "MM/dd/yy";
    public static final String DATE_FORMAT_US = "MM/dd/yyyy";
    public static final String DATE_FORMAT_EUROPE = "dd/MM/yyyy";
    public static final String DATE_FORMAT_ASIA = "yyyy/MM/dd";
    public static final String DATE_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMAT_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss z";
    
    /** Time format patterns */
    public static final String TIME_FORMAT_12H = "hh:mm:ss a";
    public static final String TIME_FORMAT_24H = "HH:mm:ss";
    public static final String TIME_FORMAT_SHORT_12H = "hh:mm a";
    public static final String TIME_FORMAT_SHORT_24H = "HH:mm";
    
    /** Combined date/time formats */
    public static final String DATETIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_DISPLAY = "MMM dd, yyyy HH:mm:ss";
    public static final String DATETIME_FORMAT_ISO = "yyyy-MM-dd'T'HH:mm:ssXXX";
    
    /** Time units in milliseconds */
    public static final long MILLIS_PER_SECOND = 1000;
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;
    
    /** Calendar field constants (for easy access) */
    public static final int SECOND = Calendar.SECOND;
    public static final int MINUTE = Calendar.MINUTE;
    public static final int HOUR = Calendar.HOUR;
    public static final int HOUR_OF_DAY = Calendar.HOUR_OF_DAY;
    public static final int DAY = Calendar.DAY_OF_MONTH;
    public static final int WEEK = Calendar.WEEK_OF_YEAR;
    public static final int MONTH = Calendar.MONTH;
    public static final int YEAR = Calendar.YEAR;
    
    // ============================================================
    // DATE CREATION
    // ============================================================
    
    /**
     * Creates a date from year, month, day
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day (1-31)
     * @return Date object
     */
    public static Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Creates a date from year, month, day, hour, minute
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day (1-31)
     * @param hour The hour (0-23)
     * @param minute The minute (0-59)
     * @return Date object
     */
    public static Date createDateTime(int year, int month, int day, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Creates a date from year, month, day, hour, minute, second
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day (1-31)
     * @param hour The hour (0-23)
     * @param minute The minute (0-59)
     * @param second The second (0-59)
     * @return Date object
     */
    public static Date createDateTime(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, hour, minute, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Creates a LocalDate from year, month, day
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day (1-31)
     * @return LocalDate object
     */
    public static LocalDate createLocalDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }
    
    /**
     * Creates a LocalDateTime from year, month, day, hour, minute
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param day The day (1-31)
     * @param hour The hour (0-23)
     * @param minute The minute (0-59)
     * @return LocalDateTime object
     */
    public static LocalDateTime createLocalDateTime(int year, int month, int day, int hour, int minute) {
        return LocalDateTime.of(year, month, day, hour, minute);
    }
    
    // ============================================================
    // CURRENT DATE/TIME
    // ============================================================
    
    /**
     * Gets the current date
     * 
     * @return Current date (time set to 00:00:00)
     */
    public static Date getCurrentDate() {
        return truncateTime(new Date());
    }
    
    /**
     * Gets the current date and time
     * 
     * @return Current date and time
     */
    public static Date getCurrentDateTime() {
        return new Date();
    }
    
    /**
     * Gets the current LocalDate
     * 
     * @return Current LocalDate
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }
    
    /**
     * Gets the current LocalDateTime
     * 
     * @return Current LocalDateTime
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * Gets the current timestamp in milliseconds
     * 
     * @return Current timestamp
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * Gets the current Unix timestamp (seconds)
     * 
     * @return Current Unix timestamp
     */
    public static long getCurrentUnixTimestamp() {
        return System.currentTimeMillis() / MILLIS_PER_SECOND;
    }
    
    // ============================================================
    // DATE FORMATTING
    // ============================================================
    
    /**
     * Formats a date using the default format
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        return formatDate(date, DATE_FORMAT_DEFAULT);
    }
    
    /**
     * Formats a date using a custom pattern
     * 
     * @param date The date to format
     * @param pattern The format pattern
     * @return Formatted date string
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
    
    /**
     * Formats a LocalDate using the default format
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatLocalDate(LocalDate date) {
        return formatLocalDate(date, DATE_FORMAT_DEFAULT);
    }
    
    /**
     * Formats a LocalDate using a custom pattern
     * 
     * @param date The date to format
     * @param pattern The format pattern
     * @return Formatted date string
     */
    public static String formatLocalDate(LocalDate date, String pattern) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
    
    /**
     * Formats a LocalDateTime using the default format
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatLocalDateTime(LocalDateTime date) {
        return formatLocalDateTime(date, DATETIME_FORMAT_DEFAULT);
    }
    
    /**
     * Formats a LocalDateTime using a custom pattern
     * 
     * @param date The date to format
     * @param pattern The format pattern
     * @return Formatted date string
     */
    public static String formatLocalDateTime(LocalDateTime date, String pattern) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return date.format(formatter);
    }
    
    /**
     * Formats a date for display (e.g., "Jan 15, 2024")
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatDisplay(Date date) {
        return formatDate(date, DATE_FORMAT_DISPLAY);
    }
    
    /**
     * Formats a date in long format (e.g., "Monday, January 15, 2024")
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public static String formatLong(Date date) {
        return formatDate(date, DATE_FORMAT_LONG);
    }
    
    /**
     * Formats a time using 24-hour format
     * 
     * @param date The date containing the time
     * @return Formatted time string
     */
    public static String formatTime24(Date date) {
        return formatDate(date, TIME_FORMAT_24H);
    }
    
    /**
     * Formats a time using 12-hour format with AM/PM
     * 
     * @param date The date containing the time
     * @return Formatted time string
     */
    public static String formatTime12(Date date) {
        return formatDate(date, TIME_FORMAT_12H);
    }
    
    /**
     * Formats a date in ISO 8601 format
     * 
     * @param date The date to format
     * @return ISO 8601 formatted string
     */
    public static String formatISO(Date date) {
        return formatDate(date, DATE_FORMAT_ISO);
    }
    
    /**
     * Formats a date in RFC 1123 format (HTTP date)
     * 
     * @param date The date to format
     * @return RFC 1123 formatted string
     */
    public static String formatRFC1123(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_RFC1123, Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }
    
    // ============================================================
    // DATE PARSING
    // ============================================================
    
    /**
     * Parses a date string using the default format
     * 
     * @param dateStr The date string
     * @return Parsed date
     * @throws ParseException If parsing fails
     */
    public static Date parseDate(String dateStr) throws ParseException {
        return parseDate(dateStr, DATE_FORMAT_DEFAULT);
    }
    
    /**
     * Parses a date string using a custom pattern
     * 
     * @param dateStr The date string
     * @param pattern The format pattern
     * @return Parsed date
     * @throws ParseException If parsing fails
     */
    public static Date parseDate(String dateStr, String pattern) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) return null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(dateStr);
    }
    
    /**
     * Parses a date string using multiple formats (attempts each)
     * 
     * @param dateStr The date string
     * @param patterns Array of format patterns to try
     * @return Parsed date
     * @throws ParseException If all parsing attempts fail
     */
    public static Date parseDateMultiFormat(String dateStr, String[] patterns) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) return null;
        
        for (String pattern : patterns) {
            try {
                return parseDate(dateStr, pattern);
            } catch (ParseException e) {
                // Try next format
            }
        }
        throw new ParseException("Unable to parse date: " + dateStr, 0);
    }
    
    /**
     * Parses a LocalDate from a string
     * 
     * @param dateStr The date string
     * @param pattern The format pattern
     * @return Parsed LocalDate
     */
    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDate.parse(dateStr, formatter);
    }
    
    /**
     * Parses a LocalDateTime from a string
     * 
     * @param dateStr The date string
     * @param pattern The format pattern
     * @return Parsed LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(dateStr, formatter);
    }
    
    /**
     * Parses a date from ISO 8601 format
     * 
     * @param dateStr The ISO 8601 date string
     * @return Parsed date
     * @throws ParseException If parsing fails
     */
    public static Date parseISO(String dateStr) throws ParseException {
        return parseDate(dateStr, DATE_FORMAT_ISO);
    }
    
    /**
     * Parses a date from RFC 1123 format
     * 
     * @param dateStr The RFC 1123 date string
     * @return Parsed date
     * @throws ParseException If parsing fails
     */
    public static Date parseRFC1123(String dateStr) throws ParseException {
        return parseDate(dateStr, DATE_FORMAT_RFC1123);
    }
    
    /**
     * Attempts to parse a natural language date
     * 
     * @param text Natural language date (e.g., "tomorrow", "next week")
     * @return Parsed date, or null if not recognized
     */
    public static Date parseNaturalLanguage(String text) {
        if (text == null) return null;
        
        String lower = text.toLowerCase().trim();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        
        // Parse common natural language dates
        if (lower.equals("today")) {
            return truncateTime(now);
        } else if (lower.equals("tomorrow")) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("yesterday")) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("next week")) {
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("last week")) {
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("next month")) {
            cal.add(Calendar.MONTH, 1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("last month")) {
            cal.add(Calendar.MONTH, -1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("next year")) {
            cal.add(Calendar.YEAR, 1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("last year")) {
            cal.add(Calendar.YEAR, -1);
            return truncateTime(cal.getTime());
        } else if (lower.equals("now")) {
            return now;
        } else if (lower.equals("end of day")) {
            return endOfDay(now);
        } else if (lower.equals("start of day")) {
            return startOfDay(now);
        } else if (lower.equals("noon")) {
            cal.set(Calendar.HOUR_OF_DAY, 12);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal.getTime();
        } else if (lower.equals("midnight")) {
            return startOfDay(now);
        }
        
        return null;
    }
    
    // ============================================================
    // DATE CALCULATIONS
    // ============================================================
    
    /**
     * Adds days to a date
     * 
     * @param date The date
     * @param days The number of days to add (negative to subtract)
     * @return New date with days added
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }
    
    /**
     * Adds weeks to a date
     * 
     * @param date The date
     * @param weeks The number of weeks to add (negative to subtract)
     * @return New date with weeks added
     */
    public static Date addWeeks(Date date, int weeks) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, weeks);
        return cal.getTime();
    }
    
    /**
     * Adds months to a date
     * 
     * @param date The date
     * @param months The number of months to add (negative to subtract)
     * @return New date with months added
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return cal.getTime();
    }
    
    /**
     * Adds years to a date
     * 
     * @param date The date
     * @param years The number of years to add (negative to subtract)
     * @return New date with years added
     */
    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return cal.getTime();
    }
    
    /**
     * Adds hours to a date
     * 
     * @param date The date
     * @param hours The number of hours to add (negative to subtract)
     * @return New date with hours added
     */
    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hours);
        return cal.getTime();
    }
    
    /**
     * Adds minutes to a date
     * 
     * @param date The date
     * @param minutes The number of minutes to add (negative to subtract)
     * @return New date with minutes added
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes);
        return cal.getTime();
    }
    
    /**
     * Adds seconds to a date
     * 
     * @param date The date
     * @param seconds The number of seconds to add (negative to subtract)
     * @return New date with seconds added
     */
    public static Date addSeconds(Date date, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }
    
    /**
     * Adds milliseconds to a date
     * 
     * @param date The date
     * @param millis The number of milliseconds to add (negative to subtract)
     * @return New date with milliseconds added
     */
    public static Date addMillis(Date date, long millis) {
        return new Date(date.getTime() + millis);
    }
    
    // ============================================================
    // DATE TRUNCATION
    // ============================================================
    
    /**
     * Truncates a date to the start of the day (00:00:00)
     * 
     * @param date The date to truncate
     * @return Date at start of day
     */
    public static Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Truncates a date to the start of the month (day 1, 00:00:00)
     * 
     * @param date The date to truncate
     * @return Date at start of month
     */
    public static Date truncateToMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Truncates a date to the start of the year (Jan 1, 00:00:00)
     * 
     * @param date The date to truncate
     * @return Date at start of year
     */
    public static Date truncateToYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Truncates a date to the start of the week (Monday, 00:00:00)
     * 
     * @param date The date to truncate
     * @return Date at start of week
     */
    public static Date truncateToWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Truncates a date to the start of the hour (00 minutes)
     * 
     * @param date The date to truncate
     * @return Date at start of hour
     */
    public static Date truncateToHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Truncates a date to the start of the minute (00 seconds)
     * 
     * @param date The date to truncate
     * @return Date at start of minute
     */
    public static Date truncateToMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
    
    /**
     * Gets the start of day for a date
     * 
     * @param date The date
     * @return Date at start of day (00:00:00)
     */
    public static Date startOfDay(Date date) {
        return truncateTime(date);
    }
    
    /**
     * Gets the end of day for a date (23:59:59.999)
     * 
     * @param date The date
     * @return Date at end of day
     */
    public static Date endOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Gets the start of month for a date
     * 
     * @param date The date
     * @return Date at start of month
     */
    public static Date startOfMonth(Date date) {
        return truncateToMonth(date);
    }
    
    /**
     * Gets the end of month for a date
     * 
     * @param date The date
     * @return Date at end of month
     */
    public static Date endOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    /**
     * Gets the start of year for a date
     * 
     * @param date The date
     * @return Date at start of year
     */
    public static Date startOfYear(Date date) {
        return truncateToYear(date);
    }
    
    /**
     * Gets the end of year for a date
     * 
     * @param date The date
     * @return Date at end of year
     */
    public static Date endOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }
    
    // ============================================================
    // DATE COMPARISONS
    // ============================================================
    
    /**
     * Checks if two dates are on the same day
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return true if same day
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Checks if a date is today
     * 
     * @param date The date to check
     * @return true if today
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }
    
    /**
     * Checks if a date is in the past
     * 
     * @param date The date to check
     * @return true if in the past
     */
    public static boolean isPast(Date date) {
        return date != null && date.before(new Date());
    }
    
    /**
     * Checks if a date is in the future
     * 
     * @param date The date to check
     * @return true if in the future
     */
    public static boolean isFuture(Date date) {
        return date != null && date.after(new Date());
    }
    
    /**
     * Checks if a date is a weekend (Saturday or Sunday)
     * 
     * @param date The date to check
     * @return true if weekend
     */
    public static boolean isWeekend(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
    
    /**
     * Checks if a date is a weekday (Monday to Friday)
     * 
     * @param date The date to check
     * @return true if weekday
     */
    public static boolean isWeekday(Date date) {
        return !isWeekend(date);
    }
    
    /**
     * Checks if a year is a leap year
     * 
     * @param year The year to check
     * @return true if leap year
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }
    
    // ============================================================
    // DATE DIFFERENCES
    // ============================================================
    
    /**
     * Calculates the difference between two dates in milliseconds
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in milliseconds
     */
    public static long diffInMillis(Date date1, Date date2) {
        if (date1 == null || date2 == null) return 0;
        return Math.abs(date2.getTime() - date1.getTime());
    }
    
    /**
     * Calculates the difference between two dates in seconds
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in seconds
     */
    public static long diffInSeconds(Date date1, Date date2) {
        return diffInMillis(date1, date2) / MILLIS_PER_SECOND;
    }
    
    /**
     * Calculates the difference between two dates in minutes
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in minutes
     */
    public static long diffInMinutes(Date date1, Date date2) {
        return diffInMillis(date1, date2) / MILLIS_PER_MINUTE;
    }
    
    /**
     * Calculates the difference between two dates in hours
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in hours
     */
    public static long diffInHours(Date date1, Date date2) {
        return diffInMillis(date1, date2) / MILLIS_PER_HOUR;
    }
    
    /**
     * Calculates the difference between two dates in days
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in days
     */
    public static long diffInDays(Date date1, Date date2) {
        return diffInMillis(date1, date2) / MILLIS_PER_DAY;
    }
    
    /**
     * Calculates the difference between two dates in weeks
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in weeks
     */
    public static long diffInWeeks(Date date1, Date date2) {
        return diffInDays(date1, date2) / 7;
    }
    
    /**
     * Calculates the difference between two dates in months
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in months
     */
    public static int diffInMonths(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        
        int yearDiff = cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
        int monthDiff = cal2.get(Calendar.MONTH) - cal1.get(Calendar.MONTH);
        return yearDiff * 12 + monthDiff;
    }
    
    /**
     * Calculates the difference between two dates in years
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in years
     */
    public static int diffInYears(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal2.get(Calendar.YEAR) - cal1.get(Calendar.YEAR);
    }
    
    /**
     * Calculates age from a birthdate
     * 
     * @param birthdate The birthdate
     * @return Age in years
     */
    public static int calculateAge(Date birthdate) {
        if (birthdate == null) return 0;
        Date now = new Date();
        int age = diffInYears(birthdate, now);
        
        // Check if birthday has occurred this year
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthdate);
        Calendar nowCal = Calendar.getInstance();
        nowCal.setTime(now);
        
        if (nowCal.get(Calendar.MONTH) < birthCal.get(Calendar.MONTH) ||
            (nowCal.get(Calendar.MONTH) == birthCal.get(Calendar.MONTH) &&
             nowCal.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
    }
    
    // ============================================================
    // RELATIVE TIME
    // ============================================================
    
    /**
     * Gets a human-readable relative time string (e.g., "2 hours ago")
     * 
     * @param date The date to convert
     * @return Relative time string
     */
    public static String getRelativeTime(Date date) {
        if (date == null) return "Never";
        return getRelativeTime(date, new Date());
    }
    
    /**
     * Gets a human-readable relative time string between two dates
     * 
     * @param date1 First date
     * @param date2 Second date (reference date)
     * @return Relative time string
     */
    public static String getRelativeTime(Date date1, Date date2) {
        if (date1 == null || date2 == null) return "Unknown";
        
        long diff = Math.abs(date2.getTime() - date1.getTime());
        boolean isPast = date1.before(date2);
        String suffix = isPast ? " ago" : " from now";
        
        if (diff < MILLIS_PER_MINUTE) {
            long seconds = diff / MILLIS_PER_SECOND;
            return seconds + " second" + (seconds != 1 ? "s" : "") + suffix;
        }
        
        if (diff < MILLIS_PER_HOUR) {
            long minutes = diff / MILLIS_PER_MINUTE;
            return minutes + " minute" + (minutes != 1 ? "s" : "") + suffix;
        }
        
        if (diff < MILLIS_PER_DAY) {
            long hours = diff / MILLIS_PER_HOUR;
            return hours + " hour" + (hours != 1 ? "s" : "") + suffix;
        }
        
        if (diff < MILLIS_PER_WEEK) {
            long days = diff / MILLIS_PER_DAY;
            if (days == 1) return "Yesterday" + (isPast ? "" : " (tomorrow)");
            return days + " day" + (days != 1 ? "s" : "") + suffix;
        }
        
        if (diff < MILLIS_PER_WEEK * 4) {
            long weeks = diff / MILLIS_PER_WEEK;
            return weeks + " week" + (weeks != 1 ? "s" : "") + suffix;
        }
        
        if (diff < MILLIS_PER_WEEK * 52) {
            long months = diff / (MILLIS_PER_WEEK * 4);
            return months + " month" + (months != 1 ? "s" : "") + suffix;
        }
        
        long years = diff / (MILLIS_PER_WEEK * 52);
        return years + " year" + (years != 1 ? "s" : "") + suffix;
    }
    
    /**
     * Gets a very short relative time (e.g., "2h ago")
     * 
     * @param date The date to convert
     * @return Short relative time
     */
    public static String getShortRelativeTime(Date date) {
        if (date == null) return "N/A";
        
        long diff = System.currentTimeMillis() - date.getTime();
        boolean isPast = true;
        if (diff < 0) {
            diff = -diff;
            isPast = false;
        }
        
        String suffix = isPast ? " ago" : " from now";
        
        if (diff < MILLIS_PER_MINUTE) {
            return (diff / MILLIS_PER_SECOND) + "s" + suffix;
        }
        if (diff < MILLIS_PER_HOUR) {
            return (diff / MILLIS_PER_MINUTE) + "m" + suffix;
        }
        if (diff < MILLIS_PER_DAY) {
            return (diff / MILLIS_PER_HOUR) + "h" + suffix;
        }
        if (diff < MILLIS_PER_WEEK) {
            return (diff / MILLIS_PER_DAY) + "d" + suffix;
        }
        if (diff < MILLIS_PER_WEEK * 4) {
            return (diff / MILLIS_PER_WEEK) + "w" + suffix;
        }
        if (diff < MILLIS_PER_WEEK * 52) {
            return (diff / (MILLIS_PER_WEEK * 4)) + "mo" + suffix;
        }
        return (diff / (MILLIS_PER_WEEK * 52)) + "y" + suffix;
    }
    
    // ============================================================
    // DATE RANGES
    // ============================================================
    
    /**
     * Gets a list of dates between two dates (inclusive)
     * 
     * @param start The start date
     * @param end The end date
     * @return List of dates
     */
    public static List<Date> getDateRange(Date start, Date end) {
        List<Date> dates = new ArrayList<>();
        if (start == null || end == null) return dates;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date current = cal.getTime();
        Date endTruncated = truncateTime(end);
        
        while (!current.after(endTruncated)) {
            dates.add(current);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            current = cal.getTime();
        }
        
        return dates;
    }
    
    /**
     * Gets a list of months between two dates
     * 
     * @param start The start date
     * @param end The end date
     * @return List of month start dates
     */
    public static List<Date> getMonthRange(Date start, Date end) {
        List<Date> months = new ArrayList<>();
        if (start == null || end == null) return months;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date current = cal.getTime();
        Date endTruncated = truncateToMonth(end);
        
        while (!current.after(endTruncated)) {
            months.add(current);
            cal.add(Calendar.MONTH, 1);
            current = cal.getTime();
        }
        
        return months;
    }
    
    /**
     * Generates a date range with a step
     * 
     * @param start The start date
     * @param end The end date
     * @param step The step in days
     * @return List of dates
     */
    public static List<Date> getDateRangeWithStep(Date start, Date end, int step) {
        List<Date> dates = new ArrayList<>();
        if (start == null || end == null || step <= 0) return dates;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Date current = cal.getTime();
        Date endTruncated = truncateTime(end);
        
        while (!current.after(endTruncated)) {
            dates.add(current);
            cal.add(Calendar.DAY_OF_MONTH, step);
            current = cal.getTime();
        }
        
        return dates;
    }
    
    // ============================================================
    // BUSINESS DAYS
    // ============================================================
    
    /**
     * Calculates the number of business days between two dates
     * 
     * @param start The start date
     * @param end The end date
     * @param holidays Optional holiday list
     * @return Number of business days
     */
    public static int getBusinessDays(Date start, Date end, Date... holidays) {
        if (start == null || end == null) return 0;
        
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.HOUR_OF_DAY, 0);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);
        
        Set<Date> holidaySet = new HashSet<>();
        if (holidays != null) {
            for (Date h : holidays) {
                if (h != null) holidaySet.add(truncateTime(h));
            }
        }
        
        int businessDays = 0;
        Date current = startCal.getTime();
        
        while (!current.after(endCal.getTime())) {
            if (!isWeekend(current) && !holidaySet.contains(current)) {
                businessDays++;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
            current = startCal.getTime();
        }
        
        return businessDays;
    }
    
    /**
     * Adds business days to a date (skipping weekends and holidays)
     * 
     * @param date The start date
     * @param days The number of business days to add
     * @param holidays Optional holiday list
     * @return New date after adding business days
     */
    public static Date addBusinessDays(Date date, int days, Date... holidays) {
        if (date == null || days == 0) return date;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        Set<Date> holidaySet = new HashSet<>();
        if (holidays != null) {
            for (Date h : holidays) {
                if (h != null) holidaySet.add(truncateTime(h));
            }
        }
        
        int added = 0;
        int direction = days > 0 ? 1 : -1;
        int target = Math.abs(days);
        
        while (added < target) {
            cal.add(Calendar.DAY_OF_MONTH, direction);
            Date current = cal.getTime();
            if (!isWeekend(current) && !holidaySet.contains(current)) {
                added++;
            }
        }
        
        return cal.getTime();
    }
    
    // ============================================================
    // TIMEZONE UTILITIES
    // ============================================================
    
    /**
     * Converts a date to a different timezone
     * 
     * @param date The date to convert
     * @param fromZone The source timezone
     * @param toZone The target timezone
     * @return Converted date
     */
    public static Date convertTimezone(Date date, TimeZone fromZone, TimeZone toZone) {
        if (date == null || fromZone == null || toZone == null) return date;
        
        long fromOffset = fromZone.getOffset(date.getTime());
        long toOffset = toZone.getOffset(date.getTime());
        long diff = toOffset - fromOffset;
        return new Date(date.getTime() + diff);
    }
    
    /**
     * Gets the current timezone
     * 
     * @return Current timezone
     */
    public static TimeZone getCurrentTimezone() {
        return TimeZone.getDefault();
    }
    
    /**
     * Gets all available timezone IDs
     * 
     * @return Array of timezone IDs
     */
    public static String[] getAvailableTimezoneIDs() {
        return TimeZone.getAvailableIDs();
    }
    
    /**
     * Gets the timezone offset for a date
     * 
     * @param date The date
     * @param timezone The timezone
     * @return Offset in milliseconds
     */
    public static int getTimezoneOffset(Date date, TimeZone timezone) {
        if (date == null || timezone == null) return 0;
        return timezone.getOffset(date.getTime());
    }
    
    // ============================================================
    // DATE VALIDATION
    // ============================================================
    
    /**
     * Validates a date string
     * 
     * @param dateStr The date string
     * @param pattern The format pattern
     * @return true if valid
     */
    public static boolean isValidDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        try {
            parseDate(dateStr, pattern);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Validates a date string against multiple formats
     * 
     * @param dateStr The date string
     * @param patterns Array of format patterns
     * @return true if valid
     */
    public static boolean isValidDateMultiFormat(String dateStr, String[] patterns) {
        if (dateStr == null || dateStr.isEmpty()) return false;
        for (String pattern : patterns) {
            if (isValidDate(dateStr, pattern)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validates a date range (start before end)
     * 
     * @param start The start date
     * @param end The end date
     * @return true if valid range
     */
    public static boolean isValidDateRange(Date start, Date end) {
        return start != null && end != null && !start.after(end);
    }
    
    // ============================================================
    // DATE COMPONENTS
    // ============================================================
    
    /**
     * Gets the year from a date
     * 
     * @param date The date
     * @return The year
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * Gets the month from a date (1-12)
     * 
     * @param date The date
     * @return The month
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }
    
    /**
     * Gets the month name from a date
     * 
     * @param date The date
     * @return The month name
     */
    public static String getMonthName(Date date) {
        return formatDate(date, "MMMM");
    }
    
    /**
     * Gets the month short name from a date
     * 
     * @param date The date
     * @return The month short name
     */
    public static String getMonthShortName(Date date) {
        return formatDate(date, "MMM");
    }
    
    /**
     * Gets the day of month from a date (1-31)
     * 
     * @param date The date
     * @return The day
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Gets the day of week from a date (1-7, Sunday=1)
     * 
     * @param date The date
     * @return The day of week
     */
    public static int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    /**
     * Gets the day of week name from a date
     * 
     * @param date The date
     * @return The day of week name
     */
    public static String getDayOfWeekName(Date date) {
        return formatDate(date, "EEEE");
    }
    
    /**
     * Gets the day of week short name from a date
     * 
     * @param date The date
     * @return The day of week short name
     */
    public static String getDayOfWeekShortName(Date date) {
        return formatDate(date, "EEE");
    }
    
    /**
     * Gets the hour from a date (0-23)
     * 
     * @param date The date
     * @return The hour
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * Gets the minute from a date (0-59)
     * 
     * @param date The date
     * @return The minute
     */
    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
    
    /**
     * Gets the second from a date (0-59)
     * 
     * @param date The date
     * @return The second
     */
    public static int getSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }
    
    /**
     * Gets the day of year from a date (1-366)
     * 
     * @param date The date
     * @return The day of year
     */
    public static int getDayOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * Gets the week of year from a date (1-52)
     * 
     * @param date The date
     * @return The week of year
     */
    public static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
    
    /**
     * Gets the quarter from a date (1-4)
     * 
     * @param date The date
     * @return The quarter
     */
    public static int getQuarter(Date date) {
        int month = getMonth(date);
        return (month - 1) / 3 + 1;
    }
    
    // ============================================================
    // DATE FORMAT UTILITIES
    // ============================================================
    
    /**
     * Gets a human-readable date difference
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Human-readable difference
     */
    public static String getHumanReadableDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) return "";
        
        long diff = Math.abs(date2.getTime() - date1.getTime());
        long days = diff / MILLIS_PER_DAY;
        long hours = (diff % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
        long minutes = (diff % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;
        long seconds = (diff % MILLIS_PER_MINUTE) / MILLIS_PER_SECOND;
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0) sb.append(seconds).append("s");
        if (sb.length() == 0) sb.append("0s");
        
        return sb.toString().trim();
    }
    
    /**
     * Checks if a date is within a range
     * 
     * @param date The date to check
     * @param start The start of range
     * @param end The end of range
     * @return true if date is within range
     */
    public static boolean isWithinRange(Date date, Date start, Date end) {
        if (date == null) return false;
        if (start != null && date.before(start)) return false;
        if (end != null && date.after(end)) return false;
        return true;
    }
    
    /**
     * Gets the first day of the month
     * 
     * @param date The date
     * @return Date at first day of month
     */
    public static Date getFirstDayOfMonth(Date date) {
        return startOfMonth(date);
    }
    
    /**
     * Gets the last day of the month
     * 
     * @param date The date
     * @return Date at last day of month
     */
    public static Date getLastDayOfMonth(Date date) {
        return endOfMonth(date);
    }
    
    /**
     * Gets the first day of the year
     * 
     * @param date The date
     * @return Date at first day of year
     */
    public static Date getFirstDayOfYear(Date date) {
        return startOfYear(date);
    }
    
    /**
     * Gets the last day of the year
     * 
     * @param date The date
     * @return Date at last day of year
     */
    public static Date getLastDayOfYear(Date date) {
        return endOfYear(date);
    }
    
    // ============================================================
    // LOCALDATE/LOCALDATETIME UTILITIES
    // ============================================================
    
    /**
     * Converts Date to LocalDate
     * 
     * @param date The Date to convert
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Converts Date to LocalDateTime
     * 
     * @param date The Date to convert
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Converts LocalDate to Date
     * 
     * @param localDate The LocalDate to convert
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Converts LocalDateTime to Date
     * 
     * @param localDateTime The LocalDateTime to convert
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    // ============================================================
    // DATE HELPERS
    // ============================================================
    
    /**
     * Gets the next occurrence of a day of week
     * 
     * @param fromDate The date to start from
     * @param dayOfWeek The day of week (Calendar.SUNDAY, etc.)
     * @return Next occurrence date
     */
    public static Date getNextDayOfWeek(Date fromDate, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = (dayOfWeek - currentDay + 7) % 7;
        if (daysToAdd == 0) daysToAdd = 7;
        cal.add(Calendar.DAY_OF_MONTH, daysToAdd);
        return cal.getTime();
    }
    
    /**
     * Gets the previous occurrence of a day of week
     * 
     * @param fromDate The date to start from
     * @param dayOfWeek The day of week (Calendar.SUNDAY, etc.)
     * @return Previous occurrence date
     */
    public static Date getPreviousDayOfWeek(Date fromDate, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        int currentDay = cal.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract = (currentDay - dayOfWeek + 7) % 7;
        if (daysToSubtract == 0) daysToSubtract = 7;
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);
        return cal.getTime();
    }
    
    /**
     * Gets the nth day of week in a month
     * 
     * @param year The year
     * @param month The month (1-12)
     * @param weekOfMonth The week in month (1-5)
     * @param dayOfWeek The day of week (Calendar.SUNDAY, etc.)
     * @return The date
     */
    public static Date getNthDayOfWeekInMonth(int year, int month, int weekOfMonth, int dayOfWeek) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1);
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK);
        int daysToAdd = (dayOfWeek - firstDayOfMonth + 7) % 7;
        int day = 1 + daysToAdd + (weekOfMonth - 1) * 7;
        
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }
    
    // ============================================================
    // DATE RANGE UTILITIES
    // ============================================================
    
    /**
     * Checks if two date ranges overlap
     * 
     * @param start1 First range start
     * @param end1 First range end
     * @param start2 Second range start
     * @param end2 Second range end
     * @return true if ranges overlap
     */
    public static boolean rangesOverlap(Date start1, Date end1, Date start2, Date end2) {
        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            return false;
        }
        return !(end1.before(start2) || end2.before(start1));
    }
    
    /**
     * Gets the overlap of two date ranges
     * 
     * @param start1 First range start
     * @param end1 First range end
     * @param start2 Second range start
     * @param end2 Second range end
     * @return Overlap range [start, end], or null if no overlap
     */
    public static Date[] getRangeOverlap(Date start1, Date end1, Date start2, Date end2) {
        if (!rangesOverlap(start1, end1, start2, end2)) {
            return null;
        }
        
        Date overlapStart = start1.after(start2) ? start1 : start2;
        Date overlapEnd = end1.before(end2) ? end1 : end2;
        return new Date[]{overlapStart, overlapEnd};
    }
    
    // ============================================================
    // DEMO / TESTING
    // ============================================================
    
    /**
     * Demo method showing usage of DateUtils
     */
    public static void main(String[] args) {
        System.out.println("📅 DateUtils Demo");
        System.out.println("═".repeat(60));
        
        // Current date/time
        Date now = new Date();
        System.out.println("\n📌 Current Date/Time:");
        System.out.println("  Now: " + formatDateTime(now));
        System.out.println("  Date: " + formatDate(now, DATE_FORMAT_DISPLAY));
        System.out.println("  Time (24h): " + formatTime24(now));
        System.out.println("  Time (12h): " + formatTime12(now));
        System.out.println("  ISO: " + formatISO(now));
        System.out.println("  RFC1123: " + formatRFC1123(now));
        System.out.println("  Timestamp: " + getCurrentTimestamp());
        System.out.println("  Unix: " + getCurrentUnixTimestamp());
        
        // Date creation
        System.out.println("\n📌 Date Creation:");
        Date customDate = createDate(2024, 12, 25);
        System.out.println("  Christmas 2024: " + formatDisplay(customDate));
        Date customDateTime = createDateTime(2024, 12, 25, 14, 30, 0);
        System.out.println("  Christmas 2024 14:30: " + formatDateTime(customDateTime));
        
        // Date operations
        System.out.println("\n📌 Date Operations:");
        Date tomorrow = addDays(now, 1);
        System.out.println("  Tomorrow: " + formatDisplay(tomorrow));
        Date nextWeek = addWeeks(now, 1);
        System.out.println("  Next Week: " + formatDisplay(nextWeek));
        Date nextMonth = addMonths(now, 1);
        System.out.println("  Next Month: " + formatDisplay(nextMonth));
        Date nextYear = addYears(now, 1);
        System.out.println("  Next Year: " + formatDisplay(nextYear));
        
        // Date truncation
        System.out.println("\n📌 Date Truncation:");
        System.out.println("  Start of Day: " + formatDateTime(startOfDay(now)));
        System.out.println("  End of Day: " + formatDateTime(endOfDay(now)));
        System.out.println("  Start of Month: " + formatDate(startOfMonth(now), DATE_FORMAT_DISPLAY));
        System.out.println("  End of Month: " + formatDate(endOfMonth(now), DATE_FORMAT_DISPLAY));
        System.out.println("  Start of Year: " + formatDate(startOfYear(now), DATE_FORMAT_DISPLAY));
        System.out.println("  End of Year: " + formatDate(endOfYear(now), DATE_FORMAT_DISPLAY));
        
        // Date differences
        System.out.println("\n📌 Date Differences:");
        Date pastDate = addDays(now, -30);
        System.out.println("  Days between: " + diffInDays(pastDate, now) + " days");
        System.out.println("  Hours between: " + diffInHours(pastDate, now) + " hours");
        System.out.println("  Months between: " + diffInMonths(pastDate, now) + " months");
        System.out.println("  Years between: " + diffInYears(pastDate, now) + " years");
        
        // Relative time
        System.out.println("\n📌 Relative Time:");
        Date twoHoursAgo = addHours(now, -2);
        System.out.println("  2 hours ago: " + getRelativeTime(twoHoursAgo));
        System.out.println("  Short: " + getShortRelativeTime(twoHoursAgo));
        
        Date threeDaysAgo = addDays(now, -3);
        System.out.println("  3 days ago: " + getRelativeTime(threeDaysAgo));
        
        // Date range
        System.out.println("\n📌 Date Range:");
        Date rangeStart = createDate(2024, 1, 1);
        Date rangeEnd = createDate(2024, 1, 10);
        List<Date> range = getDateRange(rangeStart, rangeEnd);
        System.out.println("  Days in January 1-10, 2024: " + range.size());
        System.out.println("  First: " + formatDisplay(range.get(0)));
        System.out.println("  Last: " + formatDisplay(range.get(range.size() - 1)));
        
        // Business days
        System.out.println("\n📌 Business Days:");
        Date start = createDate(2024, 1, 1);
        Date end = createDate(2024, 1, 31);
        int businessDays = getBusinessDays(start, end);
        System.out.println("  Business days in Jan 2024: " + businessDays);
        
        Date afterBusinessDays = addBusinessDays(start, 10);
        System.out.println("  10 business days after Jan 1, 2024: " + formatDisplay(afterBusinessDays));
        
        // Age calculation
        System.out.println("\n📌 Age Calculation:");
        Date birthdate = createDate(1990, 6, 15);
        int age = calculateAge(birthdate);
        System.out.println("  Age for 1990-06-15: " + age + " years");
        
        // Date components
        System.out.println("\n📌 Date Components:");
        System.out.println("  Year: " + getYear(now));
        System.out.println("  Month: " + getMonthName(now) + " (" + getMonth(now) + ")");
        System.out.println("  Day: " + getDay(now));
        System.out.println("  Day of Week: " + getDayOfWeekName(now));
        System.out.println("  Day of Year: " + getDayOfYear(now));
        System.out.println("  Week of Year: " + getWeekOfYear(now));
        System.out.println("  Quarter: " + getQuarter(now));
        System.out.println("  Hour: " + getHour(now));
        System.out.println("  Minute: " + getMinute(now));
        System.out.println("  Second: " + getSecond(now));
        
        // Natural language parsing
        System.out.println("\n📌 Natural Language Parsing:");
        Date tomorrowNL = parseNaturalLanguage("tomorrow");
        System.out.println("  'tomorrow': " + formatDisplay(tomorrowNL));
        Date nextWeekNL = parseNaturalLanguage("next week");
        System.out.println("  'next week': " + formatDisplay(nextWeekNL));
        Date noonNL = parseNaturalLanguage("noon");
        System.out.println("  'noon': " + formatDateTime(noonNL));
        
        // LocalDate/LocalDateTime
        System.out.println("\n📌 LocalDate/LocalDateTime:");
        LocalDate localDate = getCurrentLocalDate();
        System.out.println("  LocalDate: " + formatLocalDate(localDate));
        LocalDateTime localDateTime = getCurrentLocalDateTime();
        System.out.println("  LocalDateTime: " + formatLocalDateTime(localDateTime));
        
        // Date validation
        System.out.println("\n📌 Date Validation:");
        String validDate = "2024-12-25";
        System.out.println("  '2024-12-25' valid: " + isValidDate(validDate, DATE_FORMAT_DEFAULT));
        String invalidDate = "2024-13-45";
        System.out.println("  '2024-13-45' valid: " + isValidDate(invalidDate, DATE_FORMAT_DEFAULT));
        
        // Timezone
        System.out.println("\n📌 Timezone:");
        System.out.println("  Current Timezone: " + getCurrentTimezone().getDisplayName());
        String[] tzIds = getAvailableTimezoneIDs();
        System.out.println("  Available Timezones: " + tzIds.length);
        
        System.out.println("\n✅ Demo completed!");
    }
}