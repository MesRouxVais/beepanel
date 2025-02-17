package fr.mesrouxvais.beepanel.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")  // Spécifie le profil 'test' pour activer la configuration 
public class BasicStatementsRepositoryTest {

    @Autowired
    private BasicStatementsRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
    	
        jdbcTemplate.execute("DROP TABLE IF EXISTS basic_statements");
        jdbcTemplate.execute("CREATE TABLE basic_statements (" +
        		"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "valueName TEXT, " +
                "value REAL, " +
                "device TEXT, " +
                "date timestamp)");
        
    }

    @Test
    public void testAddBasicStatement() {
    	
        BasicStatement statement = new BasicStatement("test1", 15.0f, "BasicStatementsRepositoryTest", LocalDateTime.now());

        repository.addBasicStatement(statement);
        

        List<BasicStatement> statements = repository.getAllBasicStatements();
        assertThat(statements).hasSize(1);
        assertThat(statements.get(0).value()).isEqualTo(15.0f);
        assertThat(statements.get(0).valueName()).isEqualTo("test1");
    }
    /*
    @Test
    public void testGetStatementsByYears() {
    	
    	 BasicStatement statement = new BasicStatement(23f, 17.0f, 60.0f, 70, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
    	 repository.addBasicStatement(statement);
    	 
    	 List<BasicStatement> statements = repository.getStatementsByYear(LocalDateTime.now());
    	 assertThat(statements).hasSize(1);
         assertThat(statements.get(0).interiorTemperature()).isEqualTo(23f);
         assertThat(statements.get(0).outsideTemperature()).isEqualTo(17.0f);
         
         String dateString = "2023-12-15 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         LocalDateTime customDate = LocalDateTime.parse(dateString, formatter);
         
         statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
         repository.addBasicStatement(statement);
         
         
         statements = repository.getStatementsByYear(customDate);
         assertThat(statements).hasSize(1);
         assertThat(statements.get(0).outsideTemperature()).isEqualTo(18.0f);
         
         
    }
    @Test
    public void testGetStatementsByMonth() {
    	
    	String dateString = "2023-12-16 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime customDate = LocalDateTime.parse(dateString, formatter);
        
        BasicStatement statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        
        List<BasicStatement> statements = repository.getStatementsByMonth(customDate);
        assertThat(statements).hasSize(1);
        
        
        dateString = "2023-11-16 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        customDate = LocalDateTime.parse(dateString, formatter);
        
        statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        
        statements = repository.getStatementsByMonth(customDate);
        assertThat(statements).hasSize(1);
        
        dateString = "2023-11-18 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        customDate = LocalDateTime.parse(dateString, formatter);
        
        statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        
        statements = repository.getStatementsByMonth(customDate);
        assertThat(statements).hasSize(2);
        
    	
    	
    }
    
    @Test
    public void testGetStatementsByDay() {
    	
    	String dateString = "2023-12-16 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime customDate = LocalDateTime.parse(dateString, formatter);
        
        BasicStatement statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        repository.addBasicStatement(statement);
        
        List<BasicStatement> statements = repository.getStatementsByDay(customDate);
        assertThat(statements).hasSize(2);
        
        dateString = "2023-12-17 10:30:00"; 
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        customDate = LocalDateTime.parse(dateString, formatter);
        
        statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        
        statements = repository.getStatementsByDay(customDate);
        
        assertThat(statements).hasSize(1);
    }
    
    @Test
    public void testGet24hoursOfStatements() {
    	
    	String dateString = "2023-12-16 10:30:00"; // "yyyy-MM-dd HH:mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime customDate = LocalDateTime.parse(dateString, formatter);
        
        BasicStatement statement = new BasicStatement(22.5f, 18.0f, 60.0f, 70, customDate);
        repository.addBasicStatement(statement);
        
        statement = new BasicStatement(22.5f, 18.0f, 60.0f, 100, customDate);
    	for (int i = 0; i < 78; i++) {
    		repository.addBasicStatement(statement);
		}
    	
    	
    	List<BasicStatement> statements = repository.getLast78Statements();
    	
    	assertThat(statements).hasSize(78);
    	
    	for (BasicStatement basicStatement : statements) {
    		assertThat(basicStatement.weight().equals(100));
		}
    }
    */
}
