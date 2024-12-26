package fr.mesrouxvais.beepanel.api;

public record SigFoxMessage(
		String device,
		long time,
		String data,
		String token
) {

}
