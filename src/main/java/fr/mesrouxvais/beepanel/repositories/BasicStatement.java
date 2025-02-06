package fr.mesrouxvais.beepanel.repositories;

import java.time.LocalDateTime;

public record BasicStatement(
        String valueName,
        Float value,
        String device,
        LocalDateTime date
) {

    public BasicStatement {
        if (valueName == null || value == null || device == null || date == null) {
            throw new IllegalArgumentException("Aucun champ ne peut être nul");
        }
    }
}
