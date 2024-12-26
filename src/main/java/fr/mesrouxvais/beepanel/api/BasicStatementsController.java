package fr.mesrouxvais.beepanel.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class BasicStatementsController {
	
	@Autowired
	private BasicStatementsRepository repository;
	
	@Value("${beepanel.api.token}")
    private String apiToken;
	
	@GetMapping
    List<BasicStatement> getAll() {
        return repository.getAllStatements();
    }
	
	@GetMapping("/24h")
    List<BasicStatement> getLast24h() {
        return repository.getLast78Statements();
    }
	
	@GetMapping("/request/{year}/{month}")
    List<BasicStatement> getForTheMonth(@PathVariable int year, @PathVariable int month) {	
        return repository.getStatementsByMonth(DateTool.makeDate(year,month));
    }
	@GetMapping("/request/{year}")
    List<BasicStatement> getForTheYear(@PathVariable int year) {
        return repository.getStatementsByYear(DateTool.makeDate(year));
    }
	
	@PostMapping()
	void create(@RequestBody SigFoxMessage message) {
		System.out.println(message);
	}
	
	
	
}
