package fr.mesrouxvais.beepanel.api;

import java.time.LocalDateTime;
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
        return repository.getAllBasicStatements();
    }
	
	@GetMapping("/DateBetween/{fromDate}/{toDate}/{limit}")
    List<BasicStatement> getBetweenDates(@PathVariable String fromDate, @PathVariable String toDate, @PathVariable int limit) {
		if(DateTool.isValidDate(fromDate) || DateTool.isValidDate(toDate)) {
			 return repository.getStatementsBetweenDates(fromDate,toDate,limit);
		}else {
			return null;
		}
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
