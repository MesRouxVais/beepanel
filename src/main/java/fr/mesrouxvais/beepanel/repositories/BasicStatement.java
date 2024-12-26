package fr.mesrouxvais.beepanel.repositories;

import java.time.LocalDateTime;

public record BasicStatement(
        Float interiorTemperature,
        Float outsideTemperature,
        Float humidity,
        Integer weight,
        LocalDateTime date
) {

    public BasicStatement {
        if (interiorTemperature == null || outsideTemperature == null || humidity == null || weight == null || date == null) {
            throw new IllegalArgumentException("Aucun champ ne peut être nul");
        }
        
        if (weight < 0) {
            throw new IllegalArgumentException("Le poids ne peut pas être négatif");
        }
    }
}
