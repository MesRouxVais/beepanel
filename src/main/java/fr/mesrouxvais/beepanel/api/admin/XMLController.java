package fr.mesrouxvais.beepanel.api.admin;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin/xml")
public class XMLController {
	
	@Value("${beepanel.admin.xml}")
    private String xmlPath;
	@Value("${beepanel.api.token}")
    private String adminToken;
	//URL fichierexml = XMLController.class.getClassLoader().getResource("xmlTemplate/templateTrame.xml");
	
    @PutMapping("/{token}")
    public String updateFile(@RequestBody String text, @PathVariable String token) {
    	if(!token.equalsIgnoreCase(adminToken)) {
    		return "token inconnu" ;
    	}
    	
    	System.out.println("--------------------------------------------- XMLController [put] \n" + text +" \n ---------------------------------------------");
        File file;
        file = new File(xmlPath);
        
        try (FileWriter writer = new FileWriter(file, false)) { // false pour écraser le contenu
            writer.write(text); // Écrire le texte dans le fichier
            return "Le fichier a été mis à jour avec succès.";
        } catch (IOException e) {
            e.printStackTrace();
            return "Une erreur est survenue lors de la mise à jour du fichier.";
        }
    }
    
    @GetMapping("")
    public String getFile() throws URISyntaxException {
        
        File file;
        file = new File(xmlPath);
        System.out.println(file.getAbsolutePath());

        // Vérifier si le fichier existe
        if (!file.exists()) {
            return "Le fichier spécifié n'existe pas.";
        }

        try {
            // Lire le contenu du fichier
            String content = new String(Files.readAllBytes(file.toPath()));
            return content; // Retourne le contenu du fichier
        } catch (IOException e) {
            e.printStackTrace();
            return "Une erreur est survenue lors de la lecture du fichier.";
        }
    }

}
