package fr.mesrouxvais.beepanel.repositories;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class XMLRepository {
	
	private final JdbcClient jdbClient;
    static final String DATABASE_NAME = "xml_storage";
    
    public XMLRepository(JdbcClient jdbClient) {
        this.jdbClient = jdbClient;
        
        
    }

    
    public String getXml() {
        String sql = "SELECT xml_content FROM xml_storage WHERE id = 1";
        return jdbClient.sql(sql).query(String.class).single();
    }

    // Mettre à jour un XML par ID
    public void updateXml(String newXmlContent) {
        String sql = "UPDATE xml_storage SET xml_content = ? WHERE id = 1";
        
        jdbClient.sql(sql).param(newXmlContent).update();
        
        System.out.println("xml updated :" + "\n"+ newXmlContent);
        
    }
    


}
