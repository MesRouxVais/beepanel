package fr.mesrouxvais.beepanel.repositories;


import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BasicStatementsRepository {

    private final JdbcClient jdbClient;
    private final String DATABASE_NAME = "basic_statements";

    public BasicStatementsRepository(JdbcClient jdbClient) {
        this.jdbClient = jdbClient;
    }

    
    public void addBasicStatement(BasicStatement basicStatement) {
    	 var updated = jdbClient.sql("INSERT INTO "+ DATABASE_NAME +"(valueName, value, device, date) values(?,?,?,?)")
                 .params(List.of(basicStatement.valueName(),basicStatement.value(),basicStatement.device(),basicStatement.date()))
                 .update();

         Assert.state(updated == 1, "Failed to create basicStatement " + basicStatement.date());
    }

    
    public List<BasicStatement> getAllStatements() {
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
    
    //get whit Date
    public List<BasicStatement> getStatementsByYear(LocalDateTime date) {
        String sqlQuery = "SELECT valueName, value, device, date FROM " + DATABASE_NAME 
                          + " WHERE strftime('%Y',date) = ?";
        
        return jdbClient.sql(sqlQuery)
                        .params(List.of(String.valueOf(date.getYear())))
                        .query(BasicStatement.class)
                        .list();
    }
    
    
    public List<BasicStatement> getStatementsByMonth(LocalDateTime date) {
        String sqlQuery = "SELECT valueName, value, device, date FROM " + DATABASE_NAME 
                          + " WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ?";
        
        String monthFormatted = String.format("%02d", date.getMonthValue());
        
        return jdbClient.sql(sqlQuery)
                        .params(List.of(String.valueOf(date.getYear()), monthFormatted))
                        .query(BasicStatement.class)
                        .list();
    }
}
