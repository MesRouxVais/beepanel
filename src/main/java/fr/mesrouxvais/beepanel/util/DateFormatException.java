package fr.mesrouxvais.beepanel.util;

public class DateFormatException extends RuntimeException {
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DateFormatException() {
        super("Impossible de formater la date.");
    }

   
    public DateFormatException(String message) {
        super(message);
    }

    
    public DateFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    
    public DateFormatException(Throwable cause) {
        super("Unable to format the date.", cause);
    }
}

