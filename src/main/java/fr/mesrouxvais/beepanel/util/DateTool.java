package fr.mesrouxvais.beepanel.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

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
        String monthFormatted = String.format("%02d", month); // Cela formate le mois en deux chiffres
        
        // Construire la chaîne de date avec le bon format
        String dateString = year + "-" + monthFormatted + "-15 10:30:00"; // Le 15 est arbitraire pour illustrer
        
        // Définir le format attendu
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parser la chaîne dans un objet LocalDateTime
        return LocalDateTime.parse(dateString, formatter);
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
    
    static public String validateAndCorrectDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);  // Désactiver le traitement laxiste des dates
        
        try {
            Date date = dateFormat.parse(dateString);
            return dateFormat.format(date);
        } catch (ParseException e) {
            // Si la date est invalide, essayer de "corriger" en utilisant la date la plus proche valide
            return getClosestValidDate(dateString);
        }
    }

    static private String getClosestValidDate(String invalidDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String[] dateParts = invalidDate.split("-");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;  // Mois 0-based
        int day = Integer.parseInt(dateParts[2]);
        
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        
        // Si la date est invalide (par exemple le 30 février), on ajuste à la date la plus proche valide
        if (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) < day) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        
        return dateFormat.format(calendar.getTime());
    }
}
