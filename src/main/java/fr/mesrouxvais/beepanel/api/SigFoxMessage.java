package fr.mesrouxvais.beepanel.api;

public record SigFoxMessage(
		String device,
		String data,
		String token
) {

}
