package service;

/*
 * LEGENDA: STANDARD DI DOCUMENTAZIONE JAVADOC
 * @param: definisce i vincoli e il significato degli input (componenti del record).
 * @author / @version: tracciano paternità e versione del file per manutenzione.
 */

/**
 * Risultato immutabile di una singola operazione applicativa.
 * Fornisce un semplice contenitore (successo + messaggio) destinato alla UI.
 * Utilizza il costrutto `record` per ottenere un DTO immutabile e compatto.
 *
 * @param success indica se l'operazione è andata a buon fine (true) o no (false).
 * @param message messaggio descrittivo destinato alla UI; può essere null o vuoto.
 *
 * @author ParthEll Team
 * @version 1.0
 */
// serve a far parlare in modo ordinato la parte logica con la schermata utente.
// Contiene solo due informazioni:
// 1. se l’operazione è riuscita oppure no
// 2. il messaggio da mostrare all’utente

public record OperationResult(boolean success, String message) {
	// Record immutabile: scelta che semplifica la gestione di DTO leggeri e thread-safe.
}
