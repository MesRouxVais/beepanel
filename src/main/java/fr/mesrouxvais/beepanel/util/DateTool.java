package fr.mesrouxvais.beepanel.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTool {
    
    /**
     * Converts a string to LocalDateTime according to the input pattern.
     * 
     * @param fullDate date string like "yyyy-MM-dd HH:mm:ss"
     * @return LocalDateTime object
     * @throws DateFormatException if the date format is invalid
     */
    static public LocalDateTime makeDate(String fullDate) throws DateFormatException {
        String dateString = fullDate;
        LocalDateTime customDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            customDate = LocalDateTime.parse(dateString, formatter);
        } catch (Exception e) {
            throw new DateFormatException("Invalid date format: " + dateString, e);
        }
        
        return customDate;
    }

    /**
     * Creates a LocalDateTime for the given year and month, defaulting to 15th day and 10:30:00 time.
     * 
     * @param year the year
     * @param month the month
     * @return LocalDateTime object
     * @throws DateFormatException if the date format is invalid
     */
    static public LocalDateTime makeDate(int year, int month) throws DateFormatException {
        String dateString = year + "-" + month + "-15 10:30:00";
        return makeDate(dateString);
    }

    /**
     * Creates a LocalDateTime for the given year, defaulting to December 15th and 10:30:00 time.
     * 
     * @param year the year
     * @return LocalDateTime object
     * @throws DateFormatException if the date format is invalid
     */
    static public LocalDateTime makeDate(int year) throws DateFormatException {
        String dateString = year + "-12-15 10:30:00";
        return makeDate(dateString);
    }
}
