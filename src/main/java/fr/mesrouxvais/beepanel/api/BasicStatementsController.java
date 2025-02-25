package fr.mesrouxvais.beepanel.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import fr.mesrouxvais.beepanel.repositories.BasicStatement;
import fr.mesrouxvais.beepanel.repositories.BasicStatementsRepository;
import fr.mesrouxvais.beepanel.util.DateTool;
//TODO rendre le tout utilisable par l'user
@RestController
@RequestMapping("/api/basicStatements")
@CrossOrigin
public class BasicStatementsController {
	
	@Value("${beepanel.api.token}")
    private String adminToken;
	
	@Autowired
	private BasicStatementsRepository repository;
	
	@Autowired
	private SigFoxMessageReader sigFoxMessageReader;
	
	@Value("${beepanel.api.token}")
    private String apiToken;
	
	@GetMapping
    List<BasicStatement> getAll() {
        return repository.getAllBasicStatements();
    }
	
	@GetMapping("/DateBetween/{fromDate}/{toDate}/{limit}/{dataBaseCode}")
    List<BasicStatement> getBetweenDates(@PathVariable String fromDate, @PathVariable String toDate, @PathVariable int limit, @PathVariable int dataBaseCode) {
		
		fromDate = DateTool.validateAndCorrectDate(fromDate);
	    toDate = DateTool.validateAndCorrectDate(toDate);
	    
	    if (dataBaseCode == 1) {
	        return repository.getStatementsBetweenDates(fromDate, toDate, limit, BasicStatementsRepository.COMPILED_DATABASE_NAME);
	    } else if (dataBaseCode == 0) {
	        return repository.getStatementsBetweenDates(fromDate, toDate, limit, BasicStatementsRepository.BASIC_DATABASE_NAME);
	    }
	    
	    return null;
    }
	
	@PostMapping()
	void create(@RequestBody SigFoxMessage message) {
		System.out.println("sigfox new message :" +message);
		
		if(!message.token().equals(adminToken)) {
			System.out.println("sigfox token invalid");
			return;
		}
		
		sigFoxMessageReader.messageToTagsConversion(message.data(), message.device());
		
	}
	
	
	
}
