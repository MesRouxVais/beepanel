package fr.mesrouxvais.beepanel.api.admin;

import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.mesrouxvais.beepanel.repositories.XMLRepository;

@RestController
@RequestMapping("/api/admin/xml")
public class XMLController {
	
	@Value("${beepanel.admin.xml}")
    private String xmlPath;
	@Value("${beepanel.api.token}")
    private String adminToken;
	@Autowired
	private XMLRepository repository;
	
	
	//URL fichierexml = XMLController.class.getClassLoader().getResource("xmlTemplate/templateTrame.xml");
	
    @PutMapping("/{token}")
    public String updateFile(@RequestBody String text, @PathVariable String token) {
    	if(!token.equalsIgnoreCase(adminToken)) {
    		return "token inconnu" ;
    	}
    	
    	repository.updateXml(text);
    	
    	return "all good";
    }
    
    @GetMapping("")
    public String getFile() throws URISyntaxException {
        
        return repository.getXml();
    }

}
