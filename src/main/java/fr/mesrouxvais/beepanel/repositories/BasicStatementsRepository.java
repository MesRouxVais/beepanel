package fr.mesrouxvais.beepanel.repositories;


import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Repository
public class BasicStatementsRepository {

    private final JdbcClient jdbClient;
    private final String DATABASE_NAME = "basic_statements";

    public BasicStatementsRepository(JdbcClient jdbClient) {
        this.jdbClient = jdbClient;
        
    }

    
    public void addBasicStatement(BasicStatement basicStatement) {
    	/*
    	 var updated = jdbClient.sql("INSERT INTO "+ DATABASE_NAME +"(valueName, value, device, date) values(?,?,?,?)")
                 .params(List.of(basicStatement.valueName(),basicStatement.value(),basicStatement.device(),basicStatement.date()))
                 .update();
        */

         //Assert.state(updated == 1, "Failed to create basicStatement " + basicStatement.date());
         
         //compile if new day
         
         LocalDate lastToCompileDate = getLastToCompileDate().toLocalDate();
         LocalDate basicStatementDate = basicStatement.date().toLocalDate();
         
         long daysDifference = ChronoUnit.DAYS.between(lastToCompileDate, basicStatementDate);
         
         if(daysDifference > 0) {
        	 List<BasicStatement> toCompileList = getStatementsByDate(lastToCompileDate.toString(), 100);
        	 System.out.println(toCompileList);
         }else {
        	 System.out.println("No need to compile");
         }
         
         
    }

    
    public List<BasicStatement> getAllBasicStatements() {
    	return jdbClient.sql("SELECT valueName, value, device, date FROM " + DATABASE_NAME)
                .query(BasicStatement.class)
                .list();
    }
    public List<BasicStatement> getLastNStatements(int number) {
        String sqlQuery = "SELECT valueName, value, device, date FROM " + DATABASE_NAME
                + " ORDER BY id DESC LIMIT ?";  // Limite le nombre de résultats à "number"

		// Exécution de la requête avec les paramètres
		return jdbClient.sql(sqlQuery)                                                         
              .params(number) // Ajout des paramètres : type et number
              .query(BasicStatement.class)                                          
              .list();      
    }
    
    /*
    TODO faire un mélange complet avec getStatementsBetweenDates
    public List<BasicStatement> getLastNStatementsWhitType(String type, int number) {
        String sqlQuery = "SELECT valueName, value, device, date FROM " + DATABASE_NAME       
                + " WHERE valueName = ? "  // Condition pour le type
                + " ORDER BY id DESC LIMIT ?";  // Limite le nombre de résultats à "number"

		// Exécution de la requête avec les paramètres
		return jdbClient.sql(sqlQuery)                                                         
              .params(List.of(String.valueOf(type), number)) // Ajout des paramètres : type et number
              .query(BasicStatement.class)                                          
              .list();      
    }
    */

    
    
    //New for postgre

    
    public LocalDateTime getLastToCompileDate() {
    	String sqlQuery = "SELECT date FROM "+DATABASE_NAME+" WHERE date = (SELECT MAX(date) FROM "+DATABASE_NAME+")  LIMIT 1";
    	
    	return jdbClient.sql(sqlQuery).query(LocalDateTime.class).single();
    }
    
    public List<BasicStatement> getStatementsByDate(String date, int limit){
    	String sqlQuery = "SELECT * FROM " + DATABASE_NAME +
                " WHERE DATE(date) = CAST(? AS DATE)" +
                " ORDER BY date ASC LIMIT ?";

		return jdbClient.sql(sqlQuery)
		      .param(date)  // Assurez-vous que fromDate est bien un Timestamp
		      .param(limit)     // Limite en entier
		      .query(BasicStatement.class)
		      .list();
    }
    	
    
	public List<BasicStatement> getStatementsBetweenDates(String fromDate, String toDate, int limit) {
		String sqlQuery = "SELECT * FROM " + DATABASE_NAME +
                " WHERE date >= CAST(? AS TIMESTAMP) AND date <= CAST(? AS TIMESTAMP) " +
                " ORDER BY date ASC LIMIT ?";

		return jdbClient.sql(sqlQuery)
		      .param(fromDate)  // Assurez-vous que fromDate est bien un Timestamp
		      .param(toDate)    // Assurez-vous que toDate est bien un Timestamp
		      .param(limit)     // Limite en entier
		      .query(BasicStatement.class)
		      .list();

		
	}
}
