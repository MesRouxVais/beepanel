package fr.mesrouxvais.beepanel.repositories;


import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import fr.mesrouxvais.beepanel.util.DateTool;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class BasicStatementsRepository {

    private final JdbcClient jdbClient;
    public static final String BASIC_DATABASE_NAME = "basic_statements";
    public static final String COMPILED_DATABASE_NAME = "compiled_statements";


    public BasicStatementsRepository(JdbcClient jdbClient) {
        this.jdbClient = jdbClient;
        
    }
    
    
    public void seeForCompilation(LocalDateTime newStatementsDate) {
    	
    	LocalDateTime lastToCompileDateTime = getLastToCompileDate();
        LocalDate lastToCompileDate = lastToCompileDateTime.toLocalDate();
        LocalDate basicStatementDate = newStatementsDate.toLocalDate();
        
        long daysDifference = ChronoUnit.DAYS.between(lastToCompileDate, basicStatementDate);
        
        
        if(daysDifference > 0 && getStatementsByDate(lastToCompileDate.toString(), 100, COMPILED_DATABASE_NAME).size() == 0) {
       	 List<BasicStatement> toCompileList = getStatementsByDate(lastToCompileDate.toString(), 100, BASIC_DATABASE_NAME);
       	 System.out.println(toCompileList);
       	 
       	 Map<String, double[]> map = new HashMap<>();
       	 
       	 for (BasicStatement e : toCompileList) {
                String name = e.valueName();
                float value = e.value();

                // Si le nom existe déjà dans la HashMap, mettre à jour la somme et le compteur
                if (map.containsKey(name)) {
               	 double[] data = map.get(name);
                    data[0] += value;   // Somme des valeurs
                    data[1] += 1;       // Compteur des occurrences
                } else {
                    // Si le nom n'existe pas encore, initialiser avec la valeur actuelle
                    map.put(name, new double[] { value, 1 });
                }
            }
       	 System.out.println(map);
       	 
       	 for (Map.Entry<String, double[]> entry : map.entrySet()) {
                String name = entry.getKey();
                double[] data = entry.getValue();
                double average = data[0] / data[1];  // Moyenne = somme / compteur
                
                BasicStatement compileBasicStatement = new BasicStatement(name, (float) average, "backend-compile", lastToCompileDateTime);
                System.out.println(compileBasicStatement);
                addBasicStatement(compileBasicStatement, COMPILED_DATABASE_NAME);
            }
       	 
       	 
        }else {
       	 System.out.println("No need to compile");
        }
    	
    }
    
    public void addBasicStatement(BasicStatement basicStatement, String dbName) {
    	 if(dbName == COMPILED_DATABASE_NAME) {
    		 var updated = jdbClient.sql("INSERT INTO "+ dbName +"(valueName, value, device, date) values(?,?,?,?)")
                     .params(List.of(basicStatement.valueName(),basicStatement.value(),basicStatement.device(),basicStatement.date()))
                     .update();
            

             Assert.state(updated == 1, "Failed to create basicStatement " + basicStatement.date());
    	 }
         
    }

    
    public List<BasicStatement> getAllBasicStatements() {
    	return jdbClient.sql("SELECT valueName, value, device, date FROM " + BASIC_DATABASE_NAME)
                .query(BasicStatement.class)
                .list();
    }
    public List<BasicStatement> getLastNStatements(int number) {
        String sqlQuery = "SELECT valueName, value, device, date FROM " + BASIC_DATABASE_NAME
                + " ORDER BY id DESC LIMIT ?";  // Limite le nombre de résultats à "number"

		// Exécution de la requête avec les paramètres
		return jdbClient.sql(sqlQuery)                                                         
              .params(number) // Ajout des paramètres : type et number
              .query(BasicStatement.class)                                          
              .list();      
    }
    

    
    public LocalDateTime getLastToCompileDate() {
    	String sqlQuery = "SELECT date FROM "+BASIC_DATABASE_NAME+" WHERE date = (SELECT MAX(date) FROM "+BASIC_DATABASE_NAME+")  LIMIT 1";
    	
    	return jdbClient.sql(sqlQuery).query(LocalDateTime.class).single();
    }
    
    public List<BasicStatement> getStatementsByDate(String date, int limit, String dbName){
    	String sqlQuery = "SELECT * FROM " + dbName +
                " WHERE DATE(date) = CAST(? AS DATE)" +
                " ORDER BY date ASC LIMIT ?";

		return jdbClient.sql(sqlQuery)
		      .param(date)  // Assurez-vous que fromDate est bien un Timestamp
		      .param(limit)     // Limite en entier
		      .query(BasicStatement.class)
		      .list();
    }
    	
    
	public List<BasicStatement> getStatementsBetweenDates(String fromDate, String toDate, int limit, String dbName) {
		String sqlQuery = "SELECT * FROM " + dbName +
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
