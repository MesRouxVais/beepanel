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
        return repository.getAllStatements();
    }
	
	@GetMapping("/byTypeAndNumber/{type}/{number}")
    List<BasicStatement> getLastN(@PathVariable String type, @PathVariable int number) {
		if(String.valueOf(type).equals("all")) {
			return repository.getLastNStatements(number);
		}
		
        return repository.getLastNStatementsWhitType(type, number);
    }
	
	@GetMapping("/byDate/{year}/{month}")
    List<BasicStatement> getForTheMonth(@PathVariable int year, @PathVariable int month) {	
        return repository.getStatementsByMonth(DateTool.makeDate(year,month));
        
    }
	@GetMapping("/byDate/{year}")
    List<BasicStatement> getForTheYear(@PathVariable int year) {
        return repository.getStatementsByYear(DateTool.makeDate(year));
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
