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
    	 var updated = jdbClient.sql("INSERT INTO "+ DATABASE_NAME +"(interiorTemperature, outsideTemperature, humidity, weight, date) values(?,?,?,?,?)")
                 .params(List.of(basicStatement.interiorTemperature(),basicStatement.outsideTemperature(),basicStatement.humidity(),basicStatement.weight(),basicStatement.date()))
                 .update();

         Assert.state(updated == 1, "Failed to create basicStatement " + basicStatement.date());
    }

    
    public List<BasicStatement> getAllStatements() {
    	return jdbClient.sql("SELECT interiorTemperature, outsideTemperature, humidity, weight, date FROM " + DATABASE_NAME)
                .query(BasicStatement.class)
                .list();
    }
    
    //so 24h of recording
    public List<BasicStatement> getLast78Statements() {
        String sqlQuery = "SELECT interiorTemperature, outsideTemperature, humidity, weight, date FROM " + DATABASE_NAME 
                          + " ORDER BY id DESC LIMIT 78";
        
        return jdbClient.sql(sqlQuery)
                        .query(BasicStatement.class)
                        .list();
    }
    
    //get whit Date
    public List<BasicStatement> getStatementsByYear(LocalDateTime date) {
        String sqlQuery = "SELECT interiorTemperature, outsideTemperature, humidity, weight, date FROM " + DATABASE_NAME 
                          + " WHERE strftime('%Y',date) = ?";
        
        return jdbClient.sql(sqlQuery)
                        .params(List.of(String.valueOf(date.getYear())))
                        .query(BasicStatement.class)
                        .list();
    }
    
    public List<BasicStatement> getStatementsByDay(LocalDateTime date) {
        String sqlQuery = "SELECT interiorTemperature, outsideTemperature, humidity, weight, date FROM " + DATABASE_NAME 
                          + " WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ? AND strftime('%d', date) = ?";
        
        return jdbClient.sql(sqlQuery)
                        .params(List.of(String.valueOf(date.getYear()), String.valueOf(date.getMonthValue()), String.valueOf(date.getDayOfMonth())))
                        .query(BasicStatement.class)
                        .list();
    }
    
    public List<BasicStatement> getStatementsByMonth(LocalDateTime date) {
        String sqlQuery = "SELECT interiorTemperature, outsideTemperature, humidity, weight, date FROM " + DATABASE_NAME 
                          + " WHERE strftime('%Y', date) = ? AND strftime('%m', date) = ?";
        
        return jdbClient.sql(sqlQuery)
                        .params(List.of(String.valueOf(date.getYear()), String.valueOf(date.getMonthValue())))
                        .query(BasicStatement.class)
                        .list();
    }
}
