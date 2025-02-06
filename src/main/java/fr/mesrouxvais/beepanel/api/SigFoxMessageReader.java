package fr.mesrouxvais.beepanel.api;

import java.io.File;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import fr.mesrouxvais.beepanel.repositories.BasicStatement;
import fr.mesrouxvais.beepanel.repositories.BasicStatementsRepository;
import fr.mesrouxvais.beepanel.util.BinaryTool;

@Component
public class SigFoxMessageReader {
	@Value("${beepanel.admin.xml}")
	private String xmlPath;
	
	public void messageToTagsConversion(String trame, String device) {
		byte[] valeurTrame = BinaryTool.stringHexaToArray(trame);
		
		//URL fichierexml = SigFoxMessageReader.class.getClassLoader().getResource("xmlTemplate/templateTrame.xml");


		try {
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        File file;
	        file = new File(xmlPath);
	        Document document = builder.parse(file);
	        // Normaliser le document
	        document.getDocumentElement().normalize();
	        
	        NodeList enableInversion = document.getElementsByTagName("enableInversion");
	        Element enableInversionElement = (Element) enableInversion.item(0);
	        
	        if(enableInversionElement.getAttribute("value").equals("true")) {
	        	valeurTrame = BinaryTool.invertArray(valeurTrame);
	        }
	        
	        NodeList trames = document.getElementsByTagName("type");
	        int trameType = 0;
	        
	        if (trames.getLength() > 0) {
	            Element typeElement = (Element) trames.item(0);
	            String sizeStr = typeElement.getAttribute("size");
	            int size;
	            try {
	                size = Integer.parseInt(sizeStr);
	                System.out.println("La taille du trame type est : " + size);
	                trameType  = BinaryTool.isolateBits(valeurTrame[0], 8-size,7);
	                System.out.println("le type de trame est : " + trameType);
	            } catch (NumberFormatException e) {
	                System.out.println("Erreur de conversion de 'size' en int");
	                return;
	            }
	            
	        }else {
	        	return;
	        }
	        
	        NodeList cores = document.getElementsByTagName("core");
	        Element trameCore = null;
	        for(int i = 0; i < cores.getLength(); i++) {
	        	Element core = (Element) cores.item(i);
	        	String idStr = core.getAttribute("id");
	        	int id;
	        	try {
	                id = Integer.parseInt(idStr);
	                
	                if(id == trameType) {
		        		trameCore = core;
		        	}
	            } catch (NumberFormatException e) {
	                System.out.println("Erreur de conversion de 'id' en int");
	                return;
	            }
	        	
	        }
	        if(trameCore == null) {
	        	return;
	        }
	        
	        
	        NodeList children = trameCore.getChildNodes();
	        for (int i = 0; i < children.getLength(); i++) {
	            Node child = children.item(i);
	            if (child.getNodeType() == Node.ELEMENT_NODE) { // Vérifie si c'est un élément (pas un texte ou un commentaire)
	                Element childElement = (Element) child;
	                
	                String expressionField  = childElement.getTextContent();
	                
	                
	                int valuePosition;
	                int valueSize;
	                try {
	                	valuePosition = Integer.parseInt(childElement.getAttribute("position"));
	                	valueSize = Integer.parseInt(childElement.getAttribute("size"));
		                
	                	float value = BinaryTool.extractBits(valeurTrame, valuePosition, valuePosition-1+valueSize);
	                	
	                	if(expressionField != "") {
	                		Expression expression = new ExpressionBuilder(expressionField)
	                                .variable("x")
	                                .build();
	                		
	                		expression.setVariable("x", value);
	                		
	                		value = (float) expression.evaluate();
		                }
	                	
		                baliseMaker(value,childElement.getTagName(), device);
		            } catch (NumberFormatException e) {
		                System.out.println("Erreur de conversion de 'id' en int");
		                return;
		            }
	            }
	        }

	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		
	}
	
	
	@Autowired
	private BasicStatementsRepository repository;
	
	private void baliseMaker(float value, String name, String device) {
		BasicStatement statement = new BasicStatement(name, value, device, LocalDateTime.now());
		
		repository.addBasicStatement(statement);
	}
}
